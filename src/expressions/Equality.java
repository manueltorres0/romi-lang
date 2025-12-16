package expressions;

import ast.ASTNodes;
import cesk.CESK;
import error.UndefinedVariableError;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import Maps.*;
import Control.*;
import Class.IClass;
import programs.Kontinuation;
import types.IType;
import types.NumberType;
import types.Shape;

public class Equality implements IExpression {
  private Variable left;
  private Variable right;

  public Equality(ASTNodes left, ASTNodes right, AtomicBoolean valid) {
    this.left = left.convertToVariableOrError(valid);
    this.right = right.convertToVariableOrError(valid);
  }

  public Equality(Variable left, Variable right) {
    this.left = left;
    this.right = right;
  }

  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    boolean undefined = false;

    if (this.left.containsUndefinedVariables(definedVariables, definedClassNames)) {
      undefined = true;
      this.left = new UndefinedVariableError(this.left.toString(),
          "Contains undefined Variable");
    }

    if  (this.right.containsUndefinedVariables(definedVariables, definedClassNames)) {
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

    if (leftVal.equals(rightVal)) {
      return new ValueControl(new GoodNumber(0));
    } else {
      return new ValueControl(new GoodNumber(1));
    }
  }

  @Override
  public boolean isError() {
    return false;
  }


  @Override
  public int hashCode() {
    return Objects.hash("Equality", left, right);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Equality eq) {
      return this.left.equals(eq.left) && this.right.equals(eq.right);
    }
    return false;
  }

  @Override
  public List<Variable> getVariables() {
    return List.of(this.left, this.right);
  }


  @Override
  public IExpression renameVariables(Set<Variable> existingVariables) {
    return new Equality(this.left.renameVariables(existingVariables),
        this.right.renameVariables(existingVariables));
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    return false;
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
    return new Equality(left.copyVariable(), right.copyVariable());
  }

}
