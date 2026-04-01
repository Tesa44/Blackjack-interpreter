parser grammar ExprParser;
options { tokenVocab=ExprLexer; }

program
     : strategyBlock? stat* EOF
    ;

stat:
    SIMULATE INT ROUNDS SEMI #sim_stat
    | SET BALANCE INT SEMI #set_balance_stat
    | SET BET INT SEMI #set_bet_stat
    | PLOT BALANCE SEMI #plot_balance_stat
    | SHOW GAMES (WHERE conditionExpr)? SEMI #show_stat
    | STATS GAMES (WHERE conditionExpr)? (GROUP BY groupPropertyList)? SEMI #stats_stat
    | TIMELINE GAMES (WHERE conditionExpr)? SEMI #timeline_stat
    ;

conditionExpr
    : conditionTerm (OR conditionTerm)* #orCondition
    ;

conditionTerm
    : conditionFactor (AND conditionFactor)* #andCondition
    ;

conditionFactor
    : comparison #comparisonFactor
    | LPAREN conditionExpr RPAREN #parenCondition
    ;

comparison
    : property comparisonOperator (INT | TRUE | FALSE | action) #con_tok
    | property IN INT RANGE INT #in_range_tok
    | property CONTAINS rank #contains_tok
    ;

comparisonOperator
    : EQ
    | GT
    | LT
    | GTE
    | LTE
    ;

property:
    ACTION
    | PLAYER DOT TOTAL
    | PLAYER DOT INITIALTOTAL
    | PLAYER DOT INIT
    | DEALER DOT TOTAL
    | DEALER DOT UPCARD
    | DEALER DOT INIT
    | PLAYER DOT CARDS
    | DEALER DOT CARDS
    | PLAYER DOT ISPAIR
    | PLAYER DOT ISSOFT
    ;

groupProperty
    : property
    | PLAYER DOT STREAKS
    | DEALER DOT STREAKS
    ;

groupPropertyList
    : groupProperty (COMMA groupProperty)*
    ;


strategyBlock
    : STRATEGY LCURLY rule* RCURLY
    ;


rule
    : WHEN playerCondition AGAINST dealerCondition THEN action SEMI
    ;

playerCondition
    : PAIR valueRange
    | TOTAL valueRange
    | SOFT rankList
    ;

dealerCondition
    : valueRange
    ;

valueRange
    : INT (MINUS INT)?
    ;

rankList
    : rank (COMMA rank)*
    ;

rank
    : ACE
    | KING
    | QUEEN
    | JACK
    | TEN
    | NINE
    | EIGHT
    | SEVEN
    | SIX
    | FIVE
    | FOUR
    | THREE
    | TWO
    ;

action
    : HIT
    | STAND
    | SPLIT
    | DOUBLE
    ;
