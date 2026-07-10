/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceOrderType;
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
 * Provides the local service utility for CommerceOrderType. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceOrderTypeLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderTypeLocalService
 * @generated
 */
public class CommerceOrderTypeLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceOrderTypeLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce order type to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderType the commerce order type
	 * @return the commerce order type that was added
	 */
	public static CommerceOrderType addCommerceOrderType(
		CommerceOrderType commerceOrderType) {

		return getService().addCommerceOrderType(commerceOrderType);
	}

	public static CommerceOrderType addCommerceOrderType(
			String externalReferenceCode, long userId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int displayOrder,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceOrderType(
			externalReferenceCode, userId, nameMap, descriptionMap, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, displayOrder, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	public static void checkCommerceOrderTypes() throws PortalException {
		getService().checkCommerceOrderTypes();
	}

	/**
	 * Creates a new commerce order type with the primary key. Does not add the commerce order type to the database.
	 *
	 * @param commerceOrderTypeId the primary key for the new commerce order type
	 * @return the new commerce order type
	 */
	public static CommerceOrderType createCommerceOrderType(
		long commerceOrderTypeId) {

		return getService().createCommerceOrderType(commerceOrderTypeId);
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
	 * Deletes the commerce order type from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderType the commerce order type
	 * @return the commerce order type that was removed
	 * @throws PortalException
	 */
	public static CommerceOrderType deleteCommerceOrderType(
			CommerceOrderType commerceOrderType)
		throws PortalException {

		return getService().deleteCommerceOrderType(commerceOrderType);
	}

	/**
	 * Deletes the commerce order type with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderTypeId the primary key of the commerce order type
	 * @return the commerce order type that was removed
	 * @throws PortalException if a commerce order type with the primary key could not be found
	 */
	public static CommerceOrderType deleteCommerceOrderType(
			long commerceOrderTypeId)
		throws PortalException {

		return getService().deleteCommerceOrderType(commerceOrderTypeId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderTypeModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderTypeModelImpl</code>.
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

	public static CommerceOrderType fetchCommerceOrderType(
		long commerceOrderTypeId) {

		return getService().fetchCommerceOrderType(commerceOrderTypeId);
	}

	public static CommerceOrderType
		fetchCommerceOrderTypeByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommerceOrderTypeByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order type with the matching UUID and company.
	 *
	 * @param uuid the commerce order type's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce order type, or <code>null</code> if a matching commerce order type could not be found
	 */
	public static CommerceOrderType fetchCommerceOrderTypeByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchCommerceOrderTypeByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce order type with the primary key.
	 *
	 * @param commerceOrderTypeId the primary key of the commerce order type
	 * @return the commerce order type
	 * @throws PortalException if a commerce order type with the primary key could not be found
	 */
	public static CommerceOrderType getCommerceOrderType(
			long commerceOrderTypeId)
		throws PortalException {

		return getService().getCommerceOrderType(commerceOrderTypeId);
	}

	public static CommerceOrderType getCommerceOrderTypeByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceOrderTypeByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order type with the matching UUID and company.
	 *
	 * @param uuid the commerce order type's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce order type
	 * @throws PortalException if a matching commerce order type could not be found
	 */
	public static CommerceOrderType getCommerceOrderTypeByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceOrderTypeByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce order types.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @return the range of commerce order types
	 */
	public static List<CommerceOrderType> getCommerceOrderTypes(
		int start, int end) {

		return getService().getCommerceOrderTypes(start, end);
	}

	public static List<CommerceOrderType> getCommerceOrderTypes(
			long companyId, String className, long classPK, boolean active,
			int start, int end)
		throws PortalException {

		return getService().getCommerceOrderTypes(
			companyId, className, classPK, active, start, end);
	}

	/**
	 * Returns the number of commerce order types.
	 *
	 * @return the number of commerce order types
	 */
	public static int getCommerceOrderTypesCount() {
		return getService().getCommerceOrderTypesCount();
	}

	public static int getCommerceOrderTypesCount(
		long companyId, boolean active) {

		return getService().getCommerceOrderTypesCount(companyId, active);
	}

	public static int getCommerceOrderTypesCount(
			long companyId, String className, long classPK, boolean active)
		throws PortalException {

		return getService().getCommerceOrderTypesCount(
			companyId, className, classPK, active);
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

	public static CommerceOrderType getOrAddEmptyCommerceOrderType(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		return getService().getOrAddEmptyCommerceOrderType(
			externalReferenceCode, companyId, userId);
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
	 * Updates the commerce order type in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderType the commerce order type
	 * @return the commerce order type that was updated
	 */
	public static CommerceOrderType updateCommerceOrderType(
		CommerceOrderType commerceOrderType) {

		return getService().updateCommerceOrderType(commerceOrderType);
	}

	public static CommerceOrderType updateCommerceOrderType(
			String externalReferenceCode, long userId, long commerceOrderTypeId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int displayOrder,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderType(
			externalReferenceCode, userId, commerceOrderTypeId, nameMap,
			descriptionMap, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute, displayOrder,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceOrderType
			updateCommerceOrderTypeExternalReferenceCode(
				String externalReferenceCode, long commerceOrderTypeId)
		throws PortalException {

		return getService().updateCommerceOrderTypeExternalReferenceCode(
			externalReferenceCode, commerceOrderTypeId);
	}

	public static CommerceOrderType updateStatus(
			long userId, long commerceOrderTypeId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, commerceOrderTypeId, status, serviceContext,
			workflowContext);
	}

	public static CommerceOrderTypeLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceOrderTypeLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceOrderTypeLocalServiceUtil.class,
			CommerceOrderTypeLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:2130032150