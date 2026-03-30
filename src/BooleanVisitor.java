import blackjack.engine.BlackjackGame;
import blackjack.engine.Deck;
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

    @Override
    public String visitProgram(ExprParser.ProgramContext ctx) {
        currentStrategy = ctx.strategyBlock() == null
                ? BasicStrategyConfig.create()
                : asStrategy(visit(ctx.strategyBlock()));
        return (String) visit(ctx.stat());
    }

    @Override
    public String visitStat(ExprParser.StatContext ctx) {
        return runSimulation(asInt(visit(ctx.expr())), currentStrategy);
    }

    @Override
    public Integer visitExpr(ExprParser.ExprContext ctx) {
        return Integer.parseInt(ctx.INT().getText());
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
        SimulationResult result = runner.run();

        return "Simulated " + rounds + " rounds -> "
                + "playerWins=" + result.getPlayerWins() + ", "
                + "dealerWins=" + result.getDealerWins() + ", "
                + "pushes=" + result.getPushes();
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
}
