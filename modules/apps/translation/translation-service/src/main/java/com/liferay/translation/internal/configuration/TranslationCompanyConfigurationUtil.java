/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.configuration;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;

/**
 * @author Akhash Ramprakash
 */
public class TranslationCompanyConfigurationUtil {

	public static boolean isHTMLInlineCodeProtectionEnabled(long companyId) {
		try {
			TranslationCompanyConfiguration translationCompanyConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					TranslationCompanyConfiguration.class, companyId);

			return translationCompanyConfiguration.
				htmlInlineCodeProtectionEnabled();
		}
		catch (ConfigurationException configurationException) {
			if (_log.isWarnEnabled()) {
				_log.warn(configurationException);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TranslationCompanyConfigurationUtil.class);

}