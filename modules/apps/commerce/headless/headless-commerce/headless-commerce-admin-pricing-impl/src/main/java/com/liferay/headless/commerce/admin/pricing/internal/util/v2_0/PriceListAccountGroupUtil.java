/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupService;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListCommerceAccountGroupRel;
import com.liferay.commerce.price.list.service.CommercePriceListCommerceAccountGroupRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListAccountGroup;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class PriceListAccountGroupUtil {

	public static CommercePriceListCommerceAccountGroupRel
			addCommercePriceListAccountGroupRel(
				AccountGroupService accountGroupService,
				CommercePriceListCommerceAccountGroupRelService
					commercePriceListCommerceAccountGroupRelService,
				PriceListAccountGroup priceListAccountGroup,
				CommercePriceList commercePriceList,
				ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext = serviceContextHelper.getServiceContext(
			commercePriceList.getGroupId());

		AccountGroup accountGroup;

		if (Validator.isNull(
				priceListAccountGroup.getAccountGroupExternalReferenceCode())) {

			accountGroup = accountGroupService.getAccountGroup(
				priceListAccountGroup.getAccountGroupId());
		}
		else {
			accountGroup =
				accountGroupService.getAccountGroupByExternalReferenceCode(
					priceListAccountGroup.
						getAccountGroupExternalReferenceCode(),
					serviceContext.getCompanyId());
		}

		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel =
				commercePriceListCommerceAccountGroupRelService.
					fetchCommercePriceListCommerceAccountGroupRel(
						commercePriceList.getCommercePriceListId(),
						accountGroup.getAccountGroupId());

		if (commercePriceListCommerceAccountGroupRel != null) {
			commercePriceListCommerceAccountGroupRelService.
				deleteCommercePriceListCommerceAccountGroupRel(
					commercePriceListCommerceAccountGroupRel.
						getCommercePriceListCommerceAccountGroupRelId());
		}

		return commercePriceListCommerceAccountGroupRelService.
			addCommercePriceListCommerceAccountGroupRel(
				commercePriceList.getCommercePriceListId(),
				accountGroup.getAccountGroupId(),
				GetterUtil.get(priceListAccountGroup.getOrder(), 0),
				serviceContext);
	}

}