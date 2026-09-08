/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupService;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryRelService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleAccountGroup;
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
		"dto.class.name=com.liferay.commerce.order.rule.model.COREntryRel-AccountGroup"
	},
	service = DTOConverter.class
)
public class OrderRuleAccountGroupDTOConverter
	implements DTOConverter<COREntryRel, OrderRuleAccountGroup> {

	@Override
	public String getContentType() {
		return OrderRuleAccountGroup.class.getSimpleName();
	}

	@Override
	public OrderRuleAccountGroup toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		COREntryRel corEntryRel = _corEntryRelService.getCOREntryRel(
			(Long)dtoConverterContext.getId());

		AccountGroup orderRuleAccountGroup =
			_accountGroupService.getAccountGroup(corEntryRel.getClassPK());
		COREntry corEntry = corEntryRel.getCOREntry();

		return new OrderRuleAccountGroup() {
			{
				setAccountGroupExternalReferenceCode(
					orderRuleAccountGroup::getExternalReferenceCode);
				setAccountGroupId(orderRuleAccountGroup::getAccountGroupId);
				setActions(dtoConverterContext::getActions);
				setOrderRuleAccountGroupId(corEntryRel::getCOREntryRelId);
				setOrderRuleExternalReferenceCode(
					corEntry::getExternalReferenceCode);
				setOrderRuleId(corEntry::getCOREntryId);
			}
		};
	}

	@Reference
	private AccountGroupService _accountGroupService;

	@Reference
	private COREntryRelService _corEntryRelService;

}