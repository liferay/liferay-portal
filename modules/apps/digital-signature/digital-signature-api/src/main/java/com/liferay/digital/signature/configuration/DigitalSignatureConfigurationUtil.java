/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.configuration;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;

/**
 * @author José Abelenda
 */
public class DigitalSignatureConfigurationUtil {

	public static DigitalSignatureConfiguration
		getDigitalSignatureConfiguration(long companyId, long groupId) {

		try {
			DigitalSignatureConfiguration companyDigitalSignatureConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					DigitalSignatureConfiguration.class, companyId);

			if ((groupId == 0) ||
				Objects.equals(
					companyDigitalSignatureConfiguration.siteSettingsStrategy(),
					"always-inherit")) {

				return companyDigitalSignatureConfiguration;
			}

			DigitalSignatureConfiguration groupDigitalSignatureConfiguration =
				ConfigurationProviderUtil.getGroupConfiguration(
					DigitalSignatureConfiguration.class, companyId, groupId);

			if (Objects.equals(
					companyDigitalSignatureConfiguration.siteSettingsStrategy(),
					"always-override")) {

				return groupDigitalSignatureConfiguration;
			}

			if (Objects.equals(
					companyDigitalSignatureConfiguration.siteSettingsStrategy(),
					"inherit-or-override")) {

				if (Validator.isNotNull(
						groupDigitalSignatureConfiguration.apiUsername()) &&
					Validator.isNotNull(
						groupDigitalSignatureConfiguration.apiAccountId()) &&
					Validator.isNotNull(
						groupDigitalSignatureConfiguration.accountBaseURI()) &&
					Validator.isNotNull(
						groupDigitalSignatureConfiguration.environment()) &&
					Validator.isNotNull(
						groupDigitalSignatureConfiguration.integrationKey()) &&
					Validator.isNotNull(
						groupDigitalSignatureConfiguration.rsaPrivateKey())) {

					return groupDigitalSignatureConfiguration;
				}

				return companyDigitalSignatureConfiguration;
			}

			return companyDigitalSignatureConfiguration;
		}
		catch (ConfigurationException configurationException) {
			return ReflectionUtil.throwException(configurationException);
		}
	}

	public static boolean isEmbeddedViewEnabled(long companyId, long groupId) {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-69290")) {
			return false;
		}

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			getDigitalSignatureConfiguration(companyId, groupId);

		return digitalSignatureConfiguration.embeddedViewEnabled();
	}

}