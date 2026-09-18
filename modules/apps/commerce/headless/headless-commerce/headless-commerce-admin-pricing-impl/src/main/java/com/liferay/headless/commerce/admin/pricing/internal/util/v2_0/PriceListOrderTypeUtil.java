/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.commerce.exception.NoSuchOrderTypeException;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel;
import com.liferay.commerce.price.list.service.CommercePriceListOrderTypeRelService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListOrderType;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class PriceListOrderTypeUtil {

	public static CommercePriceListOrderTypeRel
			addCommercePriceListOrderTypeRel(
				CommerceOrderTypeService commerceOrderTypeService,
				CommercePriceListOrderTypeRelService
					commercePriceListOrderTypeRelService,
				PriceListOrderType priceListOrderType,
				CommercePriceList commercePriceList,
				ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext = serviceContextHelper.getServiceContext(
			commercePriceList.getGroupId());

		CommerceOrderType commerceOrderType;

		if (Validator.isNull(
				priceListOrderType.getOrderTypeExternalReferenceCode())) {

			commerceOrderType = commerceOrderTypeService.getCommerceOrderType(
				priceListOrderType.getOrderTypeId());
		}
		else {
			commerceOrderType =
				commerceOrderTypeService.
					fetchCommerceOrderTypeByExternalReferenceCode(
						priceListOrderType.getOrderTypeExternalReferenceCode(),
						serviceContext.getCompanyId());

			if (commerceOrderType == null) {
				String orderTypeExternalReferenceCode =
					priceListOrderType.getOrderTypeExternalReferenceCode();

				throw new NoSuchOrderTypeException(
					"Unable to find order type with external reference code " +
						orderTypeExternalReferenceCode);
			}
		}

		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel =
			commercePriceListOrderTypeRelService.
				fetchCommercePriceListOrderTypeRel(
					commercePriceList.getCommercePriceListId(),
					commerceOrderType.getCommerceOrderTypeId());

		if (commercePriceListOrderTypeRel != null) {
			commercePriceListOrderTypeRelService.
				deleteCommercePriceListOrderTypeRel(
					commercePriceListOrderTypeRel.
						getCommercePriceListOrderTypeRelId());
		}

		return commercePriceListOrderTypeRelService.
			addCommercePriceListOrderTypeRel(
				commercePriceList.getCommercePriceListId(),
				commerceOrderType.getCommerceOrderTypeId(),
				GetterUtil.get(priceListOrderType.getPriority(), 0),
				serviceContext);
	}

}