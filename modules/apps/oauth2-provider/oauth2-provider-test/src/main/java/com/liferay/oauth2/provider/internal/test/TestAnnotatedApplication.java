/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.test;

import com.liferay.oauth2.provider.scope.RequiresNoScope;
import com.liferay.oauth2.provider.scope.RequiresScope;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.util.Collections;
import java.util.Set;

/**
 * @author Carlos Sierra Andrés
 */
public class TestAnnotatedApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@GET
	@RequestScopeRead
	public String getString() {
		return "everything.read";
	}

	@GET
	@Path("/no-scope")
	@RequiresNoScope
	public String getStringNoScope() {
		return "no-scope";
	}

	@RequiresScope("everything.read")
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface RequestScopeRead {
	}

}