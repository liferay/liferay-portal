/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceInventoryWarehouseLocalService}.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryWarehouseLocalService
 * @generated
 */
public class CommerceInventoryWarehouseLocalServiceWrapper
	implements CommerceInventoryWarehouseLocalService,
			   ServiceWrapper<CommerceInventoryWarehouseLocalService> {

	public CommerceInventoryWarehouseLocalServiceWrapper() {
		this(null);
	}

	public CommerceInventoryWarehouseLocalServiceWrapper(
		CommerceInventoryWarehouseLocalService
			commerceInventoryWarehouseLocalService) {

		_commerceInventoryWarehouseLocalService =
			commerceInventoryWarehouseLocalService;
	}

	/**
	 * Adds the commerce inventory warehouse to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouse the commerce inventory warehouse
	 * @return the commerce inventory warehouse that was added
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
		addCommerceInventoryWarehouse(
			com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
				commerceInventoryWarehouse) {

		return _commerceInventoryWarehouseLocalService.
			addCommerceInventoryWarehouse(commerceInventoryWarehouse);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
			addCommerceInventoryWarehouse(
				String externalReferenceCode,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				boolean active, String street1, String street2, String street3,
				String city, String zip, String commerceRegionCode,
				String commerceCountryCode, double latitude, double longitude,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.
			addCommerceInventoryWarehouse(
				externalReferenceCode, nameMap, descriptionMap, active, street1,
				street2, street3, city, zip, commerceRegionCode,
				commerceCountryCode, latitude, longitude, serviceContext);
	}

	/**
	 * Creates a new commerce inventory warehouse with the primary key. Does not add the commerce inventory warehouse to the database.
	 *
	 * @param commerceInventoryWarehouseId the primary key for the new commerce inventory warehouse
	 * @return the new commerce inventory warehouse
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
		createCommerceInventoryWarehouse(long commerceInventoryWarehouseId) {

		return _commerceInventoryWarehouseLocalService.
			createCommerceInventoryWarehouse(commerceInventoryWarehouseId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce inventory warehouse from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouse the commerce inventory warehouse
	 * @return the commerce inventory warehouse that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
			deleteCommerceInventoryWarehouse(
				com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
					commerceInventoryWarehouse)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.
			deleteCommerceInventoryWarehouse(commerceInventoryWarehouse);
	}

	/**
	 * Deletes the commerce inventory warehouse with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseId the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse that was removed
	 * @throws PortalException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
			deleteCommerceInventoryWarehouse(long commerceInventoryWarehouseId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.
			deleteCommerceInventoryWarehouse(commerceInventoryWarehouseId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceInventoryWarehouseLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceInventoryWarehouseLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceInventoryWarehouseLocalService.dynamicQuery();
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

		return _commerceInventoryWarehouseLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseModelImpl</code>.
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

		return _commerceInventoryWarehouseLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseModelImpl</code>.
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

		return _commerceInventoryWarehouseLocalService.dynamicQuery(
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

		return _commerceInventoryWarehouseLocalService.dynamicQueryCount(
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

		return _commerceInventoryWarehouseLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
		fetchCommerceInventoryWarehouse(long commerceInventoryWarehouseId) {

		return _commerceInventoryWarehouseLocalService.
			fetchCommerceInventoryWarehouse(commerceInventoryWarehouseId);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
		fetchCommerceInventoryWarehouseByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceInventoryWarehouseLocalService.
			fetchCommerceInventoryWarehouseByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce inventory warehouse with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory warehouse's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
		fetchCommerceInventoryWarehouseByUuidAndCompanyId(
			String uuid, long companyId) {

		return _commerceInventoryWarehouseLocalService.
			fetchCommerceInventoryWarehouseByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
			geolocateCommerceInventoryWarehouse(
				long commerceInventoryWarehouseId, double latitude,
				double longitude)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.
			geolocateCommerceInventoryWarehouse(
				commerceInventoryWarehouseId, latitude, longitude);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceInventoryWarehouseLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce inventory warehouse with the primary key.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse
	 * @throws PortalException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
			getCommerceInventoryWarehouse(long commerceInventoryWarehouseId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehouse(commerceInventoryWarehouseId);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
			getCommerceInventoryWarehouseByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehouseByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce inventory warehouse with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory warehouse's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory warehouse
	 * @throws PortalException if a matching commerce inventory warehouse could not be found
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
			getCommerceInventoryWarehouseByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehouseByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce inventory warehouses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of commerce inventory warehouses
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryWarehouse>
			getCommerceInventoryWarehouses(int start, int end) {

		return _commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehouses(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryWarehouse>
			getCommerceInventoryWarehouses(long companyId) {

		return _commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehouses(companyId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryWarehouse>
			getCommerceInventoryWarehouses(
				long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.inventory.model.
						CommerceInventoryWarehouse> orderByComparator) {

		return _commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehouses(
				companyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryWarehouse>
			getCommerceInventoryWarehouses(
				long companyId, long accountEntryId, long groupId,
				boolean active) {

		return _commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehouses(
				companyId, accountEntryId, groupId, active);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryWarehouse>
			getCommerceInventoryWarehouses(
				long accountEntryId, long groupId, String sku) {

		return _commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehouses(accountEntryId, groupId, sku);
	}

	/**
	 * Returns the number of commerce inventory warehouses.
	 *
	 * @return the number of commerce inventory warehouses
	 */
	@Override
	public int getCommerceInventoryWarehousesCount() {
		return _commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehousesCount();
	}

	@Override
	public int getCommerceInventoryWarehousesCount(
		long companyId, boolean active, String commerceCountryCode) {

		return _commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehousesCount(
				companyId, active, commerceCountryCode);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceInventoryWarehouseLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceInventoryWarehouseLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceInventoryWarehouseLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.inventory.model.CommerceInventoryWarehouse>
				search(
					long companyId, Boolean active, String commerceCountryCode,
					String keywords, int start, int end,
					com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.search(
			companyId, active, commerceCountryCode, keywords, start, end, sort);
	}

	@Override
	public int searchCommerceInventoryWarehousesCount(
			long companyId, Boolean active, String commerceCountryCode,
			String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.
			searchCommerceInventoryWarehousesCount(
				companyId, active, commerceCountryCode, keywords);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
			setActive(long commerceInventoryWarehouseId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.setActive(
			commerceInventoryWarehouseId, active);
	}

	/**
	 * Updates the commerce inventory warehouse in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouse the commerce inventory warehouse
	 * @return the commerce inventory warehouse that was updated
	 */
	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
		updateCommerceInventoryWarehouse(
			com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
				commerceInventoryWarehouse) {

		return _commerceInventoryWarehouseLocalService.
			updateCommerceInventoryWarehouse(commerceInventoryWarehouse);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
			updateCommerceInventoryWarehouse(
				long commerceInventoryWarehouseId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				boolean active, String street1, String street2, String street3,
				String city, String zip, String commerceRegionCode,
				String commerceCountryCode, double latitude, double longitude,
				long mvccVersion,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.
			updateCommerceInventoryWarehouse(
				commerceInventoryWarehouseId, nameMap, descriptionMap, active,
				street1, street2, street3, city, zip, commerceRegionCode,
				commerceCountryCode, latitude, longitude, mvccVersion,
				serviceContext);
	}

	@Override
	public com.liferay.commerce.inventory.model.CommerceInventoryWarehouse
			updateCommerceInventoryWarehouseExternalReferenceCode(
				String externalReferenceCode, long commerceInventoryWarehouseId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceInventoryWarehouseLocalService.
			updateCommerceInventoryWarehouseExternalReferenceCode(
				externalReferenceCode, commerceInventoryWarehouseId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceInventoryWarehouseLocalService.getBasePersistence();
	}

	@Override
	public CommerceInventoryWarehouseLocalService getWrappedService() {
		return _commerceInventoryWarehouseLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceInventoryWarehouseLocalService
			commerceInventoryWarehouseLocalService) {

		_commerceInventoryWarehouseLocalService =
			commerceInventoryWarehouseLocalService;
	}

	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1443348979