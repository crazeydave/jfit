package fitlibrary.batch;

import java.io.IOException;
import java.util.concurrent.Executor;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.batch.resultsOut.ParallelFolderTestResultRepository;
import fitlibrary.batch.trinidad.TestResult;
import fitlibrary.batch.trinidad.TestResultRepository;

@RunWith(JMock.class)
public class TestParallelFolderTestResultRepository {
	final Mockery context = new JUnit4Mockery();
	final TestResultRepository testResults = context.mock(TestResultRepository.class);
	final TestResult result1 = context.mock(TestResult.class);
	final TestResult result2 = context.mock(TestResult.class,"TestResult2");
	Executor executor = new ThreadPerTaskExecutor();
	final ParallelFolderTestResultRepository testResultRepository = new ParallelFolderTestResultRepository(testResults,executor);

	@Test public void noTestResults() throws InterruptedException {
		testResultRepository.closeAndWaitForCompletion();
	}
	@Test public void oneTestResult() throws IOException, InterruptedException {
		context.checking(new Expectations() {{
	        one(testResults).recordTestResult(result1);
	    }});

		testResultRepository.recordTestResult(result1);
		testResultRepository.closeAndWaitForCompletion();
	}
	@Test public void twoTestResults() throws IOException, InterruptedException {
		context.checking(new Expectations() {{
	        one(testResults).recordTestResult(result1);
	        one(testResults).recordTestResult(result2);
	    }});

		testResultRepository.recordTestResult(result1);
		testResultRepository.recordTestResult(result2);
		testResultRepository.closeAndWaitForCompletion();
	}
	
	 static class ThreadPerTaskExecutor implements Executor {
	     @Override
		public void execute(Runnable r) {
	         new Thread(r).start();
	     }
	 }
}
