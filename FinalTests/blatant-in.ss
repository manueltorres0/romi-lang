(
       (module faker
                  (class Faker ()
                        (method fake () (new Faker ()))
                  )
       )
       (tmodule A
                  (timport faker
                                  ( ()  ((fake () Number)) )
                  )
                  (class A ()
                         (method make ()  (def f (new Faker ()))  (f --> fake ()))
                  )
                  (
                      () ( (make () Number) )
                 )
       )
       (module c
                  (import A)
                  (class Last ()
                         (method break ()  (def a (new A ()))  (a --> make ()))
                  )
       )
       (timport c
                  ( ()  ((break () Number)) )
        )
        (def o (new Last ()))
        (def issue (o --> break ()))
        4.0
    )