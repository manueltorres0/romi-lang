package Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import Maps.ProxyOrValue;
import Maps.StoreObject;
import expressions.GoodNumber;
import expressions.Variable;
import systemComponents.IModule;
import systemComponents.InterfaceImport;
import types.*;

public class Utils {

  public static <T> boolean hasElementMatching(Predicate<T> pred, List<T> list) {
    for (T element : list) {
      if (pred.test(element)) {
        return true;
      }
    }
    return false;
  }

  public static <T> boolean anyMatchFullScan(Predicate<T> pred, List<T> list) {
    boolean result = false;
    for (T element : list) {
      if (pred.test(element)) {
        result = true;
      }
    }
    return result;
  }


  public static <T> void voidMap(Iterable<T> iterable, Consumer<T> func) {
    for (T element : iterable) {
      func.accept(element);
    }
  }

  public static <T> T find(Iterable<T> iterable, Predicate<T> pred) {
    for (T element : iterable) {
      if (pred.test(element)) {
        return element;
      }
    }
    throw new IllegalArgumentException("Element not found");
  }


  public static boolean modulesContainImportModule(List<IModule> modules, InterfaceImport myImport) {
    return Utils.hasElementMatching(mod -> mod.getModuleName().equals(myImport.getModuleName()), modules);
  }

  public static Optional<ProxyOrValue> conforms(ProxyOrValue obj, IType type) {
    return switch(obj) {
      case GoodNumber n when type.isNumber() -> Optional.of(obj);
      case StoreObject sObj when type.isShape() && sObj.firstOrderCheck((Shape) type) ->
              Optional.of(new MyProxy(sObj, (Shape) type));
      case MyProxy prx when type.isShape() && prx.hasShape((Shape) type) -> Optional.of(prx);
      default -> Optional.empty();
    };
  }

}
