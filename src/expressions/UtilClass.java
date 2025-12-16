package expressions;

import java.util.Random;
import java.util.Set;

public class UtilClass {

  public static String hashToLetters(Set<Variable> existingVariables) {
    Random random = new Random();
    int code = Math.abs(random.nextInt());

    StringBuilder newName = new StringBuilder();
    int length = 6;

    for (int i = 0; i < length; i++) {
      newName.append((char)('a' + (code % 26)));
      code /= 26;
    }

    Variable newVariable = new Variable(newName.toString());

    if (existingVariables.contains(newVariable)) {
      return hashToLetters(existingVariables);
    } else {
      return newVariable.toString();
    }
  }
}
