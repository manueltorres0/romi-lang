package statements;

import Maps.Environment;
import Maps.Location;
import Maps.Store;
import Utils.ModuleClassBinding;
import block.Block;
import cesk.CESK;
import error.TypeError;
import expressions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ast.*;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import Control.*;
import programs.Kontinuation;
import Class.*;
import types.IType;
import types.Shape;

public class AssignmentStatement implements IStatement {
  private final Variable variable;
  public IExpression expression;

  public AssignmentStatement(ArrayList<ASTNodes> seq, AtomicBoolean valid) {
    this.variable = seq.get(0).convertToVariableOrError(valid);
    this.expression = seq.get(2).convertToExpressionOrError(valid);
  }

  public AssignmentStatement(Variable variable, IExpression expression) {
    this.variable = variable.copyVariable();
    this.expression = expression.copyExpr();
  }


  public List<IStatement> evaluateStatement(GoodNumber n, HashMap<Variable, GoodNumber> store) {
    store.put(variable, n);
    return new ArrayList<>();
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    if (definedVariables.contains(variable)) {
      if (this.expression.containsUndefinedVariables(definedVariables, definedClassNames)) {
        return true;
      } else {
        return false;
      }
    }
    return true;
  }

  @Override
  public IControl findNextExpression(Kontinuation k, CESK cesk) {
    return new ExpressionControl(expression);
  }

  @Override
  public IControl evaluateStatement(Environment env, Store store, IControl control, Block program, List<IClass> classes) {
    Location varLoc = env.get(variable);
    store.put(varLoc, control.getValue());
    return new Search();
  }

  @Override
  public IStatement copy() {
    return new AssignmentStatement(this.variable, this.expression);
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar, IType returnType) {
    IType varType = tVar.get(this.variable);
    if (expression.containsTypeError(sClasses, tVar)) {
      return true;
    } else if (!expression.returnTypeEquals(varType, tVar, sClasses)) {
      this.expression = new TypeError(expression.toString(),
          "Expression return type does not match Variable type");
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof AssignmentStatement ass) {
      return this.variable.equals(ass.variable)
          && this.expression.equals(ass.expression);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash("Assignment", variable, expression);
  }

  @Override
  public boolean isNestedBlock() {
    return false;
  }

  @Override
  public Block getNestedBlockOrThrow() {
    throw new IllegalStateException("Statement is not a nested block");
  }

  @Override
  public void renameClassesToQualifiedNames(ModuleClassBinding moduleToClassBinder) {
    expression.renameClassesToQualifiedNames(moduleToClassBinder);
  }

}
