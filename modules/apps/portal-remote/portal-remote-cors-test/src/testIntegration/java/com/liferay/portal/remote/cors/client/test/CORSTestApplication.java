/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cors.client.test;

import com.liferay.portal.remote.cors.annotation.CORS;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

import java.util.Collections;
import java.util.Set;

/**
 * @author Marta Medio
 */
@CORS
@Path("/cors-app")
public class CORSTestApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@GET
	public String getString() {
		return "get";
	}

	@GET
	@Path("/duplicate/path/whatever")
	public String getTestPath1() {
		return "/duplicate/path/whatever";
	}

	@GET
	@Path("/instance/only/path/whatever")
	public String getTestPath2() {
		return "/instance/only/path/whatever";
	}

	@GET
	@Path("/overwritten/path/whatever")
	public String getTestPath3() {
		return "/overwritten/path/whatever";
	}

	@GET
	@Path("/system/only/path/whatever")
	public String getTestPath4() {
		return "/system/only/path/whatever";
	}

}