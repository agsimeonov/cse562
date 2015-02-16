package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;

public class TableNode extends ParseTree {
  private DataTable dataTable;

  public TableNode(ParseTree base, DataTable dataTable) {
    super(base);
    this.dataTable = dataTable;
  }

  @Override
  public Iterator<Row> iterator() {
    return dataTable.iterator();
  }
}
