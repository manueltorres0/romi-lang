package expressions;

import Control.*;
import Maps.Environment;
import Maps.Location;
import Maps.ProxyOrValue;
import Maps.Store;
import Maps.StoreObject;
import Utils.ModuleClassBinding;
import ast.ASTNodes;
import cesk.CESK;
import error.TypeError;
import error.UndefinedVariableError;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import programs.Kontinuation;
import Class.IClass;
import types.IShape;
import types.IType;
import types.Shape;

public class NewClass implements IExpression {
  public Variable className;
  public List<Variable> params;

  public NewClass(ASTNodes className, ASTNodes params, AtomicBoolean valid) {
    this.className = className.convertToVariableOrError(valid);
    this.params = params.convertToFieldOrParamList(valid);
  }

  public NewClass(Variable className, List<Variable> result) {
    this.className = className;
    this.params = result;
  }

  @Override
  public boolean isError() {
    return false;
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    boolean undefined = false;

    if (this.className.containsUndefinedClassNames(definedClassNames)) {
      this.className = new UndefinedVariableError(this.className.toString(),
          "Contains undefined Class");
      undefined = true;
    }

    List<Variable> afterCheck = new ArrayList<Variable>();
    for (Variable var : this.params) {
      if (var.containsUndefinedVariables(definedVariables, definedClassNames)) {
        afterCheck.add( new UndefinedVariableError(var.toString(),
            "Contains undefined Variable"));
        undefined = true;
      } else {
        afterCheck.add(var);
      }
    }
    this.params = afterCheck;
    return undefined;
  }

  @Override
  public IControl evaluate(Environment env, Store store, List<IClass> classes,
                           Kontinuation k, CESK cesk) {
    IClass classOfObject = getClassToConstruct(classes);
    if (!(this.params.size() == numFieldsForClassToConstruct(classes))) {
      return new ErrorControl();
    }
    Optional<IShape> classType = classOfObject.getClassType();
    if (classType.isEmpty()) {
      return new ValueControl(this.evaluateNewUntypedClass(env, store, classes));
    } else {
      return this.evaluateNewTypedClass(env, store, classes, classType.get());
    }
  }

  private IControl evaluateNewTypedClass(Environment env, Store store, List<IClass> classes,
                                         IShape classType) {
    StoreObject objectToProxy = this.evaluateNewUntypedClass(env, store, classes);
    Optional<ProxyOrValue> conformed = Utils.Utils.conforms(objectToProxy, classType);
    if (conformed.isPresent()) {
      return new ValueControl(conformed.get());
    } else {
      return new ErrorControl();
    }
  }

  private StoreObject evaluateNewUntypedClass(Environment env, Store store, List<IClass> classes) {
    List<ProxyOrValue> paramValues = getParamsFromStore(env, store);
    return new StoreObject(paramValues, getClassToConstruct(classes));
  }

  @Override
  public List<Variable> getVariables() {
    return new ArrayList<>(this.params);
  }

  @Override
  public IExpression renameVariables(Set<Variable> existingVariables) {
    List<Variable> result = new ArrayList<>();
    for (Variable var : this.params) {
      result.add(var.renameVariables(existingVariables));
    }
    return new NewClass(this.className, result);

  }


  private List<ProxyOrValue> getParamsFromStore(Environment env, Store store) {
    List<ProxyOrValue> result = new ArrayList<>();
    for (Variable var : this.params) {
      Location paramLoc = env.get(var);
      ProxyOrValue param = store.get(paramLoc);
      result.add(param);
    }
    return result;
  }

  private IClass getClassToConstruct(List<IClass> classes) {
    for (IClass c : classes) {
      if (c.getClassName().equals(this.className)) {
        return c;
      }
    }
    throw new RuntimeException("Class not found");
  }

  private int numFieldsForClassToConstruct(List<IClass> classes) {
    return getClassToConstruct(classes).numFields();
  }

  @Override
  public int hashCode() {
    return Objects.hash("NewClass", className, params);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof NewClass newClass) {
      return this.className.equals(newClass.className)
          && this.params.equals(newClass.params);
    }
    return false;
  }
  @Override
  public void renameClassesToQualifiedNames(ModuleClassBinding moduleToClassBinder) {
    this.className = moduleToClassBinder.makeQualifiedName(className);
  }
  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    Shape sClass = sClasses.get(this.className);
    List<IType> paramTypes = getParamTypes(tVar);
    if (paramTypes.size() != sClass.fieldTypes.size()) {
      params.add(new TypeError(params.toString(), "The number of parameters does" +
          "not match the type's number of parameters."));
      return true;
    }
    List<Integer> indicesOfTypeErrorParams = sClass.checkConstructorArgsContainsTypeError(paramTypes);
    if (!indicesOfTypeErrorParams.isEmpty()) {
      Utils.Utils.voidMap(indicesOfTypeErrorParams, i -> this.params.set(i,
              new TypeError(this.params.get(i).toString(), "TypeError with param")));
      return true;
    }
    return false;
  }

  private List<IType> getParamTypes(Map<Variable,IType> tVar) {
    List<IType> paramTypes = new ArrayList<>();
    Utils.Utils.voidMap(this.params, var -> paramTypes.add(tVar.get(var)));
    return paramTypes;
  }

  @Override
  public boolean returnTypeEquals(IType varType, Map<Variable, IType> tVar, Map<Variable, Shape> sClasses) {
    return varType.equals(sClasses.get(this.className));
  }
  @Override
  public IType getReturnType(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    return sClasses.get(this.className);
  }
  @Override
  public IExpression copyExpr() {
    List<Variable> paramsCopy = new ArrayList<>();
    Utils.Utils.voidMap(this.params, param -> paramsCopy.add(param.copyVariable()));
    return new NewClass(className.copyVariable(), paramsCopy);
  }
}
