package edu.buffalo.cse562.parsetree;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import edu.buffalo.cse562.table.Row;

public class ProjectNode extends ParseTree {
  protected ArrayList<SelectExpressionItem> expressions = new ArrayList<SelectExpressionItem>();

  public ProjectNode(ParseTree base, ArrayList<SelectExpressionItem> expressions) {
    super(base);
    this.expressions = expressions;
  }

  @Override
  public Iterator<Row> iterator() {
    // Determine here whether we have an aggregate, non aggregate, or distinct and choose correctly
//    return new NonAggregateIterator((RowIterator) left.getLeft().iterator(), null, expressions);
    return null;
  }
}
