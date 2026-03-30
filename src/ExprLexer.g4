// DELETE THIS CONTENT IF YOU PUT COMBINED GRAMMAR IN Parser TAB
lexer grammar ExprLexer;

AND : 'and' ;
OR : 'or' ;
NOT : 'not' ;
EQ : '=' ;
COMMA : ',' ;
SEMI : ';' ;
LPAREN : '(' ;
RPAREN : ')' ;
LCURLY : '{' ;
RCURLY : '}' ;
SIMULATE: 'simulate';
ROUNDS: 'rounds';
SHOW: 'show';
GAMES: 'games';
WHERE: 'where';
PLAYER: 'player';
DEALER: 'dealer';
TOTAL: 'total';
DOT: '.';


INT : [0-9]+ ;
ID: [a-zA-Z_][a-zA-Z_0-9]* ;
WS: [ \t\n\r\f]+ -> skip ;
