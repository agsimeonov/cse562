package edu.buffalo.cse562.table;

import java.util.HashMap;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;

public class Row {
  private HashMap<String, LeafValue> values = new HashMap<String, LeafValue>();
  private Schema                     schema;

  public Row(Schema rowSchema) {
    schema = rowSchema;
  }

  public void addColumnValue(String wholeColumnName, LeafValue value) {
    values.put(wholeColumnName.toLowerCase(), value);
  }
  
  public LeafValue getValue(Column column) {
    return values.get(column.getWholeColumnName().toLowerCase());
  }

  public String toString() {
    String rowString = "";

    for (int i = 0; i < schema.numColumns(); i++) {
      try {
        Column column = schema.getColumns().get(i);
        LeafValue value = values.get(column.getWholeColumnName().toLowerCase());
        
        if (value instanceof LongValue) {
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

      if (i != schema.numColumns() - 1) rowString += "|";
    }

    return rowString;
  }
}
