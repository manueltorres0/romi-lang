((def x 1.0)
 (def res 0.0)
 (def zero 0.0)
 (if0 zero
      (block (def temp 2.0)
             (def sum1 (x + temp))
             (if0 zero
                  (block (def temp2 3.0)
                         (def sum2 (sum1 + temp2))
                         (if0 zero
                              (block (def temp3 4.0)
                                     (def sum3 (sum2 + temp3))
                                     (res = sum3))
                              (res = 0.0)))
                  (res = 0.0)))
      (res = 0.0))
 res)