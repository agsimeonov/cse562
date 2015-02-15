package edu.buffalo.cse562.table;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

/**
 * Represents the schema for a table and its rows.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Schema {
  private ArrayList<Column> columns = new ArrayList<Column>();
  private ArrayList<String> types   = new ArrayList<String>();

  /**
   * Creates the schema for a given table.
   * 
   * @param table - the table associated with the schema
   * @param columnDefinitions - an ordered list of column definitions for each column in the schema
   */
  public Schema(Table table, List<ColumnDefinition> columnDefinitions) {
    for (ColumnDefinition columnDefinition : columnDefinitions) {
      columns.add(new Column(table, columnDefinition.getColumnName()));
      types.add(columnDefinition.getColDataType().getDataType());
    }
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
    String inputColumnName = inputColumn.getWholeColumnName().toLowerCase();
    
    for (Column column : columns) {
      String columnName = column.getWholeColumnName().toLowerCase();
      if (inputColumnName.equals(columnName)) return true;
    }
    
    return false;
  }
  
  /**
   * Acquires the column type string for a given column.
   * 
   * @param column - given column
   * @return the type string for a column, null if that column does not exist in the schema.
   */
  public String getColumnType(Column column) {
    for (int i = 0; i < columns.size(); i++) {
      if (column.getWholeColumnName().equals(columns.get(i).getWholeColumnName())) {
        return types.get(i);
      }
    }
    return null;
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
