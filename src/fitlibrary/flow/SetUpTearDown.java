/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import java.lang.reflect.Method;

import fitlibrary.flow.SetUpTearDownReferenceCounter.MethodCaller;
import fitlibrary.table.Row;
import fitlibrary.table.RowOnParse;
import fitlibrary.table.TableFactory;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.TestResults;

public class SetUpTearDown {
	private final SetUpTearDownReferenceCounter referenceCounter = new SetUpTearDownReferenceCounter();

	public void callSetUpSutChain(Object sutInitially, final Row row, final TestResults testResults) {
		Object sut = sutInitially;
		if (sut instanceof TypedObject)
			sut = ((TypedObject)sut).getSubject();
		referenceCounter.callSetUpOnNewReferences(sut, methodCaller(row, testResults));
	}
	public void callTearDownSutChain(Object sut, Row row, TestResults testResults) {
		referenceCounter.callTearDownOnReferencesThatAreCountedDown(sut, methodCaller(row, testResults));
	}
	public void callSuiteSetUp(Object suiteFixture, Row row, TestResults testResults) {
		callMethod(suiteFixture, "suiteSetUp", row,testResults);
	}
	public void callSuiteTearDown(Object suiteFixture, TestResults testResults) {
		callMethod(suiteFixture,"suiteTearDown",TableFactory.row("a"),testResults);
	}
	private MethodCaller methodCaller(final  Row row, final TestResults testResults) {
		return new MethodCaller(){
			public void setUp(Object object) {
				callMethod(object,"setUp",row,testResults);
			}
			public void tearDown(Object object) {
				callMethod(object,"tearDown",row,testResults);
			}
		};
	}
	protected void callMethod(Object object, String methodName, Row row, TestResults testResults) {
		try {
			Method method = object.getClass().getMethod(methodName, new Class[]{});
			method.invoke(object, new Object[]{});
		} catch (NoSuchMethodException e) {
			//
		} catch (Exception e) {
			row.error(testResults, e);
		}
	}
}
