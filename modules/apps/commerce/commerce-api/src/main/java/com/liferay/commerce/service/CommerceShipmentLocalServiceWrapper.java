/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceShipmentLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShipmentLocalService
 * @generated
 */
public class CommerceShipmentLocalServiceWrapper
	implements CommerceShipmentLocalService,
			   ServiceWrapper<CommerceShipmentLocalService> {

	public CommerceShipmentLocalServiceWrapper() {
		this(null);
	}

	public CommerceShipmentLocalServiceWrapper(
		CommerceShipmentLocalService commerceShipmentLocalService) {

		_commerceShipmentLocalService = commerceShipmentLocalService;
	}

	/**
	 * Adds the commerce shipment to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShipmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShipment the commerce shipment
	 * @return the commerce shipment that was added
	 */
	@Override
	public com.liferay.commerce.model.CommerceShipment addCommerceShipment(
		com.liferay.commerce.model.CommerceShipment commerceShipment) {

		return _commerceShipmentLocalService.addCommerceShipment(
			commerceShipment);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment addCommerceShipment(
			long commerceOrderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.addCommerceShipment(
			commerceOrderId, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment addCommerceShipment(
			String externalReferenceCode, long groupId, long commerceAccountId,
			long commerceAddressId, long commerceShippingMethodId,
			String commerceShippingOptionName,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.addCommerceShipment(
			externalReferenceCode, groupId, commerceAccountId,
			commerceAddressId, commerceShippingMethodId,
			commerceShippingOptionName, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment
			addDeliverySubscriptionCommerceShipment(
				long userId, long commerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.
			addDeliverySubscriptionCommerceShipment(
				userId, commerceOrderItemId);
	}

	/**
	 * Creates a new commerce shipment with the primary key. Does not add the commerce shipment to the database.
	 *
	 * @param commerceShipmentId the primary key for the new commerce shipment
	 * @return the new commerce shipment
	 */
	@Override
	public com.liferay.commerce.model.CommerceShipment createCommerceShipment(
		long commerceShipmentId) {

		return _commerceShipmentLocalService.createCommerceShipment(
			commerceShipmentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce shipment from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShipmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShipment the commerce shipment
	 * @return the commerce shipment that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.model.CommerceShipment deleteCommerceShipment(
			com.liferay.commerce.model.CommerceShipment commerceShipment)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.deleteCommerceShipment(
			commerceShipment);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment deleteCommerceShipment(
			com.liferay.commerce.model.CommerceShipment commerceShipment,
			boolean restoreStockQuantity)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.deleteCommerceShipment(
			commerceShipment, restoreStockQuantity);
	}

	/**
	 * Deletes the commerce shipment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShipmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShipmentId the primary key of the commerce shipment
	 * @return the commerce shipment that was removed
	 * @throws PortalException if a commerce shipment with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceShipment deleteCommerceShipment(
			long commerceShipmentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.deleteCommerceShipment(
			commerceShipmentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceShipmentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceShipmentLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceShipmentLocalService.dynamicQuery();
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

		return _commerceShipmentLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShipmentModelImpl</code>.
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

		return _commerceShipmentLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShipmentModelImpl</code>.
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

		return _commerceShipmentLocalService.dynamicQuery(
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

		return _commerceShipmentLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commerceShipmentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment fetchCommerceShipment(
		long commerceShipmentId) {

		return _commerceShipmentLocalService.fetchCommerceShipment(
			commerceShipmentId);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment
		fetchCommerceShipmentByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceShipmentLocalService.
			fetchCommerceShipmentByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce shipment matching the UUID and group.
	 *
	 * @param uuid the commerce shipment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce shipment, or <code>null</code> if a matching commerce shipment could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceShipment
		fetchCommerceShipmentByUuidAndGroupId(String uuid, long groupId) {

		return _commerceShipmentLocalService.
			fetchCommerceShipmentByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceShipmentLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce shipment with the primary key.
	 *
	 * @param commerceShipmentId the primary key of the commerce shipment
	 * @return the commerce shipment
	 * @throws PortalException if a commerce shipment with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceShipment getCommerceShipment(
			long commerceShipmentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.getCommerceShipment(
			commerceShipmentId);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment
			getCommerceShipmentByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.
			getCommerceShipmentByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce shipment matching the UUID and group.
	 *
	 * @param uuid the commerce shipment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce shipment
	 * @throws PortalException if a matching commerce shipment could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceShipment
			getCommerceShipmentByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.
			getCommerceShipmentByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the commerce shipments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShipmentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @return the range of commerce shipments
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
		getCommerceShipments(int start, int end) {

		return _commerceShipmentLocalService.getCommerceShipments(start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
		getCommerceShipments(long commerceOrderId, int start, int end) {

		return _commerceShipmentLocalService.getCommerceShipments(
			commerceOrderId, start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
		getCommerceShipments(
			long groupId, long commerceAccountId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceShipment>
					orderByComparator) {

		return _commerceShipmentLocalService.getCommerceShipments(
			groupId, commerceAccountId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
			getCommerceShipments(
				long companyId, long[] groupIds, long[] commerceAccountIds,
				String keywords, int[] shipmentStatuses,
				boolean excludeShipmentStatus, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.getCommerceShipments(
			companyId, groupIds, commerceAccountIds, keywords, shipmentStatuses,
			excludeShipmentStatus, start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
		getCommerceShipments(
			long[] groupIds, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceShipment>
					orderByComparator) {

		return _commerceShipmentLocalService.getCommerceShipments(
			groupIds, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
		getCommerceShipments(
			long[] groupIds, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceShipment>
					orderByComparator) {

		return _commerceShipmentLocalService.getCommerceShipments(
			groupIds, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
		getCommerceShipments(
			long[] groupIds, long commerceAddressId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceShipment>
					orderByComparator) {

		return _commerceShipmentLocalService.getCommerceShipments(
			groupIds, commerceAddressId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce shipments matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce shipments
	 * @param companyId the primary key of the company
	 * @return the matching commerce shipments, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
		getCommerceShipmentsByUuidAndCompanyId(String uuid, long companyId) {

		return _commerceShipmentLocalService.
			getCommerceShipmentsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of commerce shipments matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce shipments
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce shipments
	 * @param end the upper bound of the range of commerce shipments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce shipments, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
		getCommerceShipmentsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceShipment>
					orderByComparator) {

		return _commerceShipmentLocalService.
			getCommerceShipmentsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce shipments.
	 *
	 * @return the number of commerce shipments
	 */
	@Override
	public int getCommerceShipmentsCount() {
		return _commerceShipmentLocalService.getCommerceShipmentsCount();
	}

	@Override
	public int getCommerceShipmentsCount(long commerceOrderId) {
		return _commerceShipmentLocalService.getCommerceShipmentsCount(
			commerceOrderId);
	}

	@Override
	public int getCommerceShipmentsCount(long groupId, long commerceAccountId) {
		return _commerceShipmentLocalService.getCommerceShipmentsCount(
			groupId, commerceAccountId);
	}

	@Override
	public int getCommerceShipmentsCount(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.getCommerceShipmentsCount(
			companyId, groupIds, commerceAccountIds, keywords, shipmentStatuses,
			excludeShipmentStatus);
	}

	@Override
	public int getCommerceShipmentsCount(long[] groupIds) {
		return _commerceShipmentLocalService.getCommerceShipmentsCount(
			groupIds);
	}

	@Override
	public int getCommerceShipmentsCount(long[] groupIds, int status) {
		return _commerceShipmentLocalService.getCommerceShipmentsCount(
			groupIds, status);
	}

	@Override
	public int getCommerceShipmentsCount(
		long[] groupIds, long commerceAddressId) {

		return _commerceShipmentLocalService.getCommerceShipmentsCount(
			groupIds, commerceAddressId);
	}

	@Override
	public int[] getCommerceShipmentStatusesByCommerceOrderId(
		long commerceOrderId) {

		return _commerceShipmentLocalService.
			getCommerceShipmentStatusesByCommerceOrderId(commerceOrderId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceShipmentLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceShipmentLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceShipmentLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment
			reprocessCommerceShipment(long commerceShipmentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.reprocessCommerceShipment(
			commerceShipmentId);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.model.CommerceShipment> searchCommerceShipments(
				com.liferay.portal.kernel.search.SearchContext searchContext)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.searchCommerceShipments(
			searchContext);
	}

	@Override
	public long searchCommerceShipmentsCount(
			com.liferay.portal.kernel.search.SearchContext searchContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.searchCommerceShipmentsCount(
			searchContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateAddress(
			String externalReferenceCode, long commerceShipmentId, String name,
			String description, String street1, String street2, String street3,
			String city, String zip, long regionId, long countryId,
			String phoneNumber,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.updateAddress(
			externalReferenceCode, commerceShipmentId, name, description,
			street1, street2, street3, city, zip, regionId, countryId,
			phoneNumber, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateCarrierDetails(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, String trackingNumber, String trackingURL)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.updateCarrierDetails(
			commerceShipmentId, commerceShippingMethodId, carrier,
			trackingNumber, trackingURL);
	}

	/**
	 * Updates the commerce shipment in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShipmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShipment the commerce shipment
	 * @return the commerce shipment that was updated
	 */
	@Override
	public com.liferay.commerce.model.CommerceShipment updateCommerceShipment(
		com.liferay.commerce.model.CommerceShipment commerceShipment) {

		return _commerceShipmentLocalService.updateCommerceShipment(
			commerceShipment);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.updateCommerceShipment(
			commerceShipmentId, commerceShippingMethodId, carrier,
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, shippingDateMonth,
			shippingDateDay, shippingDateYear, shippingDateHour,
			shippingDateMinute, trackingNumber, trackingURL, status,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status, String name, String description,
			String street1, String street2, String street3, String city,
			String zip, long regionId, long countryId, String phoneNumber,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.updateCommerceShipment(
			commerceShipmentId, commerceShippingMethodId, carrier,
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, shippingDateMonth,
			shippingDateDay, shippingDateYear, shippingDateHour,
			shippingDateMinute, trackingNumber, trackingURL, status, name,
			description, street1, street2, street3, city, zip, regionId,
			countryId, phoneNumber, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateExpectedDate(
			long commerceShipmentId, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.updateExpectedDate(
			commerceShipmentId, expectedDateMonth, expectedDateDay,
			expectedDateYear, expectedDateHour, expectedDateMinute);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment
			updateExternalReferenceCode(
				long commerceShipmentId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.updateExternalReferenceCode(
			commerceShipmentId, externalReferenceCode);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateShippingDate(
			long commerceShipmentId, int shippingDateMonth, int shippingDateDay,
			int shippingDateYear, int shippingDateHour, int shippingDateMinute)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.updateShippingDate(
			commerceShipmentId, shippingDateMonth, shippingDateDay,
			shippingDateYear, shippingDateHour, shippingDateMinute);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateStatus(
			long commerceShipmentId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentLocalService.updateStatus(
			commerceShipmentId, status);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceShipmentLocalService.getBasePersistence();
	}

	@Override
	public CommerceShipmentLocalService getWrappedService() {
		return _commerceShipmentLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceShipmentLocalService commerceShipmentLocalService) {

		_commerceShipmentLocalService = commerceShipmentLocalService;
	}

	private CommerceShipmentLocalService _commerceShipmentLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-109738487