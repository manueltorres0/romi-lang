package types;

import expressions.Variable;

public interface IFieldType {

  IType getType();
  Variable getFieldName();

  boolean hasType(IType varType);
}
