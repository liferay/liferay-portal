/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.commerce.product.service.CPConfigurationListRelService;
import com.liferay.commerce.product.service.CPConfigurationListService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Account;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListAccount;
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
		"dto.class.name=com.liferay.commerce.product.model.CPConfigurationListRel"
	},
	service = DTOConverter.class
)
public class ProductConfigurationListAccountDTOConverter
	implements DTOConverter
		<CPConfigurationListRel, ProductConfigurationListAccount> {

	@Override
	public String getContentType() {
		return ProductConfigurationListAccount.class.getSimpleName();
	}

	@Override
	public ProductConfigurationListAccount toDTO(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CPConfigurationListRel cpConfigurationListRel =
			_cpConfigurationListRelService.getCPConfigurationListRel(
				(Long)dtoConverterContext.getId());

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			cpConfigurationListRel.getClassPK());
		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.getCPConfigurationList(
				cpConfigurationListRel.getCPConfigurationListId());

		return new ProductConfigurationListAccount() {
			{
				setAccount(
					() -> new Account() {
						{
							setId(accountEntry::getAccountEntryId);
							setLogoId(accountEntry::getLogoId);
							setName(accountEntry::getName);
						}
					});
				setAccountExternalReferenceCode(
					accountEntry::getExternalReferenceCode);
				setAccountId(accountEntry::getAccountEntryId);
				setActions(dtoConverterContext::getActions);
				setProductConfigurationListAccountId(
					cpConfigurationListRel::getCPConfigurationListRelId);
				setProductConfigurationListExternalReferenceCode(
					cpConfigurationList::getExternalReferenceCode);
				setProductConfigurationListId(
					cpConfigurationList::getCPConfigurationListId);
			}
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CPConfigurationListRelService _cpConfigurationListRelService;

	@Reference
	private CPConfigurationListService _cpConfigurationListService;

}