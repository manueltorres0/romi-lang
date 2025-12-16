package Control;

import Maps.Environment;
import Maps.ProxyOrValue;
import Maps.Store;
import cesk.CESK;
import java.util.List;
import Class.IClass;
import programs.Kontinuation;

public class ValueControl implements IControl {
  ProxyOrValue value;

  public ValueControl(ProxyOrValue value) {
    this.value = value;
  }


  @Override
  public IControl evaluate(Environment env, Store store, List<IClass> classes,
                           Kontinuation k, CESK cesk) {
    throw new RuntimeException("Cannot evaluate an object or number");
  }

  @Override
  public ProxyOrValue getValue() {
    return this.value;
  }
}
