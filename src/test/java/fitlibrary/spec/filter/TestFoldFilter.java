package fitlibrary.spec.filter;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.spec.PipeLine;
import fitlibrary.spec.filter.FoldFilter;

@RunWith(JMock.class)
public class TestFoldFilter {
	final Mockery context = new Mockery();
	final PipeLine pipeline = context.mock(PipeLine.class);
	final FoldFilter filter = new FoldFilter(pipeline);

	@Test
	public void noAffectWhenBothEmpty() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("", ""); will(returnValue(true));
		}});
		filter.match("", "");
	}
	@Test
	public void noAffectWhenNoFold() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("ab", "ab"); will(returnValue(true));
		}});
		filter.match("ab", "ab");
	}
	@Test
	public void whenFoldInActual() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("abcd", "abcd"); will(returnValue(true));
		}});
		filter.match("ab"+"<div class=\"included\">"+"ABC"+"</div></div>"+"cd", "ab"+"cd");
	}
	@Test
	public void whenFoldInExpected() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("abcd", "abcd"); will(returnValue(true));
		}});
		filter.match("ab"+"cd","ab"+"<div class=\"included\">"+"ABC"+"</div></div>"+"cd");
	}
	@Test
	public void whenFoldInBoth() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("abcd", "abcd"); will(returnValue(true));
		}});
		String text = "ab"+"<div class=\"included\">"+"ABC"+"</div></div>"+"cd";
		filter.match(text,text);
	}
}
