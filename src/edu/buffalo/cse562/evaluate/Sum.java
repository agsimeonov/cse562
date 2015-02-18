package edu.buffalo.cse562.evaluate;

import java.sql.SQLException;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.LongValue;
import edu.buffalo.cse562.table.Row;

public class Sum extends Generate {
  Boolean isLong = null;
  LongValue longSum = new LongValue(0);
  DoubleValue doubleSum = new DoubleValue(0.0);
  
  public Sum(Expression expression, Evaluate evaluate) {
    super(expression, evaluate);
  }
  
  public LeafValue yield(Row row) {
    LeafValue result = null;
    evaluate.setRow(row); 
    
    try {
      result = evaluate.eval(expression);
      
      if (isLong == null) isLong = result instanceof LongValue ? true : false;
      
      if (isLong) {
        longSum.setValue(longSum.getValue() + result.toLong());
        return longSum;
      } else {
        doubleSum.setValue(doubleSum.getValue() + result.toDouble());
        return doubleSum;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (InvalidLeaf e) {
      e.printStackTrace();
    }
    
    return null;
  }
}
