parser grammar ExprParser;
options { tokenVocab=ExprLexer; }

program
     : strategyBlock? stat* EOF
    ;

stat:
    SIMULATE expr ROUNDS SEMI #sim_stat
    | SHOW GAMES (WHERE expr)? SEMI #show_stat
    ;

expr:
    INT #int_tok
    | property comparisonOperator INT #con_tok
    ;

comparisonOperator
    : EQ
    | GT
    | LT
    | GTE
    | LTE
    ;

property:
    PLAYER DOT TOTAL
    | DEALER DOT TOTAL
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
