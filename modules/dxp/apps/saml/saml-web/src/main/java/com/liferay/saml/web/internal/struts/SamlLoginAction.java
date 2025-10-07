/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.struts;

import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.servlet.profile.SamlSpIdpConnectionsProfile;
import com.liferay.saml.util.JspUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(property = "path=/portal/saml/login", service = StrutsAction.class)
public class SamlLoginAction extends BaseSamlStrutsAction {

	@Override
	public boolean isEnabled(HttpServletRequest httpServletRequest) {
		if (!_samlProviderConfigurationHelper.isRoleIdp()) {
			return _samlProviderConfigurationHelper.isEnabled();
		}

		return false;
	}

	@Override
	protected String doExecute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String entityId = ParamUtil.getString(
			httpServletRequest, "idpEntityId");

		long companyId = _portal.getCompanyId(httpServletRequest);

		if (Validator.isNotNull(entityId)) {
			httpServletRequest.setAttribute(
				SamlWebKeys.SAML_SP_IDP_CONNECTION,
				_samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
					companyId, entityId));

			if (GetterUtil.getBoolean(
					ParamUtil.getBoolean(httpServletRequest, "forceAuthn"))) {

				AuthTokenUtil.checkCSRFToken(
					httpServletRequest, SamlLoginAction.class.getName());

				httpServletRequest.setAttribute(
					SamlWebKeys.FORCE_REAUTHENTICATION, Boolean.TRUE);
			}

			return null;
		}

		List<SamlSpIdpConnection> samlSpIdpConnections = ListUtil.filter(
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnections(companyId),
			samlSpIdpConnection -> isEnabled(
				samlSpIdpConnection, httpServletRequest));

		if (samlSpIdpConnections.isEmpty()) {
			SamlProviderConfiguration samlProviderConfiguration =
				_samlProviderConfigurationHelper.getSamlProviderConfiguration();

			if (samlProviderConfiguration.allowShowingTheLoginPortlet()) {
				return null;
			}
		}

		boolean samlIdpRedirectMessageEnabled = GetterUtil.getBoolean(
			PropsUtil.get("saml.idp.redirect.message.enabled"), true);

		httpServletRequest.setAttribute(
			SamlWebKeys.SAML_SSO_LOGIN_CONTEXT,
			_toJSONObject(samlSpIdpConnections));

		if (samlIdpRedirectMessageEnabled) {
			httpServletRequest.setAttribute(
				SamlWebKeys.SAML_IDP_REDIRECT_MESSAGE,
				_language.get(
					httpServletRequest,
					"redirecting-to-your-identity-provider"));
		}
		else if (samlSpIdpConnections.size() == 1) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Company company = themeDisplay.getCompany();

			JspUtil.dispatch(
				httpServletRequest, httpServletResponse,
				"/portal/saml/select_idp.jsp",
				_layoutSEOLinkManager.getFullPageTitle(
					themeDisplay.getLayout(), null, null, null, null,
					company.getName(), themeDisplay.getLocale()),
				true);

			return null;
		}

		JspUtil.dispatch(
			httpServletRequest, httpServletResponse,
			"/portal/saml/select_idp.jsp",
			"please-select-your-identity-provider",
			!samlIdpRedirectMessageEnabled);

		return null;
	}

	protected boolean isEnabled(
		SamlSpIdpConnection samlSpIdpConnection,
		HttpServletRequest httpServletRequest) {

		SamlSpIdpConnectionsProfile samlSpIdpConnectionsProfile =
			_samlSpIdpConnectionsProfileSnapshot.get();

		if (samlSpIdpConnectionsProfile != null) {
			return samlSpIdpConnectionsProfile.isEnabled(
				samlSpIdpConnection, httpServletRequest);
		}

		return samlSpIdpConnection.isEnabled();
	}

	private JSONObject _toJSONObject(
		List<SamlSpIdpConnection> samlSpIdpConnections) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (SamlSpIdpConnection samlSpIdpConnection : samlSpIdpConnections) {
			jsonArray.put(
				JSONUtil.put(
					"enabled", samlSpIdpConnection.isEnabled()
				).put(
					"entityId", samlSpIdpConnection.getSamlIdpEntityId()
				).put(
					"name", samlSpIdpConnection.getName()
				));
		}

		return JSONUtil.put("relevantIdpConnections", jsonArray);
	}

	private static final Snapshot<SamlSpIdpConnectionsProfile>
		_samlSpIdpConnectionsProfileSnapshot = new Snapshot<>(
			SamlLoginAction.class, SamlSpIdpConnectionsProfile.class, null,
			true);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	@Reference
	private Portal _portal;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

}