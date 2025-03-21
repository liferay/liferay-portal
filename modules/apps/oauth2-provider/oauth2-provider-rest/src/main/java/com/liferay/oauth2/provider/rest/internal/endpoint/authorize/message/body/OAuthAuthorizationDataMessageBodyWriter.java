/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.authorize.message.body;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.net.URI;

import org.apache.cxf.rs.security.oauth2.common.OAuthAuthorizationData;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.OAuth2.Application)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=OAuthAuthorizationDataMessageBodyWriter"
	},
	service = MessageBodyWriter.class
)
@Produces("text/html")
@Provider
public class OAuthAuthorizationDataMessageBodyWriter
	extends BaseMessageBodyWriter<OAuthAuthorizationData> {

	@Override
	public boolean isWriteable(
		Class<?> clazz, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		if (clazz.isAssignableFrom(OAuthAuthorizationData.class) &&
			StringUtil.equalsIgnoreCase(mediaType.getType(), "text") &&
			StringUtil.equalsIgnoreCase(mediaType.getSubtype(), "html")) {

			return true;
		}

		return false;
	}

	@Activate
	protected void activate() {
		_invokerFilterURIMaxLength = GetterUtil.getInteger(
			_props.get(PropsKeys.INVOKER_FILTER_URI_MAX_LENGTH),
			_invokerFilterURIMaxLength);
	}

	@Override
	protected String writeTo(
		OAuthAuthorizationData oAuthAuthorizationData,
		String authorizeScreenURL) {

		authorizeScreenURL = setParameter(
			authorizeScreenURL, OAuthConstants.AUTHORIZATION_CODE_CHALLENGE,
			oAuthAuthorizationData.getClientCodeChallenge());
		authorizeScreenURL = setParameter(
			authorizeScreenURL, OAuthConstants.CLIENT_AUDIENCE,
			oAuthAuthorizationData.getAudience());
		authorizeScreenURL = setParameter(
			authorizeScreenURL, OAuthConstants.CLIENT_ID,
			oAuthAuthorizationData.getClientId());
		authorizeScreenURL = setParameter(
			authorizeScreenURL, OAuthConstants.NONCE,
			oAuthAuthorizationData.getNonce());
		authorizeScreenURL = setParameter(
			authorizeScreenURL, OAuthConstants.REDIRECT_URI,
			oAuthAuthorizationData.getRedirectUri());
		authorizeScreenURL = setParameter(
			authorizeScreenURL, OAuthConstants.RESPONSE_TYPE,
			oAuthAuthorizationData.getResponseType());
		authorizeScreenURL = setParameter(
			authorizeScreenURL, OAuthConstants.SCOPE,
			oAuthAuthorizationData.getProposedScope());
		authorizeScreenURL = setParameter(
			authorizeScreenURL, OAuthConstants.SESSION_AUTHENTICITY_TOKEN,
			oAuthAuthorizationData.getAuthenticityToken());
		authorizeScreenURL = setParameter(
			authorizeScreenURL, OAuthConstants.STATE,
			oAuthAuthorizationData.getState());
		authorizeScreenURL = setParameter(
			authorizeScreenURL, "reply_to",
			_getReplyTo(oAuthAuthorizationData));

		if (authorizeScreenURL.length() > _invokerFilterURIMaxLength) {
			authorizeScreenURL = removeParameter(
				authorizeScreenURL, OAuthConstants.SCOPE);
		}

		return authorizeScreenURL;
	}

	private String _getReplyTo(OAuthAuthorizationData oAuthAuthorizationData) {
		if (portal.isForwardedSecure(messageContext.getHttpServletRequest())) {
			UriInfo uriInfo = messageContext.getUriInfo();

			UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();

			uriBuilder.path("decision");
			uriBuilder.scheme(Http.HTTPS);

			URI uri = uriBuilder.build();

			return uri.toString();
		}

		return oAuthAuthorizationData.getReplyTo();
	}

	private int _invokerFilterURIMaxLength = 4000;

	@Reference
	private Props _props;

}