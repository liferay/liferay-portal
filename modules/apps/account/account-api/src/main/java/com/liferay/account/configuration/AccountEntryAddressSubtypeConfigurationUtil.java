/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.configuration;

import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;

import java.util.Objects;

/**
 * @author Mátyás Wollner
 */
public class AccountEntryAddressSubtypeConfigurationUtil {

	public static String
		getAddressSubtypeListTypeDefinitionExternalReferenceCode(
			long companyId, String name) {

		try {
			AccountEntryAddressSubtypeConfiguration
				accountEntryAddressSubtypeConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						AccountEntryAddressSubtypeConfiguration.class,
						companyId);

			if (Objects.equals(
					name,
					AccountListTypeConstants.
						ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING)) {

				return accountEntryAddressSubtypeConfiguration.
					billingAddressSubtypeListTypeDefinitionExternalReferenceCode();
			}
			else if (Objects.equals(
						name,
						AccountListTypeConstants.
							ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING)) {

				return accountEntryAddressSubtypeConfiguration.
					billingAndShippingAddressSubtypeListTypeDefinitionExternalReferenceCode();
			}
			else if (Objects.equals(
						name,
						AccountListTypeConstants.
							ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING)) {

				return accountEntryAddressSubtypeConfiguration.
					shippingAddressSubtypeListTypeDefinitionExternalReferenceCode();
			}
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountEntryAddressSubtypeConfigurationUtil.class);

}