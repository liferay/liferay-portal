/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.service;

import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceWishList. This utility wraps
 * <code>com.liferay.commerce.wish.list.service.impl.CommerceWishListLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Andrea Di Giorgi
 * @see CommerceWishListLocalService
 * @generated
 */
public class CommerceWishListLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.wish.list.service.impl.CommerceWishListLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce wish list to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceWishListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceWishList the commerce wish list
	 * @return the commerce wish list that was added
	 */
	public static CommerceWishList addCommerceWishList(
		CommerceWishList commerceWishList) {

		return getService().addCommerceWishList(commerceWishList);
	}

	public static CommerceWishList addCommerceWishList(
			long userId, long groupId, String name, boolean defaultWishList)
		throws PortalException {

		return getService().addCommerceWishList(
			userId, groupId, name, defaultWishList);
	}

	/**
	 * Creates a new commerce wish list with the primary key. Does not add the commerce wish list to the database.
	 *
	 * @param commerceWishListId the primary key for the new commerce wish list
	 * @return the new commerce wish list
	 */
	public static CommerceWishList createCommerceWishList(
		long commerceWishListId) {

		return getService().createCommerceWishList(commerceWishListId);
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
	 * Deletes the commerce wish list from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceWishListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceWishList the commerce wish list
	 * @return the commerce wish list that was removed
	 * @throws PortalException
	 */
	public static CommerceWishList deleteCommerceWishList(
			CommerceWishList commerceWishList)
		throws PortalException {

		return getService().deleteCommerceWishList(commerceWishList);
	}

	/**
	 * Deletes the commerce wish list with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceWishListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceWishListId the primary key of the commerce wish list
	 * @return the commerce wish list that was removed
	 * @throws PortalException if a commerce wish list with the primary key could not be found
	 */
	public static CommerceWishList deleteCommerceWishList(
			long commerceWishListId)
		throws PortalException {

		return getService().deleteCommerceWishList(commerceWishListId);
	}

	public static void deleteCommerceWishLists(
		long userId, java.util.Date date) {

		getService().deleteCommerceWishLists(userId, date);
	}

	public static void deleteCommerceWishListsByGroupId(long groupId) {
		getService().deleteCommerceWishListsByGroupId(groupId);
	}

	public static void deleteCommerceWishListsByUserId(long userId) {
		getService().deleteCommerceWishListsByUserId(userId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.wish.list.model.impl.CommerceWishListModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.wish.list.model.impl.CommerceWishListModelImpl</code>.
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

	public static CommerceWishList fetchCommerceWishList(
		long commerceWishListId) {

		return getService().fetchCommerceWishList(commerceWishListId);
	}

	public static CommerceWishList fetchCommerceWishList(
		long userId, long groupId, boolean defaultWishList,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return getService().fetchCommerceWishList(
			userId, groupId, defaultWishList, orderByComparator);
	}

	/**
	 * Returns the commerce wish list matching the UUID and group.
	 *
	 * @param uuid the commerce wish list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce wish list, or <code>null</code> if a matching commerce wish list could not be found
	 */
	public static CommerceWishList fetchCommerceWishListByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchCommerceWishListByUuidAndGroupId(
			uuid, groupId);
	}

	public static CommerceWishList fetchDefaultCommerceWishList(
			long userId, long groupId, String guestUuid)
		throws PortalException {

		return getService().fetchDefaultCommerceWishList(
			userId, groupId, guestUuid);
	}

	public static CommerceWishList forceDeleteCommerceWishList(
		CommerceWishList commerceWishList) {

		return getService().forceDeleteCommerceWishList(commerceWishList);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce wish list with the primary key.
	 *
	 * @param commerceWishListId the primary key of the commerce wish list
	 * @return the commerce wish list
	 * @throws PortalException if a commerce wish list with the primary key could not be found
	 */
	public static CommerceWishList getCommerceWishList(long commerceWishListId)
		throws PortalException {

		return getService().getCommerceWishList(commerceWishListId);
	}

	/**
	 * Returns the commerce wish list matching the UUID and group.
	 *
	 * @param uuid the commerce wish list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce wish list
	 * @throws PortalException if a matching commerce wish list could not be found
	 */
	public static CommerceWishList getCommerceWishListByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCommerceWishListByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the commerce wish lists.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.wish.list.model.impl.CommerceWishListModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce wish lists
	 * @param end the upper bound of the range of commerce wish lists (not inclusive)
	 * @return the range of commerce wish lists
	 */
	public static List<CommerceWishList> getCommerceWishLists(
		int start, int end) {

		return getService().getCommerceWishLists(start, end);
	}

	public static List<CommerceWishList> getCommerceWishLists(
		long groupId, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return getService().getCommerceWishLists(
			groupId, start, end, orderByComparator);
	}

	public static List<CommerceWishList> getCommerceWishLists(
		long userId, long groupId, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return getService().getCommerceWishLists(
			userId, groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce wish lists matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce wish lists
	 * @param companyId the primary key of the company
	 * @return the matching commerce wish lists, or an empty list if no matches were found
	 */
	public static List<CommerceWishList> getCommerceWishListsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getCommerceWishListsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of commerce wish lists matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce wish lists
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce wish lists
	 * @param end the upper bound of the range of commerce wish lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce wish lists, or an empty list if no matches were found
	 */
	public static List<CommerceWishList> getCommerceWishListsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return getService().getCommerceWishListsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce wish lists.
	 *
	 * @return the number of commerce wish lists
	 */
	public static int getCommerceWishListsCount() {
		return getService().getCommerceWishListsCount();
	}

	public static int getCommerceWishListsCount(long groupId) {
		return getService().getCommerceWishListsCount(groupId);
	}

	public static int getCommerceWishListsCount(long userId, long groupId) {
		return getService().getCommerceWishListsCount(userId, groupId);
	}

	public static CommerceWishList getDefaultCommerceWishList(
			long userId, long groupId, String guestUuid)
		throws PortalException {

		return getService().getDefaultCommerceWishList(
			userId, groupId, guestUuid);
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

	/**
	 * Updates the commerce wish list in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceWishListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceWishList the commerce wish list
	 * @return the commerce wish list that was updated
	 */
	public static CommerceWishList updateCommerceWishList(
		CommerceWishList commerceWishList) {

		return getService().updateCommerceWishList(commerceWishList);
	}

	public static CommerceWishList updateCommerceWishList(
			long commerceWishListId, String name, boolean defaultWishList)
		throws PortalException {

		return getService().updateCommerceWishList(
			commerceWishListId, name, defaultWishList);
	}

	public static CommerceWishListLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceWishListLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceWishListLocalServiceUtil.class,
			CommerceWishListLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1579568768