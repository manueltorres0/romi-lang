package Class;

import Maps.ProxyOrValue;
import Method.IMethod;
import Utils.ModuleClassBinding;
import ast.ASTNodes;
import expressions.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import Utils.Utils;
import types.IShape;
import types.IType;
import types.Shape;

public class MyClass implements IClass {
  Variable className;
  List<Variable> fields;
  List<IMethod> methods;
  Optional<IShape> shape;

  public MyClass(ArrayList<ASTNodes> seq, AtomicBoolean valid) {
    seq.removeFirst();
    this.className = seq.get(0).convertToVariableOrError(valid);

    ASTNodes fieldNodes = seq.get(1);
    this.fields = fieldNodes.convertToFieldOrParamList(valid);

    List<IMethod> methods  = new ArrayList<>();
    for (int i = 2; i < seq.size(); i++) {
      methods.add(seq.get(i).convertToMethodOrError(valid));
    }
    this.methods = methods;
    shape = Optional.empty();

  }

  private MyClass(Variable qualifiedName, List<Variable> fields, List<IMethod> methods,
                  Optional<IShape> shape) {
    this.className = qualifiedName;
    this.fields = fields;
    this.methods = methods;
    this.shape = shape.isEmpty() ? shape : Optional.of(shape.get());
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedClassNames) {
    definedClassNames.add(this.className);
    return Utils.hasElementMatching(
        method -> method.containsUndefinedVariables(new HashSet<>(), definedClassNames),
        this.methods);
  }

  @Override
  public boolean containsClassWithDuplicateMethodFieldOrParamNames() {
    return this.containsClassWithMethodsSameName()
        || this.containsClassWithFieldsSameName()
        || containsClassWithMethodParamsSameName();
  }


  private boolean containsClassWithMethodsSameName() {
    HashSet<Variable> methodNames = new HashSet<>();
    return Utils.hasElementMatching(
        method -> method.containsDuplicateMethodNames(methodNames),
        this.methods);
  }


  private boolean containsClassWithFieldsSameName() {
    HashSet<Variable> fieldNames = new HashSet<>();
    return Utils.hasElementMatching(
        field -> field.containsDuplicateFieldOrParamNames(fieldNames),
        this.fields
    );
  }

  private boolean containsClassWithMethodParamsSameName() {
    return Utils.hasElementMatching(IMethod::containsDuplicateParamNames, this.methods);
  }

  @Override
  public Variable getClassName() {
    return this.className;
  }

  @Override
  public boolean hasField(Variable fieldName) {
    return this.fields.contains(fieldName);
  }

  @Override
  public boolean hasMethodAndCorrectNumberOfParams(Variable methodName, int numArgs) {
    return Utils.hasElementMatching(method -> {
      if (method.getMethodName().equals(methodName)) {
        return method.hasNumParams(numArgs);
      } else {
        return false;
      }
    }, this.methods);
  }

  @Override
  public int numFields() {
    return this.fields.size();
  }

  @Override
  public Map<Variable, ProxyOrValue> makeFieldMap(List<ProxyOrValue> paramValues) {
    Map<Variable, ProxyOrValue> fieldMap = new HashMap<Variable, ProxyOrValue>();
    for (int i = 0; i < paramValues.size(); i++) {
      ProxyOrValue paramValue = paramValues.get(i);
      Variable fieldName = this.fields.get(i);
      fieldMap.put(fieldName, paramValue);
    }
    return fieldMap;
  }

  @Override
  public Map<Variable, IMethod> makeMethodMap() {
    Map<Variable, IMethod> methodMap = new HashMap<Variable, IMethod>();
    Utils.voidMap(this.methods, method -> methodMap.put(method.getMethodName(), method));
    return methodMap;
  }

  @Override
  public MyClass convertToClass(ModuleClassBinding moduleToClassBinder, Variable thisModuleName) {
    Variable qualifiedName = renameToQualifiedName(thisModuleName);
    Utils.voidMap(this.methods, method -> method.renameClassesToQualifiedNames(moduleToClassBinder));
    return new MyClass(qualifiedName, this.fields, this.methods, this.shape);
  }

  private Variable renameToQualifiedName(Variable moduleName) {
    String qualifiedNameString = moduleName.toString() + "." + className.toString();
    return new Variable(qualifiedNameString);
  }


  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses) {
    Map<Variable, IType> tVar = new HashMap<>();
    Shape thisShape = sClasses.get(this.className);
    tVar.put(new Variable("this"), thisShape);
    List<Variable> fieldNamesList = thisShape.getFieldNames();
    Set<Variable> methodNamesSet = thisShape.getMethodNames();
    if (!fieldsHaveDifferentShape(fieldNamesList)
        && !methodsHaveDifferentShape(methodNamesSet)) {
      return Utils.anyMatchFullScan(m -> m.containsTypeError(sClasses, tVar), this.methods);
    } else {
      return true;
    }
  }

  @Override
  public void addType(IShape shape) {
    this.shape = Optional.of(shape);
  }

  @Override
  public MyClass copyClass() {
    return new MyClass(this.className.copyVariable(), copyFields(), copyMethods(), this.shape);
  }

  @Override
  public Optional<IShape> getClassType() {
    return this.shape;
  }

  private List<Variable> copyFields() {
    List<Variable> copyFields = new ArrayList<>();
    Utils.voidMap(this.fields, field -> copyFields.add(field.copyVariable()));
    return copyFields;
  }

  private List<IMethod> copyMethods() {
    List<IMethod> copyMethods = new ArrayList<>();
    Utils.voidMap(this.methods, m -> copyMethods.add(m.copyMethod()));
    return copyMethods;
  }

  private boolean methodsHaveDifferentShape(Set<Variable> methodNamesSet) {
    Set<Variable> thisMethodNamesSet = new HashSet<>();
    Utils.voidMap(methods, m -> thisMethodNamesSet.add(m.getMethodName()));
    return !thisMethodNamesSet.equals(methodNamesSet);
  }

  private boolean fieldsHaveDifferentShape(List<Variable> fieldNamesList) {
    if (fieldNamesList.size() != this.fields.size()) { return true; }
    for (int i = 0; i < fieldNamesList.size(); i++) {
      Variable shapeField = fieldNamesList.get(i);
      Variable thisClassField = this.fields.get(i);
      if (!shapeField.equals(thisClassField)) {
        return true;
      }
    }
    return false;
  }

}

