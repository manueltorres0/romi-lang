import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LastAssignmentTests {
  private final PrintStream originalOut = System.out;
  private final InputStream originalIn = System.in;
  private ByteArrayOutputStream outContent;
  final String runtime = "\"run-time error\"";

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
      Main.main(new String[] {});
    } catch (Exception e) {
      e.printStackTrace();
    }
    return outContent.toString().trim();
  }

  @Test
  public void testBlatantRunTimeError() {
    String input = """
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
        """;
    String output = runProgram(input);
    assertEquals(runtime, output);
  }

  @Test
  public void testDuplicateImportsDoesNotThrow() {
    String input = """
                 (
                 (module untyped (class C ()))
        
                 (tmodule typed (timport untyped ( () () )) (timport untyped ( () () ))
        
                            (class M ()  (method m () (new C () ) ) ) ( () ( (m ()  ( () () ) ) ) ))
        
                 (import typed)
                 (def hello (new M ()))
                 (def x (hello --> m ()))
                 4.0
                 )
        """;
    String output = runProgram(input);
    assertEquals("4.0", output);
  }


  @Test
  public void testValidNestedConforms() {
    String input =
        """
             (
              (module accempty (class accEmpty ()))
              (module empty (class Empty (inner)))
            
              (module factory (import empty) (import accempty)
                (class Factory () (method create ()
                                          (def inner (new accEmpty()))
                                          (def empty (new Empty (inner)))
                                          (def outer (new Empty (empty)))
                                          outer)))
            
              (timport factory ( () ( (create ()
                                              ( ((inner  ( ( (inner ( () ())  ) )  () ) )) () )
                                              )   ) ))
              (def factory (new Factory ()))
              (def broken (factory --> create ()))
              4.0
              )
            
            
            
            """;
    String output = runProgram(input);
    assertEquals("4.0", output);
  }

  @Test
  public void testStructuralEquality() {
    String input = """
        (
         (module main
           (class C (x)))
         (timport main (((x Number)) ()))
         (def val1 5.0)
         (def val2 5.0)
         (def p1 (new C (val1)))
         (def p2 (new C (val2)))
         (p1 == p2)
         )
        """;
    String output = runProgram(input);
    assertEquals("0.0", output);
  }

}
