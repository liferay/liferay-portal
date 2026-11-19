/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.service;

import com.liferay.commerce.wish.list.model.CommerceWishListItem;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceWishListItem. This utility wraps
 * <code>com.liferay.commerce.wish.list.service.impl.CommerceWishListItemLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Andrea Di Giorgi
 * @see CommerceWishListItemLocalService
 * @generated
 */
public class CommerceWishListItemLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.wish.list.service.impl.CommerceWishListItemLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce wish list item to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceWishListItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceWishListItem the commerce wish list item
	 * @return the commerce wish list item that was added
	 */
	public static CommerceWishListItem addCommerceWishListItem(
		CommerceWishListItem commerceWishListItem) {

		return getService().addCommerceWishListItem(commerceWishListItem);
	}

	public static CommerceWishListItem addCommerceWishListItem(
			long userId, long commerceWishListId, String cpInstanceUuid,
			long cProductId, String json)
		throws PortalException {

		return getService().addCommerceWishListItem(
			userId, commerceWishListId, cpInstanceUuid, cProductId, json);
	}

	public static CommerceWishListItem addOrUpdateCommerceWishListItem(
			long userId, long commerceWishListId, String cpInstanceUuid,
			long cProductId, String json)
		throws PortalException {

		return getService().addOrUpdateCommerceWishListItem(
			userId, commerceWishListId, cpInstanceUuid, cProductId, json);
	}

	/**
	 * Creates a new commerce wish list item with the primary key. Does not add the commerce wish list item to the database.
	 *
	 * @param commerceWishListItemId the primary key for the new commerce wish list item
	 * @return the new commerce wish list item
	 */
	public static CommerceWishListItem createCommerceWishListItem(
		long commerceWishListItemId) {

		return getService().createCommerceWishListItem(commerceWishListItemId);
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
	 * Deletes the commerce wish list item from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceWishListItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceWishListItem the commerce wish list item
	 * @return the commerce wish list item that was removed
	 */
	public static CommerceWishListItem deleteCommerceWishListItem(
		CommerceWishListItem commerceWishListItem) {

		return getService().deleteCommerceWishListItem(commerceWishListItem);
	}

	/**
	 * Deletes the commerce wish list item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceWishListItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceWishListItemId the primary key of the commerce wish list item
	 * @return the commerce wish list item that was removed
	 * @throws PortalException if a commerce wish list item with the primary key could not be found
	 */
	public static CommerceWishListItem deleteCommerceWishListItem(
			long commerceWishListItemId)
		throws PortalException {

		return getService().deleteCommerceWishListItem(commerceWishListItemId);
	}

	public static void deleteCommerceWishListItems(long commerceWishListId) {
		getService().deleteCommerceWishListItems(commerceWishListId);
	}

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	public static void deleteCommerceWishListItemsByCPDefinitionId(
		long cpDefinitionId) {

		getService().deleteCommerceWishListItemsByCPDefinitionId(
			cpDefinitionId);
	}

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	public static void deleteCommerceWishListItemsByCPInstanceId(
		long cpInstanceId) {

		getService().deleteCommerceWishListItemsByCPInstanceId(cpInstanceId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.wish.list.model.impl.CommerceWishListItemModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.wish.list.model.impl.CommerceWishListItemModelImpl</code>.
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

	public static CommerceWishListItem fetchCommerceWishListItem(
		long commerceWishListItemId) {

		return getService().fetchCommerceWishListItem(commerceWishListItemId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce wish list item with the primary key.
	 *
	 * @param commerceWishListItemId the primary key of the commerce wish list item
	 * @return the commerce wish list item
	 * @throws PortalException if a commerce wish list item with the primary key could not be found
	 */
	public static CommerceWishListItem getCommerceWishListItem(
			long commerceWishListItemId)
		throws PortalException {

		return getService().getCommerceWishListItem(commerceWishListItemId);
	}

	public static CommerceWishListItem getCommerceWishListItem(
			long commerceWishListId, String cpInstanceUuid, long cProductId)
		throws PortalException {

		return getService().getCommerceWishListItem(
			commerceWishListId, cpInstanceUuid, cProductId);
	}

	/**
	 * Returns a range of all the commerce wish list items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.wish.list.model.impl.CommerceWishListItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce wish list items
	 * @param end the upper bound of the range of commerce wish list items (not inclusive)
	 * @return the range of commerce wish list items
	 */
	public static List<CommerceWishListItem> getCommerceWishListItems(
		int start, int end) {

		return getService().getCommerceWishListItems(start, end);
	}

	/**
	 * Returns the number of commerce wish list items.
	 *
	 * @return the number of commerce wish list items
	 */
	public static int getCommerceWishListItemsCount() {
		return getService().getCommerceWishListItemsCount();
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
	 * Updates the commerce wish list item in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceWishListItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceWishListItem the commerce wish list item
	 * @return the commerce wish list item that was updated
	 */
	public static CommerceWishListItem updateCommerceWishListItem(
		CommerceWishListItem commerceWishListItem) {

		return getService().updateCommerceWishListItem(commerceWishListItem);
	}

	public static CommerceWishListItem updateCommerceWishListItem(
			long commerceWishListId, String cpInstanceUuid, long cProductId,
			String json)
		throws PortalException {

		return getService().updateCommerceWishListItem(
			commerceWishListId, cpInstanceUuid, cProductId, json);
	}

	public static CommerceWishListItemLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceWishListItemLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceWishListItemLocalServiceUtil.class,
			CommerceWishListItemLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:540598323