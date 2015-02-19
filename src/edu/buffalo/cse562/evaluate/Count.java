package edu.buffalo.cse562.evaluate;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LongValue;
import edu.buffalo.cse562.table.Row;

/**
 * Aggregate count solver that produces the current row count as new rows are encountered.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Count extends Aggregate {
  private LongValue count = new LongValue(0);

  /**
   * Initializes the aggregate by setting the expression to be evaluated.
   * 
   * @param expression - expression to be evaluated
   * @param evaluate - evaluator used to evaluate the given expression
   */
  protected Count(Expression expression, Evaluate evaluate) {
    super(expression, evaluate);
  }

  @Override
  public LeafValue yield(Row row) {
    count.setValue(count.getValue() + 1);
    this.result = count;
    return count;
  }
}
