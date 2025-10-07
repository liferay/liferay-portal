/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.struts;

import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.servlet.profile.WebSsoProfile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(property = "path=/portal/saml/sso", service = StrutsAction.class)
public class WebSsoAction extends BaseSamlStrutsAction {

	@Override
	public boolean isEnabled(HttpServletRequest httpServletRequest) {
		if (!_samlProviderConfigurationHelper.isRoleSp()) {
			return _samlProviderConfigurationHelper.isEnabled();
		}

		return false;
	}

	@Override
	protected String doExecute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		_webSsoProfile.processAuthnRequest(
			httpServletRequest, httpServletResponse);

		return null;
	}

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference(unbind = "-")
	private WebSsoProfile _webSsoProfile;

}