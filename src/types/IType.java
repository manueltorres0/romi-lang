package types;

import Closure.ClosureOrReturnType;
import expressions.Variable;

public interface IType extends ClosureOrReturnType {

  boolean isNumber();
  boolean containsField(Variable fieldName);

  boolean containsMethod(Variable methodName);

  IMethodType getMethodType(Variable methodName);

  IFieldType getFieldType(Variable fieldName);

  boolean isShape();
}
