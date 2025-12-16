package types;

import java.util.List;
import java.util.Map;

import expressions.Variable;

public interface IMethodType {
  Variable getMethodName();

  List<Integer> checkMethodArgsContainsTypeError(List<IType> argTypes);

  boolean hasReturnType(IType varType);
  IType getReturnType();

  void addTypedParams(Map<Variable, IType> tVar, List<Variable> paramNames);

  int numParams();

  boolean argumentNumIsMismatched(int sizeOfArgsPassedIn);

  int getNumParams();
  List<IType> getParamTypes();
}
