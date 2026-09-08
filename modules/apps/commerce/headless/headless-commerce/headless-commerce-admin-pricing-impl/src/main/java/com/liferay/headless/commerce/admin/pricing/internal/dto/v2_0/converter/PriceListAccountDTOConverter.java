/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListAccountRel;
import com.liferay.commerce.price.list.service.CommercePriceListAccountRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListAccount;
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
		"dto.class.name=com.liferay.commerce.price.list.model.CommercePriceListAccountRel"
	},
	service = DTOConverter.class
)
public class PriceListAccountDTOConverter
	implements DTOConverter<CommercePriceListAccountRel, PriceListAccount> {

	@Override
	public String getContentType() {
		return PriceListAccount.class.getSimpleName();
	}

	@Override
	public PriceListAccount toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePriceListAccountRel commercePriceListAccountRel =
			_commercePriceListAccountRelService.getCommercePriceListAccountRel(
				(Long)dtoConverterContext.getId());

		AccountEntry accountEntry =
			commercePriceListAccountRel.getAccountEntry();
		CommercePriceList commercePriceList =
			commercePriceListAccountRel.getCommercePriceList();

		return new PriceListAccount() {
			{
				setAccountExternalReferenceCode(
					accountEntry::getExternalReferenceCode);
				setAccountId(accountEntry::getAccountEntryId);
				setActions(dtoConverterContext::getActions);
				setOrder(commercePriceListAccountRel::getOrder);
				setPriceListAccountId(
					commercePriceListAccountRel::
						getCommercePriceListAccountRelId);
				setPriceListExternalReferenceCode(
					commercePriceList::getExternalReferenceCode);
				setPriceListId(commercePriceList::getCommercePriceListId);
			}
		};
	}

	@Reference
	private CommercePriceListAccountRelService
		_commercePriceListAccountRelService;

}