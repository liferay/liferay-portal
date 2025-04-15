/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package org.eclipse.equinox.http.servlet.internal.context;

import java.util.Dictionary;

import javax.servlet.ServletContext;

/**
 * @author Dante Wang
 */
public interface ServletContextHelperDataContext {

	public void destroy();

	public Dictionary<String, Object> getContextAttributes();

	public ServletContext getServletContext();

}