package Maps;

import Utils.Pair;
import block.Block;
import java.util.List;
import java.util.Map;
import java.util.Set;

import expressions.Variable;
import Class.*;

public interface ObjectOrNumber {
  boolean isObject();

  boolean containsField(List<IClass> classes, Variable fieldName);

  void mutate(Variable fieldName, ObjectOrNumber value);

  StoreObject getObject();

  double getValue();

  boolean equals(Object obj);

  boolean equals(Object obj, Set<Pair<StoreObject, StoreObject>> visited);

  Variable getClassName();

  Block convertMethodToBlock(Variable methodName);

  boolean hasField(Variable fieldName);

  ObjectOrNumber getField(Variable fieldName);

  boolean isTrue();

  List<Variable> getMethodParamNames(Variable methodName);
}
