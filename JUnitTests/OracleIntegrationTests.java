import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OracleIntegrationTests {
  private final PrintStream originalOut = System.out;
  private final InputStream originalIn = System.in;
  private ByteArrayOutputStream outContent;

  @BeforeEach
  void setUp() {
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void resetStream() {
    outContent.reset();
    System.setIn(originalIn);
    System.setOut(originalOut);
  }

  private String runProgram(String args) {
    System.setIn(new ByteArrayInputStream(args.getBytes()));
    try {
      Main.main(new String[]{});
    } catch (Exception e) {
      e.printStackTrace();
    }
    return outContent.toString().trim();
  }


  @Test
  public void testOracle() {
    String input = """
        ((module Calc (class Calculator (base)
                       (method add (val) (def b (this --> base)) (b + val))))
                     (import Calc)
                     (def ten 10.0)
                     (def calc (new Calculator (ten)))
                     (def x 5.0)
                     (calc --> add (x)))
        """;
    String output = runProgram(input);
    assertEquals("15.0", output);
  }

  @Test
  public void test_Tests_copy_10_test_0() {
    String input = """
        ((module ModLeaf (class Leaf (v)))
         (module ModNode (class Node (l r)))
         (import ModLeaf)
         (import ModNode)
         (def one 1.0)
         (def two 2.0)
         (def three 3.0)
         (def lOne (new Leaf (one)))
         (def lTwo (new Leaf (two)))
         (def lThree (new Leaf (three)))
         (def lTwoClone (new Leaf (two)))
         (def simplestNode (new Node (lOne lTwo)))
         (def simplestNodeVariant (new Node (lThree lTwoClone)))
         (def ans -1.0)
         (if0 (simplestNode == simplestNodeVariant)
              (ans = 0.0)
              (ans = 1.0))
         ans)
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_10_test_5() {
    String input = """
        ((module ModApple (class Apple ()))
         (import ModApple)
         (def apple (new Apple ()))
         (def five 5.0)
         (five / apple))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_10_test_7() {
    String input = """
        ((module ModCounter
           (class Counter (initial)
             (method increment (delta)
               (def current (this --> initial))
               (def next (current + delta))
               (this --> initial = next)
               next)))
         (import ModCounter)
         (def zero 0.0)
         (def myCounter (new Counter (zero)))
         (def one 1.0)
         (def next 1000.0)
         (def a (myCounter --> increment (one)))
         next)
        """;
    String output = runProgram(input);
    assertEquals("1000.0", output);
  }

  @Test
  public void test_Tests_copy_10_test_8() {
    String input = """
        ((module ModCounter
           (class Counter (initial)
             (method increment (delta)
               (def current (this --> initial))
               (def next (current + delta))
               (this --> initial = next)
               next)))
         (import ModCounter)
         (def zero 0.0)
         (def myCounter (new Counter (zero)))
         (def one 1.0)
         (def tmp (myCounter --> increment (one)))
         (myCounter --> increment (one)))
        """;
    String output = runProgram(input);
    assertEquals("2.0", output);
  }

  @Test
  public void test_Tests_copy_10_test_9() {
    String input = """
        ((module ModCounter
           (class Counter (initial)
             (method increment (delta)
               (def current (this --> initial))
               (def next (current + delta))
               (this --> initial = next)
               next)))
         (import ModCounter)
         (def zero 0.0)
         (def myCounter (new Counter (zero)))
         (def one 1.0)
         (def tmp (myCounter --> increment (one)))
         (tmp = (myCounter --> increment (one)))
         (myCounter --> initial = 1000.0)
         (myCounter --> increment (one)))
        """;
    String output = runProgram(input);
    assertEquals("1001.0", output);
  }

  @Test
  public void test_Tests_copy_11_test_0() {
    String input = """
        ((module ModTest (class Test (x y)))
         (import ModTest)
         (def x 1.0)
         (def test (new Test (x)))
         (test isa Test))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_11_test_1() {
    String input = """
        ((module ModTest
           (class Test (x)
             (method getX () (this --> x))))
         (import ModTest)
         (def one 1.0)
         (def instance (new Test (one)))
         (if0 (instance isa Test)
              (one = 2.0)
              (instance --> x = 1.0))
         instance)
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_11_test_2() {
    String input = """
        ((module ModTest (class Test (x)))
         (import ModTest)
         (def x 1.0)
         (def test (new Test (x)))
         (test --> y))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_11_test_3() {
    String input = """
        ((module ModTest
           (class Test (x)
             (method getX () (this --> x))))
         (import ModTest)
         (def one 1.0)
         (def instance (new Test (one)))
         (one + instance))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_11_test_4() {
    String input = """
        ((module ModTest
           (class Test (x)
             (method getX () (this --> x))))
         (module ModMutator
           (import ModTest)
           (class Mutator (test)
             (method mutate ()
               (def y (this --> test))
               (y --> x = 2.0)
               0.0)))
         (import ModTest)
         (import ModMutator)
         (def x 1.0)
         (def test (new Test (x)))
         (def mutator (new Mutator (test)))
         (x = (mutator --> mutate ()))
         (test --> getX ()))
        """;
    String output = runProgram(input);
    assertEquals("2.0", output);
  }

  @Test
  public void test_Tests_copy_11_test_5() {
    String input = """
        ((module ModTest
           (class Test (x)
             (method getX () (this --> x))))
         (import ModTest)
         (def x 1.0)
         (def test (new Test (x)))
         (test --> getX (x)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_11_test_6() {
    String input = """
        ((module ModTest
           (class Test (x)
             (method getX () (this --> x))))
         (import ModTest)
         (def x 1.0)
         (def test (new Test (x)))
         (test --> getY (x)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_11_test_7() {
    String input = """
        ((module ModTest
           (class Test (x)
             (method getX () (this --> x))))
         (import ModTest)
         (def one 1.0)
         (def instance (new Test (one)))
         (def result -1.0)
         (if0 instance
              (result = 0.0)
              (result = 1.0))
         result)
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_11_test_8() {
    String input = """
        ((module ModTest
           (class Test (x)
             (method getX () (this --> x))))
         (import ModTest)
         (def x 1.0)
         (def test (new Test (x)))
         (x --> getX ()))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_11_test_9() {
    String input = """
        ((module ModTest
           (class Test (x)
             (method getX () (this --> x))))
         (import ModTest)
         (def one 1.0)
         (def instance (new Test (one)))
         (if0 (instance isa Test)
              (one = 2.0)
              (instance --> x = 1.0))
         (instance --> getX ()))
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_12_test_0() {
    String input = """
        ((def x 1.0)
         (def y 2.0)
         z)
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_12_test_1() {
    String input = """
        ((module ModMath (class Math (x y)))
         x)
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_12_test_3() {
    String input = """
        ((module ModGoodClass
           (class goodClass (x)
             (method myMethod (x y) 1.0)))
         (def x 5.0)
         (def y x)
         (new badClass (x)))
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_12_test_5() {
    String input = """
        ((module ModNewClass (class newClass (x x)))
         1.9)
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_12_test_6() {
    String input = """
        ((module ModCowboy
           (class Cowboy (hat hat)
             (method draw () -1.0)))
         (module ModCowboyTwo
           (class cowboy (hat)
             (method draw () 1.0)))
         (import ModCowboy)
         (def cowboyHat 1.0)
         (new Cowboy (cowboyHat)))
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_12_test_7() {
    String input = """
        ((def w 3.0)
         (def x 8.0)
         (def y 1.0)
         (def z -1.0)
         (if0 (y + z)
              (block
                (a = 0.0)
                (while0 a
                        (block
                          (final = (w / x))
                          (a = 1.0))))
              (z = 0.0))
         (final + final))
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_12_test_8() {
    String input = """
        ((module ModMyGreatClass
           (class myGreatClass (a b ab)
             (method myMethod (x y) 1.0)
             (method myMethodTwo (x) y)))
         1.0)
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_12_test_9() {
    String input = """
        ((module ModMyGreatClass
           (class myGreatClass ()
             (method class ())))
         (module ModMyGreatClassTwo
           (class myGreatClassTwo (x x)))
         (def a 1.0)
         a)
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_14_test_0() {
    String input = """
        ((def x 0.0)
         (if0 x
              (block
                (def y 2.0)
                (x = y))
              (block
                (x = 3.0)))
         (if0 (x == x)
              (block
                (def y 3.0)
                (x = y))
              (block
                (x = 3.0)))
         x)
        """;
    String output = runProgram(input);
    assertEquals("3.0", output);
  }


  @Test
  public void test_Tests_copy_15_test_6() {
    String input = """
        ((module ModIsaClass (class IsaClass ()))
         (module ModIsanotherClass (class IsanotherClass ()))
         (import ModIsaClass)
         (import ModIsanotherClass)
         (def instance (new IsaClass ()))
         (def true (instance isa IsaClass))
         (def false (instance isa IsanotherClass))
         (true + false))
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_16_test_0() {
    String input = """
        ((module ModPoint
           (class Point (x)
             (method addy (y) (x + y))))
         (def this 2.0)
         (def three 3.0)
         (def new (new Point (this)))
         (new --> x = 4.0)
         (new --> addy (three)))
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_16_test_1() {
    String input = """
        ((module ModPointA
           (class PointA (x y)
             (method addCoord () 2.0)))
         (module ModPointB
           (class PointB (x y)
             (method addCoord () 1.0)))
         (import ModPointA)
         (import ModPointB)
         (def one 1.0)
         (def two 2.0)
         (def pA (new PointA (one two)))
         (def pAA (new PointA (two one)))
         (def pB (new PointB (one two)))
         (def result -1.0)
         (if0 (pB == pA)
              (result = pB)
              (if0 (pA == pAA)
                   (result = one)
                   (if0 (two == pA)
                        (result = two)
                        (block
                          (pAA --> x = (pA --> x))
                          (pAA --> y = (pA --> y))
                          (if0 (pA == pAA)
                               (result = 413.0)
                               (result = 612.0))))))
         result)
        """;
    String output = runProgram(input);
    assertEquals("413.0", output);
  }

  @Test
  public void test_Tests_copy_16_test_2() {
    String input = """
        ((module ModPointThreeD
           (class PointThreeD (x y z)
             (method delta ()
               (def x (this --> x))
               (def y (this --> y))
               (x + y))))
         (import ModPointThreeD)
         (def x 1.0)
         (def point (new PointThreeD (x x)))
         (point --> x = x)
         (x = (point --> delta ()))
         x)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_16_test_3() {
    String input = """
        ((module ModPointThreeD
           (class PointThreeD (x y z)
             (method delta ()
               (def x (this --> x))
               (def y (this --> y))
               (x + y))))
         (import ModPointThreeD)
         (def x 1.0)
         (def point (new PointThreeD (x x x)))
         (point --> x = x)
         (x = (point --> gamma ()))
         x)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_16_test_4() {
    String input = """
        ((module ModPointA
           (class PointA (x y)
             (method addCoord () 2.0)))
         (module ModPointB
           (class PointB (x y)
             (method addCoord () 1.0)))
         (import ModPointA)
         (import ModPointB)
         (def one 1.0)
         (def two 2.0)
         (def pA (new PointA (one two)))
         (def pAA (new PointA (two one)))
         (def pB (new PointB (one two)))
         (def result -1.0)
         (pB = pAA)
         (if0 (pB isa PointB)
              (result = pB)
              (result = (pB --> addCoord (pA))))
         result)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_16_test_5() {
    String input = """
        ((module ModPointA
           (class PointA (x y)
             (method addCoord () 2.0)))
         (module ModMultiplier
           (class Multiplier ()
             (method times (m n)
               (def negOne -1.0)
               (def result 0.0)
               (def keepRunning 0.0)
               (while0 keepRunning
                       (block
                         (result = (result + m))
                         (n = (n + negOne))
                         (if0 n
                              (keepRunning = 1.0)
                              (keepRunning = 0.0))))
               result)))
         (import ModPointA)
         (import ModMultiplier)
         (def one 1.0)
         (def two 2.0)
         (def pA (new PointA (one two)))
         (def multer (new Multiplier ()))
         (multer --> times (pA one)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_16_test_6() {
    String input = """
        ((module ModPointA
           (class PointA (x y)
             (method addCoord () 2.0)
             (method setInternal (x)
               (x = 2.0)
               x)))
         (import ModPointA)
         (def one 1.0)
         (def two 2.0)
         (def pA (new PointA (one two)))
         (def x (new PointA (one two)))
         (pA = (pA --> setInternal (x)))
         x)
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_16_test_7() {
    String input = """
        ((module ModFib
           (class Fib (fstFib sndFib)
             (method calcN (n)
               (def result 0.0)
               (def prevvFib (this --> fstFib))
               (def prevFib (this --> sndFib))
               (def negOne -1.0)
               (def runCount (n + negOne))
               (def keepRunning 0.0)
               (while0 keepRunning
                       (block
                         (result = (prevFib + prevvFib))
                         (prevvFib = prevFib)
                         (prevFib = result)
                         (runCount = (runCount + negOne))
                         (if0 runCount
                              (keepRunning = 1.0)
                              (keepRunning = 0.0))))
               result)))
         (import ModFib)
         (def fstNum 0.0)
         (def sndNum 1.0)
         (def fibInstance (new Fib (fstNum sndNum)))
         (def n 10.0)
         (def result (fstNum --> calcN (n)))
         result)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_16_test_8() {
    String input = """
        ((module ModFib
           (class Fib (fstFib sndFib)
             (method calcN (n)
               (def result 0.0)
               (def prevvFib (this --> fstFib))
               (def prevFib (this --> sndFib))
               (def negOne -1.0)
               (def runCount (n + negOne))
               (def keepRunning 0.0)
               (while0 keepRunning
                       (block
                         (result = (prevFib + prevvFib))
                         (prevvFib = prevFib)
                         (prevFib = result)
                         (runCount = (runCount + negOne))
                         (if0 runCount
                              (keepRunning = 1.0)
                              (keepRunning = 0.0))))
               result)))
         (import ModFib)
         (def fstNum 0.0)
         (def sndNum 1.0)
         (def fibInstance (new Fib (fstNum sndNum)))
         (def n 10.0)
         (def result (fibInstance --> calcN (n)))
         result)
        """;
    String output = runProgram(input);
    assertEquals("55.0", output);
  }

  @Test
  public void test_Tests_copy_16_test_9() {
    String input = """
        ((module ModMultiplier
           (class Multiplier ()
             (method times (m n)
               (def negOne -1.0)
               (def result 0.0)
               (def keepRunning 0.0)
               (while0 keepRunning
                       (block
                         (result = (result + m))
                         (n = (n + negOne))
                         (if0 n
                              (keepRunning = 1.0)
                              (keepRunning = 0.0))))
               result)))
         (module ModFact
           (import ModMultiplier)
           (class Fact ()
             (method calcN (n)
               (def result 1.0)
               (if0 n
                    (result = 1.0)
                    (block
                      (def negOne -1.0)
                      (def nMinOne (n + negOne))
                      (def calcNminOne (this --> calcN (nMinOne)))
                      (def multiplier (new Multiplier ()))
                      (result = (multiplier --> times (n calcNminOne)))))
               result)))
         (import ModFact)
         (def factorial (new Fact ()))
         (def n 5.0)
         (factorial --> calcN (n)))
        """;
    String output = runProgram(input);
    assertEquals("120.0", output);
  }

  @Test
  public void test_Tests_copy_17_test_0() {
    String input = """
        ((module ModA
           (import ModT)
           (class A (x)
             (method m () -100.0)
             (method a ()
               (def c (new T ()))
               -100.0)))
         (module ModT
           (class T (x)
             (method a () -100.0)))
         (def y 1.0)
         y)
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_17_test_1() {
    String input = """
        ((module ModA
           (class A (fOne fTwo)
             (method mOne (pOne)
               (def x 1.0)
               (x = pOne)
               x)))
         (module ModB
           (class B (fThree)
             (method mTwo (pTwo)
               (def y 2.0)
               (y = pTwo)
               y)))
         (module ModC
           (class C (fThree)
             (method mTwo (pTwo)
               (def y 2.0)
               (y = pTwo)
               y)))
         (def a 10.0)
         a)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void test_Tests_copy_17_test_2() {
    String input = """
        ((module ModA (class A ()))
         (def x 1.0)
         (def y 0.0)
         (x / y))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_17_test_3() {
    String input = """
        ((module ModA (class A (x)))
         (import ModA)
         (def x 1.0)
         (def y 2.0)
         (new A (x y)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_17_test_4() {
    String input = """
        ((module ModCowboy
           (class Cowboy (hat)
             (method draw () -1.0)))
         (module ModCowboyTwo
           (class cowboy (hat)
             (method draw () 1.0)))
         (import ModCowboy)
         (def cowboyHat 1.0)
         (new Cowboy (cowboyHat)))
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_17_test_5() {
    String input = """
        ((module ModCounter
           (class counter (value)
             (method inc (one)
               (def value (this --> value))
               (this --> value = (value + one))
               (this --> value))))
         (import ModCounter)
         (def one 1.0)
         (def zero 0.0)
         (def c (new counter (zero)))
         (c --> value = (c --> inc (one)))
         (new counter (one)))
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_17_test_6() {
    String input = """
        ((module ModGoodClass
           (class goodClass (x)
             (method myMethod (x y) 1.0)))
         (def x 5.0)
         (def y x)
         (new badClass (x)))
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_17_test_7() {
    String input = """
        ((module ModBallerina
           (class ballerina (skill height speed)
             (method twirl (speed x y) 1.0)
             (method speed () 2.0)
             (method twirl (speed x) 2.0)))
         (module ModGymnast
           (class gymnast (skill height speed)
             (method jump (speed x y xy x) 1.0)))
         (def x 5.0)
         (def y x)
         y)
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_17_test_9() {
    String input = """
        ((module ModTest
           (class Test ()
             (method testMethod 1.0)))
         1.0)
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_18_test_0() {
    String input = """
        ((def x = 1.0) 0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_18_test_1() {
    String input = """
        ((block (def x 1.0) (x = 1.0)) 0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_18_test_2() {
    String input = """
        ((def x 1.0) (x + 2.0))
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_18_test_4() {
    String input = """
        ((module ModA (class A (x x))) 0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_18_test_5() {
    String input = """
        ((module ModA
           (class A (x)
             (method m () 0.0)
             (method m () 0.0)))
         0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_18_test_6() {
    String input = """
        ((module ModA
           (class A ()
             (method m (p p) 0.0)))
         0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_18_test_7() {
    String input = """
        ((def y 1.0) x)
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_18_test_8() {
    String input = """
        ((def y 1.0) (x = 1.0) 0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_18_test_9() {
    String input = """
        ((def a 1.0) (new Missing (a)))
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_19_test_0() {
    String input = """
        ((module ModC
           (class C (x y)
             (method delta (x)
               (def z (this --> x))
               (def x (x + x))
               (z + x))))
         (import ModC)
         (def x 1.0)
         (def c (new C (x x)))
         (def i (c isa C))
         (x = (c --> delta (x)))
         (x + i))
        """;
    String output = runProgram(input);
    assertEquals("3.0", output);
  }

  @Test
  public void test_Tests_copy_19_test_1() {
    String input = """
        ((module ModC
           (class C (x y)
             (method delta (x)
               (def z (this --> x))
               (def x (x + x))
               (z + x))))
         (import ModC)
         (def b 5.0)
         (def c 10.0)
         (def classVar (new C (b c)))
         classVar)
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_19_test_2() {
    String input = """
        ((module ModC
           (class C (x y)
             (method delta (x)
               (def z (this --> x))
               (def x (x + x))
               (z + x))))
         (import ModC)
         (def b 5.0)
         (def c 10.0)
         (def classVar (new C (b c)))
         (classVar --> x))
        """;
    String output = runProgram(input);
    assertEquals("5.0", output);
  }

  @Test
  public void test_Tests_copy_19_test_3() {
    String input = """
        ((module ModC
           (class C (x y)
             (method delta (x)
               (def z (this --> x))
               (def x (x + x))
               (z + x))))
         (import ModC)
         (def b 5.0)
         (def c 10.0)
         (def classVar (new C (b c)))
         (classVar isa C))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_19_test_4() {
    String input = """
        ((module ModC
           (class C (x y)
             (method delta (x)
               (def z (this --> x))
               (def x (x + x))
               (z + x))))
         (module ModB (class B (g h)))
         (import ModC)
         (def b 5.0)
         (def c 10.0)
         (def d 5.0)
         (def classVar (new C (b c)))
         (def classVarTwo (new C (d c)))
         (classVar == classVarTwo))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_19_test_5() {
    String input = """
        ((module ModC
           (class C (x y)
             (method delta (x)
               (def a 1000.0)
               (def b (this --> x))
               (this --> x = a)
               (b = (a + a))
               b)))
         (import ModC)
         (def b 5.0)
         (def c 10.0)
         (def classVar (new C (b c)))
         (def result (classVar --> delta (c)))
         result)
        """;
    String output = runProgram(input);
    assertEquals("2000.0", output);
  }

  @Test
  public void test_Tests_copy_19_test_6() {
    String input = """
        ((module ModC
           (class C (x y)
             (method delta (x)
               (def a 1000.0)
               (def b (this --> x))
               (this --> x = a)
               (b = (a + a))
               b)))
         (module ModB
           (import ModC)
           (class B (x y)
             (method delta (x)
               (def a -1.0)
               (def b 2.0)
               (def prevClass (new C (a b)))
               (a = (prevClass --> x))
               (a / x))))
         (import ModB)
         (def b 5.0)
         (def c 10.0)
         (def classVar (new B (b c)))
         (def result (classVar --> delta (c)))
         result)
        """;
    String output = runProgram(input);
    assertEquals("-0.1", output);
  }

  @Test
  public void test_Tests_copy_19_test_8() {
    String input = """
        ((module ModC
           (class C (x y)
             (method delta (x)
               (def a 1000.0)
               (def b (this --> x))
               (this --> x = a)
               (b = (a + a))
               b)))
         (module ModB
           (import ModC)
           (class B (x y)
             (method delta (x)
               (def a -1.0)
               (def b 2.0)
               (def prevClass (new C (a b)))
               (a = (prevClass --> x))
               (a / x))))
         (import ModB)
         (def b 5.0)
         (def c 10.0)
         (def classVar (new B (b c)))
         (def result (classVar --> h))
         result)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_19_test_9() {
    String input = """
        ((module ModC
           (class C (x y)
             (method delta (x)
               (def a 1000.0)
               (def b (this --> x))
               (this --> x = a)
               (b = (a + a))
               b)))
         (module ModB
           (import ModC)
           (class B (x y)
             (method delta (x)
               (def a -1.0)
               (def b 2.0)
               (def prevClass (new C (a b)))
               (a = (prevClass --> x))
               (a / x))))
         (import ModC)
         (import ModB)
         (def result -1.0)
         (def a 1.0)
         (def b 2.0)
         (def varOne (new C (a b)))
         (def varTwo (new B (a b)))
         (if0 (varOne == varTwo)
              (result = (a + b))
              (result = (a / b)))
         result)
        """;
    String output = runProgram(input);
    assertEquals("0.5", output);
  }

  @Test
  public void test_Tests_copy_20_test_0() {
    String input = """
        ((module ModPoint (class Point (x y)))
         (import ModPoint)
         (def x 1.0)
         (def point (new Point (x x)))
         (def answer (x + point))
         x)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_20_test_1() {
    String input = """
        ((module ModPoint (class Point (x y)))
         (import ModPoint)
         (def six 6.0)
         (def twentyTwoPointTwo 22.2)
         (def eight 8.0)
         (def pOne (new Point (six twentyTwoPointTwo)))
         (def pTwo (new Point (eight twentyTwoPointTwo)))
         (pOne == pTwo))
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_20_test_2() {
    String input = """
        ((module ModWheel (class Wheel (year diameter)))
         (module ModSeat (class Seat (width)))
         (module ModCar (import ModWheel) (import ModSeat) (class Car (w s)))
         (import ModCar)
         (import ModWheel)
         (import ModSeat)
         (def sixtyEight 68.0)
         (def twenty 20.0)
         (def smallWheel (new Wheel (twenty sixtyEight)))
         (def alsoSmallWheel (new Wheel (twenty sixtyEight)))
         (def seat (new Seat (sixtyEight)))
         (def carOne (new Car (smallWheel seat)))
         (def carTwo (new Car (alsoSmallWheel seat)))
         (carOne == carTwo))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_20_test_3() {
    String input = """
        ((module ModPoint (class Point (x y)))
         (import ModPoint)
         (def x 1.0)
         (def point (new Point (x x)))
         (def z (point --> z))
         z)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_20_test_4() {
    String input = """
        ((module ModPoint (class Point (x y)))
         (import ModPoint)
         (def x 1.0)
         (def point (new Point (x x)))
         (point --> z = 3.0)
         x)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_20_test_6() {
    String input = """
        ((module ModTimer
           (class Timer (time)
             (method getTime () (this --> time))))
         (import ModTimer)
         (def three 3.0)
         (def fourtyTwo 42.0)
         (def true 0.0)
         (while0 true
                 (block
                   (def c (new Timer (three)))
                   (fourtyTwo = c)
                   (true = 1.0)))
         (fourtyTwo --> getTime ()))
        """;
    String output = runProgram(input);
    assertEquals("3.0", output);
  }

  @Test
  public void test_Tests_copy_20_test_7() {
    String input = """
        ((module ModTimer
           (class Timer (time)
             (method getTime () (this --> time))))
         (import ModTimer)
         (def three 3.0)
         (def fourtyTwo 42.0)
         (def true 0.0)
         (while0 true
                 (block
                   (def c (new Timer (three)))
                   (fourtyTwo = c)
                   (true = 1.0)))
         (time --> getTime ()))
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_20_test_8() {
    String input = """
        ((module ModCowboy
           (class Cowboy ()
             (method draw ()
               (def output 3.0)
               output)))
         (module ModArtist
           (class Artist ()
             (method draw () 777.0)))
         (import ModArtist)
         (import ModCowboy)
         (def a (new Artist ()))
         (def c (new Cowboy ()))
         (def x -88.0)
         (if0 x
              (x = a)
              (x = c))
         (x --> draw ()))
        """;
    String output = runProgram(input);
    assertEquals("3.0", output);
  }

  @Test
  public void test_Tests_copy_20_test_9() {
    String input = """
        ((module ModPoint (class Point (x y)))
         (import ModPoint)
         (def six 6.0)
         (def twentyTwoPointTwo 22.2)
         (def eight 8.0)
         (def pOne (new Point (new twentyTwoPointTwo)))
         (def pTwo (new Point (eight twentyTwoPointTwo)))
         (pOne == pTwo))
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_21_test_0() {
    String input = """
        ((module ModC
           (class C ()
             (method id ()
               (def x 3.0)
               x)))
         (import ModC)
         (def x 100.0)
         (def c (new C ()))
         (c --> id ()))
        """;
    String output = runProgram(input);
    assertEquals("3.0", output);
  }

  @Test
  public void test_Tests_copy_21_test_1() {
    String input = """
        ((module ModA
           (class A (x)
             (method getx () (this --> x))))
         (import ModA)
         (def x 5.0)
         (def a (new A (x)))
         (a --> getx ()))
        """;
    String output = runProgram(input);
    assertEquals("5.0", output);
  }

  @Test
  public void test_Tests_copy_21_test_2() {
    String input = """
        ((module ModPoint
           (class point (x y)
             (method bumpswap (t)
               (def oldx (this --> x))
               (def oldy (this --> y))
               (def tmp oldx)
               (if0 t
                    (block
                      (this --> x = oldy)
                      (this --> y = tmp))
                    (block
                      (this --> x = oldx)))
               (while0 t
                       (block
                         (this --> x = (this --> x))
                         (t = 1.0)))
               (oldx + oldy))))
         (import ModPoint)
         (def one 1.0)
         (def zero 0.0)
         (def x 3.0)
         (def y 4.0)
         (def p (new point (x y)))
         (def res (p --> bumpswap (zero)))
         (res + one))
        """;
    String output = runProgram(input);
    assertEquals("8.0", output);
  }

  @Test
  public void test_Tests_copy_21_test_3() {
    String input = """
        ((module ModVec
           (class vec (x y)
             (method sum ()
               (def a (this --> x))
               (def b (this --> y))
               (a + b))))
         (module ModAcc
           (import ModVec)
           (class acc (val)
             (method addfromvec (vec inc)
               (def s (vec --> sum ()))
               (def base (this --> val))
               (if0 inc
                    (block
                      (this --> val = (base + s)))
                    (block
                      (this --> val = base)))
               (while0 inc
                       (block
                         (this --> val = (this --> val))
                         (inc = 1.0)))
               (this --> val))))
         (import ModVec)
         (import ModAcc)
         (def zero 0.0)
         (def one 1.0)
         (def vx 5.0)
         (def vy 6.0)
         (def acczero 0.0)
         (def v (new vec (vx vy)))
         (def a (new acc (acczero)))
         (a --> val = (a --> addfromvec (v zero)))
         (a --> val))
        """;
    String output = runProgram(input);
    assertEquals("11.0", output);
  }

  @Test
  public void test_Tests_copy_21_test_4() {
    String input = """
        ((module ModFoo
           (class foo (a b)
             (method mix (t u)
               (def x (this --> a))
               (def y (this --> b))
               (def sum (x + y))
               (if0 t
                    (block
                      (this --> a = (sum + u)))
                    (block
                      (this --> b = (sum + u))))
               (while0 t
                       (block
                         (t = u)))
               (this --> a))))
         (module ModBar
           (import ModFoo)
           (class bar (f)
             (method combine (p tt uu)
               (def tmp (p --> mix (tt uu)))
               (def pa (p --> a))
               (def fv (this --> f))
               (pa + fv))))
         (import ModFoo)
         (import ModBar)
         (def zero 0.0)
         (def one 1.0)
         (def two 2.0)
         (def three 3.0)
         (def four 4.0)
         (def five 5.0)
         (def p (new foo (two three)))
         (def b (new bar (five)))
         (b --> combine (p zero four)))
        """;
    String output = runProgram(input);
    assertEquals("14.0", output);
  }

  @Test
  public void test_Tests_copy_21_test_5() {
    String input = """
        ((module ModP
           (class p (x y)
             (method sumaftermut (z)
               (def ylocal (this --> y))
               (def xlocal z)
               (this --> y = xlocal)
               (xlocal = 1.0)
               (if0 z
                    (block
                      (this --> x = ylocal))
                    (block
                      (this --> x = xlocal)))
               (while0 z
                       (block
                         (z = 1.0)))
               (xlocal + ylocal))))
         (import ModP)
         (def zzero 0.0)
         (def x 10.0)
         (def y 7.0)
         (def pt (new p (x y)))
         (def res (pt --> sumaftermut (zzero)))
         (def sx (pt --> x))
         (res + sx))
        """;
    String output = runProgram(input);
    assertEquals("15.0", output);
  }

  @Test
  public void test_Tests_copy_21_test_6() {
    String input = """
        ((module ModA (class A (x)))
         (import ModA)
         (def x 1.0)
         (new A (x)))
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_21_test_7() {
    String input = """
        ((module ModNode
           (class Node (next)
             (method mkself ()
               (this --> next = this)
               0.0)))
         (import ModNode)
         (def z 0.0)
         (def a (new Node (z)))
         (def b (new Node (z)))
         (a --> next = a)
         (b --> next = b)
         (a == b))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_21_test_8() {
    String input = """
        ((module ModAccum
           (class Accum (sum)
             (method add (inc)
               (def old (this --> sum))
               (this --> sum = (old + inc))
               (this --> sum))
             (method get ()
               (def tmp (this --> sum))
               tmp)))
         (module ModStepper
           (class Stepper (count)
             (method down (dec)
               (def cur (this --> count))
               (this --> count = (cur + dec))
               (this --> count))))
         (module ModRunner
           (import ModAccum)
           (import ModStepper)
           (class Runner (acc step cap)
             (method loopAdd (unit)
               (def acc (this --> acc))
               (def step (this --> step))
               (def cap (this --> cap))
               (def zero 0.0)
               (def two 2.0)
               (def dec -1.0)
               (def t 0.0)
               (def tmp 0.0)
               (while0 t
                       (block
                         (def iszero (cap == zero))
                         (if0 iszero
                              (block
                                (t = 1.0))
                              (block
                                (def flag (cap == two))
                                (if0 flag
                                     (block
                                       (tmp = (acc --> add (unit)))
                                       (tmp = (acc --> add (unit))))
                                     (block
                                       (tmp = (acc --> add (unit)))))
                                (cap = (step --> down (dec)))))))
               (acc --> get ()))))
         (import ModAccum)
         (import ModStepper)
         (import ModRunner)
         (def start 0.0)
         (def cap 4.0)
         (def acc (new Accum (start)))
         (def step (new Stepper (cap)))
         (def run (new Runner (acc step cap)))
         (def unit 2.0)
         (def res (run --> loopAdd (unit)))
         res)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void test_Tests_copy_21_test_9() {
    String input = """
        ((module ModBox
           (class Box (x)
             (method swapAdd (y)
               (def tmp (this --> x))
               (this --> x = y)
               (tmp + y))))
         (import ModBox)
         (def x 2.0)
         (def box (new Box (x)))
         (def twin box)
         (def inc 3.0)
         (def s (box --> swapAdd (inc)))
         (def out (twin --> x))
         (def ok (out == inc))
         (if0 ok
              (block
                (s = (s + out)))
              (block
                (s = 999.0)))
         s)
        """;
    String output = runProgram(input);
    assertEquals("8.0", output);
  }

  @Test
  public void test_Tests_copy_22_test_1() {
    String input = """
        ((def x 1.0)
         (x + 1.0))
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_22_test_3() {
    String input = """
        ((module ModC
           (class C (x)
             (method m (a a) a)))
         0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_22_test_4() {
    String input = """
        ((def x 1.0)
         (x + z))
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_22_test_5() {
    String input = """
        ((module ModC
           (class C ()
             (method id () 0.0)))
         (import ModC)
         (def o (new C ()))
         (o isa D))
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_22_test_6() {
    String input = """
        ((module ModC
           (class C (x)
             (method get () (this --> x))))
         (import ModC)
         (def x 1.0)
         (new C ()))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_22_test_7() {
    String input = """
        ((module ModBox
           (class Box (v)
             (method get () (this --> v))))
         (import ModBox)
         (def v 7.0)
         (def o (new Box (v)))
         (o --> v = 9.0)
         (o --> v))
        """;
    String output = runProgram(input);
    assertEquals("9.0", output);
  }

  @Test
  public void test_Tests_copy_22_test_8() {
    String input = """
        ((module ModAdder
           (class Adder (x y)
             (method sum ()
               (def a (this --> x))
               (def b (this --> y))
               (a + b))))
         (import ModAdder)
         (def x 1.5)
         (def y 2.5)
         (def a (new Adder (x y)))
         (a --> sum ()))
        """;
    String output = runProgram(input);
    assertEquals("4.0", output);
  }

  @Test
  public void test_Tests_copy_22_test_9() {
    String input = """
        ((def t 5.0)
         (t == t))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_23_test_0() {
    String input = """
        ((module ModC (class C (f)))
         (import ModC)
         (def a 1.0)
         (def o (new C (a)))
         (o --> g = a)
         a)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_23_test_1() {
    String input = """
        ((module ModC
           (class C ()
             (method a (x) x)))
         (import ModC)
         (def o (new C ()))
         (def one 1.0)
         (def two 2.0)
         (o --> a (one two)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_23_test_2() {
    String input = """
        ((module ModCC
           (class cc (f)
             (method m (p)
               (def newBox (new cc (p)))
               42.0)))
         (module ModC
           (import ModCC)
           (class C (f)
             (method m (p) 42.0)))
         (import ModC)
         (def one 1.0)
         (def instance (new C (one)))
         (def field (instance --> f))
         (instance --> f = (instance --> m (one)))
         (one = (instance --> f))
         one)
        """;
    String output = runProgram(input);
    assertEquals("42.0", output);
  }

  @Test
  public void test_Tests_copy_23_test_3() {
    String input = """
        ((module ModPoint (class Point (x y)))
         (import ModPoint)
         (def startx 0.0)
         (def starty 0.0)
         (def Point (new Point (startx starty)))
         (Point --> y))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_23_test_4() {
    String input = """
        ((module ModC
           (class C (f)
             (method a (x)
               (this --> f = x)
               (x + x))))
         (import ModC)
         (def x 1.0)
         (def two 2.0)
         (def c (new C (x)))
         (def y (c --> a (two)))
         x)
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_23_test_5() {
    String input = """
        ((def n 1.0)
         (n --> f))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_23_test_6() {
    String input = """
        ((module ModC (class C (f)))
         (import ModC)
         (def one 1.0)
         (def two 2.0)
         (def c (new C (one)))
         (c --> f = two)
         (c --> f))
        """;
    String output = runProgram(input);
    assertEquals("2.0", output);
  }

  @Test
  public void test_Tests_copy_23_test_7() {
    String input = """
        ((module ModC (class C (f)))
         (import ModC)
         (def a 1.0)
         (def b 2.0)
         (new C (a b)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }


  @Test
  public void test_Tests_copy_23_test_8() {
    String input = """
        ((module ModC (class C (f)))
         (import ModC)
         (def one 1.0)
         (def c (new C (one)))
         (c isa C))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_23_test_9() {
    String input = """
        ((module ModC
           (class C (f)
             (method m () (this --> f))))
         (import ModC)
         (def one 1.0)
         (def six 666.0)
         (if0 one
              (one = one)
              (block
                (def two 2.0)
                (def oneC (new C (two)))
                (one = one)))
         six)
        """;
    String output = runProgram(input);
    assertEquals("666.0", output);
  }

  @Test
  public void test_Tests_copy_24_test_0() {
    String input = """
        ((module ModC
           (class C (v)
             (method checkSelf ()
               (if0 this
                    (this --> v = 1.0)
                    (this --> v = 88.0))
               0.0)))
         (import ModC)
         (def zero 0.0)
         (def c (new C (zero)))
         (def garbage (c --> checkSelf ()))
         (c --> v))
        """;
    String output = runProgram(input);
    assertEquals("88.0", output);
  }

  @Test
  public void test_Tests_copy_24_test_1() {
    String input = """
        ((module ModC
           (class C (x)
             (method loop (y)
               (while0 (this --> x)
                       (block
                         (def x 50.0)
                         (y = (y + x))
                         (this --> x = 1.0)))
               (this --> x))))
         (import ModC)
         (def zero 0.0)
         (def c (new C (zero)))
         (c --> loop (zero)))
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_24_test_3() {
    String input = """
        ((module ModC
           (class C (x)
             (method add (x)
               (def f (this --> x))
               (x + f))))
         (import ModC)
         (def ten 10.0)
         (def five 5.0)
         (def c (new C (ten)))
         (c --> add (five)))
        """;
    String output = runProgram(input);
    assertEquals("15.0", output);
  }

  @Test
  public void test_Tests_copy_24_test_4() {
    String input = """
        ((module ModC
           (class C (v)
             (method getV () 3.0)
             (method getVPlusOne ()
               (this --> getV ()))))
         (import ModC)
         (def ten 10.0)
         (def c (new C (ten)))
         (c --> getVPlusOne ()))
        """;
    String output = runProgram(input);
    assertEquals("3.0", output);
  }

  @Test
  public void test_Tests_copy_24_test_5() {
    String input = """
        ((module ModC
           (class C (x)
             (method add (x)
               (def fieldx (this --> x))
               (x + fieldx))))
         (import ModC)
         (def ten 10.0)
         (def five 5.0)
         (def c (new C (ten)))
         (c --> add (five)))
        """;
    String output = runProgram(input);
    assertEquals("15.0", output);
  }

  @Test
  public void test_Tests_copy_24_test_7() {
    String input = """
        ((module ModC
           (class C ()
             (method self () this)))
         (import ModC)
         (def c (new C ()))
         (c --> self ()))
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_24_test_9() {
    String input = """
        ((module ModAdder
           (class Adder ()
             (method add (x y z)
               (def sum (x + y))
               (sum + z))))
         (import ModAdder)
         (def a (new Adder ()))
         (def two 2.0)
         (a --> add (two two two)))
        """;
    String output = runProgram(input);
    assertEquals("6.0", output);
  }

  @Test
  public void test_Tests_copy_25_test_0() {
    String input = """
        ((module ModCounter
           (class Counter (count)
             (method get ()
               (this --> count))))
         (module ModFactory
           (import ModCounter)
           (class Factory (initialValue)
             (method create ()
               (def val (this --> initialValue))
               (new Counter (val)))))
         (import ModFactory)
         (def zero 0.0)
         (def f (new Factory (zero)))
         (f --> create ()))
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_25_test_1() {
    String input = """
        ((module ModCounter
           (class Counter (value)
             (method getDouble ()
               (def v (this --> value))
               (v = (v + v))
               v)))
         (import ModCounter)
         (def five 5.0)
         (def ten 10.0)
         (def c (new Counter (five)))
         (c --> value = ten)
         (c --> value))
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void test_Tests_copy_25_test_3() {
    String input = """
        ((module ModDog (class Dog ()))
         (module ModCat (class Cat ()))
         (import ModDog)
         (import ModCat)
         (def d (new Dog ()))
         (def result 0.0)
         (if0 (d isa Cat)
              (block
                (result = 100.0))
              (block
                (result = 200.0)))
         result)
        """;
    String output = runProgram(input);
    assertEquals("200.0", output);
  }

  @Test
  public void test_Tests_copy_25_test_4() {
    String input = """
        ((module ModFoo
           (class Foo ()
             (method test ()
               (def x 10.0)
               x)))
         (import ModFoo)
         (def x 1.0)
         (def obj (new Foo ()))
         (def result (obj --> test ()))
         x)
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_25_test_5() {
    String input = """
        ((module ModCounter
           (class Counter (val)
             (method getValue ()
               (this --> val))
             (method doubleValue ()
               (def a 0.0)
               (def b 0.0)
               (a = (this --> getValue ()))
               (b = (this --> getValue ()))
               (a + b))))
         (import ModCounter)
         (def seven 7.0)
         (def c (new Counter (seven)))
         (c --> doubleValue ()))
        """;
    String output = runProgram(input);
    assertEquals("14.0", output);
  }

  @Test
  public void test_Tests_copy_25_test_6() {
    String input = """
        ((module ModPoint
           (class Point (x y)
             (method getX () (this --> x))))
         (import ModPoint)
         (def one 1.0)
         (def two 2.0)
         (def p (new Point (one two)))
         (p = one)
         (p --> getX ()))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_25_test_7() {
    String input = """
        ((module ModAdder
           (class Adder ()
             (method add (a b c) (a + b))))
         (import ModAdder)
         (def obj (new Adder ()))
         (def x 1.0)
         (def y 2.0)
         (obj --> add (x y)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_25_test_8() {
    String input = """
        ((module ModTriple (class Triple (a b c)))
         (import ModTriple)
         (def x 1.0)
         (def y 2.0)
         (new Triple (x y)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_26_test_0() {
    String input = """
        ((module ModVec
           (class Vec (x y z)
             (method shiftByAmounts (x y z)
               (def curx (this --> x))
               (def cury (this --> y))
               (def curz (this --> z))
               (this --> x = (curx + x))
               (this --> y = (cury + y))
               (this --> z = (curz + z))
               this)
             (method shiftByVec (vec)
               (def shiftx (vec --> x))
               (def shifty (vec --> y))
               (def shiftz (vec --> z))
               (this --> shiftByAmounts (shiftx shifty shiftz)))
             (method scaleByAmount (s)
               (if0 s
                    (block
                      (this --> x = 0.0)
                      (this --> y = 0.0)
                      (this --> z = 0.0))
                    (block
                      (def one 1.0)
                      (def inverted (one / s))
                      (def curx (this --> x))
                      (def cury (this --> y))
                      (def curz (this --> z))
                      (this --> x = (curx / inverted))
                      (this --> y = (cury / inverted))
                      (this --> z = (curz / inverted))))
               this)
             (method flip ()
               (def negOne -1.0)
               (this --> scaleByAmount (negOne)))
             (method zero ()
               (def zero 0.0)
               (this --> scaleByAmount (zero)))))
         (import ModVec)
         (def x 1.5)
         (def y 2.0)
         (def z 2.5)
         (def u (new Vec (x y z)))
         (def v (new Vec (x x x)))
         (def temp (u --> shiftByVec (v)))
         (def temp (u --> flip ()))
         (u --> y))
        """;
    String output = runProgram(input);
    assertEquals("-3.5", output);
  }

  @Test
  public void test_Tests_copy_26_test_1() {
    String input = """
        ((module ModConsList
           (class ConsList (first rest)
             (method isEmpty () 1.0)
             (method addToFront (x)
               (new ConsList (x this)))
             (method contains (x)
               (def result 0.5)
               (def firstElem (this --> first))
               (def restOfList (this --> rest))
               (def zero 0.0)
               (if0 (firstElem == x)
                    (result = zero)
                    (result = (restOfList --> contains (x))))
               result)
             (method length ()
               (def one 1.0)
               (def restOfList (this --> rest))
               (def result (restOfList --> length ()))
               (result + one))
             (method get (index)
               (def result 0.5)
               (def firstElem (this --> first))
               (def restOfList (this --> rest))
               (if0 index
                    (result = firstElem)
                    (block
                      (def negOne -1.0)
                      (def nextIndex (index + negOne))
                      (result = (restOfList --> get (nextIndex)))))
               result)))
         (module ModEmpty
           (import ModConsList)
           (class Empty ()
             (method addToFront (x)
               (new ConsList (x this)))
             (method isEmpty () 0.0)
             (method contains (x) 1.0)
             (method length () 0.0)
             (method get (index) -1.0)))
         (import ModEmpty)
         (import ModConsList)
         (def triNumberList (new Empty ()))
         (def item 0.0)
         (def numItems 10.0)
         (def keepLooping 0.0)
         (def i 1.0)
         (def one 1.0)
         (def fifteen 15.0)
         (def result 0.5)
         (def failure -732.3)
         (while0 keepLooping
                 (block
                   (triNumberList = (triNumberList --> addToFront (item)))
                   (item = (item + i))
                   (if0 (i == numItems)
                        (keepLooping = one)
                        (i = (i + one)))))
         (if0 (triNumberList --> contains (fifteen))
              (block
                (def two 2.0)
                (def twothElem (triNumberList --> get (two)))
                (result = (triNumberList --> length ()))
                (result = (result + twothElem)))
              (result = failure))
         result)
        """;
    String output = runProgram(input);
    assertEquals("38.0", output);
  }

  @Test
  public void test_Tests_copy_26_test_2() {
    String input = """
        ((module ModA
           (class A (x)
             (method f (n)
               (def result 0.5)
               (def thisx (this --> x))
               (if0 thisx
                    (result = n)
                    (block
                      (def negOne -1.0)
                      (def one 1.0)
                      (this --> x = (thisx + negOne))
                      (n = (n + one))
                      (result = (this --> f (n)))))
               result)))
         (import ModA)
         (def num 100.0)
         (def zero 0.0)
         (def a (new A (num)))
         (a --> f (zero)))
        """;
    String output = runProgram(input);
    assertEquals("100.0", output);
  }

  @Test
  public void test_Tests_copy_26_test_3() {
    String input = """
        ((module ModA (class A (x)))
         (module ModB (class B (x)))
         (import ModA)
         (import ModB)
         (def temp 0.0)
         (def templol 0.0)
         (def a (new A (temp)))
         (def b (new B (temp)))
         (def otherA (new A (temp)))
         (def otherB (new B (temp)))
         (def result 0.0)
         (a --> x = b)
         (b --> x = a)
         (otherA --> x = otherB)
         (templol = (new B (otherA)))
         (temp = (new A (templol)))
         (otherB --> x = temp)
         (if0 (a == otherA)
              (result = 0.0)
              (result = 1.0))
         result)
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_26_test_4() {
    String input = """
        ((module ModA
           (class A (x)
             (method f (a xnew)
               (a --> x = xnew)
               0.0)))
         (import ModA)
         (def var 0.0)
         (def a (new A (var)))
         (def other (new A (var)))
         (def temp 0.0)
         (var = 1.5)
         (temp = (a --> f (other var)))
         (other --> x))
        """;
    String output = runProgram(input);
    assertEquals("1.5", output);
  }

  @Test
  public void test_Tests_copy_26_test_5() {
    String input = """
        ((module ModCounter
           (class Counter (x)
             (method incr ()
               (def one 1.0)
               (def thisx (this --> x))
               (this --> x = (thisx + one))
               thisx)))
         (import ModCounter)
         (def zero 0.0)
         (def cnt (new Counter (zero)))
         (def temp 0.0)
         (temp = (cnt --> incr ()))
         (temp = (cnt --> incr ()))
         (temp = (cnt --> incr ()))
         temp)
        """;
    String output = runProgram(input);
    assertEquals("2.0", output);
  }

  @Test
  public void test_Tests_copy_26_test_6() {
    String input = """
        ((module ModA (class A ()))
         (import ModA)
         (def a (new A ()))
         (def result 0.0)
         (if0 a
              (result = 0.1)
              (result = 0.2))
         result)
        """;
    String output = runProgram(input);
    assertEquals("0.2", output);
  }

  @Test
  public void test_Tests_copy_26_test_9() {
    String input = """
        ((module ModA
           (class A ()
             (method f (x) 4.0)))
         (import ModA)
         (def a (new A ()))
         (a --> f (a a)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_27_test_0() {
    String input = """
        ((module ModCounter
           (class counter (value)
             (method reset ()
               (def zero 0.0)
               (def value zero)
               value)
             (method inc (amount)
               (def temp (amount + amount))
               (this --> value = temp)
               (this --> value))))
         (import ModCounter)
         (def five 5.0)
         (def c (new counter (five)))
         (if0 (c --> value)
              (c --> value = 1.0)
              (block
                (while0 (c --> value)
                        (block
                          (c --> value = (c isa counter))))))
         (c --> value))
        """;
    String output = runProgram(input);
    assertEquals("5.0", output);
  }

  @Test
  public void test_Tests_copy_27_test_1() {
    String input = """
        ((module ModJob
           (class Job (age)
             (method salary ()
               (def pay (this --> age))
               (def two 2.0)
               (pay = (pay / two))
               pay)))
         (import ModJob)
         (def fifty 50.0)
         (def newAge fifty)
         (def newestAge newAge)
         (def zero 0.0)
         (def Bob (new Job (fifty)))
         (def Janet (new Job (newestAge)))
         (Bob == Janet))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_27_test_2() {
    String input = """
        ((module ModJob
           (class Job (age)
             (method salary ()
               (def pay (this --> age))
               (def two 2.0)
               (pay = (pay / two))
               pay)))
         (import ModJob)
         (def fifty 50.0)
         (def newAge fifty)
         (def newestAge newAge)
         (def Bob (new Job (fifty)))
         (def Janet (new Job (newestAge)))
         (Bob / Janet))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_27_test_3() {
    String input = """
        ((module ModPerson
           (class Person (age)
             (method birthday ()
               (def one 1.0)
               (this --> age = (one + one))
               (this --> age))))
         (import ModPerson)
         (def twenty 20.0)
         (def sophia (new Person (twenty)))
         (def chris sophia)
         (chris --> birthday ()))
        """;
    String output = runProgram(input);
    assertEquals("2.0", output);
  }

  @Test
  public void test_Tests_copy_27_test_4() {
    String input = """
        ((module ModPerson
           (class Person (age)
             (method birthday ()
               (def one 1.0)
               (this --> age = (one + one))
               (this --> age))))
         (import ModPerson)
         (def twenty 20.0)
         (def thirty 30.0)
         (def sophia (new Person (twenty)))
         (sophia --> birthday (thirty)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_27_test_5() {
    String input = """
        ((module ModDog
           (class Dog (speed weight)
             (method setStats ()
               (def s (this --> speed))
               (def w (this --> weight))
               s)))
         (module ModBird
           (class Bird (speed weight)
             (method setStats ()
               (def s (this --> speed))
               (def w (this --> weight))
               s)))
         (import ModDog)
         (import ModBird)
         (def ten 10.0)
         (def twenty 29.0)
         (def myDog (new Dog (ten twenty)))
         (def myBird (new Bird (ten twenty)))
         (def hello 10.0)
         (myDog isa Bird))
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_27_test_6() {
    String input = """
        ((module ModPerson
           (class Person (age)
             (method birthday ()
               (def one 1.0)
               (this --> age = (one + one))
               (this --> age))))
         (import ModPerson)
         (def twenty 20.0)
         (def thirty twenty)
         (def sophia (new Person (twenty)))
         (def james sophia)
         (sophia --> birthday ()))
        """;
    String output = runProgram(input);
    assertEquals("2.0", output);
  }

  @Test
  public void test_Tests_copy_27_test_7() {
    String input = """
        ((module ModA
           (class A (fOne fTwo)
             (method mOne (pOne)
               (def one 1.0)
               one)))
         (import ModA)
         (def ten 10.0)
         (def twenty 20.0)
         (def firstExample (new A (ten twenty)))
         firstExample)
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_27_test_8() {
    String input = """
        ((module ModPerson
           (class Person (age)
             (method age ()
               (def thirty 30.0)
               (this --> age = thirty)
               (this --> age))))
         (import ModPerson)
         (def twenty 20.0)
         (def curry (new Person (twenty)))
         (def ans 1.0)
         (if0 ans
              (block
                (def five 5.0)
                (def result (twenty + five))
                (ans = result))
              (block
                (def result (curry --> age ()))
                (ans = result)))
         ans)
        """;
    String output = runProgram(input);
    assertEquals("30.0", output);
  }

  @Test
  public void test_Tests_copy_27_test_9() {
    String input = """
        ((module ModA
           (class A (fOne fTwo)
             (method mOne ()
               (def x 1.0)
               (def pOne 4.0)
               (x = (this --> fOne))
               (this --> fTwo = pOne)
               (this --> fOne))))
         (module ModB
           (class B (fThree)
             (method mTwo ()
               (def two 2.0)
               (def pTwo 3.0)
               (def y two)
               (this --> fThree = pTwo)
               (this --> fThree))))
         (module ModC
           (import ModA)
           (class C (fThree)
             (method mTwo ()
               (def ten 10.0)
               (def pTwo 3.0)
               (def y (new A (ten ten)))
               (this --> fThree = pTwo)
               (this --> fThree))))
         (import ModA)
         (def x 100.0)
         (def ten 10.0)
         (def y (new A (ten x)))
         (def z 0.0)
         (y isa A))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_2_test_0() {
    String input = """
        ((def x 1.0)
         (def y 2.0)
         (x = 3.0)
         (module ModMTClass (class MTClass ()))
         (def z = 4.0)
         z)
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_2_test_4() {
    String input = """
        ((module ModGoodClass (class goodClass ()))
         (def x 1.0)
         (x = 2.0)
         x)
        """;
    String output = runProgram(input);
    assertEquals("2.0", output);
  }

  @Test
  public void test_Tests_copy_2_test_5() {
    String input = """
        ((module ModGoodClass
           (class goodClass (fieldOne)
             (method goodMethod (paramOne paramTwo)
               (def x 1.0)
               (x = 2.0)
               x)))
         (import ModGoodClass)
         (def z 3.0)
         (def y (new goodClass (z)))
         (def field (y --> fieldOne))
         (y --> fieldOne = (y --> goodMethod (z z)))
         (new goodClass (field)))
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_2_test_6() {
    String input = """
        ((module ModThis (class this ()))
         (import ModThis)
         (def this 10.0)
         (new this (this)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_2_test_7() {
    String input = """
        ((module ModThis
           (class this (this)
             (method this () 1.0)))
         (import ModThis)
         (def that 1.0)
         (def this (new this (that)))
         (def thisOfThis this)
         (thisOfThis isa this))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_2_test_8() {
    String input = """
        ((module ModClassA (class classA (fieldOne)))
         (import ModClassA)
         (def field 1.0)
         (def a (new classA (field)))
         (a --> this))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_2_test_9() {
    String input = """
        ((module ModClassA
           (class classA (fieldOne)
             (method mOne () this)))
         (import ModClassA)
         (def aField 0.0)
         (def aInstance (new classA (aField)))
         (aInstance --> mOne ()))
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_3_test_0() {
    String input = """
        ((module ModPoint
           (class Point (x y)
             (method delta (x)
               (def y (this --> y))
               (x = 1.0)
               (x + y))))
         (import ModPoint)
         (def x 1.0)
         (def point (new Point (x x)))
         (point --> x = x)
         (x = (point --> delta (x)))
         x)
        """;
    String output = runProgram(input);
    assertEquals("2.0", output);
  }

  @Test
  public void test_Tests_copy_3_test_5() {
    String input = """
        ((module ModBox (class Box (v)))
         (import ModBox)
         (def x 7.0)
         (new Box (x)))
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_3_test_6() {
    String input = """
        ((def x 0.0)
         (if0 x
              (block
                (def y 5.0)
                (x = y))
              (block
                (def y 9.0)
                (x = y)))
         x)
        """;
    String output = runProgram(input);
    assertEquals("5.0", output);
  }

  @Test
  public void test_Tests_copy_3_test_7() {
    String input = """
        ((def x) 0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_4_test_0() {
    String input = """
        ((module ModC (class c (f)))
         (import ModC)
         (def x 1.0)
         (def y 1.0)
         (def obj (new c (x)))
         (def objtwo (new c (y)))
         (obj == objtwo))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_4_test_1() {
    String input = """
        ((module ModC (class c (f)))
         (import ModC)
         (def x 1.0)
         (def y 2.0)
         (def obj (new c (x)))
         (def objtwo (new c (y)))
         (obj == objtwo))
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }


  @Test
  public void test_Tests_copy_4_test_3() {
    String input = """
        ((module ModPoint
           (class Point (x y)
             (method delta (x)
               (def y 1.0)
               (x = 1.0)
               (x + y))))
         (import ModPoint)
         (def x 1.0)
         (def point (new Point (x x)))
         (point --> x = x)
         (x = (point --> delta (x)))
         x)
        """;
    String output = runProgram(input);
    assertEquals("2.0", output);
  }

  @Test
  public void test_Tests_copy_4_test_4() {
    String input = """
        ((module ModPoint
           (class Point (x y)
             (method set (dx dy)
               (def a (this --> x))
               (def b (this --> y))
               (this --> x = dx)
               (this --> y = dy)
               (a + b))
             (method addX (v)
               (def old (this --> x))
               (this --> x = (old + v))
               (this --> x))))
         (module ModPair
           (class Pair (first second)
             (method equalify ()
               (def x (this --> first))
               (def y (this --> second))
               (def t (x == y))
               t)))
         (import ModPoint)
         (import ModPair)
         (def nten 10.0)
         (def ntwenty 20.0)
         (def nthree 3.0)
         (def nfour 4.0)
         (def nfive 5.0)
         (def nsix 6.0)
         (def nzero 0.0)
         (def none 1.0)
         (def p (new Point (nten ntwenty)))
         (def q (new Point (nfive nthree)))
         (def r 0.0)
         (def flag 0.0)
         (def tmp 0.0)
         (def pair (new Pair (nfive nsix)))
         (def ax 0.0)
         (def bx 0.0)
         (r = (p --> set (nthree nfour)))
         (tmp = (q --> addX (nfive)))
         (p --> x = nten)
         (while0 flag
                 (block
                   (def inner 0.0)
                   (flag = none)
                   (tmp = (p --> set (nten ntwenty)))))
         (if0 (pair --> equalify ())
              (pair --> first = nzero)
              (pair --> second = none))
         (ax = (p --> x))
         (bx = (q --> x))
         (ax + bx))
        """;
    String output = runProgram(input);
    assertEquals("20.0", output);
  }

  @Test
  public void test_Tests_copy_4_test_5() {
    String input = """
        ((module ModPoint
           (class Point (x y)
             (method set (dx dy)
               (def a (this --> x))
               (def b (this --> y))
               (this --> x = dx)
               (this --> y = dy)
               (a + b))
             (method addX (v)
               (def old (this --> x))
               (this --> x = (old + v))
               (this --> x))))
         (module ModPair
           (class Pair (first second)
             (method equalify ()
               (def x (this --> first))
               (def y (this --> second))
               (def t (x == y))
               t)))
         (import ModPoint)
         (import ModPair)
         (def nten 10.0)
         (def ntwenty 20.0)
         (def nthree 3.0)
         (def nfour 4.0)
         (def nfive 5.0)
         (def nsix 6.0)
         (def nzero 0.0)
         (def none 1.0)
         (def p (new Point (nten ntwenty)))
         (def q (new Point (nfive nthree)))
         (def r 0.0)
         (def flag 0.0)
         (def tmp 0.0)
         (def pair (new Pair (nfive nsix)))
         (def ax 0.0)
         (def bx 0.0)
         (r = (p --> set (nthree nfour)))
         (tmp = (q --> addX (nfive)))
         (p --> x = nten)
         (while0 flag
                 (block
                   (def inner 0.0)
                   (flag = none)
                   (tmp = (p --> set (nten ntwenty)))))
         (if0 (pair --> equalify ())
              (pair --> first = nzero)
              (pair --> second = none))
         (ax = (p --> x))
         (bx = (q --> x))
         (new Pair (ax bx)))
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_4_test_6() {
    String input = """
        ((module ModC (class c ()))
         (import ModC)
         (def x 1.0)
         (def y 2.0)
         (def z (new c (x y)))
         0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_4_test_7() {
    String input = """
        ((def x 1.0)
         (def y (x --> x))
         0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_4_test_8() {
    String input = """
        ((module ModC (class c (f)))
         (import ModC)
         (def x 1.0)
         (def y x)
         (def g (new c (x)))
         (g --> h = y)
         0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_4_test_9() {
    String input = """
        ((module ModBox
           (class Box (val)
             (method set (x)
               (this --> val = x)
               0.0)
             (method get () (this --> val))))
         (import ModBox)
         (def one 1.0)
         (def fourtytwo 42.0)
         (def b (new Box (one)))
         (def x (b --> set (fourtytwo)))
         (b --> get ()))
        """;
    String output = runProgram(input);
    assertEquals("42.0", output);
  }

  @Test
  public void test_Tests_copy_5_test_1() {
    String input = """
        ((class) 1.0)
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_5_test_3() {
    String input = """
        ((module ModPoint (class Point (x x))) 1.0)
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_5_test_4() {
    String input = """
        ((module ModCounter
           (class Counter (count)
             (method count () 1.0)))
         1.0)
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_5_test_5() {
    String input = """
        (undeclared)
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_5_test_6() {
    String input = """
        ((new UndefinedClass ()))
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_5_test_7() {
    String input = """
        ((module ModEmpty (class Empty ())) 1.0)
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_5_test_8() {
    String input = """
        ((module ModPoint
           (class Point (x y)
             (method getX () (this --> x))))
         1.0)
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_5_test_9() {
    String input = """
        ((def x 5.0) x)
        """;
    String output = runProgram(input);
    assertEquals("5.0", output);
  }


  @Test
  public void test_Tests_copy_6_test_0_original() {
    String input = """
        ((module ModX
           (class X (a b c d e)
             (method a (b c d)
               (def a 0.0)
               (a = 1.0)
               a)
             (method b (b c d)
               (def b 0.0)
               (b = 1.0)
               b)))
         (module ModB (class b (a b c d e)))
         (def a 1.0)
         (def b 2.0)
         (def Var (new X (a a a a a)))
         (a = b)
         (b = a)
         (Var --> d = (new X (a a a a a)))
         (a = (Var --> c))
         (a = (Var --> a (a b a)))
         (Var isa X))
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }
  @Test
  public void test_Tests_copy_6_test_0() {
    String input = """
        ((module ModX
           (class X (a b c d e)
             (method a (b c d)
               (def a 0.0)
               (a = 1.0)
               a)
             (method b (b c d)
               (def b 0.0)
               (b = 1.0)
               b)))
         (module ModB (class b (a b c d e)))
         (import ModX)
         (def a 1.0)
         (def b 2.0)
         (def Var (new X (a a a a a)))
         (a = b)
         (b = a)
         (Var --> d = (new X (a a a a a)))
         (a = (Var --> c))
         (a = (Var --> a (a b a)))
         (Var isa X))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_6_test_1() {
    String input = """
        ((module ModX
           (class X (a b c d e)
             (method a (b c d)
               (def a 0.0)
               (a = 1.0)
               a)
             (method b (b c d)
               (def b 0.0)
               (b = 1.0)
               b)))
         (module ModB (class b (a b c d e)))
         (import ModX)
         (def a 1.0)
         (def b 2.0)
         (def Var (new X (a a a a a)))
         (a = b)
         (b = a)
         (Var --> d = (new X (a a a a a)))
         (a = (Var --> c))
         (a = (Var --> a (a b c)))
         (Var isa X))
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_6_test_2() {
    String input = """
        ((module ModX
           (class X (a b c d e)
             (method a (b c d)
               (def a 0.0)
               (a = 1.0)
               a)
             (method b (a a d)
               (def b 0.0)
               (b = 1.0)
               b)))
         (module ModB (class b (a b c d e)))
         (import ModX)
         (def a 1.0)
         (def b 2.0)
         (def Var (new X (a a a a a)))
         (a = b)
         (b = a)
         (Var --> d = (new X (a a a a a)))
         (a = (Var --> c))
         (a = (Var --> a (a b c)))
         (Var isa X))
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_6_test_3() {
    String input = """
        ((module ModX
           (class X (a b c d e)
             (method a (b c d)
               (def a 0.0)
               (a = 1.0)
               a)
             (method a (b c d)
               (def b 0.0)
               (b = 1.0)
               b)))
         (module ModB (class b (a b c d e)))
         (import ModX)
         (def a 1.0)
         (def b 2.0)
         (def Var (new X (a a a a a)))
         (a = b)
         (b = a)
         (Var --> d = (new X (a a a a a)))
         (a = (Var --> c))
         (a = (Var --> a (a b c)))
         (Var isa X))
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_6_test_4() {
    String input = """
        ((module ModX
           (class X (a b c d e)
             (method a (b c d)
               (def a 0.0)
               (a = 1.0)
               (c --> a (a a a)))
             (method b (b c d)
               (def b 0.0)
               (b = 2.0)
               b)))
         (module ModB (class b (a b c d e)))
         (import ModX)
         (import ModB)
         (def a 1.0)
         (def b (new b (a a a a a)))
         (def Var (new X (a a a a a)))
         (Var --> a (a b a)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }


  @Test
  public void test_Tests_copy_6_test_6() {
    String input = """
        ((module ModMath
           (class Math ()
             (method exp (base power)
               (def increment 1.0)
               (def counter 0.0)
               (def condition 0.0)
               (def solution 1.0)
               (while0 condition
                       (block
                         (if0 (counter == power)
                              (condition = 1.0)
                              (block
                                (solution = (this --> mult (solution base)))
                                (counter = (counter + increment))))))
               solution)
             (method mult (a b)
               (def result 0.0)
               (def i 0.0)
               (def condition 0.0)
               (def negOne -1.0)
               (def one 1.0)
               (if0 b
                    (result = 0.0)
                    (while0 condition
                            (block
                              (result = (result + a))
                              (i = (i + one))
                              (if0 (i == b)
                                   (condition = 1.0)
                                   (condition = 0.0))
                              (i = (i / negOne))
                              (if0 (i == b)
                                   (block
                                     (condition = 1.0)
                                     (result = (result / negOne)))
                                   (condition = condition))
                              (i = (i / negOne)))))
               result)
             (method sqrt (n)
               (def it 100.0)
               (def result 0.0)
               (if0 n
                    (result = 0.0)
                    (block
                      (def two 2.0)
                      (def guess (n / two))
                      (def i 0.0)
                      (def condition 0.0)
                      (def one 1.0)
                      (while0 condition
                              (block
                                (def guessDivN (n / guess))
                                (def guessSum (guess + guessDivN))
                                (guess = (guessSum / two))
                                (i = (i + one))
                                (if0 (i == it)
                                     (condition = 1.0)
                                     (condition = 0.0))))
                      (result = guess)))
               result)))
         (module ModPointTwoD
           (import ModMath)
           (class pointTwoD (x y)
             (method distance (point)
               (def math (new Math ()))
               (def xA (this --> x))
               (def yA (this --> y))
               (def xB (point --> x))
               (def yB (point --> y))
               (def negOne -1.0)
               (def negXB (xB / negOne))
               (def negYB (yB / negOne))
               (def deltaX (xA + negXB))
               (def deltaY (yA + negYB))
               (def two 2.0)
               (def deltaXsq (math --> exp (deltaX two)))
               (def deltaYsq (math --> exp (deltaY two)))
               (def sum (deltaXsq + deltaYsq))
               (math --> sqrt (sum)))))
         (import ModPointTwoD)
         (def startPoint 200.0)
         (def endX 296.0)
         (def endY 447.0)
         (def pointA (new pointTwoD (startPoint startPoint)))
         (def pointB (new pointTwoD (endX endY)))
         (def distance (pointA --> distance (pointB)))
         distance)
        """;
    String output = runProgram(input);
    assertEquals("265.0", output);
  }

  @Test
  public void test_Tests_copy_6_test_7() {
    String input = """
        ((module ModPosn
           (class posn (x y)
             (method f () 1.0)))
         (import ModPosn)
         (def a 1.0)
         (def point (new posn (a a)))
         point)
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_6_test_8() {
    String input = """
        ((module ModNum
           (class Num (base)
             (method exp (power)
               (def increment 1.0)
               (def outerCounter 0.0)
               (def outerCondition 0.0)
               (def base (this --> base))
               (def solution 1.0)
               (while0 outerCondition
                       (block
                         (if0 (outerCounter == power)
                              (outerCondition = 1.0)
                              (block
                                (def innerCounter 0.0)
                                (def innerCondition 0.0)
                                (def tempSolution 0.0)
                                (while0 innerCondition
                                        (if0 (innerCounter == base)
                                             (innerCondition = 1.0)
                                             (block
                                               (tempSolution = (tempSolution + solution))
                                               (innerCounter = (innerCounter + increment)))))
                                (solution = tempSolution)
                                (outerCounter = (outerCounter + increment))))))
               solution)))
         (import ModNum)
         (def base 3.0)
         (def num (new Num (base)))
         (def power 10.0)
         (num --> run (power)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_6_test_9() {
    String input = """
        ((module ModA
           (class A ()
             (method f ()
               (def x 1.0)
               x)))
         (import ModA)
         (def x 2.0)
         (def a (new A ()))
         (def newX (a --> f ()))
         x)
        """;
    String output = runProgram(input);
    assertEquals("2.0", output);
  }

  @Test
  public void test_Tests_copy_7_test_0() {
    String input = """
        ((module ModC
           (class C ()
             (method m ()
               (def x 1.0)
               (x = 42.0)
               x)))
         (import ModC)
         (def obj (new C ()))
         (obj --> m ()))
        """;
    String output = runProgram(input);
    assertEquals("42.0", output);
  }

  @Test
  public void test_Tests_copy_7_test_1() {
    String input = """
        ((module ModC
           (class C ()
             (method m ()
               (def x 1.0)
               x)))
         (import ModC)
         (def obj (new C ()))
         obj)
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_7_test_2() {
    String input = """
        ((module ModC (class C (x)))
         (import ModC)
         (def z 1.0)
         (def obj (new C (z)))
         (obj --> x = 42.0)
         (obj --> x))
        """;
    String output = runProgram(input);
    assertEquals("42.0", output);
  }

  @Test
  public void test_Tests_copy_7_test_4() {
    String input = """
        ((module ModC (class C (x x)))
         (def y 1.0)
         (def z 2.0)
         (def obj (new C (y z)))
         C)
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_7_test_6() {
    String input = """
        ((module ModC (class C ()))
         (def x 1.0)
         (x --> f))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_7_test_7() {
    String input = """
        ((module ModC
           (class C ()
             (method m (x) x)))
         (import ModC)
         (def obj (new C ()))
         (obj --> m))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_7_test_8() {
    String input = """
        ((module ModA (class A ()))
         (module ModB (class B ()))
         (import ModA)
         (import ModB)
         (def obj (new A ()))
         (obj isa B))
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_8_test_0() {
    String input = """
        ((module ModA
           (class A (fa fb fc)
             (method sum ()
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (module ModB
           (class B (fa fb fc)
             (method sum (this)
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (import ModA)
         (import ModB)
         (def one 1.0)
         (def two 2.0)
         (def three 3.0)
         (def cA (new A (one two three)))
         (def cB (new B (one one one)))
         (def result 0.0)
         (def same 0.0)
         (result = (cA --> sum ()))
         (cB = (cB --> sum (one)))
         (result = (result + cB))
         (cB = (new A (one two three)))
         (same = (cA == cB))
         (result = (result + same))
         (cA --> fa = three)
         (cB = (cA --> fa))
         (result = (result + cB))
         (cA --> fa = (new B (one two three)))
         (cB = (cA --> fa))
         (if0 (cB isa B)
              (block
                (result = (result + one)))
              (block
                (result = (result + three))))
         (while0 (cB isa B)
                 (block
                   (cB = 1.0)
                   (result = (result + cB))))
         result)
        """;
    String output = runProgram(input);
    assertEquals("14.0", output);
  }

  @Test
  public void test_Tests_copy_8_test_1() {
    String input = """
        ((module ModAdd
           (class Add (x y)
             (method calculate ()
               (def xVal (this --> x))
               (def yVal (this --> y))
               (xVal + yVal))))
         (module ModDivide
           (class Divide (x y)
             (method calculate ()
               (def xVal (this --> x))
               (def yVal (this --> y))
               (xVal / yVal))))
         (module ModXYCalculator
           (import ModAdd)
           (import ModDivide)
           (class XYCalculator (Add Divide)
             (method init (x y)
               (this --> Add = (new Add (x y)))
               (this --> Divide = (new Divide (x y)))
               0.0)
             (method Add ()
               (def Add (this --> Add))
               (Add --> calculate ()))
             (method Divide ()
               (def Divide (this --> Divide))
               (Divide --> calculate ()))))
         (import ModXYCalculator)
         (def result 0.0)
         (def tmp 0.0)
         (def one 1.0)
         (def four 4.0)
         (def a 0.0)
         (a = (new XYCalculator (a a)))
         (tmp = (a --> init (one four)))
         (result = (a --> Add ()))
         (tmp = (a --> Divide ()))
         (result = (result + tmp))
         result)
        """;
    String output = runProgram(input);
    assertEquals("5.25", output);
  }

  @Test
  public void test_Tests_copy_8_test_2() {
    String input = """
        ((module ModA (class A (x)))
         (module ModB (class B (y)))
         (import ModA)
         (import ModB)
         (def one 1.0)
         (def z 0.0)
         (def bOne (new B (one)))
         (def bTwo (new B (one)))
         (def bbOne (new B (bOne)))
         (def bbTwo (new B (bTwo)))
         (def bbbOne (new B (bbOne)))
         (def bbbTwo (new B (bbTwo)))
         (def bbbbOne (new B (bbbOne)))
         (def bbbbTwo (new B (bbbTwo)))
         (def aOne (new A (bbbbOne)))
         (def aTwo (new A (bbbbTwo)))
         (aOne == aTwo))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_8_test_3() {
    String input = """
        ((module ModA
           (class A (fa fb fc)
             (method sum ()
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (module ModB
           (class B (fa fb fc)
             (method sum (this)
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (import ModA)
         (import ModB)
         (def one 1.0)
         (def two 2.0)
         (def three 3.0)
         (def cA (new A (one two three)))
         (def cB (new B (one one one)))
         (def result 0.0)
         (cA --> fa = cB)
         (result = (cA --> fa))
         result)
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_8_test_4() {
    String input = """
        ((module ModA
           (class A (fa fb fc)
             (method sum ()
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (module ModB
           (class B (fa fb fc)
             (method sum (this)
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (import ModA)
         (import ModB)
         (def one 1.0)
         (def two 2.0)
         (def three 3.0)
         (def cA (new A (one two three)))
         (def cB (new B (one one one)))
         (def result 0.0)
         (cA --> fa = cB)
         (result = (cA --> fa))
         (cA + cB))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_8_test_5() {
    String input = """
        ((module ModA
           (class A (fa fb fc)
             (method sum ()
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (module ModB
           (class B (fa fb fc)
             (method sum (this)
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (import ModA)
         (import ModB)
         (def one 1.0)
         (def two 2.0)
         (def three 3.0)
         (def four 4.0)
         (def cA (new A (one two three four)))
         (def cB (new B (one one one)))
         (def result 0.0)
         (cA --> fa = cB)
         (result = (cA --> fa))
         result)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_8_test_6() {
    String input = """
        ((module ModA
           (class A (fa fb fc)
             (method sum ()
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (module ModB
           (class B (fa fb fc)
             (method sum (this)
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (import ModA)
         (import ModB)
         (def one 1.0)
         (def two 2.0)
         (def three 3.0)
         (def four 4.0)
         (def cA (new A (one two three)))
         (def cB (new B (one one one)))
         (def result 0.0)
         (cA --> f = cB)
         (result = (cA --> fa))
         result)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_8_test_7() {
    String input = """
        ((module ModA
           (class A (fa fb fc)
             (method sum (a b c)
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (module ModB
           (class B (fa fb fc)
             (method sum (this)
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (import ModA)
         (import ModB)
         (def one 1.0)
         (def two 2.0)
         (def three 3.0)
         (def four 4.0)
         (def cA (new A (one two three)))
         (def cB (new B (one one one)))
         (def result 0.0)
         (cA --> fa = cB)
         (result = (cA --> sum (one two three four)))
         result)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_8_test_8() {
    String input = """
        ((module ModA
           (class A (fa fb fc)
             (method sum (a b c)
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (module ModB
           (class B (fa fb fc)
             (method sum (this)
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (import ModA)
         (import ModB)
         (def one 1.0)
         (def two 2.0)
         (def three 3.0)
         (def four 4.0)
         (def cA (new A (one two three)))
         (def cB (new B (one one one)))
         (def result 0.0)
         (cA --> fa = cB)
         (result = (cA --> suum (one two three)))
         result)
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_8_test_9() {
    String input = """
        ((module ModA
           (class A (fa fb fc)
             (method sum (a b c)
               (def sum 0.0)
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (module ModB
           (import ModA)
           (class B (fa fb fc)
             (method sum (this)
               (def sum 0.0)
               (def result (new A (sum sum sum)))
               (def fa (this --> fa))
               (def fb (this --> fb))
               (def fc (this --> fc))
               (this --> fa = result)
               (sum = (fa + fb))
               (sum = (sum + fc))
               sum)))
         (import ModB)
         (import ModA)
         (def one 1.0)
         (def two 2.0)
         (def three 3.0)
         (def four 4.0)
         (def cA (new A (one two three)))
         (def cB (new B (one one one)))
         (def result 0.0)
         (cA --> fa = cB)
         (result = (cB --> sum (one)))
         (cB --> fa))
        """;
    String output = runProgram(input);
    assertEquals("\"object\"", output);
  }

  @Test
  public void test_Tests_copy_9_test_0() {
    String input = """
        ((module ModA
           (class A ()
             (method m ()
               (def x 1.0))))
         x)
        """;
    String output = runProgram(input);
    assertEquals("\"parser error\"", output);
  }

  @Test
  public void test_Tests_copy_9_test_2() {
    String input = """
        ((module ModFun (class Fun ()))
         (def ret (new Gun ()))
         ret)
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_9_test_3() {
    String input = """
        ((def x 1.0)
         (if0 x
              (block
                (def y 2.0)
                (def z y)
                (y = z))
              (block
                (def y 3.0)
                (x = 7.0)))
         y)
        """;
    String output = runProgram(input);
    assertEquals("\"undeclared variable error\"", output);
  }

  @Test
  public void test_Tests_copy_9_test_5() {
    String input = """
        ((module ModA
           (class A ()
             (method m (p p)
               (def ret 0.0)
               ret)
             (method m ()
               (def ret 0.0)
               ret)))
         0.0)
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_9_test_9() {
    String input = """
        ((def a 1.0)
         (def b 0.0)
         (a / b))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_instructor_test_0() {
    String input = """
        ((module ModPoint
           (class Point (x x)
             (method delta (x) 1.0)))
         (import ModPoint)
         (def x 1.0)
         (def point (new Point (x x)))
         (point --> x = x)
         (x = (point --> delta (x)))
         x)
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_instructor_test_1() {
    String input = """
        ((module ModC
           (class C (x y)
             (method delta (x)
               (def z (this --> x))
               (def x (x + x))
               (z + x))))
         (import ModC)
         (def x 1.0)
         (def c (new C (x x)))
         (def i (c isa C))
         (x = (x + x))
         (x = (c --> delta (x)))
         (x + i))
        """;
    String output = runProgram(input);
    assertEquals("5.0", output);
  }

  @Test
  public void test_Tests_copy_instructor_test_2() {
    String input = """
        ((module ModCounter
           (class Counter (count)
             (method getCount () (this --> count))))
         (import ModCounter)
         (def u 3.0)
         (def w 42.0)
         (def v 42.0)
         (while0 (w == v)
                 (block
                   (def c (new Counter (u)))
                   (v = c)))
         (v --> getCount ()))
        """;
    String output = runProgram(input);
    assertEquals("3.0", output);
  }

  @Test
  public void test_Tests_copy_instructor_test_3() {
    String input = """
        ((module ModC
           (class C (f)
             (method eq (other)
               (def old (this --> f))
               (def myf old)
               (def urf (other --> f))
               (def res 1.0)
               (if0 (old isa C)
                    (this --> f = 42.0)
                    (this --> f = (old + res)))
               (urf = (other --> f))
               (myf = (this --> f))
               (res = (urf == myf))
               (this --> f = old)
               res)))
         (import ModC)
         (def one 1.0)
         (def c (new C (one)))
         (def d c)
         (def e (new C (c)))
         (one = (c --> eq (d)))
         (d = (c --> eq (e)))
         (one + d))
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_instructor_test_4() {
    String input = """
        ((module ModWhile
           (class While (x)
             (method w (other)
               (def w (this --> x))
               (if0 (this --> x)
                    (w = (w / w))
                    (block
                      (def D -1.0)
                      (this --> x = (w + D))))
               (other --> w (this)))))
         (module ModRepeat
           (import ModWhile)
           (class Repeat ()
             (method w (other)
               (other --> w (this)))))
         (import ModWhile)
         (import ModRepeat)
         (def x 2.0)
         (def r (new Repeat ()))
         (def w (new While (x)))
         (w --> w (r)))
        """;
    String output = runProgram(input);
    assertEquals("\"run-time error\"", output);
  }

  @Test
  public void test_Tests_copy_instructor_test_5() {
    String input = """
        ((module ModCowboy
           (class Cowboy ()
             (method draw () 1.0)))
         (module ModArtist
           (class Artist ()
             (method draw () 666.0)))
         (import ModCowboy)
         (import ModArtist)
         (def a (new Artist ()))
         (def c (new Cowboy ()))
         (def x 0.0)
         (if0 1.0
              (x = a)
              (x = c))
         (x = (x --> draw ()))
         x)
        """;
    String output = runProgram(input);
    assertEquals("1.0", output);
  }

  @Test
  public void test_Tests_copy_instructor_test_6() {
    String input = """
        ((module ModG
           (class G (o)
             (method small (delta n)
               (def k 0.0)
               (def neg -1.0)
               (while0 k
                       (if0 n
                            (k = 1.0)
                            (block
                              (delta = (delta / n))
                              (n = (n + neg)))))
               delta)))
         (module ModH
           (import ModG)
           (class H (g)))
         (import ModG)
         (import ModH)
         (def z 0.0)
         (def g (new G (z)))
         (def o 0.1)
         (def t 100.0)
         (def x (g --> small (o t)))
         (def h (new G (x)))
         (def a (new H (h)))
         (def b (new H (g)))
         (a == b))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void test_Tests_copy_instructor_test_7() {
    String input = """
        ((module ModCounter
           (class Counter (count)
             (method getCount () (this --> count))
             (method upCount ()
               (def crt (this --> count))
               (def one 1.0)
               (this --> count = (crt + one))
               0.0)))
         (import ModCounter)
         (def u 3.0)
         (def c (new Counter (u)))
         (def d c)
         (u = (d --> upCount ()))
         (c --> getCount ()))
        """;
    String output = runProgram(input);
    assertEquals("4.0", output);
  }

  @Test
  public void test_Tests_copy_instructor_test_8() {
    String input = """
        ((module ModPoint
           (class Point (x y)
             (method delta (x) (this --> x))
             (method delta (x) (this --> y))))
         (import ModPoint)
         (def x 1.0)
         (def point (new Point (x x)))
         (point --> x = x)
         (x = (point --> delta (x)))
         x)
        """;
    String output = runProgram(input);
    assertEquals("\"duplicate method, field, or parameter name\"", output);
  }

  @Test
  public void test_Tests_copy_instructor_test_9() {
    String input = """
        ((module ModKnot (class Knot (val)))
         (import ModKnot)
         (def one 1.0)
         (def knt (new Knot (one)))
         (knt --> val = knt)
         (knt == knt))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }
}