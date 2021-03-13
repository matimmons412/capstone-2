package com.techelevator.tenmo.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.tenmo.model.Accounts;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;

public class TransfersDAOIntegrationTest {

	private static SingleConnectionDataSource dataSource;
	private TransfersDAO dao;
	private static String TEST_USER1 = "FakeUser1";
	private static String TEST_USER2 = "FakeUser2";
	List<User> userList;
	List<Accounts> acctList;

	@BeforeClass
	public static void setUpDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		dataSource.setAutoCommit(false);
	}

	@AfterClass
	public static void closeDataSource() {
		dataSource.destroy();
	}

	@Before
	public void setup() {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		dao = new TransfersSqlDAO(jdbcTemplate);

		UserSqlDAO userDAO = new UserSqlDAO(jdbcTemplate);

		userDAO.create(TEST_USER1, "hashbrown");
		userDAO.create(TEST_USER2, "perogies");

		userList = new ArrayList<>();
		acctList = new ArrayList<>();

		String sqlGetUser = "SELECT * FROM users JOIN accounts USING (user_id) WHERE username = ? OR username = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetUser, TEST_USER1, TEST_USER2);

		while (results.next()) {
			User user = new User();
			user = mapRowToUser(results);
			userList.add(user);

			Accounts account = new Accounts();
			account.setId(results.getLong("account_id"));
			acctList.add(account);
		}
	}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	@Test
	public void createSendTransfer_returns_transfer_object() {
		// Arrange

		Long senderAcctId = acctList.get(0).getId();
		Long receiverAcctId = acctList.get(1).getId();
		BigDecimal num = new BigDecimal(500.00);
		// ACT
		Transfers objUnderTest = dao.createSendTransfer(senderAcctId, receiverAcctId, num);
		// ASSERT
		assertNotNull(objUnderTest);
		assertEquals(0, num.compareTo(objUnderTest.getAmount()));

	}

	@Test
	public void transfer_amount_greater_than_sender_balance_returns_null() {
		// Arrange
		
		Long senderAcctId = acctList.get(0).getId();
		Long receiverAcctId = acctList.get(1).getId();
		BigDecimal num = new BigDecimal(500000.00);
		// ACT
		Transfers objUnderTest = dao.createSendTransfer(senderAcctId, receiverAcctId, num);
		// ASSERT
		assertNull(objUnderTest);
		
	}

	@Test
	public void listTransfers_returns_list() {
		// Arrange
		Long senderAcctId = acctList.get(0).getId();
		Long receiverAcctId = acctList.get(1).getId();
		BigDecimal num = new BigDecimal(50.00);
		BigDecimal num2 = new BigDecimal(60.00);
		BigDecimal num3 = new BigDecimal(70.00);
		Transfers objUnderTest = dao.createSendTransfer(senderAcctId, receiverAcctId, num);
		Transfers objUnderTest2 = dao.createSendTransfer(senderAcctId, receiverAcctId, num2);
		Transfers objUnderTest3 = dao.createSendTransfer(senderAcctId, receiverAcctId, num3);
		// ACT
		List<Transfers> listTest = dao.listTransfers(senderAcctId);
		// ASSERT
		assertNotNull(listTest);
		assertEquals(3, listTest.size());
		assertTrue(num.compareTo(listTest.get(0).getAmount()) == 0);
	}

	@Test
	public void getTransferById() {
		// ARRANGE
		Long senderAcctId = acctList.get(0).getId();
		Long receiverAcctId = acctList.get(1).getId();
		BigDecimal num = new BigDecimal(50.00);
		Transfers objUnderTest = dao.createSendTransfer(senderAcctId, receiverAcctId, num);
		// ACT

		Transfers obj = dao.getTransferById(objUnderTest.getId(), senderAcctId);
		// ASSERT
		assertNotNull(obj);
		assertEquals(TEST_USER1, obj.getAccountFrom());
		assertTrue(num.compareTo(obj.getAmount()) == 0);

	}


	private User mapRowToUser(SqlRowSet rs) {
		User user = new User();
		user.setId(rs.getLong("user_id"));
		user.setUsername(rs.getString("username"));

		return user;
	}
}
