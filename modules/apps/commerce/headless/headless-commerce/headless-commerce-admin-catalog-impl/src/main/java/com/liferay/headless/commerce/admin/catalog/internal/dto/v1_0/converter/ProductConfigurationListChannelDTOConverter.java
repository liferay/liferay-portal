/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CPConfigurationListService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Channel;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListChannel;
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
		"dto.class.name=com.liferay.commerce.inventory.model.CommerceChannelRel"
	},
	service = DTOConverter.class
)
public class ProductConfigurationListChannelDTOConverter
	implements DTOConverter
		<CPConfigurationListRel, ProductConfigurationListChannel> {

	@Override
	public String getContentType() {
		return ProductConfigurationListChannel.class.getSimpleName();
	}

	@Override
	public ProductConfigurationListChannel toDTO(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceChannelRel commerceChannelRel =
			_commerceChannelRelService.getCommerceChannelRel(
				(Long)dtoConverterContext.getId());

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(
				commerceChannelRel.getCommerceChannelId());
		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.getCPConfigurationList(
				commerceChannelRel.getClassPK());

		return new ProductConfigurationListChannel() {
			{
				setActions(dtoConverterContext::getActions);
				setChannel(
					() -> new Channel() {
						{
							setCurrencyCode(
								commerceChannel::getCommerceCurrencyCode);
							setExternalReferenceCode(
								commerceChannel::getExternalReferenceCode);
							setId(commerceChannel::getCommerceChannelId);
							setName(commerceChannel::getName);
							setSiteGroupId(commerceChannel::getSiteGroupId);
							setType(commerceChannel::getType);
						}
					});
				setChannelExternalReferenceCode(
					commerceChannel::getExternalReferenceCode);
				setChannelId(commerceChannel::getCommerceChannelId);
				setProductConfigurationListChannelId(
					commerceChannelRel::getCommerceChannelRelId);
				setProductConfigurationListExternalReferenceCode(
					cpConfigurationList::getExternalReferenceCode);
				setProductConfigurationListId(
					cpConfigurationList::getCPConfigurationListId);
			}
		};
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CPConfigurationListService _cpConfigurationListService;

}