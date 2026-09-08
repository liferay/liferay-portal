/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryRelService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleAccount;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.order.rule.model.COREntryRel-Account"
	},
	service = DTOConverter.class
)
public class OrderRuleAccountDTOConverter
	implements DTOConverter<COREntryRel, OrderRuleAccount> {

	@Override
	public String getContentType() {
		return OrderRuleAccount.class.getSimpleName();
	}

	@Override
	public OrderRuleAccount toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		COREntryRel corEntryRel = _corEntryRelService.getCOREntryRel(
			(Long)dtoConverterContext.getId());

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			corEntryRel.getClassPK());
		COREntry corEntry = corEntryRel.getCOREntry();

		return new OrderRuleAccount() {
			{
				setAccountExternalReferenceCode(
					accountEntry::getExternalReferenceCode);
				setAccountId(accountEntry::getAccountEntryId);
				setActions(dtoConverterContext::getActions);
				setOrderRuleAccountId(corEntryRel::getCOREntryRelId);
				setOrderRuleExternalReferenceCode(
					corEntry::getExternalReferenceCode);
				setOrderRuleId(corEntry::getCOREntryId);
			}
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private COREntryRelService _corEntryRelService;

}