/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceDiscountOrderTypeRel. This utility wraps
 * <code>com.liferay.commerce.discount.service.impl.CommerceDiscountOrderTypeRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceDiscountOrderTypeRelLocalService
 * @generated
 */
public class CommerceDiscountOrderTypeRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountOrderTypeRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce discount order type rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountOrderTypeRel the commerce discount order type rel
	 * @return the commerce discount order type rel that was added
	 */
	public static CommerceDiscountOrderTypeRel addCommerceDiscountOrderTypeRel(
		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel) {

		return getService().addCommerceDiscountOrderTypeRel(
			commerceDiscountOrderTypeRel);
	}

	public static CommerceDiscountOrderTypeRel addCommerceDiscountOrderTypeRel(
			long userId, long commerceDiscountId, long commerceOrderTypeId,
			int priority,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscountOrderTypeRel(
			userId, commerceDiscountId, commerceOrderTypeId, priority,
			serviceContext);
	}

	/**
	 * Creates a new commerce discount order type rel with the primary key. Does not add the commerce discount order type rel to the database.
	 *
	 * @param commerceDiscountOrderTypeRelId the primary key for the new commerce discount order type rel
	 * @return the new commerce discount order type rel
	 */
	public static CommerceDiscountOrderTypeRel
		createCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId) {

		return getService().createCommerceDiscountOrderTypeRel(
			commerceDiscountOrderTypeRelId);
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
	 * Deletes the commerce discount order type rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountOrderTypeRel the commerce discount order type rel
	 * @return the commerce discount order type rel that was removed
	 * @throws PortalException
	 */
	public static CommerceDiscountOrderTypeRel
			deleteCommerceDiscountOrderTypeRel(
				CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel)
		throws PortalException {

		return getService().deleteCommerceDiscountOrderTypeRel(
			commerceDiscountOrderTypeRel);
	}

	/**
	 * Deletes the commerce discount order type rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountOrderTypeRelId the primary key of the commerce discount order type rel
	 * @return the commerce discount order type rel that was removed
	 * @throws PortalException if a commerce discount order type rel with the primary key could not be found
	 */
	public static CommerceDiscountOrderTypeRel
			deleteCommerceDiscountOrderTypeRel(
				long commerceDiscountOrderTypeRelId)
		throws PortalException {

		return getService().deleteCommerceDiscountOrderTypeRel(
			commerceDiscountOrderTypeRelId);
	}

	public static void deleteCommerceDiscountOrderTypeRels(
		long commerceDiscountId) {

		getService().deleteCommerceDiscountOrderTypeRels(commerceDiscountId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountOrderTypeRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountOrderTypeRelModelImpl</code>.
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

	public static CommerceDiscountOrderTypeRel
		fetchCommerceDiscountOrderTypeRel(long commerceDiscountOrderTypeRelId) {

		return getService().fetchCommerceDiscountOrderTypeRel(
			commerceDiscountOrderTypeRelId);
	}

	public static CommerceDiscountOrderTypeRel
		fetchCommerceDiscountOrderTypeRel(
			long commerceDiscountId, long commerceOrderTypeId) {

		return getService().fetchCommerceDiscountOrderTypeRel(
			commerceDiscountId, commerceOrderTypeId);
	}

	/**
	 * Returns the commerce discount order type rel with the matching UUID and company.
	 *
	 * @param uuid the commerce discount order type rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount order type rel, or <code>null</code> if a matching commerce discount order type rel could not be found
	 */
	public static CommerceDiscountOrderTypeRel
		fetchCommerceDiscountOrderTypeRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchCommerceDiscountOrderTypeRelByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce discount order type rel with the primary key.
	 *
	 * @param commerceDiscountOrderTypeRelId the primary key of the commerce discount order type rel
	 * @return the commerce discount order type rel
	 * @throws PortalException if a commerce discount order type rel with the primary key could not be found
	 */
	public static CommerceDiscountOrderTypeRel getCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId)
		throws PortalException {

		return getService().getCommerceDiscountOrderTypeRel(
			commerceDiscountOrderTypeRelId);
	}

	/**
	 * Returns the commerce discount order type rel with the matching UUID and company.
	 *
	 * @param uuid the commerce discount order type rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount order type rel
	 * @throws PortalException if a matching commerce discount order type rel could not be found
	 */
	public static CommerceDiscountOrderTypeRel
			getCommerceDiscountOrderTypeRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceDiscountOrderTypeRelByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce discount order type rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount order type rels
	 * @param end the upper bound of the range of commerce discount order type rels (not inclusive)
	 * @return the range of commerce discount order type rels
	 */
	public static List<CommerceDiscountOrderTypeRel>
		getCommerceDiscountOrderTypeRels(int start, int end) {

		return getService().getCommerceDiscountOrderTypeRels(start, end);
	}

	public static List<CommerceDiscountOrderTypeRel>
		getCommerceDiscountOrderTypeRels(long commerceDiscountId) {

		return getService().getCommerceDiscountOrderTypeRels(
			commerceDiscountId);
	}

	public static List<CommerceDiscountOrderTypeRel>
			getCommerceDiscountOrderTypeRels(
				long commerceDiscountId, String name, int start, int end,
				OrderByComparator<CommerceDiscountOrderTypeRel>
					orderByComparator)
		throws PortalException {

		return getService().getCommerceDiscountOrderTypeRels(
			commerceDiscountId, name, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce discount order type rels.
	 *
	 * @return the number of commerce discount order type rels
	 */
	public static int getCommerceDiscountOrderTypeRelsCount() {
		return getService().getCommerceDiscountOrderTypeRelsCount();
	}

	public static int getCommerceDiscountOrderTypeRelsCount(
			long commerceDiscountId, String name)
		throws PortalException {

		return getService().getCommerceDiscountOrderTypeRelsCount(
			commerceDiscountId, name);
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
	 * Updates the commerce discount order type rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountOrderTypeRel the commerce discount order type rel
	 * @return the commerce discount order type rel that was updated
	 */
	public static CommerceDiscountOrderTypeRel
		updateCommerceDiscountOrderTypeRel(
			CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel) {

		return getService().updateCommerceDiscountOrderTypeRel(
			commerceDiscountOrderTypeRel);
	}

	public static CommerceDiscountOrderTypeRel updatePriority(
			long commerceDiscountOrderTypeRelId, int priority)
		throws PortalException {

		return getService().updatePriority(
			commerceDiscountOrderTypeRelId, priority);
	}

	public static CommerceDiscountOrderTypeRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceDiscountOrderTypeRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceDiscountOrderTypeRelLocalServiceUtil.class,
			CommerceDiscountOrderTypeRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1871731049