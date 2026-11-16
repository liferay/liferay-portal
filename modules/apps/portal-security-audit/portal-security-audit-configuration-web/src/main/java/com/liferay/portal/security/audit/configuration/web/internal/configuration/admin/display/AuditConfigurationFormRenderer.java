/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.web.internal.display.context.AuditConfigurationDisplayContext;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationFormThreadLocal;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationScopeUtil;

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
public class AuditConfigurationFormRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return AuditConfiguration.class.getName();
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		Map<String, Object> requestParameters = new HashMap<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		boolean systemScope = AuditConfigurationScopeUtil.isSystemScope(
			httpServletRequest);

		if (!systemScope) {
			FeatureFlagManagerUtil.checkEnabled(
				themeDisplay.getCompanyId(), "LPD-6417");
		}

		AuditConfiguration auditConfiguration = _getAuditConfiguration(
			themeDisplay.getCompanyId(), systemScope);

		if (systemScope) {
			if (!AuditConfigurationOverrideUtil.isOverridden(
					AuditConfiguration.class, "enabled")) {

				requestParameters.put(
					"enabled",
					ParamUtil.getBoolean(
						httpServletRequest, "enabled",
						auditConfiguration.enabled()));
			}

			if (!FeatureFlagManagerUtil.isEnabled(
					themeDisplay.getCompanyId(), "LPD-6417") &&
				!AuditConfigurationOverrideUtil.isOverridden(
					AuditConfiguration.class, "auditMessageMaxQueueSize")) {

				requestParameters.put(
					"auditMessageMaxQueueSize",
					ParamUtil.getInteger(
						httpServletRequest, "auditMessageMaxQueueSize",
						_getAuditMessageMaxQueueSize(auditConfiguration)));
			}
		}

		if (!AuditConfigurationOverrideUtil.isOverridden(
				AuditConfiguration.class, "pseudonymizationEnabled")) {

			requestParameters.put(
				"pseudonymizationEnabled",
				ParamUtil.getBoolean(
					httpServletRequest, "pseudonymizationEnabled",
					auditConfiguration.pseudonymizationEnabled()));

			if (FeatureFlagManagerUtil.isEnabled(
					themeDisplay.getCompanyId(), "LPD-6417")) {

				AuditConfigurationFormThreadLocal.
					setPreviousPseudonymizationEnabled(
						auditConfiguration.pseudonymizationEnabled());
			}
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
					"/configuration/audit_configuration.jsp");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			boolean systemScope = AuditConfigurationScopeUtil.isSystemScope(
				httpServletRequest);

			httpServletRequest.setAttribute(
				AuditConfigurationDisplayContext.class.getName(),
				new AuditConfigurationDisplayContext(
					_getAuditConfiguration(
						themeDisplay.getCompanyId(), systemScope),
					systemScope &&
					!FeatureFlagManagerUtil.isEnabled(
						themeDisplay.getCompanyId(), "LPD-6417"),
					systemScope));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render audit_configuration.jsp", exception);
		}
	}

	private AuditConfiguration _getAuditConfiguration(
		long companyId, boolean systemScope) {

		try {
			if (systemScope) {
				return _configurationProvider.getSystemConfiguration(
					AuditConfiguration.class);
			}

			return _configurationProvider.getCompanyConfiguration(
				AuditConfiguration.class, companyId);
		}
		catch (ConfigurationException configurationException) {
			return ReflectionUtil.throwException(configurationException);
		}
	}

	@SuppressWarnings("deprecation")
	private int _getAuditMessageMaxQueueSize(
		AuditConfiguration auditConfiguration) {

		return auditConfiguration.auditMessageMaxQueueSize();
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.security.audit.configuration.web)"
	)
	private ServletContext _servletContext;

}