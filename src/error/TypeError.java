package error;

import java.util.List;

import Control.IControl;
import Maps.Environment;
import Maps.Store;
import expressions.Variable;
import java.util.Optional;
import types.IFieldType;
import types.IMethodType;
import types.IShape;
import types.IType;
import Class.*;

public class TypeError extends ErrorNode implements IType {

  public TypeError(String errorArgs, String errorMessage) {
    super(errorArgs, errorMessage);
  }

  @Override
  public boolean isNumber() {
    return false;
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
    throw new IllegalStateException("No method type for a type error.");
  }

  @Override
  public IFieldType getFieldType(Variable fieldName) {
    throw new IllegalStateException("No fields for a type error. " +
        "Should never get here because of prior checks.");
  }

  @Override
  public boolean equals(Object o) {
    return false;
  }

  @Override
  public boolean isShape() {
    return false;
  }


  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public Environment getEnv() {
    throw new IllegalStateException("Should not have passed type check");
  }

  @Override
  public boolean isReturnType() {
    throw new IllegalStateException("Should not have passed type check");
  }

  @Override
  public int getNumParams() {
    throw new IllegalStateException("No params for type error");
  }

  @Override
  public List<IType> getParamTypes() {
    throw new IllegalStateException("No param types for type error");
  }

  @Override
  public Optional<IShape> getClassType() {
    throw new IllegalStateException("Should not have passed type check");
  }
}
