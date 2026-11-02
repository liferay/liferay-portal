/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.constants;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface EventTypes {

	public static final String ADD = "ADD";

	public static final String AGREED_TO_TERMS_OF_USE =
		"AGREED_TO_TERMS_OF_USE";

	public static final String ASSIGN = "ASSIGN";

	public static final String DELETE = "DELETE";

	public static final String IMPERSONATE = "IMPERSONATE";

	public static final String LOGIN = "LOGIN";

	public static final String LOGIN_DNE = "LOGIN_DNE";

	public static final String LOGIN_FAILURE = "LOGIN_FAILURE";

	public static final String LOGOUT = "LOGOUT";

	public static final String UNASSIGN = "UNASSIGN";

	public static final String UPDATE = "UPDATE";

}