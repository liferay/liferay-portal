/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration.util;

import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.configuration.admin.util.ConfigurationPidUtil;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.internal.configuration.VulcanCompanyConfiguration;
import com.liferay.portal.vulcan.internal.configuration.VulcanConfiguration;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Javier Gamarra
 */
public class ConfigurationUtil {

	public static Configuration fetchConfiguration(
		Configuration[] configurations) {

		if (ArrayUtil.isEmpty(configurations)) {
			return null;
		}

		for (Configuration configuration : configurations) {
			if (Objects.equals(
					configuration.getFactoryPid(),
					VulcanCompanyConfiguration.class.getName())) {

				return configuration;
			}
		}

		return configurations[0];
	}

	public static Set<String> getExcludedOperationIds(
		long companyId, ConfigurationAdmin configurationAdmin, String path) {

		try {
			String filterString = String.format(
				"(&(path=%s)(|(service.factoryPid=%s)" +
					"(&(service.factoryPid=%s)(%s=%d))))",
				path, VulcanConfiguration.class.getName(),
				VulcanCompanyConfiguration.class.getName(),
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
				companyId);

			Configuration[] configurations =
				configurationAdmin.listConfigurations(filterString);

			Configuration configuration = fetchConfiguration(configurations);

			if (configuration != null) {
				return _getExcludedOperationIds(configuration);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return new HashSet<>();
	}

	public static boolean isRESTEnabled(
		Company company, ConfigurationAdmin configurationAdmin, String path) {

		try {
			Configuration[] configurations =
				configurationAdmin.listConfigurations(
					_getVulcanConfigurationFilterString(company, path));

			Boolean companyRESTEnabled = _isRESTEnabled(true, configurations);

			if (companyRESTEnabled != null) {
				return companyRESTEnabled;
			}

			Boolean systemRESTEnabled = _isRESTEnabled(false, configurations);

			if (systemRESTEnabled != null) {
				return systemRESTEnabled;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return true;
	}

	private static Set<String> _getExcludedOperationIds(
		Configuration configuration) {

		Dictionary<String, Object> properties = configuration.getProperties();

		String excludedOperationIds = GetterUtil.getString(
			properties.get("excludedOperationIds"));

		return SetUtil.fromArray(excludedOperationIds.split(","));
	}

	private static String _getVulcanConfigurationFilterString(
		Company company, String path) {

		return String.format(
			"(&(path=%s)(|(service.factoryPid=%s)%s))",
			SystemBundleUtil.escapeFilterValue(path),
			VulcanConfiguration.class.getName(),
			ConfigurationFilterStringUtil.getCompanyScopedFilterString(
				company.getCompanyId(),
				VulcanCompanyConfiguration.class.getName(),
				company.getWebId()));
	}

	private static boolean _isCompanyFactoryPid(String factoryPid) {
		if (factoryPid == null) {
			return false;
		}

		return Objects.equals(
			ConfigurationPidUtil.getRawPid(factoryPid),
			VulcanCompanyConfiguration.class.getName());
	}

	private static Boolean _isRESTEnabled(
		boolean companyScope, Configuration[] configurations) {

		if (configurations == null) {
			return null;
		}

		for (Configuration configuration : configurations) {
			if (_isCompanyFactoryPid(configuration.getFactoryPid()) !=
					companyScope) {

				continue;
			}

			Dictionary<String, Object> properties =
				configuration.getProperties();

			Object restEnabled = properties.get("restEnabled");

			if (restEnabled != null) {
				return GetterUtil.getBoolean(restEnabled);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationUtil.class);

}