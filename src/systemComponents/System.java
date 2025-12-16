package systemComponents;

import Class.IClass;
import Utils.*;
import ast.ASTNodes;
import block.Block;
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

import java.util.stream.Collectors;
import programs.Program;
import types.IShape;
import types.IType;
import types.NumberType;
import types.Shape;

/**
 * A System is a class that represents the module system with class definitions (and imports), and a
 * runnable block with declarations, statements and an expression.
 */
public class System {
  public List<IModule> modules;
  public List<InterfaceImport> imports;
  public Block block;
  private boolean clean;


  public System() {}
  public System(List<ASTNodes> moduleImportsAndProgram, ASTNodes expression) {
    AtomicBoolean validGrammar = new AtomicBoolean(true);

    this.modules = parseModules(moduleImportsAndProgram, validGrammar);
    this.imports = parseImports(moduleImportsAndProgram, validGrammar);
    this.block = new Block(moduleImportsAndProgram, expression, validGrammar);

    this.clean = validGrammar.get();

  }

  private static List<IModule> parseModules(List<ASTNodes> moduleList, AtomicBoolean validGrammar) {
    List<IModule> processedModules = new ArrayList<>();
    if (!moduleList.isEmpty()) {
      ASTNodes firstModule = moduleList.getFirst();
      while (isNotDeclarationOrStatementOrImport(firstModule)) {
        if (firstModule.isTypedModule() || firstModule.isUntypedModule()) {
          processedModules.add(firstModule.convertToModuleOrError(validGrammar));
        } else {
          validGrammar.set(false);
          processedModules.add(new ErrorNode(firstModule.toString(), "A module does" +
              " not follow this pattern"));
        }
        moduleList.removeFirst();
        if (moduleList.isEmpty()) {
          break;
        } else {
          firstModule = moduleList.getFirst();
        }
      }
    }
    return processedModules;
  }

  private static List<InterfaceImport> parseImports(List<ASTNodes> importList, AtomicBoolean validGrammar) {
    List<InterfaceImport> processedImports = new ArrayList<>();
    if (!importList.isEmpty()) {
      ASTNodes firstImport = importList.getFirst();
      while (isNotDeclarationOrStatement(firstImport)) {
        if (firstImport.isUntypedImport() || firstImport.isTypedImport()) {
          processedImports.add(firstImport.convertToImportOrError(validGrammar));
        } else {
          validGrammar.set(false);
          processedImports.add(new ErrorNode(firstImport.toString(), "An import does" +
                  " not follow this pattern"));
        }
        importList.removeFirst();
        if (importList.isEmpty()) {
          break;
        } else {
          firstImport = importList.getFirst();
        }
      }
    }
    return processedImports;
  }

  private static boolean isNotDeclarationOrStatementOrImport(ASTNodes node) {
   return isNotDeclarationOrStatement(node) && !node.isUntypedImport() && !node.isTypedImport();
  }
  private static boolean isNotDeclarationOrStatement(ASTNodes node) {
    return !node.isDeclaration() && !node.isStatement();
  }

  public boolean containsError() {
    return !this.clean;
  }

  public boolean containsDuplicateModuleName() {
    HashSet<Variable> moduleNames = new HashSet<>();
    Predicate<IModule> pred = (IModule module) -> {
      Variable var = module.getModuleName();
      if (moduleNames.contains(var)) {
        return true;
      } else {
        moduleNames.add(var);
        return false;
      }
    };
    return Utils.hasElementMatching(pred, modules);
  }

  private boolean undefinedImports() {
    HashSet<Variable> moduleNames = new HashSet<>();
    return undefinedImportsInModules(moduleNames) || undefinedImportsInSystem(moduleNames);
  }

  private boolean undefinedImportsInModules(HashSet<Variable> moduleNames) {
    Predicate<IModule> pred = (IModule module) -> {
      if (module.undefinedImports(moduleNames)) {
        return true;
      } else {
        moduleNames.add(module.getModuleName());
        return false;
      }
    };
    return Utils.hasElementMatching(pred, modules);
  }

  private boolean undefinedImportsInSystem(HashSet<Variable> moduleNames) {
    Predicate<InterfaceImport> pred = (InterfaceImport myImport) -> !myImport.isDefined(moduleNames);
    return Utils.hasElementMatching(pred, imports);
  }

  private boolean undefinedImportsInSystem() {
    HashSet<Variable> moduleNames = new HashSet<>();
    Utils.voidMap(this.modules, mod -> moduleNames.add(mod.getModuleName()));
    return undefinedImportsInSystem(moduleNames);
  }


  private Map<Variable, Variable> mapModuleNamesToClassDefined() {
    Map<Variable, Variable> map = new HashMap<>();
    Utils.voidMap(this.modules, module -> module.mapModuleNamesToClassDefined(map));
    return map;
  }


  public boolean containsUndefinedVariables() {
    Map<Variable, Variable> map = mapModuleNamesToClassDefined();
    return undefinedImports() ||
        classesInModulesContainedUndefinedVariables(map)
        || systemBodyContainsUndefinedVariables(map);
  }


  private boolean systemBodyContainsUndefinedVariables(Map<Variable, Variable> mapModuleNamesToClasses) {
    Set<Variable> definedClassNames = this.getDefinedClassesForSystemBody(mapModuleNamesToClasses);
    return block.containsUndefinedVariables(new HashSet<>(), definedClassNames);
  }


  private Set<Variable> getDefinedClassesForSystemBody(Map<Variable, Variable> mapModuleNamesToClasses) {
    Set<Variable> importedModulesForSystemBody = mapModuleNamesToClasses
        .keySet()
        .stream()
        .filter(var -> {
          return Utils.hasElementMatching(myImport -> myImport.getModuleName().equals(var), this.imports);
        })
        .collect(Collectors.toSet());

    Set<Variable> definedClassesForSystemBody = new HashSet<>();
    Utils.voidMap(importedModulesForSystemBody,
        moduleName -> definedClassesForSystemBody.add(mapModuleNamesToClasses.get(moduleName)));

    return definedClassesForSystemBody;
  }


  private boolean classesInModulesContainedUndefinedVariables(Map<Variable, Variable> mapModuleNamesToClasses) {
    Predicate<IModule> pred = module -> module.classContainsUndefinedVariable(mapModuleNamesToClasses);
    return Utils.hasElementMatching(pred, modules);
  }

  public boolean containsClassWithDuplicateMethodFieldOrParamNames() {
    Predicate<IModule> pred = IModule::containsClassWithDuplicateMethodFieldOrParamNames;
    return Utils.hasElementMatching(pred, modules);
  }
  
  public Program linkModules() {
    List<IClass> classes = extractRenamedClassesFromModules();
    renameClassesInBody();
    return new Program(classes, block);
  }

  public void synthesize() {
    synthesizeModules();
    synthesizeBody();
  }

  public void typeIt() {
    Utils.voidMap(modules, module -> {
      if (!module.isUntypedModule()) {
        module.annotateClassWithType();
      }
    });
  }

  private void synthesizeModules() {
    Utils.voidMap(new ArrayList<>(modules), module -> {
      if (!module.isUntypedModule()) {
        Map<Variable, IShape> modulesToSynthesize = module.synthesize();
        createTypedModules(modulesToSynthesize, module.getModuleName());
      }
    });
  }

  private void createTypedModules(Map<Variable, IShape> modulesToBeChanged, Variable moduleName) {
    for (Variable moduleToBeChanged : modulesToBeChanged.keySet()) {
      IModule foundModule = Utils.find(modules, module -> module.getModuleName().equals(moduleToBeChanged));
      this.modules.add(foundModule.createTypedCopy(moduleName, modulesToBeChanged.get(moduleToBeChanged)));
    }
  }

  private void synthesizeBody() {
    Variable bodyVar = new Variable("Body");
    Map<Variable, IShape> modulesToSynthesize = renameImportsInBodyForSynthesis(bodyVar);
    createTypedModules(modulesToSynthesize, bodyVar);
  }

  private Map<Variable, IShape> renameImportsInBodyForSynthesis(Variable bodyVar) {
    Map<Variable, IShape> changedImports = new HashMap<>();
    List<InterfaceImport> newImports = new ArrayList<>();
    Utils.voidMap(imports, myImport -> {
      if (myImport.isTypedImport()) {
        myImport.addImportNameToShapeMapping(changedImports);
        newImports.add(myImport.synthesize(bodyVar));
      } else {
        newImports.add(myImport);
      }
    });
    this.imports =  newImports;
    return changedImports;
  }

  private void renameClassesInBody() {
    List<Pair<Variable, Variable>> pairsOfModuleNameToClass  = pairsOfModuleNameToClass();
    ModuleClassBinding binder = new ModuleClassBinding(pairsOfModuleNameToClass);
    block.renameClassesToQualifiedNames(binder);
  }

  private List<Pair<Variable, Variable>> pairsOfModuleNameToClass() {
    List<Pair<Variable, Variable>> pairs = new ArrayList<>();
    Utils.voidMap(this.imports,
        myImport -> {
      IModule module = findModuleCorrespondingToImport(myImport.getModuleName());
      module.mapModuleNamesToClassDefined(pairs);
    });
    return pairs;
  }


  private IModule findModuleCorrespondingToImport(Variable moduleName) {
    for (IModule module : modules) {
      if (module.getModuleName().equals(moduleName)) {
        return module;
      }
    }
    throw new IllegalArgumentException("Module " + moduleName + " not found");
  }


  private List<IClass> extractRenamedClassesFromModules() {
    List<IClass> classes = new ArrayList<IClass>();
    Map<Variable, Variable> mapModuleNamesToClasses = mapModuleNamesToClassDefined();

    Utils.voidMap(this.modules, module -> {
      classes.add(module.convertToClass(mapModuleNamesToClasses));
    });

    return classes;
  }

  public boolean containsTypeError() {
    if (modulesContainTypeErrors()) {
      return true;
    } else {
      Map<Variable, IType> tVar = new HashMap<>();
      return bodyContainsTypeErrors(this.modules, tVar);
    }
  }

  private boolean modulesContainTypeErrors() {
    List<IModule> prefDefinedMods = new ArrayList<>();

    Predicate<IModule> pred = module -> {
      prefDefinedMods.add(module);
      if (module.isUntypedModule()) {
        return false;
      }
      return module.containsTypeError(prefDefinedMods);
    };

    return Utils.anyMatchFullScan(pred, this.modules);
  }

  private boolean bodyContainsTypeErrors(List<IModule> prefDefinedMods, Map<Variable, IType> tVar) {
    Map<Variable, Shape> sClasses = new HashMap<>();
    Utils.voidMap(this.imports, myImport -> myImport.addImportedClassShapes(prefDefinedMods, sClasses));
    return this.block.containsTypeError(sClasses, tVar, new NumberType());
  }

  public boolean containsBadImport() {
    Map<Variable, IShape> importNameToShape = new HashMap<>();
    return Utils.hasElementMatching(mod -> mod.containsBadImports(modules), modules)
        || badImportsInBody(importNameToShape);
  }

  private boolean badImportsInBody(Map<Variable, IShape> importNameToShape) {
    if (undefinedImportsInSystem()) {
      return false;
    } else {
      return Utils.hasElementMatching(
          myImport -> myImport.containsImportModuleMismatch(modules), this.imports) ||
        Utils.hasElementMatching(
            myImport -> myImport.containsInvalidDuplicateImports(importNameToShape), this.imports);
    }
  }

  public List<String> extractModuleNames() {
    return this.modules.stream().
        map(mod -> "\"" + mod.getModuleName().toString() + "\"")
        .collect(Collectors.toList());
  }
}
