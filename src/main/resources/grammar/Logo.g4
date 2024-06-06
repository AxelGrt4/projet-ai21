grammar Logo ; 

@header {package fr.utc.parsing;}

FLOAT : [0-9][0-9]*('.'[0-9]+)? ;
WS : [ \t\r\n]+ -> skip ;
COMMENT1 : '//' .*? [\r\n]+ -> skip;
COMMENT2 : '/*' .*? '*/' -> skip;

programme :
 liste_instructions  
;

liste_instructions :   
 (instruction)+    
;

instruction :
   'av' expr # av
 | 'td' expr # td
 | 'tg' expr # tg
 | 'lc' # lc
 | 'bc' # bc
 | 're' expr # re
 | 'fpos' expr expr # fpos
 | 'fcc' expr # fcc
 | 'fcap' expr # fcap
 | 'repete' expr '[' liste_instructions ']' #repete
 | 'store' #store
 | 'move' #move
; 

expr :
   FLOAT         # float
 | '(' expr ')'  # parenthese
 | expr ('*' | '/') expr #mult
 | expr ('+' | '-') expr #sum
 | 'hasard' expr #hasard
 | ( 'cos(' | 'sin(') expr ')' #cos
 | 'loop' #loop
;

