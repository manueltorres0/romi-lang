(
 (module main
   (class C (x)
     (method compare ()
       (def a 5.0)
       (def b 5.0)
       (a == b))))
 (timport main (((x Number)) ((compare () Number))))
 (def val 0.0)
 (def c (new C (val)))
 (c --> compare ())
 )