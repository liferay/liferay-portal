/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceWishListLocalService}.
 *
 * @author Andrea Di Giorgi
 * @see CommerceWishListLocalService
 * @generated
 */
public class CommerceWishListLocalServiceWrapper
	implements CommerceWishListLocalService,
			   ServiceWrapper<CommerceWishListLocalService> {

	public CommerceWishListLocalServiceWrapper() {
		this(null);
	}

	public CommerceWishListLocalServiceWrapper(
		CommerceWishListLocalService commerceWishListLocalService) {

		_commerceWishListLocalService = commerceWishListLocalService;
	}

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
	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
		addCommerceWishList(
			com.liferay.commerce.wish.list.model.CommerceWishList
				commerceWishList) {

		return _commerceWishListLocalService.addCommerceWishList(
			commerceWishList);
	}

	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
			addCommerceWishList(
				long userId, long groupId, String name, boolean defaultWishList)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceWishListLocalService.addCommerceWishList(
			userId, groupId, name, defaultWishList);
	}

	/**
	 * Creates a new commerce wish list with the primary key. Does not add the commerce wish list to the database.
	 *
	 * @param commerceWishListId the primary key for the new commerce wish list
	 * @return the new commerce wish list
	 */
	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
		createCommerceWishList(long commerceWishListId) {

		return _commerceWishListLocalService.createCommerceWishList(
			commerceWishListId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceWishListLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
			deleteCommerceWishList(
				com.liferay.commerce.wish.list.model.CommerceWishList
					commerceWishList)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceWishListLocalService.deleteCommerceWishList(
			commerceWishList);
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
	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
			deleteCommerceWishList(long commerceWishListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceWishListLocalService.deleteCommerceWishList(
			commerceWishListId);
	}

	@Override
	public void deleteCommerceWishLists(long userId, java.util.Date date) {
		_commerceWishListLocalService.deleteCommerceWishLists(userId, date);
	}

	@Override
	public void deleteCommerceWishListsByGroupId(long groupId) {
		_commerceWishListLocalService.deleteCommerceWishListsByGroupId(groupId);
	}

	@Override
	public void deleteCommerceWishListsByUserId(long userId) {
		_commerceWishListLocalService.deleteCommerceWishListsByUserId(userId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceWishListLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceWishListLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceWishListLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceWishListLocalService.dynamicQuery();
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

		return _commerceWishListLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commerceWishListLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commerceWishListLocalService.dynamicQuery(
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

		return _commerceWishListLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commerceWishListLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
		fetchCommerceWishList(long commerceWishListId) {

		return _commerceWishListLocalService.fetchCommerceWishList(
			commerceWishListId);
	}

	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
		fetchCommerceWishList(
			long userId, long groupId, boolean defaultWishList,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.wish.list.model.CommerceWishList>
					orderByComparator) {

		return _commerceWishListLocalService.fetchCommerceWishList(
			userId, groupId, defaultWishList, orderByComparator);
	}

	/**
	 * Returns the commerce wish list matching the UUID and group.
	 *
	 * @param uuid the commerce wish list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce wish list, or <code>null</code> if a matching commerce wish list could not be found
	 */
	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
		fetchCommerceWishListByUuidAndGroupId(String uuid, long groupId) {

		return _commerceWishListLocalService.
			fetchCommerceWishListByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
			fetchDefaultCommerceWishList(
				long userId, long groupId, String guestUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceWishListLocalService.fetchDefaultCommerceWishList(
			userId, groupId, guestUuid);
	}

	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
		forceDeleteCommerceWishList(
			com.liferay.commerce.wish.list.model.CommerceWishList
				commerceWishList) {

		return _commerceWishListLocalService.forceDeleteCommerceWishList(
			commerceWishList);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceWishListLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce wish list with the primary key.
	 *
	 * @param commerceWishListId the primary key of the commerce wish list
	 * @return the commerce wish list
	 * @throws PortalException if a commerce wish list with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
			getCommerceWishList(long commerceWishListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceWishListLocalService.getCommerceWishList(
			commerceWishListId);
	}

	/**
	 * Returns the commerce wish list matching the UUID and group.
	 *
	 * @param uuid the commerce wish list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce wish list
	 * @throws PortalException if a matching commerce wish list could not be found
	 */
	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
			getCommerceWishListByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceWishListLocalService.
			getCommerceWishListByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<com.liferay.commerce.wish.list.model.CommerceWishList>
		getCommerceWishLists(int start, int end) {

		return _commerceWishListLocalService.getCommerceWishLists(start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.wish.list.model.CommerceWishList>
		getCommerceWishLists(
			long groupId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.wish.list.model.CommerceWishList>
					orderByComparator) {

		return _commerceWishListLocalService.getCommerceWishLists(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.wish.list.model.CommerceWishList>
		getCommerceWishLists(
			long userId, long groupId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.wish.list.model.CommerceWishList>
					orderByComparator) {

		return _commerceWishListLocalService.getCommerceWishLists(
			userId, groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce wish lists matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce wish lists
	 * @param companyId the primary key of the company
	 * @return the matching commerce wish lists, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.commerce.wish.list.model.CommerceWishList>
		getCommerceWishListsByUuidAndCompanyId(String uuid, long companyId) {

		return _commerceWishListLocalService.
			getCommerceWishListsByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<com.liferay.commerce.wish.list.model.CommerceWishList>
		getCommerceWishListsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.wish.list.model.CommerceWishList>
					orderByComparator) {

		return _commerceWishListLocalService.
			getCommerceWishListsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce wish lists.
	 *
	 * @return the number of commerce wish lists
	 */
	@Override
	public int getCommerceWishListsCount() {
		return _commerceWishListLocalService.getCommerceWishListsCount();
	}

	@Override
	public int getCommerceWishListsCount(long groupId) {
		return _commerceWishListLocalService.getCommerceWishListsCount(groupId);
	}

	@Override
	public int getCommerceWishListsCount(long userId, long groupId) {
		return _commerceWishListLocalService.getCommerceWishListsCount(
			userId, groupId);
	}

	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
			getDefaultCommerceWishList(
				long userId, long groupId, String guestUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceWishListLocalService.getDefaultCommerceWishList(
			userId, groupId, guestUuid);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceWishListLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceWishListLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceWishListLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceWishListLocalService.getPersistedModel(primaryKeyObj);
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
	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
		updateCommerceWishList(
			com.liferay.commerce.wish.list.model.CommerceWishList
				commerceWishList) {

		return _commerceWishListLocalService.updateCommerceWishList(
			commerceWishList);
	}

	@Override
	public com.liferay.commerce.wish.list.model.CommerceWishList
			updateCommerceWishList(
				long commerceWishListId, String name, boolean defaultWishList)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceWishListLocalService.updateCommerceWishList(
			commerceWishListId, name, defaultWishList);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceWishListLocalService.getBasePersistence();
	}

	@Override
	public CommerceWishListLocalService getWrappedService() {
		return _commerceWishListLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceWishListLocalService commerceWishListLocalService) {

		_commerceWishListLocalService = commerceWishListLocalService;
	}

	private CommerceWishListLocalService _commerceWishListLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1580409396