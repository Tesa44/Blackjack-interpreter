import blackjack.engine.BlackjackGame;
import blackjack.engine.Deck;
import blackjack.engine.Rank;
import blackjack.sim.SimulationConfig;
import blackjack.sim.SimulationResult;
import blackjack.sim.SimulationRunner;
import blackjack.strategy.Action;
import blackjack.strategy.Rule;
import blackjack.strategy.Strategy;

import blackjack.strategy.condition.CompositeCondition;
import blackjack.strategy.condition.DealerCondition;
import blackjack.strategy.condition.PairCondition;
import blackjack.strategy.condition.PlayerCondition;
import blackjack.strategy.condition.SoftCondition;
import blackjack.strategy.condition.TotalCondition;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class BooleanVisitor extends ExprParserBaseVisitor<String> {
    private Strategy strategy;

    // Parser overridden functions

    @Override
    public String visitProgram(ExprParser.ProgramContext ctx) {
        if (ctx.strategyBlock() == null) {
            throw new IllegalArgumentException("Missing strategy block in DSL input");
        }
        strategy = buildStrategyFromDsl(ctx.strategyBlock());

        // delegate to the single statement inside the program
        return visit(ctx.stat());
    }

    @Override
    public String visitStat(ExprParser.StatContext ctx) {
        // Current grammar: 'simulate' expr 'rounds' ';'
        // expr is just an INT, representing number of rounds
        int rounds = Integer.parseInt(ctx.expr().getText());

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

    // Helper functions to build strategy

    private Strategy buildStrategyFromDsl(ExprParser.StrategyBlockContext strategyBlockContext) {
        List<Rule> rules = new ArrayList<>();

        for (ExprParser.RuleContext ruleContext : strategyBlockContext.rule_()) {
            rules.add(buildRule(ruleContext));
        }

        return new Strategy(rules);
    }

    private Rule buildRule(ExprParser.RuleContext ruleContext) {
        PlayerCondition playerCondition = buildPlayerCondition(ruleContext.playerCondition());
        DealerCondition dealerCondition = buildDealerCondition(ruleContext.dealerCondition());
        Action action = Action.valueOf(ruleContext.action().getText());

        return new Rule(playerCondition, dealerCondition, action);
    }

    private PlayerCondition buildPlayerCondition(ExprParser.PlayerConditionContext playerConditionContext) {
        if (playerConditionContext.PAIR() != null) {
            int[] range = parseRange(playerConditionContext.valueRange());
            if (range[0] == range[1]) {
                return new PairCondition(range[0]);
            }

            List<PlayerCondition> conditions = new ArrayList<>();
            for (int value = range[0]; value <= range[1]; value++) {
                conditions.add(new PairCondition(value));
            }
            return new CompositeCondition(conditions);
        }

        if (playerConditionContext.TOTAL() != null) {
            int[] range = parseRange(playerConditionContext.valueRange());
            return new TotalCondition(range[0], range[1]);
        }

        Set<Rank> ranks = new HashSet<>();

        for (ExprParser.RankContext rankContext : playerConditionContext.rankList().rank()) {
            ranks.add(toRank(rankContext.getText()));
        }
        if (ranks.size() == 1) {
            return new SoftCondition(ranks.iterator().next());
        }

        return new SoftCondition(ranks);
    }

    private DealerCondition buildDealerCondition(ExprParser.DealerConditionContext dealerConditionContext) {
        int[] range = parseRange(dealerConditionContext.valueRange());

        return new DealerCondition(range[0], range[1]);
    }

    private int[] parseRange(ExprParser.ValueRangeContext valueRangeContext) {
        int min = Integer.parseInt(valueRangeContext.INT(0).getText());

        if (valueRangeContext.INT().size() == 1) {
            return new int[]{min, min};
        }

        int max = Integer.parseInt(valueRangeContext.INT(1).getText());

        if (min > max) {
            throw new IllegalArgumentException("Invalid range in strategy DSL: min is greater than max");
        }

        return new int[]{min, max};
    }

    private Rank toRank(String rankText) {
        return switch (rankText) {
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
            default -> throw new IllegalArgumentException("Unsupported rank in strategy DSL: " + rankText);
        };
    }
}
