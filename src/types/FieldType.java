package types;

import ast.ASTNodes;
import expressions.Variable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class FieldType implements IFieldType {
  Variable fieldName;
  IType fieldType;

  public FieldType(ArrayList<ASTNodes> seq, AtomicBoolean valid) {
    this.fieldName = seq.getFirst().convertToVariableOrError(valid);
    this.fieldType = seq.getLast().convertToTypeOrError(valid);
  }

  @Override
  public IType getType() {
    return fieldType;
  }

  @Override
  public Variable getFieldName() {
    return fieldName;
  }

  @Override
  public boolean hasType(IType varType) {
    return this.fieldType.equals(varType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldName, fieldType);
  }
  @Override
  public boolean equals(Object o) {
    if (o instanceof FieldType other) {
      return this.fieldName.equals(other.fieldName) &&
              this.fieldType.equals(other.fieldType);
    }
    return false;
  }
}
