package edu.buffalo.cse562.table;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.Index;

/**
 * Represents the schema for a table and its rows.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Schema implements Serializable {
  private static final long serialVersionUID = -1119625585280544652L;
  private ArrayList<Column> columns;
  private Index             primary;
  private ArrayList<Column> secondary;

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
   * Acquires a column at a specific index.
   * 
   * @param index - index for the column
   * @return column at specified index
   */
  public Column getColumn(int index) {
    return columns.get(index);
  }
  
  /**
   * Acquires the index of a given column.
   * 
   * @param column - the given column
   * @return index for the given column, null if it does not exist.
   */
  public Integer getIndex(Column column) {
    HashMap<String, Integer> lookupTable = this.getLookupTable();
    return lookupTable.get(column.getWholeColumnName().toLowerCase());
  }
  
  /**
   * Acquires the primary key indexes in the lookup table.
   * 
   * @return the primary key indexes in the lookup table
   */
  public List<Integer> getPrimaryIndexes() {
    List<Integer> out = new ArrayList<Integer>();
    HashMap<String, Integer> lookupTable = this.getLookupTable();
    
    for (Object o : primary.getColumnsNames()) {
      String name = (String) o;
      out.add(lookupTable.get(name.toLowerCase()));
    }
    
    return out;
  }
  
  /**
   * Sets the primary key index.
   * 
   * @param primary - the primary key index
   */
  public void setPrimaryKey(Index primary) {
    this.primary = primary;
  }
  
  /**
   * Sets the columns that act as secondary keys.
   * 
   * @param secondary - list of columns that act a secondary keys
   */
  public void setSecondaryKeys(ArrayList<Column> secondary) {
    this.secondary = secondary;
  }
  
  /**
   * Acquires the secondary key indexes in the lookup table.
   * 
   * @return the secondary key indexes in the lookup table
   */
  public List<Integer> getSecondaryIndexes() {
    List<Integer> out = new ArrayList<Integer>();
    HashMap<String, Integer> lookupTable = this.getLookupTable();
    
    for (Column column : secondary)
      out.add(lookupTable.get(column.getWholeColumnName().toLowerCase()));
    
    return out;
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
   * Acquires an ordered list of columns for the schema, that are part of a given table.
   * 
   * @param table - table to compare against
   * @return an ordered list of columns for the schema, that are part of a given table
   */
  public ArrayList<Column> getTableColumns(Table table) {
    ArrayList<Column> tableColumns = new ArrayList<Column>();
    
    for (Column column : columns) {
      if (column.getTable().getName().toLowerCase().equals(table.getName().toLowerCase())) {
        tableColumns.add(column);
      }
    }
    
    return tableColumns;
  }
  
  /**
   * Builds an index lookup table for the schema.
   * 
   * @return and index lookup table for the schema
   */
  public HashMap<String, Integer> getLookupTable() {
    HashMap<String, Integer> lookupTable = new HashMap<String, Integer>();
    for (int i = 0; i < this.size(); i++) {
      Column column = columns.get(i);
      boolean tableIsSet = !column.getTable().toString().equals("null");
      Integer integer = new Integer(i);
      lookupTable.put(column.getWholeColumnName().toLowerCase(), integer);
      if (tableIsSet) lookupTable.put(column.getColumnName().toLowerCase(), integer);
    }
    return lookupTable;
  }

  /**
   * Acquires the number of columns in the schema.
   * 
   * @return the number of columns in the schema
   */
  public int size() {
    return columns.size();
  }

  /**
   * Used during serialization.
   * 
   * @param stream - the object output stream used for serialization
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.writeInt(columns.size());
    
    for (Column column : columns) {
      // Handle table
      Table table = column.getTable();
      if (table == null) {
        stream.writeBoolean(false);
      } else {
        stream.writeBoolean(true);
        stream.writeObject(table.getSchemaName());
        stream.writeObject(table.getName());
        stream.writeObject(table.getAlias());
      }
      
      // Handle column name
      stream.writeObject(column.getColumnName());
    }
  }
  
  /**
   * Used during deserialization.
   * 
   * @param stream - the object input stream used for deserialization
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    int size = stream.readInt();
    columns = new ArrayList<Column>(size);
    
    for (int i = 0; i < size; i++) {
      // Handle table
      Table table = null;
      boolean hasTable = stream.readBoolean();
      if (hasTable) {
        String schemaName = (String) stream.readObject();
        String name = (String) stream.readObject();
        String alias = (String) stream.readObject();
        table = new Table(schemaName, name);
        table.setAlias(alias);
      }
      
      // Handle column name
      String columnName = (String) stream.readObject();
      columns.add(new Column(table, columnName));
    }
  }
}
