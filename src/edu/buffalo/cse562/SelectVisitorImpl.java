package edu.buffalo.cse562;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.Union;

public class SelectVisitorImpl implements SelectVisitor {

  @Override
  public void visit(PlainSelect plainSelect) {
    // Syste.out.println(plainSelect.getSelectItems());
    for (Object item : plainSelect.getSelectItems()) {
      SelectItem selectItem = ((SelectItem) item);
      selectItem.accept((new SelectItemVisitorImpl()));
      System.out.println(item.getClass());
    }

  }

  @Override
  public void visit(Union union) {
    // TODO Auto-generated method stub

  }

}
