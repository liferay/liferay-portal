/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.addon.keep.alive.web.internal.servlet.taglib;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.saml.addon.keep.alive.web.internal.constants.SamlKeepAliveConstants;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.util.PortletPropsKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(service = DynamicInclude.class)
public class KeepAliveSamlWebDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		String keepAliveURL = null;

		if (_KEY_IDENTITY_PROVIDER.equals(key)) {
			keepAliveURL = _getSpIdpKeepAliveUrl(httpServletRequest);
		}
		else {
			keepAliveURL = _getIdpSpKeepAliveUrl(httpServletRequest);
		}

		httpServletRequest.setAttribute(
			SamlWebKeys.SAML_KEEP_ALIVE_URL, keepAliveURL);

		_includeJSP(
			httpServletRequest, httpServletResponse,
			"/com.liferay.saml.web/keep_alive.jsp");
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(_KEY_IDENTITY_PROVIDER);
		dynamicIncludeRegistry.register(_KEY_SERVICE_PROVIDER);
	}

	private String _getIdpSpKeepAliveUrl(
		HttpServletRequest httpServletRequest) {

		SamlIdpSpConnection samlIdpSpConnection =
			(SamlIdpSpConnection)httpServletRequest.getAttribute(
				SamlWebKeys.SAML_IDP_SP_CONNECTION);

		String keepAliveURL = StringPool.BLANK;

		if (samlIdpSpConnection != null) {
			ExpandoBridge expandoBridge =
				samlIdpSpConnection.getExpandoBridge();

			keepAliveURL = (String)expandoBridge.getAttribute(
				SamlKeepAliveConstants.EXPANDO_COLUMN_NAME_KEEP_ALIVE_URL);
		}

		return keepAliveURL;
	}

	private String _getSpIdpKeepAliveUrl(
		HttpServletRequest httpServletRequest) {

		SamlSpIdpConnection samlSpIdpConnection =
			(SamlSpIdpConnection)httpServletRequest.getAttribute(
				SamlWebKeys.SAML_SP_IDP_CONNECTION);

		if (samlSpIdpConnection == null) {
			return StringPool.BLANK;
		}

		ExpandoBridge expandoBridge = samlSpIdpConnection.getExpandoBridge();

		String keepAliveURL = (String)expandoBridge.getAttribute(
			SamlKeepAliveConstants.EXPANDO_COLUMN_NAME_KEEP_ALIVE_URL);

		if ((keepAliveURL == null) ||
			keepAliveURL.equals(
				SamlKeepAliveConstants.EXPANDO_COLUMN_NAME_KEEP_ALIVE_URL)) {

			keepAliveURL = PropsUtil.get(
				PortletPropsKeys.SAML_IDP_METADATA_SESSION_KEEP_ALIVE_URL);
		}

		return keepAliveURL;
	}

	private void _includeJSP(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String jspPath)
		throws IOException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(jspPath);

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (ServletException servletException) {
			throw new IOException(
				"Unable to include JSP " + jspPath, servletException);
		}
	}

	private static final String _KEY_IDENTITY_PROVIDER =
		"com.liferay.saml.web#/admin/edit_identity_provider_connection.jsp#" +
			"post";

	private static final String _KEY_SERVICE_PROVIDER =
		"com.liferay.saml.web#/admin/edit_service_provider_connection.jsp#post";

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.saml.addon.keep.alive.web)"
	)
	private ServletContext _servletContext;

}