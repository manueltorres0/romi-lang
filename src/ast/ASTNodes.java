package ast;

import Method.IMethod;
import declarations.IDeclaration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import systemComponents.IModule;
import systemComponents.InterfaceImport;
import systemComponents.System;
import statements.*;
import expressions.*;
import error.*;
import block.*;
import Class.*;
import types.IFieldType;
import types.IMethodType;
import types.IShape;
import types.IType;

/**
 * Main purpose of this interface is to convert  Examples into ExampleBB Grammar, which
 * is then used to check if any violations occurred. The interface also allows
 * for self-referential data, specifically sequences / lists.
 */
public interface ASTNodes {

  String toString();

  System convertToSystemOrErrorNode();

  IStatement convertToStmtOrError(AtomicBoolean valid);

  IStatement convertToAssStatementOrError(AtomicBoolean valid);

  IStatement convertToWhileStatementOrError(AtomicBoolean valid);

  IStatement convertToIfStatementOrError(AtomicBoolean valid);

  IExpression convertToExpressionOrError(AtomicBoolean valid);

  IExpression convertToAdditionOrError(AtomicBoolean valid);

  IExpression convertToDivisionOrError(AtomicBoolean valid);

  IExpression convertToEqualityOrError(AtomicBoolean valid);

  Variable convertToVariableOrError(AtomicBoolean valid);

  Variable convertToModuleNameOrError(AtomicBoolean valid);

  IBlock convertToBlockOrError(AtomicBoolean valid);

  IDeclaration convertToDeclarationOrError(AtomicBoolean valid);

  IClass convertToClassOrError(AtomicBoolean valid);

  IMethod convertToMethodOrError(AtomicBoolean valid);

  IStatement convertToFieldAssignmentOrError(AtomicBoolean valid);

  IExpression convertToNewClassOrError(AtomicBoolean valid);

  boolean isDeclaration();

  boolean isStatement();

  boolean isClass();

  List<Variable> convertToFieldOrParamList(AtomicBoolean valid);

  IExpression convertToGetFieldOrError(AtomicBoolean valid);

  IExpression convertToCallMethodOrError(AtomicBoolean valid);

  IExpression convertToInstanceOfOrError(AtomicBoolean valid);

  boolean isUntypedImport();

  boolean isTypedModule();

  IModule convertToModuleOrError(AtomicBoolean validGrammar);

  InterfaceImport convertToImportOrError(AtomicBoolean valid);

  IShape convertToShapeOrError(AtomicBoolean valid);

  List<IFieldType> convertToListFieldTypesOrError(AtomicBoolean valid);

  List<IMethodType> convertToListMethodTypesOrError(AtomicBoolean valid);

  IMethodType convertToMethodTypeOrError(AtomicBoolean valid);

  List<IType> convertToTypeListOrError(AtomicBoolean valid);

  IType convertToTypeOrError(AtomicBoolean valid);

  IFieldType convertToFieldTypeOrError(AtomicBoolean valid);

  boolean isUntypedModule();

  boolean isTypedImport();
}



abstract class AbstractNodes implements ASTNodes {

  public System convertToSystemOrErrorNode() {
    return new ErrorSystem();
  }

  public IStatement convertToStmtOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(),
        "A statements.Statement is not composed of a single " + this.getClass().getSimpleName());
  }

  public IStatement convertToAssStatementOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(),
        "An Assignment statements.Statement is not composed of a single " + this.getClass().getSimpleName());
  }

  public IStatement convertToWhileStatementOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(),
        "A While statements.Statement is not composed of a single " + this.getClass().getSimpleName());
  }

  public IStatement convertToIfStatementOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(),
        "An If statements.Statement is not composed of a single " + this.getClass().getSimpleName());
  }

  public IExpression convertToAdditionOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(),
        "An expressions.Addition is not composed of a single" + this.getClass().getSimpleName());
  }

  public IExpression convertToDivisionOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(),
        "A Division is not composed of a single" + this.getClass().getSimpleName());
  }

  public IExpression convertToEqualityOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(),
        "An Equality is not composed of a single" + this.getClass().getSimpleName());
  }

  public Variable convertToVariableOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(),
        "A " + this.getClass().getSimpleName() + " cannot be a expressions.Variable");
  }

  public IBlock convertToBlockOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A block.Block is not composed of a single ast.Name");
  }

  public IDeclaration convertToDeclarationOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A Declaration is not composed of a single Name or Number");
  }

  public IClass convertToClassOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A Class is not composed of a single Name or Number");
  }

  public boolean isDeclaration() {
    return false;
  }

  public boolean isStatement() {
    return false;
  }

  public boolean isClass() {
    return false;
  }

  public List<Variable> convertToFieldOrParamList(AtomicBoolean valid) {
    valid.set(false);
    List<Variable> errorFields = new ArrayList<>();
    errorFields.add(new ErrorNode(this.toString(), "A Field List is not composed of a single Name or Number"));
    return errorFields;
  }

  public IMethod convertToMethodOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A Method is not composed of a single Name or Number");
  }

  public IStatement convertToFieldAssignmentOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A Field Assingment is not composed of a single Name or Number");
  }

  public IExpression convertToNewClassOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A NewClass Expression is not composed of a single Name or Number");
  }

  public IExpression convertToGetFieldOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A GetField Expression is not composed of a single Name or Number");
  }

  public IExpression convertToCallMethodOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A CallMethod Expression is not composed of a single Name or Number");
  }

  public IExpression convertToInstanceOfOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A InstanceOf Expression is not composed of a single Name or Number");
  }
  public boolean isUntypedImport() {
    return false;
  }

  public boolean isTypedModule() {
    return false;
  }

  public IModule convertToModuleOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A Module is not a name or number");
  }

  public InterfaceImport convertToImportOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "An import is not a name or number");
  }

  public IShape convertToShapeOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A Shape is not a name or number");
  }


  public List<IFieldType> convertToListFieldTypesOrError(AtomicBoolean valid) {
    valid.set(false);
    return List.of(new ErrorNode(this.toString(), "A name or number is not a List of fieldTypes"));
  }

  public List<IMethodType> convertToListMethodTypesOrError(AtomicBoolean valid) {
    valid.set(false);
    return List.of(new ErrorNode(this.toString(), "A name or number is not a List of methodTypes"));
  }


  public IMethodType convertToMethodTypeOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A name or number is not a methodType");
  }

  public List<IType> convertToTypeListOrError(AtomicBoolean valid) {
    valid.set(false);
    return List.of(new ErrorNode(this.toString(), "A name or number is not a type list"));
  }

  public IFieldType convertToFieldTypeOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A name or number is not a fieldType");
  }

  public boolean isTypedImport() {
    return false;
  }
  public boolean isUntypedModule() {
    return false;
  }
}


