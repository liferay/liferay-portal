/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseRelService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseAccount;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel"
	},
	service = DTOConverter.class
)
public class WarehouseAccountDTOConverter
	implements DTOConverter<CommerceInventoryWarehouseRel, WarehouseAccount> {

	@Override
	public String getContentType() {
		return WarehouseAccount.class.getSimpleName();
	}

	@Override
	public WarehouseAccount toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			_commerceInventoryWarehouseRelService.
				getCommerceInventoryWarehouseRel(
					(Long)dtoConverterContext.getId());

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			commerceInventoryWarehouseRel.getClassPK());
		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseService.getCommerceInventoryWarehouse(
				commerceInventoryWarehouseRel.
					getCommerceInventoryWarehouseId());

		return new WarehouseAccount() {
			{
				setAccountExternalReferenceCode(
					accountEntry::getExternalReferenceCode);
				setAccountId(accountEntry::getAccountEntryId);
				setActions(dtoConverterContext::getActions);
				setWarehouseAccountId(
					commerceInventoryWarehouseRel::
						getCommerceInventoryWarehouseRelId);
				setWarehouseExternalReferenceCode(
					commerceInventoryWarehouse::getExternalReferenceCode);
				setWarehouseId(
					commerceInventoryWarehouse::
						getCommerceInventoryWarehouseId);
			}
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceInventoryWarehouseRelService
		_commerceInventoryWarehouseRelService;

	@Reference
	private CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;

}