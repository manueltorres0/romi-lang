package Control;

import Maps.*;
import cesk.CESK;
import java.util.List;
import Class.IClass;
import programs.Kontinuation;

public class ErrorControl implements IControl {
  public ErrorControl() {}


  @Override
  public IControl evaluate(Environment env, Store store, List<IClass> classes,
                           Kontinuation k, CESK cesk) {
    throw new RuntimeException("Cannot evaluate errorControl");
  }

  @Override
  public ProxyOrValue getValue() {
    throw new IllegalStateException("Cannot get value from errorControl");
  }
}
