package expressions;

import Closure.Closure;
import Control.IControl;
import Maps.Environment;
import Maps.Location;
import Maps.ProxyOrValue;
import Maps.Store;
import Maps.StoreObject;
import ast.ASTNodes;
import block.Block;
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
import Class.IClass;
import Control.*;
import programs.Kontinuation;
import types.IMethodType;
import types.IType;
import types.Shape;

public class CallMethod implements IExpression {
  Variable object;
  Variable methodName;
  List<Variable> arguments;

  public CallMethod(ASTNodes object, ASTNodes methodName, ASTNodes args, AtomicBoolean valid) {
    this.object = object.convertToVariableOrError(valid);
    this.methodName = methodName.convertToVariableOrError(valid);
    this.arguments = args.convertToFieldOrParamList(valid);
  }

  public CallMethod(Variable object, Variable methodName, List<Variable> result) {
    this.object = object;
    this.methodName = methodName;
    this.arguments = result;
  }

  @Override
  public boolean isError() {
    return false;
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    boolean undefined = false;

    if (this.object.containsUndefinedVariables(definedVariables, definedClassNames)) {
      this.object = new UndefinedVariableError(this.object.toString(),
          "Contains undefined Variable");
      undefined = true;
    }

    List<Variable> afterCheck = new ArrayList<Variable>();
    for (Variable var : this.arguments) {
      if (var.containsUndefinedVariables(definedVariables, definedClassNames)) {
        afterCheck.add( new UndefinedVariableError(var.toString(),
            "Contains undefined Variable"));
        undefined = true;
      } else {
        afterCheck.add(var);
      }
    }
    this.arguments = afterCheck;
    return undefined;
  }

  @Override
  public IControl evaluate(Environment env, Store store, List<IClass> classes, Kontinuation k,
                           CESK cesk) {
    Location objLoc = env.get(this.object);
    ProxyOrValue obj = store.get(objLoc);
    List<ProxyOrValue> paramList = this.getParamsFromStore(env, store);
    if (obj.isObject()) {
      return evaluateMethodOnObject(store, classes, k, cesk, obj, paramList);
    } else if (obj.isProxy()) {
      return evaluateMethodOnProxy(store, k, cesk, obj, paramList);
    } else {
      return new ErrorControl();
    }
  }


  private IControl evaluateMethodOnProxy(Store store, Kontinuation k,
                                         CESK cesk, ProxyOrValue prx, List<ProxyOrValue> paramList) {
    if (!prx.methodTypeMatches(this.methodName, this.arguments)) {
      return new ErrorControl();
    } else {
      List<IType> domainT = prx.getDomainTypes(methodName);
      IType rangeT = prx.getRangeTypes(methodName);
      Optional<List<ProxyOrValue>> conformedArgs = conformArguments(paramList, domainT);
      if (conformedArgs.isEmpty()) {
        return new ErrorControl();
      }
      return adjustEnvironmentAndStack(store, k, cesk, prx, paramList, Optional.of(rangeT));
    }
  }

  private Optional<List<ProxyOrValue>> conformArguments(List<ProxyOrValue> tmp, List<IType> domainT) {
    List<ProxyOrValue> conformedTmps = new ArrayList<>();
    for (int i = 0; i < tmp.size(); i++) {
      Optional<ProxyOrValue> optional = Utils.Utils.conforms(tmp.get(i), domainT.get(i));
      if (optional.isEmpty()) {
        return Optional.empty();
      } else {
        conformedTmps.add(optional.get());
      }
    }
    return Optional.of(conformedTmps);
  }

  private IControl evaluateMethodOnObject(Store store, List<IClass> classes, Kontinuation k, CESK cesk, ProxyOrValue obj, List<ProxyOrValue> paramList) {
    if (!classContainsMethodWithCorrectArgs(obj.getClassName(), this.methodName,
        this.arguments.size(), classes)) {
      return new ErrorControl();
    } else {
      return adjustEnvironmentAndStack(store, k, cesk, obj, paramList, Optional.empty());
    }
  }


  private Search adjustEnvironmentAndStack(Store store, Kontinuation k, CESK cesk,
                                           ProxyOrValue obj, List<ProxyOrValue> paramList,
                                           Optional<IType> rangeType) {
    List<Variable> paramNames = obj.getMethodParamNames(this.methodName);
    Environment newEnv = new Environment(store, obj, paramNames, paramList);
    Block methodBlock = obj.convertMethodToBlock(this.methodName);
    Closure methodClosure = new Closure(newEnv, methodBlock);
    rangeType.ifPresent(k::push);
    k.push(methodClosure);
    cesk.env = newEnv;
    return new Search();
  }


  @Override
  public List<Variable> getVariables() {
    List<Variable> initial = new ArrayList<>(List.of(this.object));
    initial.addAll(this.arguments);
    return initial;
  }

  @Override
  public IExpression renameVariables(Set<Variable> existingVariables) {
    List<Variable> result = new ArrayList<>();
    for (Variable var : this.arguments) {
      result.add(var.renameVariables(existingVariables));
    }
    return new CallMethod(this.object.renameVariables(existingVariables), this.methodName,
        result);
  }


  private List<ProxyOrValue> getParamsFromStore(Environment env, Store store) {
    List<ProxyOrValue> result = new ArrayList<>();
    for (Variable var : this.arguments) {
      Location paramLoc = env.get(var);
      ProxyOrValue param = store.get(paramLoc);
      result.add(param);
    }
    return result;
  }

  @Override
  public int hashCode() {
    return Objects.hash(object, methodName, arguments);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof CallMethod call) {
      return this.object.equals(call.object)
          && this.methodName.equals(call.methodName)
          && this.arguments.equals(call.arguments);
    }
    return false;
  }


  private static boolean classContainsMethodWithCorrectArgs(Variable className, Variable methodName,
                                                            int numArgs, List<IClass> classes) {
    for (IClass c : classes) {
      if (c.getClassName().equals(className)) {
        return c.hasMethodAndCorrectNumberOfParams(methodName, numArgs);
      }
    }
    return false;
  }
  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    IType variableType = tVar.get(this.object);
    if (!variableType.containsMethod(this.methodName)) {
      methodName = new TypeError(methodName.toString(), "Object does not contain this method.");
      return true;
    }
    IMethodType methodType = variableType.getMethodType(this.methodName);
    List<IType> argTypes = getArgumentTypes(tVar);
    if (methodType.argumentNumIsMismatched(argTypes.size())) {
      this.arguments.addFirst(new TypeError("", "Wrong number of arguments for method"));
      return true;
    }
    List<Integer> indicesOfArgTypeErrors = methodType.checkMethodArgsContainsTypeError(argTypes);
    if (!indicesOfArgTypeErrors.isEmpty()) {
      Utils.Utils.voidMap(indicesOfArgTypeErrors, i -> this.arguments.set(i,
              new TypeError(this.arguments.get(i).toString(), "TypeError with method arguments")));
      return true;
    }
    return false;
  }

  @Override
  public boolean returnTypeEquals(IType varType, Map<Variable, IType> tVar, Map<Variable, Shape> sClasses) {
    IType typeOfObject = tVar.get(this.object);
    IMethodType methodType = typeOfObject.getMethodType(this.methodName);
    return methodType.hasReturnType(varType);
  }

  @Override
  public IType getReturnType(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    IType typeOfObject = tVar.get(this.object);
    IMethodType methodType = typeOfObject.getMethodType(this.methodName);
    return methodType.getReturnType();
  }

  private List<IType> getArgumentTypes(Map<Variable,IType> tVar) {
    List<IType> argTypes = new ArrayList<>();
    Utils.Utils.voidMap(this.arguments, var -> argTypes.add(tVar.get(var)));
    return argTypes;
  }

  @Override
  public IExpression copyExpr() {
    List<Variable> methodParams = new ArrayList<>();
    Utils.Utils.voidMap(this.arguments, arg -> methodParams.add(arg.copyVariable()));
    return new CallMethod(object.copyVariable(), methodName.copyVariable(), methodParams);
  }


}
