package edu.buffalo.cse562;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;

public class Main {
    public static void main(String[] args) {
    	try {
    	File file = new File("/home/agsimeon/Downloads/sample.txt");
		FileReader stream = new FileReader(file);
		CCJSqlParser parser = new CCJSqlParser(stream);
		Statement stmt;
		while((stmt = parser.Statement()) != null) { 
			stmt.accept(new StatementVisitorImpl());
		}
		Expression expr;
		while((expr = parser.Expression()) != null) { 
			expr.accept(new ExpressionVisitorImpl());
		}
    	} catch(IOException e) {
    		
    	} catch (ParseException e) {
    		
    	}
    }
}
