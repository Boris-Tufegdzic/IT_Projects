lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}

// Deca lexer rules.

fragment EOL: '\n';

//KEY WORDS
ASM : 'asm';
CLASS : 'class';
EXTENDS : 'extends';
ELSE : 'else';
FALSE : 'false';
IF : 'if';
INSTANCEOF : 'instanceof';
NEW : 'new';
NULL : 'null';
READINT : 'readInt';
READFLOAT : 'readFloat';
PRINT : 'print';
PRINTLN : 'println';
PRINTLNX : 'printlnx';
PRINTX : 'printx';
PROTECTED : 'protected';
RETURN : 'return';
THIS : 'this';
TRUE : 'true';
WHILE : 'while';

//IDENTIFIER
//Key words are not identifiers (guaranteed because they are defined first)
fragment LETTER : ('a' .. 'z'|'A' .. 'Z');
fragment DIGIT : ('0' .. '9');
IDENT : (LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;

//SPECIAL SYMBOLS
LT : '<';
GT : '>';
EQUALS : '=';
PLUS : '+';
MINUS : '-';
TIMES : '*';
SLASH : '/';
PERCENT : '%';
DOT : '.';
COMMA : ',';
OPARENT : '(';
CPARENT : ')';
OBRACE : '{';
CBRACE : '}';
EXCLAM : '!';
SEMI : ';';
EQEQ : '==';
NEQ : '!=';
GEQ : '>=';
LEQ : '<=';
AND : '&&';
OR : '||';

//INTEGER
fragment POSITIV_DIGIT : ('1' .. '9');
INT : '0' | POSITIV_DIGIT DIGIT*;

//FLOAT
fragment NUM : DIGIT+;
fragment SIGN : '+' | '-' |; 
fragment EXP : ('E' | 'e') SIGN NUM;
fragment DEC : NUM '.' NUM;
fragment FLOATDEC : (DEC | DEC EXP) ('F' | 'f' |); 
fragment DIGITHEX : '0' .. '9' | 'A' .. 'F' | 'a' .. 'f';
fragment NUMHEX : DIGITHEX+;
fragment FLOATHEX : ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN NUM ('F' | 'f'|); 
FLOAT : FLOATDEC | FLOATHEX;


//STRING
fragment STRING_CAR : ~('"' | '\\' | '\n');
STRING : '"' (STRING_CAR | '\\"' | '\\\\')* '"' { String text_without_quotes = getText().substring(1, getText().length() - 1); setText(text_without_quotes); }; //the quotes are removed from STRING.text
MULTI_LINE_STRING : '"' (STRING_CAR | EOL | '\\"' | '\\\\')* '"'{ String text_without_quotes = getText().substring(1, getText().length() - 1); setText(text_without_quotes); };

//COMMENTS
COMMENT : ('/*' .*? '*/') | ('//' .*? EOL) { skip(); }; //To be checked

//SEPARATORS
WS  :   ( ' ' | '\t' | '\r' | EOL ) { skip(); };


//INCLUSION
fragment FILENAME : (LETTER | DIGIT | '.' | '-' | '_')+;
INCLUDE : '#include' (' ')* '"' FILENAME '"' 
{String s = getText();
doInclude(s);};
