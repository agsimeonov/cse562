package edu.buffalo.cse562.iterator;

import java.util.ArrayList;

import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import edu.buffalo.cse562.table.Row;

public class NonAggregateIterator extends ProjectIterator implements RowIterator {
  public NonAggregateIterator(RowIterator iterator, ArrayList<SelectExpressionItem> expressions) {
    super(iterator, expressions);
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public Row next() {
    evaluate.setRow(iterator.next());
//    if (!this.hasNext()) return null;
//    Row inRow = iterator.next();
//    Row outRow = new Row(schema);
//    
//    evaluate.setRow(inRow);
//    for (Column column : schema.getColumns()) {
////      outRow.setValue(column, )
//    }
//    
//    return outRow;
    return null;
  }
}
