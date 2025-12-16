package Maps;

import Utils.Pair;
import block.Block;
import java.util.List;
import java.util.Set;

import expressions.Variable;
import Class.*;
import types.IType;
import types.Shape;

public interface ProxyOrValue {
  boolean isObject();

  boolean isProxy();

  boolean containsField(List<IClass> classes, Variable fieldName);

  void mutate(Variable fieldName, ProxyOrValue value);

  StoreObject getObject();

  double getValue();

  boolean equals(Object obj);

  boolean equals(Object obj, Set<Pair<StoreObject, StoreObject>> visited);

  Variable getClassName();

  Block convertMethodToBlock(Variable methodName);

  boolean hasField(Variable fieldName);

  ProxyOrValue getField(Variable fieldName);

  boolean isTrue();

  List<Variable> getMethodParamNames(Variable methodName);

  boolean methodTypeMatches(Variable methodName, List<Variable> arguments);

  boolean hasShape(Shape shape);

  List<IType> getDomainTypes(Variable methodName);

  IType getRangeTypes(Variable methodName);

  IType getFieldType(Variable fieldName);
}
