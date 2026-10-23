/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Marco Leo
 * @generated
 */
@ProviderType
public interface CommerceDiscountAccountRelFinder {

	public int countByCommerceDiscountId(
		long commerceDiscountId, String name, boolean inlineSQLHelper);

	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountAccountRel>
			findByCommerceDiscountId(
				long commerceDiscountId, String name, int start, int end,
				boolean inlineSQLHelper);

}
// LIFERAY-SERVICE-BUILDER-HASH:733338383