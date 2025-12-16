(

 (tmodule broken
          (class G () (method conform (o) (o isa G)))
          ( () ( (conform  ( ( () () ) )  Number) )   ))

 (module A (class A ()))

 (module empty
   (import broken)
   (import A)
   (class Empty () (method m () (def a (new A ())) (def g (new G ())) (g --> conform (a))) ))

 (timport empty ( () ( (m () Number) ) ))
 (def empty (new Empty ()))
 (empty --> m ())
 )


