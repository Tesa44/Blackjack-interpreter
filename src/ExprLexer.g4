// DELETE THIS CONTENT IF YOU PUT COMBINED GRAMMAR IN Parser TAB
lexer grammar ExprLexer;

STRATEGY: 'strategy';

WHEN: 'when';
AGAINST: 'against';
THEN: 'then';

PAIR: 'pair';
TOTAL: 'total';
SOFT: 'soft';

HIT: 'HIT';
STAND: 'STAND';
SPLIT: 'SPLIT';
DOUBLE: 'DOUBLE';

ACE: 'ACE';
KING: 'KING';
QUEEN: 'QUEEN';
JACK: 'JACK';
TEN: 'TEN';
NINE: 'NINE';
EIGHT: 'EIGHT';
SEVEN: 'SEVEN';
SIX: 'SIX';
FIVE: 'FIVE';
FOUR: 'FOUR';
THREE: 'THREE';
TWO: 'TWO';

AND : 'and' ;
OR : 'or' ;
NOT : 'not' ;
EQ : '=' ;
COMMA : ',' ;
MINUS : '-' ;
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
DOT: '.';


INT : [0-9]+ ;
ID: [a-zA-Z_][a-zA-Z_0-9]* ;
WS: [ \t\n\r\f]+ -> skip ;
