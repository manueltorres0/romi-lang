(
 (module main
   (class C (x)))
 (timport main (((x Number)) ()))
 (def val1 5.0)
 (def val2 10.0)
 (def p1 (new C (val1)))
 (def p2 (new C (val2)))
 (p1 + p2)
 )