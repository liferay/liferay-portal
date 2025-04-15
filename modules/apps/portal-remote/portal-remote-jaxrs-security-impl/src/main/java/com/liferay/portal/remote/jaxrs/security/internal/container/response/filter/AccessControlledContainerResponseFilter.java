/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.security.internal.container.response.filter;

import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author João Victor Alves
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(!(liferay.access.control.disable=true))",
		"osgi.jaxrs.extension=true", "osgi.jaxrs.name=Liferay.Access.Control"
	},
	scope = ServiceScope.PROTOTYPE, service = ContainerResponseFilter.class
)
public class AccessControlledContainerResponseFilter
	implements ContainerResponseFilter {

	@Override
	public void filter(
			ContainerRequestContext containerRequestContext,
			ContainerResponseContext containerResponseContext)
		throws IOException {

		_decrementServiceDepth();
	}

	private void _decrementServiceDepth() {
		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext == null) {
			return;
		}

		Map<String, Object> settings = accessControlContext.getSettings();

		Integer serviceDepth = (Integer)settings.get(
			AccessControlContext.Settings.SERVICE_DEPTH.toString());

		if (serviceDepth == null) {
			return;
		}

		serviceDepth--;

		settings.put(
			AccessControlContext.Settings.SERVICE_DEPTH.toString(),
			serviceDepth);
	}

}