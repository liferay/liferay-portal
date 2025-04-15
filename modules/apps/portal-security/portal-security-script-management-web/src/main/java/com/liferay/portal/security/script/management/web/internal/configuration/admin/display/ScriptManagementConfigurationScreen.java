/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;
import com.liferay.portal.security.script.management.web.internal.display.context.ScriptManagementConfigurationDisplayContext;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = ConfigurationScreen.class)
public class ScriptManagementConfigurationScreen
	implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "script-management";
	}

	@Override
	public String getKey() {
		return "script-management";
	}

	@Override
	public String getName(Locale locale) {
		return _language.get(locale, "script-management");
	}

	@Override
	public String getScope() {
		return ExtendedObjectClassDefinition.Scope.SYSTEM.getValue();
	}

	@Override
	public boolean isVisible() {
		return PropsValues.SCRIPT_MANAGEMENT_CONFIGURATION_ENABLED;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(
					"/configuration/script_management_configuration.jsp");

			httpServletRequest.setAttribute(
				ScriptManagementConfigurationDisplayContext.class.getName(),
				new ScriptManagementConfigurationDisplayContext(
					_scriptManagementConfigurationHelper));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render script_management_configuration.jsp",
				exception);
		}
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.security.script.management.web)"
	)
	protected ServletContext servletContext;

	@Reference
	private Language _language;

	@Reference
	private ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

}