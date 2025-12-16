package Method;

import Utils.ModuleClassBinding;
import block.Block;
import expressions.Variable;
import types.IType;
import types.Shape;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IMethod {

  boolean containsDuplicateMethodNames(Set<Variable> methodNames);

  boolean containsDuplicateParamNames();

  boolean containsUndefinedVariables(Set<Variable> definedVariables, Set<Variable> definedClassNames);

  boolean equals(Object o);

  Variable getMethodName();

  boolean hasNumParams(int numArgs);

  Block convertToBlock();

  List<Variable> getParamNames();

  void renameClassesToQualifiedNames(ModuleClassBinding moduleToClassBinder);
  boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar);

  IMethod copyMethod();
}
