package types;

import ast.ASTNodes;
import expressions.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MethodType implements IMethodType {
  Variable methodName;
  List<IType> argumentTypes;
  IType returnType;

  public MethodType(ArrayList<ASTNodes> seq, AtomicBoolean valid) {
    this.methodName = seq.getFirst().convertToVariableOrError(valid);
    this.argumentTypes = seq.get(1).convertToTypeListOrError(valid);
    this.returnType = seq.getLast().convertToTypeOrError(valid);
  }

  @Override
  public Variable getMethodName() {
    return methodName;
  }
  @Override
  public List<Integer> checkMethodArgsContainsTypeError(List<IType> argTypes) {
    List<Integer> indicesOfTypeErrorParams = new ArrayList<>();
    for (int i = 0; i < argTypes.size(); i++) {
      if (!(argTypes.get(i).equals(argumentTypes.get(i)))) {
        indicesOfTypeErrorParams.add(i);
      }
    }
    return indicesOfTypeErrorParams;
  }

  @Override
  public boolean hasReturnType(IType varType) {
    return returnType.equals(varType);
  }

  @Override
  public IType getReturnType() {
    return returnType;
  }


  @Override
  public void addTypedParams(Map<Variable, IType> tVar, List<Variable> paramNames) {
    if (paramNames.size() == argumentTypes.size()) {
      for (int i = 0; i < paramNames.size(); i++) {
        tVar.put(paramNames.get(i), argumentTypes.get(i));
      }
    }
  }

  @Override
  public int numParams() {
    return this.argumentTypes.size();
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, argumentTypes, returnType);
  }
  @Override
  public boolean equals(Object o) {
    if (o instanceof MethodType other) {
      return this.methodName.equals(other.methodName) &&
              this.argumentTypes.equals(other.argumentTypes) &&
              this.returnType.equals(other.returnType);
    }
    return false;
  }
  @Override
  public boolean argumentNumIsMismatched(int sizeOfArgsPassedIn) {
    return sizeOfArgsPassedIn != this.argumentTypes.size();
  }

  @Override
  public int getNumParams() {
    return argumentTypes.size();
  }

  @Override
  public List<IType> getParamTypes() {
    return this.argumentTypes;
  }
}
