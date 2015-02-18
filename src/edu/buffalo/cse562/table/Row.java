package edu.buffalo.cse562.table;

import java.util.HashMap;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;

/**
 * Represents a row tuple of LeafValue data elements.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Row {
  private HashMap<String, LeafValue> values = new HashMap<String, LeafValue>();
  private Schema                     schema;

  /**
   * Creates an empty row tuple of LeafValue data elements.
   * 
   * @param schema - the expected schema for the row
   */
  public Row(Schema schema) {
    this.schema = schema;
  }
  
  /**
   * Creates a new row that is the concatenation of two given row.
   * 
   * @param schema - the expected schema for the row
   * @param left - left row for concatenation
   * @param right - right row for concatenation
   */
  public Row(Schema schema, Row left, Row right) {
    this.schema = schema;
    values.putAll(left.values);
    values.putAll(right.values);
  }
  
  /**
   * Sets a value for a given column, only if said column exists in the row schema.
   * 
   * @param column - column the value belongs to
   * @param value - given value to add
   * @return true if the column exists in the row schema and the value was added successfully.
   */
  public boolean setValue(Column column, LeafValue value) {
    String name = column.getWholeColumnName().toLowerCase();
    if (!schema.hasColumn(column)) return false;
    values.put(name, value);
    return true;
  }
  
  /**
   * Acquires row value for a given column.
   * 
   * @param column - given column for row value
   * @return desired row value for the given column, null if the column does not exist
   */
  public LeafValue getValue(Column column) {
    return values.get(column.getWholeColumnName().toLowerCase());
  }
  
  /**
   * Acquires the row schema.
   * 
   * @return the row schema
   */
  public Schema getSchema() {
    return schema;
  }

  @Override
  public String toString() {
    String rowString = "";

    for (int i = 0; i < schema.size(); i++) {
      try {
        Column column = schema.getColumns().get(i);
        LeafValue value = values.get(column.getWholeColumnName().toLowerCase());
        
        if (value == null) {
          // Do nothing
        } else if (value instanceof LongValue) {
          rowString += Long.toString(value.toLong());
        } else if (value instanceof DoubleValue) {
          rowString += Double.toString(value.toDouble());
        } else if (value instanceof StringValue) {
          String stringValue = value.toString();
          rowString += stringValue.substring(1, stringValue.length() - 1);
        } else {
          rowString += value.toString();
        }
      } catch (InvalidLeaf e) {
        e.printStackTrace();
      }

      if (i != schema.size() - 1) rowString += "|";
    }

    return rowString;
  }
}
