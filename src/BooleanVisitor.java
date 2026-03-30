import blackjack.engine.BlackjackGame;
import blackjack.engine.Deck;
import blackjack.engine.RoundResult;
import blackjack.engine.Rank;
import blackjack.sim.SimulationConfig;
import blackjack.sim.SimulationResult;
import blackjack.sim.SimulationRunner;
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

public class BooleanVisitor extends ExprParserBaseVisitor<Object> {
    private Strategy currentStrategy = BasicStrategyConfig.create();
    private SimulationResult lastSimulationResult;


    @Override
    public String visitProgram(ExprParser.ProgramContext ctx) {
        currentStrategy = ctx.strategyBlock() == null
                ? BasicStrategyConfig.create()
                : asStrategy(visit(ctx.strategyBlock()));

        StringBuilder output = new StringBuilder();
        for (ExprParser.StatContext statContext : ctx.stat()) {
            String statementResult = asString(visit(statContext));
            if (statementResult == null || statementResult.isBlank()) {
                continue;
            }

            if (!output. isEmpty()) {
                output.append(System.lineSeparator()).append(System.lineSeparator());
            }
            output.append(statementResult);
        }

        return output.toString();
    }


    @Override
    public String visitSim_stat(ExprParser.Sim_statContext ctx) {
        return runSimulation(asInt(visit(ctx.expr())), currentStrategy);
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

        Deck deck = new Deck();
        BlackjackGame game = new BlackjackGame(deck);
        SimulationRunner runner = new SimulationRunner(config, game, strategy);
         lastSimulationResult = runner.run();

        return "Simulated " + rounds + " rounds -> "
                + "playerWins=" + lastSimulationResult.getPlayerWins() + ", "
                + "dealerWins=" + lastSimulationResult.getDealerWins() + ", "
                + "pushes=" + lastSimulationResult.getPushes();
    }

    @Override
    public String visitShow_stat(ExprParser.Show_statContext ctx) {
        if (lastSimulationResult == null) {
            return "No simulation results available. Run 'simulate ... rounds;' first.";
        }

        if (ctx.expr() == null) {
            return renderAllRounds();
        }

        Object showExpressionResult = visit(ctx.expr());
        if (showExpressionResult instanceof String) {
            return asString(showExpressionResult);
        }

        return renderAllRounds();
    }

    @Override
    public Integer visitInt_tok(ExprParser.Int_tokContext ctx) {
        return Integer.parseInt(ctx.INT().getText());
    }

    @Override
    public String visitCon_tok(ExprParser.Con_tokContext ctx) {
        String propertyName = asProperty(visit(ctx.property()));
        int targetTotal = Integer.parseInt(ctx.INT().getText());
        return filterRounds(propertyName, targetTotal);
    }

    @Override
    public String visitProperty(ExprParser.PropertyContext ctx) {
        return ctx.getText();
    }

    private String filterRounds(String propertyName, int targetTotal) {
        StringBuilder output = new StringBuilder();
        int matches = 0;

        List<RoundResult> roundResults = lastSimulationResult.getRoundResults();
        for (int i = 0; i < roundResults.size(); i++) {
            RoundResult roundResult = roundResults.get(i);
            if (!matches(roundResult, propertyName, targetTotal)) {
                continue;
            }

            if (matches == 0) {
                output.append("Filtered results for ")
                        .append(propertyName)
                        .append(" = ")
                        .append(targetTotal)
                        .append(":")
                        .append(System.lineSeparator());
            } else {
                output.append(System.lineSeparator());
            }

            output.append("Game #").append(i + 1).append(System.lineSeparator());
            output.append(roundResult.describeRoundSummary());
            matches++;
        }

        if (matches == 0) {
            return "No games matched " + propertyName + " = " + targetTotal + ".";
        }

        output.append(System.lineSeparator())
                .append("Matched games: ")
                .append(matches);
        return output.toString();
    }

    private boolean matches(RoundResult roundResult, String propertyName, int targetTotal) {
        return switch (propertyName) {
            case "player.total" -> roundResult.hasPlayerTotal(targetTotal);
            case "dealer.total" -> roundResult.getDealerValue() == targetTotal;
            default -> false;
        };
    }

    private String renderAllRounds() {
        StringBuilder output = new StringBuilder();
        List<RoundResult> roundResults = lastSimulationResult.getRoundResults();

        if (roundResults.isEmpty()) {
            return "No games were recorded in the last simulation.";
        }

        output.append("All game results:").append(System.lineSeparator());
        for (int i = 0; i < roundResults.size(); i++) {
            if (i > 0) {
                output.append(System.lineSeparator());
            }
            output.append("Game #").append(i + 1).append(System.lineSeparator());
            output.append(roundResults.get(i).describeRoundSummary());
        }

        output.append(System.lineSeparator())
                .append("Total games: ")
                .append(roundResults.size());
        return output.toString();
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

    private int asInt(Object value) {
        return (Integer) value;
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

    private String asString(Object value) {
        return (String) value;
    }
}
