/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.internal.dto.v1_0.converter;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.Warehouse;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouse"
	},
	service = DTOConverter.class
)
public class WarehouseDTOConverter
	implements DTOConverter<CommerceInventoryWarehouse, Warehouse> {

	@Override
	public String getContentType() {
		return Warehouse.class.getSimpleName();
	}

	@Override
	public Warehouse toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseService.getCommerceInventoryWarehouse(
				(Long)dtoConverterContext.getId());

		return new Warehouse() {
			{
				setActions(dtoConverterContext::getActions);
				setActive(commerceInventoryWarehouse::isActive);
				setCity(commerceInventoryWarehouse::getCity);
				setCountryISOCode(
					() ->
						commerceInventoryWarehouse.
							getCountryTwoLettersISOCode());
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						commerceInventoryWarehouse.getDescriptionMap()));
				setExternalReferenceCode(
					() ->
						commerceInventoryWarehouse.getExternalReferenceCode());
				setId(
					() ->
						commerceInventoryWarehouse.
							getCommerceInventoryWarehouseId());
				setLatitude(commerceInventoryWarehouse::getLatitude);
				setLongitude(commerceInventoryWarehouse::getLongitude);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						commerceInventoryWarehouse.getNameMap()));
				setRegionISOCode(
					commerceInventoryWarehouse::getCommerceRegionCode);
				setStreet1(commerceInventoryWarehouse::getStreet1);
				setStreet2(commerceInventoryWarehouse::getStreet2);
				setStreet3(commerceInventoryWarehouse::getStreet3);
				setType(commerceInventoryWarehouse::getType);
				setZip(commerceInventoryWarehouse::getZip);
			}
		};
	}

	@Reference
	private CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;

}