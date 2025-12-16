package expressions;

import Utils.ModuleClassBinding;
import cesk.CESK;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Control.IControl;
import Maps.Environment;
import Maps.Store;
import Class.IClass;
import programs.Kontinuation;
import types.IType;
import types.Shape;

public interface IExpression {

  boolean isError();

  boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                     Set<Variable> definedClassNames);

  IControl evaluate(Environment env, Store store, List<IClass> classes, Kontinuation k,
                    CESK cesk);

  List<Variable> getVariables();

  IExpression renameVariables(Set<Variable> existingVariables);

  default void renameClassesToQualifiedNames(ModuleClassBinding moduleToClassBinder) {}

  boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar);

  boolean returnTypeEquals(IType varType, Map<Variable, IType> tVar, Map<Variable, Shape> sClasses);

  IType getReturnType(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar);

  IExpression copyExpr();
}
