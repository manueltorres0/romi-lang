(
 (module untyped (class Helper (plain) (method fake () (def o (this --> plain)) (o isa Helper))))

 (module empty (class Empty ()))

 (timport untyped ( ((plain (()()) )) ((fake ()  Number) )))
 (timport empty ( () ()))

 (def e (new Empty ()))
 (def h (new Helper (e)))
 (h --> fake ())
 )