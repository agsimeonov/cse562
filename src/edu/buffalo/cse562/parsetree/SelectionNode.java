package edu.buffalo.cse562.parsetree;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.jsqlparser.expression.Expression;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.SelectionIterator;
import edu.buffalo.cse562.table.Row;

public class SelectionNode extends ParseTree {
  protected ArrayList<Expression> expressions; 
  protected Expression whereExpression; 
  
  //public SelectionNode(ParseTree base, ArrayList<Expression> expressions) {
  public SelectionNode(ParseTree base, Expression whereExpression) {
    super(base);
    this.whereExpression = whereExpression; 
    // TODO Auto-generated constructor stub
  }

  @Override
  public Iterator<Row> iterator() {
    // TODO Auto-generated method stub
    return new SelectionIterator((RowIterator)left.iterator(),whereExpression);
  }

}
