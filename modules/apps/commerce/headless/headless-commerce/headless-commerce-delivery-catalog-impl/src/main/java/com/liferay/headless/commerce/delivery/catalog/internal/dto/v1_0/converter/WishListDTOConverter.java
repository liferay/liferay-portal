/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.service.CommerceWishListService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishList;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mahmoud Azzam
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishList"
	},
	service = DTOConverter.class
)
public class WishListDTOConverter
	implements DTOConverter<CommerceWishList, WishList> {

	@Override
	public String getContentType() {
		return WishList.class.getSimpleName();
	}

	@Override
	public WishList toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceWishList commerceWishList =
			_commerceWishListService.getCommerceWishList(
				(Long)dtoConverterContext.getId());

		return new WishList() {
			{
				setDefaultWishList(commerceWishList::isDefaultWishList);
				setId(commerceWishList::getCommerceWishListId);
				setName(commerceWishList::getName);
			}
		};
	}

	@Reference
	private CommerceWishListService _commerceWishListService;

}