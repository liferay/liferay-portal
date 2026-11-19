/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.configuration.web.internal.display.context.FileSystemAuditMessageProcessorConfigurationDisplayContext;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationScopeUtil;
import com.liferay.portal.security.audit.router.configuration.FileSystemAuditMessageProcessorConfiguration;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Álvaro Saugar
 */
@Component(service = ConfigurationFormRenderer.class)
public class FileSystemAuditMessageProcessorConfigurationFormRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return FileSystemAuditMessageProcessorConfiguration.class.getName();
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		Map<String, Object> requestParameters = new HashMap<>();

		boolean systemScope = AuditConfigurationScopeUtil.isSystemScope(
			httpServletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		FileSystemAuditMessageProcessorConfiguration
			fileSystemAuditMessageProcessorConfiguration =
				_getFileSystemAuditMessageProcessorConfiguration(
					themeDisplay.getCompanyId(), systemScope);

		if (!_isOverridden("enabled")) {
			requestParameters.put(
				"enabled",
				ParamUtil.getBoolean(
					httpServletRequest, "enabled",
					fileSystemAuditMessageProcessorConfiguration.enabled()));
		}

		if (!_isOverridden("generateChecksum")) {
			requestParameters.put(
				"generateChecksum",
				ParamUtil.getBoolean(
					httpServletRequest, "generateChecksum",
					fileSystemAuditMessageProcessorConfiguration.
						generateChecksum()));
		}

		if (!_isOverridden("outputDirectory")) {
			requestParameters.put(
				"outputDirectory",
				ParamUtil.getString(
					httpServletRequest, "outputDirectory",
					fileSystemAuditMessageProcessorConfiguration.
						outputDirectory()));
		}

		if (!_isOverridden("outputFormat")) {
			requestParameters.put(
				"outputFormat",
				ParamUtil.getString(
					httpServletRequest, "outputFormat",
					fileSystemAuditMessageProcessorConfiguration.
						outputFormat()));
		}

		return requestParameters;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/configuration/file_system_audit_message_processor" +
						"_configuration.jsp");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			httpServletRequest.setAttribute(
				FileSystemAuditMessageProcessorConfigurationDisplayContext.
					class.getName(),
				new FileSystemAuditMessageProcessorConfigurationDisplayContext(
					_getFileSystemAuditMessageProcessorConfiguration(
						themeDisplay.getCompanyId(),
						AuditConfigurationScopeUtil.isSystemScope(
							httpServletRequest))));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render " +
					"file_system_audit_message_processor_configuration.jsp",
				exception);
		}
	}

	private FileSystemAuditMessageProcessorConfiguration
		_getFileSystemAuditMessageProcessorConfiguration(
			long companyId, boolean systemScope) {

		try {
			if (systemScope) {
				return _configurationProvider.getSystemConfiguration(
					FileSystemAuditMessageProcessorConfiguration.class);
			}

			return _configurationProvider.getCompanyConfiguration(
				FileSystemAuditMessageProcessorConfiguration.class, companyId);
		}
		catch (ConfigurationException configurationException) {
			return ReflectionUtil.throwException(configurationException);
		}
	}

	private boolean _isOverridden(String key) {
		return AuditConfigurationOverrideUtil.isOverridden(
			FileSystemAuditMessageProcessorConfiguration.class, key);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.security.audit.configuration.web)"
	)
	private ServletContext _servletContext;

}