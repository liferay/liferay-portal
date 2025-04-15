/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.token.web.internal.portlet.action;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.token.constants.TokenConstants;
import com.liferay.portal.settings.authentication.token.web.internal.constants.PortalSettingsTokenConstants;
import com.liferay.portal.settings.portlet.action.PortalSettingsFormContributor;
import com.liferay.portal.settings.portlet.action.PortalSettingsParameterUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Stian Sigvartsen
 */
@Component(service = PortalSettingsFormContributor.class)
public class TokenPortalSettingsFormContributor
	implements PortalSettingsFormContributor {

	@Override
	public String getDeleteMVCActionCommandName() {
		return "/portal_settings/token_delete";
	}

	@Override
	public String getParameterNamespace() {
		return PortalSettingsTokenConstants.FORM_PARAMETER_NAMESPACE;
	}

	@Override
	public String getSaveMVCActionCommandName() {
		return "/portal_settings/token";
	}

	@Override
	public String getSettingsId() {
		return TokenConstants.SERVICE_NAME;
	}

	@Override
	public void validateForm(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		boolean tokenEnabled = PortalSettingsParameterUtil.getBoolean(
			actionRequest, this, "enabled");

		if (!tokenEnabled) {
			return;
		}

		String logoutRedirectURL = PortalSettingsParameterUtil.getString(
			actionRequest, this, "logoutRedirectURL");

		if (Validator.isNotNull(logoutRedirectURL) &&
			!Validator.isUrl(logoutRedirectURL, true)) {

			SessionErrors.add(actionRequest, "logoutRedirectURLInvalid");
		}
	}

}