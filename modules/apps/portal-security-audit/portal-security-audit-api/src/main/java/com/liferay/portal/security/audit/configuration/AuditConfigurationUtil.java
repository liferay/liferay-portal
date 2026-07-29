/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;

/**
 * @author Christian Moura
 */
public class AuditConfigurationUtil {

	public static boolean isEnabled(long companyId) {
		try {
			AuditConfiguration auditConfiguration;

			if ((companyId == CompanyConstants.SYSTEM) ||
				!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-6417")) {

				auditConfiguration =
					ConfigurationProviderUtil.getSystemConfiguration(
						AuditConfiguration.class);
			}
			else {
				auditConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						AuditConfiguration.class, companyId);
			}

			return auditConfiguration.enabled();
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get the audit configuration for company " +
					companyId,
				exception);
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuditConfigurationUtil.class);

}