package ast;

import Method.IMethod;
import Method.Method;
import block.IBlock;
import block.Block;
import declarations.Declaration;
import declarations.IDeclaration;
import error.ErrorNode;
import error.ErrorSystem;
import expressions.Addition;
import expressions.CallMethod;
import expressions.Division;
import expressions.Equality;
import expressions.IExpression;
import expressions.GetField;
import expressions.InstanceOf;
import expressions.NewClass;
import expressions.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import systemComponents.IModule;
import systemComponents.Import;
import systemComponents.InterfaceImport;
import systemComponents.TImport;
import systemComponents.TypedModule;
import systemComponents.System;
import statements.AssignmentStatement;
import statements.FieldAssignment;
import statements.IfStatement;
import statements.IStatement;
import statements.WhileStatement;
import Class.*;
import systemComponents.UntypedModule;
import types.FieldType;
import types.IFieldType;
import types.IMethodType;
import types.IShape;
import types.IType;
import types.MethodType;
import types.Shape;

public class Sequence extends AbstractNodes{
  private final ArrayList<ASTNodes> seq;

  public Sequence() {
    this.seq = new ArrayList<>();
  }

  public void add(ASTNodes element) {
    this.seq.add(element);
  }


  @Override
  public String toString() {
    String acc = "(";
    for (ASTNodes node : seq) {
      acc += " " + node.toString();
    }

    return acc + ")";

  }

  @Override
  public System convertToSystemOrErrorNode() {
    if (this.seq.isEmpty()) {
      return new ErrorSystem();
    } else {
      ASTNodes expression = seq.getLast();
      this.seq.removeLast();
      return new System(this.seq, expression);
    }
  }

  @Override
  public IStatement convertToStmtOrError(AtomicBoolean valid) {
    if (this.isAssignmentStatement()) {
      return this.convertToAssStatementOrError(valid);
    } else if (this.isIfStatement()) {
      return this.convertToIfStatementOrError(valid);
    } else if (this.isWhileStatement()) {
      return this.convertToWhileStatementOrError(valid);
    } else if (this.isFieldAssignment()) {
      return this.convertToFieldAssignmentOrError(valid);
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "The given arguments do not follow a statement pattern");
    }
  }

  private boolean isAssignmentStatement() {
    return this.seq.size() == 3 && this.seq.get(1).toString().equals("=");
  }

  private boolean isIfStatement() {
    return this.seq.size() == 4 && this.seq.get(0).toString().equals("if0");
  }

  private boolean isWhileStatement() {
    return this.seq.size() == 3 && this.seq.get(0).toString().equals("while0");
  }

  private boolean isFieldAssignment() {
    return this.seq.size() == 5 && this.seq.get(1).toString().equals("-->")
        && this.seq.get(3).toString().equals("=");
  }

  public boolean isStatement() {
    return this.isAssignmentStatement()
        || this.isIfStatement()
        || this.isWhileStatement()
        || this.isFieldAssignment();
  }


  @Override
  public IStatement convertToAssStatementOrError(AtomicBoolean valid) {
    return new AssignmentStatement(this.seq, valid);
  }

  @Override
  public IStatement convertToWhileStatementOrError(AtomicBoolean valid) {
    return new WhileStatement(this.seq, valid);
  }

  @Override
  public IStatement convertToIfStatementOrError(AtomicBoolean valid) {
    return new IfStatement(this.seq, valid);
  }

  @Override
  public IStatement convertToFieldAssignmentOrError(AtomicBoolean valid) {
    return new FieldAssignment(this.seq, valid);
  }

  @Override
  public IExpression convertToExpressionOrError(AtomicBoolean valid) {
    if (this.isAddition()) {
      return this.convertToAdditionOrError(valid);
    } else if (this.isDivision()) {
      return this.convertToDivisionOrError(valid);
    } else if (this.isEquality()) {
      return this.convertToEqualityOrError(valid);
    } else if (this.isNewClass()) {
      return this.convertToNewClassOrError(valid);
    } else if (this.isGetField()) {
      return this.convertToGetFieldOrError(valid);
    } else if (this.isCallMethod()) {
      return this.convertToCallMethodOrError(valid);
    } else if (this.isInstanceOf()) {
      return this.convertToInstanceOfOrError(valid);
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "An expression does not follow this pattern");
    }
  }


  private boolean isAddition() {
    return this.seq.size() == 3 && this.seq.get(1).toString().equals("+");
  }

  private boolean isDivision() {
    return this.seq.size() == 3 && this.seq.get(1).toString().equals("/");
  }

  private boolean isEquality() {
    return this.seq.size() == 3 && this.seq.get(1).toString().equals("==");
  }

  private boolean isNewClass() {
    return this.seq.size() == 3 && this.seq.get(0).toString().equals("new");
  }

  private boolean isGetField() {
    return this.seq.size() == 3 && this.seq.get(1).toString().equals("-->");
  }

  private boolean isCallMethod() {
    return this.seq.size() == 4 && this.seq.get(1).toString().equals("-->");
  }

  private boolean isInstanceOf() {
    return this.seq.size() == 3 && this.seq.get(1).toString().equals("isa");
  }


  public IExpression convertToAdditionOrError(AtomicBoolean valid) {
    return new Addition(this.seq.get(0), this.seq.get(2), valid);
  }

  @Override
  public IExpression convertToDivisionOrError(AtomicBoolean valid) {
    return new Division(this.seq.get(0), this.seq.get(2), valid);
  }

  @Override
  public IExpression convertToEqualityOrError(AtomicBoolean valid) {
    return new Equality(this.seq.get(0), this.seq.get(2), valid);
  }

  @Override
  public Variable convertToModuleNameOrError(AtomicBoolean valid) {
    return super.convertToVariableOrError(valid);
  }

  @Override
  public IExpression convertToNewClassOrError(AtomicBoolean valid) {
    return new NewClass(this.seq.get(1), this.seq.get(2), valid);
  }

  @Override
  public IExpression convertToGetFieldOrError(AtomicBoolean valid) {
    return new GetField(this.seq.get(0), this.seq.get(2), valid);
  }

  @Override
  public IExpression convertToCallMethodOrError(AtomicBoolean valid) {
    return new CallMethod(this.seq.get(0), this.seq.get(2), this.seq.get(3), valid);
  }

  @Override
  public IExpression convertToInstanceOfOrError(AtomicBoolean valid) {
    return new InstanceOf(this.seq.get(0), this.seq.get(2), valid);
  }


  @Override
  public IBlock convertToBlockOrError(AtomicBoolean valid) {
    if (this.isStatement()) {
      return this.convertToStmtOrError(valid);
    } else if (this.isSequenceBlock()) {
      return new Block(this.seq, valid);
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "A block does not follow this pattern");
    }
  }

  public boolean isSequenceBlock() {
    if (!this.seq.isEmpty() && this.seq.get(0).toString().equals("block")) {
      return this.seq.size() > 1 && this.seq.getLast().isStatement();
    } else {
      return false;
    }
  }

  @Override
  public IDeclaration convertToDeclarationOrError(AtomicBoolean valid) {
    if (this.isDeclaration()) {
      return new Declaration(this.seq, valid);
     } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "A declaration statement does not follow this pattern");
    }
  }

  // no need to check if it is a class, it was already checked when parsed inside the program class
  // and classes are only ever declared in the program
  @Override
  public IClass convertToClassOrError(AtomicBoolean valid) {
    if (this.isClass()) {
      return new MyClass(this.seq, valid);
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "A class does not follow this pattern");
    }
  }


  @Override
  public List<Variable> convertToFieldOrParamList(AtomicBoolean valid) {
    List<Variable> fieldList = new ArrayList<>();
    for (ASTNodes node : this.seq) {
      fieldList.add(node.convertToVariableOrError(valid));
    }
    return fieldList;
  }


  @Override
  public IMethod convertToMethodOrError(AtomicBoolean valid) {
    if (this.isMethod()) {
      ASTNodes expression = this.seq.removeLast();
      return new Method(this.seq, expression, valid);
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "A Method does not follow this pattern");
    }
  }

  private boolean isMethod() {
    return this.seq.size() >= 4 && this.seq.get(0).toString().equals("method");
  }

  @Override
  public boolean isDeclaration() {
    return this.seq.size() == 3 && this.seq.get(0).toString().equals("def");
  }

  @Override
  public boolean isClass() {
    return this.seq.size() >= 3 && this.seq.get(0).toString().equals("class");
  }
  @Override
  public boolean isUntypedImport() {
    return this.seq.size() == 2 && this.seq.get(0).toString().equals("import");
  }

  @Override
  public boolean isTypedModule() {
    return this.seq.size() >= 4 && this.seq.get(0).toString().equals("tmodule");
  }
  @Override
  public boolean isUntypedModule() {
    return this.seq.size() >= 3 && this.seq.get(0).toString().equals("module");
  }

  @Override
  public boolean isTypedImport() {
    return this.seq.size() == 3 && this.seq.get(0).toString().equals("timport");
  }

  @Override
  public IModule convertToModuleOrError(AtomicBoolean valid) {
    if (this.isTypedModule()) {
      return new TypedModule(this.seq, valid);
    } else if (this.isUntypedModule()) {
      return new UntypedModule(this.seq, valid);
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "An import does not follow this pattern.");
    }
  }
  @Override
  public InterfaceImport convertToImportOrError(AtomicBoolean valid) {
    if (this.isUntypedImport()) {
      return new Import(this.seq, valid);
    } else if (this.isTypedImport()) {
      return new TImport(this.seq, valid);
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "An import does not follow this pattern.");
    }
  }


  @Override
  public IShape convertToShapeOrError(AtomicBoolean valid) {
    if (this.isShape()) {
      return new Shape(this.seq, valid);
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "A shape does not follow this pattern");
    }
  }

  private boolean isShape() {
    return this.seq.size() == 2;
  }


  public List<IMethodType> convertToListMethodTypesOrError(AtomicBoolean valid) {
    List<IMethodType> methodTypes = new ArrayList<>();
    Utils.Utils.voidMap(this.seq, node -> methodTypes.add(node.convertToMethodTypeOrError(valid)));
    return methodTypes;
  }


  @Override
  public IMethodType convertToMethodTypeOrError(AtomicBoolean valid) {
    if (this.isMethodType()) {
      return new MethodType(this.seq, valid);
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "A MethodType does not follow this pattern");
    }
  }

  private boolean isMethodType() {
    return this.seq.size() == 3;
  }

  public List<IType> convertToTypeListOrError(AtomicBoolean valid) {
    List<IType> types = new ArrayList<>();
    Utils.Utils.voidMap(this.seq, node -> types.add(node.convertToTypeOrError(valid)));
    return types;
  }


  public IType convertToTypeOrError(AtomicBoolean valid) {
    if (this.isShape()) {
      return new Shape(this.seq, valid);
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "A Type does not follow this pattern");
    }
  }

  public List<IFieldType> convertToListFieldTypesOrError(AtomicBoolean valid) {
    List<IFieldType> fieldTypes = new ArrayList<>();
    Utils.Utils.voidMap(this.seq, node -> fieldTypes.add(node.convertToFieldTypeOrError(valid)));
    return fieldTypes;
  }


  public IFieldType convertToFieldTypeOrError(AtomicBoolean valid) {
    if (this.isFieldType()) {
      return new FieldType(this.seq, valid);
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "A FieldType does not follow this pattern");
    }
  }

  private boolean isFieldType() {
    return this.seq.size() == 2;
  }

}
