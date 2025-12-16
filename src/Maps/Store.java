package Maps;

import java.util.HashMap;
import java.util.Map;



/**
 * Represents a map from Locations of variables to their actual real number values.
 */
public class Store {
  private final Map<Location, ProxyOrValue> locationsToNumber;

  public Store() {
    this.locationsToNumber = new HashMap<>();
  }

  public ProxyOrValue get(Location location) {
    return locationsToNumber.get(location);
  }

  public void put(Location location, ProxyOrValue value) {
    locationsToNumber.put(location, value);
  }

}
