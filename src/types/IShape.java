package types;

import expressions.Variable;
import java.util.List;
import java.util.Set;

public interface IShape extends IType {
  boolean containsField(Variable fieldName);
  boolean containsMethod(Variable methodName);

  List<Variable> getFieldNames();

  Set<Variable> getMethodNames();

  boolean containsDuplicateFieldOrMethodNames();
}
