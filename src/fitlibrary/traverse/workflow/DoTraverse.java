/*
a * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.traverse.workflow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import fitlibrary.DefineAction;
import fitlibrary.closure.ICalledMethodTarget;
import fitlibrary.definedAction.DefineActionsOnPage;
import fitlibrary.definedAction.DefineActionsOnPageSlowly;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.exception.FitLibraryShowException;
import fitlibrary.exception.IgnoredException;
import fitlibrary.exception.table.MissingCellsException;
import fitlibrary.flow.GlobalScope;
import fitlibrary.global.PlugBoard;
import fitlibrary.parser.Parser;
import fitlibrary.parser.graphic.GraphicParser;
import fitlibrary.parser.graphic.ObjectDotGraphic;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.table.RowOnParse;
import fitlibrary.traverse.FitHandler;
import fitlibrary.traverse.function.CalculateTraverse;
import fitlibrary.traverse.function.ConstraintTraverse;
import fitlibrary.traverse.workflow.caller.DefinedActionCaller;
import fitlibrary.traverse.workflow.caller.TwoStageSpecial;
import fitlibrary.traverse.workflow.special.PrefixSpecialAction;
import fitlibrary.traverse.workflow.special.SpecialActionContext;
import fitlibrary.traverse.workflow.special.PrefixSpecialAction.NotSyle;
import fitlibrary.typed.NonGenericTyped;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.ClassUtility;
import fitlibrary.utility.TestResults;
import fitlibrary.xref.CrossReferenceFixture;

public class DoTraverse extends DoTraverseInterpreter implements SpecialActionContext, FlowEvaluator{
	private final PrefixSpecialAction prefixSpecialAction = new PrefixSpecialAction(this);
	public static final String BECOMES_TIMEOUT = "becomes";
	// Methods that can be called within DoTraverse.
	// Each element is of the form "methodName/argCount"
	private final static String[] methodsThatAreVisibleAsActions = {
		"calculate/0", "start/1", "constraint/0", "failingConstraint/0",
		"addAs/2"
	}; // The rest of the methods that used to be here are now in GlobalScope
	//------------------- Methods that are visible as actions (the rest are hidden):
	public List<String> methodsThatAreVisible() {
		return Arrays.asList(methodsThatAreVisibleAsActions);
	}
	public DoTraverse() {
		super();
	}
	public DoTraverse(Object sut) {
		super(sut);
	}
	public DoTraverse(TypedObject typedObject) {
		super(typedObject);
	}

	//--- FIXTURE WRAPPERS FOR THIS (and so not available in GlobalScope):
	/** To allow for a CalculateTraverse to be used for the rest of the table.
     */
	public CalculateTraverse calculate() {
		CalculateTraverse traverse;
		if (this.getClass() == DoTraverse.class)
			traverse = new CalculateTraverse(getTypedSystemUnderTest());
		else
			traverse = new CalculateTraverse(this);
		return traverse;
	}
    /** To allow for DoTraverse to be used without writing any fixturing code.
     */
	public void start(String className) {
		try {
		    setSystemUnderTest(ClassUtility.newInstance(className));
		} catch (Exception e) {
		    throw new FitLibraryException("Unknown class: "+className);
		}
	}
	/** To allow for a ConstraintTraverse to be used for the rest of the table.
     */
	public ConstraintTraverse constraint() {
		return new ConstraintTraverse(this);
	}
	/** To allow for a failing ConstraintTraverse to be used for the rest of the table.
     */
	public ConstraintTraverse failingConstraint() {
		ConstraintTraverse traverse = new ConstraintTraverse(this,false);
		return traverse;
	}

	//------ THE FOLLOWING ARE HERE SO THAT THEY'RE STILL ACCESSIBLE FROM A SUBCLASS:
	
	//--- BECOMES, ETC TIMEOUTS:
	protected void becomesTimeout(int timeout) {
		global().becomesTimeout(timeout);
	}
	protected int becomesTimeout() {
		return global().becomesTimeout();
	}
	protected int getTimeout(String name) {
		return global().getTimeout(name);
	}
	protected void putTimeout(String name, int timeout) {
		global().putTimeout(name,timeout);
	}
	//--- STOP ON ERROR AND ABANDON:
	/** When (stopOnError), don't continue interpreting a table if there's been a problem */
	public void setStopOnError(boolean stopOnError) {
		global().setStopOnError(stopOnError);
	}
	public void abandonStorytest() {
		global().abandonStorytest();
	}
	//--- DYNAMIC VARIABLES:
	protected boolean addDynamicVariablesFromFile(String fileName) {
		return global().addDynamicVariablesFromFile(fileName);
	}
	protected void addDynamicVariablesFromUnicodeFile(String fileName) throws IOException {
		global().addDynamicVariablesFromUnicodeFile(fileName);
	}
	protected boolean clearDynamicVariables() {
		return global().clearDynamicVariables();
	}
	protected boolean setSystemPropertyTo(String property, String value) {
		return global().setSystemPropertyTo(property, value);
	}
	public void setFitVariable(String variableName, Object result) {
		global().setFitVariable(variableName, result);
	}
	protected Object getSymbolNamed(String fitSymbolName) {
		return global().getSymbolNamed(fitSymbolName);
	}
	//--- SLEEP & STOPWATCH:
	protected boolean sleepFor(int milliseconds) {
		return global().sleepFor(milliseconds);
	}
	protected void startStopWatch() {
		global().startStopWatch();
	}
	protected long stopWatch() {
		return global().stopWatch();
	}
	//--- FIXTURE SELECTION
	protected SetVariableTraverse setVariables() {
		return global().setVariables();
	}
	protected DoTraverse file(String fileName) {
		return global().file(fileName);
	}
	protected CrossReferenceFixture xref(String suiteName) {
		return global().xref(suiteName);
	}
	//--- DEFINED ACTIONS
	protected DefineAction defineAction(String wikiClassName) {
		return global().defineAction(wikiClassName);
	}
	protected DefineAction defineAction() {
		return global().defineAction();
	}
	protected DefineActionsOnPageSlowly defineActionsSlowlyAt(String pageName) {
		return global().defineActionsSlowlyAt(pageName);
	}
	protected DefineActionsOnPage defineActionsAt(String pageName) {
		return global().defineActionsAt(pageName);
	}
	protected DefineActionsOnPage defineActionsAtFrom(String pageName, String rootLocation) {
		return global().defineActionsAtFrom(pageName,rootLocation);
	}
	protected void clearDefinedActions() {
		global().clearDefinedActions();
	}
	protected boolean toExpandDefinedActions() {
		return global().toExpandDefinedActions();
	}
	public void setExpandDefinedActions(boolean expandDefinedActions) {
		global().setExpandDefinedActions(expandDefinedActions);
	}
	//--- RANDOM, TO, GET, FILE, HARVEST
	protected RandomSelectTraverse selectRandomly(String var) {
		return global().selectRandomly(var);
	}
	protected boolean harvestUsingPatternFrom(String[] vars, String pattern, String text) {
		return global().harvestUsingPatternFrom(vars, pattern, text);
	}
	//--- FILE LOGGING
	protected void recordToFile(String fileName) {
		global().recordToFile(fileName);
	}
	protected void startLogging(String fileName) {
		global().startLogging(fileName);
	}
	public void logMessage(String s) {
		global().logMessage(s);
	}
	//--- SHOW
	@Override
	public void show(Row row, String text) {
		global().show(row, text);
	}
	public void showAfterTable(String s) {
		showAsAfterTable("Logs",s);
	}
	public void showAsAfterTable(String title,String s) {
		global().showAsAfterTable(title,s);
	}

	//------------------- Postfix Special Actions:
	/** Check that the result of the action in the first part of the row is the same as
	 *  the expected value in the last cell of the row.
	 */
	public void is(TestResults testResults, final RowOnParse row) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("DoTraverseIs");
		ICalledMethodTarget target = findMethodFromRow222(row,0,less);
		Cell expectedCell = row.last();
		target.invokeAndCheckForSpecial(row.rowTo(1,row.size()-2),expectedCell,testResults,row,operatorCell(row));
	}
	public void equals(TestResults testResults, final RowOnParse row) throws Exception {
		is(testResults,row);
	}
	/** Check that the result of the action in the first part of the row is not the same as
	 *  the expected value in the last cell of the row.
	 */
	public void isNot(TestResults testResults, final RowOnParse row) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("DoTraverseIs");
		Cell specialCell = operatorCell(row);
		Cell expectedCell = row.last();
		try {
			ICalledMethodTarget target = findMethodFromRow222(row,0,less);
			Object result = target.invoke(row.rowTo(1,row.size()-2),testResults,true);
			target.notResult(expectedCell, result, testResults);
        } catch (IgnoredException e) {
            //
        } catch (InvocationTargetException e) {
        	Throwable embedded = e.getTargetException();
        	if (embedded instanceof FitLibraryShowException) {
        		specialCell.error(testResults);
        		row.error(testResults, e);
        	} else
        		expectedCell.exceptionMayBeExpected(false, e, testResults);
        } catch (Exception e) {
        	expectedCell.exceptionMayBeExpected(false, e, testResults);
        }
	}
	/** Check that the result of the action in the first part of the row is less than
	 *  the expected value in the last cell of the row.
	 */
	public void lessThan(TestResults testResults, final RowOnParse row) throws Exception {
		Comparison compare = new Comparison() {
			@SuppressWarnings("unchecked")
			public boolean compares(Comparable actual, Comparable expected) {
				return actual.compareTo(expected) < 0;
			}
		};
		comparison(testResults, row, compare);
	}
	/** Check that the result of the action in the first part of the row is less than
	 *  or equal to the expected value in the last cell of the row.
	 */
	public void lessThanEquals(TestResults testResults, final RowOnParse row) throws Exception {
		Comparison compare = new Comparison() {
			@SuppressWarnings("unchecked")
			public boolean compares(Comparable actual, Comparable expected) {
				return actual.compareTo(expected) <= 0;
			}
		};
		comparison(testResults, row, compare);
	}
	/** Check that the result of the action in the first part of the row is greater than
	 *  the expected value in the last cell of the row.
	 */
	public void greaterThan(TestResults testResults, final RowOnParse row) throws Exception {
		Comparison compare = new Comparison() {
			@SuppressWarnings("unchecked")
			public boolean compares(Comparable actual, Comparable expected) {
				return actual.compareTo(expected) > 0;
			}
		};
		comparison(testResults, row, compare);
	}
	/** Check that the result of the action in the first part of the row is greater than
	 *  or equal to the expected value in the last cell of the row.
	 */
	public void greaterThanEquals(TestResults testResults, final RowOnParse row) throws Exception {
		Comparison compare = new Comparison() {
			@SuppressWarnings("unchecked")
			public boolean compares(Comparable actual, Comparable expected) {
				return actual.compareTo(expected) >= 0;
			}
		};
		comparison(testResults, row, compare);
	}
	@SuppressWarnings("unchecked")
	private void comparison(TestResults testResults, final RowOnParse row,
			Comparison compare) {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("DoTraverseIs");
		Cell specialCell = operatorCell(row);
		Cell expectedCell = row.last();
		try {
			ICalledMethodTarget target = findMethodFromRow222(row,0,less);
			Object result = target.invoke(row.rowTo(1,row.size()-2),testResults,true);
			if (result instanceof Comparable) {
				target.compare(expectedCell, (Comparable)result, testResults, compare);
			} else
				throw new FitLibraryException("Unable to compare, as not Comparable");
        } catch (IgnoredException e) {
            //
        } catch (InvocationTargetException e) {
        	Throwable embedded = e.getTargetException();
        	if (embedded instanceof FitLibraryShowException) {
        		specialCell.error(testResults);
        		row.error(testResults, e);
        	} else
        		expectedCell.exceptionMayBeExpected(false, e, testResults);
        } catch (Exception e) {
        	expectedCell.exceptionMayBeExpected(false, e, testResults);
        }
	}
	public interface Comparison {
		@SuppressWarnings("unchecked")
		boolean compares(Comparable actual, Comparable expected);
	}
	private Cell operatorCell(final RowOnParse row) {
		return row.cell(row.size()-2);
	}
	/** Check that the result of the action in the first part of the row, as a string, matches
	 *  the regular expression in the last cell of the row.
	 */
	public void matches(TestResults testResults, final RowOnParse row) throws Exception {
		try
		{
			int less = 3;
			if (row.size() < less)
				throw new MissingCellsException("DoTraverseMatches");
			ICalledMethodTarget target = findMethodFromRow222(row,0,less);
			Cell expectedCell = row.last();
			String result = target.invokeForSpecial(row.rowTo(1,row.size()-2),testResults,false,operatorCell(row)).toString();
			boolean matches = Pattern.compile(".*"+expectedCell.text(this)+".*",Pattern.DOTALL).matcher(result).matches();
			if (matches)
				expectedCell.pass(testResults);
			else
				expectedCell.fail(testResults, result,this);
		} catch (PatternSyntaxException e) {
			throw new FitLibraryException(e.getMessage());
		}
	}
	/** Check that the result of the action in the first part of the row, as a string, eventually matches
	 *  the regular expression in the last cell of the row.
	 */
	public void eventuallyMatches(TestResults testResults, final RowOnParse row) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("eventuallyMatches");
		ICalledMethodTarget target = findMethodFromRow222(row,0,less);
		Cell expectedCell = row.last();
		Pattern compile = Pattern.compile(".*"+expectedCell.text(this)+".*",Pattern.DOTALL);
		
		String result = "";
		long start = System.currentTimeMillis();
		int becomesTimeout = getTimeout(BECOMES_TIMEOUT);
		while (System.currentTimeMillis() - start < becomesTimeout ) {
			result = target.invokeForSpecial(row.rowTo(1,row.size()-2),testResults,false,operatorCell(row)).toString();
			boolean matches = compile.matcher(result).matches();
			if (matches) {
				expectedCell.pass(testResults);
				return;
			}
			try {
				Thread.sleep(Math.max(500, Math.min(100,becomesTimeout/10)));
			} catch (Exception e) {
				//
			}
		}
		expectedCell.fail(testResults, result,this);
	}
	/** Check that the result of the action in the first part of the row, as a string, does not match
	 *  the regular expression in the last cell of the row.
	 */
	public void doesNotMatch(TestResults testResults, final RowOnParse row) throws Exception {
		try
		{
			int less = 3;
			if (row.size() < less)
				throw new MissingCellsException("DoTraverseMatches");
			ICalledMethodTarget target = findMethodFromRow222(row,0,less);
			Cell expectedCell = row.last();
			String result = target.invokeForSpecial(row.rowTo(1,row.size()-2),testResults,false,operatorCell(row)).toString();
			if (!Pattern.compile(".*"+expectedCell.text(this)+".*",Pattern.DOTALL).matcher(result).matches())
				expectedCell.pass(testResults);
			else if (expectedCell.text(this).equals(result))
				expectedCell.fail(testResults);
			else
				expectedCell.fail(testResults,result,this);
		} catch (PatternSyntaxException e) {
			throw new FitLibraryException(e.getMessage());
		}
	}
	/** Check that the result of the action in the first part of the row, as a string, contains
	 *  the string in the last cell of the row.
	 */
	public void contains(TestResults testResults, final RowOnParse row) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("contains");
		ICalledMethodTarget target = findMethodFromRow222(row,0,less);
		Cell expectedCell = row.last();
		String result = target.invokeForSpecial(row.rowTo(1,row.size()-2),testResults,false,operatorCell(row)).toString();
		boolean matches = result.contains(expectedCell.text(this));
		if (matches)
			expectedCell.pass(testResults);
		else
			expectedCell.failWithStringEquals(testResults, result,this);
	}
	/** Check that the result of the action in the first part of the row, as a string, contains
	 *  the string in the last cell of the row.
	 */
	public void eventuallyContains(TestResults testResults, final RowOnParse row) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("contains");
		ICalledMethodTarget target = findMethodFromRow222(row,0,less);
		Cell expectedCell = row.last();
		String result = "";
		long start = System.currentTimeMillis();
		int becomesTimeout = getTimeout(BECOMES_TIMEOUT);
		while (System.currentTimeMillis() - start < becomesTimeout ) {
			result = target.invokeForSpecial(row.rowTo(1,row.size()-2),testResults,false,operatorCell(row)).toString();
			boolean matches = result.contains(expectedCell.text(this));
			if (matches) {
				expectedCell.pass(testResults);
				return;
			}
		}
		expectedCell.failWithStringEquals(testResults, result,this);
	}
	/** Check that the result of the action in the first part of the row, as a string, contains
	 *  the string in the last cell of the row.
	 */
	public void doesNotContain(TestResults testResults, final RowOnParse row) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("doesNoContain");
		ICalledMethodTarget target = findMethodFromRow222(row,0,less);
		Cell expectedCell = row.last();
		String result = target.invokeForSpecial(row.rowTo(1,row.size()-2),testResults,false,operatorCell(row)).toString();
		boolean matches = result.contains(expectedCell.text(this));
		if (!matches)
			expectedCell.pass(testResults);
		else
			expectedCell.fail(testResults, result,this);
	}
	/** Check that the result of the action in the first part of the row, as a string becomes equals
	 *  to the given value within the timeout period.
	 */
	public void becomes(TestResults testResults, final RowOnParse row) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("DoTraverseMatches");
		ICalledMethodTarget target = findMethodFromRow222(row,0,less);
		Cell expectedCell = row.last();
		RowOnParse actionPartOfRow = row.rowTo(1,row.size()-2);
		long start = System.currentTimeMillis();
		int becomesTimeout = getTimeout(BECOMES_TIMEOUT);
		while (System.currentTimeMillis() - start < becomesTimeout ) {
			Object result = target.invokeForSpecial(actionPartOfRow, testResults, false,operatorCell(row));
			if (target.getResultParser().matches(expectedCell, result, testResults))
				break;
			try {
				Thread.sleep(Math.min(100,becomesTimeout/10));
			} catch (Exception e) {
				//
			}
		}
		target.invokeAndCheckForSpecial(actionPartOfRow,expectedCell,testResults,row,operatorCell(row));
	}

	//------------------- Prefix Special Actions:
	/** Check that the result of the action in the rest of the row matches
	 *  the expected value in the last cell of the row.
	 */
	public TwoStageSpecial check(final Row row) throws Exception {
		return prefixSpecialAction.check(row);
	}
	public TwoStageSpecial reject(final Row row) throws Exception {
		return not(row);
	}
    /** Same as reject()
     * @param testResults 
     */
	public TwoStageSpecial not(final Row row) throws Exception {
		return prefixSpecialAction.not(row,NotSyle.PASSES_ON_EXCEPION);
	}
	public TwoStageSpecial notTrue(final Row row) throws Exception {
		return prefixSpecialAction.not(row,NotSyle.ERROR_ON_EXCEPION);
	}
	/** Add a cell containing the result of the action in the rest of the row.
     *  HTML is not altered, so it can be viewed directly.
     */
	public TwoStageSpecial show(final Row row) throws Exception {
		return prefixSpecialAction.show(row);
	}
	/** Adds the result of the action in the rest of the row to a folding area after the table.
     */
	public TwoStageSpecial showAfter(final Row row) throws Exception {
		return prefixSpecialAction.showAfter(row);
	}
	/** Adds the result of the action in the rest of the row to a folding area after the table.
     */
	public TwoStageSpecial showAfterAs(final Row row) throws Exception {
		return prefixSpecialAction.showAfterAs(row);
	}
	/** Add a cell containing the result of the action in the rest of the row.
     *  HTML is escaped so that the underlying layout text can be viewed.
     */
	public TwoStageSpecial showEscaped(final Row row) throws Exception {
		return prefixSpecialAction.showEscaped(row);
	}
	/** Log result to a file
	 */
	public TwoStageSpecial log(final Row row) throws Exception {
		return prefixSpecialAction.log(row);
	}
	/** Set the dynamic variable name to the result of the action, or to the string if there's no action.
	 */
	public TwoStageSpecial set(final Row row) throws Exception {
		return prefixSpecialAction.set(row);
	}
	/** Set the named FIT symbol to the result of the action, or to the string if there's no action.
	 */
	public TwoStageSpecial setSymbolNamed(final Row row) throws Exception {
		return prefixSpecialAction.setSymbolNamed(row);
	}
	/** Add a cell containing the result of the rest of the row,
     *  shown as a Dot graphic.
	 * @param testResults 
     */
	public void showDot(RowOnParse row, TestResults testResults) throws Exception {
		Parser adapter = new GraphicParser(new NonGenericTyped(ObjectDotGraphic.class));
		try {
		    Object result = callMethodInRow(row,testResults, true,row.cell(0));
		    row.addCell(adapter.show(new ObjectDotGraphic(result)));
		} catch (IgnoredException e) { // No result, so ignore
		}
	}
	/** Checks that the action in the rest of the row succeeds.
     *  o If a boolean is returned, it must be true.
     *  o For other result types, no exception should be thrown.
     *  It's no longer needed, because the same result can now be achieved with a boolean method.
	 * @param testResults 
     */
	public TwoStageSpecial ensure(final Row row) throws Exception {
		return prefixSpecialAction.ensure(row);
	}

	/** The rest of the row is ignored. 
     */
	@SuppressWarnings("unused")
	public void note(RowOnParse row, TestResults testResults) throws Exception {
		//		Nothing to do
	}
	/** To allow for example storytests in user guide to pass overall, even if they have failures within them. */
	public void expectedTestResults(RowOnParse row, TestResults testResults) throws Exception {
		if (testResults.matches(row.text(1,this),row.text(3,this),row.text(5,this),row.text(7,this))) {
			testResults.clear();
			row.cell(0).pass(testResults);
		} else {
			String results = testResults.toString();
			testResults.clear();
			row.cell(0).fail(testResults,results,this);
		}
	}
	public Object oo(final RowOnParse row, TestResults testResults) throws Exception {
		if (row.size() < 3)
			throw new MissingCellsException("DoTraverseOO");
		String object = row.text(1,this);
		Object className = getDynamicVariable(object+".class");
		if (className == null || "".equals(className))
			className = object; // then use the object name as a class name
		RowOnParse macroRow = row.rowFrom(2);
		TypedObject typedObject = new DefinedActionCaller(object,className.toString(),macroRow,this).run(row, testResults);
		return typedObject.getSubject();
	}
	/** Don't mind that the action succeeds or not, just as long as it's not a FitLibraryException (such as action unknown) 
     */
	public void optionally(RowOnParse row, TestResults testResults) throws Exception {
		try {
		    Object result = callMethodInRow(row,testResults, true,row.cell(0));
		    if (result instanceof Boolean && !((Boolean)result).booleanValue()) {
		    	row.addCell("false").shown();
		    	getRuntimeContext().getDefinedActionCallManager().addShow(row);
		    }
		} catch (FitLibraryException e) {
			row.cell(0).error(testResults,e);
		} catch (Exception e) {
			row.addCell(PlugBoard.exceptionHandling.exceptionMessage(e)).shown();
			getRuntimeContext().getDefinedActionCallManager().addShow(row);
		}
		row.cell(0).pass(testResults);
	}
	/*
	 * |''add named''|name|...action or fixture|
	 */
	public void addNamed(RowOnParse row, TestResults testResults) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("addNamed");
		TypedObject typedObject = interpretRow(row.rowFrom(2), testResults);
		getRuntimeContext().getTableEvaluator().addNamedObject(row.text(1,this),typedObject,row,testResults);
	}
	@Override
	public FitHandler fitHandler() {
		return getFitHandler();
	}
	private GlobalScope global() {
		return getRuntimeContext().getGlobal();
	}
}
