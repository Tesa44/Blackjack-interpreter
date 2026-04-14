import java.util.List;

public class ConditionDescriptionBuilder {
    public String describe(ExprParser.ConditionExprContext ctx) {
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
        return "(" + describe(parenCondition.conditionExpr()) + ")";
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
}
