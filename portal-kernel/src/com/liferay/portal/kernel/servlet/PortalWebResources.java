/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.ServletContext;

/**
 * @author Peter Fellwock
 */
public interface PortalWebResources {

	public String getContextPath();

	public long getLastModified();

	public String getResourceType();

	public ServletContext getServletContext();

}