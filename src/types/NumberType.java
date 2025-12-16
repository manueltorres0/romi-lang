package types;

import java.util.List;

import Control.IControl;
import Maps.Environment;
import Maps.Store;
import cesk.CESK;
import expressions.Variable;
import programs.Kontinuation;
import Class.*;

public class NumberType implements IType {
  @Override
  public boolean isNumber() {
    return true;
  }

  @Override
  public boolean containsField(Variable fieldName) {
    return false;
  }

  @Override
  public boolean containsMethod(Variable methodName) {
    return false;
  }

  @Override
  public IMethodType getMethodType(Variable methodName) {
    throw new IllegalStateException("No method type for a number.");
  }

  @Override
  public IFieldType getFieldType(Variable fieldName) {
    throw new IllegalStateException("No fields for a number. Should never get here because of prior checks.");
  }


  @Override
  public boolean equals(Object o) {
    return o instanceof NumberType;
  }


  @Override
  public boolean isShape() {
    return false;
  }

  @Override
  public IControl evaluateStatementOrDeclaration(Environment env, Store store, IControl control, List<IClass> classes) {
    throw new IllegalStateException("number types do not contain statements or declarations");
  }

  @Override
  public IControl findNextExpression(Kontinuation k, CESK cesk) {
    throw new IllegalStateException("shapes do not contain a next expression");
  }

  @Override
  public Environment getEnv() {
    throw new IllegalStateException("Return types do not hold environments.");
  }

  @Override
  public boolean isReturnType() {
    return true;
  }
}
