/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service;

import com.liferay.commerce.term.model.CTermEntryLocalization;
import com.liferay.commerce.term.model.CommerceTermEntry;
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
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.SearchContext;
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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceTermEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Luca Pellizzon
 * @see CommerceTermEntryLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceTermEntryLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.term.service.impl.CommerceTermEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce term entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceTermEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce term entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntry the commerce term entry
	 * @return the commerce term entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceTermEntry addCommerceTermEntry(
		CommerceTermEntry commerceTermEntry);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceTermEntry addCommerceTermEntry(
			String externalReferenceCode, long userId, boolean active,
			Map<Locale, String> descriptionMap, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, Map<Locale, String> labelMap, String name,
			double priority, String type, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException;

	public CTermEntryLocalization addCTermEntryLocalization(
			CommerceTermEntry commerceTermEntry, String languageId,
			String description, String label)
		throws PortalException;

	public void checkCommerceTermEntries() throws PortalException;

	/**
	 * Creates a new commerce term entry with the primary key. Does not add the commerce term entry to the database.
	 *
	 * @param commerceTermEntryId the primary key for the new commerce term entry
	 * @return the new commerce term entry
	 */
	@Transactional(enabled = false)
	public CommerceTermEntry createCommerceTermEntry(long commerceTermEntryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the commerce term entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntry the commerce term entry
	 * @return the commerce term entry that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceTermEntry deleteCommerceTermEntry(
			CommerceTermEntry commerceTermEntry)
		throws PortalException;

	/**
	 * Deletes the commerce term entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntryId the primary key of the commerce term entry
	 * @return the commerce term entry that was removed
	 * @throws PortalException if a commerce term entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceTermEntry deleteCommerceTermEntry(long commerceTermEntryId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.term.model.impl.CommerceTermEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.term.model.impl.CommerceTermEntryModelImpl</code>.
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
	public CommerceTermEntry fetchCommerceTermEntry(long commerceTermEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceTermEntry fetchCommerceTermEntryByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the commerce term entry with the matching UUID and company.
	 *
	 * @param uuid the commerce term entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceTermEntry fetchCommerceTermEntryByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CTermEntryLocalization fetchCTermEntryLocalization(
		long commerceTermEntryId, String languageId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns a range of all the commerce term entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.term.model.impl.CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of commerce term entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceTermEntry> getCommerceTermEntries(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceTermEntry> getCommerceTermEntries(
		long companyId, String type);

	/**
	 * Returns the number of commerce term entries.
	 *
	 * @return the number of commerce term entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceTermEntriesCount();

	/**
	 * Returns the commerce term entry with the primary key.
	 *
	 * @param commerceTermEntryId the primary key of the commerce term entry
	 * @return the commerce term entry
	 * @throws PortalException if a commerce term entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceTermEntry getCommerceTermEntry(long commerceTermEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceTermEntry getCommerceTermEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the commerce term entry with the matching UUID and company.
	 *
	 * @param uuid the commerce term entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce term entry
	 * @throws PortalException if a matching commerce term entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceTermEntry getCommerceTermEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CTermEntryLocalization getCTermEntryLocalization(
			long commerceTermEntryId, String languageId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<String> getCTermEntryLocalizationLanguageIds(
		long commerceTermEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CTermEntryLocalization> getCTermEntryLocalizations(
		long commerceTermEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceTermEntry> getDeliveryCommerceTermEntries(
		long companyId, long commerceOrderTypeId,
		long commerceShippingOptionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDeliveryCommerceTermEntriesCount(
		long companyId, long commerceOrderTypeId,
		long commerceShippingOptionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceTermEntry> getPaymentCommerceTermEntries(
		long companyId, long commerceOrderTypeId,
		long commercePaymentMethodGroupRelId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPaymentCommerceTermEntriesCount(
		long companyId, long commerceOrderTypeId,
		long commercePaymentMethodGroupRelId);

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(SearchContext searchContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceTermEntry> searchCommerceTermEntries(
			long companyId, long accountEntryId, String type, String keywords,
			int start, int end, Sort sort)
		throws PortalException;

	/**
	 * Updates the commerce term entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntry the commerce term entry
	 * @return the commerce term entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceTermEntry updateCommerceTermEntry(
		CommerceTermEntry commerceTermEntry);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceTermEntry updateCommerceTermEntry(
			long userId, long commerceTermEntryId, boolean active,
			Map<Locale, String> descriptionMap, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, Map<Locale, String> labelMap, String name,
			double priority, String typeSettings, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceTermEntry updateCommerceTermEntryExternalReferenceCode(
			String externalReferenceCode, long commerceTermEntryId)
		throws PortalException;

	public CTermEntryLocalization updateCTermEntryLocalization(
			CommerceTermEntry commerceTermEntry, String languageId,
			String description, String label)
		throws PortalException;

	public List<CTermEntryLocalization> updateCTermEntryLocalizations(
			CommerceTermEntry commerceTermEntry,
			Map<String, String> descriptionMap, Map<String, String> labelMap)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceTermEntry updateStatus(
			long userId, long commerceTermEntryId, int status,
			ServiceContext serviceContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1818086450