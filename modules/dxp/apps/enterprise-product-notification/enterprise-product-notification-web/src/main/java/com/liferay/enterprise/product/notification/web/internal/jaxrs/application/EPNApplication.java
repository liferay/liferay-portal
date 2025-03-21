/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.enterprise.product.notification.web.internal.jaxrs.application;

import com.liferay.enterprise.product.notification.web.internal.EPNManager;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/com-liferay-enterprise-product-notification-web",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=com.liferay.enterprise.product.notification.web.internal.jaxrs.application.EPNApplication",
		"auth.verifier.auth.verifier.PortalSessionAuthVerifier.urls.includes=/*",
		"auth.verifier.guest.allowed=false", "liferay.oauth2=false"
	},
	service = Application.class
)
public class EPNApplication extends Application {

	@Path("/confirm")
	@POST
	public Response confirm(
		@Context HttpServletRequest httpServletRequest,
		@Context HttpServletResponse httpServletResponse) {

		long userId = _portal.getUserId(httpServletRequest);

		if (userId <= 0) {
			return Response.serverError(
			).build();
		}

		_epnManager.confirm(userId);

		return Response.ok(
		).build();
	}

	public Set<Object> getSingletons() {
		return Collections.singleton(this);
	}

	@Reference
	private EPNManager _epnManager;

	@Reference
	private Portal _portal;

}