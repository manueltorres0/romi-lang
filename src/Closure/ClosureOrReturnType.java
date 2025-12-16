package Closure;

import java.util.List;

import Control.IControl;
import Maps.Environment;
import Maps.Store;
import cesk.CESK;
import programs.Kontinuation;
import Class.IClass;

public interface ClosureOrReturnType {
  IControl evaluateStatementOrDeclaration(Environment env, Store store, IControl control, List<IClass> classes);

  IControl findNextExpression(Kontinuation k, CESK cesk);

  Environment getEnv();

  boolean isReturnType();
}
