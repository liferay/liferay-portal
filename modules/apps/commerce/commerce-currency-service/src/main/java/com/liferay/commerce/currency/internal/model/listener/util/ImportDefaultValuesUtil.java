/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.internal.model.listener.util;

import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Locale;

/**
 * @author Shuyang Zhou
 */
public class ImportDefaultValuesUtil {

	public static void importDefaultValues(
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		Company company) {

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				Locale defaultLocale = LocaleThreadLocal.getDefaultLocale();
				Locale siteDefaultLocale =
					LocaleThreadLocal.getSiteDefaultLocale();

				try {
					LocaleThreadLocal.setDefaultLocale(company.getLocale());
					LocaleThreadLocal.setSiteDefaultLocale(null);

					ServiceContext serviceContext = new ServiceContext();

					serviceContext.setCompanyId(company.getCompanyId());
					serviceContext.setLanguageId(
						LocaleUtil.toLanguageId(company.getLocale()));

					User guestUser = company.getGuestUser();

					serviceContext.setUserId(guestUser.getUserId());

					commerceCurrencyLocalService.importDefaultValues(
						false, serviceContext);

					return null;
				}
				finally {
					LocaleThreadLocal.setDefaultLocale(defaultLocale);
					LocaleThreadLocal.setSiteDefaultLocale(siteDefaultLocale);
				}
			});
	}

}