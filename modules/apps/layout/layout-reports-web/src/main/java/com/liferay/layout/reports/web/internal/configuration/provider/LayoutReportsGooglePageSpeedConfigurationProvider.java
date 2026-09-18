/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.reports.web.internal.configuration.provider;

import com.liferay.layout.reports.web.internal.configuration.LayoutReportsGooglePageSpeedCompanyConfiguration;
import com.liferay.layout.reports.web.internal.configuration.LayoutReportsGooglePageSpeedConfiguration;
import com.liferay.layout.reports.web.internal.configuration.LayoutReportsGooglePageSpeedGroupConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	configurationPid = "com.liferay.layout.reports.web.internal.configuration.LayoutReportsGooglePageSpeedConfiguration",
	service = LayoutReportsGooglePageSpeedConfigurationProvider.class
)
public class LayoutReportsGooglePageSpeedConfigurationProvider {

	public String getApiKey(Group group) throws ConfigurationException {
		LayoutReportsGooglePageSpeedGroupConfiguration
			layoutReportsGooglePageSpeedGroupConfiguration =
				_configurationProvider.getGroupConfiguration(
					LayoutReportsGooglePageSpeedGroupConfiguration.class,
					group.getCompanyId(), group.getGroupId());

		return _secretResolver.resolve(
			group.getCompanyId(),
			layoutReportsGooglePageSpeedGroupConfiguration.apiKey());
	}

	public String getStrategy(Group group) throws ConfigurationException {
		LayoutReportsGooglePageSpeedGroupConfiguration
			layoutReportsGooglePageSpeedGroupConfiguration =
				_configurationProvider.getGroupConfiguration(
					LayoutReportsGooglePageSpeedGroupConfiguration.class,
					group.getCompanyId(), group.getGroupId());

		String strategy =
			layoutReportsGooglePageSpeedGroupConfiguration.strategy();

		if (!strategy.equals("DESKTOP") && !strategy.equals("MOBILE")) {
			return "DESKTOP";
		}

		return strategy;
	}

	public boolean isEnabled() {
		return _layoutReportsGooglePageSpeedConfiguration.enabled();
	}

	public boolean isEnabled(Group group) throws ConfigurationException {
		if (!isEnabled(group.getCompanyId())) {
			return false;
		}

		LayoutReportsGooglePageSpeedGroupConfiguration
			layoutReportsGooglePageSpeedGroupConfiguration =
				_configurationProvider.getGroupConfiguration(
					LayoutReportsGooglePageSpeedGroupConfiguration.class,
					group.getCompanyId(), group.getGroupId());

		return layoutReportsGooglePageSpeedGroupConfiguration.enabled();
	}

	public boolean isEnabled(long companyId) throws ConfigurationException {
		if (!_layoutReportsGooglePageSpeedConfiguration.enabled()) {
			return false;
		}

		LayoutReportsGooglePageSpeedCompanyConfiguration
			layoutReportsGooglePageSpeedCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					LayoutReportsGooglePageSpeedCompanyConfiguration.class,
					companyId);

		return layoutReportsGooglePageSpeedCompanyConfiguration.enabled();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_layoutReportsGooglePageSpeedConfiguration =
			ConfigurableUtil.createConfigurable(
				LayoutReportsGooglePageSpeedConfiguration.class, properties);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	private volatile LayoutReportsGooglePageSpeedConfiguration
		_layoutReportsGooglePageSpeedConfiguration;

	@Reference
	private SecretResolver _secretResolver;

}