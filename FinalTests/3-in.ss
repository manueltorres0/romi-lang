(
 (module untyped (class C ()))

 (tmodule typed (timport untyped ( ( (field Number)) () ))

            (class M ()  (method m () (def four 4.0) (def c (new C (four) )) (c --> field) ) ) ( () ( (m ()  Number) ) ))

 (import typed)
 (def hello (new M ()))
 (hello --> m ())
 )