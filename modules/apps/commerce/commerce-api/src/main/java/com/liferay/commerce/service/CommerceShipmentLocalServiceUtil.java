/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceShipment;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceShipment. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceShipmentLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShipmentLocalService
 * @generated
 */
public class CommerceShipmentLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceShipmentLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CommerceShipment addCommerceShipment(
		CommerceShipment commerceShipment) {

		return getService().addCommerceShipment(commerceShipment);
	}

	public static CommerceShipment addCommerceShipment(
			long commerceOrderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceShipment(
			commerceOrderId, serviceContext);
	}

	public static CommerceShipment addCommerceShipment(
			String externalReferenceCode, long groupId, long commerceAccountId,
			long commerceAddressId, long commerceShippingMethodId,
			String commerceShippingOptionName,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceShipment(
			externalReferenceCode, groupId, commerceAccountId,
			commerceAddressId, commerceShippingMethodId,
			commerceShippingOptionName, serviceContext);
	}

	public static CommerceShipment addDeliverySubscriptionCommerceShipment(
			long userId, long commerceOrderItemId)
		throws PortalException {

		return getService().addDeliverySubscriptionCommerceShipment(
			userId, commerceOrderItemId);
	}

	/**
	 * Creates a new commerce shipment with the primary key. Does not add the commerce shipment to the database.
	 *
	 * @param commerceShipmentId the primary key for the new commerce shipment
	 * @return the new commerce shipment
	 */
	public static CommerceShipment createCommerceShipment(
		long commerceShipmentId) {

		return getService().createCommerceShipment(commerceShipmentId);
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
	public static CommerceShipment deleteCommerceShipment(
			CommerceShipment commerceShipment)
		throws PortalException {

		return getService().deleteCommerceShipment(commerceShipment);
	}

	public static CommerceShipment deleteCommerceShipment(
			CommerceShipment commerceShipment, boolean restoreStockQuantity)
		throws PortalException {

		return getService().deleteCommerceShipment(
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
	public static CommerceShipment deleteCommerceShipment(
			long commerceShipmentId)
		throws PortalException {

		return getService().deleteCommerceShipment(commerceShipmentId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShipmentModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShipmentModelImpl</code>.
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

	public static CommerceShipment fetchCommerceShipment(
		long commerceShipmentId) {

		return getService().fetchCommerceShipment(commerceShipmentId);
	}

	public static CommerceShipment fetchCommerceShipmentByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCommerceShipmentByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce shipment matching the UUID and group.
	 *
	 * @param uuid the commerce shipment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce shipment, or <code>null</code> if a matching commerce shipment could not be found
	 */
	public static CommerceShipment fetchCommerceShipmentByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchCommerceShipmentByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce shipment with the primary key.
	 *
	 * @param commerceShipmentId the primary key of the commerce shipment
	 * @return the commerce shipment
	 * @throws PortalException if a commerce shipment with the primary key could not be found
	 */
	public static CommerceShipment getCommerceShipment(long commerceShipmentId)
		throws PortalException {

		return getService().getCommerceShipment(commerceShipmentId);
	}

	public static CommerceShipment getCommerceShipmentByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceShipmentByExternalReferenceCode(
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
	public static CommerceShipment getCommerceShipmentByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCommerceShipmentByUuidAndGroupId(uuid, groupId);
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
	public static List<CommerceShipment> getCommerceShipments(
		int start, int end) {

		return getService().getCommerceShipments(start, end);
	}

	public static List<CommerceShipment> getCommerceShipments(
		long commerceOrderId, int start, int end) {

		return getService().getCommerceShipments(commerceOrderId, start, end);
	}

	public static List<CommerceShipment> getCommerceShipments(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus, int start, int end)
		throws PortalException {

		return getService().getCommerceShipments(
			companyId, groupIds, commerceAccountIds, keywords, shipmentStatuses,
			excludeShipmentStatus, start, end);
	}

	public static List<CommerceShipment> getCommerceShipments(
		long[] groupIds, int status, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return getService().getCommerceShipments(
			groupIds, status, start, end, orderByComparator);
	}

	public static List<CommerceShipment> getCommerceShipments(
		long[] groupIds, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return getService().getCommerceShipments(
			groupIds, start, end, orderByComparator);
	}

	public static List<CommerceShipment> getCommerceShipments(
		long[] groupIds, long commerceAddressId, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return getService().getCommerceShipments(
			groupIds, commerceAddressId, start, end, orderByComparator);
	}

	public static List<CommerceShipment>
		getCommerceShipmentsByCommerceAccountId(
			long groupId, long commerceAccountId, int start, int end,
			OrderByComparator<CommerceShipment> orderByComparator) {

		return getService().getCommerceShipmentsByCommerceAccountId(
			groupId, commerceAccountId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce shipments matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce shipments
	 * @param companyId the primary key of the company
	 * @return the matching commerce shipments, or an empty list if no matches were found
	 */
	public static List<CommerceShipment> getCommerceShipmentsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getCommerceShipmentsByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<CommerceShipment> getCommerceShipmentsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceShipment> orderByComparator) {

		return getService().getCommerceShipmentsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce shipments.
	 *
	 * @return the number of commerce shipments
	 */
	public static int getCommerceShipmentsCount() {
		return getService().getCommerceShipmentsCount();
	}

	public static int getCommerceShipmentsCount(long commerceOrderId) {
		return getService().getCommerceShipmentsCount(commerceOrderId);
	}

	public static int getCommerceShipmentsCount(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus)
		throws PortalException {

		return getService().getCommerceShipmentsCount(
			companyId, groupIds, commerceAccountIds, keywords, shipmentStatuses,
			excludeShipmentStatus);
	}

	public static int getCommerceShipmentsCount(long[] groupIds) {
		return getService().getCommerceShipmentsCount(groupIds);
	}

	public static int getCommerceShipmentsCount(long[] groupIds, int status) {
		return getService().getCommerceShipmentsCount(groupIds, status);
	}

	public static int getCommerceShipmentsCount(
		long[] groupIds, long commerceAddressId) {

		return getService().getCommerceShipmentsCount(
			groupIds, commerceAddressId);
	}

	public static int getCommerceShipmentsCountByCommerceAccountId(
		long groupId, long commerceAccountId) {

		return getService().getCommerceShipmentsCountByCommerceAccountId(
			groupId, commerceAccountId);
	}

	public static int[] getCommerceShipmentStatusesByCommerceOrderId(
		long commerceOrderId) {

		return getService().getCommerceShipmentStatusesByCommerceOrderId(
			commerceOrderId);
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

	public static CommerceShipment reprocessCommerceShipment(
			long commerceShipmentId)
		throws PortalException {

		return getService().reprocessCommerceShipment(commerceShipmentId);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceShipment> searchCommerceShipments(
				com.liferay.portal.kernel.search.SearchContext searchContext)
			throws PortalException {

		return getService().searchCommerceShipments(searchContext);
	}

	public static long searchCommerceShipmentsCount(
			com.liferay.portal.kernel.search.SearchContext searchContext)
		throws PortalException {

		return getService().searchCommerceShipmentsCount(searchContext);
	}

	public static CommerceShipment updateAddress(
			String externalReferenceCode, long commerceShipmentId, String name,
			String description, String street1, String street2, String street3,
			String city, String zip, long regionId, long countryId,
			String phoneNumber,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateAddress(
			externalReferenceCode, commerceShipmentId, name, description,
			street1, street2, street3, city, zip, regionId, countryId,
			phoneNumber, serviceContext);
	}

	public static CommerceShipment updateCarrierDetails(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, String trackingNumber, String trackingURL)
		throws PortalException {

		return getService().updateCarrierDetails(
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
	public static CommerceShipment updateCommerceShipment(
		CommerceShipment commerceShipment) {

		return getService().updateCommerceShipment(commerceShipment);
	}

	public static CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceShipment(
			commerceShipmentId, commerceShippingMethodId, carrier,
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, shippingDateMonth,
			shippingDateDay, shippingDateYear, shippingDateHour,
			shippingDateMinute, trackingNumber, trackingURL, status,
			serviceContext);
	}

	public static CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status, String name, String description,
			String street1, String street2, String street3, String city,
			String zip, long regionId, long countryId, String phoneNumber,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceShipment(
			commerceShipmentId, commerceShippingMethodId, carrier,
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, shippingDateMonth,
			shippingDateDay, shippingDateYear, shippingDateHour,
			shippingDateMinute, trackingNumber, trackingURL, status, name,
			description, street1, street2, street3, city, zip, regionId,
			countryId, phoneNumber, serviceContext);
	}

	public static CommerceShipment updateExpectedDate(
			long commerceShipmentId, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute)
		throws PortalException {

		return getService().updateExpectedDate(
			commerceShipmentId, expectedDateMonth, expectedDateDay,
			expectedDateYear, expectedDateHour, expectedDateMinute);
	}

	public static CommerceShipment updateExternalReferenceCode(
			long commerceShipmentId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			commerceShipmentId, externalReferenceCode);
	}

	public static CommerceShipment updateShippingDate(
			long commerceShipmentId, int shippingDateMonth, int shippingDateDay,
			int shippingDateYear, int shippingDateHour, int shippingDateMinute)
		throws PortalException {

		return getService().updateShippingDate(
			commerceShipmentId, shippingDateMonth, shippingDateDay,
			shippingDateYear, shippingDateHour, shippingDateMinute);
	}

	public static CommerceShipment updateStatus(
			long commerceShipmentId, int status)
		throws PortalException {

		return getService().updateStatus(commerceShipmentId, status);
	}

	public static CommerceShipmentLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceShipmentLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceShipmentLocalServiceUtil.class,
			CommerceShipmentLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:634455956