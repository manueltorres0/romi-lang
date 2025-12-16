package cesk;
import Maps.ProxyOrValue;
import java.util.List;

import Control.*;
import Maps.Environment;
import Maps.Store;
import programs.Kontinuation;
import programs.Program;
import Class.IClass;
/**
 * Represents a CESK machine that can be loaded, transitioned, and unloaded.
 */
public class CESK {
  IControl control;
  public Environment env;
  public Store store;
  public Kontinuation kontinuation;
  private final List<IClass> classes;

  /**
   * Loads the machine with the given kontinuation AST.
   */
  CESK(Program ast) {
    this.control = new Search();
    this.env = new Environment();
    this.store = new Store();
    this.kontinuation = new  Kontinuation(ast.block, env);
    this.classes = ast.classes;
  }

  CESK(IControl control, Kontinuation kontinuation, Environment env, Store store, List<IClass> classes) {
    this.control = control;
    this.env = env;
    this.store = store;
    this.kontinuation = kontinuation;
    this.classes = classes;
  }

  public static void runCSK(Program ast) {
    CESK cesk = new CESK(ast);
    while (!cesk.isFinalState()) {
      cesk = cesk.transition();
    }
    cesk.unload();
  }


  CESK transition() {
    return switch (this.control) {
      case Search s -> this.findNextExpression();
      case ExpressionControl expr -> this.evaluateControl();
      case ValueControl value -> this.evaluateStatementOrDeclaration();
      case ErrorControl e -> throw new RuntimeException("Cannot transition when control is Error");
      default -> throw new RuntimeException("Control must be one of the finite options");
    };
  }


  boolean isFinalState() {
    return ((this.control instanceof ValueControl) && kontinuation.isEmpty())
        || this.control instanceof ErrorControl;
  }


  CESK findNextExpression() {
    return new CESK(this.kontinuation.findExpression(this), this.kontinuation, this.env, this.store, this.classes);
  }

  CESK evaluateControl() {
    return new CESK(this.control.evaluate(this.env, this.store, this.classes, kontinuation, this),
        this.kontinuation, this.env, this.store, this.classes);
  }

  CESK evaluateStatementOrDeclaration() {
    IControl
        nextControl = this.kontinuation.evaluateStatementOrDeclaration(env, store, control, classes);
    return new CESK(nextControl, this.kontinuation, this.env, this.store, this.classes);
  }

  void unload() {
    if (this.control instanceof ErrorControl) {
      java.lang.System.out.print("\"run-time error\"");
    } else if (this.control.getValue().isObject()) {
      java.lang.System.out.print("\"object\"");
    } else {
      ProxyOrValue num = this.control.getValue();
      java.lang.System.out.print(num.getValue());
    }
  }

}
