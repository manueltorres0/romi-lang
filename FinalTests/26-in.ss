(
 (module empty (class Empty ()))
 (module base (class C (x y)))

 (module broken (import base) (import empty)
   (class Creator () (method make () (def four 4.0) (def e (new Empty ())) (new C (four e)))))

 (timport broken ( () ( (make () ( ( (x Number) (y ( () () )) ) ())))))
 (def c (new Creator ()))
 (def break (c --> make ()))
 (break --> x)
 )