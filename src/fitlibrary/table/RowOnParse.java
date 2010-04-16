/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.table;

import fit.Parse;
import fitlibrary.dynamicVariable.VariableResolver;
import fitlibrary.exception.FitLibraryShowException;
import fitlibrary.exception.table.MissingCellsException;
import fitlibrary.global.PlugBoard;
import fitlibrary.utility.ExtendedCamelCase;
import fitlibrary.utility.ParseUtility;
import fitlibrary.utility.TestResults;

public class RowOnParse extends ParseNode implements Row {
    private boolean rowIsHidden = false;
    
	public RowOnParse(Parse parse) {
        super(parse);
    }
    public RowOnParse() {
        this(new Parse("tr","",null,null));
    }
    public RowOnParse(String... ss) {
    	this();
    	for (String s : ss)
    		addCell(s);
    }
    public Parse parse() {
    	return parse;
    }
    public int size() {
    	if (parse.parts == null)
    		return 0;
        return parse.parts.size();
    }
    public Cell cell(int i) {
        if (!cellExists(i))
            throw new MissingCellsException("");
        return new CellOnParse(parse.parts.at(i));
    }
    public boolean cellExists(int i) {
        return i >= 0 && i < size();
    }
    @Override
	public String toString() {
        return "Row["+ParseUtility.toString(parse.parts)+"]";
    }
    @Override
	public void pass(TestResults testResults) {
    	if (rowIsHidden)
    		System.out.println("Bug: colouring a cell in a hidden table");
    	super.pass(testResults);
    }
    @Override
	public void fail(TestResults testResults) {
    	if (rowIsHidden)
    		System.out.println("Bug: colouring a cell in a hidden table");
    	super.fail(testResults);
    }
    @Override
	public void error(TestResults testResults, Throwable throwable) {
		Throwable e = PlugBoard.exceptionHandling.unwrapThrowable(throwable);
		if (e instanceof FitLibraryShowException)
        	handleShow((FitLibraryShowException) e);
        else
        	cell(0).error(testResults,e);
    }
    private void handleShow(FitLibraryShowException exception) {
    	Cell cell = addCell();
    	cell.setText(exception.getResult().getHtmlString());
    	cell.shown();
    }
    public String text(int i, VariableResolver resolver) {
        return cell(i).text(resolver);
    }
   public void missing(TestResults testResults) {
        cell(0).expectedElementMissing(testResults);
    }
    public Cell addCell() {
    	Cell cell = TableFactory.cell("");
		addCell(cell);
		return cell;
    }
    public Row addCell(Cell cell) {
    	if (rowIsHidden)
    		System.out.println("Bug: Adding a cell to a hidden row in a table");
        if (parse.parts == null)
            parse.parts = cell.parse();
        else
            parse.parts.last().more = cell.parse();
        return this;
    }
	public Cell addCell(String text) {
        Cell cell = TableFactory.cell(text);
        addCell(cell);
        return cell;
	}
    public Cell addCell(String text, int cols) {
        Cell cell = new CellOnParse(text);
        cell.setColumnSpan(cols);
        addCell(cell);
        return cell;
    }
    @Override
	public boolean equals(Object object) {
        if (!(object instanceof RowOnParse))
            return false;
        RowOnParse other = (RowOnParse)object;
        if (other.size() != size())
            return false;
        for (int i = 0; i < size(); i++)
            if (!cell(i).equals(other.cell(i)))
                return false;
        return true;
    }
	public RowOnParse rowFrom(int i) {
		// Can be an empty row
		return new RowOnParse(new Parse("tr","",parse.parts.at(i),null));
	}
	public Cell last() {
		return cell(size()-1);
	}
	public void ignore(TestResults testResults) {
		for (int i = 0; i < size(); i++)
			cell(i).ignore(testResults);
	}
	public boolean isEmpty() {
		return size() == 0;
	}
	public void setIsHidden() {
		this.rowIsHidden  = true;
		for (int i = 0; i < size(); i++)
			cell(i).setIsHidden();
	}
	public Cell lastCell() {
		return cell(size()-1);
	}
	public void addCommentRow(CellOnParse cell) {
		RowOnParse commentRow = new RowOnParse();
		commentRow.addCell("note");
		commentRow.addCell(cell);
		Parse next = parse.more;
		parse.more = commentRow.parse;
		commentRow.parse.more = next;
	}
	public Row rowTo(int from, int upto) {
		Row row = TableFactory.row();
		for (int i = from; i < upto; i++)
			row.addCell(TableFactory.cell(cell(i)));
		return row;
	}
	public void passKeywords(TestResults testResults) {
		for (int i = 0; i < size(); i += 2)
			cell(i).pass(testResults);
	}
	public Row copy() {
		Row rowCopy = TableFactory.row();
		for (int i = 0; i < size(); i++)
			rowCopy.addCell(TableFactory.cell(cell(i).fullText()));
		return rowCopy;
	}
	public void removeFirstCell() {
		parse.parts = parse.parts.more;
	}
	public void removeAllCells() {
		parse.parts = null;
	}
	public String methodNameForPlain(VariableResolver resolver) {
		String name = "";
		for (int i = 0; i < size(); i += 2) {
			name += text(i,resolver);
			if ((i+1) < size())
				name += "|";
		}
		return name;
	}
	public String methodNameForCamel(VariableResolver resolver) {
		String name = "";
		for (int i = 0; i < size(); i += 2)
			name += text(i,resolver)+" ";
		return ExtendedCamelCase.camel(name.trim());
	}
	public int argumentCount() {
		return size() / 2;
	}
	public boolean hasFurtherRows() {
		return parse.more != null;
	}
	public int getColumnSpan() {
		int col = 0;
		for (int i = 0; i < size(); i++)
			col += cell(i).getColumnSpan();
		return col;
	}
	public void setColumnSpan(int span) {
		if (isEmpty())
			addCell();
		lastCell().setColumnSpan(span - getColumnSpan() + lastCell().getColumnSpan());
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	@Override
	public void removeCell(int i) {
		cell(i-1).parse().more = null;
		cell(i-1).parse().trailer = "";
	}
}
