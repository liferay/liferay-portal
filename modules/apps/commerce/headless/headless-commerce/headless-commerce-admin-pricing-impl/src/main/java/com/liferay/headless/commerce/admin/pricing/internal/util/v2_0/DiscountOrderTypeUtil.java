/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
import com.liferay.commerce.discount.service.CommerceDiscountOrderTypeRelService;
import com.liferay.commerce.exception.NoSuchOrderTypeException;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountOrderType;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class DiscountOrderTypeUtil {

	public static CommerceDiscountOrderTypeRel addCommerceDiscountOrderTypeRel(
			CommerceDiscount commerceDiscount,
			CommerceDiscountOrderTypeRelService
				commerceDiscountOrderTypeRelService,
			CommerceOrderTypeService commerceOrderTypeService,
			DiscountOrderType discountOrderType,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext =
			serviceContextHelper.getServiceContext();

		CommerceOrderType commerceOrderType = _getCommerceOrderType(
			commerceOrderTypeService, discountOrderType, serviceContext);

		int priority = GetterUtil.get(discountOrderType.getPriority(), 0);

		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
			commerceDiscountOrderTypeRelService.
				fetchCommerceDiscountOrderTypeRel(
					commerceDiscount.getCommerceDiscountId(),
					commerceOrderType.getCommerceOrderTypeId());

		if (commerceDiscountOrderTypeRel != null) {
			return commerceDiscountOrderTypeRelService.updatePriority(
				commerceDiscountOrderTypeRel.
					getCommerceDiscountOrderTypeRelId(),
				priority);
		}

		return commerceDiscountOrderTypeRelService.
			addCommerceDiscountOrderTypeRel(
				commerceDiscount.getCommerceDiscountId(),
				commerceOrderType.getCommerceOrderTypeId(), priority,
				serviceContext);
	}

	private static CommerceOrderType _getCommerceOrderType(
			CommerceOrderTypeService commerceOrderTypeService,
			DiscountOrderType discountOrderType, ServiceContext serviceContext)
		throws PortalException {

		String orderTypeExternalReferenceCode =
			discountOrderType.getOrderTypeExternalReferenceCode();

		if (Validator.isNull(orderTypeExternalReferenceCode)) {
			return commerceOrderTypeService.getCommerceOrderType(
				GetterUtil.getLong(discountOrderType.getOrderTypeId()));
		}

		CommerceOrderType commerceOrderType =
			commerceOrderTypeService.
				fetchCommerceOrderTypeByExternalReferenceCode(
					orderTypeExternalReferenceCode,
					serviceContext.getCompanyId());

		if (commerceOrderType != null) {
			return commerceOrderType;
		}

		long orderTypeId = GetterUtil.getLong(
			discountOrderType.getOrderTypeId());

		if ((orderTypeId > 0) && !LazyReferencingThreadLocal.isEnabled()) {
			commerceOrderType = commerceOrderTypeService.fetchCommerceOrderType(
				orderTypeId);

			if ((commerceOrderType != null) &&
				(commerceOrderType.getCompanyId() ==
					serviceContext.getCompanyId())) {

				return commerceOrderType;
			}
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			throw new NoSuchOrderTypeException(
				"Unable to find order type with external reference code " +
					orderTypeExternalReferenceCode);
		}

		return commerceOrderTypeService.getOrAddEmptyCommerceOrderType(
			orderTypeExternalReferenceCode);
	}

}