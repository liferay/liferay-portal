/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.util;

import com.liferay.commerce.pricing.configuration.CommercePricingConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;

/**
 * @author Riccardo Alberti
 */
public class CommercePricingUtil {

	public static String getPricingEngineVersion(
			ConfigurationProvider configurationProvider)
		throws ConfigurationException {

		CommercePricingConfiguration commercePricingConfiguration =
			configurationProvider.getSystemConfiguration(
				CommercePricingConfiguration.class);

		return commercePricingConfiguration.commercePricingCalculationKey();
	}

}