package declarations;

import Control.ExpressionControl;
import Utils.ModuleClassBinding;
import ast.ASTNodes;
import error.TypeError;
import expressions.IExpression;
import expressions.Variable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import Control.*;
import Maps.*;
import types.IType;
import types.Shape;

public class Declaration implements IDeclaration {
  public Variable variable;
  public IExpression expression;


  public Declaration(ArrayList<ASTNodes> seq, AtomicBoolean valid) {
    this.variable = seq.get(1).convertToVariableOrError(valid);
    this.expression = seq.get(2).convertToExpressionOrError(valid);
  }
  private Declaration(Variable var, IExpression expr) {
    this.variable = var;
    this.expression = expr;
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    if (expression.containsUndefinedVariables(definedVariables, definedClassNames)) {
      return true;
    } else {
      definedVariables.add(variable);
      return false;
    }
  }

  @Override
  public IControl findRHS() {
    return new ExpressionControl(expression);
  }

  @Override
  public IControl evaluateDeclaration(Environment env, Store store, IControl control) {
    Location newLocation = new Location();
    env.put(this.variable, newLocation);
    store.put(newLocation, control.getValue());
    return new Search();
  }

  @Override
  public void renameClassesToQualifiedNames(ModuleClassBinding moduleToClassBinder) {
    this.expression.renameClassesToQualifiedNames(moduleToClassBinder);
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    if (expression.containsTypeError(sClasses, tVar)) {
      tVar.put(variable, new TypeError(expression.toString(), "Expression resulted in type error"));
      return true;
    } else {
      tVar.put(variable, expression.getReturnType(sClasses, tVar));
      return false;
    }
  }

  @Override
  public IDeclaration copyDecl() {
    return new Declaration(variable.copyVariable(), expression.copyExpr());
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Declaration declaration) {
      return this.variable.equals(declaration.variable)
          && this.expression.equals(declaration.expression);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.variable.hashCode() + this.expression.hashCode();
  }
}
