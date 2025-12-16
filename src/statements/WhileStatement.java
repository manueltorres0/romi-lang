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
import Class.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import Control.*;
import programs.Kontinuation;
import types.IType;
import types.Shape;

public class WhileStatement implements IStatement {
  public final IExpression expression;
  public final IBlock block;

  public WhileStatement(ArrayList<ASTNodes> seq, AtomicBoolean valid) {
    this.expression = seq.get(1).convertToExpressionOrError(valid);
    this.block = seq.get(2).convertToBlockOrError(valid);

  }

  public WhileStatement(IExpression expression, IBlock block) {
    this.expression = expression.copyExpr();
    this.block = block.copy();
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    boolean undefined = false;

    if (expression.containsUndefinedVariables(definedVariables, definedClassNames)) {
      undefined = true;
    }

    if (block.containsUndefinedVariables(definedVariables, definedClassNames)) {
      undefined = true;
    }

    return undefined;
  }

  @Override
  public IStatement copy() {
    return new WhileStatement(this.expression, this.block);
  }

  @Override
  public IControl evaluateStatement(Environment env, Store store, IControl control, Block instructions, List<IClass> classes) {
    if (control.getValue().isTrue()) {
      instructions.addNextToDo(this.copy());
      instructions.addNextToDo(this.block.copy());
    }
    return new Search();
  }

  @Override
  public IControl findNextExpression(Kontinuation k, CESK cesk) {
    return new ExpressionControl(expression);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof WhileStatement whileStatement) {
      return this.expression.equals(whileStatement.expression)
          && this.block.equals(whileStatement.block);
    }
    return false;
  }
  @Override
  public int hashCode() {
    return Objects.hash("While", expression, block);
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
    block.renameClassesToQualifiedNames(moduleToClassBinder);
  }
  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar, IType returnType) {
    boolean hasTypeError = false;
    hasTypeError |= expression.containsTypeError(sClasses, tVar);
    hasTypeError |= block.containsTypeError(sClasses, tVar, null);
    return hasTypeError;
  }
}
