package edu.buffalo.cse562.evaluate;

import java.sql.SQLException;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import edu.buffalo.cse562.table.Row;

/**
 * Aggregate min/max solver that produces the current min/max as new rows are encountered.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class MinMax extends Aggregate {
  private LeafValue minMax = null;
  private final boolean doMin;

  /**
   * Initializes the aggregate by setting the expression to be evaluated.
   * 
   * @param expression - expression to be evaluated
   * @param evaluate - evaluator used to evaluate the given expression
   * @param doMin - true if we want to compute the minimum, false to compute the maximum
   */
  protected MinMax(Expression expression, Evaluate evaluate, boolean doMin) {
    super(expression, evaluate);
    this.doMin = doMin;
  }

  @Override
  public LeafValue yield(Row row) {
    LeafValue result = null;
    evaluate.setRow(row); 
    
    try {
      result = evaluate.eval(expression);
      
      if (doMin) {
        if (minMax == null) {
          minMax = result;
        } else if (result instanceof LongValue) {
          if (minMax.toLong() > result.toLong()) minMax = result;
        } else if (result instanceof DoubleValue) {
          if (minMax.toDouble() > result.toDouble()) minMax = result;
        } else if (result instanceof StringValue) {
          if (minMax.toString().compareTo(result.toString()) > 0) minMax = result;
        } else {
          DateValue minMaxDate = (DateValue) minMax;
          DateValue resultDate = (DateValue) result;
          if (minMaxDate.getValue().getTime() > resultDate.getValue().getTime()) minMax = result;
        }
      } else {
        if (minMax == null) {
          minMax = result;
        } else if (result instanceof LongValue) {
          if (minMax.toLong() < result.toLong()) minMax = result;
        } else if (result instanceof DoubleValue) {
          if (minMax.toDouble() < result.toDouble()) minMax = result;
        } else if (result instanceof StringValue) {
          if (minMax.toString().compareTo(result.toString()) < 0) minMax = result;
        } else {
          DateValue minMaxDate = (DateValue) minMax;
          DateValue resultDate = (DateValue) result;
          if (minMaxDate.getValue().getTime() < resultDate.getValue().getTime()) minMax = result;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (InvalidLeaf e) {
      e.printStackTrace();
    }
    
    return minMax;
  }
}
