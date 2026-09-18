/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListDiscountRel;
import com.liferay.commerce.price.list.service.CommercePriceListDiscountRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListDiscount;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Riccardo Alberti
 */
public class PriceListDiscountUtil {

	public static CommercePriceListDiscountRel addCommercePriceListDiscountRel(
			CommerceDiscountService commerceDiscountService,
			CommercePriceListDiscountRelService
				commercePriceListDiscountRelService,
			PriceListDiscount priceListDiscount,
			CommercePriceList commercePriceList,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext = serviceContextHelper.getServiceContext(
			commercePriceList.getGroupId());

		CommerceDiscount commerceDiscount;

		if (Validator.isNull(
				priceListDiscount.getDiscountExternalReferenceCode())) {

			commerceDiscount = commerceDiscountService.getCommerceDiscount(
				priceListDiscount.getDiscountId());
		}
		else {
			commerceDiscount =
				commerceDiscountService.
					fetchCommerceDiscountByExternalReferenceCode(
						priceListDiscount.getDiscountExternalReferenceCode(),
						serviceContext.getCompanyId());

			if (commerceDiscount == null) {
				throw new NoSuchDiscountException(
					"Unable to find discount with external reference code " +
						priceListDiscount.getDiscountExternalReferenceCode());
			}
		}

		CommercePriceListDiscountRel commercePriceListDiscountRel =
			commercePriceListDiscountRelService.
				fetchCommercePriceListDiscountRel(
					commercePriceList.getCommercePriceListId(),
					commerceDiscount.getCommerceDiscountId());

		if (commercePriceListDiscountRel != null) {
			commercePriceListDiscountRelService.
				deleteCommercePriceListDiscountRel(
					commercePriceListDiscountRel.
						getCommercePriceListDiscountRelId());
		}

		return commercePriceListDiscountRelService.
			addCommercePriceListDiscountRel(
				commercePriceList.getCommercePriceListId(),
				commerceDiscount.getCommerceDiscountId(),
				GetterUtil.get(priceListDiscount.getOrder(), 0),
				serviceContext);
	}

}