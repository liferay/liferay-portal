/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscountAccountRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceDiscountAccountRel. This utility wraps
 * <code>com.liferay.commerce.discount.service.impl.CommerceDiscountAccountRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceDiscountAccountRelLocalService
 * @generated
 */
public class CommerceDiscountAccountRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountAccountRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce discount account rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountAccountRel the commerce discount account rel
	 * @return the commerce discount account rel that was added
	 */
	public static CommerceDiscountAccountRel addCommerceDiscountAccountRel(
		CommerceDiscountAccountRel commerceDiscountAccountRel) {

		return getService().addCommerceDiscountAccountRel(
			commerceDiscountAccountRel);
	}

	public static CommerceDiscountAccountRel addCommerceDiscountAccountRel(
			long userId, long commerceDiscountId, long commerceAccountId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscountAccountRel(
			userId, commerceDiscountId, commerceAccountId, serviceContext);
	}

	/**
	 * Creates a new commerce discount account rel with the primary key. Does not add the commerce discount account rel to the database.
	 *
	 * @param commerceDiscountAccountRelId the primary key for the new commerce discount account rel
	 * @return the new commerce discount account rel
	 */
	public static CommerceDiscountAccountRel createCommerceDiscountAccountRel(
		long commerceDiscountAccountRelId) {

		return getService().createCommerceDiscountAccountRel(
			commerceDiscountAccountRelId);
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
	 * Deletes the commerce discount account rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountAccountRel the commerce discount account rel
	 * @return the commerce discount account rel that was removed
	 * @throws PortalException
	 */
	public static CommerceDiscountAccountRel deleteCommerceDiscountAccountRel(
			CommerceDiscountAccountRel commerceDiscountAccountRel)
		throws PortalException {

		return getService().deleteCommerceDiscountAccountRel(
			commerceDiscountAccountRel);
	}

	/**
	 * Deletes the commerce discount account rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountAccountRelId the primary key of the commerce discount account rel
	 * @return the commerce discount account rel that was removed
	 * @throws PortalException if a commerce discount account rel with the primary key could not be found
	 */
	public static CommerceDiscountAccountRel deleteCommerceDiscountAccountRel(
			long commerceDiscountAccountRelId)
		throws PortalException {

		return getService().deleteCommerceDiscountAccountRel(
			commerceDiscountAccountRelId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static void deleteCommerceDiscountAccountRelsBycommerceAccountId(
		long commerceAccountId) {

		getService().deleteCommerceDiscountAccountRelsBycommerceAccountId(
			commerceAccountId);
	}

	public static void deleteCommerceDiscountAccountRelsByCommerceDiscountId(
			long commerceDiscountId)
		throws PortalException {

		getService().deleteCommerceDiscountAccountRelsByCommerceDiscountId(
			commerceDiscountId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountAccountRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountAccountRelModelImpl</code>.
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

	public static CommerceDiscountAccountRel fetchCommerceDiscountAccountRel(
		long commerceDiscountAccountRelId) {

		return getService().fetchCommerceDiscountAccountRel(
			commerceDiscountAccountRelId);
	}

	public static CommerceDiscountAccountRel fetchCommerceDiscountAccountRel(
		long commerceAccountId, long commerceDiscountId) {

		return getService().fetchCommerceDiscountAccountRel(
			commerceAccountId, commerceDiscountId);
	}

	/**
	 * Returns the commerce discount account rel with the matching UUID and company.
	 *
	 * @param uuid the commerce discount account rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount account rel, or <code>null</code> if a matching commerce discount account rel could not be found
	 */
	public static CommerceDiscountAccountRel
		fetchCommerceDiscountAccountRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchCommerceDiscountAccountRelByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce discount account rel with the primary key.
	 *
	 * @param commerceDiscountAccountRelId the primary key of the commerce discount account rel
	 * @return the commerce discount account rel
	 * @throws PortalException if a commerce discount account rel with the primary key could not be found
	 */
	public static CommerceDiscountAccountRel getCommerceDiscountAccountRel(
			long commerceDiscountAccountRelId)
		throws PortalException {

		return getService().getCommerceDiscountAccountRel(
			commerceDiscountAccountRelId);
	}

	/**
	 * Returns the commerce discount account rel with the matching UUID and company.
	 *
	 * @param uuid the commerce discount account rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount account rel
	 * @throws PortalException if a matching commerce discount account rel could not be found
	 */
	public static CommerceDiscountAccountRel
			getCommerceDiscountAccountRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceDiscountAccountRelByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce discount account rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount account rels
	 * @param end the upper bound of the range of commerce discount account rels (not inclusive)
	 * @return the range of commerce discount account rels
	 */
	public static List<CommerceDiscountAccountRel>
		getCommerceDiscountAccountRels(int start, int end) {

		return getService().getCommerceDiscountAccountRels(start, end);
	}

	public static List<CommerceDiscountAccountRel>
		getCommerceDiscountAccountRels(
			long commerceDiscountId, int start, int end,
			OrderByComparator<CommerceDiscountAccountRel> orderByComparator) {

		return getService().getCommerceDiscountAccountRels(
			commerceDiscountId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce discount account rels.
	 *
	 * @return the number of commerce discount account rels
	 */
	public static int getCommerceDiscountAccountRelsCount() {
		return getService().getCommerceDiscountAccountRelsCount();
	}

	public static int getCommerceDiscountAccountRelsCount(
		long commerceDiscountId) {

		return getService().getCommerceDiscountAccountRelsCount(
			commerceDiscountId);
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
	 * Updates the commerce discount account rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountAccountRel the commerce discount account rel
	 * @return the commerce discount account rel that was updated
	 */
	public static CommerceDiscountAccountRel updateCommerceDiscountAccountRel(
		CommerceDiscountAccountRel commerceDiscountAccountRel) {

		return getService().updateCommerceDiscountAccountRel(
			commerceDiscountAccountRel);
	}

	public static CommerceDiscountAccountRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceDiscountAccountRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceDiscountAccountRelLocalServiceUtil.class,
			CommerceDiscountAccountRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1161196225