/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for CommerceCurrency. This utility wraps
 * <code>com.liferay.commerce.currency.service.impl.CommerceCurrencyLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Andrea Di Giorgi
 * @see CommerceCurrencyLocalService
 * @generated
 */
public class CommerceCurrencyLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.currency.service.impl.CommerceCurrencyLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce currency to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceCurrencyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceCurrency the commerce currency
	 * @return the commerce currency that was added
	 */
	public static CommerceCurrency addCommerceCurrency(
		CommerceCurrency commerceCurrency) {

		return getService().addCommerceCurrency(commerceCurrency);
	}

	public static CommerceCurrency addCommerceCurrency(
			String externalReferenceCode, long userId, String code,
			Map<java.util.Locale, String> nameMap, String symbol,
			java.math.BigDecimal rate,
			Map<java.util.Locale, String> formatPatternMap,
			int maxFractionDigits, int minFractionDigits, String roundingMode,
			boolean primary, double priority, boolean active)
		throws PortalException {

		return getService().addCommerceCurrency(
			externalReferenceCode, userId, code, nameMap, symbol, rate,
			formatPatternMap, maxFractionDigits, minFractionDigits,
			roundingMode, primary, priority, active);
	}

	/**
	 * Creates a new commerce currency with the primary key. Does not add the commerce currency to the database.
	 *
	 * @param commerceCurrencyId the primary key for the new commerce currency
	 * @return the new commerce currency
	 */
	public static CommerceCurrency createCommerceCurrency(
		long commerceCurrencyId) {

		return getService().createCommerceCurrency(commerceCurrencyId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCommerceCurrencies(long companyId) {
		getService().deleteCommerceCurrencies(companyId);
	}

	/**
	 * Deletes the commerce currency from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceCurrencyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceCurrency the commerce currency
	 * @return the commerce currency that was removed
	 */
	public static CommerceCurrency deleteCommerceCurrency(
		CommerceCurrency commerceCurrency) {

		return getService().deleteCommerceCurrency(commerceCurrency);
	}

	/**
	 * Deletes the commerce currency with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceCurrencyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceCurrencyId the primary key of the commerce currency
	 * @return the commerce currency that was removed
	 * @throws PortalException if a commerce currency with the primary key could not be found
	 */
	public static CommerceCurrency deleteCommerceCurrency(
			long commerceCurrencyId)
		throws PortalException {

		return getService().deleteCommerceCurrency(commerceCurrencyId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.currency.model.impl.CommerceCurrencyModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.currency.model.impl.CommerceCurrencyModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static CommerceCurrency fetchCommerceCurrency(
		long commerceCurrencyId) {

		return getService().fetchCommerceCurrency(commerceCurrencyId);
	}

	public static CommerceCurrency fetchCommerceCurrency(
		long companyId, String code) {

		return getService().fetchCommerceCurrency(companyId, code);
	}

	public static CommerceCurrency fetchCommerceCurrencyByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCommerceCurrencyByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce currency with the matching UUID and company.
	 *
	 * @param uuid the commerce currency's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce currency, or <code>null</code> if a matching commerce currency could not be found
	 */
	public static CommerceCurrency fetchCommerceCurrencyByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchCommerceCurrencyByUuidAndCompanyId(
			uuid, companyId);
	}

	public static CommerceCurrency fetchPrimaryCommerceCurrency(
		long companyId) {

		return getService().fetchPrimaryCommerceCurrency(companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce currencies.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.currency.model.impl.CommerceCurrencyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce currencies
	 * @param end the upper bound of the range of commerce currencies (not inclusive)
	 * @return the range of commerce currencies
	 */
	public static List<CommerceCurrency> getCommerceCurrencies(
		int start, int end) {

		return getService().getCommerceCurrencies(start, end);
	}

	public static List<CommerceCurrency> getCommerceCurrencies(
		long companyId, boolean active) {

		return getService().getCommerceCurrencies(companyId, active);
	}

	public static List<CommerceCurrency> getCommerceCurrencies(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator) {

		return getService().getCommerceCurrencies(
			companyId, active, start, end, orderByComparator);
	}

	public static List<CommerceCurrency> getCommerceCurrencies(
		long companyId, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator) {

		return getService().getCommerceCurrencies(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce currencies.
	 *
	 * @return the number of commerce currencies
	 */
	public static int getCommerceCurrenciesCount() {
		return getService().getCommerceCurrenciesCount();
	}

	public static int getCommerceCurrenciesCount(long companyId) {
		return getService().getCommerceCurrenciesCount(companyId);
	}

	public static int getCommerceCurrenciesCount(
		long companyId, boolean active) {

		return getService().getCommerceCurrenciesCount(companyId, active);
	}

	/**
	 * Returns the commerce currency with the primary key.
	 *
	 * @param commerceCurrencyId the primary key of the commerce currency
	 * @return the commerce currency
	 * @throws PortalException if a commerce currency with the primary key could not be found
	 */
	public static CommerceCurrency getCommerceCurrency(long commerceCurrencyId)
		throws PortalException {

		return getService().getCommerceCurrency(commerceCurrencyId);
	}

	public static CommerceCurrency getCommerceCurrency(
			long companyId, String code)
		throws com.liferay.commerce.currency.exception.NoSuchCurrencyException {

		return getService().getCommerceCurrency(companyId, code);
	}

	public static CommerceCurrency getCommerceCurrencyByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceCurrencyByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce currency with the matching UUID and company.
	 *
	 * @param uuid the commerce currency's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce currency
	 * @throws PortalException if a matching commerce currency could not be found
	 */
	public static CommerceCurrency getCommerceCurrencyByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceCurrencyByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static CommerceCurrency getOrAddEmptyCommerceCurrency(
			String externalReferenceCode, long companyId, long userId,
			String code)
		throws PortalException {

		return getService().getOrAddEmptyCommerceCurrency(
			externalReferenceCode, companyId, userId, code);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static void importDefaultValues(
			boolean updateExchangeRate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		getService().importDefaultValues(updateExchangeRate, serviceContext);
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

	/**
	 * Updates the commerce currency in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceCurrencyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceCurrency the commerce currency
	 * @return the commerce currency that was updated
	 */
	public static CommerceCurrency updateCommerceCurrency(
		CommerceCurrency commerceCurrency) {

		return getService().updateCommerceCurrency(commerceCurrency);
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

	public static CommerceCurrency updateCommerceCurrencyRate(
			long commerceCurrencyId, java.math.BigDecimal rate)
		throws PortalException {

		return getService().updateCommerceCurrencyRate(
			commerceCurrencyId, rate);
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

	public static CommerceCurrencyLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceCurrencyLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceCurrencyLocalServiceUtil.class,
			CommerceCurrencyLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1009874738