/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.addon.keep.alive.web.internal.struts;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.addon.keep.alive.web.internal.constants.SamlKeepAliveConstants;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.model.SamlIdpSsoSession;
import com.liferay.saml.persistence.model.SamlPeerBinding;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;
import com.liferay.saml.persistence.service.SamlIdpSpSessionLocalService;
import com.liferay.saml.persistence.service.SamlIdpSsoSessionLocalService;
import com.liferay.saml.persistence.service.SamlPeerBindingLocalService;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(
	property = "path=/portal/saml/keep_alive", service = StrutsAction.class
)
public class KeepAliveStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (!_samlProviderConfigurationHelper.isEnabled()) {
			return "/common/referer_js.jsp";
		}

		if (_samlProviderConfigurationHelper.isRoleIdp()) {
			_executeIdpKeepAlive(httpServletRequest, httpServletResponse);
		}
		else if (_samlProviderConfigurationHelper.isRoleSp()) {
			_executeSpKeepAlive(httpServletResponse);
		}

		return null;
	}

	private void _executeIdpKeepAlive(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletResponse.addHeader(
			HttpHeaders.CACHE_CONTROL,
			HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
		httpServletResponse.addHeader(
			HttpHeaders.PRAGMA, HttpHeaders.PRAGMA_NO_CACHE_VALUE);

		httpServletResponse.setContentType(ContentTypes.TEXT_JAVASCRIPT);

		String randomString = StringUtil.randomString();
		PrintWriter printWriter = httpServletResponse.getWriter();

		List<String> keepAliveURLs = _getSPsKeepAliveURLs(httpServletRequest);

		for (String keepAliveURL : keepAliveURLs) {
			keepAliveURL = HttpComponentsUtil.addParameter(
				keepAliveURL, "r", randomString);

			printWriter.write("document.write('<img alt=\"\" src=\"");
			printWriter.write(
				HtmlUtil.escapeJS(HtmlUtil.escapeHREF(keepAliveURL)));
			printWriter.write("\"/>');");
		}
	}

	private void _executeSpKeepAlive(HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletResponse.setHeader(
			HttpHeaders.CACHE_CONTROL,
			HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
		httpServletResponse.setHeader(
			HttpHeaders.PRAGMA, HttpHeaders.PRAGMA_NO_CACHE_VALUE);

		httpServletResponse.setContentType(ContentTypes.IMAGE_GIF);

		OutputStream outputStream = httpServletResponse.getOutputStream();

		outputStream.write(Base64.decode(_BASE64_1X1_GIF));
	}

	private List<String> _getSPsKeepAliveURLs(
			HttpServletRequest httpServletRequest)
		throws Exception {

		String samlSsoSessionId = CookiesManagerUtil.getCookieValue(
			SamlWebKeys.SAML_SSO_SESSION_ID, httpServletRequest);

		SamlIdpSsoSession samlIdpSsoSession =
			_samlIdpSsoSessionLocalService.fetchSamlIdpSso(samlSsoSessionId);

		if (samlIdpSsoSession == null) {
			return Collections.emptyList();
		}

		String entityId = ParamUtil.getString(httpServletRequest, "entityId");

		return TransformUtil.transform(
			_samlIdpSpSessionLocalService.getSamlIdpSpSessions(
				samlIdpSsoSession.getSamlIdpSsoSessionId()),
			samlIdpSpSession -> {
				SamlPeerBinding samlPeerBinding =
					_samlPeerBindingLocalService.getSamlPeerBinding(
						samlIdpSpSession.getSamlPeerBindingId());

				if (entityId.equals(samlPeerBinding.getSamlPeerEntityId())) {
					return null;
				}

				SamlIdpSpConnection samlIdpSpConnection =
					_samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
						samlIdpSpSession.getCompanyId(),
						samlPeerBinding.getSamlPeerEntityId());

				ExpandoBridge expandoBridge =
					samlIdpSpConnection.getExpandoBridge();

				String keepAliveURL = (String)expandoBridge.getAttribute(
					SamlKeepAliveConstants.EXPANDO_COLUMN_NAME_KEEP_ALIVE_URL);

				if (!Validator.isBlank(keepAliveURL) &&
					!keepAliveURL.equals(
						SamlKeepAliveConstants.
							EXPANDO_COLUMN_NAME_KEEP_ALIVE_URL)) {

					return keepAliveURL;
				}

				return null;
			});
	}

	private static final String _BASE64_1X1_GIF =
		"R0lGODdhAQABAIAAAP///////ywAAAAAAQABAAACAkQBADs=";

	@Reference
	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;

	@Reference
	private SamlIdpSpSessionLocalService _samlIdpSpSessionLocalService;

	@Reference
	private SamlIdpSsoSessionLocalService _samlIdpSsoSessionLocalService;

	@Reference
	private SamlPeerBindingLocalService _samlPeerBindingLocalService;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

}