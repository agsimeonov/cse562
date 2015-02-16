package edu.buffalo.cse562.iterator;

import java.util.ArrayList;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.parsetree.Evaluate;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

public class ProjectIterator implements RowIterator {
  private RowIterator iterator;
  private Schema schema;
  private ArrayList<Expression> expressions;
  private Evaluate evaluate;
  
  public ProjectIterator(RowIterator iterator, Schema schema, ArrayList<Expression> expressions) {
    this.iterator = iterator;
    this.schema = schema;
    this.expressions = expressions;
    evaluate = new Evaluate();
  }
  
  @Override
  public void close() {
    iterator.close();
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    Row inRow = iterator.next();
    Row outRow = new Row(schema);
    
    evaluate.setRow(inRow);
    for (Column column : schema.getColumns()) {
//      outRow.setValue(column, )
    }
    
    return outRow;
  }

  @Override
  public void open() {
    iterator.open();
  }
}
