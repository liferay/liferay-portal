/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.authorize.message.body;

import com.liferay.portal.kernel.util.StringUtil;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.apache.cxf.rs.security.oauth2.common.OAuthError;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marta Medio
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.OAuth2.Application)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=OAuthErrorMessageBodyWriter"
	},
	service = MessageBodyWriter.class
)
@Produces("text/html")
@Provider
public class OAuthErrorMessageBodyWriter
	extends BaseMessageBodyWriter<OAuthError> {

	@Override
	public boolean isWriteable(
		Class<?> aClass, Type type, Annotation[] annotations,
		MediaType mediaType) {

		if (aClass.isAssignableFrom(OAuthError.class) &&
			StringUtil.equalsIgnoreCase(mediaType.getType(), "text") &&
			StringUtil.equalsIgnoreCase(mediaType.getSubtype(), "html")) {

			return true;
		}

		return false;
	}

	@Override
	protected String writeTo(OAuthError oAuthError, String authorizeScreenURL) {
		return setParameter(
			authorizeScreenURL, OAuthConstants.ERROR_KEY,
			oAuthError.getError());
	}

}