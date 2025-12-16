package Closure;

import Control.IControl;
import Maps.*;
import block.Block;
import Class.*;
import cesk.CESK;
import java.util.List;
import programs.Kontinuation;

public class Closure implements ClosureOrReturnType{
  public final Environment env;
  final Block block;

  public Closure(Environment env, Block program) {
    this.env = env;
    this.block = program;
  }

  @Override
  public IControl findNextExpression(Kontinuation k, CESK cesk) {
    return block.findNextExpression(k, cesk);
  }

  @Override
  public IControl evaluateStatementOrDeclaration(Environment env, Store store, IControl control, List<IClass> classes) {
    return block.evaluateStatementOrDeclaration(env, store, control, classes);

  }

  @Override
  public Environment getEnv() {
    return this.env;
  }

  @Override
  public boolean isReturnType() {
    return false;
  }

}
