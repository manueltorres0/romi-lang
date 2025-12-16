package Maps;

import block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import Class.*;
import Method.IMethod;
import expressions.GoodNumber;
import expressions.Variable;
import Utils.*;
import types.IType;
import types.Shape;

public final class StoreObject implements ProxyOrValue {
  private final Variable className;
  private final Map<Variable, ProxyOrValue> fields;
  private final Map<Variable, IMethod> methods;

  public StoreObject(List<ProxyOrValue> paramValues, IClass classToConstruct) {
    this.className = classToConstruct.getClassName();
    this.fields = classToConstruct.makeFieldMap(paramValues);
    this.methods = classToConstruct.makeMethodMap();
  }

  @Override
  public boolean isObject() {
    return true;
  }

  @Override
  public boolean isProxy() {
    return false;
  }

  @Override
  public boolean containsField(List<IClass> classes, Variable fieldName) {
    return Utils.hasElementMatching(myClass -> {
      if (myClass.getClassName().equals(className)) {
        return myClass.hasField(fieldName);
      }
      return false;
    }, classes);
  }

  @Override
  public void mutate(Variable fieldName, ProxyOrValue value) {
    this.fields.put(fieldName, value);
  }

  @Override
  public StoreObject getObject() {
    return this;
  }

  @Override
  public double getValue() {
    throw new IllegalStateException("Object is not a number, so cannot get value of object.");
  }

  @Override
  public int hashCode() {
    List<Variable> fieldNames = new ArrayList<>(fields.keySet());
    return Objects.hash(className, fieldNames, methods);
  }

  @Override
  public boolean equals(Object obj) {
    return equals(obj, new HashSet<>());
  }

  @Override
  public boolean equals(Object obj, Set<Pair<StoreObject, StoreObject>> visited) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof StoreObject other)) {
      return false;
    }
    Pair<StoreObject, StoreObject> pair = new Pair<>(this, other);
    if (visited.contains(pair)) {
      return true;
    }
    visited.add(pair);
    boolean result = this.className.equals(other.className) && this.methods.equals(other.methods);
    if (result) {
      for (Variable key : this.fields.keySet()) {
        ProxyOrValue thisVal = fields.get(key);
        ProxyOrValue otherVal = other.fields.get(key);
        result = result && (thisVal.equals(otherVal, visited));
      }
    }
    return result;
  }

  @Override
  public Variable getClassName() {
    return this.className;
  }

  @Override
  public Block convertMethodToBlock(Variable methodName) {
    IMethod method = this.methods.get(methodName);
    return method.convertToBlock();
  }

  @Override
  public boolean hasField(Variable fieldName) {
    return this.fields.containsKey(fieldName);
  }

  @Override
  public ProxyOrValue getField(Variable fieldName) {
    return this.fields.get(fieldName);
  }

  @Override
  public boolean isTrue() {
    return false;
  }

  @Override
  public List<Variable> getMethodParamNames(Variable methodName) {
    IMethod m = this.methods.get(methodName);
    return m.getParamNames();
  }

  @Override
  public boolean methodTypeMatches(Variable methodName, List<Variable> arguments) {
    throw new IllegalStateException("Store objects do not have type information");
  }

  @Override
  public boolean hasShape(Shape shape) {
    throw new IllegalStateException("Store objects do not have shapes");
  }

  @Override
  public List<IType> getDomainTypes(Variable methodName) {
    throw new IllegalStateException("Store objects do not have type information");
  }

  @Override
  public IType getRangeTypes(Variable methodName) {
    throw new IllegalStateException("Store objects do not have type information");
  }

  @Override
  public IType getFieldType(Variable fieldName) {
    throw new IllegalStateException("Store objects do not have type information");
  }

  public boolean firstOrderCheck(Shape shape) {
    return  shape.hasSameFieldNames(this.fields.keySet())
            && shape.fieldValuesConform(this.fields)
            && shape.hasSameMethodNames(this.methods.keySet())
            && shape.hasSameParamCount(new HashMap<>(this.methods));
  }

}
