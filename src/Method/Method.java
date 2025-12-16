package Method;

import Utils.ModuleClassBinding;
import Utils.Utils;
import ast.ASTNodes;
import block.Block;
import expressions.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import types.IMethodType;
import types.IType;
import types.Shape;

public class Method implements IMethod {
  Variable methodName;
  List<Variable> params;
  Block block;

  public Method(ArrayList<ASTNodes> body, ASTNodes expression, AtomicBoolean valid) {
    body.removeFirst();
    this.methodName = body.removeFirst().convertToVariableOrError(valid);
    this.params = body.removeFirst().convertToFieldOrParamList(valid);
    this.block = new Block(body, expression, valid);
  }

  private Method(Variable methodName, List<Variable> params, Block block) {
    this.methodName = methodName;
    this.params = params;
    this.block = block;
  }

  @Override
  public boolean containsDuplicateMethodNames(Set<Variable> methodNames) {
    if (methodNames.contains(methodName)) {
      return true;
    } else {
      methodNames.add(methodName);
      return false;
    }
  }

  @Override
  public boolean containsDuplicateParamNames() {
    Set<Variable> paramNames = new HashSet<>();
    return Utils.hasElementMatching(var -> var.containsDuplicateFieldOrParamNames(paramNames), this.params);
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {

    HashSet<Variable> scopedDefVariables = new HashSet<>(definedVariables);
    scopedDefVariables.add(new Variable("this"));
    scopedDefVariables.addAll(this.params);

    return this.block.containsUndefinedVariables(scopedDefVariables, definedClassNames);

  }


  @Override
  public int hashCode() {
    return Objects.hash(methodName, params, block);
  }


  @Override
  public boolean equals(Object other) {
    if (other instanceof Method method) {
      return this.methodName.equals(method.methodName)
          && this.params.equals(method.params)
          && this.block.equals(method.block);
    }
    return false;
  }

  @Override
  public Variable getMethodName() {
    return this.methodName;
  }

  @Override
  public boolean hasNumParams(int numArgs) {
    return this.params.size() == numArgs;
  }

  @Override
  public Block convertToBlock() {
    return this.block.copy();
  }

  @Override
  public List<Variable> getParamNames() {
    return this.params;
  }

  @Override
  public void renameClassesToQualifiedNames(ModuleClassBinding moduleToClassBinder) {
    block.renameClassesToQualifiedNames(moduleToClassBinder);
  }

  // INVARIANT: The method exists because of class type check done before this
  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    Map<Variable, IType> methodTVar = new HashMap<>(tVar);
    IType thisClassShape = tVar.get(new Variable("this"));
    IMethodType methodType = thisClassShape.getMethodType(this.methodName);
    if (methodType.numParams() != params.size()) {
      return true;
    }
    methodType.addTypedParams(methodTVar, params);
    return block.containsTypeError(sClasses, methodTVar, methodType.getReturnType());
    }

  @Override
  public IMethod copyMethod() {
    List<Variable> paramsCopy = new ArrayList<>();
    Utils.voidMap(params, param -> paramsCopy.add(param.copyVariable()));
    return new Method(methodName.copyVariable(),paramsCopy, block.copy());
  }
}
