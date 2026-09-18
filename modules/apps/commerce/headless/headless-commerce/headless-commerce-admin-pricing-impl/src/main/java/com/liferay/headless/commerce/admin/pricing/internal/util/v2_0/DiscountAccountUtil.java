/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountAccountRel;
import com.liferay.commerce.discount.service.CommerceDiscountAccountRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountAccount;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Riccardo Alberti
 */
public class DiscountAccountUtil {

	public static CommerceDiscountAccountRel addCommerceDiscountAccountRel(
			AccountEntryService accountEntryService,
			CommerceDiscountAccountRelService commerceDiscountAccountRelService,
			DiscountAccount discountAccount, CommerceDiscount commerceDiscount,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext =
			serviceContextHelper.getServiceContext();

		AccountEntry accountEntry = _getAccountEntry(
			accountEntryService, discountAccount, serviceContext);

		CommerceDiscountAccountRel commerceDiscountAccountRel =
			commerceDiscountAccountRelService.fetchCommerceDiscountAccountRel(
				accountEntry.getAccountEntryId(),
				commerceDiscount.getCommerceDiscountId());

		if (commerceDiscountAccountRel != null) {
			return commerceDiscountAccountRel;
		}

		return commerceDiscountAccountRelService.addCommerceDiscountAccountRel(
			commerceDiscount.getCommerceDiscountId(),
			accountEntry.getAccountEntryId(), serviceContext);
	}

	private static AccountEntry _getAccountEntry(
			AccountEntryService accountEntryService,
			DiscountAccount discountAccount, ServiceContext serviceContext)
		throws PortalException {

		String accountExternalReferenceCode =
			discountAccount.getAccountExternalReferenceCode();

		if (Validator.isNull(accountExternalReferenceCode)) {
			return accountEntryService.getAccountEntry(
				GetterUtil.getLong(discountAccount.getAccountId()));
		}

		AccountEntry accountEntry =
			accountEntryService.fetchAccountEntryByExternalReferenceCode(
				accountExternalReferenceCode, serviceContext.getCompanyId());

		if (accountEntry != null) {
			return accountEntry;
		}

		long accountId = GetterUtil.getLong(discountAccount.getAccountId());

		if ((accountId > 0) && !LazyReferencingThreadLocal.isEnabled()) {
			accountEntry = accountEntryService.fetchAccountEntry(accountId);

			if ((accountEntry != null) &&
				(accountEntry.getCompanyId() ==
					serviceContext.getCompanyId())) {

				return accountEntry;
			}
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			return accountEntryService.getAccountEntryByExternalReferenceCode(
				accountExternalReferenceCode, serviceContext.getCompanyId());
		}

		return accountEntryService.getOrAddEmptyAccountEntry(
			accountExternalReferenceCode, accountExternalReferenceCode,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);
	}

}