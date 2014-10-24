package fitlibrary.utility;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class TestHtmlUtils {
	@Test
	public void escapeHtml() {
		assertThat(HtmlUtils.escapeHtml("<ab;>"),is("&lt;ab;&gt;"));
	}
	@Test
	public void testException() throws Exception {
		String exception = "Problems\n" +
                         "at org.hsqldb.jdbc.Util.throwError(Unknown Source)\n" +
                         "at org.hsqldb.jdbc.jdbcPreparedStatement.<init>(Unknown Source)\n" +
                         "at org.hsqldb.jdbc.jdbcConnection.prepareStatement(Unknown Source)\n" +
                         "at org.hibernate.jdbc.AbstractBatcher.getPreparedStatement(AbstractBatcher.java:497)\n" +
                         "[wrapped] org.hibernate.exception.SQLGrammarException: could not execute query\n" +
                         "at org.springframework.orm.hibernate3.HibernateTemplate$30.doInHibernate(HibernateTemplate.java:875)\n" +
                         "at org.springframework.orm.hibernate3.HibernateTemplate.execute(HibernateTemplate.java:373)\n" +
                         "[wrapped] org.springframework.dao.InvalidDataAccessResourceUsageException: could not execute query; nested exception is org.hibernate.exception.SQLGrammarException: could not execute query\n" +
                         "at org.springframework.orm.hibernate3.SessionFactoryUtils.convertHibernateAccessException(SessionFactoryUtils.java:613)\n" +
                         "at org.springframework.orm.hibernate3.HibernateAccessor.convertHibernateAccessException(HibernateAccessor.java:412)\n" +
                         "at org.springframework.orm.hibernate3.HibernateTemplate.execute(HibernateTemplate.java:378)\n" +
                         "at org.springframework.orm.hibernate3.HibernateTemplate.find(HibernateTemplate.java:866)\n" +
                         "at org.springframework.orm.hibernate3.HibernateTemplate.find(HibernateTemplate.java:858)\n" +
                         "[wrapped] Caching problem encountered\n" +
                         "at com.sabre.liberty.greenbeans.pnr.PnrPersistenceService.retrieve(PnrPersistenceService.java:88)\n" +
                         "at com.sabre.liberty.greenbeans.pnr.PnrPersistenceController.retrievePnrCommon(PnrPersistenceController.java:49)\n" +
                         "at com.sabre.liberty.greenbeans.pnr.PnrPersistenceController.retrievePnrCommon(PnrPersistenceController.java:45)\n" +
                         "at com.sabre.liberty.greenbeans.pnr.PnrPersistenceController.retrieveAndDisplay(PnrPersistenceController.java:30)\n" +
                         "at com.sabre.liberty.greenbeans.pnr.PnrRetrieveController.retrievePnr(PnrRetrieveController.java:23)\n" +
                         "at com.sabre.liberty.greenbeans.pnr.PnrRetrieveController$$FastClassByCGLIB$$1e8c51cf.invoke(<generated>)\n" +
                         "at net.sf.cglib.proxy.MethodProxy.invoke(MethodProxy.java:163)\n" +
                         "at org.springframework.aop.framework.Cglib2AopProxy$CglibMethodInvocation.invokeJoinpoint(Cglib2AopProxy.java:700)";

      String expectedOutput="<div class=fit_stacktrace>" +
                            "<b >### Problems ###</b><br />" +
                            "at org.hsqldb.jdbc.Util.throwError(Unknown Source)<br />" +
                            "at org.hsqldb.jdbc.jdbcPreparedStatement.&lt;init>(Unknown Source)<br />" +
                            "at org.hsqldb.jdbc.jdbcConnection.prepareStatement(Unknown Source)<br />" +
                            "at org.hibernate.jdbc.AbstractBatcher.getPreparedStatement(AbstractBatcher.java:497)<br />" +
                            "<b >### org.hibernate.exception.SQLGrammarException: could not execute query ###</b><br />" +
                            "at org.springframework.orm.hibernate3.HibernateTemplate$30.doInHibernate(HibernateTemplate.java:875)<br />" +
                            "at org.springframework.orm.hibernate3.HibernateTemplate.execute(HibernateTemplate.java:373)<br />" +
                            "<b >### org.springframework.dao.InvalidDataAccessResourceUsageException: could not execute query; nested exception is org.hibernate.exception.SQLGrammarException: could not execute query ###</b><br />" +
                            "at org.springframework.orm.hibernate3.SessionFactoryUtils.convertHibernateAccessException(SessionFactoryUtils.java:613)<br />" +
                            "at org.springframework.orm.hibernate3.HibernateAccessor.convertHibernateAccessException(HibernateAccessor.java:412)<br />" +
                            "at org.springframework.orm.hibernate3.HibernateTemplate.execute(HibernateTemplate.java:378)<br />" +
                            "at org.springframework.orm.hibernate3.HibernateTemplate.find(HibernateTemplate.java:866)<br />" +
                            "at org.springframework.orm.hibernate3.HibernateTemplate.find(HibernateTemplate.java:858)<br />" +
                            "<b >### Caching problem encountered ###</b><br />" +
                            "at com.sabre.liberty.greenbeans.pnr.PnrPersistenceService.retrieve(PnrPersistenceService.java:88)<br />" +
                            "at com.sabre.liberty.greenbeans.pnr.PnrPersistenceController.retrievePnrCommon(PnrPersistenceController.java:49)<br />" +
                            "at com.sabre.liberty.greenbeans.pnr.PnrPersistenceController.retrievePnrCommon(PnrPersistenceController.java:45)<br />" +
                            "at com.sabre.liberty.greenbeans.pnr.PnrPersistenceController.retrieveAndDisplay(PnrPersistenceController.java:30)<br />" +
                            "at com.sabre.liberty.greenbeans.pnr.PnrRetrieveController.retrievePnr(PnrRetrieveController.java:23)<br />" +
                            "at com.sabre.liberty.greenbeans.pnr.PnrRetrieveController$$FastClassByCGLIB$$1e8c51cf.invoke(&lt;generated>)<br />" +
                            "at net.sf.cglib.proxy.MethodProxy.invoke(MethodProxy.java:163)<br />" +
                            "at org.springframework.aop.framework.Cglib2AopProxy$CglibMethodInvocation.invokeJoinpoint(Cglib2AopProxy.java:700)" +
                            "</div>";

      assertThat(HtmlUtils.exception(exception),is(expectedOutput));
   }
}
