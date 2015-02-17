package edu.buffalo.cse562.parsetree;

import edu.buffalo.cse562.table.Row;

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
}
