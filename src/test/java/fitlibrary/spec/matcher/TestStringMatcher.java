package fitlibrary.spec.matcher;

import org.junit.Test;

import fitlibrary.spec.matcher.StringMatcher;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestStringMatcher {
	final StringMatcher matcher = new StringMatcher();

	@Test
	public void emptyStrings() {
		assertThat(matcher.match("",""),is(true));
	}
	@Test
	public void anyStrings() {
		assertThat(matcher.match("abc","abc"),is(true));
		assertThat(matcher.match("Abc","abc"),is(false));
		assertThat(matcher.match("abc","Abc"),is(false));
	}
	@Test
	public void nonBreaking() {
		assertThat(matcher.match("","&nbsp;"),is(true));
		assertThat(matcher.match("&nbsp;",""),is(true));
		assertThat(matcher.match("&nbsp;","&nbsp;"),is(true));
	}
}
