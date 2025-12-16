package block;

import Utils.ModuleClassBinding;
import expressions.Variable;

import java.util.Map;
import java.util.Set;

import programs.IStatementOrNestedBlock;
import types.IType;
import types.Shape;

public interface IBlock extends IStatementOrNestedBlock {

  boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                     Set<Variable> definedClassNames);

  IBlock copy();

  void renameClassesToQualifiedNames(ModuleClassBinding binding);

  boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar, IType returnType);

}

