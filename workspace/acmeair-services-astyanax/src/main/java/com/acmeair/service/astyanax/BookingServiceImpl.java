package com.acmeair.service.astyanax;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.acmeair.entities.Booking;
import com.acmeair.entities.BookingPK;
import com.acmeair.entities.Customer;
import com.acmeair.entities.Flight;
import com.acmeair.entities.FlightPK;
import com.acmeair.service.BookingService;
import com.acmeair.service.CustomerService;
import com.acmeair.service.FlightService;
import com.acmeair.service.KeyGenerator;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service("bookingService")
public class BookingServiceImpl implements BookingService {

	@Resource
	FlightService flightService;

	@Resource
	CustomerService customerService;

	private static PreparedStatement INSERT_INTO_BOOKING_PS;
	private static PreparedStatement SELECT_ALL_FROM_BOOKING_BY_USER_ID_AND_BOOKING_ID_PS;
	private static PreparedStatement SELECT_ALL_BOOKINGS_BY_USER_ID_PS;
	private static PreparedStatement DELETE_FROM_BOOKING_BY_USER_ID_AND_BOOKING_ID_PS;

	@Resource
	KeyGenerator keyGenerator;
	
	private static Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

	static {
		prepareStatements();
	}
	
	private static void prepareStatements() {
		INSERT_INTO_BOOKING_PS = CUtils.getAcmeAirSession().prepare(
			"INSERT INTO booking (customer_id, booking_id, flight_id, flight_segment_id, booking_date) VALUES (?, ?, ?, ?, ?);"
		);
		SELECT_ALL_FROM_BOOKING_BY_USER_ID_AND_BOOKING_ID_PS = CUtils.getAcmeAirSession().prepare(
			"SELECT * FROM booking where customer_id = ? and booking_id = ?;"
		);
		DELETE_FROM_BOOKING_BY_USER_ID_AND_BOOKING_ID_PS = CUtils.getAcmeAirSession().prepare(
			"DELETE FROM booking where customer_id = ? and booking_id = ?;"
		);
		SELECT_ALL_BOOKINGS_BY_USER_ID_PS = CUtils.getAcmeAirSession().prepare(
			"SELECT * FROM booking where customer_id = ?;"
		);
	}

	@Override
	public BookingPK bookFlight(String customerId, FlightPK flightId) {
		String bookingId = keyGenerator.generate().toString();
		Date dateOfBooking = new Date();
		BookingPK key = new BookingPK(customerId, bookingId);
			
		BoundStatement bs = new BoundStatement(INSERT_INTO_BOOKING_PS);
		bs.bind(customerId, bookingId, flightId.getId(), flightId.getFlightSegmentId(), dateOfBooking);
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
		return key;
	}

	@Override
	public Booking getBooking(String user, String id) {
		BoundStatement bs = new BoundStatement(SELECT_ALL_FROM_BOOKING_BY_USER_ID_AND_BOOKING_ID_PS);
		bs.bind(user, id);
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
		int ii = 0;
		Booking booking = null;
		for (Row row : rs) {
			Date bookingDate = row.getDate("booking_date");
			String flightId = row.getString("flight_id");
			String flightSegmentId = row.getString("flight_segment_id");
			
			Customer customer = customerService.getCustomerByUsername(user);
			Flight flight = flightService.getFlightByFlightKey(new FlightPK(flightSegmentId, flightId));
			booking = new Booking(id, bookingDate, customer, flight);
			
			ii++;
		}
		if (ii > 1) {
			log.warn("more than one booking returned, using last");
		}
		return booking;
	}

	@Override
	public List<Booking> getBookingsByUser(String user) {
		BoundStatement bs = new BoundStatement(SELECT_ALL_BOOKINGS_BY_USER_ID_PS);
		bs.bind(user);
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);

		Booking booking = null;
		Customer customer = customerService.getCustomerByUsername(user);
		List<Booking> list = new ArrayList<Booking>();
		for (Row row : rs) {
			Date bookingDate = row.getDate("booking_date");
			String flightId = row.getString("flight_id");
			String flightSegmentId = row.getString("flight_segment_id");
			String bookingId = row.getString("booking_id");
			
			Flight flight = flightService.getFlightByFlightKey(new FlightPK(flightSegmentId, flightId));
			booking = new Booking(bookingId, bookingDate, customer, flight);
			list.add(booking);
		}
		return list;
	}

	@Override
	public void cancelBooking(String user, String id) {
		BoundStatement bs = new BoundStatement(DELETE_FROM_BOOKING_BY_USER_ID_AND_BOOKING_ID_PS);
		bs.bind(user, id);
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
	}
}
