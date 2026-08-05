/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.internal.mapper.v1_0.util;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.AvailabilityEstimate;
import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.TaxCategory;
import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.Warehouse;
import com.liferay.headless.commerce.core.util.LanguageUtils;

/**
 * @author Alessio Antonio Rendina
 * @author Zoltán Takács
 */
public class DTOMapperUtil {

	public static AvailabilityEstimate modelToDTO(
		CommerceAvailabilityEstimate commerceAvailabilityEstimate) {

		if (commerceAvailabilityEstimate == null) {
			return new AvailabilityEstimate();
		}

		return new AvailabilityEstimate() {
			{
				setExternalReferenceCode(
					commerceAvailabilityEstimate::getExternalReferenceCode);
				setId(
					commerceAvailabilityEstimate::
						getCommerceAvailabilityEstimateId);
				setPriority(commerceAvailabilityEstimate::getPriority);
				setTitle(
					() -> LanguageUtils.getLanguageIdMap(
						commerceAvailabilityEstimate.getTitleMap()));
			}
		};
	}

	public static Warehouse modelToDTO(
		CommerceInventoryWarehouse commerceInventoryWarehouse) {

		if (commerceInventoryWarehouse == null) {
			return new Warehouse();
		}

		return new Warehouse() {
			{
				setActive(commerceInventoryWarehouse::isActive);
				setCity(commerceInventoryWarehouse::getCity);
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						commerceInventoryWarehouse.getDescriptionMap()));
				setId(
					commerceInventoryWarehouse::
						getCommerceInventoryWarehouseId);
				setLatitude(commerceInventoryWarehouse::getLatitude);
				setLongitude(commerceInventoryWarehouse::getLongitude);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						commerceInventoryWarehouse.getNameMap()));
				setStreet1(commerceInventoryWarehouse::getStreet1);
				setStreet2(commerceInventoryWarehouse::getStreet2);
				setStreet3(commerceInventoryWarehouse::getStreet3);
				setZip(commerceInventoryWarehouse::getZip);
			}
		};
	}

	public static TaxCategory modelToDTO(CPTaxCategory cpTaxCategory) {
		if (cpTaxCategory == null) {
			return new TaxCategory();
		}

		return new TaxCategory() {
			{
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						cpTaxCategory.getDescriptionMap()));
				setId(cpTaxCategory::getCPTaxCategoryId);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						cpTaxCategory.getNameMap()));
			}
		};
	}

}