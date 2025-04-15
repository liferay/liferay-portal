/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package org.eclipse.equinox.http.servlet.internal;

import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.eclipse.equinox.http.servlet.internal.context.DispatchTargets;

import org.osgi.framework.ServiceReference;

/**
 * @author Dante Wang
 */
public interface HttpServletEndpointController {

	public void destroy();

	public DispatchTargets getDispatchTargets(String pathString);

	public List<String> getHttpServiceEndpoints();

	public ServletContext getParentServletContext();

	public Set<Object> getRegisteredObjects();

	public void log(String message, Throwable throwable);

	public boolean matches(ServiceReference<?> serviceReference);

}