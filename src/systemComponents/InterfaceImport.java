package systemComponents;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import expressions.Variable;
import types.IShape;
import types.Shape;

public interface InterfaceImport {

  boolean isDefined(HashSet<Variable> moduleNames);

  Variable getModuleName();

  void addImportedClassShapes(List<IModule> modules, Map<Variable, Shape> sClasses);

  boolean containsImportModuleMismatch(List<IModule> modules);

  boolean isTypedImport();

  boolean containsInvalidDuplicateImports(Map<Variable, IShape> classToShapeMap);

  InterfaceImport synthesize(Variable moduleName);

  void addImportNameToShapeMapping(Map<Variable, IShape> changedImports);

  InterfaceImport copyImport();
}
