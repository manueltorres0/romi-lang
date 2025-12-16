(

 (module compare
  (class Compare () (method compare (param) (param --> field))))

(module A (import compare) (class A (field) (method fake () (def c (new Compare ())) (c --> compare (this)) )))

(timport A ( ( (field Number) ) ( (fake () Number) ) ))
(def four 4.0)
(def a (new A (four)))
(a --> fake ())
 )