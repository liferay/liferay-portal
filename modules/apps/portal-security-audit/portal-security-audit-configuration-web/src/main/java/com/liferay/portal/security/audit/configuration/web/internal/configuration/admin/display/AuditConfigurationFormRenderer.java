/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.web.internal.display.context.AuditConfigurationDisplayContext;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationThreadLocal;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;

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

		boolean systemScope = _isSystemScope(httpServletRequest);
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

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
		}
		else {
			FeatureFlagManagerUtil.checkEnabled(
				themeDisplay.getCompanyId(), "LPD-6417");
		}

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-6417") &&
			!AuditConfigurationOverrideUtil.isOverridden(
				AuditConfiguration.class, "pseudonymizationEnabled")) {

			boolean pseudonymizationEnabled = ParamUtil.getBoolean(
				httpServletRequest, "pseudonymizationEnabled",
				auditConfiguration.pseudonymizationEnabled());

			requestParameters.put(
				"pseudonymizationEnabled", pseudonymizationEnabled);

			if (pseudonymizationEnabled !=
					auditConfiguration.pseudonymizationEnabled()) {

				AuditConfigurationThreadLocal.setAuditMessage(
					_getAuditMessage(
						auditConfiguration.pseudonymizationEnabled(),
						pseudonymizationEnabled, themeDisplay));
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
			boolean systemScope = _isSystemScope(httpServletRequest);
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			httpServletRequest.setAttribute(
				AuditConfigurationDisplayContext.class.getName(),
				new AuditConfigurationDisplayContext(
					_getAuditConfiguration(
						themeDisplay.getCompanyId(), systemScope),
					systemScope));

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/configuration/audit_configuration.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render \"audit_configuration.jsp\"", exception);
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

	private AuditMessage _getAuditMessage(
		boolean previousPseudonymizationEnabled,
		boolean pseudonymizationEnabled, ThemeDisplay themeDisplay) {

		User user = themeDisplay.getUser();

		return new AuditMessage(
			themeDisplay.getCompanyId(), themeDisplay.getUserId(),
			user.getFullName(),
			JSONUtil.put(
				"attributes",
				JSONUtil.putAll(
					JSONUtil.put(
						"name", "pseudonymizationEnabled"
					).put(
						"newValue", pseudonymizationEnabled
					).put(
						"oldValue", previousPseudonymizationEnabled
					))),
			AuditConfiguration.class.getName(),
			String.valueOf(themeDisplay.getCompanyId()),
			EventTypes.AUDIT_CONFIG_CHANGE, null);
	}

	private boolean _isSystemScope(HttpServletRequest httpServletRequest) {
		return ConfigurationAdminPortletKeys.SYSTEM_SETTINGS.equals(
			_portal.getPortletId(httpServletRequest));
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.security.audit.configuration.web)"
	)
	private ServletContext _servletContext;

}