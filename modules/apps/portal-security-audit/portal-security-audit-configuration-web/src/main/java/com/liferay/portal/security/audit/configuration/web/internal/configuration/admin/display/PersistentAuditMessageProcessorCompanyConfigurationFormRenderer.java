/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.configuration.web.internal.display.context.PersistentAuditMessageProcessorCompanyConfigurationDisplayContext;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorCompanyConfiguration;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Miranda
 */
@Component(service = ConfigurationFormRenderer.class)
public class PersistentAuditMessageProcessorCompanyConfigurationFormRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return PersistentAuditMessageProcessorCompanyConfiguration.class.
			getName();
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		FeatureFlagManagerUtil.checkEnabled(
			themeDisplay.getCompanyId(), "LPD-114718");

		if (AuditConfigurationOverrideUtil.isOverridden(
				PersistentAuditMessageProcessorCompanyConfiguration.class,
				"enabled")) {

			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"enabled",
			() -> {
				PersistentAuditMessageProcessorCompanyConfiguration
					persistentAuditMessageProcessorCompanyConfiguration =
						_getPersistentAuditMessageProcessorCompanyConfiguration(
							themeDisplay.getCompanyId());

				return ParamUtil.getBoolean(
					httpServletRequest, "enabled",
					persistentAuditMessageProcessorCompanyConfiguration.
						enabled());
			}
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
					"/configuration/persistent_audit_message_processor_" +
						"company_configuration.jsp");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			httpServletRequest.setAttribute(
				PersistentAuditMessageProcessorCompanyConfigurationDisplayContext.class.
					getName(),
				new PersistentAuditMessageProcessorCompanyConfigurationDisplayContext(
					_getPersistentAuditMessageProcessorCompanyConfiguration(
						themeDisplay.getCompanyId()),
					_configurationProvider.getSystemConfiguration(
						PersistentAuditMessageProcessorConfiguration.class)));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render \"persistent_audit_message_processor_" +
					"company_configuration.jsp\"",
				exception);
		}
	}

	private PersistentAuditMessageProcessorCompanyConfiguration
		_getPersistentAuditMessageProcessorCompanyConfiguration(
			long companyId) {

		try {
			Company company = _companyLocalService.getCompany(companyId);

			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					ConfigurationFilterStringUtil.getCompanyScopedFilterString(
						company.getCompanyId(), getPid(), company.getWebId()));

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			if (configurations != null) {
				Configuration configuration = configurations[0];

				properties = configuration.getProcessedProperties(null);
			}

			return ConfigurableUtil.createConfigurable(
				PersistentAuditMessageProcessorCompanyConfiguration.class,
				properties);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.security.audit.configuration.web)"
	)
	private ServletContext _servletContext;

}