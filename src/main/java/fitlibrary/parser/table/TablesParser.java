package fitlibrary.parser.table;

import fitlibrary.parser.Parser;
import fitlibrary.parser.lookup.ParserFactory;
import fitlibrary.runResults.TestResults;
import fitlibrary.table.Cell;
import fitlibrary.traverse.Evaluator;
import fitlibrary.traverse.Traverse;
import fitlibrary.typed.Typed;
import fitlibrary.typed.TypedObject;

public class TablesParser implements Parser {
	protected static final TablesParser theOne = new TablesParser();

	@Override
	public TypedObject parseTyped(Cell cell, TestResults testResults) throws Exception {
		return Traverse.asTypedObject(cell);
	}

	@Override
	public boolean matches(Cell cell, Object result, TestResults testResults) throws Exception {
		return cell.equals(result);
	}

	@Override
	public String show(Object result) throws Exception {
		return result.toString();
	}

	@Override
	public Evaluator traverse(TypedObject typedObject) {
		throw new RuntimeException("No Traverse available");
	}

	public static ParserFactory parserFactory() {
	   	return new ParserFactory() {
    		@Override
			public Parser parser(Evaluator evaluator, Typed typed) {
    			return theOne;
    		}
    	};
	}

}
