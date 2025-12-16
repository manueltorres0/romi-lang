package programs;

import Class.IClass;
import block.Block;
import java.util.List;

/**
 * A Program is a class that represents the grammar and error nodes of a BB program
 */
public class Program {
  public List<IClass> classes;
  public Block block;

  public Program(List<IClass> classes, Block block) {
    this.classes = classes;
    this.block = block;
  }

}
