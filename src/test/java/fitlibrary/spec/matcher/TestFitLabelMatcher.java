package fitlibrary.spec.matcher;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.spec.PipeLine;
import fitlibrary.spec.matcher.FitLabelMatcher;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JMock.class)
public class TestFitLabelMatcher {
	final Mockery context = new Mockery();
	final PipeLine pipeline = context.mock(PipeLine.class);
	final FitLabelMatcher matcher = new FitLabelMatcher(pipeline);

	@Test
	public void noAffectWhenNoFitLabel() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("ab", "ab"); will(returnValue(true));
		}});
		matcher.match("ab", "ab");
	}
	@Test
	public void failsWhenFitLabelOnlyInActual() {
		assertThat(matcher.match("ab"+"<span class=\"fit_label\">"+"ABC"+"</span>"+"cd", "ab"+"cd"),is(false));
	}
	@Test
	public void failsWhenFitLabelOnlyInExpected() {
		assertThat(matcher.match("ab"+"cd","ab"+"<span class=\"fit_label\">"+"ABC"+"</span>"+"cd"),is(false));
	}
	@Test
	public void whenFitLabelSame() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("ab", "ab"); will(returnValue(true));
			oneOf(pipeline).match("cd", "cd"); will(returnValue(true));
		}});
		assertThat(matcher.match(
				"ab"+"<span class=\"fit_label\">"+"ABC"+"</span>"+"cd",
				"ab"+"<span class=\"fit_label\">"+"ABC"+"</span>"+"cd"
		),is(true));
	}
	@Test
	public void whenActualFitLabelStartsTheSame() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("ab", "ab"); will(returnValue(true));
			oneOf(pipeline).match("cd", "cd"); will(returnValue(true));
		}});
		assertThat(matcher.match(
				"ab"+"<span class=\"fit_label\">"+"ABC and more"+"</span>"+"cd",
				"ab"+"<span class=\"fit_label\">"+"ABC"+"</span>"+"cd"
		),is(true));
	}
	@Test
	public void whenActualFitLabelDoesNotStartTheSame() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("ab", "ab"); will(returnValue(true));
		}});
		assertThat(matcher.match(
				"ab"+"<span class=\"fit_label\">"+"DIFFERENT"+"</span>"+"cd",
				"ab"+"<span class=\"fit_label\">"+"ABC"+"</span>"+"cd"
		),is(false));
	}
}
