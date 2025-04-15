/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.struts;

import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.servlet.profile.SingleLogoutProfile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(
	property = {
		"path=/portal/saml/slo", "path=/portal/saml/slo_logout",
		"path=/portal/saml/slo_soap"
	},
	service = StrutsAction.class
)
public class SingleLogoutAction extends BaseSamlStrutsAction {

	@Override
	public boolean isEnabled() {
		return _samlProviderConfigurationHelper.isEnabled();
	}

	@Override
	protected String doExecute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String requestURI = httpServletRequest.getRequestURI();

		if (_samlProviderConfigurationHelper.isRoleIdp() &&
			requestURI.endsWith("/slo_logout")) {

			_singleLogoutProfile.processIdpLogout(
				httpServletRequest, httpServletResponse);
		}
		else {
			_singleLogoutProfile.processSingleLogout(
				httpServletRequest, httpServletResponse);
		}

		return null;
	}

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private SingleLogoutProfile _singleLogoutProfile;

}