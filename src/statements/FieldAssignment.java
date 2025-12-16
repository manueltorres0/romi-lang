package statements;

import Maps.Environment;
import Maps.Store;
import Utils.ModuleClassBinding;
import ast.ASTNodes;
import block.Block;
import cesk.CESK;
import error.TypeError;
import error.UndefinedVariableError;
import expressions.IExpression;
import expressions.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import Control.*;
import programs.Kontinuation;
import Class.*;
import Maps.*;
import types.IFieldType;
import types.IType;
import types.Shape;
import java.util.Optional;

public class FieldAssignment implements IStatement {
  Variable object;
  Variable fieldName;
  public IExpression expression;

  public FieldAssignment(ArrayList<ASTNodes> seq, AtomicBoolean valid) {
    this.object = seq.get(0).convertToVariableOrError(valid);
    this.fieldName = seq.get(2).convertToVariableOrError(valid);
    this.expression = seq.get(4).convertToExpressionOrError(valid);
  }

  public FieldAssignment(Variable object, Variable fieldName, IExpression expression) {
    this.object = object.copyVariable();
    this.fieldName = fieldName.copyVariable();
    this.expression = expression.copyExpr();
  }

  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {
    boolean undefined = false;

    if (object.containsUndefinedVariables(definedVariables, definedClassNames)) {
      this.object = new UndefinedVariableError(this.object.toString(),
          "Contains undefined variable");
      undefined = true;
    }

    if (expression.containsUndefinedVariables(definedVariables, definedClassNames)) {
      undefined = true;
    }

    return undefined;
  }

  @Override
  public IStatement copy() {
    return new FieldAssignment(this.object, this.fieldName, this.expression);
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar, IType returnType) {
    boolean typeError = false;
    IType varType = tVar.get(object);
    if (expression.containsTypeError(sClasses, tVar)) {
      typeError = true;
    } else if (!varType.isShape()) {
      typeError = true;
    } else if (!varType.containsField(fieldName)) {
      typeError = true;
    } else {
      IFieldType fieldType = varType.getFieldType(fieldName);
      if (!expression.returnTypeEquals(fieldType.getType(), tVar, sClasses)) {
        expression = new TypeError(expression.toString(), "Expression return type does" +
            " not match the field type.");
        typeError = true;
      }
    }
    return typeError;
  }


  @Override
  public IControl findNextExpression(Kontinuation k, CESK cesk) {
    return new ExpressionControl(expression);
  }

  @Override
  public IControl evaluateStatement(Environment env, Store store, IControl control, Block program, List<IClass> classes) {
    Location loc = env.get(object);
    ProxyOrValue val = store.get(loc);
    ProxyOrValue controlVal = control.getValue();
    if(val.isObject()) {
      return evaluateStatementForObject(val, controlVal, classes);
    } else if (val.isProxy()) {
      return evaluateStatementForProxy(val, controlVal);
    } else {
      return new ErrorControl();
    }
  }

  private IControl evaluateStatementForProxy(ProxyOrValue proxy, ProxyOrValue controlVal) {
    if (proxy.hasField(fieldName)) {
      Optional<ProxyOrValue> conformedField = Utils.Utils.conforms(controlVal, proxy.getFieldType(fieldName));
      if (conformedField.isPresent()) {
        proxy.mutate(fieldName, controlVal);
        return new Search();
      }
    }
    return new ErrorControl();
  }

  private IControl evaluateStatementForObject(ProxyOrValue obj, ProxyOrValue controlVal, List<IClass> classes) {
    if (obj.containsField(classes, this.fieldName)) {
      obj.mutate(fieldName, controlVal);
      return new Search();
    }
    return new ErrorControl();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof FieldAssignment fieldAss) {
      return this.fieldName.equals(fieldAss.fieldName)
          && this.object.equals(fieldAss.object)
          && this.expression.equals(fieldAss.expression);
    }
    return false;
  }
  @Override
  public int hashCode() {
    return Objects.hash("FieldAssignment", fieldName, object, expression);
  }


  @Override
  public boolean isNestedBlock() {
    return false;
  }

  @Override
  public Block getNestedBlockOrThrow() {
    throw new IllegalStateException("Statement is not a nested block");
  }
  @Override
  public void renameClassesToQualifiedNames(ModuleClassBinding moduleToClassBinder) {
    expression.renameClassesToQualifiedNames(moduleToClassBinder);
  }

}
