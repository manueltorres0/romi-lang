(

 (module empty (class Empty () (method m () 4.0) ))

 (tmodule broken
          (class G (field))
          ( ( (field ( () ()))) () ))

 (import broken)
 (timport empty ( () () ))
 (def empty (new Empty ()))
 4.0
 )


