// DELETE THIS CONTENT IF YOU PUT COMBINED GRAMMAR IN Parser TAB
lexer grammar ExprLexer;

STRATEGY: 'strategy';

WHEN: 'when';
AGAINST: 'against';
THEN: 'then';

PAIR: 'pair';
TOTAL: 'total';
SOFT: 'soft';
CARDS: 'cards';

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
IN : 'in' ;
CONTAINS : 'contains' ;
GTE : '>=' ;
LTE : '<=' ;
GT : '>' ;
LT : '<' ;
EQ : '=' ;
RANGE : '..' ;
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
STATS: 'stats';
GAMES: 'games' | 'game';
WHERE: 'where';
GROUP: 'group';
BY: 'by';
ACTION: 'action';
PLAYER: 'player';
DEALER: 'dealer';
INIT: 'init' | 'Init';
UPCARD: 'upcard' | 'Upcard';
INITIALTOTAL: 'initialTotal' | 'InitialTotal';
ISPAIR: 'isPair' | 'IsPair';
ISSOFT: 'isSoft' | 'IsSoft';
TRUE: 'true' | 'TRUE';
FALSE: 'false' | 'FALSE';
DOT: '.';


INT : [0-9]+ ;
ID: [a-zA-Z_][a-zA-Z_0-9]* ;
WS: [ \t\n\r\f]+ -> skip ;
