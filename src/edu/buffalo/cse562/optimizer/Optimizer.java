package edu.buffalo.cse562.optimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.buffalo.cse562.parsetree.ParseTree;

/**
 * Performs query optimization and defines some general methods used by other optimizer classes.
 * 
 * @author Alexander Simeonov
 */
public class Optimizer {
  /**
   * Performs all optimization steps on the given tree.
   * 
   * @param root - root of the given tree
   * @param columnSet - the set of columns needed from the table nodes
   */
  public static void optimize(ParseTree root, Set<String> columnSet) {
    SplitSelect.splitAllSelectNodes(root);
    MoveDownSelect.moveDownAllSelectNodes(root);
    CrossToJoin.crossToJoin(root);
    TableColumns.setOptimalTableSchemas(root, columnSet);
  }

  /**
   * Pops a node out of the tree it belongs to. If the node has two children, only reconnects the
   * left one to the parent, you will need to get the left one's parent and complete the stitching
   * by replacing the popped out node with another one.
   * 
   * @param node - a node to pop out from it's underlying tree
   */
  public static void popNode(ParseTree node) {
    ParseTree parent = node.getBase();
    ParseTree child = node.getLeft();
    
    if (parent != null) {
      if (parent.getLeft() == node) parent.setLeft(child);
      else parent.setRight(child);
    }
    if (child != null) child.setBase(parent);
  }
  
  /**
   * Pushes a node into a given place in the tree.
   * 
   * @param node - node to push
   * @param parent - parent node to push under
   * @param left - left node to place below
   * @param right - right node to place below
   */
  public static void pushNode(ParseTree node, ParseTree parent, ParseTree left, ParseTree right) {
    if (parent != null) {
      if (parent.getLeft() == left) parent.setLeft(node);
      else parent.setRight(node);
    }
    
    if (left != null) left.setBase(node);
    if (right != null) right.setBase(node);
    
    node.setBase(parent);
    node.setLeft(left);
    node.setRight(right);
  }
  
  /**
   * Acquires all nodes of a given type in a tree.
   * 
   * @param root - root of the given tree
   * @param type - type of nodes to extract
   * @return a list of nodes that belong to the given type
   */
  public static List<ParseTree> getAllTypeNodes(ParseTree root, Class<?> type) {
    List<ParseTree> list = new ArrayList<ParseTree>();
    
    if (root != null) {
      if (root.getClass().isAssignableFrom(type)) list.add(root);
      if (root.getLeft() != null) list.addAll(getAllTypeNodes(root.getLeft(), type));
      if (root.getRight() != null) list.addAll(getAllTypeNodes(root.getRight(), type));
    }
    
    return list;
  }
}
