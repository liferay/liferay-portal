/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListAccountRel;
import com.liferay.commerce.price.list.service.CommercePriceListAccountRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListAccount;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Riccardo Alberti
 */
public class PriceListAccountUtil {

	public static CommercePriceListAccountRel addCommercePriceListAccountRel(
			AccountEntryService accountEntryService,
			CommercePriceListAccountRelService
				commercePriceListAccountRelService,
			PriceListAccount priceListAccount,
			CommercePriceList commercePriceList,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext = serviceContextHelper.getServiceContext(
			commercePriceList.getGroupId());

		AccountEntry accountEntry = _getAccountEntry(
			accountEntryService, priceListAccount, serviceContext);

		CommercePriceListAccountRel commercePriceListAccountRel =
			commercePriceListAccountRelService.fetchCommercePriceListAccountRel(
				commercePriceList.getCommercePriceListId(),
				accountEntry.getAccountEntryId());

		if (commercePriceListAccountRel != null) {
			commercePriceListAccountRelService.
				deleteCommercePriceListAccountRel(
					commercePriceListAccountRel.
						getCommercePriceListAccountRelId());
		}

		return commercePriceListAccountRelService.
			addCommercePriceListAccountRel(
				commercePriceList.getCommercePriceListId(),
				accountEntry.getAccountEntryId(),
				GetterUtil.get(priceListAccount.getOrder(), 0), serviceContext);
	}

	private static AccountEntry _getAccountEntry(
			AccountEntryService accountEntryService,
			PriceListAccount priceListAccount, ServiceContext serviceContext)
		throws PortalException {

		String accountExternalReferenceCode =
			priceListAccount.getAccountExternalReferenceCode();

		if (Validator.isNull(accountExternalReferenceCode)) {
			return accountEntryService.getAccountEntry(
				priceListAccount.getAccountId());
		}

		PriceListAccount.AccountType accountType =
			priceListAccount.getAccountType();

		if (!LazyReferencingThreadLocal.isEnabled() || (accountType == null)) {
			return accountEntryService.getAccountEntryByExternalReferenceCode(
				accountExternalReferenceCode, serviceContext.getCompanyId());
		}

		return accountEntryService.getOrAddEmptyAccountEntry(
			accountExternalReferenceCode, accountExternalReferenceCode,
			accountType.getValue());
	}

}