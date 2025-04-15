/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.authorize.message.body;

import com.liferay.oauth2.provider.rest.internal.endpoint.authorize.configuration.AuthorizeScreenConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.MessageBodyWriter;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.apache.cxf.jaxrs.ext.MessageContext;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseMessageBodyWriter<T> implements MessageBodyWriter<T> {

	@Override
	public long getSize(
		T t, Class<?> aClass, Type type, Annotation[] annotations,
		MediaType mediaType) {

		return -1L;
	}

	@Override
	public void writeTo(
			T t, Class<?> aClass, Type type, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap,
			OutputStream outputStream)
		throws WebApplicationException {

		HttpServletRequest httpServletRequest =
			messageContext.getHttpServletRequest();

		String authorizeScreenURL = null;

		try {
			authorizeScreenURL = _getAuthorizeScreenURL(
				portal.getCompanyId(httpServletRequest));
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get authorize screen configuration",
				configurationException);

			throw new WebApplicationException(
				Response.status(
					Response.Status.INTERNAL_SERVER_ERROR
				).build());
		}

		if (!HttpComponentsUtil.hasDomain(authorizeScreenURL)) {
			String portalURL = portal.getPortalURL(httpServletRequest);

			authorizeScreenURL = portalURL + authorizeScreenURL;
		}

		authorizeScreenURL = writeTo(t, authorizeScreenURL);

		messageContext.put("http.request.redirected", Boolean.TRUE);

		HttpServletResponse httpServletResponse =
			messageContext.getHttpServletResponse();

		try {
			httpServletResponse.sendRedirect(authorizeScreenURL);
		}
		catch (IOException ioException) {
			throw new WebApplicationException(ioException);
		}
	}

	protected String removeParameter(String url, String name) {
		return HttpComponentsUtil.removeParameter(url, "oauth2_" + name);
	}

	protected String setParameter(String url, String name, String value) {
		if (Validator.isBlank(value)) {
			return url;
		}

		return HttpComponentsUtil.addParameter(url, "oauth2_" + name, value);
	}

	protected abstract String writeTo(T t, String authorizeScreenURL);

	@Reference
	protected ConfigurationProvider configurationProvider;

	@Context
	protected MessageContext messageContext;

	@Reference
	protected Portal portal;

	private String _getAuthorizeScreenURL(long companyId)
		throws ConfigurationException {

		AuthorizeScreenConfiguration authorizeScreenConfiguration =
			configurationProvider.getConfiguration(
				AuthorizeScreenConfiguration.class,
				new CompanyServiceSettingsLocator(
					companyId, AuthorizeScreenConfiguration.class.getName()));

		return authorizeScreenConfiguration.authorizeScreenURL();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseMessageBodyWriter.class);

}