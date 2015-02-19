package edu.buffalo.cse562.evaluate;

import java.sql.SQLException;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.LongValue;
import edu.buffalo.cse562.table.Row;

/**
 * Aggregate sum solver that produces the current sum as new rows are encountered.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Sum extends Aggregate {
  protected Boolean isLong = null;
  protected LongValue longSum = new LongValue(0);
  protected DoubleValue doubleSum = new DoubleValue(0.0);
  
  /**
   * Initializes the aggregate by setting the expression to be evaluated.
   * 
   * @param expression - expression to be evaluated
   * @param evaluate - evaluator used to evaluate the given expression
   */
  protected Sum(Expression expression, Evaluate evaluate) {
    super(expression, evaluate);
  }
  
  @Override
  public LeafValue yield(Row row) {
    LeafValue result = null;
    evaluate.setRow(row); 
    
    try {
      result = evaluate.eval(expression);
      
      if (isLong == null) isLong = result instanceof LongValue ? true : false;
      
      if (isLong) {
        longSum.setValue(longSum.getValue() + result.toLong());
        this.result = longSum;
        return longSum;
      } else {
        doubleSum.setValue(doubleSum.getValue() + result.toDouble());
        this.result = doubleSum;
        return doubleSum;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (InvalidLeaf e) {
      e.printStackTrace();
    }
    
    this.result = longSum;
    return longSum;
  }
}
