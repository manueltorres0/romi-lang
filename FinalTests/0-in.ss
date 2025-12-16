(
 (module untyped (class C ()))

 (tmodule typed (timport untyped ( () () )) (timport untyped ( () () ))

            (class M ()  (method m () (new C () ) ) ) ( () ( (m ()  ( () () ) ) ) ))

 (import typed)
 (def hello (new M ()))
 (def x (hello --> m ()))
 4.0
 )