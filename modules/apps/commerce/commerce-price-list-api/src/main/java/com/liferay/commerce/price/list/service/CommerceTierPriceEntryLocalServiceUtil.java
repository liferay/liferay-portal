/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
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
 * Provides the local service utility for CommerceTierPriceEntry. This utility wraps
 * <code>com.liferay.commerce.price.list.service.impl.CommerceTierPriceEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceTierPriceEntryLocalService
 * @generated
 */
public class CommerceTierPriceEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.price.list.service.impl.CommerceTierPriceEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce tier price entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTierPriceEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTierPriceEntry the commerce tier price entry
	 * @return the commerce tier price entry that was added
	 */
	public static CommerceTierPriceEntry addCommerceTierPriceEntry(
		CommerceTierPriceEntry commerceTierPriceEntry) {

		return getService().addCommerceTierPriceEntry(commerceTierPriceEntry);
	}

	public static CommerceTierPriceEntry addCommerceTierPriceEntry(
			long commercePriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal minQuantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceTierPriceEntry(
			commercePriceEntryId, price, promoPrice, minQuantity,
			serviceContext);
	}

	public static CommerceTierPriceEntry addCommerceTierPriceEntry(
			long commercePriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, boolean bulkPricing,
			java.math.BigDecimal minQuantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceTierPriceEntry(
			commercePriceEntryId, price, promoPrice, bulkPricing, minQuantity,
			serviceContext);
	}

	public static CommerceTierPriceEntry addCommerceTierPriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			java.math.BigDecimal price, java.math.BigDecimal promoPrice,
			java.math.BigDecimal minQuantity, boolean bulkPricing,
			boolean discountDiscovery, java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, promoPrice,
			minQuantity, bulkPricing, discountDiscovery, discountLevel1,
			discountLevel2, discountLevel3, discountLevel4, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceTierPriceEntry addCommerceTierPriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			java.math.BigDecimal price, java.math.BigDecimal promoPrice,
			java.math.BigDecimal minQuantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, promoPrice,
			minQuantity, serviceContext);
	}

	public static CommerceTierPriceEntry addCommerceTierPriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			java.math.BigDecimal price, java.math.BigDecimal promoPrice,
			boolean bulkPricing, java.math.BigDecimal minQuantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, promoPrice,
			bulkPricing, minQuantity, serviceContext);
	}

	public static CommerceTierPriceEntry addCommerceTierPriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			java.math.BigDecimal price, java.math.BigDecimal minQuantity,
			boolean bulkPricing, boolean discountDiscovery,
			java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, minQuantity,
			bulkPricing, discountDiscovery, discountLevel1, discountLevel2,
			discountLevel3, discountLevel4, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceTierPriceEntry addOrUpdateCommerceTierPriceEntry(
			String externalReferenceCode, long commerceTierPriceEntryId,
			long commercePriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal minQuantity,
			boolean bulkPricing, boolean discountDiscovery,
			java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String priceEntryExternalReferenceCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceTierPriceEntry(
			externalReferenceCode, commerceTierPriceEntryId,
			commercePriceEntryId, price, promoPrice, minQuantity, bulkPricing,
			discountDiscovery, discountLevel1, discountLevel2, discountLevel3,
			discountLevel4, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, priceEntryExternalReferenceCode,
			serviceContext);
	}

	/**
	 * This method is used to insert a new CommerceTierPriceEntry or update an
	 * existing one
	 *
	 * @param externalReferenceCode - The external identifier code from a 3rd
	 party system to be able to locate the same entity in the portal
	 <b>Only</b> used when updating an entity; the first entity with a
	 matching reference code one will be updated
	 * @param commerceTierPriceEntryId - <b>Only</b> used when updating an
	 entity; the matching one will be updated
	 * @param commercePriceEntryId - <b>Only</b> used when adding a new entity
	 * @param price
	 * @param promoPrice
	 * @param minQuantity
	 * @param priceEntryExternalReferenceCode - <b>Only</b> used when adding a
	 new entity, similar as <code>commercePriceEntryId</code> but the
	 external identifier code from a 3rd party system. If
	 commercePriceEntryId is used, it doesn't have any effect,
	 otherwise it tries to fetch the CommercePriceEntry against the
	 external code reference
	 * @param serviceContext
	 * @return CommerceTierPriceEntry
	 * @throws PortalException
	 */
	public static CommerceTierPriceEntry addOrUpdateCommerceTierPriceEntry(
			String externalReferenceCode, long commerceTierPriceEntryId,
			long commercePriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal minQuantity,
			String priceEntryExternalReferenceCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceTierPriceEntry(
			externalReferenceCode, commerceTierPriceEntryId,
			commercePriceEntryId, price, promoPrice, minQuantity,
			priceEntryExternalReferenceCode, serviceContext);
	}

	public static CommerceTierPriceEntry addOrUpdateCommerceTierPriceEntry(
			String externalReferenceCode, long commerceTierPriceEntryId,
			long commercePriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal minQuantity, boolean bulkPricing,
			boolean discountDiscovery, java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String priceEntryExternalReferenceCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceTierPriceEntry(
			externalReferenceCode, commerceTierPriceEntryId,
			commercePriceEntryId, price, minQuantity, bulkPricing,
			discountDiscovery, discountLevel1, discountLevel2, discountLevel3,
			discountLevel4, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, priceEntryExternalReferenceCode,
			serviceContext);
	}

	public static void checkCommerceTierPriceEntries() throws PortalException {
		getService().checkCommerceTierPriceEntries();
	}

	/**
	 * Creates a new commerce tier price entry with the primary key. Does not add the commerce tier price entry to the database.
	 *
	 * @param commerceTierPriceEntryId the primary key for the new commerce tier price entry
	 * @return the new commerce tier price entry
	 */
	public static CommerceTierPriceEntry createCommerceTierPriceEntry(
		long commerceTierPriceEntryId) {

		return getService().createCommerceTierPriceEntry(
			commerceTierPriceEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCommerceTierPriceEntries(long commercePriceEntryId)
		throws PortalException {

		getService().deleteCommerceTierPriceEntries(commercePriceEntryId);
	}

	/**
	 * Deletes the commerce tier price entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTierPriceEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTierPriceEntry the commerce tier price entry
	 * @return the commerce tier price entry that was removed
	 * @throws PortalException
	 */
	public static CommerceTierPriceEntry deleteCommerceTierPriceEntry(
			CommerceTierPriceEntry commerceTierPriceEntry)
		throws PortalException {

		return getService().deleteCommerceTierPriceEntry(
			commerceTierPriceEntry);
	}

	/**
	 * Deletes the commerce tier price entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTierPriceEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTierPriceEntryId the primary key of the commerce tier price entry
	 * @return the commerce tier price entry that was removed
	 * @throws PortalException if a commerce tier price entry with the primary key could not be found
	 */
	public static CommerceTierPriceEntry deleteCommerceTierPriceEntry(
			long commerceTierPriceEntryId)
		throws PortalException {

		return getService().deleteCommerceTierPriceEntry(
			commerceTierPriceEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommerceTierPriceEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommerceTierPriceEntryModelImpl</code>.
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

	public static CommerceTierPriceEntry fetchClosestCommerceTierPriceEntry(
		long commercePriceEntryId, java.math.BigDecimal minQuantity) {

		return getService().fetchClosestCommerceTierPriceEntry(
			commercePriceEntryId, minQuantity);
	}

	public static CommerceTierPriceEntry fetchCommerceTierPriceEntry(
		long commerceTierPriceEntryId) {

		return getService().fetchCommerceTierPriceEntry(
			commerceTierPriceEntryId);
	}

	public static CommerceTierPriceEntry
		fetchCommerceTierPriceEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommerceTierPriceEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce tier price entry with the matching UUID and company.
	 *
	 * @param uuid the commerce tier price entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	public static CommerceTierPriceEntry
		fetchCommerceTierPriceEntryByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchCommerceTierPriceEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce tier price entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommerceTierPriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce tier price entries
	 * @param end the upper bound of the range of commerce tier price entries (not inclusive)
	 * @return the range of commerce tier price entries
	 */
	public static List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		int start, int end) {

		return getService().getCommerceTierPriceEntries(start, end);
	}

	public static List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, java.math.BigDecimal minQuantity) {

		return getService().getCommerceTierPriceEntries(
			commercePriceEntryId, minQuantity);
	}

	public static List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, int status) {

		return getService().getCommerceTierPriceEntries(
			commercePriceEntryId, status);
	}

	public static List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, int start, int end) {

		return getService().getCommerceTierPriceEntries(
			commercePriceEntryId, start, end);
	}

	public static List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, int start, int end,
		OrderByComparator<CommerceTierPriceEntry> orderByComparator) {

		return getService().getCommerceTierPriceEntries(
			commercePriceEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce tier price entries.
	 *
	 * @return the number of commerce tier price entries
	 */
	public static int getCommerceTierPriceEntriesCount() {
		return getService().getCommerceTierPriceEntriesCount();
	}

	public static int getCommerceTierPriceEntriesCountByCompanyId(
		long companyId) {

		return getService().getCommerceTierPriceEntriesCountByCompanyId(
			companyId);
	}

	/**
	 * Returns the commerce tier price entry with the primary key.
	 *
	 * @param commerceTierPriceEntryId the primary key of the commerce tier price entry
	 * @return the commerce tier price entry
	 * @throws PortalException if a commerce tier price entry with the primary key could not be found
	 */
	public static CommerceTierPriceEntry getCommerceTierPriceEntry(
			long commerceTierPriceEntryId)
		throws PortalException {

		return getService().getCommerceTierPriceEntry(commerceTierPriceEntryId);
	}

	public static CommerceTierPriceEntry
			getCommerceTierPriceEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceTierPriceEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce tier price entry with the matching UUID and company.
	 *
	 * @param uuid the commerce tier price entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce tier price entry
	 * @throws PortalException if a matching commerce tier price entry could not be found
	 */
	public static CommerceTierPriceEntry
			getCommerceTierPriceEntryByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceTierPriceEntryByUuidAndCompanyId(
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

	public static com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return getService().search(searchContext);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceTierPriceEntry> searchCommerceTierPriceEntries(
				long companyId, long commercePriceEntryId, String keywords,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceTierPriceEntries(
			companyId, commercePriceEntryId, keywords, start, end, sort);
	}

	public static int searchCommerceTierPriceEntriesCount(
			long companyId, long commercePriceEntryId, String keywords)
		throws PortalException {

		return getService().searchCommerceTierPriceEntriesCount(
			companyId, commercePriceEntryId, keywords);
	}

	/**
	 * Updates the commerce tier price entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTierPriceEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTierPriceEntry the commerce tier price entry
	 * @return the commerce tier price entry that was updated
	 */
	public static CommerceTierPriceEntry updateCommerceTierPriceEntry(
		CommerceTierPriceEntry commerceTierPriceEntry) {

		return getService().updateCommerceTierPriceEntry(
			commerceTierPriceEntry);
	}

	public static CommerceTierPriceEntry updateCommerceTierPriceEntry(
			long commerceTierPriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal minQuantity,
			boolean bulkPricing, boolean discountDiscovery,
			java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceTierPriceEntry(
			commerceTierPriceEntryId, price, promoPrice, minQuantity,
			bulkPricing, discountDiscovery, discountLevel1, discountLevel2,
			discountLevel3, discountLevel4, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceTierPriceEntry updateCommerceTierPriceEntry(
			long commerceTierPriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal minQuantity,
			boolean bulkPricing,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceTierPriceEntry(
			commerceTierPriceEntryId, price, promoPrice, minQuantity,
			bulkPricing, serviceContext);
	}

	public static CommerceTierPriceEntry updateCommerceTierPriceEntry(
			long commerceTierPriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal minQuantity, boolean bulkPricing,
			boolean discountDiscovery, java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceTierPriceEntry(
			commerceTierPriceEntryId, price, minQuantity, bulkPricing,
			discountDiscovery, discountLevel1, discountLevel2, discountLevel3,
			discountLevel4, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	public static CommerceTierPriceEntry updateExternalReferenceCode(
			CommerceTierPriceEntry commerceTierPriceEntry,
			String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			commerceTierPriceEntry, externalReferenceCode);
	}

	public static CommerceTierPriceEntry updateStatus(
			long userId, long commerceTierPriceEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, commerceTierPriceEntryId, status, serviceContext,
			workflowContext);
	}

	public static CommerceTierPriceEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceTierPriceEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceTierPriceEntryLocalServiceUtil.class,
			CommerceTierPriceEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-954736868