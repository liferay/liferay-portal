/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Riccardo Alberti
 * @generated
 */
@ProviderType
public interface CommercePricingClassFinder {

	public int countByCPDefinitionId(
		long cpDefinitionId, String title, boolean inlineSQLHelper);

	public java.util.List
		<com.liferay.commerce.pricing.model.CommercePricingClass>
			findByCPDefinitionId(
				long cpDefinitionId, String title, int start, int end,
				boolean inlineSQLHelper);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1040031129