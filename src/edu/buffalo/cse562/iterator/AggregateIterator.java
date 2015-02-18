package edu.buffalo.cse562.iterator;

import java.sql.SQLException;
import java.util.List;

import edu.buffalo.cse562.table.Row;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

public class AggregateIterator extends ProjectIterator {
  private boolean ready = true;
  
  public AggregateIterator(RowIterator iterator, List<SelectExpressionItem> items) {
    super(iterator, items);
    open();
  }

  @Override
  public boolean hasNext() {
    if (iterator.hasNext() && ready) {
      return true;
    }
    close();
    return false;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    evaluate.setRow(iterator.next());
    Row row = new Row(schema);
    
    for (int i = 0; i < expressions.size(); i++) {
      Column column = schema.getColumns().get(i);
      try {
        row.setValue(column, evaluate.eval(expressions.get(i)));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    ready = false;
    return  null;
  }
  
  @Override
  public void close() {
    super.close();
    ready = false;
  }

  @Override
  public void open() {
    super.open();
    ready = true;
  }
}
