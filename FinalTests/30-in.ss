(
 (module untyped (class Helper (plain) (method fake () (this --> plain))))

 (module empty (class Empty ()))

 (timport untyped ( ((plain (()()) )) ((fake ()  Number) )))
 (timport empty ( () ()))

 (def e (new Empty ()))
 (def h (new Helper (e)))
 (h --> fake ())
 )