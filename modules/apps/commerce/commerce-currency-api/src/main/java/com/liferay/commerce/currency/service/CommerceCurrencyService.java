/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CommerceCurrency. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Andrea Di Giorgi
 * @see CommerceCurrencyServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceCurrencyService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.currency.service.impl.CommerceCurrencyServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce currency remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceCurrencyServiceUtil} if injection and service tracking are not available.
	 */
	public CommerceCurrency addCommerceCurrency(
			String externalReferenceCode, String code,
			Map<Locale, String> nameMap, String symbol, BigDecimal rate,
			Map<Locale, String> formatPatternMap, int maxFractionDigits,
			int minFractionDigits, String roundingMode, boolean primary,
			double priority, boolean active)
		throws PortalException;

	public void deleteCommerceCurrency(long commerceCurrencyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency fetchCommerceCurrencyByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency fetchPrimaryCommerceCurrency(long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceCurrency> getCommerceCurrencies(
			long companyId, boolean active, int start, int end,
			OrderByComparator<CommerceCurrency> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceCurrency> getCommerceCurrencies(
			long companyId, int start, int end,
			OrderByComparator<CommerceCurrency> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceCurrenciesCount(long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceCurrenciesCount(long companyId, boolean active)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency getCommerceCurrency(long commerceCurrencyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency getCommerceCurrency(long companyId, String code)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency getOrAddEmptyCommerceCurrency(
			String externalReferenceCode, String code)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceCurrency> searchCommerceCurrencies(
			long companyId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws PortalException;

	public CommerceCurrency setActive(long commerceCurrencyId, boolean active)
		throws PortalException;

	public CommerceCurrency setPrimary(long commerceCurrencyId, boolean primary)
		throws PortalException;

	public CommerceCurrency updateCommerceCurrency(
			String externalReferenceCode, long commerceCurrencyId,
			Map<Locale, String> nameMap, String symbol, BigDecimal rate,
			Map<Locale, String> formatPatternMap, int maxFractionDigits,
			int minFractionDigits, String roundingMode, boolean primary,
			double priority, boolean active, ServiceContext serviceContext)
		throws PortalException;

	public void updateExchangeRate(
			long commerceCurrencyId, String exchangeRateProviderKey)
		throws PortalException;

	public void updateExchangeRates() throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-267213737