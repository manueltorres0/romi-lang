(
 (module untyped (class C () (method m () (new C ()))   ))

 (tmodule typed (timport untyped ( () ( (m () Number)   ) ))

            (class M ()  (method m () (def c (new C () )) (c --> m ()) ) ) ( () ( (m ()  Number) ) ))

 (import typed)
 (def hello (new M ()))
 (hello --> m ())

 )