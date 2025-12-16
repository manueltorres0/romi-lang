(
 (module lala (class A ()))
 (module untyped (import lala) (class C () (method m () (new A ()))   ))

 (tmodule typed (timport untyped ( () ( (m () (()()))   ) ))

            (class M ()  (method m () (def c (new C () )) (c --> m ()) ) ) ( () ( (m ()  (()())) ) ))

 (import typed)
 (def hello (new M ()))
 (hello --> m ())

 )