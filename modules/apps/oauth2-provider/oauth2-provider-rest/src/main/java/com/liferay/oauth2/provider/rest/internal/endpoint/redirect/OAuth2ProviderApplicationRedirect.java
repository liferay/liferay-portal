/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.redirect;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HtmlUtil;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;

/**
 * @author Raymond Augé
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.OAuth2.Application)",
		"osgi.jaxrs.name=Liferay.Authorization.Redirect",
		"osgi.jaxrs.resource=true"
	},
	service = OAuth2ProviderApplicationRedirect.class
)
@Path("/redirect")
public class OAuth2ProviderApplicationRedirect {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response redirect(
		@DefaultValue("") @Encoded @QueryParam("code") String code,
		@DefaultValue("") @Encoded @QueryParam("error") String error,
		@DefaultValue("") @Encoded @QueryParam("state") String state) {

		return Response.ok(
			StringBundler.concat(
				"<html><head><title>Liferay OAuth2 Redirect</title></head>",
				"<body><script",
				ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(null),
				" type=\"text/javascript\">window.postMessage(",
				JSONUtil.put(
					"code", HtmlUtil.escapeJS(code)
				).put(
					"error", HtmlUtil.escapeJS(error)
				).put(
					"state", HtmlUtil.escapeJS(state)
				),
				", document.location.href);</script></body></html>")
		).build();
	}

}