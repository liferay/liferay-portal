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
public interface CommercePriceModifierRelFinder {

	public int countCategoriesByCommercePriceModifierId(
		long commercePriceModifierId, String name, boolean inlineSQLHelper);

	public int countCPDefinitionsByCommercePriceModifierId(
		long commercePriceModifierId, String name, String languageId,
		boolean inlineSQLHelper);

	public int countPricingClassesByCommercePriceModifierId(
		long commercePriceModifierId, String title, boolean inlineSQLHelper);

	public java.util.List
		<com.liferay.commerce.pricing.model.CommercePriceModifierRel>
			findCategoriesByCommercePriceModifierId(
				long commercePriceModifierId, String name, int start, int end,
				boolean inlineSQLHelper);

	public java.util.List
		<com.liferay.commerce.pricing.model.CommercePriceModifierRel>
			findCPDefinitionsByCommercePriceModifierId(
				long commercePriceModifierId, String name, String languageId,
				int start, int end, boolean inlineSQLHelper);

	public java.util.List
		<com.liferay.commerce.pricing.model.CommercePriceModifierRel>
			findPricingClassesByCommercePriceModifierId(
				long commercePriceModifierId, String title, int start, int end,
				boolean inlineSQLHelper);

}
// LIFERAY-SERVICE-BUILDER-HASH:1189690203