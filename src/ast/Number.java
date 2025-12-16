package ast;

import error.ErrorNode;
import expressions.IExpression;
import expressions.GoodNumber;
import expressions.Variable;
import java.util.concurrent.atomic.AtomicBoolean;
import tokenizer.Token;
import types.IType;
import types.NumberType;

public class Number extends AbstractNodes {
  private final double num;

  public Number(Token token) {
    this.num = token.getNumericValue();

  }

  public Number(double num) {
    this.num = num;
  }

  @Override
  public String toString() {
    return String.valueOf(num);
  }

  @Override
  public IExpression convertToExpressionOrError(AtomicBoolean valid) {
    return new GoodNumber(this.num);
  }

  public Variable convertToVariableOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A expressions.GoodNumber is not a expressions.Variable");
  }

  @Override
  public Variable convertToModuleNameOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "A number is not a module name.");
  }


  @Override
  public IType convertToTypeOrError(AtomicBoolean valid) {
    valid.set(false);
    return new ErrorNode(this.toString(), "Not a valid Type ");
  }
}
