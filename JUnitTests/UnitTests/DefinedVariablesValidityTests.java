package UnitTests;

import ast.*;
import ast.Number;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import systemComponents.System;
import static org.junit.jupiter.api.Assertions.*;

public class DefinedVariablesValidityTests {

  Name xName;
  Name yName;
  Name zName;
  ASTNodes numFour;
  ASTNodes numFive;
  Sequence emptyModule;

  @BeforeEach
  void setUp() {
    xName = new Name("x");
    yName = new Name("y");
    zName = new Name("z");
    numFour = new Number(4.0);
    numFive = new Number(5.0);

    // Helper: (module ModEmpty (class Empty ()))
    Sequence fields = new Sequence();
    Sequence emptyClass = new Sequence();
    emptyClass.add(new Name("class"));
    emptyClass.add(new Name("Empty"));
    emptyClass.add(fields);

    emptyModule = new Sequence();
    emptyModule.add(new Name("module"));
    emptyModule.add(new Name("ModEmpty"));
    emptyModule.add(emptyClass);
  }

  @Test
  void testSystemWithUndefinedVariableInExpression() {
    Sequence system = new Sequence();
    system.add(xName);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testSystemWithDefinedVariableInExpression() {
    Sequence def = new Sequence();
    def.add(new Name("def"));
    def.add(xName);
    def.add(numFour);

    Sequence system = new Sequence();
    system.add(def);
    system.add(xName);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testAssignmentWithUndefinedVariable() {
    Sequence assignment = new Sequence();
    assignment.add(xName);
    assignment.add(new Name("="));
    assignment.add(yName);

    Sequence system = new Sequence();
    system.add(assignment);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testAssignmentWithDefinedVariable() {
    Sequence defY = new Sequence();
    defY.add(new Name("def"));
    defY.add(yName);
    defY.add(numFive);

    Sequence defX = new Sequence();
    defX.add(new Name("def"));
    defX.add(xName);
    defX.add(numFour);

    Sequence assignment = new Sequence();
    assignment.add(xName);
    assignment.add(new Name("="));
    assignment.add(yName);

    Sequence system = new Sequence();
    system.add(defY);
    system.add(defX);
    system.add(assignment);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testDeclarationWithUndefinedVariableInExpression() {
    Sequence def = new Sequence();
    def.add(new Name("def"));
    def.add(xName);
    def.add(yName);

    Sequence system = new Sequence();
    system.add(def);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testDeclarationChaining() {
    Sequence defX = new Sequence();
    defX.add(new Name("def"));
    defX.add(xName);
    defX.add(numFour);

    Sequence defY = new Sequence();
    defY.add(new Name("def"));
    defY.add(yName);
    defY.add(xName);

    Sequence system = new Sequence();
    system.add(defX);
    system.add(defY);
    system.add(yName);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testAdditionWithUndefinedVariable() {
    Sequence addition = new Sequence();
    addition.add(xName);
    addition.add(new Name("+"));
    addition.add(xName);

    Sequence system = new Sequence();
    system.add(addition);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testAdditionWithBothDefined() {
    Sequence defX = new Sequence();
    defX.add(new Name("def"));
    defX.add(xName);
    defX.add(numFour);

    Sequence defY = new Sequence();
    defY.add(new Name("def"));
    defY.add(yName);
    defY.add(numFive);

    Sequence addition = new Sequence();
    addition.add(xName);
    addition.add(new Name("+"));
    addition.add(yName);

    Sequence system = new Sequence();
    system.add(defX);
    system.add(defY);
    system.add(addition);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testWhileStatementWithUndefinedInCondition() {
    Sequence defX = new Sequence();
    defX.add(new Name("def"));
    defX.add(xName);
    defX.add(numFour);

    Sequence inside = new Sequence();
    inside.add(xName);
    inside.add(new Name("="));
    inside.add(numFour);

    Sequence whileStmt = new Sequence();
    whileStmt.add(new Name("while0"));
    whileStmt.add(yName); // y is undefined
    whileStmt.add(inside);

    Sequence system = new Sequence();
    system.add(defX);
    system.add(whileStmt);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testBlockWithUnDeclaredReassignment() {
    Sequence defX = new Sequence();
    defX.add(new Name("def"));
    defX.add(xName);
    defX.add(numFour);

    Sequence assignment = new Sequence();
    assignment.add(yName); // y not defined
    assignment.add(new Name("="));
    assignment.add(xName);

    Sequence block = new Sequence();
    block.add(new Name("block"));
    block.add(defX);
    block.add(assignment);

    Sequence ifStmt = new Sequence();
    ifStmt.add(new Name("if0"));
    ifStmt.add(numFour);
    ifStmt.add(block);
    ifStmt.add(assignment);

    Sequence system = new Sequence();
    system.add(ifStmt);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testBlockWithLocalDeclarationInIfStatement() {
    Sequence defX = new Sequence();
    defX.add(new Name("def"));
    defX.add(xName);
    defX.add(numFour);

    Sequence defY = new Sequence();
    defY.add(new Name("def"));
    defY.add(yName);
    defY.add(new Number(3.0));

    Sequence assignment = new Sequence();
    assignment.add(yName);
    assignment.add(new Name("="));
    assignment.add(xName);

    Sequence assignment2 = new Sequence();
    assignment2.add(yName);
    assignment2.add(new Name("="));
    assignment2.add(new Number(6.0));

    Sequence block = new Sequence();
    block.add(new Name("block"));
    block.add(defX);
    block.add(assignment);

    Sequence ifStmt = new Sequence();
    ifStmt.add(new Name("if0"));
    ifStmt.add(numFour);
    ifStmt.add(block);
    ifStmt.add(assignment2);

    Sequence system = new Sequence();
    system.add(defY);
    system.add(ifStmt);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testNumberLiteralsNeverUndefined() {
    Sequence system = new Sequence();
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testNestedBlockAccessesOuterBlockDef() {
    Sequence defX = new Sequence();
    defX.add(new Name("def"));
    defX.add(xName);
    defX.add(numFour);

    Sequence defZ = new Sequence();
    defZ.add(new Name("def"));
    defZ.add(zName);
    defZ.add(new Number(15.0));

    Sequence defY = new Sequence();
    defY.add(new Name("def"));
    defY.add(yName);
    defY.add(numFive);

    Sequence assignYtoX = new Sequence();
    assignYtoX.add(yName);
    assignYtoX.add(new Name("="));
    assignYtoX.add(xName);

    Sequence assignZ = new Sequence();
    assignZ.add(zName);
    assignZ.add(new Name("="));
    assignZ.add(numFive);

    Sequence innerBlock = new Sequence();
    innerBlock.add(new Name("block"));
    innerBlock.add(assignYtoX);

    Sequence innerStatementBlock = new Sequence();
    innerStatementBlock.add(new Name("while0"));
    innerStatementBlock.add(numFive);
    innerStatementBlock.add(innerBlock);

    Sequence outerBlock = new Sequence();
    outerBlock.add(new Name("block"));
    outerBlock.add(defY);
    outerBlock.add(innerStatementBlock);

    Sequence ifStmt = new Sequence();
    ifStmt.add(new Name("if0"));
    ifStmt.add(numFour);
    ifStmt.add(outerBlock);
    ifStmt.add(assignZ);

    Sequence system = new Sequence();
    system.add(defX);
    system.add(defZ);
    system.add(ifStmt);
    system.add(numFour);

    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  // ========== MODULE/CLASS TESTS ==========

  @Test
  void testUndefinedClassNameInNew() {
    // (new Point (x)) but Point not defined in any module
    Sequence defX = new Sequence();
    defX.add(new Name("def"));
    defX.add(xName);
    defX.add(numFour);

    Sequence args = new Sequence();
    args.add(xName);

    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);

    Sequence system = new Sequence();
    system.add(defX);
    system.add(newExpr);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testDefinedClassNameInNew() {
    // (module ModPoint (class Point (x))) then (import ModPoint) (new Point (val))
    Sequence fields = new Sequence();
    fields.add(xName);
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence importStmt = new Sequence();
    importStmt.add(new Name("import"));
    importStmt.add(new Name("ModPoint"));

    Sequence defVal = new Sequence();
    defVal.add(new Name("def"));
    defVal.add(new Name("val"));
    defVal.add(numFour);

    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);

    Sequence system = new Sequence();
    system.add(module);
    system.add(importStmt);
    system.add(defVal);
    system.add(newExpr);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testUndefinedClassNameInIsa() {
    // x isa Point, but Point not defined
    Sequence defX = new Sequence();
    defX.add(new Name("def"));
    defX.add(xName);
    defX.add(numFour);

    Sequence isaExpr = new Sequence();
    isaExpr.add(xName);
    isaExpr.add(new Name("isa"));
    isaExpr.add(new Name("Point"));

    Sequence system = new Sequence();
    system.add(defX);
    system.add(isaExpr);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testDefinedClassNameInIsa() {
    Sequence fields = new Sequence();
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence importStmt = new Sequence();
    importStmt.add(new Name("import"));
    importStmt.add(new Name("ModPoint"));

    Sequence defX = new Sequence();
    defX.add(new Name("def"));
    defX.add(xName);
    defX.add(numFour);

    Sequence isaExpr = new Sequence();
    isaExpr.add(xName);
    isaExpr.add(new Name("isa"));
    isaExpr.add(new Name("Point"));

    Sequence system = new Sequence();
    system.add(module);
    system.add(importStmt);
    system.add(defX);
    system.add(isaExpr);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testFieldAccessWithUndefinedVariable() {
    // obj --> x, but obj not defined
    Sequence fieldAccess = new Sequence();
    fieldAccess.add(new Name("obj"));
    fieldAccess.add(new Name("-->"));
    fieldAccess.add(xName);

    Sequence system = new Sequence();
    system.add(emptyModule);
    system.add(fieldAccess);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testFieldAccessWithDefinedVariable() {
    Sequence fields = new Sequence();
    fields.add(xName);
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence importStmt = new Sequence();
    importStmt.add(new Name("import"));
    importStmt.add(new Name("ModPoint"));

    Sequence defVal = new Sequence();
    defVal.add(new Name("def"));
    defVal.add(new Name("val"));
    defVal.add(numFour);

    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence defObj = new Sequence();
    defObj.add(new Name("def"));
    defObj.add(new Name("obj"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);
    defObj.add(newExpr);

    Sequence fieldAccess = new Sequence();
    fieldAccess.add(new Name("obj"));
    fieldAccess.add(new Name("-->"));
    fieldAccess.add(xName);

    Sequence system = new Sequence();
    system.add(module);
    system.add(importStmt);
    system.add(defVal);
    system.add(defObj);
    system.add(fieldAccess);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testMethodCallWithUndefinedObject() {
    // obj --> m (), but obj not defined
    Sequence args = new Sequence();
    Sequence methodCall = new Sequence();
    methodCall.add(new Name("obj"));
    methodCall.add(new Name("-->"));
    methodCall.add(new Name("m"));
    methodCall.add(args);

    Sequence system = new Sequence();
    system.add(emptyModule);
    system.add(methodCall);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testMethodCallWithUndefinedArgument() {
    // obj --> m (y), but y not defined
    Sequence fields = new Sequence();
    fields.add(xName);
    Sequence params = new Sequence();
    params.add(new Name("param"));
    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("m"));
    method.add(params);
    method.add(new Name("param"));
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);
    classSeq.add(method);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence importStmt = new Sequence();
    importStmt.add(new Name("import"));
    importStmt.add(new Name("ModPoint"));

    Sequence defVal = new Sequence();
    defVal.add(new Name("def"));
    defVal.add(new Name("val"));
    defVal.add(numFour);

    Sequence newArgs = new Sequence();
    newArgs.add(new Name("val"));
    Sequence defObj = new Sequence();
    defObj.add(new Name("def"));
    defObj.add(new Name("obj"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(newArgs);
    defObj.add(newExpr);

    Sequence callArgs = new Sequence();
    callArgs.add(yName); // y is undefined
    Sequence methodCall = new Sequence();
    methodCall.add(new Name("obj"));
    methodCall.add(new Name("-->"));
    methodCall.add(new Name("m"));
    methodCall.add(callArgs);

    Sequence system = new Sequence();
    system.add(module);
    system.add(importStmt);
    system.add(defVal);
    system.add(defObj);
    system.add(methodCall);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testMethodBodyWithUndefinedVariable() {
    // (method m () y) but y not defined in method
    Sequence params = new Sequence();
    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("m"));
    method.add(params);
    method.add(yName); // y is undefined

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
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testMethodBodyWithDefinedParameter() {
    // (method m (param) param) - param defined as parameter
    Sequence params = new Sequence();
    params.add(new Name("param"));
    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("m"));
    method.add(params);
    method.add(new Name("param"));

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
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testMethodBodyAccessingThis() {
    // (method m () (this --> x)) - this is implicit
    Sequence params = new Sequence();
    Sequence fieldAccess = new Sequence();
    fieldAccess.add(new Name("this"));
    fieldAccess.add(new Name("-->"));
    fieldAccess.add(xName);

    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("m"));
    method.add(params);
    method.add(fieldAccess);

    Sequence fields = new Sequence();
    fields.add(xName);
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);
    classSeq.add(method);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testFieldAssignmentWithUndefinedObject() {
    // obj --> x = y, but obj not defined
    Sequence defY = new Sequence();
    defY.add(new Name("def"));
    defY.add(yName);
    defY.add(numFour);

    Sequence fieldAssign = new Sequence();
    fieldAssign.add(new Name("obj"));
    fieldAssign.add(new Name("-->"));
    fieldAssign.add(xName);
    fieldAssign.add(new Name("="));
    fieldAssign.add(yName);

    Sequence system = new Sequence();
    system.add(emptyModule);
    system.add(defY);
    system.add(fieldAssign);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testFieldAssignmentWithUndefinedRHS() {
    // obj --> x = y, obj defined but y not defined
    Sequence fields = new Sequence();
    fields.add(xName);
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence importStmt = new Sequence();
    importStmt.add(new Name("import"));
    importStmt.add(new Name("ModPoint"));

    Sequence defVal = new Sequence();
    defVal.add(new Name("def"));
    defVal.add(new Name("val"));
    defVal.add(numFour);

    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence defObj = new Sequence();
    defObj.add(new Name("def"));
    defObj.add(new Name("obj"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);
    defObj.add(newExpr);

    Sequence fieldAssign = new Sequence();
    fieldAssign.add(new Name("obj"));
    fieldAssign.add(new Name("-->"));
    fieldAssign.add(xName);
    fieldAssign.add(new Name("="));
    fieldAssign.add(yName); // y is undefined

    Sequence system = new Sequence();
    system.add(module);
    system.add(importStmt);
    system.add(defVal);
    system.add(defObj);
    system.add(fieldAssign);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  // module tests

  @Test
  void testUndefinedModuleInImport() {
    // (import NonExistent) but NonExistent module not defined
    Sequence importStmt = new Sequence();
    importStmt.add(new Name("import"));
    importStmt.add(new Name("NonExistent"));

    Sequence system = new Sequence();
    system.add(importStmt);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testClassUsedWithoutImport() {
    // Module defines Point, but system doesn't import it
    Sequence fields = new Sequence();
    fields.add(xName);
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence defVal = new Sequence();
    defVal.add(new Name("def"));
    defVal.add(new Name("val"));
    defVal.add(numFour);

    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);

    Sequence system = new Sequence();
    system.add(module);
    system.add(defVal);
    system.add(newExpr);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testModuleImportWithinModule() {
    // ModB imports ModA and uses Point
    Sequence fieldsA = new Sequence();
    fieldsA.add(xName);
    Sequence classA = new Sequence();
    classA.add(new Name("class"));
    classA.add(new Name("Point"));
    classA.add(fieldsA);
    Sequence moduleA = new Sequence();
    moduleA.add(new Name("module"));
    moduleA.add(new Name("ModA"));
    moduleA.add(classA);

    // ModB imports ModA
    Sequence importA = new Sequence();
    importA.add(new Name("import"));
    importA.add(new Name("ModA"));

    // Class Line with method using Point
    Sequence params = new Sequence();
    Sequence fieldAccess = new Sequence();
    fieldAccess.add(new Name("this"));
    fieldAccess.add(new Name("-->"));
    fieldAccess.add(new Name("p"));
    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("getPoint"));
    method.add(params);
    method.add(fieldAccess);

    Sequence fieldsB = new Sequence();
    fieldsB.add(new Name("p"));
    Sequence classB = new Sequence();
    classB.add(new Name("class"));
    classB.add(new Name("Line"));
    classB.add(fieldsB);
    classB.add(method);

    Sequence moduleB = new Sequence();
    moduleB.add(new Name("module"));
    moduleB.add(new Name("ModB"));
    moduleB.add(importA);
    moduleB.add(classB);

    Sequence system = new Sequence();
    system.add(moduleA);
    system.add(moduleB);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testModuleImportUndefinedModule() {
    // ModB imports NonExistent
    Sequence importBad = new Sequence();
    importBad.add(new Name("import"));
    importBad.add(new Name("NonExistent"));

    Sequence fields = new Sequence();
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Line"));
    classSeq.add(fields);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModB"));
    module.add(importBad);
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }


  // import order tests

  @Test
  void testImportRefersToModuleDefinedLater() {
    // Import ModB before ModB is defined
    Sequence importB = new Sequence();
    importB.add(new Name("import"));
    importB.add(new Name("ModB"));

    Sequence fieldsA = new Sequence();
    Sequence classA = new Sequence();
    classA.add(new Name("class"));
    classA.add(new Name("Point"));
    classA.add(fieldsA);
    Sequence moduleA = new Sequence();
    moduleA.add(new Name("module"));
    moduleA.add(new Name("ModA"));
    moduleA.add(classA);

    Sequence fieldsB = new Sequence();
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
    system.add(importB);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testModuleImportRefersToLaterModule() {
    // ModA imports ModB, but ModB is defined after ModA
    Sequence importB = new Sequence();
    importB.add(new Name("import"));
    importB.add(new Name("ModB"));

    Sequence fieldsA = new Sequence();
    Sequence classA = new Sequence();
    classA.add(new Name("class"));
    classA.add(new Name("Point"));
    classA.add(fieldsA);
    Sequence moduleA = new Sequence();
    moduleA.add(new Name("module"));
    moduleA.add(new Name("ModA"));
    moduleA.add(importB);
    moduleA.add(classA);

    Sequence fieldsB = new Sequence();
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
    assertTrue(s.containsUndefinedVariables());
  }

// class visibility

  @Test
  void testUseClassInModuleButNotImportedToSystemBody() {
    Sequence fields = new Sequence();
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence args = new Sequence();
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);

    Sequence system = new Sequence();
    system.add(module);
    system.add(newExpr);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testUseClassInModuleAndImportedToSystemBody() {
    // Module defines Point, system imports and uses it
    Sequence fields = new Sequence();
    fields.add(xName);
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence importStmt = new Sequence();
    importStmt.add(new Name("import"));
    importStmt.add(new Name("ModPoint"));

    Sequence defVal = new Sequence();
    defVal.add(new Name("def"));
    defVal.add(new Name("val"));
    defVal.add(numFour);

    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);

    Sequence system = new Sequence();
    system.add(module);
    system.add(importStmt);
    system.add(defVal);
    system.add(newExpr);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testUseClassImportedInPreviousModuleButNotCurrent() {
    // ModA defines Point
    // ModB imports ModA (has access to Point)
    // ModC doesn't import ModA (no access to Point)
    Sequence fieldsA = new Sequence();
    fieldsA.add(xName);
    Sequence classA = new Sequence();
    classA.add(new Name("class"));
    classA.add(new Name("Point"));
    classA.add(fieldsA);
    Sequence moduleA = new Sequence();
    moduleA.add(new Name("module"));
    moduleA.add(new Name("ModA"));
    moduleA.add(classA);

    Sequence importA = new Sequence();
    importA.add(new Name("import"));
    importA.add(new Name("ModA"));
    Sequence fieldsB = new Sequence();
    Sequence classB = new Sequence();
    classB.add(new Name("class"));
    classB.add(new Name("Line"));
    classB.add(fieldsB);
    Sequence moduleB = new Sequence();
    moduleB.add(new Name("module"));
    moduleB.add(new Name("ModB"));
    moduleB.add(importA);
    moduleB.add(classB);

    Sequence params = new Sequence();
    params.add(new Name("val"));
    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newPoint = new Sequence();
    newPoint.add(new Name("new"));
    newPoint.add(new Name("Point"));
    newPoint.add(args);
    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("createPoint"));
    method.add(params);
    method.add(newPoint);

    Sequence fieldsC = new Sequence();
    Sequence classC = new Sequence();
    classC.add(new Name("class"));
    classC.add(new Name("Factory"));
    classC.add(fieldsC);
    classC.add(method);
    Sequence moduleC = new Sequence();
    moduleC.add(new Name("module"));
    moduleC.add(new Name("ModC"));
    moduleC.add(classC);

    Sequence system = new Sequence();
    system.add(moduleA);
    system.add(moduleB);
    system.add(moduleC);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }

// module closure

  @Test
  void testModuleNotClosedUndefinedVariableInMethod() {
    Sequence params = new Sequence();
    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("m"));
    method.add(params);
    method.add(yName);

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
    assertTrue(s.containsUndefinedVariables());
  }

  @Test
  void testModuleNotClosedUndefinedClassName() {
    // Module's method uses undefined class name
    Sequence params = new Sequence();
    params.add(new Name("val"));
    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);
    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("createPoint"));
    method.add(params);
    method.add(newExpr);

    Sequence fields = new Sequence();
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Factory"));
    classSeq.add(fields);
    classSeq.add(method);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModFactory"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertTrue(s.containsUndefinedVariables());
  }


  @Test
  void testUseClassDefinedInSameModuleInMethod() {
    // Module defines Point, method in Point creates another Point
    Sequence params = new Sequence();
    params.add(new Name("val"));
    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newPoint = new Sequence();
    newPoint.add(new Name("new"));
    newPoint.add(new Name("Point"));
    newPoint.add(args);
    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("clone"));
    method.add(params);
    method.add(newPoint);

    Sequence fields = new Sequence();
    fields.add(xName);
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);
    classSeq.add(method);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }

  @Test
  void testModuleClosedAllVariablesDefined() {
    // Module with method that uses parameter and this
    Sequence params = new Sequence();
    params.add(new Name("newX"));
    Sequence fieldAccess = new Sequence();
    fieldAccess.add(new Name("this"));
    fieldAccess.add(new Name("-->"));
    fieldAccess.add(xName);
    fieldAccess.add(new Name("="));
    fieldAccess.add(new Name("newX"));
    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("setX"));
    method.add(params);
    method.add(fieldAccess);
    method.add(new Name("newX"));

    Sequence fields = new Sequence();
    fields.add(xName);
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);
    classSeq.add(method);

    Sequence module = new Sequence();
    module.add(new Name("module"));
    module.add(new Name("ModPoint"));
    module.add(classSeq);

    Sequence system = new Sequence();
    system.add(module);
    system.add(numFour);
    System s = system.convertToSystemOrErrorNode();
    assertFalse(s.containsUndefinedVariables());
  }
}