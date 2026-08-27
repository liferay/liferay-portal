/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommerceCurrencyService}.
 *
 * @author Andrea Di Giorgi
 * @see CommerceCurrencyService
 * @generated
 */
public class CommerceCurrencyServiceWrapper
	implements CommerceCurrencyService,
			   ServiceWrapper<CommerceCurrencyService> {

	public CommerceCurrencyServiceWrapper() {
		this(null);
	}

	public CommerceCurrencyServiceWrapper(
		CommerceCurrencyService commerceCurrencyService) {

		_commerceCurrencyService = commerceCurrencyService;
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			addCommerceCurrency(
				String externalReferenceCode, String code,
				java.util.Map<java.util.Locale, String> nameMap, String symbol,
				java.math.BigDecimal rate,
				java.util.Map<java.util.Locale, String> formatPatternMap,
				int maxFractionDigits, int minFractionDigits,
				String roundingMode, boolean primary, double priority,
				boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.addCommerceCurrency(
			externalReferenceCode, code, nameMap, symbol, rate,
			formatPatternMap, maxFractionDigits, minFractionDigits,
			roundingMode, primary, priority, active);
	}

	@Override
	public void deleteCommerceCurrency(long commerceCurrencyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceCurrencyService.deleteCommerceCurrency(commerceCurrencyId);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			fetchCommerceCurrencyByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.
			fetchCommerceCurrencyByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			fetchPrimaryCommerceCurrency(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.fetchPrimaryCommerceCurrency(companyId);
	}

	@Override
	public java.util.List<com.liferay.commerce.currency.model.CommerceCurrency>
			getCommerceCurrencies(
				long companyId, boolean active, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.currency.model.CommerceCurrency>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.getCommerceCurrencies(
			companyId, active, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.currency.model.CommerceCurrency>
			getCommerceCurrencies(
				long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.currency.model.CommerceCurrency>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.getCommerceCurrencies(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceCurrenciesCount(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.getCommerceCurrenciesCount(companyId);
	}

	@Override
	public int getCommerceCurrenciesCount(long companyId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.getCommerceCurrenciesCount(
			companyId, active);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			getCommerceCurrency(long commerceCurrencyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.getCommerceCurrency(commerceCurrencyId);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			getCommerceCurrency(long companyId, String code)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.getCommerceCurrency(companyId, code);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			getOrAddEmptyCommerceCurrency(
				String externalReferenceCode, String code)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.getOrAddEmptyCommerceCurrency(
			externalReferenceCode, code);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceCurrencyService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.currency.model.CommerceCurrency>
				searchCommerceCurrencies(
					long companyId, String keywords,
					java.util.LinkedHashMap<String, Object> params, int start,
					int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.searchCommerceCurrencies(
			companyId, keywords, params, start, end, sort);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency setActive(
			long commerceCurrencyId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.setActive(commerceCurrencyId, active);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency setPrimary(
			long commerceCurrencyId, boolean primary)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.setPrimary(commerceCurrencyId, primary);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			updateCommerceCurrency(
				String externalReferenceCode, long commerceCurrencyId,
				java.util.Map<java.util.Locale, String> nameMap, String symbol,
				java.math.BigDecimal rate,
				java.util.Map<java.util.Locale, String> formatPatternMap,
				int maxFractionDigits, int minFractionDigits,
				String roundingMode, boolean primary, double priority,
				boolean active,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyService.updateCommerceCurrency(
			externalReferenceCode, commerceCurrencyId, nameMap, symbol, rate,
			formatPatternMap, maxFractionDigits, minFractionDigits,
			roundingMode, primary, priority, active, serviceContext);
	}

	@Override
	public void updateExchangeRate(
			long commerceCurrencyId, String exchangeRateProviderKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceCurrencyService.updateExchangeRate(
			commerceCurrencyId, exchangeRateProviderKey);
	}

	@Override
	public void updateExchangeRates()
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceCurrencyService.updateExchangeRates();
	}

	@Override
	public CommerceCurrencyService getWrappedService() {
		return _commerceCurrencyService;
	}

	@Override
	public void setWrappedService(
		CommerceCurrencyService commerceCurrencyService) {

		_commerceCurrencyService = commerceCurrencyService;
	}

	private CommerceCurrencyService _commerceCurrencyService;

}
// LIFERAY-SERVICE-BUILDER-HASH:647393766