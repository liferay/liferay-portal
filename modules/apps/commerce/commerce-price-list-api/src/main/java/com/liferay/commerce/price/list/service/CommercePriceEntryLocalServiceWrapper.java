/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommercePriceEntryLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceEntryLocalService
 * @generated
 */
public class CommercePriceEntryLocalServiceWrapper
	implements CommercePriceEntryLocalService,
			   ServiceWrapper<CommercePriceEntryLocalService> {

	public CommercePriceEntryLocalServiceWrapper() {
		this(null);
	}

	public CommercePriceEntryLocalServiceWrapper(
		CommercePriceEntryLocalService commercePriceEntryLocalService) {

		_commercePriceEntryLocalService = commercePriceEntryLocalService;
	}

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
	@Override
	public CommercePriceEntry addCommercePriceEntry(
		CommercePriceEntry commercePriceEntry) {

		return _commercePriceEntryLocalService.addCommercePriceEntry(
			commercePriceEntry);
	}

	@Override
	public CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cProductId,
			String cpInstanceUuid, long commercePriceListId,
			java.math.BigDecimal price, boolean priceOnApplication,
			java.math.BigDecimal promoPrice, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.addCommercePriceEntry(
			externalReferenceCode, cProductId, cpInstanceUuid,
			commercePriceListId, price, priceOnApplication, promoPrice,
			unitOfMeasureKey, serviceContext);
	}

	@Override
	public CommercePriceEntry addCommercePriceEntry(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.addCommercePriceEntry(
			externalReferenceCode, cProductId, cpInstanceUuid,
			commercePriceListId, discountDiscovery, discountLevel1,
			discountLevel2, discountLevel3, discountLevel4, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire, price,
			priceOnApplication, promoPrice, unitOfMeasureKey, serviceContext);
	}

	@Override
	public CommercePriceEntry addOrUpdateCommercePriceEntry(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.addOrUpdateCommercePriceEntry(
			externalReferenceCode, commercePriceEntryId, cProductId,
			cpInstanceUuid, commercePriceListId, discountDiscovery,
			discountLevel1, discountLevel2, discountLevel3, discountLevel4,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, price, priceOnApplication, promoPrice,
			skuExternalReferenceCode, unitOfMeasureKey, serviceContext);
	}

	@Override
	public void checkCommercePriceEntries()
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceEntryLocalService.checkCommercePriceEntries();
	}

	/**
	 * Creates a new commerce price entry with the primary key. Does not add the commerce price entry to the database.
	 *
	 * @param commercePriceEntryId the primary key for the new commerce price entry
	 * @return the new commerce price entry
	 */
	@Override
	public CommercePriceEntry createCommercePriceEntry(
		long commercePriceEntryId) {

		return _commercePriceEntryLocalService.createCommercePriceEntry(
			commercePriceEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCommercePriceEntries(long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceEntryLocalService.deleteCommercePriceEntries(
			commercePriceListId);
	}

	@Override
	public void deleteCommercePriceEntries(String cpInstanceUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceEntryLocalService.deleteCommercePriceEntries(
			cpInstanceUuid);
	}

	@Override
	public void deleteCommercePriceEntries(
			String cpInstanceUuid, java.math.BigDecimal quantity,
			String unitOfMeasureKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceEntryLocalService.deleteCommercePriceEntries(
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
	@Override
	public CommercePriceEntry deleteCommercePriceEntry(
			CommercePriceEntry commercePriceEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.deleteCommercePriceEntry(
			commercePriceEntry);
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
	@Override
	public CommercePriceEntry deleteCommercePriceEntry(
			long commercePriceEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.deleteCommercePriceEntry(
			commercePriceEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePriceEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePriceEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePriceEntryLocalService.dynamicQuery();
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

		return _commercePriceEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commercePriceEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commercePriceEntryLocalService.dynamicQuery(
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

		return _commercePriceEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commercePriceEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntry(
		long commercePriceEntryId) {

		return _commercePriceEntryLocalService.fetchCommercePriceEntry(
			commercePriceEntryId);
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntry(
		long commercePriceListId, String cpInstanceUuid, int status,
		String unitOfMeasureKey) {

		return _commercePriceEntryLocalService.fetchCommercePriceEntry(
			commercePriceListId, cpInstanceUuid, status, unitOfMeasureKey);
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntry(
		long commercePriceListId, String cpInstanceUuid,
		String unitOfMeasureKey) {

		return _commercePriceEntryLocalService.fetchCommercePriceEntry(
			commercePriceListId, cpInstanceUuid, unitOfMeasureKey);
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntry(
		long commercePriceListId, String cpInstanceUuid,
		String unitOfMeasureKey, boolean useAncestor) {

		return _commercePriceEntryLocalService.fetchCommercePriceEntry(
			commercePriceListId, cpInstanceUuid, unitOfMeasureKey, useAncestor);
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntryByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _commercePriceEntryLocalService.
			fetchCommercePriceEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce price entry with the matching UUID and company.
	 *
	 * @param uuid the commerce price entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	@Override
	public CommercePriceEntry fetchCommercePriceEntryByUuidAndCompanyId(
		String uuid, long companyId) {

		return _commercePriceEntryLocalService.
			fetchCommercePriceEntryByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePriceEntryLocalService.getActionableDynamicQuery();
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
	@Override
	public java.util.List<CommercePriceEntry> getCommercePriceEntries(
		int start, int end) {

		return _commercePriceEntryLocalService.getCommercePriceEntries(
			start, end);
	}

	@Override
	public java.util.List<CommercePriceEntry> getCommercePriceEntries(
		long commercePriceListId, int start, int end) {

		return _commercePriceEntryLocalService.getCommercePriceEntries(
			commercePriceListId, start, end);
	}

	@Override
	public java.util.List<CommercePriceEntry> getCommercePriceEntries(
		long commercePriceListId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommercePriceEntry>
			orderByComparator) {

		return _commercePriceEntryLocalService.getCommercePriceEntries(
			commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CommercePriceEntry> getCommercePriceEntries(
		String cpInstanceUuid, java.math.BigDecimal quantity,
		String unitOfMeasureKey) {

		return _commercePriceEntryLocalService.getCommercePriceEntries(
			cpInstanceUuid, quantity, unitOfMeasureKey);
	}

	@Override
	public java.util.List<CommercePriceEntry>
		getCommercePriceEntriesByCompanyId(long companyId, int start, int end) {

		return _commercePriceEntryLocalService.
			getCommercePriceEntriesByCompanyId(companyId, start, end);
	}

	/**
	 * Returns the number of commerce price entries.
	 *
	 * @return the number of commerce price entries
	 */
	@Override
	public int getCommercePriceEntriesCount() {
		return _commercePriceEntryLocalService.getCommercePriceEntriesCount();
	}

	@Override
	public int getCommercePriceEntriesCount(long commercePriceListId) {
		return _commercePriceEntryLocalService.getCommercePriceEntriesCount(
			commercePriceListId);
	}

	@Override
	public int getCommercePriceEntriesCountByCompanyId(long companyId) {
		return _commercePriceEntryLocalService.
			getCommercePriceEntriesCountByCompanyId(companyId);
	}

	/**
	 * Returns the commerce price entry with the primary key.
	 *
	 * @param commercePriceEntryId the primary key of the commerce price entry
	 * @return the commerce price entry
	 * @throws PortalException if a commerce price entry with the primary key could not be found
	 */
	@Override
	public CommercePriceEntry getCommercePriceEntry(long commercePriceEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.getCommercePriceEntry(
			commercePriceEntryId);
	}

	@Override
	public CommercePriceEntry getCommercePriceEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.
			getCommercePriceEntryByExternalReferenceCode(
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
	@Override
	public CommercePriceEntry getCommercePriceEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.
			getCommercePriceEntryByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commercePriceEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePriceEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public CommercePriceEntry getInstanceBaseCommercePriceEntry(
		String cpInstanceUuid, String priceListType, String unitOfMeasureKey) {

		return _commercePriceEntryLocalService.
			getInstanceBaseCommercePriceEntry(
				cpInstanceUuid, priceListType, unitOfMeasureKey);
	}

	@Override
	public java.util.List<CommercePriceEntry> getInstanceCommercePriceEntries(
		String cpInstanceUuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommercePriceEntry>
			orderByComparator) {

		return _commercePriceEntryLocalService.getInstanceCommercePriceEntries(
			cpInstanceUuid, start, end, orderByComparator);
	}

	@Override
	public int getInstanceCommercePriceEntriesCount(String cpInstanceUuid) {
		return _commercePriceEntryLocalService.
			getInstanceCommercePriceEntriesCount(cpInstanceUuid);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return _commercePriceEntryLocalService.search(searchContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePriceEntry> searchCommercePriceEntries(
				long companyId, long commercePriceListId, String keywords,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.searchCommercePriceEntries(
			companyId, commercePriceListId, keywords, start, end, sort);
	}

	@Override
	public int searchCommercePriceEntriesCount(
			long companyId, long commercePriceListId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.searchCommercePriceEntriesCount(
			companyId, commercePriceListId, keywords);
	}

	@Override
	public CommercePriceEntry setHasTierPrice(
			long commercePriceEntryId, boolean hasTierPrice)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.setHasTierPrice(
			commercePriceEntryId, hasTierPrice);
	}

	@Override
	public CommercePriceEntry setHasTierPrice(
			long commercePriceEntryId, boolean hasTierPrice,
			boolean bulkPricing)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.setHasTierPrice(
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
	@Override
	public CommercePriceEntry updateCommercePriceEntry(
		CommercePriceEntry commercePriceEntry) {

		return _commercePriceEntryLocalService.updateCommercePriceEntry(
			commercePriceEntry);
	}

	@Override
	public CommercePriceEntry updateCommercePriceEntry(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.updateCommercePriceEntry(
			commercePriceEntryId, bulkPricing, discountDiscovery,
			discountLevel1, discountLevel2, discountLevel3, discountLevel4,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, price, priceOnApplication, promoPrice,
			unitOfMeasureKey, serviceContext);
	}

	@Override
	public CommercePriceEntry updateExternalReferenceCode(
			CommercePriceEntry commercePriceEntry, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.updateExternalReferenceCode(
			commercePriceEntry, externalReferenceCode);
	}

	@Override
	public CommercePriceEntry updatePricingInfo(
			long commercePriceEntryId, boolean bulkPricing,
			java.math.BigDecimal price, boolean priceOnApplication,
			java.math.BigDecimal promoPrice, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.updatePricingInfo(
			commercePriceEntryId, bulkPricing, price, priceOnApplication,
			promoPrice, unitOfMeasureKey, serviceContext);
	}

	@Override
	public CommercePriceEntry updateStatus(
			long userId, long commercePriceEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryLocalService.updateStatus(
			userId, commercePriceEntryId, status, serviceContext,
			workflowContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePriceEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommercePriceEntry> getCTPersistence() {
		return _commercePriceEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<CommercePriceEntry> getModelClass() {
		return _commercePriceEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommercePriceEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commercePriceEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CommercePriceEntryLocalService getWrappedService() {
		return _commercePriceEntryLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePriceEntryLocalService commercePriceEntryLocalService) {

		_commercePriceEntryLocalService = commercePriceEntryLocalService;
	}

	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-424964244