package edu.buffalo.cse562.evaluate;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LeafValue;
import edu.buffalo.cse562.table.Row;

public abstract class Generate {
  protected Expression expression; 
  protected Evaluate evaluate;
  
  public Generate(Expression expression, Evaluate evaluate) {
    this.expression = expression;
    this.evaluate = evaluate;
  }
  
  public abstract LeafValue yield(Row row);
  
  public static Generate getGenerator(Function function, Expression expression, Evaluate evaulate) {
    String functionName = function.getName().toLowerCase();
    
    if (functionName.equals("sum")) {
      
    } else if (functionName.equals("count")) {

    } else if (functionName.equals("avg")) {

    } else if (functionName.equals("min")) {

    } else if (functionName.equals("max")) {

    }
    
    return null;
  }
}
