package expressions;

import Control.*;
import Maps.Environment;
import Maps.Location;
import Maps.ProxyOrValue;
import Maps.Store;
import Utils.ModuleClassBinding;
import ast.ASTNodes;
import cesk.CESK;
import error.TypeError;
import error.UndefinedVariableError;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import programs.Kontinuation;
import Class.IClass;
import types.IType;
import types.NumberType;
import types.Shape;

public class InstanceOf implements IExpression {
  public Variable object;
  public Variable className;

  public InstanceOf(ASTNodes object, ASTNodes className, AtomicBoolean valid) {
    this.object = object.convertToVariableOrError(valid);
    this.className = className.convertToVariableOrError(valid);
  }

  public InstanceOf(Variable object, Variable className) {
    this.object = object;
    this.className = className;
  }

  @Override
  public boolean isError() {
    return false;
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {

    boolean undefined = false;

    if (this.object.containsUndefinedVariables(definedVariables, definedClassNames)) {
      this.object = new UndefinedVariableError(this.object.toString(),
          "Contains undefined Variables");
      undefined = true;
    }

    if (this.className.containsUndefinedClassNames(definedClassNames)) {
      this.className = new UndefinedVariableError(this.className.toString(),
          "Contains undefined Class");
      undefined = true;
    }

    return undefined;
  }

  @Override
  public IControl evaluate(Environment env, Store store, List<IClass> classes, Kontinuation k,
                           CESK cesk) {
    Location objLoc = env.get(this.object);
    ProxyOrValue obj = store.get(objLoc);

    if ((obj.isObject() || obj.isProxy()) && obj.getClassName().equals(this.className)) {
      return new ValueControl(new GoodNumber(0));
    } else {
      return new ValueControl(new GoodNumber(1));
    }
  }

  @Override
  public List<Variable> getVariables() {
    return List.of(this.object);
  }

  @Override
  public IExpression renameVariables(Set<Variable> existingVariables) {
    return new InstanceOf(this.object.renameVariables(existingVariables), this.className);

  }

  @Override
  public int hashCode() {
    return Objects.hash("InstanceOf", object, className);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof InstanceOf ins) {
      return this.object.equals(ins.object) && this.className.equals(ins.className);
    }
    return false;
  }

  @Override
  public void renameClassesToQualifiedNames(ModuleClassBinding moduleToClassBinder) {
    this.className = moduleToClassBinder.makeQualifiedName(className);
  }
  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    IType typeOfObject = tVar.get(this.object);
    if (!typeOfObject.isShape()) {
      this.object = new TypeError(this.object.toString(), "InstanceOf typeError, object cannot be a number");
      return true;
    }
    return false;
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
    return new InstanceOf(object.copyVariable(), className.copyVariable());
  }
}
