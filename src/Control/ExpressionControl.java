package Control;

import cesk.CESK;
import expressions.*;
import Maps.*;
import java.util.List;
import Class.IClass;
import programs.Kontinuation;

public class ExpressionControl implements IControl {
  IExpression expression;

  public ExpressionControl(IExpression expression) {
    this.expression = expression;
  }


  @Override
  public IControl evaluate(Environment env, Store store, List<IClass> classes,
                           Kontinuation k, CESK cesk) {
    return this.expression.evaluate(env, store, classes, k , cesk);
  }

  @Override
  public ProxyOrValue getValue() {
    throw new IllegalStateException("Cannot get value from Expression Control");
  }
}
