package UnitTests;

import ast.*;
import ast.Number;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import systemComponents.System;
import static org.junit.jupiter.api.Assertions.*;

public class SystemParsingTests {

  ASTNodes wellFormedName;
  ASTNodes wellFormedNumber;
  Sequence statement;
  Sequence def;
  Sequence importStmt;
  Sequence module;
  Sequence validSystem;
  Sequence systemNoModules;
  Sequence invalidSystemMixed;
  Sequence onlyExpression;

  @BeforeEach
  void setUp() {
    wellFormedName = new Name("x");
    wellFormedNumber = new Number(4.0);
    Name defName = new Name("def");
    Name getsName = new Name("=");
    Name moduleName = new Name("module");
    Name importName = new Name("import");
    Name className = new Name("class");

    statement = new Sequence();
    statement.add(wellFormedName);
    statement.add(getsName);
    statement.add(wellFormedNumber);

    def = new Sequence();
    def.add(defName);
    def.add(wellFormedName);
    def.add(wellFormedNumber);

    // (import ModuleName)
    importStmt = new Sequence();
    importStmt.add(importName);
    importStmt.add(new Name("ModA"));

    // (module ModA (class Point (x)))
    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    Sequence classSeq = new Sequence();
    classSeq.add(className);
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    module = new Sequence();
    module.add(moduleName);
    module.add(new Name("ModA"));
    module.add(classSeq);

    // Valid system: module, import, def, stmt, expr
    validSystem = new Sequence();
    validSystem.add(module);
    validSystem.add(importStmt);
    validSystem.add(def);
    validSystem.add(statement);
    validSystem.add(wellFormedNumber);

    // System with no modules
    systemNoModules = new Sequence();
    systemNoModules.add(importStmt);
    systemNoModules.add(def);
    systemNoModules.add(statement);
    systemNoModules.add(wellFormedNumber);

    // Invalid: mixed order
    invalidSystemMixed = new Sequence();
    invalidSystemMixed.add(def);
    invalidSystemMixed.add(module);
    invalidSystemMixed.add(statement);
    invalidSystemMixed.add(wellFormedNumber);

    // Only expression
    onlyExpression = new Sequence();
    onlyExpression.add(wellFormedNumber);
  }

  @Test
  void testConvertValidSystem() {
    System system = validSystem.convertToSystemOrErrorNode();
    assertFalse(system.containsError());
  }

  @Test
  void testConvertSystemNoModules() {
    System system = systemNoModules.convertToSystemOrErrorNode();
    assertFalse(system.containsError());
  }

  @Test
  void testConvertSystemOnlyExpression() {
    System system = onlyExpression.convertToSystemOrErrorNode();
    assertFalse(system.containsError()); // Valid - minimal system
  }

  @Test
  void testConvertInvalidSystemModuleAfterDef() {
    System system = invalidSystemMixed.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testConvertSystemModuleAfterStatement() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(statement);
    invalidSystem.add(module);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testConvertSystemModuleAfterImport() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(importStmt);
    invalidSystem.add(module);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testConvertSystemImportAfterDef() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(def);
    invalidSystem.add(importStmt);
    invalidSystem.add(statement);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testConvertSystemImportAfterStatement() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(statement);
    invalidSystem.add(importStmt);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testConvertSystemDefAfterStatement() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(importStmt);
    invalidSystem.add(statement);
    invalidSystem.add(def);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testConvertSystemStatementAfterExpression() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(importStmt);
    invalidSystem.add(def);
    invalidSystem.add(wellFormedNumber);
    invalidSystem.add(statement);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testConvertSystemMultipleModules() {
    Sequence fields1 = new Sequence();
    fields1.add(new Name("x"));
    Sequence class1 = new Sequence();
    class1.add(new Name("class"));
    class1.add(new Name("Point"));
    class1.add(fields1);
    Sequence module1 = new Sequence();
    module1.add(new Name("module"));
    module1.add(new Name("ModA"));
    module1.add(class1);

    Sequence fields2 = new Sequence();
    fields2.add(new Name("y"));
    Sequence class2 = new Sequence();
    class2.add(new Name("class"));
    class2.add(new Name("Line"));
    class2.add(fields2);
    Sequence module2 = new Sequence();
    module2.add(new Name("module"));
    module2.add(new Name("ModB"));
    module2.add(class2);

    Sequence validSystem = new Sequence();
    validSystem.add(module1);
    validSystem.add(module2);
    validSystem.add(importStmt);
    validSystem.add(def);
    validSystem.add(statement);
    validSystem.add(wellFormedNumber);

    System system = validSystem.convertToSystemOrErrorNode();
    assertFalse(system.containsError());
  }

  @Test
  void testConvertSystemModuleWithImports() {
    // (module ModB (import ModA) (class Line (a b)))
    Sequence fields = new Sequence();
    fields.add(new Name("a"));
    fields.add(new Name("b"));
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Line"));
    classSeq.add(fields);

    Sequence moduleWithImport = new Sequence();
    moduleWithImport.add(new Name("module"));
    moduleWithImport.add(new Name("ModB"));
    moduleWithImport.add(importStmt);
    moduleWithImport.add(classSeq);

    Sequence validSystem = new Sequence();
    validSystem.add(module);
    validSystem.add(moduleWithImport);
    validSystem.add(wellFormedNumber);

    System system = validSystem.convertToSystemOrErrorNode();
    assertFalse(system.containsError());
  }

  @Test
  void testConvertSystemMultipleExpressions() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(def);
    invalidSystem.add(statement);
    invalidSystem.add(wellFormedNumber);
    invalidSystem.add(new Number(5.0));

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testConvertSystemComplexInvalidOrder() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(statement);
    invalidSystem.add(importStmt);
    invalidSystem.add(def);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testConvertSystemOnlyModules() {
    Sequence onlyModules = new Sequence();
    onlyModules.add(module);

    System system = onlyModules.convertToSystemOrErrorNode();
    assertTrue(system.containsError()); // Missing expression
  }

  @Test
  void testConvertSystemModuleAndExpression() {
    Sequence validSystem = new Sequence();
    validSystem.add(module);
    validSystem.add(wellFormedNumber);

    System system = validSystem.convertToSystemOrErrorNode();
    assertFalse(system.containsError());
  }

  @Test
  void testConvertEmptySequenceToSystem() {
    Sequence emptySequence = new Sequence();
    System system = emptySequence.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testConvertNameToSystem() {
    System system = wellFormedName.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testConvertNumberToSystem() {
    System system = wellFormedNumber.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  // modules

  @Test
  void testModuleWithNoClass() {
    Sequence invalidModule = new Sequence();
    invalidModule.add(new Name("module"));
    invalidModule.add(new Name("ModA"));

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(invalidModule);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testModuleWithMultipleClasses() {
    Sequence fields1 = new Sequence();
    Sequence class1 = new Sequence();
    class1.add(new Name("class"));
    class1.add(new Name("Point"));
    class1.add(fields1);

    Sequence fields2 = new Sequence();
    Sequence class2 = new Sequence();
    class2.add(new Name("class"));
    class2.add(new Name("Line"));
    class2.add(fields2);

    Sequence invalidModule = new Sequence();
    invalidModule.add(new Name("module"));
    invalidModule.add(new Name("ModA"));
    invalidModule.add(class1);
    invalidModule.add(class2);

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(invalidModule);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testModuleWithNoImports() {
    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence validModule = new Sequence();
    validModule.add(new Name("module"));
    validModule.add(new Name("ModA"));
    validModule.add(classSeq);

    Sequence validSystem = new Sequence();
    validSystem.add(validModule);
    validSystem.add(wellFormedNumber);

    System system = validSystem.convertToSystemOrErrorNode();
    assertFalse(system.containsError());
  }

  @Test
  void testModuleWithMultipleImports() {
    Sequence import1 = new Sequence();
    import1.add(new Name("import"));
    import1.add(new Name("ModA"));

    Sequence import2 = new Sequence();
    import2.add(new Name("import"));
    import2.add(new Name("ModB"));

    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence validModule = new Sequence();
    validModule.add(new Name("module"));
    validModule.add(new Name("ModC"));
    validModule.add(import1);
    validModule.add(import2);
    validModule.add(classSeq);

    Sequence validSystem = new Sequence();
    validSystem.add(module);
    validSystem.add(validModule);
    validSystem.add(wellFormedNumber);

    System system = validSystem.convertToSystemOrErrorNode();
    assertFalse(system.containsError());
  }

  @Test
  void testModuleNameIsKeyword() {
    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence invalidModule = new Sequence();
    invalidModule.add(new Name("module"));
    invalidModule.add(new Name("def")); // Keyword as module name
    invalidModule.add(classSeq);

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(invalidModule);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testModuleWithMalformedImport() {
    Sequence badImport = new Sequence();
    badImport.add(new Name("import"));
    badImport.add(new Number(5.0));

    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence invalidModule = new Sequence();
    invalidModule.add(new Name("module"));
    invalidModule.add(new Name("ModA"));
    invalidModule.add(badImport);
    invalidModule.add(classSeq);

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(invalidModule);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testModuleWithMalformedClass() {
    Sequence badClass = new Sequence();
    badClass.add(new Name("class"));
    badClass.add(new Name("Point"));
    // Missing field list

    Sequence invalidModule = new Sequence();
    invalidModule.add(new Name("module"));
    invalidModule.add(new Name("ModA"));
    invalidModule.add(badClass);

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(invalidModule);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testModuleWithImportThenClassThenImport() {
    Sequence import1 = new Sequence();
    import1.add(new Name("import"));
    import1.add(new Name("ModA"));

    Sequence import2 = new Sequence();
    import2.add(new Name("import"));
    import2.add(new Name("ModB"));

    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence invalidModule = new Sequence();
    invalidModule.add(new Name("module"));
    invalidModule.add(new Name("ModC"));
    invalidModule.add(import1);
    invalidModule.add(classSeq);
    invalidModule.add(import2);

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(invalidModule);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testModuleWithNoModuleKeyword() {
    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Point"));
    classSeq.add(fields);

    Sequence invalidModule = new Sequence();
    invalidModule.add(new Name("ModA")); // No "module" keyword
    invalidModule.add(classSeq);

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(invalidModule);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

// imports

  @Test
  void testImportWithNoModuleName() {
    Sequence badImport = new Sequence();
    badImport.add(new Name("import"));

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(badImport);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testImportWithMultipleModuleNames() {
    Sequence badImport = new Sequence();
    badImport.add(new Name("import"));
    badImport.add(new Name("ModA"));
    badImport.add(new Name("ModB"));

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(badImport);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testImportNotWrappedInParens() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(new Name("import"));
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testImportNameIsKeyword() {
    Sequence badImport = new Sequence();
    badImport.add(new Name("import"));
    badImport.add(new Name("while0"));

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(badImport);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testImportWithNumberInsteadOfName() {
    Sequence badImport = new Sequence();
    badImport.add(new Name("import"));
    badImport.add(new Number(5.0));

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(badImport);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testImportNoKeyword() {
    Sequence badImport = new Sequence();
    badImport.add(new Name("ModA")); // No "import" keyword

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(badImport);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

// system structure

  @Test
  void testSystemModulesAndImportsNoExpression() {
    Sequence validSystem = new Sequence();
    validSystem.add(module);
    validSystem.add(importStmt);

    System system = validSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testSystemMultipleImportsInBody() {
    Sequence import1 = new Sequence();
    import1.add(new Name("import"));
    import1.add(new Name("ModA"));

    Sequence import2 = new Sequence();
    import2.add(new Name("import"));
    import2.add(new Name("ModA"));

    Sequence validSystem = new Sequence();
    validSystem.add(module);
    validSystem.add(import1);
    validSystem.add(import2);
    validSystem.add(wellFormedNumber);

    System system = validSystem.convertToSystemOrErrorNode();
    assertFalse(system.containsError());
  }

  @Test
  void testDuplicateImportStatements() {
    Sequence import1 = new Sequence();
    import1.add(new Name("import"));
    import1.add(new Name("ModA"));

    Sequence import2 = new Sequence();
    import2.add(new Name("import"));
    import2.add(new Name("ModA"));

    Sequence validSystem = new Sequence();
    validSystem.add(module);
    validSystem.add(import1);
    validSystem.add(import2);
    validSystem.add(def);
    validSystem.add(wellFormedNumber);

    System system = validSystem.convertToSystemOrErrorNode();
    assertFalse(system.containsError());
  }


// bad orders

  @Test
  void testModuleAfterExpression() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(wellFormedNumber);
    invalidSystem.add(module);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testImportAfterExpression() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(wellFormedNumber);
    invalidSystem.add(importStmt);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testMixedModulesAndImports() {
    Sequence import1 = new Sequence();
    import1.add(new Name("import"));
    import1.add(new Name("ModA"));

    Sequence fields = new Sequence();
    fields.add(new Name("x"));
    Sequence classSeq = new Sequence();
    classSeq.add(new Name("class"));
    classSeq.add(new Name("Line"));
    classSeq.add(fields);
    Sequence module2 = new Sequence();
    module2.add(new Name("module"));
    module2.add(new Name("ModB"));
    module2.add(classSeq);

    Sequence invalidSystem = new Sequence();
    invalidSystem.add(module);
    invalidSystem.add(import1);
    invalidSystem.add(module2); // Module after import - INVALID
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

  @Test
  void testSystemAllPossibleComponents() {
    Sequence validSystem = new Sequence();
    validSystem.add(module);
    validSystem.add(importStmt);
    validSystem.add(def);
    validSystem.add(statement);
    validSystem.add(wellFormedNumber);

    System system = validSystem.convertToSystemOrErrorNode();
    assertFalse(system.containsError());
  }

  @Test
  void testSystemModuleAfterDef() {
    Sequence invalidSystem = new Sequence();
    invalidSystem.add(def);
    invalidSystem.add(module);
    invalidSystem.add(wellFormedNumber);

    System system = invalidSystem.convertToSystemOrErrorNode();
    assertTrue(system.containsError());
  }

}