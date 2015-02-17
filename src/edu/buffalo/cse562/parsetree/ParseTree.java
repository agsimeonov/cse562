package edu.buffalo.cse562.parsetree;

import edu.buffalo.cse562.table.Row;

public abstract class ParseTree implements Iterable<Row> {
  protected ParseTree base;
  protected ParseTree left;
  protected ParseTree right;

  public ParseTree(ParseTree base) {
    this.base = base;
  }
  
  public void setBase(ParseTree base) {
    this.base = base;
  }
  
  public void setLeft(ParseTree left) {
    this.left = left;
  }
  
  public void setRight(ParseTree right) {
    this.right = right;
  }
  
  public ParseTree getBase() {
    return base;
  }
  
  public ParseTree getLeft() {
    return left;
  }
  
  public ParseTree getRight() {
    return right;
  }
}
