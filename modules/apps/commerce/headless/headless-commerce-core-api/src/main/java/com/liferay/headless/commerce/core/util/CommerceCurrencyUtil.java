/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.core.util;

import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Luca Pellizzon
 */
public class CommerceCurrencyUtil {

	public static CommerceCurrency fetchCommerceCurrency(
		long companyId, String currencyCode,
		String currencyExternalReferenceCode, long currencyId) {

		CommerceCurrency commerceCurrency = null;

		if (Validator.isNotNull(currencyCode)) {
			commerceCurrency =
				CommerceCurrencyLocalServiceUtil.fetchCommerceCurrency(
					companyId, currencyCode);

			if (commerceCurrency != null) {
				return commerceCurrency;
			}
		}

		if (currencyId > 0) {
			commerceCurrency =
				CommerceCurrencyLocalServiceUtil.fetchCommerceCurrency(
					currencyId);

			if (commerceCurrency != null) {
				return commerceCurrency;
			}
		}

		if (Validator.isNotNull(currencyExternalReferenceCode)) {
			commerceCurrency =
				CommerceCurrencyLocalServiceUtil.
					fetchCommerceCurrencyByExternalReferenceCode(
						currencyExternalReferenceCode, companyId);

			if (commerceCurrency != null) {
				return commerceCurrency;
			}
		}

		return null;
	}

	public static CommerceCurrency getCommerceCurrency(
			long companyId, String currencyCode,
			String currencyExternalReferenceCode, long currencyId)
		throws NoSuchCurrencyException {

		CommerceCurrency commerceCurrency = fetchCommerceCurrency(
			companyId, currencyCode, currencyExternalReferenceCode, currencyId);

		if (commerceCurrency == null) {
			throw new NoSuchCurrencyException();
		}

		return commerceCurrency;
	}

}