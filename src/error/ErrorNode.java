package error;

import Control.IControl;
import Maps.Environment;
import Maps.ProxyOrValue;
import Maps.Store;
import Method.IMethod;
import Utils.ModuleClassBinding;
import Utils.Pair;
import ast.Name;
import block.*;
import cesk.CESK;
import declarations.IDeclaration;
import expressions.IExpression;
import expressions.Variable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import programs.Kontinuation;
import statements.IStatement;
import Class.*;
import systemComponents.IModule;
import systemComponents.InterfaceImport;
import types.*;


public class ErrorNode extends Variable implements IExpression, IStatement, IBlock, IDeclaration,
    IMethod, IClass, IModule, InterfaceImport, IShape, IType, IMethodType, IFieldType {
  private final String errorArgs;
  private final String errorMessage;


  public ErrorNode(String errorArgs, String errorMessage) {
    super(new Name());
    this.errorArgs = errorArgs;
    this.errorMessage = errorMessage;
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedClassNames) {
    throw new IllegalStateException("Error node cannot be checked for undefined variables.");
  }

  @Override
  public boolean isDefined(HashSet<Variable> moduleNames) {
    throw new IllegalStateException("Error node cannot be checked for declaration");
  }

  @Override
  public Variable getModuleName() {
    throw new IllegalStateException("Cannot get module name of error node");
  }

  @Override
  public void addImportedClassShapes(List<IModule> modules, Map<Variable, Shape> sClasses) {
    throw new IllegalStateException("Error node cannot add imported classe shapes");
  }

  @Override
  public boolean containsImportModuleMismatch(List<IModule> modules) {
    throw new IllegalStateException("Cannot check module mismatch in error node");
  }

  @Override
  public boolean isTypedImport() {
    throw new IllegalStateException("Cannot check for typed import in error node");
  }

  @Override
  public boolean containsInvalidDuplicateImports(Map<Variable, IShape> classToShapeMap) {
    throw new IllegalStateException("Cannot check duplicate imports in error node");
  }

  @Override
  public InterfaceImport synthesize(Variable moduleName) {
    throw new IllegalStateException("Cannot synthesize with error node");
  }

  @Override
  public void addImportNameToShapeMapping(Map<Variable, IShape> changedImports) {
    throw new IllegalStateException("Cannot add import names from error nodes");
  }

  @Override
  public InterfaceImport copyImport() {
    throw new IllegalStateException("Cannot copy import from error node");
  }

  @Override
  public boolean undefinedImports(HashSet<Variable> moduleNames) {
    throw new IllegalStateException("Cannot check for undefined imports in error node");
  }

  @Override
  public void mapModuleNamesToClassDefined(Map<Variable, Variable> map) {
    throw new IllegalStateException("Cannot map module names to class defined error node");
  }

  @Override
  public void mapModuleNamesToClassDefined(List<Pair<Variable, Variable>> pairs) {
    throw new IllegalStateException("Cannot map module names to class defined error node");
  }

  @Override
  public boolean classContainsUndefinedVariable(Map<Variable, Variable> mapModuleToClass) {
    throw new IllegalStateException("Cannot check error node for classes");
  }

  @Override
  public boolean containsClassWithDuplicateMethodFieldOrParamNames() {
    throw new IllegalStateException("A non-wellformed AST should not be checked for validity");
  }

  @Override
  public MyClass convertToClass(Map<Variable, Variable> mapModuleNamesToClasses) {
    throw new IllegalStateException("Cannot convert error node to Class");
  }

  @Override
  public void addShapeMapping(Map<Variable, Shape> sClasses) {
    throw new IllegalStateException("Cannot add shape mappings with error node");
  }

  @Override
  public boolean containsTypeError(List<IModule> prevDefinedMods) {
    throw new IllegalStateException("Cannot check for type error in error node");
  }

  @Override
  public boolean containsBadImports(List<IModule> prevDefinedMods) {
    throw new IllegalStateException("Cannot check for bad imports");
  }

  @Override
  public boolean isUntypedModule() {
    throw new IllegalStateException("Cannot check if module or tmodule");
  }

  @Override
  public boolean containsDuplicateMethodNames(Set<Variable> methodNames) {
    throw new IllegalStateException("A non-wellformed AST should not be checked for validity");
  }

  @Override
  public boolean containsDuplicateParamNames() {
    throw new IllegalStateException("A non-wellformed AST should not be checked for validity");
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    throw new IllegalStateException("A non-wellformed AST should not be checked for validity");
  }

  @Override
  public void renameClassesToQualifiedNames(ModuleClassBinding moduleToClassBinder) {
    throw new IllegalStateException("Cannot rename error nodes");
  }

  @Override
  public IDeclaration copyDecl() {
    throw new IllegalStateException("Cannot copy decl from error node");
  }

  @Override
  public IMethod copyMethod() {
    throw new IllegalStateException("Cannot copy method from error node");
  }

  @Override
  public boolean isNestedBlock() {
    throw new IllegalStateException("An error node cannot be a nested block");
  }

  @Override
  public Block getNestedBlockOrThrow() {
    throw new IllegalStateException("An error node does not have a nested block");
  }

  @Override
  public IStatement copy() {
    throw new IllegalStateException("Cannot call copy block on error node");
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar,
                                   IType returnType) {
    throw  new IllegalStateException("Cannot check for type error in error node");
  }

  public Variable copyVariable() {
    throw new IllegalStateException("Cannot call copy block on error node");
  }

  @Override
  public IControl evaluateStatementOrDeclaration(Environment env, Store store, IControl control,
                                                 List<IClass> classes) {
    throw new IllegalStateException("Cannot evaluateStatementOrDeclaration on error node");
  }

  @Override
  public IControl findNextExpression(Kontinuation k, CESK cesk) {
    throw new IllegalStateException("Cannot find next expression on error node");
  }

  @Override
  public Environment getEnv() {
    throw new IllegalStateException("Cannot get env on error node");
  }

  @Override
  public boolean isReturnType() {
    throw new IllegalStateException("Cannot check for type error in error node");
  }

  @Override
  public IControl evaluateStatement(Environment env, Store store, IControl control,
                                    Block program, List<IClass> classes) {
    throw new IllegalStateException("Cannot evaluate statement on error node");
  }

  @Override
  public IControl findRHS() {
    throw new IllegalStateException("Cannot find RHS of error node");
  }

  @Override
  public IControl evaluateDeclaration(Environment env, Store store,
                                      IControl control) {
    throw new IllegalStateException("Cannot evaluate declaration on error node");
  }

  @Override
  public Variable getMethodName() {
    throw new IllegalStateException("Cannot get method name of error node");
  }

  @Override
  public List<Integer> checkMethodArgsContainsTypeError(List<IType> argTypes) {
    throw new IllegalStateException("Cannot check methods args of error node");
  }

  @Override
  public boolean hasReturnType(IType varType) {
    throw new IllegalStateException("Cannot check return type of error node");
  }

  @Override
  public IType getReturnType() {
    throw new IllegalStateException("Cannot get return type of error node");
  }

  @Override
  public void addTypedParams(Map<Variable, IType> tVar, List<Variable> paramNames) {
    throw new IllegalStateException("Cannot add typed params on error node");
  }

  @Override
  public int numParams() {
    throw new IllegalStateException("Cannot get number of params on error node");
  }

  @Override
  public boolean argumentNumIsMismatched(int sizeOfArgsPassedIn) {
    throw new IllegalStateException("cannot check argument size of error nodes");
  }

  @Override
  public int getNumParams() {
    throw new IllegalStateException("cannot get num params on error node");
  }

  @Override
  public List<IType> getParamTypes() {
    throw new IllegalStateException("cannot get params on error node");
  }

  @Override
  public boolean hasNumParams(int numArgs) {
    throw new IllegalStateException("error nodes do not have parameters");
  }

  @Override
  public Block convertToBlock() {
    throw new IllegalStateException("error nodes cannot be converted to block");
  }

  @Override
  public List<Variable> getParamNames() {
    throw new IllegalStateException("error nodes do not have params");
  }

  @Override
  public Variable getClassName() {
    throw new IllegalStateException("A non-wellformed AST should not be checked for class name");
  }

  @Override
  public Map<Variable, IShape> synthesize() {
    throw new IllegalStateException("Cannot synthesize with error node (map / system version)");
  }

  @Override
  public IModule createTypedCopy(Variable moduleName, IShape shape) {
    throw new IllegalStateException("Cannot create a typed copy of error node");
  }

  @Override
  public void annotateClassWithType() {
    throw new IllegalStateException("Error node cannot annotate a class with a type");
  }

  @Override
  public boolean hasField(Variable fieldName) {
    throw new IllegalStateException("An error node does not have fields");
  }

  @Override
  public boolean hasMethodAndCorrectNumberOfParams(Variable methodName, int numArgs) {
    throw new IllegalStateException("A error node does not have methods and arguments");
  }

  @Override
  public int numFields() {
    throw new IllegalStateException("An error node does not have fields");
  }

  @Override
  public Map<Variable, ProxyOrValue> makeFieldMap(List<ProxyOrValue> paramValues) {
    throw new IllegalStateException("Cannot make field map from error node");
  }

  @Override
  public Map<Variable, IMethod> makeMethodMap() {
    throw new IllegalStateException("Cannot make method map from error node");
  }

  @Override
  public MyClass convertToClass(ModuleClassBinding mapImportedModuleToClass, Variable moduleName) {
    throw new IllegalStateException("Cannot convert an error node to class");
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses) {
    throw new IllegalStateException("Should not check for type errors on error node");
  }

  @Override
  public void addType(IShape shape) {
    throw new IllegalStateException("Error node should not try adding type");
  }

  @Override
  public MyClass copyClass() {
    throw new IllegalStateException("Cannot copy class from error node");
  }

  @Override
  public Optional<IShape> getClassType() {
    throw new IllegalStateException("Cannot get class type from error node");
  }

  @Override
  public IType getType() {
    throw new IllegalStateException("Cannot get type of error node");
  }

  @Override
  public Variable getFieldName() {
    throw new IllegalStateException("Cannot get field name of error node");
  }

  @Override
  public boolean hasType(IType varType) {
    throw new IllegalStateException("Cannot check for type of error node");
  }

  @Override
  public boolean isNumber() {
    throw new IllegalStateException("Cannot check for number of error node");
  }

  @Override
  public boolean containsField(Variable fieldName) {
    throw new IllegalStateException("Cannot check for fields of error node");
  }

  @Override
  public boolean containsMethod(Variable methodName) {
    throw new IllegalStateException("Cannot check for methods of error node");
  }

  @Override
  public IMethodType getMethodType(Variable methodName) {
    throw new IllegalStateException("Cannot get method type for error node");
  }

  @Override
  public IFieldType getFieldType(Variable fieldName) {
    throw new IllegalStateException("Cannot get field type for error node");
  }

  @Override
  public boolean isShape() {
    throw new IllegalStateException("Cannot check if error node is shape");
  }

  @Override
  public List<Variable> getFieldNames() {
    throw new IllegalStateException("Cannot get field names for error node");
  }

  @Override
  public Set<Variable> getMethodNames() {
    throw new IllegalStateException("Cannot get method names for error node");
  }

  @Override
  public boolean containsDuplicateFieldOrMethodNames() {
    throw new IllegalStateException("Cannot check for duplicate field or method names for error node");
  }

  @Override
  public IExpression copyExpr() {
    throw new IllegalStateException("Cannot copy an expr from error node");
  }
}
