parser grammar ExprParser;
options { tokenVocab=ExprLexer; }

program
    : strategyBlock? stat EOF
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

stat
    : SIMULATE expr ROUNDS SEMI
    ;

expr
    : INT
    ;