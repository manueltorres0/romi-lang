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

public class TImport implements InterfaceImport {
  Variable moduleName;
  IShape shape;
  public TImport(ArrayList<ASTNodes> seq, AtomicBoolean valid) {
    seq.removeFirst();
    this.moduleName = seq.removeFirst().convertToVariableOrError(valid);
    this.shape = seq.removeFirst().convertToShapeOrError(valid);
  }

  private TImport(Variable moduleName, IShape shape) {
    this.moduleName = moduleName;
    this.shape = shape;
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
    sClasses.put(module.getClassName(), (Shape) this.shape);
  }

  @Override
  public boolean containsImportModuleMismatch(List<IModule> modules) {
    IModule module = find(modules, mod -> mod.getModuleName().equals(this.moduleName));
    return !module.isUntypedModule();
  }

  @Override
  public boolean isTypedImport() {
    return true;
  }

  @Override
  public boolean containsInvalidDuplicateImports(Map<Variable, IShape> moduleToShapeMap) {
    if (moduleToShapeMap.containsKey(moduleName)) {
      return !moduleToShapeMap.get(moduleName).equals(shape);
    } else {
      moduleToShapeMap.put(moduleName, shape);
      return false;
    }
  }

  @Override
  public InterfaceImport synthesize(Variable moduleName) {
    String renamedModule = this.moduleName.toString() + ".into." + moduleName;
    return new Import(new Variable(renamedModule));
  }

  @Override
  public void addImportNameToShapeMapping(Map<Variable, IShape> changedImports) {
    changedImports.put(this.moduleName, this.shape);
  }

  @Override
  public InterfaceImport copyImport() {
    return new TImport(this.moduleName.copyVariable(), this.shape);
  }
}
