(
 (tmodule typed (class Data (num)) (((num Number)) ()))
 (import typed)
 (def zero 0.0)
 (def p (new Data (zero)))
 (p isa Data)
 )