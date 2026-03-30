parser grammar ExprParser;
options { tokenVocab=ExprLexer; }
//
//program
//    : stat EOF
//    | def EOF
//    ;
//
//stat: ID '=' expr ';'
//    | expr ';'
//    ;
//
//def : ID '(' ID (',' ID)* ')' '{' stat* '}' ;
//
//expr: ID
//    | INT
//    | func
//    | 'not' expr
//    | expr 'and' expr
//    | expr 'or' expr
//    ;
//
//func : ID '(' expr (',' expr)* ')' ;

program
    : stat+ EOF
    ;

stat:
    SIMULATE expr ROUNDS SEMI #sim_stat
    | SHOW GAMES WHERE expr SEMI #show_stat
    ;

expr:
    INT #int_tok
    | property EQ INT #con_tok
    ;

property:
    PLAYER DOT TOTAL
    | DEALER DOT TOTAL
    ;
