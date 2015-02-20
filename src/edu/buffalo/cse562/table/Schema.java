package edu.buffalo.cse562.table;

import java.util.ArrayList;

import net.sf.jsqlparser.schema.Column;

/**
 * Represents the schema for a table and its rows.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Schema {
  private ArrayList<Column> columns;

  /**
   * Creates the schema for a given table.
   * 
   * @param column - columns in this schema
   */
  public Schema(ArrayList<Column> columns) {
    this.columns = columns;
  }
  
  /**
   * Creates a new schema that is the concatenation of two given schemas.
   * 
   * @param left - left schema for concatenation
   * @param right - right schema for concatenation
   */
  public Schema(Schema left, Schema right) {
    columns = new ArrayList<Column>();
    columns.addAll(left.getColumns());
    columns.addAll(right.getColumns());
  }

  /**
   * Acquires an ordered list of columns for the schema.
   * 
   * @return an ordered list of columns for the schema
   */
  public ArrayList<Column> getColumns() {
    return columns;
  }

  /**
   * Acquires the number of columns in the schema.
   * 
   * @return the number of columns in the schema
   */
  public int size() {
    return columns.size();
  }
}
