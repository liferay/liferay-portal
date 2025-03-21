/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Drew Brokke
 */
@ProviderType
public interface InactiveRequestHandler {

	public default boolean isShowInactiveRequestMessage() {
		return false;
	}

	public void processInactiveRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String messageKey)
		throws IOException;

}