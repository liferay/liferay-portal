/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.internal.dto.v1_0.converter;

import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.commerce.inventory.service.CommerceInventoryReplenishmentItemService;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.ReplenishmentItem;
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
		"dto.class.name=com.liferay.headless.commerce.admin.inventory.dto.v1_0.ReplenishmentItem"
	},
	service = DTOConverter.class
)
public class ReplenishmentItemDTOConverter
	implements DTOConverter
		<CommerceInventoryReplenishmentItem, ReplenishmentItem> {

	@Override
	public String getContentType() {
		return ReplenishmentItem.class.getSimpleName();
	}

	@Override
	public ReplenishmentItem toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			_commerceInventoryReplenishmentItemService.
				getCommerceInventoryReplenishmentItem(
					(Long)dtoConverterContext.getId());

		return new ReplenishmentItem() {
			{
				setAvailabilityDate(
					() ->
						commerceInventoryReplenishmentItem.
							getAvailabilityDate());
				setExternalReferenceCode(
					() ->
						commerceInventoryReplenishmentItem.
							getExternalReferenceCode());
				setId(
					() ->
						commerceInventoryReplenishmentItem.
							getCommerceInventoryReplenishmentItemId());
				setQuantity(
					() -> _commerceQuantityFormatter.format(
						commerceInventoryReplenishmentItem.getCompanyId(),
						commerceInventoryReplenishmentItem.getQuantity(),
						commerceInventoryReplenishmentItem.getSku(),
						commerceInventoryReplenishmentItem.
							getUnitOfMeasureKey()));
				setSku(commerceInventoryReplenishmentItem::getSku);
				setUnitOfMeasureKey(
					() ->
						commerceInventoryReplenishmentItem.
							getUnitOfMeasureKey());
				setWarehouseId(
					() ->
						commerceInventoryReplenishmentItem.
							getCommerceInventoryWarehouseId());
			}
		};
	}

	@Reference
	private CommerceInventoryReplenishmentItemService
		_commerceInventoryReplenishmentItemService;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

}