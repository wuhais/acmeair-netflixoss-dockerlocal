package com.acmeair.service.astyanax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.acmeair.entities.AirportCodeMapping;
import com.acmeair.entities.Flight;
import com.acmeair.entities.FlightPK;
import com.acmeair.entities.FlightSegment;
import com.acmeair.service.FlightService;
import com.acmeair.service.KeyGenerator;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
//import com.netflix.astyanax.ColumnMutation;
//import com.netflix.astyanax.MutationBatch;
//import com.netflix.astyanax.model.ColumnFamily;
//import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
//import com.netflix.astyanax.serializers.CompositeSerializer;
//import com.netflix.astyanax.serializers.StringSerializer;
//import com.netflix.astyanax.annotations.Component;
import com.datastax.driver.core.Row;

@Service("flightService")
public class FlightServiceImpl implements FlightService {

//	public static class FlightSegmentSer {
//		@Component(ordinal = 0)
//		String origPort;
//		
//		@Component(ordinal = 1)
//		String destPort;
//		
//		public FlightSegmentSer(String oPort, String dPort) {
//			origPort = oPort;
//			destPort = dPort;
//		}
//	}
//	
//	static AnnotatedCompositeSerializer<FlightSegmentSer> flightSegmentSerializer = new AnnotatedCompositeSerializer<FlightSegmentSer>(FlightSegmentSer.class);
//	
//	private static final ColumnFamily<String, String> CF_AIRPORT_CODE_MAPPING = new ColumnFamily<String, String>("airport_code_mapping", StringSerializer.get(), StringSerializer.get());
//	//private static final ColumnFamily<String, FlightSegmentSer> CF_FLIGHT_SEGMENT =
//	//	new ColumnFamily<String, FlightSegmentSer>
//	//	("flight_segment", StringSerializer.get(), flightSegmentSerializer);
//	private static final ColumnFamily<String, String> CF_FLIGHT = new ColumnFamily<String, String>("flight", StringSerializer.get(), StringSerializer.get());

	private static Logger log = LoggerFactory.getLogger(FlightServiceImpl.class);
			
	@Resource
	KeyGenerator keyGenerator;

	//TODO: consider adding time based invalidation to these maps
	private static ConcurrentHashMap<String, FlightSegment> originAndDestPortToSegmentCache = new ConcurrentHashMap<String,FlightSegment>();
	private static ConcurrentHashMap<String, List<Flight>> flightSegmentAndDataToFlightCache = new ConcurrentHashMap<String,List<Flight>>();
	private static ConcurrentHashMap<FlightPK, Flight> flightPKtoFlightCache = new ConcurrentHashMap<FlightPK, Flight>();
	
	private static PreparedStatement INSERT_INTO_FLIGHT_SEGMENT_PS;
	private static PreparedStatement INSERT_INTO_FLIGHT_PS;
	private static PreparedStatement INSERT_INTO_AIRPORT_CODE_MAPPING_PS;
	private static PreparedStatement SELECT_ALL_FROM_FLIGHT_SEGMENT_BY_PORTS_PS;
	private static PreparedStatement SELECT_ALL_FROM_FLIGHT_BY_SEGMENT_PS;
	private static PreparedStatement SELECT_ALL_FROM_FLIGHT_BY_SEGMENT_AND_ID_PS;
	private static PreparedStatement SELECT_ALL_FROM_FLIGHT_BY_SEGMENT_AND_DEPARTURE_DATE_PS;
	
	static {
		prepareStatements();
	}
	
	private static void prepareStatements() {
		INSERT_INTO_FLIGHT_SEGMENT_PS = CUtils.getAcmeAirSession().prepare(
			"INSERT INTO flight_segment (flight_segment_id, origin_port, dest_port, miles) VALUES (?, ?, ?, ?);"
		);
		SELECT_ALL_FROM_FLIGHT_SEGMENT_BY_PORTS_PS = CUtils.getAcmeAirSession().prepare(
			"SELECT * FROM flight_segment where origin_port = ? and dest_port = ?;"
		);
		INSERT_INTO_FLIGHT_PS = CUtils.getAcmeAirSession().prepare(
			"INSERT INTO flight (flight_id, flight_segment_id, scheduled_departure_time, scheduled_arrival_time, " +
				"first_class_base_cost, economy_class_base_cost, num_first_class_seats, num_economy_class_seats, " +
				"airplane_type_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
		);
		SELECT_ALL_FROM_FLIGHT_BY_SEGMENT_PS = CUtils.getAcmeAirSession().prepare(
			"SELECT * FROM flight where flight_segment_id = ?;"
		);
		SELECT_ALL_FROM_FLIGHT_BY_SEGMENT_AND_ID_PS = CUtils.getAcmeAirSession().prepare(
			"SELECT * FROM flight where flight_segment_id = ? and flight_id = ?;"
		);
		SELECT_ALL_FROM_FLIGHT_BY_SEGMENT_AND_DEPARTURE_DATE_PS = CUtils.getAcmeAirSession().prepare(
			"SELECT * FROM flight WHERE flight_segment_id = ? and scheduled_departure_time = ?"
		);
		INSERT_INTO_AIRPORT_CODE_MAPPING_PS = CUtils.getAcmeAirSession().prepare(
			"INSERT INTO airport_code_mapping (airport_code, airport_name) VALUES (?, ?);"
		);
	}
	
	@Override
	public Flight getFlightByFlightKey(FlightPK key) {
		BoundStatement bs = new BoundStatement(SELECT_ALL_FROM_FLIGHT_BY_SEGMENT_AND_ID_PS);
		bs.bind(key.getFlightSegmentId(), key.getId());
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
		int ii = 0;
		Flight flight = null;
		flight = flightPKtoFlightCache.get(key);
		if (flight == null) {
			for (Row row : rs) {
				Date scheduled_departure_time = row.getDate("scheduled_departure_time");
				Date scheduled_arrival_time = row.getDate("scheduled_arrival_time");
				BigDecimal first_class_base_cost = row.getDecimal("first_class_base_cost");
				BigDecimal economy_class_base_cost = row.getDecimal("economy_class_base_cost");
				int num_first_class_seats = row.getInt("num_first_class_seats");
				int num_economy_class_seats = row.getInt("num_economy_class_seats");
				String airplane_type_id = row.getString("airplane_type_id");
				flight = new Flight(key.getId(), key.getFlightSegmentId(), scheduled_departure_time, scheduled_arrival_time,
					first_class_base_cost, economy_class_base_cost, num_first_class_seats, num_economy_class_seats, airplane_type_id);
				ii++;
			}
			if (ii > 1) {
				log.warn("more than one flight segment row returned, using last");
			}
			if (key != null && flight != null) {
				flightPKtoFlightCache.putIfAbsent(key, flight);
			}
		}
		return flight;
	}

	@Override
	public List<Flight> getFlightByAirportsAndDepartureDate(String fromAirport,
			String toAirport, Date deptDate) {
		// TODO: Understand if this should be moved to REST or Web 2.0 tier
		// wasn't needed in WXS version, but without it timezone doesn't match
		// on exact compare in Cassandra
		Calendar c = Calendar.getInstance();
		c.setTime(deptDate);
		c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
		Date departureTime = c.getTime();
		
		String originPortAndDestPortQueryString = fromAirport + toAirport;
		FlightSegment segment = originAndDestPortToSegmentCache.get(originPortAndDestPortQueryString);
		if (segment == null) {
			segment = getFlightSegment(fromAirport, toAirport);
			if (segment == null) {
				segment = new FlightSegment(); // put a sentinel value of a non-populated flightsegment
			}
			originAndDestPortToSegmentCache.putIfAbsent(originPortAndDestPortQueryString, segment);
		}
		// cache flights that not available (checks against sentinel value above indirectly)
		if (segment.getFlightName() == null) {
			return new ArrayList<Flight>();
		}
		
		String segId = segment.getFlightName();
		String flightSegmentIdAndScheduledDepartureTimeQueryString = segId + deptDate.toString();
		List<Flight> flights = flightSegmentAndDataToFlightCache.get(flightSegmentIdAndScheduledDepartureTimeQueryString);
		
		if (flights == null) {
			flights = new ArrayList<Flight>();
			
			BoundStatement bs = new BoundStatement(SELECT_ALL_FROM_FLIGHT_BY_SEGMENT_AND_DEPARTURE_DATE_PS);
			bs.bind(segment.getFlightName(), departureTime);
			log.info("bs = " + bs);
			log.info("statement = " + SELECT_ALL_FROM_FLIGHT_BY_SEGMENT_AND_DEPARTURE_DATE_PS.getQueryString());
			log.info("bound values = " + segment.getFlightName() + " " + deptDate);
			log.info("departureTime = " + departureTime);
			ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
			for (Row row : rs) {
				String flight_id = row.getString("flight_id");
				Date scheduled_departure_time = row.getDate("scheduled_departure_time");
				Date scheduled_arrival_time = row.getDate("scheduled_arrival_time");
				BigDecimal first_class_base_cost = row.getDecimal("first_class_base_cost");
				BigDecimal economy_class_base_cost = row.getDecimal("economy_class_base_cost");
				int num_first_class_seats = row.getInt("num_first_class_seats");
				int num_economy_class_seats = row.getInt("num_economy_class_seats");
				String airplane_type_id = row.getString("airplane_type_id");
				Flight f = new Flight(flight_id, segment.getFlightName(), scheduled_departure_time, scheduled_arrival_time,
					first_class_base_cost, economy_class_base_cost, num_first_class_seats, num_economy_class_seats, airplane_type_id);
				f.setFlightSegment(segment);
				flights.add(f);
			}

			flightSegmentAndDataToFlightCache.putIfAbsent(flightSegmentIdAndScheduledDepartureTimeQueryString, flights);
		}
		return flights;
	}

	@Override
	public List<Flight> getFlightByAirports(String fromAirport, String toAirport) {
		FlightSegment segment = getFlightSegment(fromAirport, toAirport);
		
		BoundStatement bs = new BoundStatement(SELECT_ALL_FROM_FLIGHT_BY_SEGMENT_PS);
		bs.bind(segment.getFlightName());
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
		List<Flight> flights = new ArrayList<Flight>();
		for (Row row : rs) {
			String flight_id = row.getString("flight_id");
			Date scheduled_departure_time = row.getDate("scheduled_departure_time");
			Date scheduled_arrival_time = row.getDate("scheduled_arrival_time");
			BigDecimal first_class_base_cost = row.getDecimal("first_class_base_cost");
			BigDecimal economy_class_base_cost = row.getDecimal("economy_class_base_cost");
			int num_first_class_seats = row.getInt("num_first_class_seats");
			int num_economy_class_seats = row.getInt("num_economy_class_seats");
			String airplane_type_id = row.getString("airplane_type_id");
			Flight f = new Flight(flight_id, segment.getFlightName(), scheduled_departure_time, scheduled_arrival_time,
				first_class_base_cost, economy_class_base_cost, num_first_class_seats, num_economy_class_seats, airplane_type_id);
			f.setFlightSegment(segment);
			flights.add(f);
		}
		return flights;
	}

	@Override
	public void storeAirportMapping(AirportCodeMapping mapping) {
		BoundStatement bs = new BoundStatement(INSERT_INTO_AIRPORT_CODE_MAPPING_PS);
		bs.bind(mapping.getAirportCode(), mapping.getAirportName());
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
	}

	@Override
	public Flight createNewFlight(String flightSegmentId,
			Date scheduledDepartureTime, Date scheduledArrivalTime,
			BigDecimal firstClassBaseCost, BigDecimal economyClassBaseCost,
			int numFirstClassSeats, int numEconomyClassSeats,
			String airplaneTypeId) {
		String id = keyGenerator.generate().toString();
		Flight flight = new Flight(id, flightSegmentId,
			scheduledDepartureTime, scheduledArrivalTime,
			firstClassBaseCost, economyClassBaseCost,
			numFirstClassSeats, numEconomyClassSeats,
			airplaneTypeId);

		BoundStatement bs = new BoundStatement(INSERT_INTO_FLIGHT_PS);
		bs.bind(flight.getPkey().getId(), flight.getPkey().getFlightSegmentId(),
			flight.getScheduledDepartureTime(), flight.getScheduledArrivalTime(),
			flight.getFirstClassBaseCost(), flight.getEconomyClassBaseCost(),
			flight.getNumFirstClassSeats(), flight.getNumEconomyClassSeats(),
			flight.getAirplaneTypeId());
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
		return flight;
	}

	@Override
	public void storeFlightSegment(FlightSegment flightSeg) {		
		BoundStatement bs = new BoundStatement(INSERT_INTO_FLIGHT_SEGMENT_PS);
		bs.bind(flightSeg.getFlightName(), flightSeg.getOriginPort(), flightSeg.getDestPort(), flightSeg.getMiles());

		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
	}
	
	private FlightSegment getFlightSegment(String originPort, String destPort) {
		BoundStatement bs = new BoundStatement(SELECT_ALL_FROM_FLIGHT_SEGMENT_BY_PORTS_PS);
		bs.bind(originPort, destPort);
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
		int ii = 0;
		FlightSegment fs = null;
		for (Row row : rs) {
			String segmentId = row.getString("flight_segment_id");
			int miles = row.getInt("miles");
			fs = new FlightSegment(segmentId, originPort, destPort, miles);
			ii++;
		}
		if (ii > 1) {
			log.warn("more than one flight segment row returned, using last");
		}
		return fs;
	}
	
	// TODO: Needed in loader.  Without a call to this J2SE doesn't exit
	// is there a way to deamonize this?
	@Override
	public void closeDatasource() {
		Cluster c = CUtils.getAcmeAirSession().getCluster();
		c.shutdown();
	}
}
