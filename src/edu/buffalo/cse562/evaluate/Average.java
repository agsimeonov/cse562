package edu.buffalo.cse562.evaluate;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.LongValue;
import edu.buffalo.cse562.table.Row;

/**
 * Aggregate average solver that produces the current average as new rows are encountered.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Average extends Sum {
  private final Count counter;

  /**
   * Initializes the aggregate by setting the expression to be evaluated.
   * 
   * @param expression - expression to be evaluated
   * @param evaluate - evaluator used to evaluate the given expression
   */
  protected Average(Expression expression, Evaluate evaluate) {
    super(expression, evaluate);
    counter = new Count(expression, evaluate);
  }

  @Override
  public LeafValue yield(Row row) {
    super.yield(row);
    long count = 1;
    
    try {
      count = counter.yield(row).toLong();
    } catch (InvalidLeaf e) {
      e.printStackTrace();
    }
    
    if (isLong) {
      LongValue result = new LongValue(longSum.getValue());
      result.setValue(result.getValue() / count);
      return result;
    } else {
      DoubleValue result = new DoubleValue(doubleSum.getValue());
      result.setValue(result.getValue() / (double) count);
      return result;
    }
  }
}
