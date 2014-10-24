package fitlibrary.spec.matcher;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.spec.PipeLine;
import fitlibrary.spec.matcher.ImageSrcMatcher;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JMock.class)
public class TestImageSrcMatcher {
	final Mockery context = new Mockery();
	final PipeLine pipeline = context.mock(PipeLine.class);
	final ImageSrcMatcher matcher = new ImageSrcMatcher(pipeline);

	@Test
	public void noAffectWhenNoFitLabel() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("ab", "ab"); will(returnValue(true));
		}});
		matcher.match("ab", "ab");
	}
	@Test
	public void failsWhenImageOnlyInActual() {
		assertThat(matcher.match(
				"ab"+"<img src=\""+"files/image.jpg"+">"+"cd", 
				"ab"+"cd"
		),is(false));
	}
	@Test
	public void failsWhenImageOnlyInExpected() {
		assertThat(matcher.match(
				"ab"+"cd",
				"ab"+"<img src=\""+"files/image.jpg"+">"+"cd"
		),is(false));
	}
	@Test
	public void passesWhenFitLabelSame() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("ab", "ab"); will(returnValue(true));
			oneOf(pipeline).match("cd", "cd"); will(returnValue(true));
		}});
		assertThat(matcher.match(
				"ab"+"<img src=\""+"files/image.jpg"+">"+"cd",
				"ab"+"<img src=\""+"files/image.jpg"+">"+"cd"
		),is(true));
	}
	@Test
	public void whenActualImageEndsTheSame() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("ab", "ab"); will(returnValue(true));
			oneOf(pipeline).match("cd", "cd"); will(returnValue(true));
		}});
		assertThat(matcher.match(
				"ab"+"<img src=\""+"OTHER-STUFF/files/image.jpg"+">"+"cd",
				"ab"+"<img src=\""+"files/image.jpg"+">"+"cd"
		),is(true));
	}
	@Test
	public void whenActualFitLabelDoesNotEndTheSame() {
		context.checking(new Expectations() {{
			oneOf(pipeline).match("ab", "ab"); will(returnValue(true));
		}});
		assertThat(matcher.match(
				"ab"+"<img src=\""+"OTHER-STUFF/files/image.gif"+">"+"cd",
				"ab"+"<img src=\""+"files/image.jpg"+">"+"cd"
		),is(false));
	}
}
