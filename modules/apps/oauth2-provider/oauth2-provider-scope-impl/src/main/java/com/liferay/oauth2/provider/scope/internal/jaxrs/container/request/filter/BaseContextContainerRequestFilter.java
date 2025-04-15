/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.scope.internal.jaxrs.container.request.filter;

import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseContextContainerRequestFilter
	implements ContainerRequestFilter {

	public Bundle getBundle() {
		return FrameworkUtil.getBundle(application.getClass());
	}

	public long getCompanyId() {
		return PortalUtil.getCompanyId(httpServletRequest);
	}

	@Context
	protected Application application;

	@Context
	protected HttpServletRequest httpServletRequest;

}