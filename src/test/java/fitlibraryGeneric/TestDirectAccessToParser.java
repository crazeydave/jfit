/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibraryGeneric;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fitlibrary.DoFixture;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.exception.parse.BadNumberException;
import fitlibrary.parser.ParserTestCase;
import fitlibrary.parser.lookup.ParserSelectorForType;
import fitlibrary.traverse.workflow.DoEvaluator;
import fitlibraryGeneric.generic.LocalParameterizedType;

public class TestDirectAccessToParser {
    private DoEvaluator doFixture = ParserTestCase.evaluatorWithRuntime(new MyFixture());

    @Test
	public void testParseInt() throws Exception {
		assertThat(ParserSelectorForType.evaluate(doFixture, int.class, "3"),is((Object)3));
	}
	@Test(expected=BadNumberException.class)
	public void testParseIntFails() throws Exception {
		ParserSelectorForType.evaluate(doFixture, int.class, "three");
	}
	@Test
	public void testParseWithFinder() throws Exception {
		assertThat(ParserSelectorForType.evaluate(doFixture, X.class, "3"),is((Object)new X("3")));
	}
	@Test(expected=FitLibraryException.class)
	public void testParseWithOutFinder() throws Exception {
		ParserSelectorForType.evaluate(doFixture, Y.class, "3");
	}
	@Test
	public void testParseWithEnumFinder() throws Exception {
		assertThat(ParserSelectorForType.evaluate(doFixture, En.class, "a"),is((Object)En.A));
	}
	@Test
	public void testParseWithGenericFinder() throws Exception {
		LocalParameterizedType type = new LocalParameterizedType(TestDirectAccessToParser.class, Gen.class, Integer.class);
		assertThat(ParserSelectorForType.evaluate(doFixture, type, "3"),is((Object)new Gen<Integer>(3)));
	}
	@Test
	public void testParseWithGenericEnumFinder() throws Exception {
		LocalParameterizedType type = new LocalParameterizedType(TestDirectAccessToParser.class, Gen.class, En.class);
		assertThat(ParserSelectorForType.evaluate(doFixture, type, "A"),is((Object)new Gen<En>(En.A)));
	}
	@Test
	public void testParseWithGenericListEnumFinder() throws Exception {
		LocalParameterizedType innerType = new LocalParameterizedType(TestDirectAccessToParser.class, List.class, En.class);
		LocalParameterizedType type = new LocalParameterizedType(TestDirectAccessToParser.class, Gen.class, innerType);
		List<En> expectedList = new ArrayList<En>();
		expectedList.add(En.A);
		expectedList.add(En.B);
		assertThat(ParserSelectorForType.evaluate(doFixture, type, "a, b"),is((Object)new Gen<List<En>>(expectedList)));
	}
	
	public static class MyFixture extends DoFixture {
		public X findX(String s) {
			return new X(s);
		}
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Gen findGen(String key, Type type) throws Exception {
			Type innerType = ((ParameterizedType)type).getActualTypeArguments()[0];
			if (innerType == Integer.class)
				return new Gen<Integer>(Integer.valueOf(key));
			if (innerType == En.class)
				return new Gen<En>((En) ParserSelectorForType.evaluate(this, En.class, key));
			if (((ParameterizedType)innerType).getRawType() == List.class && 
					((ParameterizedType)innerType).getActualTypeArguments()[0] == En.class)
				return new Gen<List<En>>((List<En>) ParserSelectorForType.evaluate(this, innerType, key));
			throw new RuntimeException();
		}
	}
	public static class X {
		private String s;

		public X(String s) {
			this.s = s;
		}
		@Override
		public boolean equals(Object obj) {
			return s.equals(((X)obj).s);
		}
		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}
	public static class Y {
		//
	}
	public static class Gen<T> {
		private T t;

		public Gen(T t) {
			this.t = t;
		}
		@Override
		public boolean equals(Object obj) {
			return t.equals(((Gen<?>)obj).t);
		}
		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}
	public static enum En {
		A, B
	}
}
