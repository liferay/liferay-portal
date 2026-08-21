/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.HashMapDictionary;

/**
 * @author Christian Moura
 */
public class AuditConfigurationUtil {

	public static long getCompanyId(long companyId) {
		if ((companyId > CompanyConstants.SYSTEM) &&
			FeatureFlagManagerUtil.isEnabled(companyId, "LPD-6417")) {

			return companyId;
		}

		return CompanyConstants.SYSTEM;
	}

	public static <T> T getConfiguration(Class<T> clazz, long companyId) {
		return _getConfiguration(clazz, companyId, getCompanyId(companyId));
	}

	public static <T> T getScopedConfiguration(
		Class<T> clazz, long configurationCompanyId) {

		return _getConfiguration(
			clazz, configurationCompanyId, configurationCompanyId);
	}

	public static boolean isEnabled(long companyId) {
		AuditConfiguration auditConfiguration = getConfiguration(
			AuditConfiguration.class, companyId);

		return auditConfiguration.enabled();
	}

	private static <T> T _getConfiguration(
		Class<T> clazz, long companyId, long configurationCompanyId) {

		try {
			if (configurationCompanyId == CompanyConstants.SYSTEM) {
				return ConfigurationProviderUtil.getSystemConfiguration(clazz);
			}

			return ConfigurationProviderUtil.getCompanyConfiguration(
				clazz, configurationCompanyId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to get the configuration ", clazz.getName(),
						" for company ", companyId),
					exception);
			}
		}

		return ConfigurableUtil.createConfigurable(
			clazz, new HashMapDictionary<>());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuditConfigurationUtil.class);

}