package systemComponents;

import static Utils.Utils.modulesContainImportModule;

import Class.*;
import Utils.*;
import ast.ASTNodes;
import error.ErrorNode;
import expressions.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import types.*;

public class TypedModule implements IModule {
  Variable moduleName;
  List<InterfaceImport> imports;
  IClass myClass;
  IShape shape;


  public TypedModule(List<ASTNodes> nodes, AtomicBoolean valid) {
    nodes.removeFirst();
    this.moduleName = nodes.removeFirst().convertToModuleNameOrError(valid);
    this.shape = nodes.removeLast().convertToShapeOrError(valid);
    this.myClass = nodes.removeLast().convertToClassOrError(valid);
    List<InterfaceImport> tempImports = new ArrayList<>();
    for (ASTNodes node : nodes) {
      if (node.isUntypedImport() || node.isTypedImport()) {
        tempImports.add(node.convertToImportOrError(valid));
      } else {
        valid.set(false);
        tempImports.add(new ErrorNode(node.toString(), "An import does not follow this pattern"));
      }
    }
    this.imports = tempImports;

  }

  public TypedModule(Variable moduleName, List<InterfaceImport> imports, IClass myClass, IShape shape) {
    this.moduleName = moduleName;
    this.imports = imports;
    this.myClass = myClass;
    this.shape = shape;
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
    return this.myClass.containsClassWithDuplicateMethodFieldOrParamNames()
        || this.shape.containsDuplicateFieldOrMethodNames();
  }

  @Override
  public MyClass convertToClass(Map<Variable, Variable> mapModuleNamesToClasses) {
    List<Pair<Variable, Variable>> importedPairs = importedPairsOfModuleToClass(mapModuleNamesToClasses);
    ModuleClassBinding modulesToClassNameBinder = new ModuleClassBinding(importedPairs);
    return this.myClass.convertToClass(modulesToClassNameBinder, this.moduleName);
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

  @Override
  public void addShapeMapping(Map<Variable, Shape> sClasses) {
    sClasses.put(myClass.getClassName(), (Shape) shape);
  }

  @Override
  public boolean containsTypeError(List<IModule> prevDefinedMods) {
    Map<Variable, Shape> sClasses = new HashMap<>();
    populateShapeClassesWithImports(prevDefinedMods, sClasses);
    sClasses.put(this.myClass.getClassName(), (Shape) this.shape);
    return this.myClass.containsTypeError(sClasses);
  }

  @Override
  public boolean containsBadImports(List<IModule> prevDefinedMods) {
    return containsMismatchedModuleAndImport(prevDefinedMods)
            || containsDuplicateImportsDifferentShape(prevDefinedMods);
  }

  @Override
  public boolean isUntypedModule() {
    return false;
  }

  @Override
  public Variable getClassName() {
    return this.myClass.getClassName();
  }

  @Override
  public Map<Variable, IShape> synthesize() {
    Map<Variable, IShape> changedImports = new HashMap<>();
    List<InterfaceImport> newImports = new ArrayList<>();
    Utils.voidMap(imports, myImport -> {
      if (myImport.isTypedImport()) {
        myImport.addImportNameToShapeMapping(changedImports);
        newImports.add(myImport.synthesize(moduleName));
      } else {
        newImports.add(myImport);
      }
    });
    this.imports =  newImports;
    return changedImports;
  }

  @Override
  public IModule createTypedCopy(Variable moduleName, IShape shape) {
    throw new IllegalStateException("Only untyped modules should be converted to typed modules");
  }

  @Override
  public void annotateClassWithType() {
    myClass.addType(this.shape);
  }

  private boolean containsMismatchedModuleAndImport(List<IModule> prevDefinedMods) {
    boolean containsMismatch = false;
    for (InterfaceImport myImport : imports) {
      if (!modulesContainImportModule(prevDefinedMods,  myImport)) {
        return false;
      }
      containsMismatch |= myImport.containsImportModuleMismatch(prevDefinedMods);
    }
    return containsMismatch;
  }



  private boolean containsDuplicateImportsDifferentShape(List<IModule> prevDefinedMods) {
    Map<Variable, IShape> moduleToShapeMap = new HashMap<>();
    boolean hasInvalidDuplicateImports = false;
    for (InterfaceImport myImport : imports) {
      if (!modulesContainImportModule(prevDefinedMods, myImport)) {
        return false;
      }
      if (myImport.isTypedImport()) {
        hasInvalidDuplicateImports |= myImport.containsInvalidDuplicateImports(moduleToShapeMap);
      }
    }
    return hasInvalidDuplicateImports;
  }


  private void populateShapeClassesWithImports(List<IModule> modules, Map<Variable, Shape> sClasses) {
    Utils.voidMap(this.imports,
        myImport -> myImport.addImportedClassShapes(modules, sClasses));
  }
}
