/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceList;
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
 * Provides the local service utility for CommercePriceList. This utility wraps
 * <code>com.liferay.commerce.price.list.service.impl.CommercePriceListLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListLocalService
 * @generated
 */
public class CommercePriceListLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.price.list.service.impl.CommercePriceListLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommercePriceList addCatalogBaseCommercePriceList(
			long groupId, long userId, String commerceCurrencyCode, String name,
			String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCatalogBaseCommercePriceList(
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
	public static CommercePriceList addCommercePriceList(
		CommercePriceList commercePriceList) {

		return getService().addCommercePriceList(commercePriceList);
	}

	public static CommercePriceList addCommercePriceList(
			String externalReferenceCode, long userId, long groupId,
			boolean catalogBasePriceList, String commerceCurrencyCode,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear, String name,
			boolean netPrice, boolean neverExpire,
			long parentCommercePriceListId, double priority, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePriceList(
			externalReferenceCode, userId, groupId, catalogBasePriceList,
			commerceCurrencyCode, displayDateDay, displayDateHour,
			displayDateMinute, displayDateMonth, displayDateYear,
			expirationDateDay, expirationDateHour, expirationDateMinute,
			expirationDateMonth, expirationDateYear, name, netPrice,
			neverExpire, parentCommercePriceListId, priority, type,
			serviceContext);
	}

	public static CommercePriceList addOrUpdateCommercePriceList(
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
		throws PortalException {

		return getService().addOrUpdateCommercePriceList(
			externalReferenceCode, userId, groupId, commercePriceListId,
			catalogBasePriceList, commerceCurrencyCode, displayDateDay,
			displayDateHour, displayDateMinute, displayDateMonth,
			displayDateYear, expirationDateDay, expirationDateHour,
			expirationDateMinute, expirationDateMonth, expirationDateYear, name,
			netPrice, neverExpire, parentCommercePriceListId, priority, type,
			serviceContext);
	}

	public static void checkCommercePriceLists() throws PortalException {
		getService().checkCommercePriceLists();
	}

	public static void cleanPriceListCache() {
		getService().cleanPriceListCache();
	}

	/**
	 * Creates a new commerce price list with the primary key. Does not add the commerce price list to the database.
	 *
	 * @param commercePriceListId the primary key for the new commerce price list
	 * @return the new commerce price list
	 */
	public static CommercePriceList createCommercePriceList(
		long commercePriceListId) {

		return getService().createCommercePriceList(commercePriceListId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static CommercePriceList deleteCommercePriceList(
			CommercePriceList commercePriceList)
		throws PortalException {

		return getService().deleteCommercePriceList(commercePriceList);
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
	public static CommercePriceList deleteCommercePriceList(
			long commercePriceListId)
		throws PortalException {

		return getService().deleteCommercePriceList(commercePriceListId);
	}

	public static void deleteCommercePriceLists(long companyId)
		throws PortalException {

		getService().deleteCommercePriceLists(companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListModelImpl</code>.
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

	public static CommercePriceList fetchCatalogBaseCommercePriceList(
		long groupId) {

		return getService().fetchCatalogBaseCommercePriceList(groupId);
	}

	public static CommercePriceList fetchCatalogBaseCommercePriceListByType(
		long groupId, String type) {

		return getService().fetchCatalogBaseCommercePriceListByType(
			groupId, type);
	}

	public static CommercePriceList fetchCommercePriceList(
		long commercePriceListId) {

		return getService().fetchCommercePriceList(commercePriceListId);
	}

	public static CommercePriceList
		fetchCommercePriceListByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommercePriceListByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce price list matching the UUID and group.
	 *
	 * @param uuid the commerce price list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce price list, or <code>null</code> if a matching commerce price list could not be found
	 */
	public static CommercePriceList fetchCommercePriceListByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchCommercePriceListByUuidAndGroupId(
			uuid, groupId);
	}

	public static CommercePriceList forceDeleteCommercePriceList(
			CommercePriceList commercePriceList)
		throws PortalException {

		return getService().forceDeleteCommercePriceList(commercePriceList);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static CommercePriceList getCatalogBaseCommercePriceList(
			long groupId)
		throws PortalException {

		return getService().getCatalogBaseCommercePriceList(groupId);
	}

	public static CommercePriceList getCatalogBaseCommercePriceListByType(
			long groupId, String type)
		throws PortalException {

		return getService().getCatalogBaseCommercePriceListByType(
			groupId, type);
	}

	/**
	 * Returns the commerce price list with the primary key.
	 *
	 * @param commercePriceListId the primary key of the commerce price list
	 * @return the commerce price list
	 * @throws PortalException if a commerce price list with the primary key could not be found
	 */
	public static CommercePriceList getCommercePriceList(
			long commercePriceListId)
		throws PortalException {

		return getService().getCommercePriceList(commercePriceListId);
	}

	public static CommercePriceList getCommercePriceList(
			long groupId, long commerceAccountId,
			long[] commerceAccountGroupIds)
		throws PortalException {

		return getService().getCommercePriceList(
			groupId, commerceAccountId, commerceAccountGroupIds);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	public static CommercePriceList getCommercePriceListByAccountAndChannelId(
		long groupId, long commerceAccountId, long commerceChannelId,
		String type) {

		return getService().getCommercePriceListByAccountAndChannelId(
			groupId, commerceAccountId, commerceChannelId, type);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	public static CommercePriceList getCommercePriceListByAccountGroupIds(
		long groupId, long[] commerceAccountGroupIds, String type) {

		return getService().getCommercePriceListByAccountGroupIds(
			groupId, commerceAccountGroupIds, type);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	public static CommercePriceList
		getCommercePriceListByAccountGroupsAndChannelId(
			long groupId, long[] commerceAccountGroupIds,
			long commerceChannelId, String type) {

		return getService().getCommercePriceListByAccountGroupsAndChannelId(
			groupId, commerceAccountGroupIds, commerceChannelId, type);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	public static CommercePriceList getCommercePriceListByAccountId(
		long groupId, long commerceAccountId, String type) {

		return getService().getCommercePriceListByAccountId(
			groupId, commerceAccountId, type);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	public static CommercePriceList getCommercePriceListByChannelId(
		long groupId, long commerceChannelId, String type) {

		return getService().getCommercePriceListByChannelId(
			groupId, commerceChannelId, type);
	}

	public static CommercePriceList getCommercePriceListByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommercePriceListByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommercePriceList getCommercePriceListByLowestPrice(
			long groupId, long commerceAccountId,
			long[] commerceAccountGroupIds, long commerceChannelId,
			long commerceOrderTypeId, String cpInstanceUuid,
			String currencyCode, String type, String unitOfMeasureKey)
		throws PortalException {

		return getService().getCommercePriceListByLowestPrice(
			groupId, commerceAccountId, commerceAccountGroupIds,
			commerceChannelId, commerceOrderTypeId, cpInstanceUuid,
			currencyCode, type, unitOfMeasureKey);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	public static CommercePriceList getCommercePriceListByUnqualified(
		long groupId, String type) {

		return getService().getCommercePriceListByUnqualified(groupId, type);
	}

	/**
	 * Returns the commerce price list matching the UUID and group.
	 *
	 * @param uuid the commerce price list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce price list
	 * @throws PortalException if a matching commerce price list could not be found
	 */
	public static CommercePriceList getCommercePriceListByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCommercePriceListByUuidAndGroupId(uuid, groupId);
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
	public static List<CommercePriceList> getCommercePriceLists(
		int start, int end) {

		return getService().getCommercePriceLists(start, end);
	}

	public static List<CommercePriceList> getCommercePriceLists(
		long companyId, int start, int end) {

		return getService().getCommercePriceLists(companyId, start, end);
	}

	public static List<CommercePriceList> getCommercePriceLists(
		long[] groupIds, long companyId, int start, int end) {

		return getService().getCommercePriceLists(
			groupIds, companyId, start, end);
	}

	public static List<CommercePriceList> getCommercePriceLists(
		long[] groupIds, long companyId, int status, int start, int end,
		OrderByComparator<CommercePriceList> orderByComparator) {

		return getService().getCommercePriceLists(
			groupIds, companyId, status, start, end, orderByComparator);
	}

	public static List<CommercePriceList>
		getCommercePriceListsByAccountAndChannelAndOrderTypeId(
			long groupId, long commerceAccountId, long commerceChannelId,
			long commerceOrderTypeId, String currencyCode, String type) {

		return getService().
			getCommercePriceListsByAccountAndChannelAndOrderTypeId(
				groupId, commerceAccountId, commerceChannelId,
				commerceOrderTypeId, currencyCode, type);
	}

	public static List<CommercePriceList>
		getCommercePriceListsByAccountAndChannelId(
			long groupId, long commerceAccountId, long commerceChannelId,
			String currencyCode, String type) {

		return getService().getCommercePriceListsByAccountAndChannelId(
			groupId, commerceAccountId, commerceChannelId, currencyCode, type);
	}

	public static List<CommercePriceList>
		getCommercePriceListsByAccountAndOrderTypeId(
			long groupId, long commerceAccountId, long commerceOrderTypeId,
			String currencyCode, String type) {

		return getService().getCommercePriceListsByAccountAndOrderTypeId(
			groupId, commerceAccountId, commerceOrderTypeId, currencyCode,
			type);
	}

	public static List<CommercePriceList>
		getCommercePriceListsByAccountGroupIds(
			long groupId, long[] commerceAccountGroupIds, String currencyCode,
			String type) {

		return getService().getCommercePriceListsByAccountGroupIds(
			groupId, commerceAccountGroupIds, currencyCode, type);
	}

	public static List<CommercePriceList>
		getCommercePriceListsByAccountGroupsAndChannelAndOrderTypeId(
			long groupId, long[] commerceAccountGroupIds,
			long commerceChannelId, long commerceOrderTypeId,
			String currencyCode, String type) {

		return getService().
			getCommercePriceListsByAccountGroupsAndChannelAndOrderTypeId(
				groupId, commerceAccountGroupIds, commerceChannelId,
				commerceOrderTypeId, currencyCode, type);
	}

	public static List<CommercePriceList>
		getCommercePriceListsByAccountGroupsAndChannelId(
			long groupId, long[] commerceAccountGroupIds,
			long commerceChannelId, String currencyCode, String type) {

		return getService().getCommercePriceListsByAccountGroupsAndChannelId(
			groupId, commerceAccountGroupIds, commerceChannelId, currencyCode,
			type);
	}

	public static List<CommercePriceList>
		getCommercePriceListsByAccountGroupsAndOrderTypeId(
			long groupId, long[] commerceAccountGroupIds,
			long commerceOrderTypeId, String currencyCode, String type) {

		return getService().getCommercePriceListsByAccountGroupsAndOrderTypeId(
			groupId, commerceAccountGroupIds, commerceOrderTypeId, currencyCode,
			type);
	}

	public static List<CommercePriceList> getCommercePriceListsByAccountId(
		long groupId, long commerceAccountId, String currencyCode,
		String type) {

		return getService().getCommercePriceListsByAccountId(
			groupId, commerceAccountId, currencyCode, type);
	}

	public static List<CommercePriceList>
		getCommercePriceListsByChannelAndOrderTypeId(
			long groupId, long commerceChannelId, long commerceOrderTypeId,
			String currencyCode, String type) {

		return getService().getCommercePriceListsByChannelAndOrderTypeId(
			groupId, commerceChannelId, commerceOrderTypeId, currencyCode,
			type);
	}

	public static List<CommercePriceList> getCommercePriceListsByChannelId(
		long groupId, long commerceChannelId, String currencyCode,
		String type) {

		return getService().getCommercePriceListsByChannelId(
			groupId, commerceChannelId, currencyCode, type);
	}

	public static List<CommercePriceList> getCommercePriceListsByOrderTypeId(
		long groupId, long commerceOrderTypeId, String currencyCode,
		String type) {

		return getService().getCommercePriceListsByOrderTypeId(
			groupId, commerceOrderTypeId, currencyCode, type);
	}

	public static List<CommercePriceList> getCommercePriceListsByUnqualified(
		long groupId, String currencyCode, String type) {

		return getService().getCommercePriceListsByUnqualified(
			groupId, currencyCode, type);
	}

	/**
	 * Returns all the commerce price lists matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce price lists
	 * @param companyId the primary key of the company
	 * @return the matching commerce price lists, or an empty list if no matches were found
	 */
	public static List<CommercePriceList>
		getCommercePriceListsByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getCommercePriceListsByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<CommercePriceList>
		getCommercePriceListsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CommercePriceList> orderByComparator) {

		return getService().getCommercePriceListsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce price lists.
	 *
	 * @return the number of commerce price lists
	 */
	public static int getCommercePriceListsCount() {
		return getService().getCommercePriceListsCount();
	}

	public static int getCommercePriceListsCount(
		long commercePricingClassId, String name) {

		return getService().getCommercePriceListsCount(
			commercePricingClassId, name);
	}

	public static int getCommercePriceListsCount(
		long[] groupIds, long companyId, int status) {

		return getService().getCommercePriceListsCount(
			groupIds, companyId, status);
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

	public static List<CommercePriceList> searchByCommercePricingClassId(
		long commercePricingClassId, String name, int start, int end) {

		return getService().searchByCommercePricingClassId(
			commercePricingClassId, name, start, end);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePriceList> searchCommercePriceLists(
				long companyId, long[] groupIds, String keywords, int status,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommercePriceLists(
			companyId, groupIds, keywords, status, start, end, sort);
	}

	public static int searchCommercePriceListsCount(
			long companyId, long[] groupIds, String keywords, int status)
		throws PortalException {

		return getService().searchCommercePriceListsCount(
			companyId, groupIds, keywords, status);
	}

	public static CommercePriceList setCatalogBasePriceList(
			long commercePriceListId, boolean catalogBasePriceList)
		throws PortalException {

		return getService().setCatalogBasePriceList(
			commercePriceListId, catalogBasePriceList);
	}

	public static void setCatalogBasePriceList(
			long groupId, long commercePriceListId, String type)
		throws PortalException {

		getService().setCatalogBasePriceList(
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
	public static CommercePriceList updateCommercePriceList(
		CommercePriceList commercePriceList) {

		return getService().updateCommercePriceList(commercePriceList);
	}

	public static CommercePriceList updateCommercePriceList(
			long commercePriceListId, boolean catalogBasePriceList,
			String commerceCurrencyCode, int displayDateDay,
			int displayDateHour, int displayDateMinute, int displayDateMonth,
			int displayDateYear, int expirationDateDay, int expirationDateHour,
			int expirationDateMinute, int expirationDateMonth,
			int expirationDateYear, String name, boolean netPrice,
			boolean neverExpire, long parentCommercePriceListId,
			double priority, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommercePriceList(
			commercePriceListId, catalogBasePriceList, commerceCurrencyCode,
			displayDateDay, displayDateHour, displayDateMinute,
			displayDateMonth, displayDateYear, expirationDateDay,
			expirationDateHour, expirationDateMinute, expirationDateMonth,
			expirationDateYear, name, netPrice, neverExpire,
			parentCommercePriceListId, priority, type, serviceContext);
	}

	public static void updateCommercePriceListCurrencies(
			long companyId, String oldCommerceCurrencyCode,
			String newCommerceCurrencyCode)
		throws PortalException {

		getService().updateCommercePriceListCurrencies(
			companyId, oldCommerceCurrencyCode, newCommerceCurrencyCode);
	}

	public static CommercePriceList updateExternalReferenceCode(
			CommercePriceList commercePriceList, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			commercePriceList, externalReferenceCode);
	}

	public static CommercePriceList updateStatus(
			long userId, long commercePriceListId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, commercePriceListId, status, serviceContext,
			workflowContext);
	}

	public static CommercePriceListLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePriceListLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommercePriceListLocalServiceUtil.class,
			CommercePriceListLocalService.class);

}