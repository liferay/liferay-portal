/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceInventoryWarehouseRelLocalService}.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryWarehouseRelLocalService
 * @generated
 */
public class CommerceInventoryWarehouseRelLocalServiceWrapper
	implements CommerceInventoryWarehouseRelLocalService,
			   ServiceWrapper<CommerceInventoryWarehouseRelLocalService> {

	public CommerceInventoryWarehouseRelLocalServiceWrapper() {
		this(null);
	}

	public CommerceInventoryWarehouseRelLocalServiceWrapper(
		CommerceInventoryWarehouseRelLocalService
			commerceInventoryWarehouseRelLocalService) {

		_commerceInventoryWarehouseRelLocalService =
			commerceInventoryWarehouseRelLocalService;
	}

	/**
	 * Adds the commerce inventory warehouse rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseRel the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel that was added
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel
		addCommerceInventoryWarehouseRel(
			com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel
				commerceInventoryWarehouseRel) {

		return _commerceInventoryWarehouseRelLocalService.
			addCommerceInventoryWarehouseRel(commerceInventoryWarehouseRel);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel
			addCommerceInventoryWarehouseRel(
				long userId, String className, long classPK,
				long commerceInventoryWarehouseId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseRelLocalService.
			addCommerceInventoryWarehouseRel(
				userId, className, classPK, commerceInventoryWarehouseId);
	}

	/**
	 * Creates a new commerce inventory warehouse rel with the primary key. Does not add the commerce inventory warehouse rel to the database.
	 *
	 * @param commerceInventoryWarehouseRelId the primary key for the new commerce inventory warehouse rel
	 * @return the new commerce inventory warehouse rel
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel
		createCommerceInventoryWarehouseRel(
			long commerceInventoryWarehouseRelId) {

		return _commerceInventoryWarehouseRelLocalService.
			createCommerceInventoryWarehouseRel(
				commerceInventoryWarehouseRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce inventory warehouse rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseRel the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel
			deleteCommerceInventoryWarehouseRel(
				com.liferay.commerce.inventory.model.
					CommerceInventoryWarehouseRel commerceInventoryWarehouseRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseRelLocalService.
			deleteCommerceInventoryWarehouseRel(commerceInventoryWarehouseRel);
	}

	/**
	 * Deletes the commerce inventory warehouse rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseRelId the primary key of the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel that was removed
	 * @throws PortalException if a commerce inventory warehouse rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel
			deleteCommerceInventoryWarehouseRel(
				long commerceInventoryWarehouseRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseRelLocalService.
			deleteCommerceInventoryWarehouseRel(
				commerceInventoryWarehouseRelId);
	}

	@Override
	public void deleteCommerceInventoryWarehouseRels(
			long commerceInventoryWarehouseId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceInventoryWarehouseRelLocalService.
			deleteCommerceInventoryWarehouseRels(commerceInventoryWarehouseId);
	}

	@Override
	public void deleteCommerceInventoryWarehouseRels(
			String className, long commerceInventoryWarehouseId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceInventoryWarehouseRelLocalService.
			deleteCommerceInventoryWarehouseRels(
				className, commerceInventoryWarehouseId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceInventoryWarehouseRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceInventoryWarehouseRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceInventoryWarehouseRelLocalService.dynamicQuery();
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

		return _commerceInventoryWarehouseRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseRelModelImpl</code>.
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

		return _commerceInventoryWarehouseRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseRelModelImpl</code>.
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

		return _commerceInventoryWarehouseRelLocalService.dynamicQuery(
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

		return _commerceInventoryWarehouseRelLocalService.dynamicQueryCount(
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

		return _commerceInventoryWarehouseRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel
		fetchCommerceInventoryWarehouseRel(
			long commerceInventoryWarehouseRelId) {

		return _commerceInventoryWarehouseRelLocalService.
			fetchCommerceInventoryWarehouseRel(commerceInventoryWarehouseRelId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel>
			getAccountEntryCommerceInventoryWarehouseRels(
				long commerceInventoryWarehouseId, String keywords, int start,
				int end) {

		return _commerceInventoryWarehouseRelLocalService.
			getAccountEntryCommerceInventoryWarehouseRels(
				commerceInventoryWarehouseId, keywords, start, end);
	}

	@Override
	public int getAccountEntryCommerceInventoryWarehouseRelsCount(
		long commerceInventoryWarehouseId, String keywords) {

		return _commerceInventoryWarehouseRelLocalService.
			getAccountEntryCommerceInventoryWarehouseRelsCount(
				commerceInventoryWarehouseId, keywords);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel>
			getAccountGroupCommerceInventoryWarehouseRels(
				long commerceInventoryWarehouseId, String keywords, int start,
				int end) {

		return _commerceInventoryWarehouseRelLocalService.
			getAccountGroupCommerceInventoryWarehouseRels(
				commerceInventoryWarehouseId, keywords, start, end);
	}

	@Override
	public int getAccountGroupCommerceInventoryWarehouseRelsCount(
		long commerceInventoryWarehouseId, String keywords) {

		return _commerceInventoryWarehouseRelLocalService.
			getAccountGroupCommerceInventoryWarehouseRelsCount(
				commerceInventoryWarehouseId, keywords);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceInventoryWarehouseRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce inventory warehouse rel with the primary key.
	 *
	 * @param commerceInventoryWarehouseRelId the primary key of the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel
	 * @throws PortalException if a commerce inventory warehouse rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel
			getCommerceInventoryWarehouseRel(
				long commerceInventoryWarehouseRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseRelLocalService.
			getCommerceInventoryWarehouseRel(commerceInventoryWarehouseRelId);
	}

	/**
	 * Returns a range of all the commerce inventory warehouse rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory warehouse rels
	 * @param end the upper bound of the range of commerce inventory warehouse rels (not inclusive)
	 * @return the range of commerce inventory warehouse rels
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel>
			getCommerceInventoryWarehouseRels(int start, int end) {

		return _commerceInventoryWarehouseRelLocalService.
			getCommerceInventoryWarehouseRels(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel>
			getCommerceInventoryWarehouseRels(
				String className, long commerceInventoryWarehouseId) {

		return _commerceInventoryWarehouseRelLocalService.
			getCommerceInventoryWarehouseRels(
				className, commerceInventoryWarehouseId);
	}

	/**
	 * Returns the number of commerce inventory warehouse rels.
	 *
	 * @return the number of commerce inventory warehouse rels
	 */
	@Override
	public int getCommerceInventoryWarehouseRelsCount() {
		return _commerceInventoryWarehouseRelLocalService.
			getCommerceInventoryWarehouseRelsCount();
	}

	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel>
			getCommerceOrderTypeCommerceInventoryWarehouseRels(
				long commerceInventoryWarehouseId, String keywords, int start,
				int end) {

		return _commerceInventoryWarehouseRelLocalService.
			getCommerceOrderTypeCommerceInventoryWarehouseRels(
				commerceInventoryWarehouseId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCommerceInventoryWarehouseRelsCount(
		long commerceInventoryWarehouseId, String keywords) {

		return _commerceInventoryWarehouseRelLocalService.
			getCommerceOrderTypeCommerceInventoryWarehouseRelsCount(
				commerceInventoryWarehouseId, keywords);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceInventoryWarehouseRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceInventoryWarehouseRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce inventory warehouse rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseRel the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel that was updated
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel
		updateCommerceInventoryWarehouseRel(
			com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel
				commerceInventoryWarehouseRel) {

		return _commerceInventoryWarehouseRelLocalService.
			updateCommerceInventoryWarehouseRel(commerceInventoryWarehouseRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceInventoryWarehouseRelLocalService.getBasePersistence();
	}

	@Override
	public CommerceInventoryWarehouseRelLocalService getWrappedService() {
		return _commerceInventoryWarehouseRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceInventoryWarehouseRelLocalService
			commerceInventoryWarehouseRelLocalService) {

		_commerceInventoryWarehouseRelLocalService =
			commerceInventoryWarehouseRelLocalService;
	}

	private CommerceInventoryWarehouseRelLocalService
		_commerceInventoryWarehouseRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:916231167