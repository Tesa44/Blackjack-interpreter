import blackjack.engine.RoundResult;
import blackjack.engine.Rank;
import blackjack.query.Filter;
import blackjack.sim.FrontendExportData;
import blackjack.sim.SimulationResult;
import blackjack.sim.SimulationResultJsonExporter;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class BooleanVisitor extends ExprParserBaseVisitor<Object> {
    private final SimulationResultJsonExporter simulationResultJsonExporter = new SimulationResultJsonExporter();
    private final SimulationFacade simulationFacade = new SimulationFacade();
    private final RoundResultPredicateBuilder predicateBuilder = new RoundResultPredicateBuilder();
    private final ConditionDescriptionBuilder conditionDescriptionBuilder = new ConditionDescriptionBuilder();
    private final RoundViewMapper roundViewMapper = new RoundViewMapper();
    private final StatsExportFactory statsExportFactory = new StatsExportFactory();

    private Strategy currentStrategy = BasicStrategyConfig.create();
    private SimulationResult lastSimulationResult;
    private int configuredInitialBalance = 0;
    private int configuredBetPerGame = 1;
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
        lastSimulationResult = simulationFacade.simulateRounds(
                Integer.parseInt(ctx.INT().getText()),
                configuredInitialBalance,
                configuredBetPerGame,
                currentStrategy
        );
        frontendExportData = simulationFacade.createExportData("SIMULATE_ROUNDS", lastSimulationResult);
        return "";
    }

    @Override
    public String visitSim_until_stat(ExprParser.Sim_until_statContext ctx) {
        lastSimulationResult = simulationFacade.simulateUntilTarget(
                Integer.parseInt(ctx.INT().getText()),
                configuredInitialBalance,
                configuredBetPerGame,
                currentStrategy
        );
        frontendExportData = simulationFacade.createExportData("SIMULATE_UNTIL_TARGET", lastSimulationResult);
        return "";
    }

    @Override
    public String visitSim_broke_stat(ExprParser.Sim_broke_statContext ctx) {
        lastSimulationResult = simulationFacade.simulateUntilBroke(
                configuredInitialBalance,
                configuredBetPerGame,
                currentStrategy
        );
        frontendExportData = simulationFacade.createExportData("SIMULATE_UNTIL_BROKE", lastSimulationResult);
        return "";
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

        frontendExportData.setPlotResult(simulationFacade.createPlotResult(lastSimulationResult));
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

    @Override
    public String visitShow_stat(ExprParser.Show_statContext ctx) {
        if (lastSimulationResult == null) {
            return "No simulation results available. Run 'simulate ... rounds;' first.";
        }

        List<RoundResult> filteredResults = ctx.conditionExpr() == null
                ? lastSimulationResult.getRoundResults()
                : createFilter(ctx.conditionExpr()).apply(lastSimulationResult.getRoundResults());

        frontendExportData.getShowResults().add(
                new FrontendExportData.ShowResult(
                        describeFilter(ctx.conditionExpr()),
                        roundViewMapper.map(lastSimulationResult.getRoundResults(), filteredResults)
                )
        );
        return "";
    }

    @Override
    public String visitStats_stat(ExprParser.Stats_statContext ctx) {
        if (lastSimulationResult == null) {
            return "No simulation results available. Run 'simulate ... rounds;' first.";
        }

        List<String> groupBy = ctx.groupPropertyList() == null
                ? List.of()
                : new ArrayList<>(asPropertyList(visit(ctx.groupPropertyList())));
        List<RoundResult> filteredResults = createFilter(ctx.conditionExpr()).apply(lastSimulationResult.getRoundResults());

        frontendExportData.getStatsResults().add(
                statsExportFactory.create(describeFilter(ctx.conditionExpr()), groupBy, filteredResults)
        );
        return "";
    }

    @Override
    public String visitTimeline_stat(ExprParser.Timeline_statContext ctx) {
        if (lastSimulationResult == null) {
            return "No simulation results available. Run 'simulate ... rounds;' first.";
        }

        List<RoundResult> filteredResults = createFilter(ctx.conditionExpr()).apply(lastSimulationResult.getRoundResults());
        frontendExportData.getTimelineResults().add(
                new FrontendExportData.TimelineResult(
                        describeFilter(ctx.conditionExpr()),
                        roundViewMapper.map(lastSimulationResult.getRoundResults(), filteredResults)
                )
        );
        return "";
    }

    @Override
    public Predicate<RoundResult> visitOrCondition(ExprParser.OrConditionContext ctx) {
        List<ExprParser.ConditionTermContext> terms = ctx.conditionTerm();
        Predicate<RoundResult> predicate = asRoundPredicate(visit(terms.get(0)));

        for (int i = 1; i < terms.size(); i++) {
            predicate = predicate.or(asRoundPredicate(visit(terms.get(i))));
        }

        return predicate;
    }

    @Override
    public Predicate<RoundResult> visitAndCondition(ExprParser.AndConditionContext ctx) {
        List<ExprParser.ConditionFactorContext> factors = ctx.conditionFactor();
        Predicate<RoundResult> predicate = asRoundPredicate(visit(factors.get(0)));

        for (int i = 1; i < factors.size(); i++) {
            predicate = predicate.and(asRoundPredicate(visit(factors.get(i))));
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
        return predicateBuilder.comparison(propertyName, operator, targetValue);
    }

    @Override
    public Predicate<RoundResult> visitIn_range_tok(ExprParser.In_range_tokContext ctx) {
        String propertyName = asProperty(visit(ctx.property()));
        int minTotal = Integer.parseInt(ctx.INT(0).getText());
        int maxTotal = Integer.parseInt(ctx.INT(1).getText());
        return predicateBuilder.range(propertyName, minTotal, maxTotal);
    }

    @Override
    public Predicate<RoundResult> visitContains_tok(ExprParser.Contains_tokContext ctx) {
        String propertyName = asProperty(visit(ctx.property()));
        Rank rank = asRank(visit(ctx.rank()));
        return predicateBuilder.contains(propertyName, rank);
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
        return new Filter(asRoundPredicate(visit(ctx)));
    }

    private String describeFilter(ExprParser.ConditionExprContext ctx) {
        return ctx == null ? "all games" : conditionDescriptionBuilder.describe(ctx);
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
