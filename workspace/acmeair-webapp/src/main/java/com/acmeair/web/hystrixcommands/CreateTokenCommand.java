/*******************************************************************************
* Copyright (c) 2013 IBM Corp.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
package com.acmeair.web.hystrixcommands;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.net.URI;

import com.netflix.hystrix.*;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpRequest.Verb;
import com.netflix.client.http.HttpResponse;
import com.netflix.niws.client.http.RestClient;
import com.netflix.client.ClientFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.acmeair.entities.*;

import org.codehaus.jackson.map.*;

public class CreateTokenCommand extends HystrixCommand<CustomerSession> {
	private static final Log log = LogFactory.getLog(CreateTokenCommand.class);
	private String userid;
	
	public CreateTokenCommand(String userid) {
		super (Setter.
				withGroupKey(HystrixCommandGroupKey.Factory.asKey(CommandConstants.COMMAND_GROUP_KEY)).
				andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(CommandConstants.THREAD_POOL_KEY)).
				andThreadPoolPropertiesDefaults(
						HystrixThreadPoolProperties.Setter().
							withCoreSize(CommandConstants.THREAD_POOL_CORE_SIZE)));
        this.userid = userid;
	}
	
	@Override
	protected CustomerSession run() throws Exception {
		String responseString = null;
		try {
			URI uri = new URI(CommandConstants.ACME_AIR_AUTH_SERVICE_CONTEXT_AND_REST_PATH + "/authtoken/byuserid/" + userid);
			
			RestClient client = (RestClient) ClientFactory.getNamedClient(CommandConstants.ACME_AIR_AUTH_SERVICE_NAMED_CLIENT);
	
			HttpRequest request = HttpRequest.newBuilder().uri(uri).verb(Verb.POST).build();
			HttpResponse response = client.executeWithLoadBalancer(request);
			
			responseString = response.getEntity(String.class);
			log.debug("responseString = " + responseString);
			
			ObjectMapper mapper = new ObjectMapper();
			CustomerSession cs = mapper.readValue(responseString, CustomerSession.class);
			return cs;
		}
		catch (Throwable t) {
			log.error("caught exception", t);
			log.error("responseString = " + responseString);
			throw new Exception(t);
		}
	}
	
	@Override
	protected CustomerSession getFallback() {
		// TODO: Eventually we need to make this debug vs. error if fallback is expected to be called
		log.error("calling CreateTokenCommand fallback");
		log.error(getExecutionEvents());
		try {
			throw new Exception();
		}
		catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}
		return new CustomerSession();
	}

}
