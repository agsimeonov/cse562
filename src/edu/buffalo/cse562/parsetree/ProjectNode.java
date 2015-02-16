package edu.buffalo.cse562.parsetree;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.jsqlparser.expression.Expression;
import edu.buffalo.cse562.iterator.ProjectIterator;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;

public class ProjectNode extends ParseTree {
  private ArrayList<Expression> expressions = new ArrayList<Expression>();

  public ProjectNode(ParseTree base, ArrayList<Expression> expressions) {
    super(base);
    this.expressions = expressions;
  }
  
  public ArrayList<Expression> getExpressions() {
    return expressions;
  }

  @Override
  public Iterator<Row> iterator() {
    return new ProjectIterator((RowIterator) left.getLeft().iterator(), null, expressions);
  }
}
