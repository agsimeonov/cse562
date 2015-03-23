package edu.buffalo.cse562.parsetree;

import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * A base class for all parse tree nodes.  Provides basic methods, and fields.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public abstract class ParseTree implements Iterable<Row> {
  protected ParseTree base;
  protected ParseTree left;
  protected ParseTree right;

  /**
   * Initializes the parse tree node.
   * 
   * @param base - the new parent node
   */
  public ParseTree(ParseTree base) {
    this.base = base;
  }
  
  /**
   * Sets the parent node.
   * 
   * @param base - the new parent node
   */
  public void setBase(ParseTree base) {
    this.base = base;
  }
  
  /**
   * Sets the left child node.
   * 
   * @param left - the new left child node
   */
  public void setLeft(ParseTree left) {
    this.left = left;
  }
  
  /**
   * Sets the right child node.
   * 
   * @param right - the new right child node
   */
  public void setRight(ParseTree right) {
    this.right = right;
  }
  
  /**
   * Gets the parent node.
   * 
   * @return the parent node
   */
  public ParseTree getBase() {
    return base;
  }
  
  /**
   * Gets the left child node.
   * 
   * @return the left child node
   */
  public ParseTree getLeft() {
    return left;
  }
  
  /**
   * Gets the right child node.
   * 
   * @return the right child node
   */
  public ParseTree getRight() {
    return right;
  }
  
  /**
   * Acquires the node schema.
   * 
   * @return the schema for the node
   */
  public abstract Schema getSchema();
  
  /**
   * Acquires the depth of the current node in the tree
   * 
   * @return the depth of the current node in the tree
   */
  public int getDepth() {
    if (base == null) {
      return 0;
    } else {
      return base.getDepth() + 1;
    }
  }
  
  /**
   * Sets the correct parent nodes for the whole tree.
   * 
   * @param parent - the parent node for the root, usually set to null by the user
   */
  public void setParentNodes(ParseTree parent) {
    this.base = parent;
    if (left != null) left.setParentNodes(this);
    if (right != null) right.setParentNodes(this);
  }
  
  @Override
  public String toString() {
    String label = this.getClass().getSimpleName();
    int depth = this.getDepth();
    
    String string = right == null ? "" : right.toString();
    if (depth != 0) {
      for (int i = 0; i < depth - 1; i++)
        string += "|   ";
      string += "|---" + label + "\n";
    } else {
      string += label + "\n";
    }
    string += left == null ? "" : left.toString() + "\n";
    
    return string;
  }
}
