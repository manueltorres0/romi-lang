package systemComponents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import ast.*;
import error.ErrorNode;
import expressions.*;
import Class.*;
import Utils.*;
import types.IShape;
import types.Shape;

public class UntypedModule implements IModule {
  Variable moduleName;
  List<InterfaceImport> imports;
  IClass myClass;


  public UntypedModule(List<ASTNodes> nodes, AtomicBoolean valid) {
    nodes.removeFirst();
    this.moduleName = nodes.removeFirst().convertToModuleNameOrError(valid);
    this.myClass = nodes.removeLast().convertToClassOrError(valid);
    List<InterfaceImport> tempImports = new ArrayList<>();
    for (ASTNodes node : nodes) {
      if (node.isUntypedImport()){
        tempImports.add(node.convertToImportOrError(valid));
      } else {
        valid.set(false);
        tempImports.add(new ErrorNode(node.toString(), "An import does not follow this pattern"));
      }
    }
    this.imports = tempImports;

  }

  @Override
  public Variable getModuleName() {
    return moduleName;
  }

  @Override
  public boolean undefinedImports(HashSet<Variable> moduleNames) {
    Predicate<InterfaceImport> pred = (InterfaceImport myImport) -> !myImport.isDefined(moduleNames);
    return Utils.hasElementMatching(pred, imports);
  }

  @Override
  public void mapModuleNamesToClassDefined(Map<Variable, Variable> map) {
    map.put(this.moduleName, this.myClass.getClassName().copyVariable());
  }

  @Override
  public void mapModuleNamesToClassDefined(List<Pair<Variable, Variable>> pairs) {
    pairs.add(new Pair(this.moduleName, this.myClass.getClassName().copyVariable()));
  }

  @Override
  public boolean classContainsUndefinedVariable(Map<Variable, Variable> mapModuleToClass) {
    Set<Variable> definedClassNames = new HashSet<>();
    Utils.voidMap(this.imports, myImport -> {
      definedClassNames.add(mapModuleToClass.get(myImport.getModuleName()));
    });

    return this.myClass.containsUndefinedVariables(definedClassNames);
  }

  @Override
  public boolean containsClassWithDuplicateMethodFieldOrParamNames() {
    return this.myClass.containsClassWithDuplicateMethodFieldOrParamNames();
  }

  @Override
  public MyClass convertToClass(Map<Variable, Variable> mapModuleNamesToClasses) {
    List<Pair<Variable, Variable>> importedPairs = importedPairsOfModuleToClass(mapModuleNamesToClasses);
    ModuleClassBinding modulesToClassNameBinder = new ModuleClassBinding(importedPairs);
    return this.myClass.convertToClass(modulesToClassNameBinder, this.moduleName);
  }


  @Override
  public void addShapeMapping(Map<Variable, Shape> sClasses) {
    throw new IllegalStateException("untyped modules should not be type checked (add shape mapping)");
  }

  @Override
  public boolean containsTypeError(List<IModule> prevDefinedMods) {
    throw new IllegalStateException("Untyped modules should not be type checked (contains type error)");
  }

  @Override
  public boolean containsBadImports(List<IModule> prevDefinedMods) {
    return false;
  }

  @Override
  public boolean isUntypedModule() {
    return true;
  }

  private List<Pair<Variable, Variable>> importedPairsOfModuleToClass(Map<Variable, Variable> mapModuleNamesToClasses) {
    List<Pair<Variable, Variable>> importedPairs = new ArrayList<>();
    Utils.voidMap(this.imports, myImport -> {
      Variable moduleName = myImport.getModuleName();
      importedPairs.add(new Pair(moduleName, mapModuleNamesToClasses.get(moduleName).copyVariable()));
    });
    importedPairs.add(new Pair(this.moduleName, this.myClass.getClassName().copyVariable()));
    return importedPairs;
  }

  public Variable getClassName() {
    return this.myClass.getClassName();
  }

  @Override
  public Map<Variable, IShape> synthesize() {
    throw new IllegalStateException("Untyped Modules should be skipped for synthesis");
  }

  @Override
  public IModule createTypedCopy(Variable moduleNameOfParentModule, IShape shape) {
    String newModuleName = this.moduleName.toString() + ".into." + moduleNameOfParentModule.toString();
    return new TypedModule(new Variable(newModuleName), copyImports(), myClass.copyClass(), shape);
  }

  private List<InterfaceImport> copyImports() {
    List<InterfaceImport> copyImports = new ArrayList<>();
    Utils.voidMap(this.imports, myImport -> copyImports.add(myImport.copyImport()));
    return copyImports;
  }


  @Override
  public void annotateClassWithType() {
    throw new IllegalStateException("Untyped modules should not annotate their class since they have no shape to annotate with");
  }
}
