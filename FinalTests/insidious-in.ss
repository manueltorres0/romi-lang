((module modOne (class M ()  (method return () this)))
        (timport modOne ( () ( (return () Number) ) ))
        (def m (new M ()))
        (m --> return ()))