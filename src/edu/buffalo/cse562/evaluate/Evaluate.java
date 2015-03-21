package edu.buffalo.cse562.evaluate;

import java.sql.SQLException;
import java.util.HashMap;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import edu.buffalo.cse562.Eval;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * Evaluates any expression in the context of a tuple.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Evaluate extends Eval {
  private Row                      row;
  private HashMap<String, Integer> lookupTable;

  /** 
   * Initializes the evaluator. 
   * 
   * @param schema - the input schema
   */
  public Evaluate(Schema schema) {
    super();
    lookupTable = schema.getLookupTable();
  }

  /**
   * Sets the row used for the evaluation.
   * 
   * @param row - row used for the evaluation
   */
  public void setRow(Row row) {
    this.row = row;
  }
  
  /**
   * Takes a given column and produces the value for that column on the currently set row.
   * 
   * @param column - a given column
   * @return the value for a given column, null if the column doesn't exist or the row is not set
   */
  @Override
  public LeafValue eval(Column column) throws SQLException {
    if (row == null) return null;
    else return row.getValue(lookupTable.get(column.getWholeColumnName().toLowerCase()));
  }
  
  /**
   * Converts a given function to a column and passes it to {{@link #eval(Column)} for processing.
   * Takes care of the DATE('YYYY-MM-DD') function.
   * 
   * @param function - function to be converted to a column
   */
  @Override
  public LeafValue eval(Function function) throws SQLException {
    if (function.getName().toLowerCase().equals("date"))
      return new DateValue(function.getParameters().getExpressions().get(0).toString());
    
    return eval(new Column(new Table(), function.toString()));
  }
}
