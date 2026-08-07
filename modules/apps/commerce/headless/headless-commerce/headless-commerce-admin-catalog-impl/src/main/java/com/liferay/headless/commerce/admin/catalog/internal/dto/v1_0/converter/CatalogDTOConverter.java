/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Catalog;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.product.model.CommerceCatalog",
	service = DTOConverter.class
)
public class CatalogDTOConverter
	implements DTOConverter<CommerceCatalog, Catalog> {

	@Override
	public String getContentType() {
		return Catalog.class.getSimpleName();
	}

	@Override
	public Catalog toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceCatalog commerceCatalog =
			_commerceCatalogService.getCommerceCatalog(
				(Long)dtoConverterContext.getId());

		AccountEntry accountEntry = _accountEntryLocalService.fetchAccountEntry(
			commerceCatalog.getAccountEntryId());

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				commerceCatalog.getCompanyId(),
				commerceCatalog.getCommerceCurrencyCode());

		return new Catalog() {
			{
				setAccountExternalReferenceCode(
					() -> {
						if (accountEntry == null) {
							return null;
						}

						return accountEntry.getExternalReferenceCode();
					});
				setAccountId(commerceCatalog::getAccountEntryId);
				setAccountType(
					() -> {
						if (accountEntry == null) {
							return null;
						}

						return AccountType.create(accountEntry.getType());
					});
				setActions(dtoConverterContext::getActions);
				setCurrencyCode(commerceCurrency::getCode);
				setCurrencyExternalReferenceCode(
					commerceCurrency::getExternalReferenceCode);
				setCurrencyId(commerceCurrency::getCommerceCurrencyId);
				setDefaultLanguageId(
					commerceCatalog::getCatalogDefaultLanguageId);
				setExternalReferenceCode(
					commerceCatalog::getExternalReferenceCode);
				setId(commerceCatalog::getCommerceCatalogId);
				setName(commerceCatalog::getName);
				setSystem(commerceCatalog::isSystem);
			}
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

}