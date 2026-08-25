/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
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
 * Provides the local service utility for CommercePriceEntry. This utility wraps
 * <code>com.liferay.commerce.price.list.service.impl.CommercePriceEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceEntryLocalService
 * @generated
 */
public class CommercePriceEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.price.list.service.impl.CommercePriceEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce price entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceEntry the commerce price entry
	 * @return the commerce price entry that was added
	 */
	public static CommercePriceEntry addCommercePriceEntry(
		CommercePriceEntry commercePriceEntry) {

		return getService().addCommercePriceEntry(commercePriceEntry);
	}

	public static CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cProductId,
			String cpInstanceUuid, long commercePriceListId,
			java.math.BigDecimal price, boolean priceOnApplication,
			java.math.BigDecimal promoPrice, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePriceEntry(
			externalReferenceCode, cProductId, cpInstanceUuid,
			commercePriceListId, price, priceOnApplication, promoPrice,
			unitOfMeasureKey, serviceContext);
	}

	public static CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cProductId,
			String cpInstanceUuid, long commercePriceListId,
			boolean discountDiscovery, java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, java.math.BigDecimal price,
			boolean priceOnApplication, java.math.BigDecimal promoPrice,
			String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePriceEntry(
			externalReferenceCode, cProductId, cpInstanceUuid,
			commercePriceListId, discountDiscovery, discountLevel1,
			discountLevel2, discountLevel3, discountLevel4, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire, price,
			priceOnApplication, promoPrice, unitOfMeasureKey, serviceContext);
	}

	public static CommercePriceEntry addOrUpdateCommercePriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			long cProductId, String cpInstanceUuid, long commercePriceListId,
			boolean discountDiscovery, java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, java.math.BigDecimal price,
			boolean priceOnApplication, java.math.BigDecimal promoPrice,
			String skuExternalReferenceCode, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommercePriceEntry(
			externalReferenceCode, commercePriceEntryId, cProductId,
			cpInstanceUuid, commercePriceListId, discountDiscovery,
			discountLevel1, discountLevel2, discountLevel3, discountLevel4,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, price, priceOnApplication, promoPrice,
			skuExternalReferenceCode, unitOfMeasureKey, serviceContext);
	}

	public static void checkCommercePriceEntries() throws PortalException {
		getService().checkCommercePriceEntries();
	}

	/**
	 * Creates a new commerce price entry with the primary key. Does not add the commerce price entry to the database.
	 *
	 * @param commercePriceEntryId the primary key for the new commerce price entry
	 * @return the new commerce price entry
	 */
	public static CommercePriceEntry createCommercePriceEntry(
		long commercePriceEntryId) {

		return getService().createCommercePriceEntry(commercePriceEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCommercePriceEntries(long commercePriceListId)
		throws PortalException {

		getService().deleteCommercePriceEntries(commercePriceListId);
	}

	public static void deleteCommercePriceEntries(String cpInstanceUuid)
		throws PortalException {

		getService().deleteCommercePriceEntries(cpInstanceUuid);
	}

	public static void deleteCommercePriceEntries(
			String cpInstanceUuid, java.math.BigDecimal quantity,
			String unitOfMeasureKey)
		throws PortalException {

		getService().deleteCommercePriceEntries(
			cpInstanceUuid, quantity, unitOfMeasureKey);
	}

	/**
	 * Deletes the commerce price entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceEntry the commerce price entry
	 * @return the commerce price entry that was removed
	 * @throws PortalException
	 */
	public static CommercePriceEntry deleteCommercePriceEntry(
			CommercePriceEntry commercePriceEntry)
		throws PortalException {

		return getService().deleteCommercePriceEntry(commercePriceEntry);
	}

	/**
	 * Deletes the commerce price entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceEntryId the primary key of the commerce price entry
	 * @return the commerce price entry that was removed
	 * @throws PortalException if a commerce price entry with the primary key could not be found
	 */
	public static CommercePriceEntry deleteCommercePriceEntry(
			long commercePriceEntryId)
		throws PortalException {

		return getService().deleteCommercePriceEntry(commercePriceEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
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

	public static CommercePriceEntry fetchCommercePriceEntry(
		long commercePriceEntryId) {

		return getService().fetchCommercePriceEntry(commercePriceEntryId);
	}

	public static CommercePriceEntry fetchCommercePriceEntry(
		long commercePriceListId, String cpInstanceUuid, int status,
		String unitOfMeasureKey) {

		return getService().fetchCommercePriceEntry(
			commercePriceListId, cpInstanceUuid, status, unitOfMeasureKey);
	}

	public static CommercePriceEntry fetchCommercePriceEntry(
		long commercePriceListId, String cpInstanceUuid,
		String unitOfMeasureKey) {

		return getService().fetchCommercePriceEntry(
			commercePriceListId, cpInstanceUuid, unitOfMeasureKey);
	}

	public static CommercePriceEntry fetchCommercePriceEntry(
		long commercePriceListId, String cpInstanceUuid,
		String unitOfMeasureKey, boolean useAncestor) {

		return getService().fetchCommercePriceEntry(
			commercePriceListId, cpInstanceUuid, unitOfMeasureKey, useAncestor);
	}

	public static CommercePriceEntry
		fetchCommercePriceEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommercePriceEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce price entry with the matching UUID and company.
	 *
	 * @param uuid the commerce price entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchCommercePriceEntryByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchCommercePriceEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce price entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of commerce price entries
	 */
	public static List<CommercePriceEntry> getCommercePriceEntries(
		int start, int end) {

		return getService().getCommercePriceEntries(start, end);
	}

	public static List<CommercePriceEntry> getCommercePriceEntries(
		long commercePriceListId, int start, int end) {

		return getService().getCommercePriceEntries(
			commercePriceListId, start, end);
	}

	public static List<CommercePriceEntry> getCommercePriceEntries(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getService().getCommercePriceEntries(
			commercePriceListId, start, end, orderByComparator);
	}

	public static List<CommercePriceEntry> getCommercePriceEntries(
		String cpInstanceUuid, java.math.BigDecimal quantity,
		String unitOfMeasureKey) {

		return getService().getCommercePriceEntries(
			cpInstanceUuid, quantity, unitOfMeasureKey);
	}

	public static List<CommercePriceEntry> getCommercePriceEntriesByCompanyId(
		long companyId, int start, int end) {

		return getService().getCommercePriceEntriesByCompanyId(
			companyId, start, end);
	}

	/**
	 * Returns the number of commerce price entries.
	 *
	 * @return the number of commerce price entries
	 */
	public static int getCommercePriceEntriesCount() {
		return getService().getCommercePriceEntriesCount();
	}

	public static int getCommercePriceEntriesCount(long commercePriceListId) {
		return getService().getCommercePriceEntriesCount(commercePriceListId);
	}

	public static int getCommercePriceEntriesCountByCompanyId(long companyId) {
		return getService().getCommercePriceEntriesCountByCompanyId(companyId);
	}

	/**
	 * Returns the commerce price entry with the primary key.
	 *
	 * @param commercePriceEntryId the primary key of the commerce price entry
	 * @return the commerce price entry
	 * @throws PortalException if a commerce price entry with the primary key could not be found
	 */
	public static CommercePriceEntry getCommercePriceEntry(
			long commercePriceEntryId)
		throws PortalException {

		return getService().getCommercePriceEntry(commercePriceEntryId);
	}

	public static CommercePriceEntry
			getCommercePriceEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommercePriceEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce price entry with the matching UUID and company.
	 *
	 * @param uuid the commerce price entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price entry
	 * @throws PortalException if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry getCommercePriceEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getCommercePriceEntryByUuidAndCompanyId(
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

	public static CommercePriceEntry getInstanceBaseCommercePriceEntry(
		String cpInstanceUuid, String priceListType, String unitOfMeasureKey) {

		return getService().getInstanceBaseCommercePriceEntry(
			cpInstanceUuid, priceListType, unitOfMeasureKey);
	}

	public static List<CommercePriceEntry> getInstanceCommercePriceEntries(
		String cpInstanceUuid, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getService().getInstanceCommercePriceEntries(
			cpInstanceUuid, start, end, orderByComparator);
	}

	public static int getInstanceCommercePriceEntriesCount(
		String cpInstanceUuid) {

		return getService().getInstanceCommercePriceEntriesCount(
			cpInstanceUuid);
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
		<CommercePriceEntry> searchCommercePriceEntries(
				long companyId, long commercePriceListId, String keywords,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommercePriceEntries(
			companyId, commercePriceListId, keywords, start, end, sort);
	}

	public static int searchCommercePriceEntriesCount(
			long companyId, long commercePriceListId, String keywords)
		throws PortalException {

		return getService().searchCommercePriceEntriesCount(
			companyId, commercePriceListId, keywords);
	}

	public static CommercePriceEntry setHasTierPrice(
			long commercePriceEntryId, boolean hasTierPrice)
		throws PortalException {

		return getService().setHasTierPrice(commercePriceEntryId, hasTierPrice);
	}

	public static CommercePriceEntry setHasTierPrice(
			long commercePriceEntryId, boolean hasTierPrice,
			boolean bulkPricing)
		throws PortalException {

		return getService().setHasTierPrice(
			commercePriceEntryId, hasTierPrice, bulkPricing);
	}

	/**
	 * Updates the commerce price entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceEntry the commerce price entry
	 * @return the commerce price entry that was updated
	 */
	public static CommercePriceEntry updateCommercePriceEntry(
		CommercePriceEntry commercePriceEntry) {

		return getService().updateCommercePriceEntry(commercePriceEntry);
	}

	public static CommercePriceEntry updateCommercePriceEntry(
			long commercePriceEntryId, boolean bulkPricing,
			boolean discountDiscovery, java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, java.math.BigDecimal price,
			boolean priceOnApplication, java.math.BigDecimal promoPrice,
			String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommercePriceEntry(
			commercePriceEntryId, bulkPricing, discountDiscovery,
			discountLevel1, discountLevel2, discountLevel3, discountLevel4,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, price, priceOnApplication, promoPrice,
			unitOfMeasureKey, serviceContext);
	}

	public static CommercePriceEntry updateExternalReferenceCode(
			CommercePriceEntry commercePriceEntry, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			commercePriceEntry, externalReferenceCode);
	}

	public static CommercePriceEntry updatePricingInfo(
			long commercePriceEntryId, boolean bulkPricing,
			java.math.BigDecimal price, boolean priceOnApplication,
			java.math.BigDecimal promoPrice, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updatePricingInfo(
			commercePriceEntryId, bulkPricing, price, priceOnApplication,
			promoPrice, unitOfMeasureKey, serviceContext);
	}

	public static CommercePriceEntry updateStatus(
			long userId, long commercePriceEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, commercePriceEntryId, status, serviceContext,
			workflowContext);
	}

	public static CommercePriceEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePriceEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommercePriceEntryLocalServiceUtil.class,
			CommercePriceEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1584391167