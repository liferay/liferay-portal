/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.connection.authentication;

import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * @author Matija Petanjek
 */
public interface HttpAuthentication {

	public String getAuthorizationHeader() throws ModuleException;

}