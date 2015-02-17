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
  private ArrayList<Column> columns = new ArrayList<Column>();

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
   * @param left - left schema for append
   * @param right - right schema for append
   */
  public Schema(Schema left, Schema right) {
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
   * Checks if a given column exists in the schema.
   * 
   * @param inputColumn - the given column to check for
   * @return true if the given column exists in the schema, otherwise false
   */
  public boolean hasColumn(Column inputColumn) {
    boolean tableIsSet = !inputColumn.getTable().toString().equals("null");
    String inputColumnName = tableIsSet ? inputColumn.getWholeColumnName().toLowerCase()
                                       : inputColumn.getColumnName().toLowerCase();

    for (Column column : columns) {
      String columnName = tableIsSet ? column.getWholeColumnName().toLowerCase()
                                    : column.getColumnName().toLowerCase();
      if (inputColumnName.equals(columnName)) return true;
    }

    return false;
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
