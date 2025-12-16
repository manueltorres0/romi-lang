package UnitTests;

import ast.*;
import ast.Number;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import systemComponents.System;
import static org.junit.jupiter.api.Assertions.*;

public class DuplicateNameTests {

  Name xName;
  Name yName;
  ASTNodes numFour;
  Sequence emptyClass;
  Sequence anotherEmptyClass;

  @BeforeEach
  void setUp() {
    xName = new Name("x");
    yName = new Name("y");
    numFour = new Number(4.0);

    // (class Empty ())
    Sequence fields1 = new Sequence();
    emptyClass = new Sequence();
    emptyClass.add(new Name("class"));
    emptyClass.add(new Name("Empty"));
    emptyClass.add(fields1);

    // (class Other ())
    Sequence fields2 = new Sequence();
    anotherEmptyClass = new Sequence();
    anotherEmptyClass.add(new Name("class"));
    anotherEmptyClass.add(new Name("Other"));
    anotherEmptyClass.add(fields2);
  }

  // dup module

  @Test
  void testTwoModulesSameName() {
    Sequence module1 = new Sequence();
    module1.add(new Name("module"));
    module1.add(new Name("ModA"));
    module1.add(emptyClass);

    Sequence module2 = new Sequence();
    module2.add(new Name("module"));
    module2.add(new Name("ModA"));
    module2.add(anotherEmptyClass);

    Sequence system = new Sequence();
    system.add(module1);
    system.add(module2);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsDuplicateModuleName());
  }

  @Test
  void testThreeModulesTwoDuplicates() {
    Sequence module1 = new Sequence();
    module1.add(new Name("module"));
    module1.add(new Name("ModA"));
    module1.add(emptyClass);

    Sequence fieldsB = new Sequence();
    Sequence classB = new Sequence();
    classB.add(new Name("class"));
    classB.add(new Name("ClassB"));
    classB.add(fieldsB);
    Sequence module2 = new Sequence();
    module2.add(new Name("module"));
    module2.add(new Name("ModB"));
    module2.add(classB);

    Sequence module3 = new Sequence();
    module3.add(new Name("module"));
    module3.add(new Name("ModA"));
    module3.add(anotherEmptyClass);

    Sequence system = new Sequence();
    system.add(module1);
    system.add(module2);
    system.add(module3);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsDuplicateModuleName());
  }

  @Test
  void testNoDuplicateModuleNames() {
    Sequence module1 = new Sequence();
    module1.add(new Name("module"));
    module1.add(new Name("ModA"));
    module1.add(emptyClass);

    Sequence module2 = new Sequence();
    module2.add(new Name("module"));
    module2.add(new Name("ModB"));
    module2.add(anotherEmptyClass);

    Sequence system = new Sequence();
    system.add(module1);
    system.add(module2);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsDuplicateModuleName());
  }

  // dup field

  @Test
  void testDuplicateFieldNames() {
    Sequence fields = new Sequence();
    fields.add(xName);
    fields.add(xName);
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }

  @Test
  void testUniqueFieldNames() {
    Sequence fields = new Sequence();
    fields.add(xName);
    fields.add(yName);
    fields.add(new Name("z"));
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }

  // dup method

  @Test
  void testDuplicateMethodNames() {
    Sequence params = new Sequence();
    Sequence method1 = new Sequence();
    method1.add(new Name("method"));
    method1.add(new Name("m"));
    method1.add(params);
    method1.add(xName);

    Sequence method2 = new Sequence();
    method2.add(new Name("method"));
    method2.add(new Name("m"));
    method2.add(params);
    method2.add(yName);

    Sequence fields = new Sequence();
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("TestClass"));
    classSeq.add(fields);
    classSeq.add(method1);
    classSeq.add(method2);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModTest"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }

  @Test
  void testUniqueMethodNames() {
    Sequence params = new Sequence();
    Sequence method1 = new Sequence();
    method1.add(new Name("method"));
    method1.add(new Name("m"));
    method1.add(params);
    method1.add(xName);

    Sequence method2 = new Sequence();
    method2.add(new Name("method"));
    method2.add(new Name("n"));
    method2.add(params);
    method2.add(yName);

    Sequence fields = new Sequence();
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("TestClass"));
    classSeq.add(fields);
    classSeq.add(method1);
    classSeq.add(method2);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModTest"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }

  // dup param name

  @Test
  void testDuplicateParameterNames() {
    Sequence params = new Sequence();
    params.add(new Name("a"));
    params.add(new Name("a"));

    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("m"));
    method.add(params);
    method.add(new Name("a"));

    Sequence fields = new Sequence();
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("TestClass"));
    classSeq.add(fields);
    classSeq.add(method);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModTest"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }

  @Test
  void testUniqueParameterNames() {
    Sequence params = new Sequence();
    params.add(new Name("a"));
    params.add(new Name("b"));
    params.add(new Name("c"));

    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("m"));
    method.add(params);
    method.add(new Name("a"));

    Sequence fields = new Sequence();
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("TestClass"));
    classSeq.add(fields);
    classSeq.add(method);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModTest"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }

  // more

  @Test
  void testFieldAndMethodSameName() {
    Sequence params = new Sequence();
    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("m"));
    method.add(params);
    method.add(xName);

    Sequence fields = new Sequence();
    fields.add(new Name("m"));
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("TestClass"));
    classSeq.add(fields);
    classSeq.add(method);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModTest"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }


  @Test
  void testTwoDifferentMethodsSameParameterName() {
    Sequence params1 = new Sequence();
    params1.add(new Name("val"));
    Sequence method1 = new Sequence();
    method1.add(new Name("method"));
    method1.add(new Name("setX"));
    method1.add(params1);
    method1.add(new Name("val"));

    Sequence params2 = new Sequence();
    params2.add(new Name("val"));
    Sequence method2 = new Sequence();
    method2.add(new Name("method"));
    method2.add(new Name("setY"));
    method2.add(params2);
    method2.add(new Name("val"));

    Sequence fields = new Sequence();
    fields.add(xName);
    fields.add(yName);
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);
    classSeq.add(method1);
    classSeq.add(method2);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }

  @Test
  void testTwoMethodsSameNameDifferentParameters() {
    Sequence params1 = new Sequence();
    params1.add(new Name("a"));
    Sequence method1 = new Sequence();
    method1.add(new Name("method"));
    method1.add(new Name("compute"));
    method1.add(params1);
    method1.add(new Name("a"));

    Sequence params2 = new Sequence();
    params2.add(new Name("a"));
    params2.add(new Name("b"));
    Sequence method2 = new Sequence();
    method2.add(new Name("method"));
    method2.add(new Name("compute"));
    method2.add(params2);
    method2.add(new Name("a"));

    Sequence fields = new Sequence();
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Calculator"));
    classSeq.add(fields);
    classSeq.add(method1);
    classSeq.add(method2);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModCalc"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }

  @Test
  void testTwoClassesInDifferentModulesSameFields() {
    Sequence fieldsA = new Sequence();
    fieldsA.add(xName);
    fieldsA.add(yName);
    Sequence classA = new Sequence();
    classA.add(new Name("class"));
    classA.add(new Name("Point"));
    classA.add(fieldsA);
    Sequence moduleA = new Sequence();
    moduleA.add(new Name("module"));
    moduleA.add(new Name("ModA"));
    moduleA.add(classA);

    Sequence fieldsB = new Sequence();
    fieldsB.add(xName);
    fieldsB.add(yName);
    Sequence classB = new Sequence();
    classB.add(new Name("class"));
    classB.add(new Name("Line"));
    classB.add(fieldsB);
    Sequence moduleB = new Sequence();
    moduleB.add(new Name("module"));
    moduleB.add(new Name("ModB"));
    moduleB.add(classB);

    Sequence system = new Sequence();
    system.add(moduleA);
    system.add(moduleB);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }

  @Test
  void testCombinedDuplicates() {
    Sequence fields = new Sequence();
    fields.add(xName);
    fields.add(xName);

    Sequence params = new Sequence();
    Sequence method1 = new Sequence();
    method1.add(new Name("method"));
    method1.add(new Name("m"));
    method1.add(params);
    method1.add(xName);

    Sequence method2 = new Sequence();
    method2.add(new Name("method"));
    method2.add(new Name("m"));
    method2.add(params);
    method2.add(xName);

    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("TestClass"));
    classSeq.add(fields);
    classSeq.add(method1);
    classSeq.add(method2);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModTest"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }

  @Test
  void testTwoClassesInDifferentModulesSameClassName() {
    // ModA has class Point
    Sequence fieldsA = new Sequence();
    Sequence classA = new Sequence();
    classA.add(new Name("class"));
    classA.add(new Name("Point"));
    classA.add(fieldsA);
    Sequence moduleA = new Sequence();
    moduleA.add(new Name("module"));
    moduleA.add(new Name("ModA"));
    moduleA.add(classA);

    Sequence classB = new Sequence();
    classB.add(new Name("class"));
    classB.add(new Name("Point"));
    classB.add(new Sequence());
    Sequence moduleB = new Sequence();
    moduleB.add(new Name("module"));
    moduleB.add(new Name("ModB"));
    moduleB.add(classB);

    Sequence system = new Sequence();
    system.add(moduleA);
    system.add(moduleB);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsDuplicateModuleName());
    assertFalse(s.containsClassWithDuplicateMethodFieldOrParamNames());
  }

}