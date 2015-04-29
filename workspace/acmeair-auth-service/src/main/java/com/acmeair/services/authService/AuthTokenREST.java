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
package com.acmeair.services.authService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.springframework.stereotype.Component;

import com.acmeair.entities.*;
import com.acmeair.service.*;
import com.acmeair.wxs.utils.TransactionService;

import javax.ws.rs.core.Context;

@Path("/authtoken")
@Component
public class AuthTokenREST {
	
	private CustomerService customerService = ServiceLocator.getService(CustomerService.class);
	
	@Context 
	private HttpServletRequest request;
	private TransactionService transactionService = null; 
	private boolean initializedTXService = false;
	
	private TransactionService getTxService() {
		if (!this.initializedTXService) {
			this.initializedTXService = true;
			transactionService = ServiceLocator.getService(TransactionService.class);
		}
		return transactionService;
	}

	@POST
	@Path("/byuserid/{userid}")
	@Produces("application/json")
	public /* CustomerSession */ Response createToken(@PathParam("userid") String userid) {
		setupTransaction();
		CustomerSession cs = customerService.createSession(userid);
		return Response.ok(cs).build();
	}
	
	@GET
	@Path("{tokenid}")
	@Produces("application/json")
	public Response validateToken(@PathParam("tokenid") String tokenid) {
		setupTransaction();
		CustomerSession cs = customerService.validateSession(tokenid);
		if (cs == null) {
			throw new WebApplicationException(404);
		}
		else {
			return Response.ok(cs).build();
		}
	}
	
	@DELETE
	@Path("{tokenid}")
	@Produces("application/json")
	public Response invalidateToken(@PathParam("tokenid") String tokenid) {
		setupTransaction();
		customerService.invalidateSession(tokenid);
		return Response.ok().build();
	}
	
	private void setupTransaction() {
		// The following code is to ensure that OG is always set on the thread
		try {
			TransactionService txService = getTxService();
			if (txService != null) {
				txService.prepareForTransaction();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
