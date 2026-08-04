/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service;

import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceCurrency. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Andrea Di Giorgi
 * @see CommerceCurrencyLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceCurrencyLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.currency.service.impl.CommerceCurrencyLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce currency local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceCurrencyLocalServiceUtil} if injection and service tracking are not available.
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
	@Indexable(type = IndexableType.REINDEX)
	public CommerceCurrency addCommerceCurrency(
		CommerceCurrency commerceCurrency);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceCurrency addCommerceCurrency(
			String externalReferenceCode, long userId, String code,
			Map<Locale, String> nameMap, String symbol, BigDecimal rate,
			Map<Locale, String> formatPatternMap, int maxFractionDigits,
			int minFractionDigits, String roundingMode, boolean primary,
			double priority, boolean active)
		throws PortalException;

	/**
	 * Creates a new commerce currency with the primary key. Does not add the commerce currency to the database.
	 *
	 * @param commerceCurrencyId the primary key for the new commerce currency
	 * @return the new commerce currency
	 */
	@Transactional(enabled = false)
	public CommerceCurrency createCommerceCurrency(long commerceCurrencyId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteCommerceCurrencies(long companyId);

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
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceCurrency deleteCommerceCurrency(
		CommerceCurrency commerceCurrency);

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
	@Indexable(type = IndexableType.DELETE)
	public CommerceCurrency deleteCommerceCurrency(long commerceCurrencyId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency fetchCommerceCurrency(long commerceCurrencyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency fetchCommerceCurrency(long companyId, String code);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency fetchCommerceCurrencyByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the commerce currency with the matching UUID and company.
	 *
	 * @param uuid the commerce currency's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce currency, or <code>null</code> if a matching commerce currency could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency fetchCommerceCurrencyByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency fetchPrimaryCommerceCurrency(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceCurrency> getCommerceCurrencies(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceCurrency> getCommerceCurrencies(
		long companyId, boolean active);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceCurrency> getCommerceCurrencies(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceCurrency> getCommerceCurrencies(
		long companyId, int start, int end,
		OrderByComparator<CommerceCurrency> orderByComparator);

	/**
	 * Returns the number of commerce currencies.
	 *
	 * @return the number of commerce currencies
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceCurrenciesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceCurrenciesCount(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceCurrenciesCount(long companyId, boolean active);

	/**
	 * Returns the commerce currency with the primary key.
	 *
	 * @param commerceCurrencyId the primary key of the commerce currency
	 * @return the commerce currency
	 * @throws PortalException if a commerce currency with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency getCommerceCurrency(long commerceCurrencyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency getCommerceCurrency(long companyId, String code)
		throws NoSuchCurrencyException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency getCommerceCurrencyByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the commerce currency with the matching UUID and company.
	 *
	 * @param uuid the commerce currency's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce currency
	 * @throws PortalException if a matching commerce currency could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency getCommerceCurrencyByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceCurrency getOrAddEmptyCommerceCurrency(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void importDefaultValues(
			boolean updateExchangeRate, ServiceContext serviceContext)
		throws Exception;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceCurrency> searchCommerceCurrencies(
			long companyId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceCurrency setActive(long commerceCurrencyId, boolean active)
		throws PortalException;

	public CommerceCurrency setPrimary(long commerceCurrencyId, boolean primary)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public CommerceCurrency updateCommerceCurrency(
		CommerceCurrency commerceCurrency);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceCurrency updateCommerceCurrency(
			String externalReferenceCode, long commerceCurrencyId,
			Map<Locale, String> nameMap, String symbol, BigDecimal rate,
			Map<Locale, String> formatPatternMap, int maxFractionDigits,
			int minFractionDigits, String roundingMode, boolean primary,
			double priority, boolean active, ServiceContext serviceContext)
		throws PortalException;

	public CommerceCurrency updateCommerceCurrencyRate(
			long commerceCurrencyId, BigDecimal rate)
		throws PortalException;

	public void updateExchangeRate(
			long commerceCurrencyId, String exchangeRateProviderKey)
		throws PortalException;

	public void updateExchangeRates() throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:105499712