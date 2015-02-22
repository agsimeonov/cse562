package edu.buffalo.cse562.evaluate;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LeafValue;
import edu.buffalo.cse562.table.Row;

/**
 * Aggregate average solver that produces the current average as new rows are encountered.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Average extends Sum {
  private double count = 0.0;

  /**
   * Initializes the aggregate by setting the expression to be evaluated.
   * 
   * @param expression - expression to be evaluated
   * @param evaluate - evaluator used to evaluate the given expression
   */
  protected Average(Expression expression, Evaluate evaluate) {
    super(expression, evaluate);
  }

  @Override
  public LeafValue yield(Row row) {
    super.yield(row);
    count = count + 1.0;
    if (isLong) return new DoubleValue(((double) longSum.getValue()) / count);
    else return new DoubleValue(doubleSum.getValue() / count);
  }
}
