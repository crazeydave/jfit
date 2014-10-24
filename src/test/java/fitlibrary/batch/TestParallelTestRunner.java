package fitlibrary.batch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fit.Counts;
import fitlibrary.batch.fitnesseIn.ParallelFitNesseRepository;
import fitlibrary.batch.fitnesseIn.ParallelTestRepository;
import fitlibrary.batch.resultsOut.ParallelTestResultRepository;
import fitlibrary.batch.resultsOut.SuiteResult;
import fitlibrary.batch.testRun.ParallelTestRunner;
import fitlibrary.batch.trinidad.InMemoryTestImpl;
import fitlibrary.batch.trinidad.TestDescriptor;
import fitlibrary.batch.trinidad.TestEngine;
import fitlibrary.batch.trinidad.TestResult;

@RunWith(JMock.class)
public class TestParallelTestRunner {
	final Mockery context = new JUnit4Mockery();
	final Counts countsZero = new Counts(0,0,0,0);
	final Counts countsOnes = new Counts(1,1,1,1);
	final ParallelTestRepository repository = context.mock(ParallelTestRepository.class);
	final TestEngine testEngine = context.mock(TestEngine.class);
	final ParallelTestResultRepository resultRepository = context.mock(ParallelTestResultRepository.class);
	final SuiteResult suiteResult = context.mock(SuiteResult.class);
	final BlockingQueue<TestDescriptor> queue = new LinkedBlockingQueue<TestDescriptor>();
	
	final InMemoryTestImpl test1 = new InMemoryTestImpl("TestOne","One Contents");
	final TestResult result1 = context.mock(TestResult.class);

	@Before public void before() throws IOException, InterruptedException {
		context.checking(new Expectations() {{
	        one(repository).prepareResultRepository(resultRepository);
	        one(repository).getSuite("suite"); will(returnValue(queue));
	        one(resultRepository).recordTestResult(suiteResult);
	        one(resultRepository).closeAndWaitForCompletion();
	    }});
	}
	@Test public void hasNoTests() throws IOException, InterruptedException {
		queue.offer(ParallelFitNesseRepository.TEST_SENTINEL);
		
		context.checking(new Expectations() {{
	        one(suiteResult).getCounts(); will(returnValue(countsZero));
	    }});

		ParallelTestRunner runner = new ParallelTestRunner(repository,testEngine,resultRepository);
		assertThat(runner.runSuite("suite", suiteResult),equalTo(countsZero));
	}
	@Test public void hasOneTest() throws IOException, InterruptedException {
		queue.offer(test1);
		queue.offer(ParallelFitNesseRepository.TEST_SENTINEL);
		
		context.checking(new Expectations() {{
	        one(testEngine).runTest(test1); will(returnValue(result1));
	        one(suiteResult).append(result1);
	        one(resultRepository).recordTestResult(result1);
	        one(suiteResult).getCounts(); will(returnValue(countsOnes));
	    }});

		ParallelTestRunner runner = new ParallelTestRunner(repository,testEngine,resultRepository);
		assertThat(runner.runSuite("suite", suiteResult),equalTo(countsOnes));
	}
	@Test public void hasTwoTests() throws IOException, InterruptedException {
		final TestDescriptor test2 = new InMemoryTestImpl("TestTwo","Two Contents");
		final TestResult result2 = context.mock(TestResult.class,"testResult2");

		queue.offer(test1);
		queue.offer(test2);
		queue.offer(ParallelFitNesseRepository.TEST_SENTINEL);
		
		context.checking(new Expectations() {{
	        one(testEngine).runTest(test1); will(returnValue(result1));
	        one(suiteResult).append(result1);
	        one(resultRepository).recordTestResult(result1);

	        one(testEngine).runTest(test2); will(returnValue(result2));
	        one(suiteResult).append(result2);
	        one(resultRepository).recordTestResult(result2);

	        one(suiteResult).getCounts(); will(returnValue(countsOnes));
	    }});

		ParallelTestRunner runner = new ParallelTestRunner(repository,testEngine,resultRepository);
		assertThat(runner.runSuite("suite", suiteResult),equalTo(countsOnes));
	}
}
