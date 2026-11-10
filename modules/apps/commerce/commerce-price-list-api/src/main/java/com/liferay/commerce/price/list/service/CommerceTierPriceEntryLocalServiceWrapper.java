/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommerceTierPriceEntryLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceTierPriceEntryLocalService
 * @generated
 */
public class CommerceTierPriceEntryLocalServiceWrapper
	implements CommerceTierPriceEntryLocalService,
			   ServiceWrapper<CommerceTierPriceEntryLocalService> {

	public CommerceTierPriceEntryLocalServiceWrapper() {
		this(null);
	}

	public CommerceTierPriceEntryLocalServiceWrapper(
		CommerceTierPriceEntryLocalService commerceTierPriceEntryLocalService) {

		_commerceTierPriceEntryLocalService =
			commerceTierPriceEntryLocalService;
	}

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
	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
		CommerceTierPriceEntry commerceTierPriceEntry) {

		return _commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			commerceTierPriceEntry);
	}

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			long commercePriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal minQuantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			commercePriceEntryId, price, promoPrice, minQuantity,
			serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			long commercePriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, boolean bulkPricing,
			java.math.BigDecimal minQuantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			commercePriceEntryId, price, promoPrice, bulkPricing, minQuantity,
			serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, promoPrice,
			minQuantity, bulkPricing, discountDiscovery, discountLevel1,
			discountLevel2, discountLevel3, discountLevel4, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			java.math.BigDecimal price, java.math.BigDecimal promoPrice,
			java.math.BigDecimal minQuantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, promoPrice,
			minQuantity, serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			java.math.BigDecimal price, java.math.BigDecimal promoPrice,
			boolean bulkPricing, java.math.BigDecimal minQuantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, promoPrice,
			bulkPricing, minQuantity, serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addCommerceTierPriceEntry(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.addCommerceTierPriceEntry(
			externalReferenceCode, commercePriceEntryId, price, minQuantity,
			bulkPricing, discountDiscovery, discountLevel1, discountLevel2,
			discountLevel3, discountLevel4, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addOrUpdateCommerceTierPriceEntry(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.
			addOrUpdateCommerceTierPriceEntry(
				externalReferenceCode, commerceTierPriceEntryId,
				commercePriceEntryId, price, promoPrice, minQuantity,
				bulkPricing, discountDiscovery, discountLevel1, discountLevel2,
				discountLevel3, discountLevel4, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, priceEntryExternalReferenceCode, serviceContext);
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
	@Override
	public CommerceTierPriceEntry addOrUpdateCommerceTierPriceEntry(
			String externalReferenceCode, long commerceTierPriceEntryId,
			long commercePriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal minQuantity,
			String priceEntryExternalReferenceCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.
			addOrUpdateCommerceTierPriceEntry(
				externalReferenceCode, commerceTierPriceEntryId,
				commercePriceEntryId, price, promoPrice, minQuantity,
				priceEntryExternalReferenceCode, serviceContext);
	}

	@Override
	public CommerceTierPriceEntry addOrUpdateCommerceTierPriceEntry(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.
			addOrUpdateCommerceTierPriceEntry(
				externalReferenceCode, commerceTierPriceEntryId,
				commercePriceEntryId, price, minQuantity, bulkPricing,
				discountDiscovery, discountLevel1, discountLevel2,
				discountLevel3, discountLevel4, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, priceEntryExternalReferenceCode, serviceContext);
	}

	@Override
	public void checkCommerceTierPriceEntries()
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceTierPriceEntryLocalService.checkCommerceTierPriceEntries();
	}

	/**
	 * Creates a new commerce tier price entry with the primary key. Does not add the commerce tier price entry to the database.
	 *
	 * @param commerceTierPriceEntryId the primary key for the new commerce tier price entry
	 * @return the new commerce tier price entry
	 */
	@Override
	public CommerceTierPriceEntry createCommerceTierPriceEntry(
		long commerceTierPriceEntryId) {

		return _commerceTierPriceEntryLocalService.createCommerceTierPriceEntry(
			commerceTierPriceEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCommerceTierPriceEntries(long commercePriceEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceTierPriceEntryLocalService.deleteCommerceTierPriceEntries(
			commercePriceEntryId);
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
	@Override
	public CommerceTierPriceEntry deleteCommerceTierPriceEntry(
			CommerceTierPriceEntry commerceTierPriceEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.deleteCommerceTierPriceEntry(
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
	@Override
	public CommerceTierPriceEntry deleteCommerceTierPriceEntry(
			long commerceTierPriceEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.deleteCommerceTierPriceEntry(
			commerceTierPriceEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceTierPriceEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceTierPriceEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceTierPriceEntryLocalService.dynamicQuery();
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

		return _commerceTierPriceEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commerceTierPriceEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commerceTierPriceEntryLocalService.dynamicQuery(
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

		return _commerceTierPriceEntryLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _commerceTierPriceEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommerceTierPriceEntry fetchClosestCommerceTierPriceEntry(
		long commercePriceEntryId, java.math.BigDecimal minQuantity) {

		return _commerceTierPriceEntryLocalService.
			fetchClosestCommerceTierPriceEntry(
				commercePriceEntryId, minQuantity);
	}

	@Override
	public CommerceTierPriceEntry fetchCommerceTierPriceEntry(
		long commerceTierPriceEntryId) {

		return _commerceTierPriceEntryLocalService.fetchCommerceTierPriceEntry(
			commerceTierPriceEntryId);
	}

	@Override
	public CommerceTierPriceEntry
		fetchCommerceTierPriceEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceTierPriceEntryLocalService.
			fetchCommerceTierPriceEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce tier price entry with the matching UUID and company.
	 *
	 * @param uuid the commerce tier price entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce tier price entry, or <code>null</code> if a matching commerce tier price entry could not be found
	 */
	@Override
	public CommerceTierPriceEntry fetchCommerceTierPriceEntryByUuidAndCompanyId(
		String uuid, long companyId) {

		return _commerceTierPriceEntryLocalService.
			fetchCommerceTierPriceEntryByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceTierPriceEntryLocalService.getActionableDynamicQuery();
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
	@Override
	public java.util.List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		int start, int end) {

		return _commerceTierPriceEntryLocalService.getCommerceTierPriceEntries(
			start, end);
	}

	@Override
	public java.util.List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, java.math.BigDecimal minQuantity) {

		return _commerceTierPriceEntryLocalService.getCommerceTierPriceEntries(
			commercePriceEntryId, minQuantity);
	}

	@Override
	public java.util.List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, int status) {

		return _commerceTierPriceEntryLocalService.getCommerceTierPriceEntries(
			commercePriceEntryId, status);
	}

	@Override
	public java.util.List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, int start, int end) {

		return _commerceTierPriceEntryLocalService.getCommerceTierPriceEntries(
			commercePriceEntryId, start, end);
	}

	@Override
	public java.util.List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
		long commercePriceEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceTierPriceEntry>
			orderByComparator) {

		return _commerceTierPriceEntryLocalService.getCommerceTierPriceEntries(
			commercePriceEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce tier price entries.
	 *
	 * @return the number of commerce tier price entries
	 */
	@Override
	public int getCommerceTierPriceEntriesCount() {
		return _commerceTierPriceEntryLocalService.
			getCommerceTierPriceEntriesCount();
	}

	@Override
	public int getCommerceTierPriceEntriesCountByCompanyId(long companyId) {
		return _commerceTierPriceEntryLocalService.
			getCommerceTierPriceEntriesCountByCompanyId(companyId);
	}

	/**
	 * Returns the commerce tier price entry with the primary key.
	 *
	 * @param commerceTierPriceEntryId the primary key of the commerce tier price entry
	 * @return the commerce tier price entry
	 * @throws PortalException if a commerce tier price entry with the primary key could not be found
	 */
	@Override
	public CommerceTierPriceEntry getCommerceTierPriceEntry(
			long commerceTierPriceEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.getCommerceTierPriceEntry(
			commerceTierPriceEntryId);
	}

	@Override
	public CommerceTierPriceEntry
			getCommerceTierPriceEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.
			getCommerceTierPriceEntryByExternalReferenceCode(
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
	@Override
	public CommerceTierPriceEntry getCommerceTierPriceEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.
			getCommerceTierPriceEntryByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceTierPriceEntryLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceTierPriceEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceTierPriceEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return _commerceTierPriceEntryLocalService.search(searchContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceTierPriceEntry> searchCommerceTierPriceEntries(
				long companyId, long commercePriceEntryId, String keywords,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.
			searchCommerceTierPriceEntries(
				companyId, commercePriceEntryId, keywords, start, end, sort);
	}

	@Override
	public int searchCommerceTierPriceEntriesCount(
			long companyId, long commercePriceEntryId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.
			searchCommerceTierPriceEntriesCount(
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
	@Override
	public CommerceTierPriceEntry updateCommerceTierPriceEntry(
		CommerceTierPriceEntry commerceTierPriceEntry) {

		return _commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntry);
	}

	@Override
	public CommerceTierPriceEntry updateCommerceTierPriceEntry(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntryId, price, promoPrice, minQuantity,
			bulkPricing, discountDiscovery, discountLevel1, discountLevel2,
			discountLevel3, discountLevel4, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommerceTierPriceEntry updateCommerceTierPriceEntry(
			long commerceTierPriceEntryId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal minQuantity,
			boolean bulkPricing,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntryId, price, promoPrice, minQuantity,
			bulkPricing, serviceContext);
	}

	@Override
	public CommerceTierPriceEntry updateCommerceTierPriceEntry(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.updateCommerceTierPriceEntry(
			commerceTierPriceEntryId, price, minQuantity, bulkPricing,
			discountDiscovery, discountLevel1, discountLevel2, discountLevel3,
			discountLevel4, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Override
	public CommerceTierPriceEntry updateExternalReferenceCode(
			CommerceTierPriceEntry commerceTierPriceEntry,
			String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.updateExternalReferenceCode(
			commerceTierPriceEntry, externalReferenceCode);
	}

	@Override
	public CommerceTierPriceEntry updateStatus(
			long userId, long commerceTierPriceEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTierPriceEntryLocalService.updateStatus(
			userId, commerceTierPriceEntryId, status, serviceContext,
			workflowContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceTierPriceEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommerceTierPriceEntry> getCTPersistence() {
		return _commerceTierPriceEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<CommerceTierPriceEntry> getModelClass() {
		return _commerceTierPriceEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommerceTierPriceEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commerceTierPriceEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CommerceTierPriceEntryLocalService getWrappedService() {
		return _commerceTierPriceEntryLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceTierPriceEntryLocalService commerceTierPriceEntryLocalService) {

		_commerceTierPriceEntryLocalService =
			commerceTierPriceEntryLocalService;
	}

	private CommerceTierPriceEntryLocalService
		_commerceTierPriceEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1430756467