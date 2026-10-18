/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@ProviderType
public interface CommercePriceListFinder {

	public int countByCommercePricingClassId(
		long commercePricingClassId, String name, boolean inlineSQLHelper);

	public java.util.List
		<com.liferay.commerce.price.list.model.CommercePriceList>
			findByExpirationDate(
				java.util.Date expirationDate,
				com.liferay.portal.kernel.dao.orm.QueryDefinition
					<com.liferay.commerce.price.list.model.CommercePriceList>
						queryDefinition);

	public java.util.List
		<com.liferay.commerce.price.list.model.CommercePriceList>
			findByCommercePricingClassId(
				long commercePricingClassId, String name, int start, int end,
				boolean inlineSQLHelper);

}
// LIFERAY-SERVICE-BUILDER-HASH:-2106466116