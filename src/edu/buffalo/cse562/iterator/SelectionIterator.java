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
    boolean trueExpression = true; 
    
    if (row != null) return true; //If not null, recursing up the tree
    while (leftIterator.hasNext()) { 
      
      Row nextRow = leftIterator.next(); //continue down the tree 
      evaluate.setRow(nextRow);
      try {
        if (whereExpression != null) {
          BooleanValue evaluatedExpr = (BooleanValue) evaluate.eval(whereExpression);
//          System.out.println("Evaluated: " + whereExpression + " for " + nextRow + " val was " + evaluatedExpr);
          if (evaluatedExpr == null) return false;
          
          if (evaluatedExpr.getValue() == false) {
            trueExpression = false;
          }
          else { 
              row = nextRow;
              trueExpression = true;
              return true; 
          } 
        }
      } catch (SQLException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
      }
    }

    trueExpression = true; 
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
