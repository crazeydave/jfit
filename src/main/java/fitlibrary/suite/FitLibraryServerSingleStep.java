package fitlibrary.suite;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import fit.FitProtocol;
import fit.FitServerBridge;
import fit.exception.FitParseException;
import fitlibrary.flow.actor.DoFlowActor;
import fitlibrary.log.ConfigureLoggingThroughFiles;
import fitlibrary.log.FitLibraryLogger;
import fitlibrary.runResults.TestResults;
import fitlibrary.runResults.TestResultsOnCounts;
import fitlibrary.suite.Reporter.ReportAction;
import fitlibrary.table.TableFactory;
import fitlibrary.table.Tables;

public class FitLibraryServerSingleStep extends FitServerBridge {
	static Logger logger = FitLibraryLogger.getLogger(FitLibraryServer.class);
	private BatchFitLibrarySingleStep batching = new BatchFitLibrarySingleStep();
	private final BlockingQueue<ReportAction> reportQueue = new LinkedBlockingQueue<ReportAction>();
	private final DoFlowActor actor = batching.actor(reportQueue, suiteTestResults);
	private final CountDownLatch endGate = new CountDownLatch(1);

	@Override
	public TestResults doTables(String html) {
		try {
			return doTables(TableFactory.tables(html));
		} catch (FitParseException e) {
			e.printStackTrace();
		}
		return new TestResultsOnCounts();
	}

	public TestResults doTables(Tables tables) {
		for (int t = 0; t < tables.size(); t++)
			actor.addTable(tables.at(t));
		return suiteTestResults;
	}

	@Override
	public void exit() throws Exception {
		batching.exit();
		super.exit();
	}

	@Override
	protected void usage() {
		logger.trace("usage: java fitlibrary.suite.FitLibraryServerSingleStep [-v] host port socketTicket");
		System.exit(-1);
	}

	@Override
	public void process() {
		logger.trace("Ready to receive tables from ZiBreve");
		new Thread(new Reporter(reportQueue,this,endGate,suiteTestResults)).start();
		try {
			while (true) {
				logger.trace("Reading table size...");
				int size = FitProtocol.readSize(socketReader);
				if (size == 0)
					break;
				logger.trace("Received table of size " + size + " from ZiBreve");
				try {
					String document = FitProtocol.readDocument(socketReader, size);
					doTables(document);
					logger.trace("Finished running table");
				} catch (FitParseException e) {
					exception(e);
				}
			}
			logger.trace("No more tables to receive from ZiBreve");
			actor.endStorytest();
			endGate.await();
		} catch (Exception e) {
			exception(e);
		}
	}

	public static void main(String[] args) {
		ConfigureLoggingThroughFiles.configure();
		FitServerBridge fitServer = new FitLibraryServerSingleStep();
		try {
			logger.trace(new Date());
			fitServer.run(args);
			logger.trace(("Exit: " + fitServer.exitCode()));
			if (fitServer.isExit())
				System.exit(fitServer.exitCode());
		} catch (Exception e) {
			fitServer.printExceptionDetails(e);
		}
	}
}
