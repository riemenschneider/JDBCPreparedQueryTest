import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Tuple;

@RunWith(VertxUnitRunner.class)
public class JDBCPreparedQueryTest {
	
	private static final Logger log = LoggerFactory.getLogger(JDBCPreparedQueryTest.class);
	
	private Vertx vertx;
	
	@Before
	public void createVertx() {
		vertx = Vertx.vertx();
	}
	
	@Test
	public void test(TestContext context) {
		JDBCPool pool = JDBCPool.pool(vertx, new JsonObject()
			.put("driver_class", "org.hsqldb.jdbc.JDBCDriver")
			.put("url", "jdbc:hsqldb:mem:testDb")
			.put("user", "sa")
			.put("password", "sa"));

		// create table
		pool.query("CREATE TABLE test_table ( test_data CLOB )")
			.execute()
			// insert Buffer into table
			.compose(rows -> pool.preparedQuery("INSERT INTO test_table ( test_data ) VALUES ( ? )")
				.execute(Tuple.of(Buffer.buffer("Hello world!")))
				.onFailure(e -> log.error("Failed to insert buffer into table.", e)))
			// ensure successful completion
			.onComplete(context.asyncAssertSuccess());
	}
	
	@After
	public void closeVertx(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}

}
