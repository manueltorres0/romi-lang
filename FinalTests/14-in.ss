(

 (module empty (class Empty ()))

 (tmodule broken
          (class G (field))
          ( ( (field ( () ()))) () ))

 (module factory (import empty) (import broken)
   (class Factory () (method create ()
                             (def four 4.0)
                             (def empty (new Empty ()))
                             (def g (new G (empty)))
                             4.0)))

 (import broken)
 (timport factory ( () ( (create () Number)   ) ))
 (def factory (new Factory ()))
 (factory --> create ())
 )


