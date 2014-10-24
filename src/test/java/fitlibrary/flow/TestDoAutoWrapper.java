/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import fit.ColumnFixture;
import fitlibrary.DoFixture;
import fitlibrary.collection.array.ArrayTraverse;
import fitlibrary.collection.list.ListTraverse;
import fitlibrary.collection.map.MapTraverse;
import fitlibrary.collection.set.SetTraverse;
import fitlibrary.object.DomainFixtured;
import fitlibrary.parser.ParserTestCase;
import fitlibrary.traverse.workflow.DoTraverse;
import fitlibrary.typed.TypedObject;
import fitlibraryGeneric.typed.GenericTypedObject;

public class TestDoAutoWrapper {
	DoFixture doFixture = ParserTestCase.evaluatorWithRuntime();
	DoTraverse doTraverse = ParserTestCase.evaluatorWithRuntime(new DoTraverse());
	IDoAutoWrapper autoWrapper = new DoAutoWrapper(doFixture);
	
	@Test
	public void nullValueInTypedResultIsNotAutoWrappedButReturnsAsNull() {
		verify(null,null);
	}
	@Test
	public void stringIsNotAutoWrapped() {
		verify("a","a");
	}
	@Test
	public void stringBufferIsNotAutoWrapped() {
		Object value = new StringBuffer("a");
		verify(value,value);
	}
	@Test
	public void primitiveLikeIsNotAutoWrapped() {
		verify('a','a');
		verify(true,true);
		verify(1L,1L);
		verify(1,1);
		verify(1.2,1.2);
		verify(1.2F,1.2F);
		verify(new BigDecimal(1.1),new BigDecimal(1.1));
		verify(new BigInteger("1"),new BigInteger("1"));
	}
	@Test
	public void fitFixtureIsNotAutoWrapped() {
		Object fixture = new ColumnFixture();
		verify(fixture,fixture);
	}
	@Test
	public void sameDoFixtureIsNotAutoWrapped() {
		verify(doFixture,doFixture);
	}
	@Test
	public void doTraverseIsNotAutoWrappedButHasOuterContextSet() {
		verify(doTraverse,doTraverse);
	}
	@Test
	public void objectWithParseMethodIsNotAutoWrapped() {
		Object withParse = new WithParse();
		verify(withParse ,withParse);
	}
	@Test
	public void domainFixturedIsNotAutoWrapped() {
		Object domainFixtured = new DomainFixtured(){
			//
		};
		verify(domainFixtured ,domainFixtured);
	}

	@Test
	public void mapIsAutoWrappedWithMapTraverse() {
		verifyClass(new HashMap<String,String>(),MapTraverse.class);
	}
	@Test
	public void arrayIsAutoWrappedWithArrayTraverse() {
		verifyClass(new String[]{"a"},ArrayTraverse.class);
	}
	@Test
	public void setIsAutoWrappedWithArrayTraverse() {
		verifyClass(new HashSet<String>(),SetTraverse.class);
	}
	@Test
	public void listIsAutoWrappedWithArrayTraverse() {
		verifyClass(new ArrayList<String>(),ListTraverse.class);
	}
	@Test
	public void otherIsAutoWrappedWithDoTraverse() {
		verifyClass(new WithOutParse(),DoTraverse.class);
	}
	
	private void verify(Object value, Object expected) {
		assertThat(autoWrapper.wrap(new GenericTypedObject(value)),is((TypedObject)new GenericTypedObject(expected)));
	}
	private void verifyClass(Object value, Class<?> expectedType) {
		TypedObject actual = autoWrapper.wrap(new GenericTypedObject(value));
		assertThat(actual.getSubject(),instanceOf(expectedType));
	}
	static class WithParse {
		public static Object parse(String s) {
			return s;
		}
	}
	static class WithOutParse {
		//
	}
}
