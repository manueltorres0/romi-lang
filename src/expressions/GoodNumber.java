package expressions;

import Utils.Pair;
import block.Block;
import cesk.CESK;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import Class.*;
import Control.*;
import Maps.Environment;
import Maps.ProxyOrValue;
import Maps.Store;
import Maps.StoreObject;
import programs.Kontinuation;
import types.IType;
import types.NumberType;
import types.Shape;

public class GoodNumber implements IExpression, ProxyOrValue {
  private final double value;

  public GoodNumber(double num) {
    value = num;
  }

  public double getValue() {
    return this.value;
  }

  @Override
  public boolean isError() {
    return false;
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    return false;
  }

  @Override
  public IControl evaluate(Environment env, Store store, List<IClass> classes,
                           Kontinuation k, CESK cesk) {
    return new ValueControl(this);
  }

  @Override
  public List<Variable> getVariables() {
    return List.of();
  }

  @Override
  public IExpression renameVariables(Set<Variable> existingVariables) {
    throw new IllegalStateException("should not rename variables in good numbers");
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    return false;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public boolean isTrue() {
    return (value > -0.00001 && value < 0.000001);
  }

  @Override
  public List<Variable> getMethodParamNames(Variable methodName) {
    throw new IllegalStateException("should not get method param names in good numbers");
  }

  @Override
  public boolean methodTypeMatches(Variable methodName, List<Variable> arguments) {
    throw new IllegalStateException("should not check for method type matches in good numbers");
  }

  @Override
  public boolean hasShape(Shape shape) {
    throw new IllegalStateException("should not check for the shape of a good number");
  }

  @Override
  public List<IType> getDomainTypes(Variable methodName) {
    throw new IllegalStateException("good numbers have no domain types");
  }

  @Override
  public IType getRangeTypes(Variable methodName) {
    throw new IllegalStateException("good numbers have no range types");
  }

  @Override
  public IType getFieldType(Variable fieldName) {
    throw new IllegalStateException("Store objects do not have type information");
  }

  @Override
  public boolean isObject() {
    return false;
  }

  @Override
  public boolean isProxy() {
    return false;
  }

  @Override
  public boolean containsField(List<IClass> classes, Variable fieldName) {
    throw new IllegalStateException("good numbers do not have fields");
  }

  @Override
  public void mutate(Variable fieldName, ProxyOrValue value) {
    throw new IllegalStateException("numbers should not mutate");
  }

  @Override
  public StoreObject getObject() {
    throw new IllegalStateException("Good number is not an object");
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof GoodNumber num) {
      return (Math.abs(this.value - num.value) < 0.0000000001);
    }
    return false;
  }

  @Override
  public boolean equals(Object obj, Set<Pair<StoreObject, StoreObject>> visited) {
    return this.equals(obj);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public Variable getClassName() {
    throw new IllegalStateException("good numbers are not objects");
  }

  @Override
  public Block convertMethodToBlock(Variable methodName) {
    throw new IllegalStateException("good numbers are not methods, and cannot be converted");
  }

  @Override
  public boolean hasField(Variable fieldName) {
    throw  new IllegalStateException("good numbers do not have fields");
  }

  @Override
  public ProxyOrValue getField(Variable fieldName) {
    throw  new IllegalStateException("good numbers do not have fields");
  }

  @Override
  public boolean returnTypeEquals(IType varType, Map<Variable, IType> tVar, Map<Variable, Shape> sClasses) {
    return varType.isNumber();
  }
  @Override
  public IType getReturnType(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    return new NumberType();
  }
  @Override
  public IExpression copyExpr() {
    return new GoodNumber(value);
  }

}
