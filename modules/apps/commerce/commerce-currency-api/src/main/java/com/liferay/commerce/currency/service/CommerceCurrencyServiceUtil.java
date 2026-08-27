/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for CommerceCurrency. This utility wraps
 * <code>com.liferay.commerce.currency.service.impl.CommerceCurrencyServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Andrea Di Giorgi
 * @see CommerceCurrencyService
 * @generated
 */
public class CommerceCurrencyServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.currency.service.impl.CommerceCurrencyServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommerceCurrency addCommerceCurrency(
			String externalReferenceCode, String code,
			Map<java.util.Locale, String> nameMap, String symbol,
			java.math.BigDecimal rate,
			Map<java.util.Locale, String> formatPatternMap,
			int maxFractionDigits, int minFractionDigits, String roundingMode,
			boolean primary, double priority, boolean active)
		throws PortalException {

		return getService().addCommerceCurrency(
			externalReferenceCode, code, nameMap, symbol, rate,
			formatPatternMap, maxFractionDigits, minFractionDigits,
			roundingMode, primary, priority, active);
	}

	public static void deleteCommerceCurrency(long commerceCurrencyId)
		throws PortalException {

		getService().deleteCommerceCurrency(commerceCurrencyId);
	}

	public static CommerceCurrency fetchCommerceCurrencyByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCommerceCurrencyByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommerceCurrency fetchPrimaryCommerceCurrency(long companyId)
		throws PortalException {

		return getService().fetchPrimaryCommerceCurrency(companyId);
	}

	public static List<CommerceCurrency> getCommerceCurrencies(
			long companyId, boolean active, int start, int end,
			OrderByComparator<CommerceCurrency> orderByComparator)
		throws PortalException {

		return getService().getCommerceCurrencies(
			companyId, active, start, end, orderByComparator);
	}

	public static List<CommerceCurrency> getCommerceCurrencies(
			long companyId, int start, int end,
			OrderByComparator<CommerceCurrency> orderByComparator)
		throws PortalException {

		return getService().getCommerceCurrencies(
			companyId, start, end, orderByComparator);
	}

	public static int getCommerceCurrenciesCount(long companyId)
		throws PortalException {

		return getService().getCommerceCurrenciesCount(companyId);
	}

	public static int getCommerceCurrenciesCount(long companyId, boolean active)
		throws PortalException {

		return getService().getCommerceCurrenciesCount(companyId, active);
	}

	public static CommerceCurrency getCommerceCurrency(long commerceCurrencyId)
		throws PortalException {

		return getService().getCommerceCurrency(commerceCurrencyId);
	}

	public static CommerceCurrency getCommerceCurrency(
			long companyId, String code)
		throws PortalException {

		return getService().getCommerceCurrency(companyId, code);
	}

	public static CommerceCurrency getOrAddEmptyCommerceCurrency(
			String externalReferenceCode, String code)
		throws PortalException {

		return getService().getOrAddEmptyCommerceCurrency(
			externalReferenceCode, code);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceCurrency> searchCommerceCurrencies(
				long companyId, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceCurrencies(
			companyId, keywords, params, start, end, sort);
	}

	public static CommerceCurrency setActive(
			long commerceCurrencyId, boolean active)
		throws PortalException {

		return getService().setActive(commerceCurrencyId, active);
	}

	public static CommerceCurrency setPrimary(
			long commerceCurrencyId, boolean primary)
		throws PortalException {

		return getService().setPrimary(commerceCurrencyId, primary);
	}

	public static CommerceCurrency updateCommerceCurrency(
			String externalReferenceCode, long commerceCurrencyId,
			Map<java.util.Locale, String> nameMap, String symbol,
			java.math.BigDecimal rate,
			Map<java.util.Locale, String> formatPatternMap,
			int maxFractionDigits, int minFractionDigits, String roundingMode,
			boolean primary, double priority, boolean active,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceCurrency(
			externalReferenceCode, commerceCurrencyId, nameMap, symbol, rate,
			formatPatternMap, maxFractionDigits, minFractionDigits,
			roundingMode, primary, priority, active, serviceContext);
	}

	public static void updateExchangeRate(
			long commerceCurrencyId, String exchangeRateProviderKey)
		throws PortalException {

		getService().updateExchangeRate(
			commerceCurrencyId, exchangeRateProviderKey);
	}

	public static void updateExchangeRates() throws PortalException {
		getService().updateExchangeRates();
	}

	public static CommerceCurrencyService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceCurrencyService> _serviceSnapshot =
		new Snapshot<>(
			CommerceCurrencyServiceUtil.class, CommerceCurrencyService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-259728914