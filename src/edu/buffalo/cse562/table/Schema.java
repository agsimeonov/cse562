package edu.buffalo.cse562.table;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class Schema {
  private final ArrayList<Column> schema;
  
  public Schema(Table table, List<ColumnDefinition> columnDefinitions) {
    schema = new ArrayList<Column>();
  }
}
