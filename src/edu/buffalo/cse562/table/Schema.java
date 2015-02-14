package edu.buffalo.cse562.table;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class Schema {
  private final ArrayList<Column> columns = new ArrayList<Column>();
  private final ArrayList<String> types   = new ArrayList<String>();

  public Schema(Table table, List<ColumnDefinition> columnDefinitions) {
    for (ColumnDefinition columnDefinition : columnDefinitions) {
      columns.add(new Column(table, columnDefinition.getColumnName()));
      types.add(columnDefinition.getColDataType().getDataType());
    }
  }
  
  // Copy constructor (fields hold references)
  public Schema(Schema original) {
    for (int i = 0; i < original.numColumns(); i++) {
      columns.add(original.columns.get(i));
      types.add(original.types.get(i));
    }
  }

  public ArrayList<Column> getColumns() {
    return columns;
  }
  
  public String getColumnType(Column column) {
    for (int i = 0; i < columns.size(); i++) {
      if (column.getWholeColumnName().equals(columns.get(i).getWholeColumnName())) {
        return types.get(i);
      }
    }
    return null;
  }

  public int numColumns() {
    return columns.size();
  }
  
//  public Schema getSubSchema(Column... subset) {
//    //MAYBE USE SOMETHING LIKE THIS FOR PROJECT
//    Schema subSchema = new Schema(this);
//    
//    for (Column)
//    
//    return null;
//  }
}
