package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.UnionIterator;
import edu.buffalo.cse562.table.Row;

public class UnionNode extends ParseTree {
  public UnionNode(ParseTree base, ParseTree left, ParseTree right) {
    super(base);
    super.left = left;
    super.right = right;
  }

  @Override
  public Iterator<Row> iterator() {
    return new UnionIterator((RowIterator) left.iterator(), (RowIterator) right.iterator());
  }
}
