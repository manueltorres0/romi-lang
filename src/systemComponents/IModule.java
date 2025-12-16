package systemComponents;

import Utils.Pair;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import Class.MyClass;

import expressions.Variable;
import types.IShape;
import types.Shape;

public interface IModule {
  Variable getModuleName();

  boolean undefinedImports(HashSet<Variable> moduleNames);

  void mapModuleNamesToClassDefined(Map<Variable, Variable> map);

  void mapModuleNamesToClassDefined(List<Pair<Variable, Variable>> pairs);

  boolean classContainsUndefinedVariable(Map<Variable, Variable> mapModuleToClass);

  boolean containsClassWithDuplicateMethodFieldOrParamNames();

  MyClass convertToClass(Map<Variable, Variable> mapModuleNamesToClasses);

  void addShapeMapping(Map<Variable, Shape> sClasses);

  boolean containsTypeError(List<IModule> prevDefinedMods);

  boolean containsBadImports(List<IModule> prevDefinedMods);

  boolean isUntypedModule();

  Variable getClassName();

  Map<Variable, IShape> synthesize();

  IModule createTypedCopy(Variable moduleName, IShape shape);

  void annotateClassWithType();
}
