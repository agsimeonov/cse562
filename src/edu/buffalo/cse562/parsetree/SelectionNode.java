package edu.buffalo.cse562.parsetree;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.jsqlparser.expression.Expression;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.SelectionIterator;
import edu.buffalo.cse562.table.Row;

public class SelectionNode extends ParseTree {
  protected Expression whereExpression; 
  protected boolean isHaving; 
  public SelectionNode(ParseTree base, Expression whereExpression) {
    super(base);
    this.whereExpression = whereExpression; 
  }

  @Override
  public Iterator<Row> iterator() {
    return new SelectionIterator((RowIterator)left.iterator(),whereExpression);
  }

}
