package Control;

import Maps.Environment;
import Maps.ProxyOrValue;
import Maps.Store;
import cesk.CESK;
import java.util.List;
import Class.IClass;
import programs.Kontinuation;

public interface IControl {

  IControl evaluate(Environment env, Store store, List<IClass> classes, Kontinuation k,
                    CESK cesk);

  ProxyOrValue getValue();

}


