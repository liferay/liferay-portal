/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.facebook.connect.web.internal.portlet.action;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.facebook.connect.constants.FacebookConnectConstants;
import com.liferay.portal.settings.authentication.facebook.connect.web.internal.constants.PortalSettingsFacebookConnectConstants;
import com.liferay.portal.settings.portlet.action.PortalSettingsFormContributor;
import com.liferay.portal.settings.portlet.action.PortalSettingsParameterUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 * @author Stian Sigvartsen
 */
@Component(service = PortalSettingsFormContributor.class)
public class FacebookConnectPortalSettingsFormContributor
	implements PortalSettingsFormContributor {

	@Override
	public String getDeleteMVCActionCommandName() {
		return "/portal_settings/facebook_connect_delete";
	}

	@Override
	public String getParameterNamespace() {
		return PortalSettingsFacebookConnectConstants.FORM_PARAMETER_NAMESPACE;
	}

	@Override
	public String getSaveMVCActionCommandName() {
		return "/portal_settings/facebook_connect";
	}

	@Override
	public String getSettingsId() {
		return FacebookConnectConstants.SERVICE_NAME;
	}

	@Override
	public void validateForm(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		boolean facebookEnabled = PortalSettingsParameterUtil.getBoolean(
			actionRequest, this, "enabled");

		if (!facebookEnabled) {
			return;
		}

		String facebookGraphURL = PortalSettingsParameterUtil.getString(
			actionRequest, this, "graphURL");
		String facebookOauthAuthURL = PortalSettingsParameterUtil.getString(
			actionRequest, this, "oauthAuthURL");
		String facebookOauthRedirectURL = PortalSettingsParameterUtil.getString(
			actionRequest, this, "oauthRedirectURL");
		String facebookOauthTokenURL = PortalSettingsParameterUtil.getString(
			actionRequest, this, "oauthTokenURL");

		if (Validator.isNotNull(facebookGraphURL) &&
			!Validator.isUrl(facebookGraphURL)) {

			SessionErrors.add(actionRequest, "facebookConnectGraphURLInvalid");
		}

		if (Validator.isNotNull(facebookOauthAuthURL) &&
			!Validator.isUrl(facebookOauthAuthURL)) {

			SessionErrors.add(
				actionRequest, "facebookConnectOauthAuthURLInvalid");
		}

		if (Validator.isNotNull(facebookOauthRedirectURL) &&
			!Validator.isUrl(facebookOauthRedirectURL)) {

			SessionErrors.add(
				actionRequest, "facebookConnectOauthRedirectURLInvalid");
		}

		if (Validator.isNotNull(facebookOauthTokenURL) &&
			!Validator.isUrl(facebookOauthTokenURL)) {

			SessionErrors.add(
				actionRequest, "facebookConnectOauthTokenURLInvalid");
		}
	}

}