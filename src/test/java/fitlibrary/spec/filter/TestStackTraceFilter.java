package fitlibrary.spec.filter;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import fitlibrary.spec.PipeLine;

@RunWith(JMock.class)
public class TestStackTraceFilter {
	final Mockery context = new Mockery();
	final PipeLine pipeline = context.mock(PipeLine.class);
	final StackTraceFilter filter = new StackTraceFilter(pipeline);

	@Test
	public void noAffectWhenBothEmpty() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("", ""); will(returnValue(true));
		}});
		filter.match("", "");
	}
	@Test
	public void noAffectWhenNoStackTrace() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("ab", "ab"); will(returnValue(true));
		}});
		filter.match("ab", "ab");
	}
	@Test
	public void whenStackTraceOnlyInActual() {
		assertThat(filter.match(
				"ab"+"class=\"fit_stacktrace\">"+"ABC",
				"ab"+"cd"
		),is(false));
	}
	@Test
	public void whenStackTraceOnlyInExpected() {
		assertThat(filter.match(
				"ab"+"cd",
				"ab"+"class=\"fit_stacktrace\">"+"ABC"
		),is(false));
	}
	@Test
	public void whenStackTraceInBoth() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("ab", "ab"); will(returnValue(true));
		}});
		assertThat(filter.match(
				"ab"+"class=\"fit_stacktrace\">"+"ABC",
				"ab"+"class=\"fit_stacktrace\">"+"OTHER STUFF"
		),is(true));
	}
}
