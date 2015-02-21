package edu.buffalo.cse562.iterator;

import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.jsqlparser.expression.BooleanValue;
import net.sf.jsqlparser.expression.Expression;
import edu.buffalo.cse562.evaluate.Evaluate;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

public class SelectionIterator implements RowIterator{

  private RowIterator leftIterator; 
  private Expression whereExpression; 
  private Evaluate evaluate; 
  private Schema schema; 
  private Row row;
  //private ArrayList<Expression> expressions; 
  
  public SelectionIterator(RowIterator leftIterator, Expression expressions) {
    this.leftIterator = leftIterator;
    this.whereExpression = expressions;
    evaluate = new Evaluate(); 
    this.open(); 
  }

  @Override
  
  public boolean hasNext() {
      if (row != null) return true; 
      while (leftIterator.hasNext()) { 
        Row nextRow = leftIterator.next();  
        evaluate.setRow(nextRow);
        try {
          if (whereExpression != null) {
            BooleanValue evaluatedExpr = (BooleanValue) evaluate.eval(whereExpression);
          //  System.out.println("Evaluated: " + whereExpression + " for " + nextRow + " val was " + evaluatedExpr);
            if (evaluatedExpr == null) return false;
            if (evaluatedExpr.getValue() == true) {
              row = nextRow;
              return true;             
            }
          }
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
     }
     return false;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null; //Expr didnt hold true 
    Row outRow = row; 
    row = null;
    return outRow; 
  }

  @Override
  public void close() {
    leftIterator.close();
  }

  @Override
  public void open() {
    leftIterator.open();
  }
  
}
