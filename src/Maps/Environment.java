package Maps;

import expressions.Variable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a map of declared variables to their locations in the CESK machine,
 * the location is a key for the store.
 */
public class Environment {
  private final Map<Variable, Location> locationMap;

  public Environment() {
    this.locationMap = new HashMap<Variable, Location>();
  }

  public Environment(Environment locationMap) {
    this.locationMap = new HashMap<>(locationMap.locationMap);
  }

  /**
   * Makes the new environment for a method call, adding the corresponding values to the given
   * store.
   */
  public Environment(Store store, ProxyOrValue obj, List<Variable> arguments,
                     List<ProxyOrValue> paramList) {

    this.locationMap = new HashMap<>();
    for (int i = 0; i < arguments.size(); i++) {
      Variable paramVariable = arguments.get(i);
      ProxyOrValue paramValue = paramList.get(i);

      Location paramLoc = new Location();
      this.locationMap.put(paramVariable, paramLoc);
      store.put(paramLoc, paramValue);
    }

    Location objLocation = new Location();
    this.locationMap.put(new Variable("this"), objLocation);
    store.put(objLocation, obj);
  }


  public Location get(Variable var) {
    return locationMap.get(var);
  }

  public Location put(Variable var, Location location) {
    return locationMap.put(var, location);
  }

  public int size() {
    return locationMap.size();
  }

  public Environment extendEnv(List<Variable> renamedVariables, List<ProxyOrValue> variableValues, Store s) {

    for (int i = 0; i < variableValues.size(); i++) {
      Variable var = renamedVariables.get(i);
      ProxyOrValue value = variableValues.get(i);

      Location newLoc = new Location();
      this.locationMap.put(var, newLoc);
      s.put(newLoc, value);
      if (value == null) {
        throw new RuntimeException("null here");
      }
    }
    return this;
  }

  public Set<Variable> keySet() {
    return locationMap.keySet();
  }

}
