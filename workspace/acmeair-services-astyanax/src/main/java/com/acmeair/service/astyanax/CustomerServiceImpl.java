package com.acmeair.service.astyanax;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.acmeair.entities.Customer;
import com.acmeair.entities.Customer.MemberShipStatus;
import com.acmeair.entities.Customer.PhoneType;
import com.acmeair.entities.CustomerAddress;
import com.acmeair.entities.CustomerSession;
import com.acmeair.service.CustomerService;
import com.acmeair.service.KeyGenerator;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
//import com.netflix.astyanax.MutationBatch;
//import com.netflix.astyanax.model.ColumnFamily;
//import com.netflix.astyanax.model.ColumnList;
//import com.netflix.astyanax.serializers.StringSerializer;
import com.datastax.driver.core.PreparedStatement;

@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

	private static final int DAYS_TO_ALLOW_SESSION = 1;

//	private static final ColumnFamily<String, String> CF_CUSTOMER = new ColumnFamily<String, String>("customer", StringSerializer.get(), StringSerializer.get());
//	private static final ColumnFamily<String, String> CF_CUSTOMER_SESSION = new ColumnFamily<String, String>("customer_session", StringSerializer.get(), StringSerializer.get());
	
	private static Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
	
	@Resource
	KeyGenerator keyGenerator;

	static {
		prepareStatements();
	}
	
	private static PreparedStatement INSERT_INTO_CUSTOMER_PS;
	private static PreparedStatement SELECT_ALL_FROM_CUSTOMER_BY_USERNAME_PS;
	private static PreparedStatement INSERT_INTO_CUSTOMER_SESSION_PS;
	private static PreparedStatement SELECT_ALL_FROM_CUSTOMER_SESSION_BY_SESSION_ID_PS;
	private static PreparedStatement DELETE_FROM_CUSTOMER_SESSION_BY_SESSION_ID_PS;
	
	private static void prepareStatements() {
		INSERT_INTO_CUSTOMER_PS = CUtils.getAcmeAirSession().prepare(
			"INSERT INTO customer (username, password, customer_status, total_miles, miles_ytd, " +
			"addr_street1, addr_street2, addr_city, addr_state_province, addr_country, addr_postal_code, " +
			"phone_number, phone_number_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
		);
		SELECT_ALL_FROM_CUSTOMER_BY_USERNAME_PS = CUtils.getAcmeAirSession().prepare(
			"SELECT * FROM customer where username = ?;"
		);
		INSERT_INTO_CUSTOMER_SESSION_PS = CUtils.getAcmeAirSession().prepare(
			"INSERT INTO customer_session (session_id, customer_id, last_accessed_time, timeout_time) VALUES (?, ?, ?, ?);"
		);
		SELECT_ALL_FROM_CUSTOMER_SESSION_BY_SESSION_ID_PS = CUtils.getAcmeAirSession().prepare(
			"SELECT * FROM customer_session where session_id = ?;"
		);
		DELETE_FROM_CUSTOMER_SESSION_BY_SESSION_ID_PS = CUtils.getAcmeAirSession().prepare(
			"DELETE FROM customer_session where session_id = ?;"
		);
	}
	
	public Customer createCustomer(String username, String password,
			MemberShipStatus status, int total_miles, int miles_ytd,
			String phoneNumber, PhoneType phoneNumberType,
			CustomerAddress address) {
		Customer customer = new Customer(username, password, status, total_miles, miles_ytd, address, phoneNumber, phoneNumberType);
		return upsertCustomer(customer);
	}
	
	
	private Customer upsertCustomer(Customer customer) {
		BoundStatement bs = new BoundStatement(INSERT_INTO_CUSTOMER_PS);
		bs.bind(customer.getUsername(), customer.getPassword(), customer.getStatus().toString(), customer.getTotal_miles(),
			customer.getMiles_ytd(), customer.getAddress().getStreetAddress1(), customer.getAddress().getStreetAddress2(),
			customer.getAddress().getCity(), customer.getAddress().getStateProvince(), customer.getAddress().getCountry(),
			customer.getAddress().getPostalCode(), customer.getPhoneNumber(), customer.getPhoneNumberType().toString());
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
		return customer;
	}
	

	@Override
	public Customer updateCustomer(Customer customer) {
		return upsertCustomer(customer);
	}
	

	private Customer getCustomer(String username) {
		BoundStatement bs = new BoundStatement(SELECT_ALL_FROM_CUSTOMER_BY_USERNAME_PS);
		bs.bind(username);
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
		int ii = 0;
		Customer customer = null;
		for (Row row : rs) {
			String password = row.getString("password");
			MemberShipStatus status = MemberShipStatus.valueOf(row.getString("customer_status"));
			int total_miles = row.getInt("total_miles");
			int miles_ytd = row.getInt("miles_ytd");
			String addr_street1 = row.getString("addr_street1");
			String addr_street2 = row.getString("addr_street2");
			String addr_city = row.getString("addr_city");
			String addr_state_province = row.getString("addr_state_province");
			String addr_country = row.getString("addr_country");
			String addr_postal_code = row.getString("addr_postal_code");
			String phone_number = row.getString("phone_number");
			PhoneType phone_number_type = PhoneType.valueOf(row.getString("phone_number_type"));
			CustomerAddress address = new CustomerAddress(addr_street1, addr_street2,
				addr_city, addr_state_province, addr_country, addr_postal_code);
			customer = new Customer(username, password, status, total_miles, miles_ytd, address, phone_number, phone_number_type);
			ii++;
		}
		if (ii > 1) {
			log.warn("more than one customer row returned, using last");
		}
		return customer;
	}
	
	
	@Override
	public Customer getCustomerByUsername(String username) {
		Customer c = getCustomer(username);
		if (c != null) {
			c.setPassword(null);
		}
		return c;
	}
	

	@Override
	public boolean validateCustomer(String username, String password) {
		boolean validatedCustomer = false;
		Customer customerToValidate = getCustomer(username);
		if (customerToValidate != null) {
			validatedCustomer = password.equals(customerToValidate.getPassword());
		}
		return validatedCustomer;
	}
	

	@Override
	public Customer getCustomerByUsernameAndPassword(String username,
			String password) {
		Customer c = getCustomer(username);
		if (!c.getPassword().equals(password)) {
			return null;
		}
		// Should we also set the password to null?
		return c;
	}
	

	@Override
	public CustomerSession validateSession(String sessionid) {
		BoundStatement bs = new BoundStatement(SELECT_ALL_FROM_CUSTOMER_SESSION_BY_SESSION_ID_PS);
		bs.bind(sessionid);
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
		int ii = 0;
		CustomerSession cs = null;
		for (Row row : rs) {
			String sessId = row.getString("session_id");
			String custId = row.getString("customer_id");
			Date lastAccessedTime = row.getDate("last_accessed_time");
			Date timeoutTime = row.getDate("timeout_time");
			cs = new CustomerSession(sessId, custId, lastAccessedTime, timeoutTime);
			ii++;
		}
		if (ii > 1) {
			log.warn("more than one customer session row returned, using last");
		}
		return cs;
	}

	@Override
	public CustomerSession createSession(String customerId) {
		String sessionId = keyGenerator.generate().toString();
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.add(Calendar.DAY_OF_YEAR, DAYS_TO_ALLOW_SESSION);
		Date expiration = c.getTime();
		CustomerSession cSession = new CustomerSession(sessionId, customerId, now, expiration);
		
		BoundStatement bs = new BoundStatement(INSERT_INTO_CUSTOMER_SESSION_PS);
		bs.bind(cSession.getId(), cSession.getCustomerid(), cSession.getLastAccessedTime(), cSession.getTimeoutTime());
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
		return cSession;
	}

	@Override
	public void invalidateSession(String sessionid) {
		BoundStatement bs = new BoundStatement(DELETE_FROM_CUSTOMER_SESSION_BY_SESSION_ID_PS);
		bs.bind(sessionid);
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
	}
}
