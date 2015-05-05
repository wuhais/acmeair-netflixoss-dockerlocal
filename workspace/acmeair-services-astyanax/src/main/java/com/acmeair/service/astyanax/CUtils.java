package com.acmeair.service.astyanax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

public class CUtils {
	private static Session aaSess;
	
	private static final Log log = LogFactory.getLog(CUtils.class);
	static DynamicStringProperty cContactPoint = DynamicPropertyFactory.getInstance().getStringProperty("com.acmeair.cassandra.contactpoint", "cass1");
	static String curContactPoint = "unknown";

	public static Session getAcmeAirSession() {
		String contactPoint = cContactPoint.get();
		
		if (!contactPoint.equals(curContactPoint)) {
			synchronized(curContactPoint) {
				// a different thread already got through sync block since the change
				if (contactPoint.equals(curContactPoint)) {
					return aaSess;
				}
				
				// shut down a different old connection
				try {
					if  (aaSess != null) {
						aaSess.getCluster().shutdown();
					}
				}
				catch (Exception e) {
					log.error("could not close existing cluster", e);
				}
				
				Cluster cluster = Cluster.builder().addContactPoint(contactPoint).build();
				aaSess = cluster.connect("acmeair");
				curContactPoint = contactPoint;
			}
		}
			
		return aaSess;
	}
}
