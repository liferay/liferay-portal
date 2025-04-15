/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.test;

import com.liferay.oauth2.provider.scope.RequiresNoScope;
import com.liferay.oauth2.provider.scope.RequiresScope;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.Application;

import java.util.Collections;
import java.util.Set;

/**
 * @author Stian Sigvartsen
 */
public class TestRunnablePostHandlingApplication extends Application {

	public TestRunnablePostHandlingApplication(Runnable runnable) {
		_runnable = runnable;
	}

	@Override
	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@GET
	@RequiresScope("everything.read")
	public String getString() {
		return "everything.read";
	}

	@POST
	@RequiresNoScope
	public void run() {
		_runnable.run();
	}

	private final Runnable _runnable;

}