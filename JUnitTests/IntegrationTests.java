import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTests {
  private final PrintStream originalOut = System.out;
  private final InputStream originalIn = System.in;
  private final String parserErrMsg = "\"parser error\"";
  private final String duplicateClassErrMsg = "\"duplicate class name\"";
  private final String duplicateMethodFieldOrParam = "\"duplicate method, field, or parameter name\"";
  private final String undeclaredVarErrMsg = "\"undeclared variable error\"";
  private final String belongsMessage = "\"belongs\"";
  private ByteArrayOutputStream outContent;

  @org.junit.jupiter.api.BeforeEach
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

  private String runMain(String args) {
    System.setIn(new ByteArrayInputStream(args.getBytes()));
    Main.main(new String[]{});
    // maybe remove trim? currently here just to make life easier
    // and not have to add \n to all of the expected outputs
    return outContent.toString().trim();
  }

  private void runTest(String args, String expected) {
    String result = runMain(args);
    assertEquals(expected, result);
  }

  @Test
  void testOne() {
    runTest("((module A (class X ())) " +
            "  (module B " +
            "  (import A) " +
            "  (class Y ()" +
            "  (method make () " +
            "  (def x (new X ()))" +
            "  x)" +
            "  )" +
            "  ) " +
            "  (import B) " +
            "  (def a (new Y ()))" +
            "  (a --> make ()))", "\"object\"");
  }

//  @Test
//  void testProfOne() {
//    runTest("((module Point (class Point\n" +
//            "                 (x y)\n" +
//            "                 (method delta (x) (def y (this --> y)) (x = 1.0) (x + y))))\n" +
//            " (import Point)\n" +
//            " (def x 1.0)\n" +
//            " (def point (new Point (x x)))\n" +
//            " (point --> x = x)\n" +
//            " (x = (point --> delta (x)))\n" +
//            " x)", "2.0");
//  }
//
//  @Test
//  void testProfTwo() {
//    runTest("((module Point (class Point\n" +
//            "                 (x y)\n" +
//            "                 (method delta (x) (def y (this --> y)) (x = 1.0) (x + y))))\n" +
//            " (module Point (class Point\n" +
//            "                 (x y z)\n" +
//            "                 (method\n" +
//            "                  delta\n" +
//            "                  ()\n" +
//            "                  (def x (this --> x))\n" +
//            "                  (def y (this --> y))\n" +
//            "                  (x + y))))\n" +
//            " (import Point)\n" +
//            " (def x 1.0)\n" +
//            " (def point (new Point (x x)))\n" +
//            " (point --> x = x)\n" +
//            " (x = (point --> delta (x)))\n" +
//            " x)\n", "\"duplicate module name\"");
//  }
//
//  @Test
//  void testProfThree() {
//    runTest("((module PointThreeD (class PointThreeD\n" +
//            "                       (x y z)\n" +
//            "                       (method\n" +
//            "                        delta\n" +
//            "                        ()\n" +
//            "                        (def x (this --> x))\n" +
//            "                        (def y (this --> y))\n" +
//            "                        (x + y))))\n" +
//            " (import PointThreeD)\n" +
//            " (def x 1.0)\n" +
//            " (def point (new Point (x x)))\n" +
//            " (point --> x = x)\n" +
//            " (x = (point --> delta (x)))\n" +
//            " x)\n", "\"undeclared variable error\"");
//  }


  @Test
  void testModuleShadowing() {
    runTest("(" +
            "(module A (class D () (method m () 2.0)))" +
            "(module B (class D () (method m () 3.0)))" +
            "(import A)" +
            "(import B)" +
            "(def d (new D ()))" +
            "(d --> m ())" +
            ")", "3.0");
  }
  @Test
  void testModuleShadowing2() {
    runTest("(" +
            "(module A (class D () (method m () 2.0)))" +
            "(module B (class D () (method m () 3.0)))" +
            "(import B)" +
            "(import A)" +
            "(def d (new D ()))" +
            "(d --> m ())" +
            ")", "2.0");
  }
  @Test
  void testModuleImportsModuleWithSameClassName() {
    runTest("(" +
            "(module A (class D () (method m () 2.0)))" +
            "(module B (import A) (class D () (method m () 3.0) " +
            "(method n () " +
            "(def X (new D ()))" +
            "(X --> m ()))))" +
            "(import A)" +
            "(import B)" +
            "(def d (new D ()))" +
            "(d --> n ())" +
            ")", "3.0");
  }

  @Test
  void testModuleCannotImportFutureModule() {
    runTest("(" +
            "(module A (import B) (class D () (method m () 2.0)))" +
            "(module B (class D () (method m () 3.0) " +
            "(method n () " +
            "(def X (new D ()))" +
            "(X --> m ()))))" +
            "(import A)" +
            "(def d (new D ()))" +
            "(d --> n ())" +
            ")", "\"undeclared variable error\"");
  }

  @Test
  void testRunTimeError() {
    runTest("(" +
            "(module A (class D () (method m () 2.0)))" +
            "(module B (import A) (class D () (method m () (def zero 0.0) (zero / zero)) " +
            "(method n () " +
            "(def X (new D ()))" +
            "(X --> m ()))))" +
            "(import A)" +
            "(import B)" +
            "(def d (new D ()))" +
            "(d --> n ())" +
            ")", "\"run-time error\"");
  }

  @Test
  void testDuplicateMethodNameError() {
    runTest("(" +
            "(module A (class D () (method m () 2.0)))" +
            "(module B (import A) (class D () (method m () 3.0) " +
            "(method m () " +
            "(def X (new D ()))" +
            "(X --> m ()))))" +
            "(import A)" +
            "(import B)" +
            "(def d (new D ()))" +
            "(d --> m ())" +
            ")", "\"duplicate method, field, or parameter name\"");
  }

  @Test
  void testDuplicateParamNameError() {
    runTest("(" +
            "(module A (class D () (method m () 2.0)))" +
            "(module B (import A) (class D (a a) (method m () 3.0) " +
            "(method n () " +
            "(def X (new D ()))" +
            "(X --> m ()))))" +
            "(import A)" +
            "(import B)" +
            "(def d (new D ()))" +
            "(d --> n ())" +
            ")", "\"duplicate method, field, or parameter name\"");
  }

  @Test
  void testDuplicateFieldName() {
    runTest("(" +
            "(module A (class D () (method m () 2.0)))" +
            "(module B (import A) (class D (X X) (method m () 3.0) " +
            "(method n () " +
            "(def X (new D ()))" +
            "(X --> m ()))))" +
            "(import A)" +
            "(import B)" +
            "(def d (new D ()))" +
            "(d --> n ())" +
            ")", "\"duplicate method, field, or parameter name\"");
  }

  @Test
  void testDuplicateModuleNameError() {
    runTest("(" +
            "(module A (class D () (method m () 2.0)))" +
            "(module A (import A) (class D () (method m () 3.0) " +
            "(method n () " +
            "(def X (new D ()))" +
            "(X --> m ()))))" +
            "(import A)" +
            "(import B)" +
            "(def d (new D ()))" +
            "(d --> n ())" +
            ")", "\"duplicate module name\"");
  }

  @Test
  void testUndeclaredClassInModule() {
    runTest("(" +
            "(module A (class D () (method m () 2.0)))" +
            "(module B (import A) (class D () (method m () 3.0) " +
            "(method n () " +
            "(def X (new A ()))" +
            "(X --> m ()))))" +
            "(import A)" +
            "(import B)" +
            "(def d (new D ()))" +
            "(d --> n ())" +
            ")", "\"undeclared variable error\"");
  }

  @Test
  void runTestDuplicateModuleErrorPriority() {
    runTest("(" +
            "(module A (class D () (method m () 2.0)))" +
            "(module A (import A) (class D (X X) (method m () 3.0) " +
            "(method m (b b) " +
            "(def X (new A ()))" +
            "(X --> m ()))))" +
            "(import A)" +
            "(import B)" +
            "(def d (new D ()))" +
            "(d --> n ())" +
            ")", "\"duplicate module name\"");
  }





//  @Test
//  void testNotInfiniteLoop() {
//    runTest("(4.0)", "4.0");
//  }
//
//  @Test
//  void testNotInfiniteLoop2() {
//    runTest("((def x 0.0) (def y 3.0) (if0 x (block (def x 1.0) (y = x)) (block (def x 2.0) (y = x))) y)", "1.0");
//  }
//
//  //Tests from assignment 5 (should all pass)
//  @Test
//  void testZeroInProfessor() {
//    runTest("((def x 1.0) (def y x) (x = 0.0) x y)", parserErrMsg);
//  }
//
//  @Test
//  void testOneInProfessor() {
//    runTest("((def y 1.0) (x = 1.0) x)", undeclaredVarErrMsg);
//  }
//
//  @Test
//  void testTwoInProfessor() {
//    runTest("((def x 0.0) (x = 0.0) (while0 x (x = 2.0)) x)", "2.0");
//  }
//
//  @Test
//  void testParserErrorPrioritizedOverUndeclaredVariableError() {
//    runTest("((if0 3.0 (block (while0 var (block (while0 vartwo (block (if0 == (x = y) (sanity = gone))))(while0 dog (x = y))))) (block (a = b))) x )", parserErrMsg);
//  }
//
//  @Test
//  void testUndeclaredVariableErrorInWellFormedAST() {
//    runTest("((if0 3.0 (block (while0 var (block (while0 vartwo (block (if0 haha (x = y) (sanity = gone))))(while0 dog (x = y))))) (block (a = b))) x )", undeclaredVarErrMsg);
//  }
//
//  @Test
//  void testLocalScopeIsForgotten() {
//    runTest(
//            "((def a 0.0) (def b 1.0) (if0 a (block (def x 1.0) (x = a)) (a = b)) x)", undeclaredVarErrMsg);
//  }
//
//  @Test
//  void testLocalScopeDeclarationsAreStored() {
//    runTest("((def var 0.0) (def dog 10.0) (def a 0.0) (def b 0.0) (def x 1.0) (def y 2.0) (if0 3.0 (block (while0 var (block (def vartwo 0.0) (while0 vartwo (block (def haha 0.0) (def sanity 3.0) (def gone 2.4) (if0 haha (x = y) (sanity = gone))))(while0 dog (x = y))))) (block (a = b))) x )", "1.0");
//  }
//
//  @Test
//  void testRedefiningGlobalVariable() {
//    runTest("((def x 0.0) (def y 3.0) (if0 x (block (def x 1.0) (y = x)) (block (def x 2.0) (y = x))) x)", "0.0");
//  }
//
//  @Test
//  void testUndeclaredVariableInWhileBlock() {
//    runTest("((def i 1.0) (while0 i (j = 2.0)) i)", undeclaredVarErrMsg);
//  }
//
//  @Test
//  void testUndeclaredVariableWithinExpression() {
//    runTest("((def x 1.0) (x = (x + y)) x)", undeclaredVarErrMsg);
//  }
//
//  @Test
//  void testFailedFromCSK() {
//    runTest("(((x = 1.0) (y = 2.0) (z = (x + y)) (if0 x (block (z = (z / y))) (block)) (while0 z (block (z = (z / y)) (if0 z (block (m = (x + y))) (block)))) z))", parserErrMsg);
//  }
//
//  @Test
//  void testFailedFromCSKAgain() {
//    runTest("(((a = 1.0) (b = 3.0) (c = (a / b)) (d = 10.0) (e = (c + c)) (e = (e + c)) (f = (e / d)) (g = 7.0) (h = (f / g)) (i = 17.0) (j = (h / i)) (k = 999.7) (result = (k / j)) result))", parserErrMsg);
//  }
//
//  @Test
//  void testFailedFromCSK8() {
//    runTest("""
//             (((x = 1.0)
//             (y = 2.0)
//             (z = (x + y))
//             (if0 x (block (z = (z / y))) (block))
//             (while0 z (block (z = (z / y)) (if0 z (block (m = (x + y))) (block))))
//             z))""", parserErrMsg);
//  }
//
//  @Test
//  void testDeeplyNestedElse() {
//    runTest("((def x 1.0)\n" +
//            " (def res 0.0)\n" +
//            " (def zero 0.0)\n" +
//            " (if0 zero\n" +
//            "      (block (def temp 2.0)\n" +
//            "             (def sum1 (x + temp))\n" +
//            "             (if0 zero\n" +
//            "                  (block (def temp2 3.0)\n" +
//            "                         (def sum2 (sum1 + temp2))\n" +
//            "                         (if0 1.0\n" +
//            "                              (block (def temp3 4.0)\n" +
//            "                                     (def sum3 (sum2 + temp3))\n" +
//            "                                     (res = sum3))\n" +
//            "                              (res = 5.0)))\n" +
//            "                  (res = 4.0)))\n" +
//            "      (res = 3.0))\n" +
//            " res)", "5.0");
//  }
//
//  @Test
//  void testMethodOutsideOfClass() {
//    runTest("((method foo (x) (def y 1.0) y) (def z 1.0) z)", parserErrMsg);
//  }
//  @Test
//  void testMissingParensAroundFieldNames() {
//    runTest("((class A x y (method m (p) p)) (def y 1.0) y)", parserErrMsg);
//  }
//  @Test
//  void testDuplicateClassName() {
//    runTest("((class A (x) (method m () -1000.0)) (class A (x) (method a () -1000.0)) (def y 1.0) y)", duplicateClassErrMsg);
//  }
//
//  @Test
//  void testDuplicateClassPriorityOverDuplicateMethod() {
//    runTest("((class A (x) (method m () -1000.0) (method m () -1000.0)) (class A (x) (method a () -1000.0)) (def y 1.0) y)", duplicateClassErrMsg);
//  }
//
//  @Test
//  void testClassCanReferenceLaterClass() {
//    runTest("((class A (x) (method m () -100.0) (method a () (def c (new T ())) -200.0)) (class T (x) (method a () -300.0)) (def y 1.0) y)", "1.0");
//  }
//  //Duplicate Field Name Cases:
//  @Test
//  void testDupeField() {
//    runTest("((class A (x x) (method m () -1000.0)) (def y 1.0) y)", duplicateMethodFieldOrParam);
//  }
//
//  @Test
//  void testDupeMethod() {
//    runTest("((class A (x) (method m () -1000.0) (method m () -1000.0)) (def y 1.0) y) ", duplicateMethodFieldOrParam);
//  }
//
//  @Test
//  void testDupeMethodDiffName() {
//    runTest("((class A (x) (method m () -100.0) (method a () -100.0)) (def y 1.0) y) ", "1.0");
//  }
//
//
//  @Test
//  void testDupeParam() {
//    runTest("((class A (x) (method m (a a) -1000.0)) (def y 1.0) y)", duplicateMethodFieldOrParam);
//  }
//
//  @Test
//  void testDupeParamDiffMethod() {
//    runTest("((class A (x) (method m (a) -1000.0) (method b (a) -1000.0))  (def y 1.0) y)", "1.0");
//  }
//
//  @Test
//  void testSimpleValid() {
//    runTest("((class A (x) (method m () -1000.0)) (def y 1.0) y)", "1.0");
//  }
//
//  @Test
//  void testValidMethod() {
//    runTest("((class A (x) (method m () (def y this) (y isa A))) (def t 1.0) (def z (new A (t))) (z --> m ()))", "0.0");
//  }
//
//  @Test
//  void testProfOne() {
//    runTest("((class Point (x y) (method delta (x) (def y (this --> y)) (x = 1.0) (x + y))) (def x 1.0) (def point (new Point (x x))) (point --> x = x) (x = (point --> delta (x))) x)","2.0");
//  }
//
//  @Test
//  void testProfTwo() {
//    runTest("""
//          ((class Point (x y) (method delta (x) (def y (this --> y)) (x = 1.0) (x + y)))
//           (class Point
//             (x y z)
//             (method delta () (def x (this --> x)) (def y (this --> y)) (x + y)))
//           (def x 1.0)
//           (def point (new Point (x x)))
//           (point --> x = x)
//           (x = (point --> delta (x)))
//           x)
//          """, duplicateClassErrMsg);
//  }
//
//  @Test
//  void testProfThree() {
//    runTest("((class PointThreeD\n" +
//            "   (x y z)\n" +
//            "   (method delta () (def x (this --> x)) (def y (this --> y)) (x + y)))\n" +
//            " (def x 1.0)\n" +
//            " (def point (new Point (x x)))\n" +
//            " (point --> x = x)\n" +
//            " (x = (point --> delta (x)))\n" +
//            " x)", undeclaredVarErrMsg);
//  }
//
//  @Test
//  void testUndefinedClassInIsA() {
//    runTest("((class A (x) (method m () (c isa B))) 1.0)", undeclaredVarErrMsg);
//  }
//  @Test
//  void testDefinedClassInIsA() {
//    runTest("((class A (x) (method m () (c isa A))) 1.0)", undeclaredVarErrMsg);
//  }
//
//  @Test
//  void testClassKeywordCannotBeClassName() {
//    runTest("((class class (x) (method m () (def y this) (y isa A))) (def t 1.0) (def z (new A (t))) (z --> m ()))", parserErrMsg);
//  }
//
//  @Test
//  void testClassKeywordCannotBeFieldName() {
//    runTest("((class A (class) (method m () (def y this) (y isa A))) (def t 1.0) (def z (new A (t))) (z --> m ()))", parserErrMsg);
//  }
//
//
//  //  TRY TESTING WHAT WOULD HAPPEN IF (def x 1.0) (def b (new A (x))) (b --> m ()), IMO IT SHOULD RETURN TRUE (0.0)
////  ALSO TRY TESTING WHAT HAPPENS IF AN OBJECT'S NAME IS THE SAME AS THE CLASS NAME / METHOD NAME, EXPECTED BEHAVIOR: IT SHOULDN'T CAUSE ANY PROBLEMS
//  @Test
//  void testThisCanBeDefined() {
//    runTest("((class A (x) (method m () (def this this) (this isa A))) 10.0)", "10.0");
//  }
//
//  @Test
//  void testThisCanBeRedefinedAndCalled() {
//    runTest("((class A (x) (method m () (def this this) (this isa A))) (def x 1.0) (def b (new A (x))) (b --> m ()))", "0.0");
//  }
//
//  @Test
//  void testObjectCanShareNameWithClass() {
//    runTest("((class A (x) (method m () (def this this) (this isa A))) (def x 1.0) (def A (new A (x))) (A --> m ()))", "0.0");
//  }
//
//  @Test
//  void testObjectCanShareNameWithMethod() {
//    runTest("((class A (x) (method m () (def this this) (this isa A))) (def x 1.0) (def m (new A (x))) (m --> m ()))", "0.0");
//  }
//
//  @Test
//  void testObjectReturnsObject() {
//    runTest("((class A (x) (method m () (def this this) (this isa A))) (def x 1.0) (def m (new A (x))) m)", "\"object\"");
//  }
//  @Test
//  void testFieldUpdateFunctionality() {
//    runTest("((class A (x) (method m () (def this this) (this isa A))) (def x 1.0) (def m (new A (x))) (m --> x = 7.0) (m --> x))", "7.0");
//  }
//
//  @Test
//  void testFieldUpdateDoesNotInterfereWithGlobalScope() {
//    runTest("((class A (x) (method m () (def this this) (this isa A))) (def x 1.0) (def m (new A (x))) (m --> x = 7.0) x)", "1.0");
//  }
//  @Test
//  void testIsAReturnsFalse() {
//    runTest("((class A (x) (method m () (def this this) (this isa B))) (class B (x) (method m () (def this this) (this isa B))) (def x 1.0) (def m (new A (x))) (m --> m()))", "1.0");
//  }
//  @Test
//  void testOnlyExpression() {
//    runTest("(0.0)", "0.0");
//  }
//
//  @Test
//  void testFailedStudentTest26Part1() {
//    runTest("((class Empty\n" +
//            "     ()\n" +
//            "     (method addToFront (x) (new ConsList (x this)))\n" +
//            "     (method isEmpty () 0.0)\n" +
//            "     (method contains (x) 1.0)\n" +
//            "     (method length () 0.0)\n" +
//            "     (method get (index) -1.0))\n" +
//            "   (class ConsList\n" +
//            "     (first rest)\n" +
//            "     (method isEmpty () 1.0)\n" +
//            "     (method addToFront (x) (new ConsList (x this)))\n" +
//            "     (method\n" +
//            "      contains\n" +
//            "      (x)\n" +
//            "      (def result 0.5)\n" +
//            "      (def firstElem (this --> first))\n" +
//            "      (def restOfList (this --> rest))\n" +
//            "      (def zero 0.0)\n" +
//            "      (if0\n" +
//            "       (firstElem == x)\n" +
//            "       (result = zero)\n" +
//            "       (result = (restOfList --> contains (x))))\n" +
//            "      result)\n" +
//            "     (method\n" +
//            "      length\n" +
//            "      ()\n" +
//            "      (def one 1.0)\n" +
//            "      (def restOfList (this --> rest))\n" +
//            "      (def result (restOfList --> length ()))\n" +
//            "      (result + one))\n" +
//            "     (method\n" +
//            "      get\n" +
//            "      (index)\n" +
//            "      (def result 0.5)\n" +
//            "      (def firstElem (this --> first))\n" +
//            "      (def restOfList (this --> rest))\n" +
//            "      (if0\n" +
//            "       index\n" +
//            "       (result = firstElem)\n" +
//            "       (block\n" +
//            "        (def negOne -1.0)\n" +
//            "        (def nextIndex (index + negOne))\n" +
//            "        (result = (restOfList --> get (nextIndex)))))\n" +
//            "      result))\n" +
//            "   (def triNumberList (new Empty ()))\n" +
//            "   (def item 0.0)\n" +
//            "   (def numItems 10.0)\n" +
//            "   (def keepLooping 0.0)\n" +
//            "   (def i 1.0)\n" +
//            "   (def one 1.0)\n" +
//            "   (def fifteen 15.0)\n" +
//            "   (def result 0.5)\n" +
//            "   (def failure -732.3)\n" +
//            "   (while0\n" +
//            "    keepLooping\n" +
//            "    (block\n" +
//            "     (triNumberList = (triNumberList --> addToFront (item)))\n" +
//            "     (item = (item + i))\n" +
//            "     (if0 (i == numItems) (keepLooping = one) (i = (i + one)))))\n" +
//            "   (if0\n" +
//            "    (triNumberList --> contains (fifteen))\n" +
//            "    (block\n" +
//            "     (def two 2.0)\n" +
//            "     (def twothElem (triNumberList --> get (two)))\n" +
//            "     (result = (triNumberList --> length ()))\n" +
//            "     (result = (result + twothElem)))\n" +
//            "    (result = failure))\n" +
//            "   result)", "38.0");
//  }
//
//  @Test
//  void testFailedStudentTest26Part2() {
//    runTest("((class A (x))\n" +
//            "   (class B (x))\n" +
//            "   (def temp 0.0)\n" +
//            "   (def templol 0.0)\n" +
//            "   (def a (new A (temp)))\n" +
//            "   (def b (new B (temp)))\n" +
//            "   (def otherA (new A (temp)))\n" +
//            "   (def otherB (new B (temp)))\n" +
//            "   (def result 0.0)\n" +
//            "   (a --> x = b)\n" +
//            "   (b --> x = a)\n" +
//            "   (otherA --> x = otherB)\n" +
//            "   (templol = (new B (otherA)))\n" +
//            "   (temp = (new A (templol)))\n" +
//            "   (otherB --> x = temp)\n" +
//            "   (if0 (a == otherA) (result = 0.0) (result = 1.0))\n" +
//            "   result)", "0.0");
//  }
//  @Test
//  void testStudentCaseFail23() {
//    runTest("((class C (f) (method m (p) 42.0))\n" +
//            "   (class cc (f) (method m (p) (def newBox (new cc (p))) 42.0))\n" +
//            "   (def one 1.0)\n" +
//            "   (def instance (new C (one)))\n" +
//            "   (def field (instance --> f))\n" +
//            "   (instance --> f = (instance --> m (one)))\n" +
//            "   (one = (instance --> f))\n" +
//            "   one)","42.0");
//  }
}