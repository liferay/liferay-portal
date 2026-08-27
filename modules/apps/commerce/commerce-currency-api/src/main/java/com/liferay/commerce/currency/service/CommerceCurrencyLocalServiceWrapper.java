/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceCurrencyLocalService}.
 *
 * @author Andrea Di Giorgi
 * @see CommerceCurrencyLocalService
 * @generated
 */
public class CommerceCurrencyLocalServiceWrapper
	implements CommerceCurrencyLocalService,
			   ServiceWrapper<CommerceCurrencyLocalService> {

	public CommerceCurrencyLocalServiceWrapper() {
		this(null);
	}

	public CommerceCurrencyLocalServiceWrapper(
		CommerceCurrencyLocalService commerceCurrencyLocalService) {

		_commerceCurrencyLocalService = commerceCurrencyLocalService;
	}

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
	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
		addCommerceCurrency(
			com.liferay.commerce.currency.model.CommerceCurrency
				commerceCurrency) {

		return _commerceCurrencyLocalService.addCommerceCurrency(
			commerceCurrency);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			addCommerceCurrency(
				String externalReferenceCode, long userId, String code,
				java.util.Map<java.util.Locale, String> nameMap, String symbol,
				java.math.BigDecimal rate,
				java.util.Map<java.util.Locale, String> formatPatternMap,
				int maxFractionDigits, int minFractionDigits,
				String roundingMode, boolean primary, double priority,
				boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.addCommerceCurrency(
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
	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
		createCommerceCurrency(long commerceCurrencyId) {

		return _commerceCurrencyLocalService.createCommerceCurrency(
			commerceCurrencyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCommerceCurrencies(long companyId) {
		_commerceCurrencyLocalService.deleteCommerceCurrencies(companyId);
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
	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
		deleteCommerceCurrency(
			com.liferay.commerce.currency.model.CommerceCurrency
				commerceCurrency) {

		return _commerceCurrencyLocalService.deleteCommerceCurrency(
			commerceCurrency);
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
	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			deleteCommerceCurrency(long commerceCurrencyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.deleteCommerceCurrency(
			commerceCurrencyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceCurrencyLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceCurrencyLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceCurrencyLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _commerceCurrencyLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commerceCurrencyLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commerceCurrencyLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _commerceCurrencyLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _commerceCurrencyLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
		fetchCommerceCurrency(long commerceCurrencyId) {

		return _commerceCurrencyLocalService.fetchCommerceCurrency(
			commerceCurrencyId);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
		fetchCommerceCurrency(long companyId, String code) {

		return _commerceCurrencyLocalService.fetchCommerceCurrency(
			companyId, code);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
		fetchCommerceCurrencyByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceCurrencyLocalService.
			fetchCommerceCurrencyByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce currency with the matching UUID and company.
	 *
	 * @param uuid the commerce currency's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce currency, or <code>null</code> if a matching commerce currency could not be found
	 */
	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
		fetchCommerceCurrencyByUuidAndCompanyId(String uuid, long companyId) {

		return _commerceCurrencyLocalService.
			fetchCommerceCurrencyByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
		fetchPrimaryCommerceCurrency(long companyId) {

		return _commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
			companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceCurrencyLocalService.getActionableDynamicQuery();
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
	@Override
	public java.util.List<com.liferay.commerce.currency.model.CommerceCurrency>
		getCommerceCurrencies(int start, int end) {

		return _commerceCurrencyLocalService.getCommerceCurrencies(start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.currency.model.CommerceCurrency>
		getCommerceCurrencies(long companyId, boolean active) {

		return _commerceCurrencyLocalService.getCommerceCurrencies(
			companyId, active);
	}

	@Override
	public java.util.List<com.liferay.commerce.currency.model.CommerceCurrency>
		getCommerceCurrencies(
			long companyId, boolean active, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.currency.model.CommerceCurrency>
					orderByComparator) {

		return _commerceCurrencyLocalService.getCommerceCurrencies(
			companyId, active, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.currency.model.CommerceCurrency>
		getCommerceCurrencies(
			long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.currency.model.CommerceCurrency>
					orderByComparator) {

		return _commerceCurrencyLocalService.getCommerceCurrencies(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce currencies.
	 *
	 * @return the number of commerce currencies
	 */
	@Override
	public int getCommerceCurrenciesCount() {
		return _commerceCurrencyLocalService.getCommerceCurrenciesCount();
	}

	@Override
	public int getCommerceCurrenciesCount(long companyId) {
		return _commerceCurrencyLocalService.getCommerceCurrenciesCount(
			companyId);
	}

	@Override
	public int getCommerceCurrenciesCount(long companyId, boolean active) {
		return _commerceCurrencyLocalService.getCommerceCurrenciesCount(
			companyId, active);
	}

	/**
	 * Returns the commerce currency with the primary key.
	 *
	 * @param commerceCurrencyId the primary key of the commerce currency
	 * @return the commerce currency
	 * @throws PortalException if a commerce currency with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			getCommerceCurrency(long commerceCurrencyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.getCommerceCurrency(
			commerceCurrencyId);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			getCommerceCurrency(long companyId, String code)
		throws com.liferay.commerce.currency.exception.NoSuchCurrencyException {

		return _commerceCurrencyLocalService.getCommerceCurrency(
			companyId, code);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			getCommerceCurrencyByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.
			getCommerceCurrencyByExternalReferenceCode(
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
	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			getCommerceCurrencyByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.
			getCommerceCurrencyByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceCurrencyLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceCurrencyLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			getOrAddEmptyCommerceCurrency(
				String externalReferenceCode, long companyId, long userId,
				String code)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.getOrAddEmptyCommerceCurrency(
			externalReferenceCode, companyId, userId, code);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceCurrencyLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void importDefaultValues(
			boolean updateExchangeRate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		_commerceCurrencyLocalService.importDefaultValues(
			updateExchangeRate, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.currency.model.CommerceCurrency>
				searchCommerceCurrencies(
					long companyId, String keywords,
					java.util.LinkedHashMap<String, Object> params, int start,
					int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.searchCommerceCurrencies(
			companyId, keywords, params, start, end, sort);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency setActive(
			long commerceCurrencyId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.setActive(
			commerceCurrencyId, active);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency setPrimary(
			long commerceCurrencyId, boolean primary)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.setPrimary(
			commerceCurrencyId, primary);
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
	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
		updateCommerceCurrency(
			com.liferay.commerce.currency.model.CommerceCurrency
				commerceCurrency) {

		return _commerceCurrencyLocalService.updateCommerceCurrency(
			commerceCurrency);
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

		return _commerceCurrencyLocalService.updateCommerceCurrency(
			externalReferenceCode, commerceCurrencyId, nameMap, symbol, rate,
			formatPatternMap, maxFractionDigits, minFractionDigits,
			roundingMode, primary, priority, active, serviceContext);
	}

	@Override
	public com.liferay.commerce.currency.model.CommerceCurrency
			updateCommerceCurrencyRate(
				long commerceCurrencyId, java.math.BigDecimal rate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCurrencyLocalService.updateCommerceCurrencyRate(
			commerceCurrencyId, rate);
	}

	@Override
	public void updateExchangeRate(
			long commerceCurrencyId, String exchangeRateProviderKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceCurrencyLocalService.updateExchangeRate(
			commerceCurrencyId, exchangeRateProviderKey);
	}

	@Override
	public void updateExchangeRates()
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceCurrencyLocalService.updateExchangeRates();
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceCurrencyLocalService.getBasePersistence();
	}

	@Override
	public CommerceCurrencyLocalService getWrappedService() {
		return _commerceCurrencyLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceCurrencyLocalService commerceCurrencyLocalService) {

		_commerceCurrencyLocalService = commerceCurrencyLocalService;
	}

	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1418828198