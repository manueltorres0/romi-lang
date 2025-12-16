package Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Maps.ProxyOrValue;
import Maps.StoreObject;
import block.Block;
import expressions.Variable;
import types.Shape;
import Class.*;
import types.*;

public class MyProxy implements ProxyOrValue {
  Shape shape;
  StoreObject obj;

  public MyProxy(StoreObject obj, Shape shape) {
    this.shape = shape;
    this.obj = obj;
  }

  @Override
  public boolean isObject() {
    return false;
  }

  @Override
  public boolean isProxy() {
    return true;
  }

  @Override
  public void mutate(Variable fieldName, ProxyOrValue value) {
    obj.mutate(fieldName, value);
  }

  @Override
  public StoreObject getObject() {
    return this.obj;
  }

  @Override
  public double getValue() {
    throw new IllegalStateException("A proxy does not have a value");
  }

  @Override
  public Variable getClassName() {
    return obj.getClassName();
  }

  @Override
  public Block convertMethodToBlock(Variable methodName) {
    return this.obj.convertMethodToBlock(methodName);
  }

  @Override
  public boolean hasField(Variable fieldName) {
    return this.obj.hasField(fieldName);
  }

  @Override
  public ProxyOrValue getField(Variable fieldName) {
    return obj.getField(fieldName);
  }

  @Override
  public boolean isTrue() {
    return false;
  }

  @Override
  public List<Variable> getMethodParamNames(Variable methodName) {
    return this.obj.getMethodParamNames(methodName);
  }

  @Override
  public boolean methodTypeMatches(Variable methodName, List<Variable> arguments) {
    return shape.containsMethod(methodName)
        && !shape.getMethodType(methodName).argumentNumIsMismatched(arguments.size());
  }

  @Override
  public boolean equals(Object obj, Set<Pair<StoreObject, StoreObject>> visited) {
    if (obj instanceof MyProxy) {
      MyProxy other = (MyProxy) obj;
      return this.shape.equals(other.shape) && this.obj.equals(other.obj);
    } else if (obj instanceof StoreObject storeObject) {
      return this.obj.equals(storeObject);
    }
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    return this.equals(obj, new HashSet<>());
  }

  @Override
  public boolean containsField(List<IClass> classes, Variable fieldName) {
    return this.obj.containsField(classes, fieldName);
  }

  @Override
  public boolean hasShape(Shape shape) {
    return this.shape.equals(shape);
  }

  @Override
  public List<IType> getDomainTypes(Variable methodName) {
    IMethodType methodType = shape.getMethodType(methodName);
    return methodType.getParamTypes();
  }

  @Override
  public IType getRangeTypes(Variable methodName) {
    return shape.getMethodType(methodName).getReturnType();
  }

  @Override
  public IType getFieldType(Variable fieldName) {
    return shape.getFieldType(fieldName).getType();
  }
}
