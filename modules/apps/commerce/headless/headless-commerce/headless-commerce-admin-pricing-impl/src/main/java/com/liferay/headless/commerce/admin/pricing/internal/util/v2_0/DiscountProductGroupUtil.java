/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelService;
import com.liferay.commerce.pricing.exception.NoSuchPricingClassException;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountProductGroup;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class DiscountProductGroupUtil {

	public static CommerceDiscountRel addCommerceDiscountRel(
			CommercePricingClassService commercePricingClassService,
			CommerceDiscountRelService commerceDiscountRelService,
			DiscountProductGroup discountProductGroup,
			CommerceDiscount commerceDiscount,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext =
			serviceContextHelper.getServiceContext();

		CommercePricingClass commercePricingClass = _getCommercePricingClass(
			commercePricingClassService, discountProductGroup, serviceContext);

		CommerceDiscountRel commerceDiscountRel =
			commerceDiscountRelService.fetchCommerceDiscountRel(
				commerceDiscount.getCommerceDiscountId(),
				CommercePricingClass.class.getName(),
				commercePricingClass.getCommercePricingClassId());

		if (commerceDiscountRel != null) {
			return commerceDiscountRel;
		}

		return commerceDiscountRelService.addCommerceDiscountRel(
			commerceDiscount.getCommerceDiscountId(),
			CommercePricingClass.class.getName(),
			commercePricingClass.getCommercePricingClassId(), null,
			serviceContext);
	}

	private static CommercePricingClass _getCommercePricingClass(
			CommercePricingClassService commercePricingClassService,
			DiscountProductGroup discountProductGroup,
			ServiceContext serviceContext)
		throws PortalException {

		String productGroupExternalReferenceCode =
			discountProductGroup.getProductGroupExternalReferenceCode();

		if (Validator.isNull(productGroupExternalReferenceCode)) {
			return commercePricingClassService.getCommercePricingClass(
				GetterUtil.getLong(discountProductGroup.getProductGroupId()));
		}

		CommercePricingClass commercePricingClass =
			commercePricingClassService.
				fetchCommercePricingClassByExternalReferenceCode(
					productGroupExternalReferenceCode,
					serviceContext.getCompanyId());

		if (commercePricingClass != null) {
			return commercePricingClass;
		}

		long productGroupId = GetterUtil.getLong(
			discountProductGroup.getProductGroupId());

		if ((productGroupId > 0) && !LazyReferencingThreadLocal.isEnabled()) {
			commercePricingClass =
				commercePricingClassService.fetchCommercePricingClass(
					productGroupId);

			if ((commercePricingClass != null) &&
				(commercePricingClass.getCompanyId() ==
					serviceContext.getCompanyId())) {

				return commercePricingClass;
			}
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			throw new NoSuchPricingClassException(
				"Unable to find product group with external reference code " +
					productGroupExternalReferenceCode);
		}

		return commercePricingClassService.getOrAddEmptyCommercePricingClass(
			productGroupExternalReferenceCode);
	}

}