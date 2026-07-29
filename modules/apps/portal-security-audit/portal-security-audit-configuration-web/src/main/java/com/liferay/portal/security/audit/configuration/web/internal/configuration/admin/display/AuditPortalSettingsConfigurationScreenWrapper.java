/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.web.internal.display.context.AuditConfigurationDisplayContext;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Christian Moura
 */
public class AuditPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	public AuditPortalSettingsConfigurationScreenWrapper(
		ConfigurationProvider configurationProvider, Language language,
		PortalSettingsConfigurationScreenFactory
			portalSettingsConfigurationScreenFactory,
		ExtendedObjectClassDefinition.Scope scope,
		ServletContext servletContext) {

		_configurationProvider = configurationProvider;
		_language = language;
		_portalSettingsConfigurationScreenFactory =
			portalSettingsConfigurationScreenFactory;
		_scope = scope;
		_servletContext = servletContext;
	}

	@Override
	public String getScope() {
		return _scope.getValue();
	}

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new AuditPortalSettingsConfigurationScreenContributor());
	}

	private final ConfigurationProvider _configurationProvider;
	private final Language _language;
	private final PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;
	private final ExtendedObjectClassDefinition.Scope _scope;
	private final ServletContext _servletContext;

	private class AuditPortalSettingsConfigurationScreenContributor
		implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "audit";
		}

		@Override
		public String getJspPath() {
			return "/configuration/view.jsp";
		}

		@Override
		public String getKey() {
			return StringBundler.concat(
				AuditConfiguration.class.getName(), StringPool.POUND,
				getScope());
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "audit-configuration-name");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/portal_security_audit_configuration" +
				"/save_audit_configuration";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public boolean isVisible() {
			if (ExtendedObjectClassDefinition.Scope.COMPANY.equals(_scope)) {
				return FeatureFlagManagerUtil.isEnabled(
					CompanyThreadLocal.getCompanyId(), "LPD-6417");
			}

			return true;
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			httpServletRequest.setAttribute(
				AuditConfigurationDisplayContext.class.getName(),
				new AuditConfigurationDisplayContext(
					_getAuditConfiguration(),
					ExtendedObjectClassDefinition.Scope.SYSTEM.equals(_scope) &&
					!FeatureFlagManagerUtil.isEnabled(
						CompanyThreadLocal.getCompanyId(), "LPD-6417")));
		}

		private AuditConfiguration _getAuditConfiguration() {
			try {
				if (ExtendedObjectClassDefinition.Scope.COMPANY.equals(
						_scope)) {

					return _configurationProvider.getCompanyConfiguration(
						AuditConfiguration.class,
						CompanyThreadLocal.getCompanyId());
				}

				return _configurationProvider.getSystemConfiguration(
					AuditConfiguration.class);
			}
			catch (ConfigurationException configurationException) {
				return ReflectionUtil.throwException(configurationException);
			}
		}

	}

}