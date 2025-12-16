package types;

import Control.IControl;
import Maps.Environment;
import Maps.ProxyOrValue;
import Maps.Store;
import Method.IMethod;
import ast.ASTNodes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import Utils.*;
import cesk.CESK;
import expressions.Variable;
import programs.Kontinuation;
import Class.*;

public class Shape implements IShape {
  public List<IFieldType> fieldTypes;
  public List<IMethodType> methodTypes;

  public Shape(ArrayList<ASTNodes> seq, AtomicBoolean valid) {
    ASTNodes possibleFieldTypes = seq.get(0);
    ASTNodes possibleMethodTypes = seq.get(1);

    this.fieldTypes = possibleFieldTypes.convertToListFieldTypesOrError(valid);
    this.methodTypes = possibleMethodTypes.convertToListMethodTypesOrError(valid);

  }

  public List<Integer> checkConstructorArgsContainsTypeError(List<IType> paramTypes) {
    List<Integer> indicesOfTypeErrorParams = new ArrayList<>();
    for (int i = 0; i < paramTypes.size(); i++) {
      if (!(paramTypes.get(i).equals(fieldTypes.get(i).getType()))) {
        indicesOfTypeErrorParams.add(i);
      }
    }
    return indicesOfTypeErrorParams;
  }

  @Override
  public boolean isNumber() {
    return false;
  }

  @Override
  public boolean containsField(Variable fieldName) {
    return Utils.hasElementMatching(fieldType -> fieldType.getFieldName().equals(fieldName), this.fieldTypes);
  }

  @Override
  public boolean containsMethod(Variable methodName) {
    return Utils.hasElementMatching(methodType -> methodType.getMethodName().equals(methodName), this.methodTypes);
  }

  @Override
  public List<Variable> getFieldNames() {
    List<Variable> fieldNameList = new ArrayList<>();
    Utils.voidMap(this.fieldTypes, fType -> fieldNameList.add(fType.getFieldName()));
    return fieldNameList;
  }

  @Override
  public Set<Variable> getMethodNames() {
    Set<Variable> methodNameSet = new HashSet<>();
    Utils.voidMap(this.methodTypes, mType -> methodNameSet.add(mType.getMethodName()));
    return methodNameSet;
  }

  @Override
  public boolean containsDuplicateFieldOrMethodNames() {
    return containsDuplicateFieldNames() ||  containsDuplicateMethodNames();
  }

  private boolean containsDuplicateFieldNames() {
    Set<Variable> fieldNameSet = new HashSet<>();
    return Utils.hasElementMatching(fType -> {
      if (fieldNameSet.contains(fType.getFieldName())) {
        return true;
      } else {
        fieldNameSet.add(fType.getFieldName());
        return false;
      }
    }, this.fieldTypes);
  }

  private boolean containsDuplicateMethodNames() {
    Set<Variable> methodNameSet = new HashSet<>();
    return Utils.hasElementMatching(mType -> {
      if (methodNameSet.contains(mType.getMethodName())) {
        return true;
      } else {
        methodNameSet.add(mType.getMethodName());
        return false;
      }
    }, this.methodTypes);
  }


  @Override
  public IMethodType getMethodType(Variable methodName) {
    for (IMethodType methodType : methodTypes) {
      if (methodType.getMethodName().equals(methodName)) {
        return methodType;
      }
    }
    throw new IllegalStateException("Should never get here, we checked that the method exists.");
  }

  @Override
  public IFieldType getFieldType(Variable fieldName) {
    for (IFieldType fieldType : fieldTypes) {
      if (fieldType.getFieldName().equals(fieldName)) {
        return fieldType;
      }
    }
    throw new IllegalStateException("Should never get here, we checked that the method exists.");
  }

  @Override
  public boolean isShape() {
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldTypes, methodTypes);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Shape other) {
      return this.fieldTypes.equals(other.fieldTypes) &&
              this.methodTypes.equals(other.methodTypes);
    }
    return false;
  }

  public boolean hasSameFieldNames(Set<Variable> fieldSetFromInstantiated) {
    Set<Variable> fieldNames = new HashSet<>();
    Utils.voidMap(this.fieldTypes, field -> fieldNames.add(field.getFieldName()));
    return fieldNames.equals(fieldSetFromInstantiated);
  }
  public boolean hasSameMethodNames(Set<Variable> methodSetFromInstantiated) {
    Set<Variable> methodNames = new HashSet<>();
    Utils.voidMap(this.methodTypes, method -> methodNames.add(method.getMethodName()));
    return methodNames.equals(methodSetFromInstantiated);
  }

  public boolean fieldValuesConform(Map<Variable, ProxyOrValue> fieldMap) {
    for (IFieldType fieldType : fieldTypes) {
      Variable shapeFieldName = fieldType.getFieldName();
      Optional<ProxyOrValue> fieldConformed = Utils.conforms(fieldMap.get(shapeFieldName), fieldType.getType());
      if (fieldConformed.isPresent()) {
        fieldMap.put(shapeFieldName, fieldConformed.get());
      } else {
        return false;
      }
    }
    return true;
  }

  public boolean hasSameParamCount(Map<Variable, IMethod> methodMap) {
    return !Utils.hasElementMatching(
            mType -> !methodMap.get(mType.getMethodName()).hasNumParams(mType.getNumParams()),
            this.methodTypes);
  }

  @Override
  public IControl evaluateStatementOrDeclaration(Environment env, Store store, IControl control, List<IClass> classes) {
    throw new IllegalStateException("shapes do not contain statements or declarations");
  }

  @Override
  public IControl findNextExpression(Kontinuation k, CESK cesk) {
    throw new IllegalStateException("shapes do not contain a next expression");
  }

  @Override
  public Environment getEnv() {
    throw new IllegalStateException("Return types do not hold environments.");
  }

  @Override
  public boolean isReturnType() {
    return true;
  }

}
