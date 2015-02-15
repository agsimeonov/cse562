package edu.buffalo.cse562.parsetree;

import java.sql.SQLException;

import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.Eval;
import edu.buffalo.cse562.table.Row;

/**
 * Evaluates any expression in the context of a tuple.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Evaluate extends Eval {
  private Row row;
  
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
    return row == null ? null : row.getValue(column);
  }
}
