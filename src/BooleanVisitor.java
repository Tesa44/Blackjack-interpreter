import blackjack.engine.BlackjackGame;
import blackjack.engine.Deck;
import blackjack.engine.RoundResult;
import blackjack.engine.Rank;
import blackjack.query.Filter;
import blackjack.query.GroupByClassifier;
import blackjack.sim.FrontendExportData;
import blackjack.sim.SimulationConfig;
import blackjack.sim.SimulationResult;
import blackjack.sim.SimulationResultJsonExporter;
import blackjack.sim.SimulationRunner;
import blackjack.sim.StreakStatistics;
import blackjack.sim.StreakStatisticsCalculator;
import blackjack.sim.Statistics;
import blackjack.sim.StatisticsCalculator;
import blackjack.strategy.Action;
import blackjack.strategy.BasicStrategyConfig;
import blackjack.strategy.Rule;
import blackjack.strategy.Strategy;
import blackjack.strategy.condition.CompositeCondition;
import blackjack.strategy.condition.DealerCondition;
import blackjack.strategy.condition.PairCondition;
import blackjack.strategy.condition.PlayerCondition;
import blackjack.strategy.condition.SoftCondition;
import blackjack.strategy.condition.TotalCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

public class BooleanVisitor extends ExprParserBaseVisitor<Object> {
    private Strategy currentStrategy = BasicStrategyConfig.create();
    private SimulationResult lastSimulationResult;
    private int configuredInitialBalance = 0;
    private int configuredBetPerGame = 1;
    private final SimulationResultJsonExporter simulationResultJsonExporter = new SimulationResultJsonExporter();
    private FrontendExportData frontendExportData = new FrontendExportData();


    @Override
    public String visitProgram(ExprParser.ProgramContext ctx) {
        currentStrategy = ctx.strategyBlock() == null
                ? BasicStrategyConfig.create()
                : asStrategy(visit(ctx.strategyBlock()));
        frontendExportData = new FrontendExportData();

        for (ExprParser.StatContext statContext : ctx.stat()) {
            visit(statContext);
        }

        if (lastSimulationResult != null && frontendExportData.getSummary() != null) {
            simulationResultJsonExporter.export(frontendExportData);
        }

        return "";
    }


    @Override
    public String visitSim_stat(ExprParser.Sim_statContext ctx) {
        return runSimulation(Integer.parseInt(ctx.INT().getText()), currentStrategy);
    }

    @Override
    public String visitSim_until_stat(ExprParser.Sim_until_statContext ctx) {
        return runSimulationUntilTarget(Integer.parseInt(ctx.INT().getText()), currentStrategy);
    }

    @Override
    public String visitSim_broke_stat(ExprParser.Sim_broke_statContext ctx) {
        return runSimulationUntilBroke(currentStrategy);
    }

    @Override
    public String visitSet_balance_stat(ExprParser.Set_balance_statContext ctx) {
        configuredInitialBalance = Integer.parseInt(ctx.INT().getText());
        return "";
    }

    @Override
    public String visitSet_bet_stat(ExprParser.Set_bet_statContext ctx) {
        configuredBetPerGame = Integer.parseInt(ctx.INT().getText());
        return "";
    }

    @Override
    public String visitPlot_balance_stat(ExprParser.Plot_balance_statContext ctx) {
        if (lastSimulationResult == null) {
            return "No simulation results available. Run 'simulate ... rounds;' first.";
        }

        frontendExportData.setPlotResult(
                new FrontendExportData.PlotResult(new ArrayList<>(lastSimulationResult.getBalanceHistory()))
        );
        return "";
    }



    @Override
    public Strategy visitStrategyBlock(ExprParser.StrategyBlockContext ctx) {
        List<Rule> rules = new ArrayList<>();
        for (ExprParser.RuleContext ruleContext : ctx.rule_()) {
            rules.add(asRule(visit(ruleContext)));
        }
        return new Strategy(rules);
    }

    @Override
    public Rule visitRule(ExprParser.RuleContext ctx) {
        PlayerCondition playerCondition = asPlayerCondition(visit(ctx.playerCondition()));
        DealerCondition dealerCondition = asDealerCondition(visit(ctx.dealerCondition()));
        Action action = asAction(visit(ctx.action()));
        return new Rule(playerCondition, dealerCondition, action);
    }

    @Override
    public PlayerCondition visitPlayerCondition(ExprParser.PlayerConditionContext ctx) {
        if (ctx.PAIR() != null) {
            int[] range = asRange(visit(ctx.valueRange()));
            return buildPairCondition(range);
        }

        if (ctx.TOTAL() != null) {
            int[] range = asRange(visit(ctx.valueRange()));
            return new TotalCondition(range[0], range[1]);
        }

        Set<Rank> ranks = asRankSet(visit(ctx.rankList()));
        if (ranks.size() == 1) {
            return new SoftCondition(ranks.iterator().next());
        }
        return new SoftCondition(ranks);
    }

    @Override
    public DealerCondition visitDealerCondition(ExprParser.DealerConditionContext ctx) {
        int[] range = asRange(visit(ctx.valueRange()));
        return new DealerCondition(range[0], range[1]);
    }

    @Override
    public int[] visitValueRange(ExprParser.ValueRangeContext ctx) {
        int min = Integer.parseInt(ctx.INT(0).getText());
        int max = ctx.INT().size() == 1 ? min : Integer.parseInt(ctx.INT(1).getText());

        if (min > max) {
            throw new IllegalArgumentException("Invalid range in strategy DSL: min is greater than max");
        }

        return new int[]{min, max};
    }

    @Override
    public Set<Rank> visitRankList(ExprParser.RankListContext ctx) {
        Set<Rank> ranks = new LinkedHashSet<>();
        for (ExprParser.RankContext rankContext : ctx.rank()) {
            ranks.add(asRank(visit(rankContext)));
        }
        return ranks;
    }

    @Override
    public Rank visitRank(ExprParser.RankContext ctx) {
        return switch (ctx.getText()) {
            case "ACE" -> Rank.ACE;
            case "KING" -> Rank.KING;
            case "QUEEN" -> Rank.QUEEN;
            case "JACK" -> Rank.JACK;
            case "TEN" -> Rank.TEN;
            case "NINE" -> Rank.NINE;
            case "EIGHT" -> Rank.EIGHT;
            case "SEVEN" -> Rank.SEVEN;
            case "SIX" -> Rank.SIX;
            case "FIVE" -> Rank.FIVE;
            case "FOUR" -> Rank.FOUR;
            case "THREE" -> Rank.THREE;
            case "TWO" -> Rank.TWO;
            default -> throw new IllegalArgumentException("Unsupported rank in strategy DSL: " + ctx.getText());
        };
    }

    @Override
    public Action visitAction(ExprParser.ActionContext ctx) {
        return Action.valueOf(ctx.getText());
    }

    private String runSimulation(int rounds, Strategy strategy) {
        SimulationConfig config = new SimulationConfig();
        config.setRounds(rounds);
        config.setInitialBalance(configuredInitialBalance);
        config.setBetPerGame(configuredBetPerGame);

        Deck deck = new Deck();
        BlackjackGame game = new BlackjackGame(deck);
        SimulationRunner runner = new SimulationRunner(config, game, strategy);
        lastSimulationResult = runner.run();
        resetFrontendExport("SIMULATE_ROUNDS");
        return "";
    }

    private String runSimulationUntilTarget(int targetBalance, Strategy strategy) {
        SimulationConfig config = new SimulationConfig();
        config.setInitialBalance(configuredInitialBalance);
        config.setBetPerGame(configuredBetPerGame);
        config.setTargetBalance(targetBalance);

        Deck deck = new Deck();
        BlackjackGame game = new BlackjackGame(deck);
        SimulationRunner runner = new SimulationRunner(config, game, strategy);
        lastSimulationResult = runner.run();
        resetFrontendExport("SIMULATE_UNTIL_TARGET");
        return "";
    }

    private String runSimulationUntilBroke(Strategy strategy) {
        SimulationConfig config = new SimulationConfig();
        config.setInitialBalance(configuredInitialBalance);
        config.setBetPerGame(configuredBetPerGame);
        config.setRunUntilBroke(true);

        Deck deck = new Deck();
        BlackjackGame game = new BlackjackGame(deck);
        SimulationRunner runner = new SimulationRunner(config, game, strategy);
        lastSimulationResult = runner.run();
        resetFrontendExport("SIMULATE_UNTIL_BROKE");
        return "";
    }

    @Override
    public String visitShow_stat(ExprParser.Show_statContext ctx) {
        if (lastSimulationResult == null) {
            return "No simulation results available. Run 'simulate ... rounds;' first.";
        }

        if (ctx.conditionExpr() == null) {
            frontendExportData.getShowResults().add(
                    new FrontendExportData.ShowResult("all games", buildRoundViews(lastSimulationResult.getRoundResults()))
            );
            return "";
        }

        Filter filter = createFilter(ctx.conditionExpr());
        List<RoundResult> filteredResults = filter.apply(lastSimulationResult.getRoundResults());
        frontendExportData.getShowResults().add(
                new FrontendExportData.ShowResult(describeCondition(ctx.conditionExpr()), buildRoundViews(filteredResults))
        );
        return "";
    }

    @Override
    public String visitStats_stat(ExprParser.Stats_statContext ctx) {
        if (lastSimulationResult == null) {
            return "No simulation results available. Run 'simulate ... rounds;' first.";
        }

        Filter filter = createFilter(ctx.conditionExpr());
        List<RoundResult> filteredResults = filter.apply(lastSimulationResult.getRoundResults());
        frontendExportData.getStatsResults().add(buildStatsResult(ctx, filteredResults));
        return "";
    }

    @Override
    public String visitTimeline_stat(ExprParser.Timeline_statContext ctx) {
        if (lastSimulationResult == null) {
            return "No simulation results available. Run 'simulate ... rounds;' first.";
        }

        Filter filter = createFilter(ctx.conditionExpr());
        List<RoundResult> filteredResults = filter.apply(lastSimulationResult.getRoundResults());
        frontendExportData.getTimelineResults().add(
                new FrontendExportData.TimelineResult(
                        ctx.conditionExpr() == null ? "all games" : describeCondition(ctx.conditionExpr()),
                        buildRoundViews(filteredResults)
                )
        );
        return "";
    }

    @Override
    public Predicate<RoundResult> visitOrCondition(ExprParser.OrConditionContext ctx) {
        List<ExprParser.ConditionTermContext> terms = ctx.conditionTerm();
        Predicate<RoundResult> predicate = asRoundPredicate(visit(terms.get(0)));

        for (int i = 1; i < terms.size(); i++) {
            Predicate<RoundResult> nextPredicate = asRoundPredicate(visit(terms.get(i)));
            predicate = predicate.or(nextPredicate);
        }

        return predicate;
    }

    @Override
    public Predicate<RoundResult> visitAndCondition(ExprParser.AndConditionContext ctx) {
        List<ExprParser.ConditionFactorContext> factors = ctx.conditionFactor();
        Predicate<RoundResult> predicate = asRoundPredicate(visit(factors.get(0)));

        for (int i = 1; i < factors.size(); i++) {
            Predicate<RoundResult> nextPredicate = asRoundPredicate(visit(factors.get(i)));
            predicate = predicate.and(nextPredicate);
        }

        return predicate;
    }

    @Override
    public Predicate<RoundResult> visitComparisonFactor(ExprParser.ComparisonFactorContext ctx) {
        return asRoundPredicate(visit(ctx.comparison()));
    }

    @Override
    public Predicate<RoundResult> visitParenCondition(ExprParser.ParenConditionContext ctx) {
        return asRoundPredicate(visit(ctx.conditionExpr()));
    }

    @Override
    public Predicate<RoundResult> visitCon_tok(ExprParser.Con_tokContext ctx) {
        String propertyName = asProperty(visit(ctx.property()));
        String operator = ctx.comparisonOperator().getText();
        String targetValue = ctx.getChild(2).getText();

        if ("action".equals(propertyName)) {
            Action expectedAction = Action.valueOf(targetValue);
            return roundResult -> matchesAction(roundResult, operator, expectedAction);
        }

        if ("true".equalsIgnoreCase(targetValue) || "false".equalsIgnoreCase(targetValue)) {
            boolean targetBoolean = Boolean.parseBoolean(targetValue);
            return roundResult -> matchesBoolean(roundResult, propertyName, operator, targetBoolean);
        }

        int targetTotal = Integer.parseInt(targetValue);
        return roundResult -> matches(roundResult, propertyName, operator, targetTotal);
    }

    @Override
    public Predicate<RoundResult> visitIn_range_tok(ExprParser.In_range_tokContext ctx) {
        String propertyName = asProperty(visit(ctx.property()));
        int minTotal = Integer.parseInt(ctx.INT(0).getText());
        int maxTotal = Integer.parseInt(ctx.INT(1).getText());

        if (minTotal > maxTotal) {
            throw new IllegalArgumentException("Invalid range in query DSL: min is greater than max");
        }

        return roundResult -> matchesRange(roundResult, propertyName, minTotal, maxTotal);
    }

    @Override
    public Predicate<RoundResult> visitContains_tok(ExprParser.Contains_tokContext ctx) {
        String propertyName = asProperty(visit(ctx.property()));
        Rank rank = asRank(visit(ctx.rank()));
        return roundResult -> matchesContains(roundResult, propertyName, rank);
    }

    @Override
    public String visitProperty(ExprParser.PropertyContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitGroupProperty(ExprParser.GroupPropertyContext ctx) {
        return ctx.getText();
    }

    @Override
    public List<String> visitGroupPropertyList(ExprParser.GroupPropertyListContext ctx) {
        List<String> properties = new ArrayList<>();
        for (ExprParser.GroupPropertyContext propertyContext : ctx.groupProperty()) {
            properties.add(asProperty(visit(propertyContext)));
        }
        return properties;
    }

    private Filter createFilter(ExprParser.ConditionExprContext ctx) {
        if (ctx == null) {
            return new Filter(roundResult -> true);
        }
        Predicate<RoundResult> condition = asRoundPredicate(visit(ctx));
        return new Filter(condition);
    }

    private boolean matches(RoundResult roundResult, String propertyName, String operator, int targetTotal) {
        return switch (propertyName) {
            case "player.total" -> roundResult.hasPlayerTotal(operator, targetTotal);
            case "player.initialTotal", "player.InitialTotal", "player.init", "player.Init" ->
                    compare(roundResult.getPlayerInitialTotal(), operator, targetTotal);
            case "dealer.total" -> compare(roundResult.getDealerValue(), operator, targetTotal);
            case "dealer.upcard", "dealer.Upcard", "dealer.init", "dealer.Init" ->
                    compare(roundResult.getDealerUpcardValue(), operator, targetTotal);
            default -> false;
        };
    }

    private boolean matchesBoolean(RoundResult roundResult, String propertyName, String operator, boolean expectedValue) {
        if (!"=".equals(operator)) {
            throw new IllegalArgumentException("Boolean properties only support '=' comparisons: " + propertyName);
        }

        return switch (propertyName) {
            case "player.isPair", "player.IsPair" -> roundResult.hasPlayerPair() == expectedValue;
            case "player.isSoft", "player.IsSoft" -> roundResult.hasPlayerSoftHand() == expectedValue;
            default -> false;
        };
    }

    private boolean matchesAction(RoundResult roundResult, String operator, Action expectedAction) {
        if (!"=".equals(operator)) {
            throw new IllegalArgumentException("Action filters only support '=' comparisons.");
        }

        return roundResult.getAction() == expectedAction;
    }

    private boolean matchesRange(RoundResult roundResult, String propertyName, int minTotal, int maxTotal) {
        return switch (propertyName) {
            case "player.total" -> roundResult.hasPlayerTotalInRange(minTotal, maxTotal);
            case "player.initialTotal", "player.InitialTotal", "player.init", "player.Init" ->
                    roundResult.getPlayerInitialTotal() >= minTotal && roundResult.getPlayerInitialTotal() <= maxTotal;
            case "dealer.total" -> roundResult.getDealerValue() >= minTotal && roundResult.getDealerValue() <= maxTotal;
            case "dealer.upcard", "dealer.Upcard", "dealer.init", "dealer.Init" ->
                    roundResult.getDealerUpcardValue() >= minTotal && roundResult.getDealerUpcardValue() <= maxTotal;
            default -> false;
        };
    }

    private boolean matchesContains(RoundResult roundResult, String propertyName, Rank rank) {
        return switch (propertyName) {
            case "player.cards" -> roundResult.hasPlayerCard(rank);
            case "dealer.cards" -> roundResult.hasDealerCard(rank);
            default -> false;
        };
    }

    private boolean compare(int actualValue, String operator, int targetValue) {
        return switch (operator) {
            case "=" -> actualValue == targetValue;
            case ">" -> actualValue > targetValue;
            case "<" -> actualValue < targetValue;
            case ">=" -> actualValue >= targetValue;
            case "<=" -> actualValue <= targetValue;
            default -> throw new IllegalArgumentException("Unsupported comparison operator: " + operator);
        };
    }

    private String describeCondition(ExprParser.ConditionExprContext ctx) {
        ExprParser.OrConditionContext orCondition = (ExprParser.OrConditionContext) ctx;
        StringBuilder description = new StringBuilder();
        List<ExprParser.ConditionTermContext> terms = orCondition.conditionTerm();

        for (int i = 0; i < terms.size(); i++) {
            if (i > 0) {
                description.append(" or ");
            }
            description.append(describeConditionTerm(terms.get(i)));
        }

        return description.toString();
    }

    private String describeConditionTerm(ExprParser.ConditionTermContext ctx) {
        ExprParser.AndConditionContext andCondition = (ExprParser.AndConditionContext) ctx;
        StringBuilder description = new StringBuilder();
        List<ExprParser.ConditionFactorContext> factors = andCondition.conditionFactor();

        for (int i = 0; i < factors.size(); i++) {
            if (i > 0) {
                description.append(" and ");
            }
            description.append(describeConditionFactor(factors.get(i)));
        }

        return description.toString();
    }

    private String describeConditionFactor(ExprParser.ConditionFactorContext ctx) {
        if (ctx instanceof ExprParser.ComparisonFactorContext comparisonFactor) {
            ExprParser.ComparisonContext comparison = comparisonFactor.comparison();
            if (comparison instanceof ExprParser.Con_tokContext conTok) {
                return describeComparison(conTok);
            }
            if (comparison instanceof ExprParser.In_range_tokContext inRangeTok) {
                return describeComparison(inRangeTok);
            }
            return describeComparison((ExprParser.Contains_tokContext) comparison);
        }

        ExprParser.ParenConditionContext parenCondition = (ExprParser.ParenConditionContext) ctx;
        return "(" + describeCondition(parenCondition.conditionExpr()) + ")";
    }

    private String describeComparison(ExprParser.Con_tokContext ctx) {
        return ctx.property().getText()
                + " "
                + ctx.comparisonOperator().getText()
                + " "
                + ctx.getChild(2).getText();
    }

    private String describeComparison(ExprParser.In_range_tokContext ctx) {
        return ctx.property().getText()
                + " in "
                + ctx.INT(0).getText()
                + ".."
                + ctx.INT(1).getText();
    }

    private String describeComparison(ExprParser.Contains_tokContext ctx) {
        return ctx.property().getText()
                + " contains "
                + ctx.rank().getText();
    }

    private void resetFrontendExport(String simulationMode) {
        frontendExportData = new FrontendExportData();
        frontendExportData.setSummary(
                new FrontendExportData.Summary(
                        simulationMode,
                        lastSimulationResult.getRoundResults().size(),
                        lastSimulationResult.getPlayerWins(),
                        lastSimulationResult.getDealerWins(),
                        lastSimulationResult.getPushes(),
                        lastSimulationResult.getInitialBalance(),
                        lastSimulationResult.getBetPerGame(),
                        lastSimulationResult.getFinalBalance()
                )
        );
    }

    private List<FrontendExportData.RoundView> buildRoundViews(List<RoundResult> results) {
        List<FrontendExportData.RoundView> roundViews = new ArrayList<>();
        List<RoundResult> allResults = lastSimulationResult.getRoundResults();
        Set<RoundResult> selectedResults = new HashSet<>(results);

        for (int i = 0; i < allResults.size(); i++) {
            RoundResult roundResult = allResults.get(i);
            if (selectedResults.contains(roundResult)) {
                roundViews.add(new FrontendExportData.RoundView(i + 1, roundResult));
            }
        }

        return roundViews;
    }

    private PlayerCondition buildPairCondition(int[] range) {
        if (range[0] == range[1]) {
            return new PairCondition(range[0]);
        }

        List<PlayerCondition> conditions = new ArrayList<>();
        for (int value = range[0]; value <= range[1]; value++) {
            conditions.add(new PairCondition(value));
        }
        return new CompositeCondition(conditions);
    }

    private FrontendExportData.StatsResult buildStatsResult(
            ExprParser.Stats_statContext ctx,
            List<RoundResult> filteredResults
    ) {
        List<String> groupBy = ctx.groupPropertyList() == null
                ? Collections.emptyList()
                : new ArrayList<>(asPropertyList(visit(ctx.groupPropertyList())));
        GroupByClassifier classifier = groupBy.isEmpty() ? null : new GroupByClassifier(groupBy);

        FrontendExportData.SummaryStats summary = null;
        List<FrontendExportData.GroupedStatsEntry> groupedEntries = null;
        FrontendExportData.StreakStats streakStats = null;

        if (classifier == null) {
            summary = toSummaryStats(new StatisticsCalculator().calculate(filteredResults));
        } else if (classifier.isPlayerStreakGrouping()) {
            streakStats = toStreakStats("Player", new StreakStatisticsCalculator().calculatePlayerStreaks(filteredResults));
        } else if (classifier.isDealerStreakGrouping()) {
            streakStats = toStreakStats("Dealer", new StreakStatisticsCalculator().calculateDealerStreaks(filteredResults));
        } else {
            groupedEntries = buildGroupedStats(classifier, filteredResults);
        }

        return new FrontendExportData.StatsResult(
                ctx.conditionExpr() == null ? "all games" : describeCondition(ctx.conditionExpr()),
                groupBy,
                summary,
                groupedEntries,
                streakStats
        );
    }

    private FrontendExportData.SummaryStats toSummaryStats(Statistics statistics) {
        int totalGames = statistics.getTotalGames();
        List<FrontendExportData.ActionStats> actionStats = new ArrayList<>();
        for (Action action : Action.values()) {
            int count = statistics.getActionCounts().getOrDefault(action, 0);
            int wins = statistics.getActionWins().getOrDefault(action, 0);
            double winRate = count == 0 ? 0.0 : (double) wins / count;
            actionStats.add(new FrontendExportData.ActionStats(action.name(), count, wins, winRate));
        }

        return new FrontendExportData.SummaryStats(
                totalGames,
                statistics.getPlayerWins(),
                statistics.getDealerWins(),
                statistics.getDraws(),
                statistics.getPlayerWinRate(),
                statistics.getDealerWinRate(),
                statistics.getDrawRate(),
                rate(statistics.getPlayerBusts(), totalGames),
                rate(statistics.getDealerBusts(), totalGames),
                actionStats
        );
    }

    private List<FrontendExportData.GroupedStatsEntry> buildGroupedStats(
            GroupByClassifier classifier,
            List<RoundResult> filteredResults
    ) {
        Map<GroupByClassifier.GroupKey, List<RoundResult>> groupedResults = new TreeMap<>();
        for (RoundResult result : filteredResults) {
            GroupByClassifier.GroupKey groupKey = classifier.classify(result);
            groupedResults.computeIfAbsent(groupKey, ignored -> new ArrayList<>()).add(result);
        }

        List<FrontendExportData.GroupedStatsEntry> groupedEntries = new ArrayList<>();
        StatisticsCalculator statisticsCalculator = new StatisticsCalculator();
        for (Map.Entry<GroupByClassifier.GroupKey, List<RoundResult>> entry : groupedResults.entrySet()) {
            Statistics statistics = statisticsCalculator.calculate(entry.getValue());
            groupedEntries.add(
                    new FrontendExportData.GroupedStatsEntry(
                            entry.getKey().label(),
                            statistics.getTotalGames(),
                            statistics.getPlayerWinRate(),
                            statistics.getDealerWinRate()
                    )
            );
        }
        return groupedEntries;
    }

    private FrontendExportData.StreakStats toStreakStats(String sideLabel, StreakStatistics streakStatistics) {
        List<FrontendExportData.StreakEntry> entries = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : streakStatistics.getStreakCounts().entrySet()) {
            entries.add(
                    new FrontendExportData.StreakEntry(
                            entry.getKey(),
                            entry.getValue(),
                            streakStatistics.getRate(entry.getKey())
                    )
            );
        }
        return new FrontendExportData.StreakStats(sideLabel, streakStatistics.getTotalStreaks(), entries);
    }

    private double rate(int count, int total) {
        if (total == 0) {
            return 0.0;
        }
        return (double) count / total;
    }



    private Strategy asStrategy(Object value) {
        return (Strategy) value;
    }

    private Rule asRule(Object value) {
        return (Rule) value;
    }

    private PlayerCondition asPlayerCondition(Object value) {
        return (PlayerCondition) value;
    }

    private DealerCondition asDealerCondition(Object value) {
        return (DealerCondition) value;
    }
    private String asProperty(Object value) {
        return (String) value;
    }

    @SuppressWarnings("unchecked")
    private List<String> asPropertyList(Object value) {
        return (List<String>) value;
    }

    private int[] asRange(Object value) {
        return (int[]) value;
    }

    @SuppressWarnings("unchecked")
    private Set<Rank> asRankSet(Object value) {
        return (Set<Rank>) value;
    }

    private Rank asRank(Object value) {
        return (Rank) value;
    }

    private Action asAction(Object value) {
        return (Action) value;
    }

    @SuppressWarnings("unchecked")
    private Predicate<RoundResult> asRoundPredicate(Object value) {
        return (Predicate<RoundResult>) value;
    }
}
