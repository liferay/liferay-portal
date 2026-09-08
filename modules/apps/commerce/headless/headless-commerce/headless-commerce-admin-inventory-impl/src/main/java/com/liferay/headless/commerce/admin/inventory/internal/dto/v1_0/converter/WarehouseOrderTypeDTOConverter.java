/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.internal.dto.v1_0.converter;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseRelService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseOrderType;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel-OrderType"
	},
	service = DTOConverter.class
)
public class WarehouseOrderTypeDTOConverter
	implements DTOConverter<CommerceInventoryWarehouseRel, WarehouseOrderType> {

	@Override
	public String getContentType() {
		return WarehouseOrderType.class.getSimpleName();
	}

	@Override
	public WarehouseOrderType toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			_commerceInventoryWarehouseRelService.
				getCommerceInventoryWarehouseRel(
					(Long)dtoConverterContext.getId());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.getCommerceOrderType(
				commerceInventoryWarehouseRel.getClassPK());

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseService.getCommerceInventoryWarehouse(
				commerceInventoryWarehouseRel.
					getCommerceInventoryWarehouseId());

		return new WarehouseOrderType() {
			{
				setActions(dtoConverterContext::getActions);
				setOrderTypeExternalReferenceCode(
					commerceOrderType::getExternalReferenceCode);
				setOrderTypeId(commerceOrderType::getCommerceOrderTypeId);
				setWarehouseExternalReferenceCode(
					() ->
						commerceInventoryWarehouse.getExternalReferenceCode());
				setWarehouseId(
					() ->
						commerceInventoryWarehouse.
							getCommerceInventoryWarehouseId());
				setWarehouseOrderTypeId(
					() ->
						commerceInventoryWarehouseRel.
							getCommerceInventoryWarehouseRelId());
			}
		};
	}

	@Reference
	private CommerceInventoryWarehouseRelService
		_commerceInventoryWarehouseRelService;

	@Reference
	private CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

}