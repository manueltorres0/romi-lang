(
 (module main
   (class Calculator (base)
     (method add (val)
       (def b (this --> base))
       (b + val))))
 (timport main (((base Number)) ((add (Number) Number))))
 (def ten 10.0)
 (def calc (new Calculator (ten)))
 (def x 5.0)
 (calc --> add (x))
 )