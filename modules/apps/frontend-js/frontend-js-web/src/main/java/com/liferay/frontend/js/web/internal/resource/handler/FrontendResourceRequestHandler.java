/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource.handler;

import com.liferay.frontend.js.web.internal.resource.FrontendResource;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * @author Iván Zaera Avellón
 */
public interface FrontendResourceRequestHandler {

	public boolean canHandleRequest(HttpServletRequest httpServletRequest);

	public FrontendResource handleRequest(HttpServletRequest httpServletRequest)
		throws IOException, ServletException;

}