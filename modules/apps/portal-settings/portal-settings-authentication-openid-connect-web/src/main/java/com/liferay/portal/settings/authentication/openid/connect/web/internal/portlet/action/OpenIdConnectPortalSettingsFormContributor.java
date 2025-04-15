/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.openid.connect.web.internal.portlet.action;

import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectConstants;
import com.liferay.portal.settings.authentication.openid.connect.web.internal.constants.PortalSettingsOpenIdConnectConstants;
import com.liferay.portal.settings.portlet.action.PortalSettingsFormContributor;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Edward C. Han
 */
@Component(service = PortalSettingsFormContributor.class)
public class OpenIdConnectPortalSettingsFormContributor
	implements PortalSettingsFormContributor {

	@Override
	public String getDeleteMVCActionCommandName() {
		return "/portal_settings/openid_connect_delete";
	}

	@Override
	public String getParameterNamespace() {
		return PortalSettingsOpenIdConnectConstants.FORM_PARAMETER_NAMESPACE;
	}

	@Override
	public String getSaveMVCActionCommandName() {
		return "/portal_settings/openid_connect";
	}

	@Override
	public String getSettingsId() {
		return OpenIdConnectConstants.SERVICE_NAME;
	}

	@Override
	public void validateForm(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {
	}

}