/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.parser;

import junit.framework.TestCase;
import fitlibrary.DoFixture;
import fitlibrary.flow.GlobalActionScope;
import fitlibrary.flow.ScopeStack;
import fitlibrary.runtime.RuntimeContextContainer;
import fitlibrary.traverse.workflow.DoTraverse;
import fitlibrary.traverse.workflow.FlowEvaluator;
import fitlibrary.typed.TypedObject;
import fitlibraryGeneric.typed.GenericTypedObject;

public abstract class ParserTestCase extends TestCase {
	static GlobalActionScope global = new GlobalActionScope();
	static TypedObject globalTO = new GenericTypedObject(global);
	
	public static DoFixture evaluatorWithRuntime() {
		return evaluatorWithRuntime(new DoFixture());
	}
	public static DoFixture evaluatorWithRuntime(DoFixture evaluator) {
		evaluator.setRuntimeContext(new RuntimeContextContainer(new ScopeStack((FlowEvaluator)evaluator.traverse(),globalTO),global));
		return evaluator;
	}
	public static DoTraverse evaluatorWithRuntime(DoTraverse evaluator) {
		evaluator.setRuntimeContext(new RuntimeContextContainer(new ScopeStack(evaluator,globalTO),global));
		return evaluator;
	}
}
