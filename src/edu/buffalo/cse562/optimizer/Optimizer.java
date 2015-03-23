package edu.buffalo.cse562.optimizer;

import edu.buffalo.cse562.parsetree.ParseTree;

public class Optimizer {
  public static void optimize(ParseTree root) {
    SplitSelect.splitAllSelectNodes(root);
    MoveDownSelect.moveDownAllSelectNodes(root);
  }
}
