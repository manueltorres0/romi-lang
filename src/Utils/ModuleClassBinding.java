package Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import expressions.Variable;

/**
 * Represents a tool to produce the qualified names for classes according to their moduleName.
 * This tool handles the naming resolution where the prefix of two identically named classes
 * corresponds to the last module to be imported.
 */
public class ModuleClassBinding {

  Map<Variable,Variable> classToModule;

  public ModuleClassBinding(List<Pair<Variable, Variable>> pairs) {
    classToModule = new HashMap<>();
    Utils.voidMap(pairs, pair -> classToModule.put(pair.className(), pair.moduleName()));
  }

  public Variable makeQualifiedName(Variable className) {
    String qualifiedNameString = classToModule.get(className).toString() + "." + className.toString();
    return new Variable(qualifiedNameString);
  }
}
