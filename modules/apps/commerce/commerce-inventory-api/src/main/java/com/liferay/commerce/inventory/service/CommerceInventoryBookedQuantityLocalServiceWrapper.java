/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceInventoryBookedQuantityLocalService}.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryBookedQuantityLocalService
 * @generated
 */
public class CommerceInventoryBookedQuantityLocalServiceWrapper
	implements CommerceInventoryBookedQuantityLocalService,
			   ServiceWrapper<CommerceInventoryBookedQuantityLocalService> {

	public CommerceInventoryBookedQuantityLocalServiceWrapper() {
		this(null);
	}

	public CommerceInventoryBookedQuantityLocalServiceWrapper(
		CommerceInventoryBookedQuantityLocalService
			commerceInventoryBookedQuantityLocalService) {

		_commerceInventoryBookedQuantityLocalService =
			commerceInventoryBookedQuantityLocalService;
	}

	/**
	 * Adds the commerce inventory booked quantity to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantity the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was added
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
		addCommerceInventoryBookedQuantity(
			com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
				commerceInventoryBookedQuantity) {

		return _commerceInventoryBookedQuantityLocalService.
			addCommerceInventoryBookedQuantity(commerceInventoryBookedQuantity);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
			addCommerceInventoryBookedQuantity(
				long userId, java.util.Date expirationDate,
				java.math.BigDecimal quantity, String sku,
				String unitOfMeasureKey, java.util.Map<String, String> context)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			addCommerceInventoryBookedQuantity(
				userId, expirationDate, quantity, sku, unitOfMeasureKey,
				context);
	}

	@Override
	public void checkCommerceInventoryBookedQuantities() {
		_commerceInventoryBookedQuantityLocalService.
			checkCommerceInventoryBookedQuantities();
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
			consumeCommerceInventoryBookedQuantity(
				long commerceInventoryBookedQuantityId,
				java.math.BigDecimal quantity)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryBookedQuantityException {

		return _commerceInventoryBookedQuantityLocalService.
			consumeCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantityId, quantity);
	}

	/**
	 * Creates a new commerce inventory booked quantity with the primary key. Does not add the commerce inventory booked quantity to the database.
	 *
	 * @param commerceInventoryBookedQuantityId the primary key for the new commerce inventory booked quantity
	 * @return the new commerce inventory booked quantity
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
		createCommerceInventoryBookedQuantity(
			long commerceInventoryBookedQuantityId) {

		return _commerceInventoryBookedQuantityLocalService.
			createCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantityId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the commerce inventory booked quantity from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantity the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was removed
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
		deleteCommerceInventoryBookedQuantity(
			com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
				commerceInventoryBookedQuantity) {

		return _commerceInventoryBookedQuantityLocalService.
			deleteCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantity);
	}

	/**
	 * Deletes the commerce inventory booked quantity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantityId the primary key of the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was removed
	 * @throws PortalException if a commerce inventory booked quantity with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
			deleteCommerceInventoryBookedQuantity(
				long commerceInventoryBookedQuantityId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			deleteCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantityId);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
			deleteCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				java.util.Map<String, String> context,
				com.liferay.commerce.inventory.type.CommerceInventoryAuditType
					commerceInventoryAuditType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			deleteCommerceInventoryBookedQuantity(
				userId, commerceInventoryBookedQuantityId, context,
				commerceInventoryAuditType);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceInventoryBookedQuantityLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceInventoryBookedQuantityLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceInventoryBookedQuantityLocalService.dynamicQuery();
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

		return _commerceInventoryBookedQuantityLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityModelImpl</code>.
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

		return _commerceInventoryBookedQuantityLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityModelImpl</code>.
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

		return _commerceInventoryBookedQuantityLocalService.dynamicQuery(
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

		return _commerceInventoryBookedQuantityLocalService.dynamicQueryCount(
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

		return _commerceInventoryBookedQuantityLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
		fetchCommerceInventoryBookedQuantity(
			long commerceInventoryBookedQuantityId) {

		return _commerceInventoryBookedQuantityLocalService.
			fetchCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantityId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceInventoryBookedQuantityLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce inventory booked quantities.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory booked quantities
	 * @param end the upper bound of the range of commerce inventory booked quantities (not inclusive)
	 * @return the range of commerce inventory booked quantities
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity>
			getCommerceInventoryBookedQuantities(int start, int end) {

		return _commerceInventoryBookedQuantityLocalService.
			getCommerceInventoryBookedQuantities(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity>
			getCommerceInventoryBookedQuantities(
				long companyId, String sku, String unitOfMeasureKey, int start,
				int end) {

		return _commerceInventoryBookedQuantityLocalService.
			getCommerceInventoryBookedQuantities(
				companyId, sku, unitOfMeasureKey, start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity>
				getCommerceInventoryBookedQuantities(
					long companyId, String keywords, String sku,
					String unitOfMeasureKey, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			getCommerceInventoryBookedQuantities(
				companyId, keywords, sku, unitOfMeasureKey, start, end);
	}

	/**
	 * Returns the number of commerce inventory booked quantities.
	 *
	 * @return the number of commerce inventory booked quantities
	 */
	@Override
	public int getCommerceInventoryBookedQuantitiesCount() {
		return _commerceInventoryBookedQuantityLocalService.
			getCommerceInventoryBookedQuantitiesCount();
	}

	@Override
	public int getCommerceInventoryBookedQuantitiesCount(
			long companyId, String keywords, String sku,
			String unitOfMeasureKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			getCommerceInventoryBookedQuantitiesCount(
				companyId, keywords, sku, unitOfMeasureKey);
	}

	/**
	 * Returns the commerce inventory booked quantity with the primary key.
	 *
	 * @param commerceInventoryBookedQuantityId the primary key of the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity
	 * @throws PortalException if a commerce inventory booked quantity with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
			getCommerceInventoryBookedQuantity(
				long commerceInventoryBookedQuantityId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			getCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantityId);
	}

	@Override
	public java.math.BigDecimal getCommerceInventoryBookedQuantity(
		long companyId, long commerceChannelGroupId, String sku,
		String unitOfMeasureKey) {

		return _commerceInventoryBookedQuantityLocalService.
			getCommerceInventoryBookedQuantity(
				companyId, commerceChannelGroupId, sku, unitOfMeasureKey);
	}

	@Override
	public java.math.BigDecimal getCommerceInventoryBookedQuantity(
		long companyId, String sku, String unitOfMeasureKey) {

		return _commerceInventoryBookedQuantityLocalService.
			getCommerceInventoryBookedQuantity(
				companyId, sku, unitOfMeasureKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceInventoryBookedQuantityLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceInventoryBookedQuantityLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
			resetCommerceInventoryBookedQuantity(
				long commerceInventoryBookedQuantityId, long userId,
				java.util.Date expirationDate, java.math.BigDecimal quantity,
				String sku, String unitOfMeasureKey,
				java.util.Map<String, String> context)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			resetCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantityId, userId, expirationDate,
				quantity, sku, unitOfMeasureKey, context);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
			restockCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				java.util.Map<String, String> context)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			restockCommerceInventoryBookedQuantity(
				userId, commerceInventoryBookedQuantityId, context);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity>
				searchCommerceInventoryBookedQuantities(
					com.liferay.portal.kernel.search.SearchContext
						searchContext)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			searchCommerceInventoryBookedQuantities(searchContext);
	}

	@Override
	public int searchCommerceInventoryBookedQuantitiesCount(
			com.liferay.portal.kernel.search.SearchContext searchContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			searchCommerceInventoryBookedQuantitiesCount(searchContext);
	}

	/**
	 * Updates the commerce inventory booked quantity in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantity the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was updated
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
		updateCommerceInventoryBookedQuantity(
			com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
				commerceInventoryBookedQuantity) {

		return _commerceInventoryBookedQuantityLocalService.
			updateCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantity);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity
			updateCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				java.math.BigDecimal quantity,
				java.util.Map<String, String> context, long mvccVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryBookedQuantityLocalService.
			updateCommerceInventoryBookedQuantity(
				userId, commerceInventoryBookedQuantityId, quantity, context,
				mvccVersion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceInventoryBookedQuantityLocalService.
			getBasePersistence();
	}

	@Override
	public CommerceInventoryBookedQuantityLocalService getWrappedService() {
		return _commerceInventoryBookedQuantityLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceInventoryBookedQuantityLocalService
			commerceInventoryBookedQuantityLocalService) {

		_commerceInventoryBookedQuantityLocalService =
			commerceInventoryBookedQuantityLocalService;
	}

	private CommerceInventoryBookedQuantityLocalService
		_commerceInventoryBookedQuantityLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1846744277