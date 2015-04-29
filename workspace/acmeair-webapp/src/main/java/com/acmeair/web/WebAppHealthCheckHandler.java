package com.acmeair.web;

import com.netflix.karyon.spi.HealthCheckHandler;

public class WebAppHealthCheckHandler implements HealthCheckHandler {

	@Override
	public int getStatus() {
		// TODO:  AWS - Understand if this is being called
		return 200;
	}
}
