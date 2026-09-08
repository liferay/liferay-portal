/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter;

import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryRelService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleChannel;
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
		"dto.class.name=com.liferay.commerce.order.rule.model.COREntryRel-Channel"
	},
	service = DTOConverter.class
)
public class OrderRuleChannelDTOConverter
	implements DTOConverter<COREntryRel, OrderRuleChannel> {

	@Override
	public String getContentType() {
		return OrderRuleChannel.class.getSimpleName();
	}

	@Override
	public OrderRuleChannel toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		COREntryRel corEntryRel = _corEntryRelService.getCOREntryRel(
			(Long)dtoConverterContext.getId());

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(
				corEntryRel.getClassPK());
		COREntry corEntry = corEntryRel.getCOREntry();

		return new OrderRuleChannel() {
			{
				setActions(dtoConverterContext::getActions);
				setChannelExternalReferenceCode(
					commerceChannel::getExternalReferenceCode);
				setChannelId(commerceChannel::getCommerceChannelId);
				setOrderRuleChannelId(corEntryRel::getCOREntryRelId);
				setOrderRuleExternalReferenceCode(
					corEntry::getExternalReferenceCode);
				setOrderRuleId(corEntry::getCOREntryId);
			}
		};
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private COREntryRelService _corEntryRelService;

}