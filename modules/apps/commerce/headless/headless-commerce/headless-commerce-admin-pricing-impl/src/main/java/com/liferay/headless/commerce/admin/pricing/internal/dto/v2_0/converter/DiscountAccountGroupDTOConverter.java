/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.account.model.AccountGroup;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel;
import com.liferay.commerce.discount.service.CommerceDiscountCommerceAccountGroupRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountAccountGroup;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel"
	},
	service = DTOConverter.class
)
public class DiscountAccountGroupDTOConverter
	implements DTOConverter
		<CommerceDiscountCommerceAccountGroupRel, DiscountAccountGroup> {

	@Override
	public String getContentType() {
		return DiscountAccountGroup.class.getSimpleName();
	}

	@Override
	public DiscountAccountGroup toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel =
				_commerceDiscountCommerceAccountGroupRelService.
					getCommerceDiscountCommerceAccountGroupRel(
						(Long)dtoConverterContext.getId());

		AccountGroup discountAccountGroup =
			commerceDiscountCommerceAccountGroupRel.getAccountGroup();
		CommerceDiscount commerceDiscount =
			commerceDiscountCommerceAccountGroupRel.getCommerceDiscount();

		return new DiscountAccountGroup() {
			{
				setAccountGroupExternalReferenceCode(
					discountAccountGroup::getExternalReferenceCode);
				setAccountGroupId(discountAccountGroup::getAccountGroupId);
				setActions(dtoConverterContext::getActions);
				setDiscountAccountGroupId(
					commerceDiscountCommerceAccountGroupRel::
						getCommerceDiscountCommerceAccountGroupRelId);
				setDiscountExternalReferenceCode(
					commerceDiscount::getExternalReferenceCode);
				setDiscountId(commerceDiscount::getCommerceDiscountId);
			}
		};
	}

	@Reference
	private CommerceDiscountCommerceAccountGroupRelService
		_commerceDiscountCommerceAccountGroupRelService;

}