(
 (module untyped (class Helper (data)))

 (tmodule typed (timport untyped (((data Number)) ()))

          (class Processor (value)
            (method process (n) (this --> value = n) (this --> value))

            (method broken () (def four 4.0) (def obj (new Helper (four))) (this --> process (four))))

   (((value Number)) ((process (Number) Number) (broken () Number))))

 (import typed)
 (def zero 0.0)
 (def p (new Processor (zero)))
 (p --> broken ())
 )