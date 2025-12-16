package expressions;

import Control.*;
import Maps.Environment;
import Maps.Location;
import Maps.ProxyOrValue;
import Maps.Store;
import ast.ASTNodes;
import cesk.CESK;
import error.TypeError;
import error.UndefinedVariableError;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import Class.IClass;
import programs.Kontinuation;
import types.IType;
import types.NumberType;
import types.Shape;

public class Addition implements IExpression {
  private Variable left;
  private Variable right;

  public Addition(ASTNodes left, ASTNodes right, AtomicBoolean valid) {
    this.left = left.convertToVariableOrError(valid);
    this.right = right.convertToVariableOrError(valid);
  }

  public Addition(Variable left, Variable right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean isError() {
    return false;
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    boolean undefined = false;

    if (this.left.containsUndefinedVariables(definedVariables, definedClassNames)) {
      undefined = true;
      this.left = new UndefinedVariableError(this.left.toString(),
          "Contains undefined Variable");
    }

    if (this.right.containsUndefinedVariables(definedVariables, definedClassNames)) {
      undefined = true;
      this.right = new UndefinedVariableError(this.right.toString(),
          "Contains undefined Variable");
    }
    return undefined;

  }

  @Override
  public IControl evaluate(Environment env, Store store, List<IClass> classes,
                           Kontinuation k, CESK cesk) {
    Location locLeft = env.get(this.left);
    Location locRight = env.get(this.right);

    ProxyOrValue leftVal = store.get(locLeft);
    ProxyOrValue rightVal = store.get(locRight);

    if (leftVal.isObject() || rightVal.isObject() || leftVal.isProxy() || rightVal.isProxy()) {
      return new ErrorControl();
    } else {
      return new ValueControl(new GoodNumber(leftVal.getValue() + rightVal.getValue()));
    }
  }

  @Override
  public List<Variable> getVariables() {
    return List.of(this.left, this.right);
  }

  @Override
  public IExpression renameVariables(Set<Variable> existingVariables) {
    return new Addition(this.left.renameVariables(existingVariables),
        this.right.renameVariables(existingVariables));
  }

  @Override
  public int hashCode() {
    return Objects.hash("Addition", left, right);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Addition add) {
      return this.left.equals(add.left) && this.right.equals(add.right);
    }
    return false;
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    boolean hasTypeError = false;
    if (!(tVar.get(left).isNumber())) {
      hasTypeError = true;
      left = new TypeError(left.toString(), "Not a number");
    }
    if (!(tVar.get(right).isNumber())) {
      hasTypeError = true;
      right = new TypeError(right.toString(), "Not a number");
    }
    return hasTypeError;
  }

  @Override
  public boolean returnTypeEquals(IType varType, Map<Variable, IType> tVar, Map<Variable, Shape> sClasses) {
    return varType.isNumber();
  }

  @Override
  public IType getReturnType(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    return new NumberType();
  }

  @Override
  public IExpression copyExpr() {
    return new Addition(left.copyVariable(), right.copyVariable());
  }

}
