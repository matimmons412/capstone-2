package com.techelevator.tenmo.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.tenmo.model.User;

public class AccountsDAOIntergrationTest {

	private static SingleConnectionDataSource dataSource;
	private AccountDAO dao;
	private static String TEST_USER1 = "FakeUser1";
	private User testUser;
	private UserSqlDAO userDAO;
	
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
		dao = new AccountSqlDAO(jdbcTemplate);
		
		userDAO = new UserSqlDAO(jdbcTemplate);
		
		userDAO.create(TEST_USER1, "hashbrown");
		
		testUser = userDAO.findByUsername(TEST_USER1);
		
		
	}
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
	@Test
	public void getBalance_returns_balance() {
		// ARRANGE 
		
		BigDecimal balance = dao.getBalance(testUser.getId());
		
		assertEquals(1000, balance);
	}
	
	@Test
	public void findAll_returns_list_of_all_users() {
		// ARRANGE 
		List<User> userList = new ArrayList<>();
		
		// ACT
		
		userList = userDAO.findAll();
		
		// ASSERT 
		assertNotNull(userList);
		assertNotEquals(0, userList.size());
		assertEquals(TEST_USER1, userList.get(2).getUsername());
		
		
	}
}
