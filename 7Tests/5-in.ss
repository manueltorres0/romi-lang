(
 (module main
   (class C (x)
     (method addToField (val)
       (def f (this --> x))
       (f + val))))
 (timport main (((x Number)) ((addToField (Number) Number))))
 (def ten 10.0)
 (def c (new C (ten)))
 (def five 5.0)
 (c --> addToField (five))
 )