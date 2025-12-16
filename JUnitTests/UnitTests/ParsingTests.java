package UnitTests;

import ast.*;
import ast.Number;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

public class ParsingTests {

  ASTNodes wellFormedName;
  ASTNodes wellFormedNumber;
  Sequence statement;
  Sequence def;
  Sequence invalidDefTooMany;
  Sequence invalidDefTooLittle;
  Sequence validBlockNoDefs;
  Sequence invalidBlockNoStatements;
  Sequence invalidBlockDefsButNoStatements;

  @BeforeEach
  void setUp() {
    wellFormedName = new Name("x");
    wellFormedNumber = new Number(4.0);
    Name defName = new Name("def");
    Name blockName = new Name("block");
    Name getsName = new Name("=");

    statement = new Sequence();
    statement.add(wellFormedName);
    statement.add(getsName);
    statement.add(wellFormedNumber);

    def = new Sequence();
    def.add(defName);
    def.add(wellFormedName);
    def.add(wellFormedNumber);

    invalidDefTooMany = new Sequence();
    invalidDefTooMany.add(defName);
    invalidDefTooMany.add(wellFormedName);
    invalidDefTooMany.add(wellFormedNumber);
    invalidDefTooMany.add(wellFormedNumber);

    invalidDefTooLittle = new Sequence();
    invalidDefTooLittle.add(defName);
    invalidDefTooLittle.add(wellFormedName);

    validBlockNoDefs = new Sequence();
    validBlockNoDefs.add(blockName);
    validBlockNoDefs.add(statement);

    invalidBlockNoStatements = new Sequence();
    invalidBlockNoStatements.add(blockName);

    invalidBlockDefsButNoStatements = new Sequence();
    invalidBlockDefsButNoStatements.add(blockName);
    invalidBlockDefsButNoStatements.add(def);
  }

  // isDeclaration() tests
  @Test
  void testIsDeclarationValidDef() {
    assertTrue(def.isDeclaration());
  }

  @Test
  void testIsDeclarationNotDef() {
    assertFalse(statement.isDeclaration());
  }

  @Test
  void testIsDeclarationOnNonSequence() {
    assertFalse(wellFormedName.isDeclaration());
  }

  // convertToDeclarationOrError() tests
  @Test
  void testConvertValidDeclaration() {
    AtomicBoolean valid = new AtomicBoolean(true);
    def.convertToDeclarationOrError(valid);
    assertTrue(valid.get());
    // CHECK
  }

  @Test
  void testConvertInvalidDeclarationTooMany() {
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidDefTooMany.convertToDeclarationOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertInvalidDeclarationTooFew() {
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidDefTooLittle.convertToDeclarationOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertNonDeclarationSequence() {
    AtomicBoolean valid = new AtomicBoolean(true);
    statement.convertToDeclarationOrError(valid);
    assertFalse(valid.get());
  }

  // convertToBlockOrError() tests
  @Test
  void testConvertValidBlock() {
    AtomicBoolean valid = new AtomicBoolean(true);
    validBlockNoDefs.convertToBlockOrError(valid);
    assertTrue(valid.get());
  }

  @Test
  void testConvertStatementAsBlock() {
    AtomicBoolean valid = new AtomicBoolean(true);
    statement.convertToBlockOrError(valid);
    assertTrue(valid.get());
  }

  @Test
  void testConvertEmptyBlock() {
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidBlockNoStatements.convertToBlockOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertBlockWithOnlyDefs() {
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidBlockDefsButNoStatements.convertToBlockOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertNonBlockNonStatement() {
    AtomicBoolean valid = new AtomicBoolean(true);
    wellFormedName.convertToBlockOrError(valid);
    assertFalse(valid.get());
  }

  //- ---- method parsing

  // Method construction tests
  Sequence validMethod;
  Sequence invalidMethodTwoNames;
  Sequence invalidMethodNoKeyword;
  Sequence invalidMethodNoParensList;
  Sequence invalidMethodNoListofParams;
  Sequence invalidMethodIntertwinedDefsStmts;
  Sequence invalidMethodNoFinalExpr;
  Sequence invalidMethodDuplicateParams;
  Sequence invalidMethodTwoFinalExprs;

  void setUpMethodTests() {
    Name methodName = new Name("method");
    Name methodNameValue = new Name("getX");
    Name paramX = new Name("x");
    Name paramY = new Name("y");

    // Valid method: (method getX () x)
    Sequence emptyParams = new Sequence();
    validMethod = new Sequence();
    validMethod.add(methodName);
    validMethod.add(methodNameValue);
    validMethod.add(emptyParams);
    validMethod.add(wellFormedName);

    // Invalid: two method names (method getX getY () x)
    invalidMethodTwoNames = new Sequence();
    invalidMethodTwoNames.add(methodName);
    invalidMethodTwoNames.add(methodNameValue);
    invalidMethodTwoNames.add(new Name("getY"));
    invalidMethodTwoNames.add(emptyParams);
    invalidMethodTwoNames.add(wellFormedName);

    // Invalid: no method keyword (getX () x)
    invalidMethodNoKeyword = new Sequence();
    invalidMethodNoKeyword.add(methodNameValue);
    invalidMethodNoKeyword.add(emptyParams);
    invalidMethodNoKeyword.add(wellFormedName);

    // Invalid: no wrapping list for params (method getX x y body)
    invalidMethodNoParensList = new Sequence();
    invalidMethodNoParensList.add(methodName);
    invalidMethodNoParensList.add(methodNameValue);
    invalidMethodNoParensList.add(paramX);
    invalidMethodNoParensList.add(paramY);
    invalidMethodNoParensList.add(wellFormedName);

    // Invalid: no list of params (method getX body)
    invalidMethodNoListofParams = new Sequence();
    invalidMethodNoListofParams.add(methodName);
    invalidMethodNoListofParams.add(methodNameValue);
    invalidMethodNoListofParams.add(wellFormedName);

    // Invalid: intertwined defs and statements (method m () (def x 1) (y = 2) (def z 3) x)
    Sequence params = new Sequence();
    invalidMethodIntertwinedDefsStmts = new Sequence();
    invalidMethodIntertwinedDefsStmts.add(methodName);
    invalidMethodIntertwinedDefsStmts.add(methodNameValue);
    invalidMethodIntertwinedDefsStmts.add(params);
    invalidMethodIntertwinedDefsStmts.add(def);  // def
    invalidMethodIntertwinedDefsStmts.add(statement);  // statement
    invalidMethodIntertwinedDefsStmts.add(def);  // def again - invalid order
    invalidMethodIntertwinedDefsStmts.add(wellFormedName);  // final expr

    // Invalid: no final expression (method getX () (def x 1))
    invalidMethodNoFinalExpr = new Sequence();
    invalidMethodNoFinalExpr.add(methodName);
    invalidMethodNoFinalExpr.add(methodNameValue);
    invalidMethodNoFinalExpr.add(params);
    invalidMethodNoFinalExpr.add(def);

    // Invalid: duplicate parameter names (method m (x x) x)
    Sequence duplicateParams = new Sequence();
    duplicateParams.add(paramX);
    duplicateParams.add(paramX);
    invalidMethodDuplicateParams = new Sequence();
    invalidMethodDuplicateParams.add(methodName);
    invalidMethodDuplicateParams.add(methodNameValue);
    invalidMethodDuplicateParams.add(duplicateParams);
    invalidMethodDuplicateParams.add(wellFormedName);

    // Invalid: two final expressions (method getX () x y)
    invalidMethodTwoFinalExprs = new Sequence();
    invalidMethodTwoFinalExprs.add(methodName);
    invalidMethodTwoFinalExprs.add(methodNameValue);
    invalidMethodTwoFinalExprs.add(params);
    invalidMethodTwoFinalExprs.add(wellFormedName);
    invalidMethodTwoFinalExprs.add(new Name("y"));
  }

  @Test
  void testConvertValidMethod() {
    setUpMethodTests();
    AtomicBoolean valid = new AtomicBoolean(true);
    validMethod.convertToMethodOrError(valid);
    assertTrue(valid.get());
  }

  @Test
  void testConvertMethodTwoNames() {
    setUpMethodTests();
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidMethodTwoNames.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodNoKeyword() {
    setUpMethodTests();
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidMethodNoKeyword.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodNoListForParams() {
    setUpMethodTests();
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidMethodNoParensList.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodNoLisOfParams() {
    setUpMethodTests();
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidMethodNoListofParams.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodIntertwinedDefsStmts() {
    setUpMethodTests();
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidMethodIntertwinedDefsStmts.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodNoFinalExpr() {
    setUpMethodTests();
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidMethodNoFinalExpr.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodDuplicateParamsNoParserError() {
    setUpMethodTests();
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidMethodDuplicateParams.convertToMethodOrError(valid);
    assertTrue(valid.get());
  }

  @Test
  void testConvertMethodTwoFinalExpressions() {
    setUpMethodTests();
    AtomicBoolean valid = new AtomicBoolean(true);
    invalidMethodTwoFinalExprs.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }


  @Test
  void testConvertMethodParamsNotNames() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence params = new Sequence();
    params.add(new Number(5.0));
    params.add(new Name("x"));
    Sequence invalidMethod = new Sequence();
    invalidMethod.add(new Name("method"));
    invalidMethod.add(new Name("calc"));
    invalidMethod.add(params);
    invalidMethod.add(new Name("x"));
    invalidMethod.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodStatementsNoDefsNoExpr() {
    // (method m () (x = 5.0)) - has statement but no final expression
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence assignStmt = new Sequence();
    assignStmt.add(new Name("x"));
    assignStmt.add(new Name("="));
    assignStmt.add(new Number(5.0));
    Sequence invalidMethod = new Sequence();
    invalidMethod.add(new Name("method"));
    invalidMethod.add(new Name("m"));
    invalidMethod.add(new Sequence());
    invalidMethod.add(assignStmt);
    invalidMethod.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodNameIsKeyword() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidMethod = new Sequence();
    invalidMethod.add(new Name("method"));
    invalidMethod.add(new Name("class"));
    invalidMethod.add(new Sequence());
    invalidMethod.add(new Name("x"));
    invalidMethod.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodParamIsKeyword() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence params = new Sequence();
    params.add(new Name("method"));
    Sequence invalidMethod = new Sequence();
    invalidMethod.add(new Name("method"));
    invalidMethod.add(new Name("m"));
    invalidMethod.add(params);
    invalidMethod.add(new Name("x"));
    invalidMethod.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodEmpty() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidMethod = new Sequence();
    invalidMethod.add(new Name("method"));
    invalidMethod.add(new Name("m"));
    invalidMethod.add(new Sequence());
    invalidMethod.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodThisAsParameter() {
    // (method m (this) this) - "this" as parameter name
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence params = new Sequence();
    params.add(new Name("this"));
    Sequence method = new Sequence();
    method.add(new Name("method"));
    method.add(new Name("m"));
    method.add(params);
    method.add(new Name("this"));
    method.convertToMethodOrError(valid);
    assertTrue(valid.get());
  }

  @Test
  void testConvertMethodOnlyStatementsWithFinalExpr() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence assignStmt = new Sequence();
    assignStmt.add(new Name("x"));
    assignStmt.add(new Name("="));
    assignStmt.add(new Number(5.0));
    Sequence validMethod = new Sequence();
    validMethod.add(new Name("method"));
    validMethod.add(new Name("m"));
    validMethod.add(new Sequence());
    validMethod.add(assignStmt);
    validMethod.add(new Name("x"));
    validMethod.convertToMethodOrError(valid);
    assertTrue(valid.get());
  }

  @Test
  void testConvertClassNoFieldList() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidClass = new Sequence();
    invalidClass.add(new Name("class"));
    invalidClass.add(new Name("Point"));
    invalidClass.convertToClassOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertClassDuplicateFields() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    fields.add(new Name("x"));
    Sequence invalidClass = new Sequence();
    invalidClass.add(new Name("class"));
    invalidClass.add(new Name("Point"));
    invalidClass.add(fields);
    invalidClass.convertToClassOrError(valid);
    assertTrue(valid.get());
  }

  @Test
  void testConvertClassFieldsNotNames() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence fields = new Sequence();
    fields.add(new Number(5.0));
    fields.add(new Name("y"));
    Sequence invalidClass = new Sequence();
    invalidClass.add(new Name("class"));
    invalidClass.add(new Name("Point"));
    invalidClass.add(fields);
    invalidClass.convertToClassOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertClassNoMethods() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    fields.add(new Name("y"));
    Sequence validClass = new Sequence();
    validClass.add(new Name("class"));
    validClass.add(new Name("Point"));
    validClass.add(fields);
    validClass.convertToClassOrError(valid);
    assertTrue(valid.get());
  }

  @Test
  void testConvertClassNameIsKeyword() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    Sequence invalidClass = new Sequence();
    invalidClass.add(new Name("class"));
    invalidClass.add(new Name("new"));
    invalidClass.add(fields);
    invalidClass.convertToClassOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertClassClassKeywordMissing() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    Sequence invalidClass = new Sequence();
    invalidClass.add(new Name("classha"));
    invalidClass.add(new Name("Hello"));
    invalidClass.add(fields);
    invalidClass.convertToClassOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertClassFieldIsKeyword() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence fields = new Sequence();
    fields.add(new Name("method"));
    fields.add(new Name("y"));
    Sequence invalidClass = new Sequence();
    invalidClass.add(new Name("class"));
    invalidClass.add(new Name("Point"));
    invalidClass.add(fields);
    invalidClass.convertToClassOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertFieldAccessNonNameField() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidExpr = new Sequence();
    invalidExpr.add(new Name("obj"));
    invalidExpr.add(new Name("-->"));
    invalidExpr.add(new Number(5.0));
    invalidExpr.convertToExpressionOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodCallNonNameMethod() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidExpr = new Sequence();
    invalidExpr.add(new Name("obj"));
    invalidExpr.add(new Name("-->"));
    invalidExpr.add(new Number(5.0));
    invalidExpr.add(new Sequence());
    invalidExpr.convertToExpressionOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertNewNonNameClass() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidExpr = new Sequence();
    invalidExpr.add(new Name("new"));
    invalidExpr.add(new Number(5.0));
    invalidExpr.add(new Sequence());
    invalidExpr.convertToExpressionOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertNewNoParamList() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidExpr = new Sequence();
    invalidExpr.add(new Name("new"));
    invalidExpr.add(new Name("hello"));
    invalidExpr.convertToExpressionOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertNewParamsNoList() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidExpr = new Sequence();
    invalidExpr.add(new Name("new"));
    invalidExpr.add(new Name("hello"));
    invalidExpr.add(new Name("p1"));
    invalidExpr.add(new Name("p2"));
    invalidExpr.convertToExpressionOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertNewArgsNotNames() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence args = new Sequence();
    args.add(new Number(5.0));
    args.add(new Name("y"));
    Sequence invalidExpr = new Sequence();
    invalidExpr.add(new Name("new"));
    invalidExpr.add(new Name("Point"));
    invalidExpr.add(args);
    invalidExpr.convertToExpressionOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertIsaNonNameClass() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidExpr = new Sequence();
    invalidExpr.add(new Name("obj"));
    invalidExpr.add(new Name("isa"));
    invalidExpr.add(new Number(5.0));
    invalidExpr.convertToExpressionOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertMethodCallArgsNotNames() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence args = new Sequence();
    args.add(new Number(5.0));
    args.add(new Name("x"));
    Sequence invalidExpr = new Sequence();
    invalidExpr.add(new Name("obj"));
    invalidExpr.add(new Name("-->"));
    invalidExpr.add(new Name("m"));
    invalidExpr.add(args);
    invalidExpr.convertToExpressionOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertFieldAssignmentNonNameField() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidStmt = new Sequence();
    invalidStmt.add(new Name("obj"));
    invalidStmt.add(new Name("-->"));
    invalidStmt.add(new Number(5.0));
    invalidStmt.add(new Name("="));
    invalidStmt.add(new Name("x"));
    invalidStmt.convertToStmtOrError(valid);
    assertFalse(valid.get());
  }

  @Test
  void testConvertFieldAssignmentObjectNotName() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidStmt = new Sequence();
    invalidStmt.add(new Number(5.0));
    invalidStmt.add(new Name("-->"));
    invalidStmt.add(new Name("field"));
    invalidStmt.add(new Name("="));
    invalidStmt.add(new Name("x"));
    invalidStmt.convertToStmtOrError(valid);
    assertFalse(valid.get());
  }

  // Nested structures
  @Test
  void testConvertNestedMethods() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence innerMethod = new Sequence();
    innerMethod.add(new Name("method"));
    innerMethod.add(new Name("inner"));
    innerMethod.add(new Sequence());
    innerMethod.add(new Name("x"));

    Sequence outerMethod = new Sequence();
    outerMethod.add(new Name("method"));
    outerMethod.add(new Name("outer"));
    outerMethod.add(new Sequence());
    outerMethod.add(innerMethod);
    outerMethod.add(new Name("y"));

    outerMethod.convertToMethodOrError(valid);
    assertFalse(valid.get());
  }


  @Test
  void testConvertClassWithInvalidMethod() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence fields = new Sequence();
    fields.add(new Name("x"));

    Sequence invalidMethod = new Sequence();
    invalidMethod.add(new Name("method"));
    invalidMethod.add(new Name("m"));
    invalidMethod.add(new Sequence());

    Sequence invalidClass = new Sequence();
    invalidClass.add(new Name("class"));
    invalidClass.add(new Name("C"));
    invalidClass.add(fields);
    invalidClass.add(invalidMethod);

    invalidClass.convertToClassOrError(valid);
    assertFalse(valid.get());
  }


  @Test
  void testConvertMethodCallWithNoArgsList() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence ambiguousExpr = new Sequence();
    ambiguousExpr.add(new Name("obj"));
    ambiguousExpr.add(new Name("-->"));
    ambiguousExpr.add(new Name("method"));
    ambiguousExpr.convertToExpressionOrError(valid);
    assertFalse(valid.get());
  }


  @Test
  void testConvertIsaWithNoClassName() {
    AtomicBoolean valid = new AtomicBoolean(true);
    Sequence invalidExpr = new Sequence();
    invalidExpr.add(new Name("obj"));
    invalidExpr.add(new Name("isa"));
    invalidExpr.convertToExpressionOrError(valid);
    assertFalse(valid.get());
  }

}