package edu.buffalo.cse562.visitor;

import edu.buffalo.cse562.table.TableManager;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

/**
 * Manages evaluation of top level statements.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class StatementVisitorImpl implements StatementVisitor {

  @Override
  public void visit(Select select) {
    select.getSelectBody().accept(new SelectManager());
  }

  @Override
  public void visit(Delete delete) {}

  @Override
  public void visit(Update update) {}

  @Override
  public void visit(Insert insert) {}

  @Override
  public void visit(Replace replace) {}

  @Override
  public void visit(Drop drop) {}

  @Override
  public void visit(Truncate truncate) {}

  @Override
  public void visit(CreateTable createTable) {
    TableManager.createTable(createTable);
  }
}
