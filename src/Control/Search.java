package Control;

import Maps.*;
import cesk.CESK;
import java.util.List;
import Class.IClass;
import programs.Kontinuation;

public class Search implements IControl {
  public Search() {}

  @Override
  public IControl evaluate(Environment env, Store store, List<IClass> classes,
                           Kontinuation k, CESK cesk) {
    throw new RuntimeException("Cannot evaluate Search Control");
  }

  @Override
  public ProxyOrValue getValue() {
    throw new IllegalStateException("Cannot get value from errorControl");
  }
}
