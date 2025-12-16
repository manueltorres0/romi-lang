package UnitTests;

import ast.*;
import ast.Number;
import expressions.*;
import declarations.*;
import statements.*;
import expressions.NewClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import systemComponents.System;
import programs.Program;
import static org.junit.jupiter.api.Assertions.*;

public class LinkerUnitTests {

  Name xName;
  Name yName;
  ASTNodes numFour;
  ASTNodes numFive;

  Sequence modulePointWithX;  // ModPoint with Point(x)
  Sequence moduleLineWithY;   // ModLine with Line(y)
  Sequence modulePointA;      // ModA with Point(x)
  Sequence modulePointB;      // ModB with Point(y)

  // Reusable imports
  Sequence importModPoint;
  Sequence importModLine;
  Sequence importModA;
  Sequence importModB;

  // Reusable defs
  Sequence defVal;
  Sequence defObj;

  @BeforeEach
  void setUp() {
    xName = new Name("x");
    yName = new Name("y");
    numFour = new Number(4.0);
    numFive = new Number(5.0);

    // Create (module ModPoint (class Point (x)))
    Sequence fieldsPoint = new Sequence();
    fieldsPoint.add(xName);
    Sequence classPoint = new Sequence();
    classPoint.add(new Name("class"));
    classPoint.add(new Name("Point"));
    classPoint.add(fieldsPoint);
    modulePointWithX = new Sequence();
    modulePointWithX.add(new Name("module"));
    modulePointWithX.add(new Name("ModPoint"));
    modulePointWithX.add(classPoint);

    // Create (module ModLine (class Line (y)))
    Sequence fieldsLine = new Sequence();
    fieldsLine.add(yName);
    Sequence classLine = new Sequence();
    classLine.add(new Name("class"));
    classLine.add(new Name("Line"));
    classLine.add(fieldsLine);
    moduleLineWithY = new Sequence();
    moduleLineWithY.add(new Name("module"));
    moduleLineWithY.add(new Name("ModLine"));
    moduleLineWithY.add(classLine);

    // Create (module ModA (class Point (x)))
    Sequence fieldsA = new Sequence();
    fieldsA.add(xName);
    Sequence classA = new Sequence();
    classA.add(new Name("class"));
    classA.add(new Name("Point"));
    classA.add(fieldsA);
    modulePointA = new Sequence();
    modulePointA.add(new Name("module"));
    modulePointA.add(new Name("ModA"));
    modulePointA.add(classA);

    // Create (module ModB (class Point (y)))
    Sequence fieldsB = new Sequence();
    fieldsB.add(yName);
    Sequence classB = new Sequence();
    classB.add(new Name("class"));
    classB.add(new Name("Point"));
    classB.add(fieldsB);
    modulePointB = new Sequence();
    modulePointB.add(new Name("module"));
    modulePointB.add(new Name("ModB"));
    modulePointB.add(classB);

    // Create imports
    importModPoint = new Sequence();
    importModPoint.add(new Name("import"));
    importModPoint.add(new Name("ModPoint"));

    importModLine = new Sequence();
    importModLine.add(new Name("import"));
    importModLine.add(new Name("ModLine"));

    importModA = new Sequence();
    importModA.add(new Name("import"));
    importModA.add(new Name("ModA"));

    importModB = new Sequence();
    importModB.add(new Name("import"));
    importModB.add(new Name("ModB"));

    // Create common defs
    defVal = new Sequence();
    defVal.add(new Name("def"));
    defVal.add(new Name("val"));
    defVal.add(numFour);

    defObj = new Sequence();
    defObj.add(new Name("def"));
    defObj.add(new Name("obj"));
    defObj.add(numFour);
  }

  @Test
  void testSingleModuleClassNameQualified() {
    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointWithX);
    systemSeq.add(numFour);

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    assertEquals(1, p.classes.size());
    assertEquals("ModPoint.Point", p.classes.get(0).getClassName().toString());
  }

  @Test
  void testMultipleModulesAllClassesRenamed() {
    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointA);
    systemSeq.add(moduleLineWithY);
    systemSeq.add(numFour);

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    assertEquals(2, p.classes.size());
    assertEquals("ModA.Point", p.classes.get(0).getClassName().toString());
    assertEquals("ModLine.Line", p.classes.get(1).getClassName().toString());
  }

  @Test
  void testTwoModulesSameClassNameUniqueQualified() {
    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointA);
    systemSeq.add(modulePointB);
    systemSeq.add(numFour);

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    assertEquals(2, p.classes.size());
    assertEquals("ModA.Point", p.classes.get(0).getClassName().toString());
    assertEquals("ModB.Point", p.classes.get(1).getClassName().toString());
  }

  // block renaming

  @Test
  void testNewExpressionInBlockFinalExpressionRenamed() {
    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);

    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointWithX);
    systemSeq.add(importModPoint);
    systemSeq.add(defVal);
    systemSeq.add(newExpr);

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    IExpression finalExpr = p.block.expression.get();
    NewClass nc = (NewClass) finalExpr;
    assertEquals("ModPoint.Point", nc.className.toString());
  }

  @Test
  void testIsaExpressionInBlockFinalExpressionRenamed() {
    Sequence isaExpr = new Sequence();
    isaExpr.add(new Name("obj"));
    isaExpr.add(new Name("isa"));
    isaExpr.add(new Name("Point"));

    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointWithX);
    systemSeq.add(importModPoint);
    systemSeq.add(defObj);
    systemSeq.add(isaExpr);

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    IExpression finalExpr = p.block.expression.get();
    assertTrue(finalExpr instanceof InstanceOf);
    InstanceOf io = (InstanceOf) finalExpr;
    assertEquals("ModPoint.Point", io.className.toString());
  }

  @Test
  void testNewExpressionInDeclarationRenamed() {
    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);

    Sequence defP = new Sequence();
    defP.add(new Name("def"));
    defP.add(new Name("p"));
    defP.add(newExpr);

    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointWithX);
    systemSeq.add(importModPoint);
    systemSeq.add(defVal);
    systemSeq.add(defP);
    systemSeq.add(new Name("p"));

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    Declaration decl = (Declaration) p.block.declarations.get(1);
    assertTrue(decl.expression instanceof NewClass);
    NewClass nc = (NewClass) decl.expression;
    assertEquals("ModPoint.Point", nc.className.toString());
  }

  @Test
  void testIsaInDeclarationRenamed() {
    Sequence isaExpr = new Sequence();
    isaExpr.add(new Name("obj"));
    isaExpr.add(new Name("isa"));
    isaExpr.add(new Name("Point"));

    Sequence defResult = new Sequence();
    defResult.add(new Name("def"));
    defResult.add(new Name("result"));
    defResult.add(isaExpr);

    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointWithX);
    systemSeq.add(importModPoint);
    systemSeq.add(defObj);
    systemSeq.add(defResult);
    systemSeq.add(new Name("result"));

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    Declaration decl = (Declaration) p.block.declarations.get(1);
    InstanceOf io = (InstanceOf) decl.expression;
    assertEquals("ModPoint.Point", io.className.toString());
  }

  @Test
  void testNewInAssignmentStatementRenamed() {
    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);

    Sequence assignment = new Sequence();
    assignment.add(new Name("obj"));
    assignment.add(new Name("="));
    assignment.add(newExpr);

    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointWithX);
    systemSeq.add(importModPoint);
    systemSeq.add(defObj);
    systemSeq.add(defVal);
    systemSeq.add(assignment);
    systemSeq.add(new Name("obj"));

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    AssignmentStatement stmt = (AssignmentStatement) p.block.stmts.getFirst();
    NewClass nc = (NewClass) stmt.expression;
    assertEquals("ModPoint.Point", nc.className.toString());
  }

  @Test
  void testNewInFieldAssignmentRenamed() {
    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);

    Sequence fieldAssign = new Sequence();
    fieldAssign.add(new Name("obj"));
    fieldAssign.add(new Name("-->"));
    fieldAssign.add(xName);
    fieldAssign.add(new Name("="));
    fieldAssign.add(newExpr);

    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointWithX);
    systemSeq.add(importModPoint);
    systemSeq.add(defVal);
    systemSeq.add(defObj);
    systemSeq.add(fieldAssign);
    systemSeq.add(new Name("obj"));

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    FieldAssignment stmt = (FieldAssignment) p.block.stmts.getFirst();
    NewClass nc = (NewClass) stmt.expression;
    assertEquals("ModPoint.Point", nc.className.toString());
  }

  @Test
  void testIsaInIfConditionRenamed() {
    Sequence isaExpr = new Sequence();
    isaExpr.add(new Name("obj"));
    isaExpr.add(new Name("isa"));
    isaExpr.add(new Name("Point"));

    Sequence defResult = new Sequence();
    defResult.add(new Name("def"));
    defResult.add(new Name("result"));
    defResult.add(numFour);

    Sequence thenStmt = new Sequence();
    thenStmt.add(new Name("result"));
    thenStmt.add(new Name("="));
    thenStmt.add(numFive);

    Sequence elseStmt = new Sequence();
    elseStmt.add(new Name("result"));
    elseStmt.add(new Name("="));
    elseStmt.add(numFour);

    Sequence ifStmt = new Sequence();
    ifStmt.add(new Name("if0"));
    ifStmt.add(isaExpr);
    ifStmt.add(thenStmt);
    ifStmt.add(elseStmt);

    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointWithX);
    systemSeq.add(importModPoint);
    systemSeq.add(defObj);
    systemSeq.add(defResult);
    systemSeq.add(ifStmt);
    systemSeq.add(new Name("result"));

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    IfStatement stmt = (IfStatement) p.block.stmts.get(0);
    InstanceOf io = (InstanceOf) stmt.expression;
    assertEquals("ModPoint.Point", io.className.toString());
  }

  @Test
  void testMultipleNewExpressionsAllRenamed() {
    Sequence argsP = new Sequence();
    argsP.add(new Name("val"));
    Sequence newPoint = new Sequence();
    newPoint.add(new Name("new"));
    newPoint.add(new Name("Point"));
    newPoint.add(argsP);
    Sequence defP = new Sequence();
    defP.add(new Name("def"));
    defP.add(new Name("p"));
    defP.add(newPoint);

    Sequence argsL = new Sequence();
    argsL.add(new Name("val"));
    Sequence newLine = new Sequence();
    newLine.add(new Name("new"));
    newLine.add(new Name("Line"));
    newLine.add(argsL);
    Sequence defL = new Sequence();
    defL.add(new Name("def"));
    defL.add(new Name("l"));
    defL.add(newLine);

    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointWithX);
    systemSeq.add(moduleLineWithY);
    systemSeq.add(importModPoint);
    systemSeq.add(importModLine);
    systemSeq.add(defVal);
    systemSeq.add(defP);
    systemSeq.add(defL);
    systemSeq.add(new Name("p"));

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    Declaration declP = (Declaration) p.block.declarations.get(1);
    NewClass ncP = (NewClass) declP.expression;
    assertEquals("ModPoint.Point", ncP.className.toString());

    Declaration declL = (Declaration) p.block.declarations.get(2);
    NewClass ncL = (NewClass) declL.expression;
    assertEquals("ModLine.Line", ncL.className.toString());
  }

  //  naming resolution thing

  @Test
  void testLastImportWinsInBlockRenaming() {
    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point"));
    newExpr.add(args);

    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointA);
    systemSeq.add(modulePointB);
    systemSeq.add(importModA);
    systemSeq.add(importModB);
    systemSeq.add(defVal);
    systemSeq.add(newExpr);

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    IExpression finalExpr = p.block.expression.get();
    NewClass nc = (NewClass) finalExpr;
    assertEquals("ModB.Point", nc.className.toString());
  }

  @Test
  void testImportOrderMattersReversed() {
    Sequence args = new Sequence();
    args.add(new Name("val"));
    Sequence newExpr = new Sequence();
    newExpr.add(new Name("new"));
    newExpr.add(new Name("Point")); // Should resolve to ModA
    newExpr.add(args);

    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointA);
    systemSeq.add(modulePointB);
    systemSeq.add(importModB);
    systemSeq.add(importModA); // Last
    systemSeq.add(defVal);
    systemSeq.add(newExpr);

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    IExpression finalExpr = p.block.expression.get();
    NewClass nc = (NewClass) finalExpr;
    assertEquals("ModA.Point", nc.className.toString()); // Last import wins
  }


  @Test
  void testOwnClassDefinitionNeverShadowedByImport() {
    Sequence importA = new Sequence();
    importA.add(new Name("import"));
    importA.add(new Name("ModA"));

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
    method.add(new Name("create"));
    method.add(params);
    method.add(newPoint);

    Sequence fieldsB = new Sequence();
    fieldsB.add(yName);
    Sequence classB = new Sequence();
    classB.add(new Name("class"));
    classB.add(new Name("Point"));
    classB.add(fieldsB);
    classB.add(method);

    Sequence moduleB = new Sequence();
    moduleB.add(new Name("module"));
    moduleB.add(new Name("ModB"));
    moduleB.add(importA);
    moduleB.add(classB);

    Sequence systemSeq = new Sequence();
    systemSeq.add(modulePointA);
    systemSeq.add(moduleB);
    systemSeq.add(numFour);

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    assertEquals("ModB.Point", p.classes.get(1).getClassName().toString());

  }

  @Test
  void testNoModulesEmptyClassList() {
    Sequence systemSeq = new Sequence();
    systemSeq.add(numFour);

    System s = systemSeq.convertToSystemOrErrorNode();
    Program p = s.linkModules();

    assertEquals(0, p.classes.size());
  }

}