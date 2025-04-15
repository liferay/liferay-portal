/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.shielded.container;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 * @author Shuyang Zhou
 */
public interface ShieldedContainerInitializer {

	public static final String SHIELDED_CONTAINER_LIB =
		"/WEB-INF/shielded-container-lib";

	public static final String SHIELDED_CONTAINER_WEB_XML =
		"/WEB-INF/shielded-container-web.xml";

	public void initialize(ServletContext servletContext)
		throws ServletException;

}