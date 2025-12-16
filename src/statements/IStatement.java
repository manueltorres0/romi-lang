package statements;

import Maps.Environment;
import Maps.Store;
import cesk.CESK;
import expressions.Variable;
import block.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import Control.IControl;
import Class.*;
import programs.Kontinuation;
import types.IType;
import types.Shape;

public interface IStatement extends IBlock {

  boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                     Set<Variable> definedClassNames);

  IControl evaluateStatement(Environment env, Store store, IControl control, Block program, List<IClass> classes);
  IControl findNextExpression(Kontinuation k, CESK cesk);

  IStatement copy();

  boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar, IType returnType);

}
