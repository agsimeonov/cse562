package edu.buffalo.cse562.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import edu.buffalo.cse562.optimizer.Optimizer;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.TableManager;

/**
 * Manages evaluation of top level statements.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class StatementParser implements StatementVisitor {

  @Override
  public void visit(Select select) {
    TreeBuilder treeBuilder = new TreeBuilder(select.getSelectBody());
    ColumnSetExtractor extractor = new ColumnSetExtractor();
    select.getSelectBody().accept(extractor);
    ParseTree root = treeBuilder.getRoot();
    Optimizer.optimize(root, extractor.getColumns());
    BufferedWriter print = new BufferedWriter(new OutputStreamWriter(System.out));
    try {
      for (Row row : treeBuilder.getRoot()) {
        print.write(row.toString());
        print.write("\n");
      }
      print.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
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
