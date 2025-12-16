(

 (module empty (class Empty ()))

 (tmodule broken
          (class G (field))
          ( ( (field ( () ()))) () ))

 (import broken)
 (timport empty ( () () ))
 (def empty (new Empty ()))
 (def g (new G (empty)))
 4.0
 )


