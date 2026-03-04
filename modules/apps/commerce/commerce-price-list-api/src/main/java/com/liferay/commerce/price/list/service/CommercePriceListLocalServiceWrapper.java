/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommercePriceListLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListLocalService
 * @generated
 */
public class CommercePriceListLocalServiceWrapper
	implements CommercePriceListLocalService,
			   ServiceWrapper<CommercePriceListLocalService> {

	public CommercePriceListLocalServiceWrapper() {
		this(null);
	}

	public CommercePriceListLocalServiceWrapper(
		CommercePriceListLocalService commercePriceListLocalService) {

		_commercePriceListLocalService = commercePriceListLocalService;
	}

	@Override
	public CommercePriceList addCatalogBaseCommercePriceList(
			long groupId, long userId, String commerceCurrencyCode, String name,
			String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.addCatalogBaseCommercePriceList(
			groupId, userId, commerceCurrencyCode, name, type, serviceContext);
	}

	/**
	 * Adds the commerce price list to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceList the commerce price list
	 * @return the commerce price list that was added
	 */
	@Override
	public CommercePriceList addCommercePriceList(
		CommercePriceList commercePriceList) {

		return _commercePriceListLocalService.addCommercePriceList(
			commercePriceList);
	}

	@Override
	public CommercePriceList addCommercePriceList(
			String externalReferenceCode, long userId, long groupId,
			boolean catalogBasePriceList, String commerceCurrencyCode,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear, String name,
			boolean netPrice, boolean neverExpire,
			long parentCommercePriceListId, double priority, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.addCommercePriceList(
			externalReferenceCode, userId, groupId, catalogBasePriceList,
			commerceCurrencyCode, displayDateDay, displayDateHour,
			displayDateMinute, displayDateMonth, displayDateYear,
			expirationDateDay, expirationDateHour, expirationDateMinute,
			expirationDateMonth, expirationDateYear, name, netPrice,
			neverExpire, parentCommercePriceListId, priority, type,
			serviceContext);
	}

	@Override
	public CommercePriceList addOrUpdateCommercePriceList(
			String externalReferenceCode, long userId, long groupId,
			long commercePriceListId, boolean catalogBasePriceList,
			String commerceCurrencyCode, int displayDateDay,
			int displayDateHour, int displayDateMinute, int displayDateMonth,
			int displayDateYear, int expirationDateDay, int expirationDateHour,
			int expirationDateMinute, int expirationDateMonth,
			int expirationDateYear, String name, boolean netPrice,
			boolean neverExpire, long parentCommercePriceListId,
			double priority, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.addOrUpdateCommercePriceList(
			externalReferenceCode, userId, groupId, commercePriceListId,
			catalogBasePriceList, commerceCurrencyCode, displayDateDay,
			displayDateHour, displayDateMinute, displayDateMonth,
			displayDateYear, expirationDateDay, expirationDateHour,
			expirationDateMinute, expirationDateMonth, expirationDateYear, name,
			netPrice, neverExpire, parentCommercePriceListId, priority, type,
			serviceContext);
	}

	@Override
	public void checkCommercePriceLists()
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListLocalService.checkCommercePriceLists();
	}

	@Override
	public void cleanPriceListCache() {
		_commercePriceListLocalService.cleanPriceListCache();
	}

	/**
	 * Creates a new commerce price list with the primary key. Does not add the commerce price list to the database.
	 *
	 * @param commercePriceListId the primary key for the new commerce price list
	 * @return the new commerce price list
	 */
	@Override
	public CommercePriceList createCommercePriceList(long commercePriceListId) {
		return _commercePriceListLocalService.createCommercePriceList(
			commercePriceListId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce price list from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceList the commerce price list
	 * @return the commerce price list that was removed
	 * @throws PortalException
	 */
	@Override
	public CommercePriceList deleteCommercePriceList(
			CommercePriceList commercePriceList)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.deleteCommercePriceList(
			commercePriceList);
	}

	/**
	 * Deletes the commerce price list with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListId the primary key of the commerce price list
	 * @return the commerce price list that was removed
	 * @throws PortalException if a commerce price list with the primary key could not be found
	 */
	@Override
	public CommercePriceList deleteCommercePriceList(long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.deleteCommercePriceList(
			commercePriceListId);
	}

	@Override
	public void deleteCommercePriceLists(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListLocalService.deleteCommercePriceLists(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePriceListLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePriceListLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePriceListLocalService.dynamicQuery();
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

		return _commercePriceListLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListModelImpl</code>.
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

		return _commercePriceListLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListModelImpl</code>.
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

		return _commercePriceListLocalService.dynamicQuery(
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

		return _commercePriceListLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commercePriceListLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommercePriceList fetchCatalogBaseCommercePriceList(long groupId) {
		return _commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
			groupId);
	}

	@Override
	public CommercePriceList fetchCatalogBaseCommercePriceListByType(
		long groupId, String type) {

		return _commercePriceListLocalService.
			fetchCatalogBaseCommercePriceListByType(groupId, type);
	}

	@Override
	public CommercePriceList fetchCommercePriceList(long commercePriceListId) {
		return _commercePriceListLocalService.fetchCommercePriceList(
			commercePriceListId);
	}

	@Override
	public CommercePriceList fetchCommercePriceListByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _commercePriceListLocalService.
			fetchCommercePriceListByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce price list matching the UUID and group.
	 *
	 * @param uuid the commerce price list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce price list, or <code>null</code> if a matching commerce price list could not be found
	 */
	@Override
	public CommercePriceList fetchCommercePriceListByUuidAndGroupId(
		String uuid, long groupId) {

		return _commercePriceListLocalService.
			fetchCommercePriceListByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public CommercePriceList forceDeleteCommercePriceList(
			CommercePriceList commercePriceList)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.forceDeleteCommercePriceList(
			commercePriceList);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePriceListLocalService.getActionableDynamicQuery();
	}

	@Override
	public CommercePriceList getCatalogBaseCommercePriceList(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.getCatalogBaseCommercePriceList(
			groupId);
	}

	@Override
	public CommercePriceList getCatalogBaseCommercePriceListByType(
			long groupId, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.
			getCatalogBaseCommercePriceListByType(groupId, type);
	}

	/**
	 * Returns the commerce price list with the primary key.
	 *
	 * @param commercePriceListId the primary key of the commerce price list
	 * @return the commerce price list
	 * @throws PortalException if a commerce price list with the primary key could not be found
	 */
	@Override
	public CommercePriceList getCommercePriceList(long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.getCommercePriceList(
			commercePriceListId);
	}

	@Override
	public CommercePriceList getCommercePriceList(
			long groupId, long commerceAccountId,
			long[] commerceAccountGroupIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.getCommercePriceList(
			groupId, commerceAccountId, commerceAccountGroupIds);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByAccountAndChannelId(
		long groupId, long commerceAccountId, long commerceChannelId,
		String type) {

		return _commercePriceListLocalService.
			getCommercePriceListByAccountAndChannelId(
				groupId, commerceAccountId, commerceChannelId, type);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByAccountGroupIds(
		long groupId, long[] commerceAccountGroupIds, String type) {

		return _commercePriceListLocalService.
			getCommercePriceListByAccountGroupIds(
				groupId, commerceAccountGroupIds, type);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByAccountGroupsAndChannelId(
		long groupId, long[] commerceAccountGroupIds, long commerceChannelId,
		String type) {

		return _commercePriceListLocalService.
			getCommercePriceListByAccountGroupsAndChannelId(
				groupId, commerceAccountGroupIds, commerceChannelId, type);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByAccountId(
		long groupId, long commerceAccountId, String type) {

		return _commercePriceListLocalService.getCommercePriceListByAccountId(
			groupId, commerceAccountId, type);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByChannelId(
		long groupId, long commerceChannelId, String type) {

		return _commercePriceListLocalService.getCommercePriceListByChannelId(
			groupId, commerceChannelId, type);
	}

	@Override
	public CommercePriceList getCommercePriceListByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.
			getCommercePriceListByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CommercePriceList getCommercePriceListByLowestPrice(
			long groupId, long commerceAccountId,
			long[] commerceAccountGroupIds, long commerceChannelId,
			long commerceOrderTypeId, String cpInstanceUuid,
			String currencyCode, String type, String unitOfMeasureKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.getCommercePriceListByLowestPrice(
			groupId, commerceAccountId, commerceAccountGroupIds,
			commerceChannelId, commerceOrderTypeId, cpInstanceUuid,
			currencyCode, type, unitOfMeasureKey);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public CommercePriceList getCommercePriceListByUnqualified(
		long groupId, String type) {

		return _commercePriceListLocalService.getCommercePriceListByUnqualified(
			groupId, type);
	}

	/**
	 * Returns the commerce price list matching the UUID and group.
	 *
	 * @param uuid the commerce price list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce price list
	 * @throws PortalException if a matching commerce price list could not be found
	 */
	@Override
	public CommercePriceList getCommercePriceListByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.
			getCommercePriceListByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the commerce price lists.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price lists
	 * @param end the upper bound of the range of commerce price lists (not inclusive)
	 * @return the range of commerce price lists
	 */
	@Override
	public java.util.List<CommercePriceList> getCommercePriceLists(
		int start, int end) {

		return _commercePriceListLocalService.getCommercePriceLists(start, end);
	}

	@Override
	public java.util.List<CommercePriceList> getCommercePriceLists(
		long companyId, int start, int end) {

		return _commercePriceListLocalService.getCommercePriceLists(
			companyId, start, end);
	}

	@Override
	public java.util.List<CommercePriceList> getCommercePriceLists(
		long[] groupIds, long companyId, int start, int end) {

		return _commercePriceListLocalService.getCommercePriceLists(
			groupIds, companyId, start, end);
	}

	@Override
	public java.util.List<CommercePriceList> getCommercePriceLists(
		long[] groupIds, long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommercePriceList>
			orderByComparator) {

		return _commercePriceListLocalService.getCommercePriceLists(
			groupIds, companyId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CommercePriceList>
		getCommercePriceListsByAccountAndChannelAndOrderTypeId(
			long groupId, long commerceAccountId, long commerceChannelId,
			long commerceOrderTypeId, String currencyCode, String type) {

		return _commercePriceListLocalService.
			getCommercePriceListsByAccountAndChannelAndOrderTypeId(
				groupId, commerceAccountId, commerceChannelId,
				commerceOrderTypeId, currencyCode, type);
	}

	@Override
	public java.util.List<CommercePriceList>
		getCommercePriceListsByAccountAndChannelId(
			long groupId, long commerceAccountId, long commerceChannelId,
			String currencyCode, String type) {

		return _commercePriceListLocalService.
			getCommercePriceListsByAccountAndChannelId(
				groupId, commerceAccountId, commerceChannelId, currencyCode,
				type);
	}

	@Override
	public java.util.List<CommercePriceList>
		getCommercePriceListsByAccountAndOrderTypeId(
			long groupId, long commerceAccountId, long commerceOrderTypeId,
			String currencyCode, String type) {

		return _commercePriceListLocalService.
			getCommercePriceListsByAccountAndOrderTypeId(
				groupId, commerceAccountId, commerceOrderTypeId, currencyCode,
				type);
	}

	@Override
	public java.util.List<CommercePriceList>
		getCommercePriceListsByAccountGroupIds(
			long groupId, long[] commerceAccountGroupIds, String currencyCode,
			String type) {

		return _commercePriceListLocalService.
			getCommercePriceListsByAccountGroupIds(
				groupId, commerceAccountGroupIds, currencyCode, type);
	}

	@Override
	public java.util.List<CommercePriceList>
		getCommercePriceListsByAccountGroupsAndChannelAndOrderTypeId(
			long groupId, long[] commerceAccountGroupIds,
			long commerceChannelId, long commerceOrderTypeId,
			String currencyCode, String type) {

		return _commercePriceListLocalService.
			getCommercePriceListsByAccountGroupsAndChannelAndOrderTypeId(
				groupId, commerceAccountGroupIds, commerceChannelId,
				commerceOrderTypeId, currencyCode, type);
	}

	@Override
	public java.util.List<CommercePriceList>
		getCommercePriceListsByAccountGroupsAndChannelId(
			long groupId, long[] commerceAccountGroupIds,
			long commerceChannelId, String currencyCode, String type) {

		return _commercePriceListLocalService.
			getCommercePriceListsByAccountGroupsAndChannelId(
				groupId, commerceAccountGroupIds, commerceChannelId,
				currencyCode, type);
	}

	@Override
	public java.util.List<CommercePriceList>
		getCommercePriceListsByAccountGroupsAndOrderTypeId(
			long groupId, long[] commerceAccountGroupIds,
			long commerceOrderTypeId, String currencyCode, String type) {

		return _commercePriceListLocalService.
			getCommercePriceListsByAccountGroupsAndOrderTypeId(
				groupId, commerceAccountGroupIds, commerceOrderTypeId,
				currencyCode, type);
	}

	@Override
	public java.util.List<CommercePriceList> getCommercePriceListsByAccountId(
		long groupId, long commerceAccountId, String currencyCode,
		String type) {

		return _commercePriceListLocalService.getCommercePriceListsByAccountId(
			groupId, commerceAccountId, currencyCode, type);
	}

	@Override
	public java.util.List<CommercePriceList>
		getCommercePriceListsByChannelAndOrderTypeId(
			long groupId, long commerceChannelId, long commerceOrderTypeId,
			String currencyCode, String type) {

		return _commercePriceListLocalService.
			getCommercePriceListsByChannelAndOrderTypeId(
				groupId, commerceChannelId, commerceOrderTypeId, currencyCode,
				type);
	}

	@Override
	public java.util.List<CommercePriceList> getCommercePriceListsByChannelId(
		long groupId, long commerceChannelId, String currencyCode,
		String type) {

		return _commercePriceListLocalService.getCommercePriceListsByChannelId(
			groupId, commerceChannelId, currencyCode, type);
	}

	@Override
	public java.util.List<CommercePriceList> getCommercePriceListsByOrderTypeId(
		long groupId, long commerceOrderTypeId, String currencyCode,
		String type) {

		return _commercePriceListLocalService.
			getCommercePriceListsByOrderTypeId(
				groupId, commerceOrderTypeId, currencyCode, type);
	}

	@Override
	public java.util.List<CommercePriceList> getCommercePriceListsByUnqualified(
		long groupId, String currencyCode, String type) {

		return _commercePriceListLocalService.
			getCommercePriceListsByUnqualified(groupId, currencyCode, type);
	}

	/**
	 * Returns all the commerce price lists matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce price lists
	 * @param companyId the primary key of the company
	 * @return the matching commerce price lists, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CommercePriceList>
		getCommercePriceListsByUuidAndCompanyId(String uuid, long companyId) {

		return _commercePriceListLocalService.
			getCommercePriceListsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of commerce price lists matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce price lists
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce price lists
	 * @param end the upper bound of the range of commerce price lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce price lists, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CommercePriceList>
		getCommercePriceListsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CommercePriceList>
				orderByComparator) {

		return _commercePriceListLocalService.
			getCommercePriceListsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce price lists.
	 *
	 * @return the number of commerce price lists
	 */
	@Override
	public int getCommercePriceListsCount() {
		return _commercePriceListLocalService.getCommercePriceListsCount();
	}

	@Override
	public int getCommercePriceListsCount(
		long commercePricingClassId, String name) {

		return _commercePriceListLocalService.getCommercePriceListsCount(
			commercePricingClassId, name);
	}

	@Override
	public int getCommercePriceListsCount(
		long[] groupIds, long companyId, int status) {

		return _commercePriceListLocalService.getCommercePriceListsCount(
			groupIds, companyId, status);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commercePriceListLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePriceListLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceListLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return _commercePriceListLocalService.search(searchContext);
	}

	@Override
	public java.util.List<CommercePriceList> searchByCommercePricingClassId(
		long commercePricingClassId, String name, int start, int end) {

		return _commercePriceListLocalService.searchByCommercePricingClassId(
			commercePricingClassId, name, start, end);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePriceList> searchCommercePriceLists(
				long companyId, long[] groupIds, String keywords, int status,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.searchCommercePriceLists(
			companyId, groupIds, keywords, status, start, end, sort);
	}

	@Override
	public int searchCommercePriceListsCount(
			long companyId, long[] groupIds, String keywords, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.searchCommercePriceListsCount(
			companyId, groupIds, keywords, status);
	}

	@Override
	public CommercePriceList setCatalogBasePriceList(
			long commercePriceListId, boolean catalogBasePriceList)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.setCatalogBasePriceList(
			commercePriceListId, catalogBasePriceList);
	}

	@Override
	public void setCatalogBasePriceList(
			long groupId, long commercePriceListId, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListLocalService.setCatalogBasePriceList(
			groupId, commercePriceListId, type);
	}

	/**
	 * Updates the commerce price list in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceList the commerce price list
	 * @return the commerce price list that was updated
	 */
	@Override
	public CommercePriceList updateCommercePriceList(
		CommercePriceList commercePriceList) {

		return _commercePriceListLocalService.updateCommercePriceList(
			commercePriceList);
	}

	@Override
	public CommercePriceList updateCommercePriceList(
			long commercePriceListId, boolean catalogBasePriceList,
			String commerceCurrencyCode, int displayDateDay,
			int displayDateHour, int displayDateMinute, int displayDateMonth,
			int displayDateYear, int expirationDateDay, int expirationDateHour,
			int expirationDateMinute, int expirationDateMonth,
			int expirationDateYear, String name, boolean netPrice,
			boolean neverExpire, long parentCommercePriceListId,
			double priority, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.updateCommercePriceList(
			commercePriceListId, catalogBasePriceList, commerceCurrencyCode,
			displayDateDay, displayDateHour, displayDateMinute,
			displayDateMonth, displayDateYear, expirationDateDay,
			expirationDateHour, expirationDateMinute, expirationDateMonth,
			expirationDateYear, name, netPrice, neverExpire,
			parentCommercePriceListId, priority, type, serviceContext);
	}

	@Override
	public void updateCommercePriceListCurrencies(
			long companyId, String oldCommerceCurrencyCode,
			String newCommerceCurrencyCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListLocalService.updateCommercePriceListCurrencies(
			companyId, oldCommerceCurrencyCode, newCommerceCurrencyCode);
	}

	@Override
	public CommercePriceList updateExternalReferenceCode(
			CommercePriceList commercePriceList, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.updateExternalReferenceCode(
			commercePriceList, externalReferenceCode);
	}

	@Override
	public CommercePriceList updateStatus(
			long userId, long commercePriceListId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListLocalService.updateStatus(
			userId, commercePriceListId, status, serviceContext,
			workflowContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePriceListLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommercePriceList> getCTPersistence() {
		return _commercePriceListLocalService.getCTPersistence();
	}

	@Override
	public Class<CommercePriceList> getModelClass() {
		return _commercePriceListLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommercePriceList>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commercePriceListLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CommercePriceListLocalService getWrappedService() {
		return _commercePriceListLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePriceListLocalService commercePriceListLocalService) {

		_commercePriceListLocalService = commercePriceListLocalService;
	}

	private CommercePriceListLocalService _commercePriceListLocalService;

}