/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.security.internal.container.response.filter;

import com.liferay.petra.reflect.AnnotationLocator;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.security.access.control.AccessControlAdvisor;
import com.liferay.portal.security.access.control.AccessControlAdvisorImpl;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;

import java.io.IOException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(!(liferay.access.control.disable=true))",
		"osgi.jaxrs.extension=true", "osgi.jaxrs.name=Liferay.Access.Control"
	},
	scope = ServiceScope.PROTOTYPE, service = ContainerRequestFilter.class
)
public class AccessControlledContainerRequestFilter
	implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext)
		throws IOException {

		_incrementServiceDepth();

		Method method = _resourceInfo.getResourceMethod();

		AccessControlled accessControlled = AnnotationLocator.locate(
			method, _resourceInfo.getClass(), AccessControlled.class);

		if (accessControlled == null) {
			accessControlled = _NULL_ACCESS_CONTROLLED;
		}

		_accessControlAdvisor.accept(method, new Object[0], accessControlled);
	}

	private void _incrementServiceDepth() {
		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext == null) {
			return;
		}

		Map<String, Object> settings = accessControlContext.getSettings();

		Integer serviceDepth = (Integer)settings.get(
			AccessControlContext.Settings.SERVICE_DEPTH.toString());

		if (serviceDepth == null) {
			serviceDepth = Integer.valueOf(1);
		}
		else {
			serviceDepth++;
		}

		settings.put(
			AccessControlContext.Settings.SERVICE_DEPTH.toString(),
			serviceDepth);
	}

	private static final AccessControlled _NULL_ACCESS_CONTROLLED =
		new AccessControlled() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return AccessControlled.class;
			}

			@Override
			public boolean guestAccessEnabled() {
				return false;
			}

			@Override
			public boolean hostAllowedValidationEnabled() {
				return false;
			}

		};

	private final AccessControlAdvisor _accessControlAdvisor =
		new AccessControlAdvisorImpl();

	@Context
	private Request _request;

	@Context
	private ResourceInfo _resourceInfo;

}