(

 (module accempty (class accEmpty ()))
 (module empty (class Empty (inner)))

 (module factory (import empty) (import accempty)
   (class Factory () (method create ()
                             (def inner (new accEmpty()))
                             (def empty (new Empty (inner)))
                             (def outer (new Empty (empty)))
                             outer)))

 (timport factory ( () ( (create ()
                                 ( ((inner  ( ( (inner ( () ())  ) )  () ) )) () )
                                 )   ) ))
 (def factory (new Factory ()))
 (def broken (factory --> create ()))
 4.0
 )


