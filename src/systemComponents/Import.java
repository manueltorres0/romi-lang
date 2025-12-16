package systemComponents;

import static Utils.Utils.find;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import ast.ASTNodes;
import expressions.Variable;
import types.*;

public class Import implements InterfaceImport {
  Variable moduleName;


  public Import(ArrayList<ASTNodes> seq, AtomicBoolean valid) {
    seq.removeFirst();
    this.moduleName = seq.getFirst().convertToVariableOrError(valid);
  }

  public Import(Variable moduleName) {
    this.moduleName = moduleName;
  }

  @Override
  public boolean isDefined(HashSet<Variable> moduleNames) {
    return moduleNames.contains(this.moduleName);
  }

  @Override
  public Variable getModuleName() {
    return moduleName;
  }

  @Override
  public void addImportedClassShapes(List<IModule> modules, Map<Variable, Shape> sClasses) {
    IModule module = find(modules, mod -> mod.getModuleName().equals(this.moduleName));
    module.addShapeMapping(sClasses);
  }

  @Override
  public boolean containsImportModuleMismatch(List<IModule> modules) {
    IModule module = find(modules, mod -> mod.getModuleName().equals(this.moduleName));
    return module.isUntypedModule();
  }

  @Override
  public boolean isTypedImport() {
    return false;
  }

  @Override
  public boolean containsInvalidDuplicateImports(Map<Variable, IShape> classToShapeMap) {
    return false;
  }

  @Override
  public InterfaceImport synthesize(Variable moduleName) {
    throw new IllegalStateException("Only timports should be synthesized.");
  }

  @Override
  public void addImportNameToShapeMapping(Map<Variable, IShape> changedImports) {
    throw new IllegalStateException("Cannot add typed modules to changed imports");
  }

  @Override
  public InterfaceImport copyImport() {
    return new Import(this.moduleName.copyVariable());
  }
}
