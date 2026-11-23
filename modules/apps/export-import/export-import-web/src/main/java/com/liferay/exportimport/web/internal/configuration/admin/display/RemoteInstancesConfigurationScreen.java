/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = ConfigurationScreen.class)
public class RemoteInstancesConfigurationScreen implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "infrastructure";
	}

	@Override
	public String getKey() {
		return "remote-environments";
	}

	@Override
	public String getName(Locale locale) {
		return _language.get(locale, "remote-environments");
	}

	@Override
	public String getScope() {
		return ExtendedObjectClassDefinition.Scope.COMPANY.getValue();
	}

	@Override
	public boolean isVisible() {
		return FeatureFlagManagerUtil.isEnabled(
			CompanyThreadLocal.getCompanyId(), "LPD-71352");
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/revamp/remote_instances/view.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render /revamp/remote_instances/view.jsp",
				exception);
		}
	}

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.exportimport.web)")
	private ServletContext _servletContext;

}