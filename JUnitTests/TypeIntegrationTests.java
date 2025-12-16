import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeIntegrationTests {
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
  public void testDefinitionAssignmentSameType() {
    String input = """
        (
                     (def ten 10.0)
                     (ten = 5.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("5.0", output);
  }


  @Test
  public void testDefinitionAssignmentWrongType() {
    String input = """
        ((tmodule modulee (class C ()) (() ()))
          (import modulee)
                     (def ten 10.0)
                     (ten = (new C ()))
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testClassDefinitionWrongFieldOrder() {
    String input = """
        ((tmodule modulee (class C (x y)) ( ((y Number) (x Number)) ()))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testClassDefinitionCorrectFieldOrder() {
    String input = """
        ((tmodule modulee (class C (x y)) ( ((x Number) (y Number)) ()))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testClassDefinitionWrongMethodOrder() {
    String input = """
        ((tmodule modulee (class C () (method m1 () 4.0) (method m2 () 6.0)) ( ()
                                      ( (m2 () Number) (m1 () Number) )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testClassDefinitionCorrectMethodOrder() {
    String input = """
        ((tmodule modulee (class C () (method m1 () 4.0) (method m2 () 6.0)) ( ()
                                      ( (m1 () Number) (m2 () Number) )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testClassDefinitionIncorrectMethodNum() {
    String input = """
        ((tmodule modulee (class C () (method m1 () 4.0) (method m2 () 6.0)) ( ()
                                      ( (m1 () Number)  )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testClassDefinitionIncorrectMethodNum2() {
    String input = """
        ((tmodule modulee (class C () (method m2 () 6.0)) ( ()
                                      ( (m1 () Number) (m2 () Number)  )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testClassDefinitionIncorrectMethodName() {
    String input = """
        ((tmodule modulee (class C () (method m2 () 6.0)) ( ()
                                      ( (m1 () Number)  )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testClassDefinitionCorrectMethodName() {
    String input = """
        ((tmodule modulee (class C () (method m2 () 6.0)) ( ()
                                      ( (m2 () Number)  )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testClassDefinitionIncorrectFieldNum() {
    String input = """
        ((tmodule modulee (class C (x y)) ( ( (y Number)) ()))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }


  @Test
  public void testMethodIncorrectParamNum() {
    String input = """
        ((tmodule modulee (class C () (method m2 (val) 6.0)) ( ()
                                      ( (m2 () Number)  )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testMethodIncorrectParamNum2() {
    String input = """
        ((tmodule modulee (class C () (method m2 () 6.0)) ( ()
                                      ( (m2 (Number) Number)  )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testMethodAssignsParamWrongType() {
    String input = """
        ((tmodule modulee (class C () (method m2 (x) (x = (new C ())) 4.0)) ( ()
                                      ( (m2 (Number) Number)  )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testMethodAssignsParamCorrectType() {
    String input = """
        ((tmodule modulee (class C () (method m2 (x) (x = 5.0) 4.0)) ( ()
                                      ( (m2 (Number) Number)  )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }


  @Test
  public void testMethodReturnTypeMatches() {
    String input = """
        ((tmodule modulee (class C () (method m2 (x) x)) ( ()
                                      ( (m2 (Number) Number)  )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testMethodReturnTypeMatches2() {
    String input = """
        ((tmodule modulee (class C () (method m2 (x) 4.0)) ( ()
                                      ( (m2 (Number) Number)  )
                                                          ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testMethodReturnTypeDoesNotMatch() {
    String input = """
        ((tmodule modulee (class C () (method m2 (x) x)) ( ()
                                      ( (m2 (  ( ()  () )   ) Number)  )
                                                                   ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testMethodReturnTypeDoesNotMatch2() {
    String input = """
        ((tmodule modulee (class C () (method m2 (x) (new C ()) )) ( ()
                                      ( (m2  (Number) Number)  )
                                                                   ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }


  @Test
  public void testThisParamValidReturnType() {
    String input = """
        ((tmodule modulee (class C () (method m2 (this) this)) ( ()
                                      ( (m2  (Number) Number)  )
                                                                   ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testAssignmentOfThisToNum() {
    String input = """
        ((tmodule modulee (class C () (method m2 (x) (this = x) 4.0)) ( ()
                                      ( (m2  (Number) Number)  )
                                                                   ))
          (import modulee)
                     (def ten 10.0)
                                ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }


  @Test
  public void testCallRightParamTypes() {
    String input = """
        ((tmodule modulee 
        (class C () (method m2 (x) (def a x) a))
         ( () ((m2 (Number) Number)) ))
          (import modulee)
                     (def ten 10.0)
                     (def c (new C ()) )
                           (c --> m2 (ten)))
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testCallWrongParamTypes() {
    String input = """
        ((tmodule modulee 
        (class C () (method m2 (x) (def a x) a))
         ( () ((m2 (Number) Number)) ))
          (import modulee)
                     (def ten 10.0)
                     (def c (new C ()) )
                           (c --> m2 (c)))
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testCallWrongOneParamWrongMultipleRight() {
    String input = """
        ((tmodule modulee 
        (class C () (method m2 (x y z) (def a x) a))
         ( () ((m2 (Number Number Number) Number)) ))
          (import modulee)
                     (def ten 10.0)
                     (def c (new C ()) )
                           (c --> m2 (ten c ten)))
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testCallMultipleRightParams() {
    String input = """
        ((tmodule modulee 
        (class C () (method m2 (x y z) (def a x) a))
         ( () ((m2 (Number Number Number) Number)) ))
          (import modulee)
                     (def ten 10.0)
                     (def c (new C ()) )
                           (c --> m2 (ten ten ten)))
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testDeclarationTypeError() {
    String input = """
        ((tmodule modulee 
        (class C () (method m2 (x) (def a x) a))
         ( () ((m2 (Number) Number)) ))
          (import modulee)
                     (def ten 10.0)
                     (def c (new C ()) )
                     (def res (c --> m2 (ten)))
                     res)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testDeclarationTypeError2() {
    String input = """
        ((tmodule modulee 
        (class C () (method m2 (x) (def a x) a))
         ( () ((m2 (Number) Number)) ))
          (import modulee)
                     (def ten 10.0)
                     (def c (new C ()) )
                     (def ten c)
                     ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testCallMethodDoesNotExist() {
    String input = """
        ((tmodule modulee 
        (class C () (method m2 (x y z) (def a x) a))
         ( () ((m2 (Number Number Number) Number)) ))
          (import modulee)
                     (def ten 10.0)
                     (def c (new C ()) )
                           (c --> m3 (ten ten ten)))
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testFieldDoesNotExist() {
    String input = """
        ((tmodule modulee 
        (class C () (method m2 (x y z) (def a x) a))
         ( () ((m2 (Number Number Number) Number)) ))
          (import modulee)
                     (def ten 10.0)
                     (def c (new C ()) )
                           (c --> a))
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testFieldExists() {
    String input = """
        ((tmodule modulee 
        (class C () (method m2 (x y z) (def a x) a))
         ( () ((m2 (Number Number Number) Number)) ))
          (import modulee)
                     (def ten 10.0)
                     (def c (new C ()) )
                           (c --> y))
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testCallMethodAssignmentToWrongType() {
    String input = """
        ((tmodule modulee 
        (class C () (method m2 (x y z) (def a x) a))
         ( () ((m2 (Number Number Number) Number)) ))
          (import modulee)
                     (def ten 10.0)
                     (def c (new C ()) )
                     (c  =  (c --> m2 (ten ten ten)))
                          ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testCallMethodAssignmentToCorrectType() {
    String input = """
        ((tmodule modulee 
        (class C () (method m2 (x y z) (def a x) a))
         ( () ((m2 (Number Number Number) Number)) ))
          (import modulee)
                     (def ten 10.0)
                     (def c (new C ()) )
                     (ten  =  (c --> m2 (ten ten ten)))
                          ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testCallMethodAssignmentToWrongType2() {
    String input = """
        ((tmodule mod (class A ()) (() ()))
         (tmodule modulee (import mod)
           (class C () (method m2 (x y z) (new A ())))
           (() ((m2 (Number Number Number) (() ())))))
         (import modulee)
         (def ten 10.0)
         (def c (new C ()))
         (ten = (c --> m2 (ten ten ten)))
         ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testCallMethodAssignmentToCorrectType2() {
    String input = """
        ((tmodule mod (class A ()) (() ()))
         (tmodule modulee (import mod)
           (class C () (method m2 (x y z) (new A ())))
           (() ((m2 (Number Number Number) (() ())))))
         (import modulee)
         (import mod)
         (def ten 10.0)
         (def a (new A ()))
         (def c (new C ()))
         (a = (c --> m2 (ten ten ten)))
         ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testFieldAssignmentToCorrectType() {
    String input = """
        ((tmodule mod (class A ()) (() ()))
              (tmodule modulee (import mod)
                (class C (a))
                (((a (() ()))) ())) 
              (import modulee)
              (import mod)
              (def ten 10.0)
              (def a (new A ()))
              (def c (new C (a)))
              (a = (c --> a))
              ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testFieldAssignmentToIncorrectType() {
    String input = """
        ((tmodule mod (class A ()) (() ()))
              (tmodule modulee (import mod)
                (class C (a))
                (((a (() ()))) ())) 
              (import modulee)
              (import mod)
              (def ten 10.0)
              (def a (new A ()))
              (def c (new C (a)))
              (ten = (c --> a))
              ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testFieldAssignmentToIncorrectType2() {
    String input = """
        ((tmodule mod (class A ()) (() ()))
              (tmodule modulee (import mod)
                (class C (a))
                (((a Number)) ())) 
              (import modulee)
              (import mod)
              (def ten 10.0)
              (def a (new A ()))
              (def c (new C (ten)))
              (a = (c --> a))
              ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }

  @Test
  public void testFieldAssignmentTocorrectType2() {
    String input = """
        ((tmodule mod (class A ()) (() ()))
              (tmodule modulee (import mod)
                (class C (a))
                (((a Number)) ())) 
              (import modulee)
              (import mod)
              (def ten 10.0)
              (def a (new A ()))
              (def c (new C (ten)))
              (ten = (c --> a))
              ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }

  @Test
  public void testIsa1() {
    String input = """
        ((tmodule mod (class A ()) (() ()))
              (import mod)
              (def ten 10.0)
              (def a (new A ()))
              (a isa A))
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

  @Test
  public void testIsa2() {
    String input = """
        ((tmodule mod (class A ()) (() ()))
              (import mod)
              (def ten 10.0)
              (def a (new A ()))
              (ten isa A))
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }


  @Test
  public void testNewCorrectType() {
    String input = """
        ((tmodule mod (class A ()) (() ()))
              (tmodule modulee (import mod)
                (class C (a))
                (((a Number)) ())) 
              (import modulee)
              (import mod)
              (def ten 10.0)
              (def a (new A ()))
              (def c (new C (ten)))
              ten)
        """;
    String output = runProgram(input);
    assertEquals("10.0", output);
  }


  @Test
  public void testNewWrongType() {
    String input = """
        ((tmodule mod (class A ()) (() ()))
              (tmodule modulee (import mod)
                (class C (a))
                (((a (() ()))) ())) 
              (import modulee)
              (import mod)
              (def ten 10.0)
              (def a (new A ()))
              (def c (new C (ten)))
              ten)
        """;
    String output = runProgram(input);
    assertEquals("\"type error\"", output);
  }



}
