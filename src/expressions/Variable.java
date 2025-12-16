package expressions;

import static expressions.UtilClass.hashToLetters;

import Maps.*;
import ast.ASTNodes;
import Control.*;
import cesk.CESK;
import Class.IClass;
import java.util.List;
import java.util.Map;
import java.util.Set;
import programs.Kontinuation;
import types.IType;
import types.Shape;


public class Variable implements IExpression {
  private final String var;

  public Variable(ASTNodes name) {
    this.var = name.toString();
  }

  public Variable(String var) {
    this.var = var;
  }

  @Override
  public boolean isError() {
    return false;
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    return !definedVariables.contains(this);
  }

  public boolean containsUndefinedClassNames(Set<Variable> definedClassNames) {
    return !definedClassNames.contains(this);
  }

  @Override
  public int hashCode() {
    return var.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Variable) {
      return var.equals(((Variable) o).var);
    }
    return false;
  }

  public boolean containsDuplicateFieldOrParamNames(Set<Variable> fieldOrParamNames) {
    if (fieldOrParamNames.contains(this)) {
      return true;
    } else {
      fieldOrParamNames.add(this);
      return false;
    }
  }

  @Override
  public String toString() {
    return var;
  }

  @Override
  public IControl evaluate(Environment env, Store store, List<IClass> classes,
                           Kontinuation k, CESK cesk) {
    Location valLoc = env.get(this);
    ProxyOrValue value = store.get(valLoc);
    return new ValueControl(value);
  }

  @Override
  public List<Variable> getVariables() {
    return List.of(this);
  }

  @Override
  public Variable renameVariables(Set<Variable> existingVariables) {
    return new Variable(hashToLetters(existingVariables));
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    return false;
  }

  public Variable copyVariable() {
    return new Variable(this.var);
  }

  @Override
  public boolean returnTypeEquals(IType varType, Map<Variable, IType> tVar, Map<Variable, Shape> sClasses) {
    return varType.equals(tVar.get(this));
  }
  @Override
  public IType getReturnType(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    return tVar.get(this);
  }

  @Override
  public IExpression copyExpr() {
    return copyVariable();
  }
}

