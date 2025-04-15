/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.test;

import jakarta.ws.rs.core.Application;

import java.util.Collections;
import java.util.Set;

/**
 * @author Carlos Sierra Andrés
 */
public class TestInterfaceAnnotatedApplication
	extends Application implements TestAnnotatedInterface {

	@Override
	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@Override
	public String getString() {
		return "everything.read";
	}

	@Override
	public String getStringNoScope() {
		return "no-scope";
	}

}