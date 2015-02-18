package edu.buffalo.cse562.evaluate;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LeafValue;
import edu.buffalo.cse562.table.Row;

/**
 * Generates aggregate function solvers which compute an aggregate on a per row basis.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public abstract class Aggregate {
  protected Expression expression; 
  protected Evaluate evaluate;
  
  /**
   * Initializes the aggregate by setting the expression to be evaluated.
   * 
   * @param expression - expression to be evaluated
   * @param evaluate - evaluator used to evaluate the given expression
   */
  protected Aggregate(Expression expression, Evaluate evaluate) {
    this.expression = expression;
    this.evaluate = evaluate;
  }
  
  /**
   * Generator function producing the current aggregate result for each encountered row.
   * 
   * @param row - the newly encountered row
   * @return computed aggregate result
   */
  public abstract LeafValue yield(Row row);
  
  /**
   * Gets a new aggregate solver for a given function.
   * 
   * @param funct - function to get an aggregate for
   * @param eval - evaluator used for expressions in the given function
   * @return corresponding new instance of an aggregate solver based on the given function
   */
  public static Aggregate getAggregate(Function funct, Evaluate eval) {
    Expression expr = null;
    if (!funct.isAllColumns()) expr = (Expression) funct.getParameters().getExpressions().get(0);
    String functName = funct.getName().toLowerCase();
    
    if (functName.equals("sum")) return new Sum(expr, eval);
    else if (functName.equals("count")) return new Count(expr, eval);
    else if (functName.equals("avg")) return new Average(expr, eval);
    else if (functName.equals("min")) return new MinMax(expr, eval, true);
    else if (functName.equals("max")) return new MinMax(expr, eval, false);
    else return null;
  }
}
