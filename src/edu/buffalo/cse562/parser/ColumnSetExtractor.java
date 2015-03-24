package edu.buffalo.cse562.parser;

import java.util.HashSet;
import java.util.Set;

import edu.buffalo.cse562.table.TableManager;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;

/**
 * Used to acquire a set of column names needed from tables in a select expression.
 * 
 * @author Alexander Simeonov
 */
public class ColumnSetExtractor implements SelectVisitor, 
                                           SelectItemVisitor, 
                                           FromItemVisitor, 
                                           ExpressionVisitor {
  private static final String WILDCARD  = "*";
  private Set<String>         columns   = new HashSet<String>();
  private Set<String>         tables    = new HashSet<String>();
  private Set<String>         wildcards = new HashSet<String>();

  /**
   * Acquires the generated set of column names.
   * 
   * @return the generated set column names
   */
  public Set<String> getColumns() {
    return columns;
  }

  @Override
  public void visit(PlainSelect in) { 
    // SELECT
    if (in.getSelectItems() != null) {
      for (Object object : in.getSelectItems()) {
        SelectItem item = (SelectItem) object;
        item.accept(this);
      }
    }
    
    // FROM
    in.getFromItem().accept(this);
    
    // JOIN
    if (in.getJoins() != null) {
      for (Object object : in.getJoins()) {
        Join join = (Join) object;
        if (join.getOnExpression() != null) join.getOnExpression().accept(this);
        if (join.getRightItem() != null) join.getRightItem().accept(this);
      }
    }
    
    // WHERE
    if (in.getWhere() != null) in.getWhere().accept(this);
    
    // GROUP BY
    if (in.getGroupByColumnReferences() != null) {
      for (Object object : in.getGroupByColumnReferences()) {
        Expression expression = (Expression) object;
        expression.accept(this);
      }
    }
    
    // HAVING
    if (in.getHaving() != null) in.getHaving().accept(this);
    
    // ORDER BY
    if (in.getOrderByElements() != null) {
      for (Object object : in.getOrderByElements()) {
        Expression expression = (Expression) object;
        expression.accept(this);
      }
    }
    
    // Handle wildcards
    for (String table : tables) {
      if (wildcards.contains(WILDCARD) || wildcards.contains(table)) {
        for (Column column : TableManager.getTable(table).getSchema().getColumns())
          columns.add(column.getWholeColumnName().toLowerCase());
      }
    }
  }

  @Override
  public void visit(Union in) {
    ColumnSetExtractor extractor = new ColumnSetExtractor();
    in.accept(extractor);
    columns.addAll(extractor.getColumns());
  }

  @Override
  public void visit(AllColumns in) {
    wildcards.add(WILDCARD);
  }

  @Override
  public void visit(AllTableColumns in) {
    wildcards.add(in.getTable().getWholeTableName().toLowerCase());
  }

  @Override
  public void visit(SelectExpressionItem in) {
    in.getExpression().accept(this);
  }

  @Override
  public void visit(Table in) {
    tables.add(in.getWholeTableName().toLowerCase());
  }

  @Override
  public void visit(SubJoin in) {
    if (in.getJoin() != null) {
      Join join = in.getJoin();
      if (join.getOnExpression() != null) join.getOnExpression().accept(this);
      if (join.getRightItem() != null) join.getRightItem().accept(this);
    }
    if (in.getLeft() != null) in.getLeft().accept(this);
  } 
  
  @Override
  public void visit(NullValue in) {}

  @Override
  public void visit(Function in) {
    if (in.getParameters() != null) {
      if (in.getParameters().getExpressions() != null) {
        for (Object expression : in.getParameters().getExpressions())
          ((Expression) expression).accept(this);
      }
    }
  }

  @Override
  public void visit(InverseExpression in) {}

  @Override
  public void visit(JdbcParameter in) {}

  @Override
  public void visit(DoubleValue in) {}

  @Override
  public void visit(LongValue in) {}

  @Override
  public void visit(DateValue in) {}

  @Override
  public void visit(TimeValue in) {}

  @Override
  public void visit(TimestampValue in) {}

  @Override
  public void visit(Parenthesis in) {
    in.getExpression().accept(this);
  }

  @Override
  public void visit(StringValue in) {}

  @Override
  public void visit(Addition in) {
    binary(in);
  }

  @Override
  public void visit(Division in) {
    binary(in);
  }

  @Override
  public void visit(Multiplication in) {
    binary(in);
  }

  @Override
  public void visit(Subtraction in) {
    binary(in);
  }

  @Override
  public void visit(AndExpression in) {
    binary(in);
  }

  @Override
  public void visit(OrExpression in) {
    binary(in);
  }

  @Override
  public void visit(Between in) {}

  @Override
  public void visit(EqualsTo in) {
    binary(in);
  }

  @Override
  public void visit(GreaterThan in) {
    binary(in);
  }

  @Override
  public void visit(GreaterThanEquals in) {
    binary(in);
  }

  @Override
  public void visit(InExpression in) {}

  @Override
  public void visit(IsNullExpression in) {}

  @Override
  public void visit(LikeExpression in) {
    binary(in);
  }

  @Override
  public void visit(MinorThan in) {
    binary(in);
  }

  @Override
  public void visit(MinorThanEquals in) {
    binary(in);
  }

  @Override
  public void visit(NotEqualsTo in) {
    binary(in);
  }

  @Override
  public void visit(Column in) {
    columns.add(in.getWholeColumnName().toLowerCase());
  }

  @Override
  public void visit(SubSelect in) {
    ColumnSetExtractor extractor = new ColumnSetExtractor();
    in.getSelectBody().accept(extractor);
    columns.addAll(extractor.getColumns());
  }

  @Override
  public void visit(CaseExpression in) {}

  @Override
  public void visit(WhenClause in) {}

  @Override
  public void visit(ExistsExpression in) {}

  @Override
  public void visit(AllComparisonExpression in) {}

  @Override
  public void visit(AnyComparisonExpression in) {}

  @Override
  public void visit(Concat in) {
    binary(in);
  }

  @Override
  public void visit(Matches in) {
    binary(in);
  }

  @Override
  public void visit(BitwiseAnd in) {
    binary(in);
  }

  @Override
  public void visit(BitwiseOr in) {
    binary(in);
  }

  @Override
  public void visit(BitwiseXor in) {
    binary(in);
  }
  
  /**
   * Handles binary expression, calls visit on left and right.
   * 
   * @param binary - a binary expression to handle
   */
  private void binary(BinaryExpression binary) {
    binary.getLeftExpression().accept(this);
    binary.getRightExpression().accept(this);
  }
}
