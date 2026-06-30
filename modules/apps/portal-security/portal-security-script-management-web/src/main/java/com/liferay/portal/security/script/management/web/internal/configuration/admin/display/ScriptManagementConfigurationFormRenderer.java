/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.security.script.management.configuration.ScriptManagementConfiguration;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;
import com.liferay.portal.security.script.management.web.internal.display.context.ScriptManagementConfigurationDisplayContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yuri Monteiro
 */
@Component(service = ConfigurationFormRenderer.class)
public class ScriptManagementConfigurationFormRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return ScriptManagementConfiguration.class.getName();
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"allowScriptContentToBeExecutedOrIncluded",
			ParamUtil.getBoolean(
				httpServletRequest, "allowScriptContentToBeExecutedOrIncluded")
		).build();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
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

	@Reference
	private ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.security.script.management.web)"
	)
	private ServletContext _servletContext;

}