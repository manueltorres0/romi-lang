(
 (module main
   (class C (x y)))
 (timport main (((x Number) (y Number)) ()))
 (def a 5.0)
 (def b 10.0)
 (def p (new C (a b)))
 (def val 20.0)
 (p --> z = val)
 (p --> x)
 )