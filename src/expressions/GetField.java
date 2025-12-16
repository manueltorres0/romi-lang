package expressions;

import Control.*;
import Maps.Environment;
import Maps.Location;
import Maps.ProxyOrValue;
import Maps.Store;
import ast.ASTNodes;
import cesk.CESK;
import error.TypeError;
import error.UndefinedVariableError;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import programs.Kontinuation;
import Class.IClass;
import types.IFieldType;
import types.IType;
import types.Shape;

public class GetField implements IExpression {
  Variable object;
  Variable fieldName;

  public GetField(ASTNodes object, ASTNodes fieldName, AtomicBoolean valid) {
    this.object = object.convertToVariableOrError(valid);
    this.fieldName = fieldName.convertToVariableOrError(valid);
  }

  public GetField(Variable object, Variable fieldName) {
    this.object = object;
    this.fieldName = fieldName;
  }

  @Override
  public boolean isError() {
    return false;
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    if (this.object.containsUndefinedVariables(definedVariables, definedClassNames)) {
      this.object = new UndefinedVariableError(this.object.toString(),
          "Contains undefined Variable");
      return true;
    }
    return false;
  }

  @Override
  public IControl evaluate(Environment env, Store store, List<IClass> classes, Kontinuation k,
                           CESK cesk) {
    Location objLoc = env.get(this.object);
    ProxyOrValue obj = store.get(objLoc);

    if (obj.isObject()) {
      return evaluateFieldRetrievalForObject(obj);
    } else if (obj.isProxy()){
      return evaluateFieldRetrievalForProxy(obj);
    } else {
      return new ErrorControl();
    }
  }

  private IControl evaluateFieldRetrievalForProxy(ProxyOrValue proxy) {
    if (proxy.hasField(this.fieldName)) {
      Optional<ProxyOrValue> conformedField =
              Utils.Utils.conforms(proxy.getField(fieldName), proxy.getFieldType(fieldName));
      if (conformedField.isPresent()) {
        return new ValueControl(conformedField.get());
      }
    }
    return new ErrorControl();
  }

  private IControl evaluateFieldRetrievalForObject(ProxyOrValue obj) {
    if (obj.hasField(this.fieldName)) {
      return new ValueControl(obj.getField(this.fieldName));
    }
    return new ErrorControl();
  }

  @Override
  public List<Variable> getVariables() {
    return List.of(this.object);
  }

  @Override
  public IExpression renameVariables(Set<Variable> existingVariables) {
    return new GetField(this.object.renameVariables(existingVariables), fieldName);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof GetField get) {
      return this.object.equals(get.object)
          && this.fieldName.equals(get.fieldName);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash("GetField", object, fieldName);
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    IType variableType = tVar.get(this.object);
    if (!variableType.containsField(this.fieldName)) {
      fieldName = new TypeError(fieldName.toString(), "Object does not contain this field.");
      return true;
    }
    return false;
  }

  @Override
  public boolean returnTypeEquals(IType varType, Map<Variable, IType> tVar, Map<Variable, Shape> sClasses) {
    IType typeOfObject = tVar.get(this.object);
    IFieldType fieldType = typeOfObject.getFieldType(this.fieldName);
    return fieldType.hasType(varType);
  }

  @Override
  public IType getReturnType(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    IType typeOfObject = tVar.get(this.object);
    IFieldType fieldType = typeOfObject.getFieldType(this.fieldName);
    return fieldType.getType();
  }

  @Override
  public IExpression copyExpr() {
    return new GetField(object.copyVariable(), fieldName.copyVariable());
  }

}
