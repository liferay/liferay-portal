/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.opensso.web.internal.portlet.action;

import com.liferay.portal.security.sso.opensso.constants.OpenSSOConstants;
import com.liferay.portal.settings.authentication.opensso.web.internal.constants.PortalSettingsOpenSSOConstants;
import com.liferay.portal.settings.portlet.action.PortalSettingsFormContributor;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Philip Jones
 */
@Component(service = PortalSettingsFormContributor.class)
public class OpenSSOPortalSettingsFormContributor
	implements PortalSettingsFormContributor {

	@Override
	public String getDeleteMVCActionCommandName() {
		return "/portal_settings/opensso_delete";
	}

	@Override
	public String getParameterNamespace() {
		return PortalSettingsOpenSSOConstants.FORM_PARAMETER_NAMESPACE;
	}

	@Override
	public String getSaveMVCActionCommandName() {
		return "/portal_settings/opensso";
	}

	@Override
	public String getSettingsId() {
		return OpenSSOConstants.SERVICE_NAME;
	}

	@Override
	public void validateForm(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {
	}

}