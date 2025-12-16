package statements;

import Utils.ModuleClassBinding;
import block.Block;
import cesk.CESK;
import java.util.ArrayList;

import Maps.Environment;
import Maps.Store;
import ast.*;
import expressions.*;
import block.IBlock;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import Control.*;
import programs.Kontinuation;
import Class.*;
import types.IType;
import types.Shape;

public class IfStatement implements IStatement {
  public final IExpression expression;
  public final IBlock then;
  public final IBlock elseBlock;

  public IfStatement(ArrayList<ASTNodes> seq, AtomicBoolean valid) {
    this.expression = seq.get(1).convertToExpressionOrError(valid);
    this.then = seq.get(2).convertToBlockOrError(valid);
    this.elseBlock = seq.get(3).convertToBlockOrError(valid);
  }
  public IfStatement(IExpression expression, IBlock then, IBlock elseBlock) {
    this.expression = expression.copyExpr();
    this.then = then.copy();
    this.elseBlock = elseBlock.copy();
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    boolean undefined = false;

    if (expression.containsUndefinedVariables(definedVariables, definedClassNames)) {
      undefined = true;
    }

    if (then.containsUndefinedVariables(definedVariables, definedClassNames)) {
      undefined = true;
    }

    if (elseBlock.containsUndefinedVariables(definedVariables, definedClassNames)) {
      undefined = true;
    }

    return undefined;
  }

  @Override
  public IStatement copy() {
    return new IfStatement(this.expression, this.then, this.elseBlock);
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar, IType returnType) {
    boolean hasTypeError = false;
    hasTypeError |= expression.containsTypeError(sClasses, tVar);
    hasTypeError |= then.containsTypeError(sClasses, tVar, null);
    hasTypeError |= elseBlock.containsTypeError(sClasses, tVar, null);
    return hasTypeError;
  }

  @Override
  public IControl findNextExpression(Kontinuation k, CESK cesk) {
    return new ExpressionControl(expression);
  }

  @Override
  public IControl evaluateStatement(Environment env, Store store, IControl control, Block instructions, List<IClass> classes) {
    if (control.getValue().isTrue()) {
      instructions.addNextToDo(this.then.copy());
    } else {
      instructions.addNextToDo(this.elseBlock.copy());
    }
    return new Search();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof IfStatement ifStatement) {
      return this.expression.equals(ifStatement.expression)
          && this.then.equals(ifStatement.then)
          && this.elseBlock.equals(ifStatement.elseBlock);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash("If", expression, then, elseBlock);
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
    then.renameClassesToQualifiedNames(moduleToClassBinder);
    elseBlock.renameClassesToQualifiedNames(moduleToClassBinder);
  }

}
