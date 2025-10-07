/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.struts;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = "path=/portal/saml/auth_redirect", service = StrutsAction.class
)
public class AuthRedirectAction extends BaseSamlStrutsAction {

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

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		redirect = _portal.escapeRedirect(redirect);

		if (Validator.isNull(redirect)) {
			redirect = _portal.getHomeURL(httpServletRequest);
		}

		try {
			httpServletResponse.sendRedirect(redirect);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}

		return null;
	}

	@Reference
	private Portal _portal;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

}