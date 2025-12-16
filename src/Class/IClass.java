package Class;


import Maps.ProxyOrValue;
import Method.IMethod;
import Utils.ModuleClassBinding;
import expressions.Variable;
import java.util.Optional;
import types.IShape;
import types.Shape;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IClass {

  boolean containsUndefinedVariables(Set<Variable> definedClassNames);

  boolean containsClassWithDuplicateMethodFieldOrParamNames();
  Variable getClassName();

  boolean hasField(Variable fieldName);

  boolean hasMethodAndCorrectNumberOfParams(Variable methodName, int numArgs);

  int numFields();

  Map<Variable, ProxyOrValue> makeFieldMap(List<ProxyOrValue> paramValues);

  Map<Variable, IMethod> makeMethodMap();

  MyClass convertToClass(ModuleClassBinding mapImportedModuleToClass, Variable moduleName);

  boolean containsTypeError(Map<Variable, Shape> sClasses);

  void addType(IShape shape);

  MyClass copyClass();

  Optional<IShape> getClassType();
}
