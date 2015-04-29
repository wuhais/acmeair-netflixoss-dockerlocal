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

public class CommandConstants {
	public static final int THREAD_POOL_CORE_SIZE = 100;
	public static final String COMMAND_GROUP_KEY = "AcmeAirGroup";
	public static final String THREAD_POOL_KEY = "AcmeAirPool";
	public static final String ACME_AIR_AUTH_SERVICE_NAMED_CLIENT = "acmeair-auth-service-client";
	public static final String ACME_AIR_AUTH_SERVICE_CONTEXT_AND_REST_PATH = "/rest/api";
}
