/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupService;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel;
import com.liferay.commerce.discount.service.CommerceDiscountCommerceAccountGroupRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountAccountGroup;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Riccardo Alberti
 */
public class DiscountAccountGroupUtil {

	public static CommerceDiscountCommerceAccountGroupRel
			addCommerceDiscountAccountGroupRel(
				AccountGroupService accountGroupService,
				CommerceDiscountCommerceAccountGroupRelService
					commerceDiscountCommerceAccountGroupRelService,
				DiscountAccountGroup discountAccountGroup,
				CommerceDiscount commerceDiscount,
				ServiceContextHelper serviceContextHelper)
		throws Exception {

		ServiceContext serviceContext =
			serviceContextHelper.getServiceContext();

		AccountGroup accountGroup = _getAccountGroup(
			accountGroupService, discountAccountGroup, serviceContext);

		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel =
				commerceDiscountCommerceAccountGroupRelService.
					fetchCommerceDiscountCommerceAccountGroupRel(
						commerceDiscount.getCommerceDiscountId(),
						accountGroup.getAccountGroupId());

		if (commerceDiscountCommerceAccountGroupRel != null) {
			return commerceDiscountCommerceAccountGroupRel;
		}

		return commerceDiscountCommerceAccountGroupRelService.
			addCommerceDiscountCommerceAccountGroupRel(
				commerceDiscount.getCommerceDiscountId(),
				accountGroup.getAccountGroupId(), serviceContext);
	}

	private static AccountGroup _getAccountGroup(
			AccountGroupService accountGroupService,
			DiscountAccountGroup discountAccountGroup,
			ServiceContext serviceContext)
		throws Exception {

		String accountGroupExternalReferenceCode =
			discountAccountGroup.getAccountGroupExternalReferenceCode();

		if (Validator.isNull(accountGroupExternalReferenceCode)) {
			return accountGroupService.getAccountGroup(
				GetterUtil.getLong(discountAccountGroup.getAccountGroupId()));
		}

		AccountGroup accountGroup =
			accountGroupService.fetchAccountGroupByExternalReferenceCode(
				accountGroupExternalReferenceCode,
				serviceContext.getCompanyId());

		if (accountGroup != null) {
			return accountGroup;
		}

		long accountGroupId = GetterUtil.getLong(
			discountAccountGroup.getAccountGroupId());

		if ((accountGroupId > 0) && !LazyReferencingThreadLocal.isEnabled()) {
			accountGroup = accountGroupService.fetchAccountGroup(
				accountGroupId);

			if ((accountGroup != null) &&
				(accountGroup.getCompanyId() ==
					serviceContext.getCompanyId())) {

				return accountGroup;
			}
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			return accountGroupService.getAccountGroupByExternalReferenceCode(
				accountGroupExternalReferenceCode,
				serviceContext.getCompanyId());
		}

		return accountGroupService.getOrAddEmptyAccountGroup(
			accountGroupExternalReferenceCode,
			accountGroupExternalReferenceCode);
	}

}