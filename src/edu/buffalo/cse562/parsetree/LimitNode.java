package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import net.sf.jsqlparser.statement.select.Limit;
import edu.buffalo.cse562.iterator.LimitIterator;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;

public class LimitNode extends ParseTree {

  private Limit limit; 
  public LimitNode(ParseTree base, Limit limit) {
    super(base);
    this.limit = limit; 
  }

  @Override
  public Iterator<Row> iterator() {
    return new LimitIterator((RowIterator)left.iterator(),limit);
  }

}
