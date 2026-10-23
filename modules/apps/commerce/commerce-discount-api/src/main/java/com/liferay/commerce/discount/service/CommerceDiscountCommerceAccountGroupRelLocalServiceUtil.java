/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceDiscountCommerceAccountGroupRel. This utility wraps
 * <code>com.liferay.commerce.discount.service.impl.CommerceDiscountCommerceAccountGroupRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceDiscountCommerceAccountGroupRelLocalService
 * @generated
 */
public class CommerceDiscountCommerceAccountGroupRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountCommerceAccountGroupRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce discount commerce account group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountCommerceAccountGroupRel the commerce discount commerce account group rel
	 * @return the commerce discount commerce account group rel that was added
	 */
	public static CommerceDiscountCommerceAccountGroupRel
		addCommerceDiscountCommerceAccountGroupRel(
			CommerceDiscountCommerceAccountGroupRel
				commerceDiscountCommerceAccountGroupRel) {

		return getService().addCommerceDiscountCommerceAccountGroupRel(
			commerceDiscountCommerceAccountGroupRel);
	}

	public static CommerceDiscountCommerceAccountGroupRel
			addCommerceDiscountCommerceAccountGroupRel(
				long userId, long commerceDiscountId,
				long commerceAccountGroupId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscountCommerceAccountGroupRel(
			userId, commerceDiscountId, commerceAccountGroupId, serviceContext);
	}

	/**
	 * Creates a new commerce discount commerce account group rel with the primary key. Does not add the commerce discount commerce account group rel to the database.
	 *
	 * @param commerceDiscountCommerceAccountGroupRelId the primary key for the new commerce discount commerce account group rel
	 * @return the new commerce discount commerce account group rel
	 */
	public static CommerceDiscountCommerceAccountGroupRel
		createCommerceDiscountCommerceAccountGroupRel(
			long commerceDiscountCommerceAccountGroupRelId) {

		return getService().createCommerceDiscountCommerceAccountGroupRel(
			commerceDiscountCommerceAccountGroupRelId);
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
	 * Deletes the commerce discount commerce account group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountCommerceAccountGroupRel the commerce discount commerce account group rel
	 * @return the commerce discount commerce account group rel that was removed
	 * @throws PortalException
	 */
	public static CommerceDiscountCommerceAccountGroupRel
			deleteCommerceDiscountCommerceAccountGroupRel(
				CommerceDiscountCommerceAccountGroupRel
					commerceDiscountCommerceAccountGroupRel)
		throws PortalException {

		return getService().deleteCommerceDiscountCommerceAccountGroupRel(
			commerceDiscountCommerceAccountGroupRel);
	}

	/**
	 * Deletes the commerce discount commerce account group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountCommerceAccountGroupRelId the primary key of the commerce discount commerce account group rel
	 * @return the commerce discount commerce account group rel that was removed
	 * @throws PortalException if a commerce discount commerce account group rel with the primary key could not be found
	 */
	public static CommerceDiscountCommerceAccountGroupRel
			deleteCommerceDiscountCommerceAccountGroupRel(
				long commerceDiscountCommerceAccountGroupRelId)
		throws PortalException {

		return getService().deleteCommerceDiscountCommerceAccountGroupRel(
			commerceDiscountCommerceAccountGroupRelId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static void
		deleteCommerceDiscountCommerceAccountGroupRelsBycommerceAccountGroupId(
			long commerceAccountGroupId) {

		getService().
			deleteCommerceDiscountCommerceAccountGroupRelsBycommerceAccountGroupId(
				commerceAccountGroupId);
	}

	public static void
			deleteCommerceDiscountCommerceAccountGroupRelsByCommerceDiscountId(
				long commerceDiscountId)
		throws PortalException {

		getService().
			deleteCommerceDiscountCommerceAccountGroupRelsByCommerceDiscountId(
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountCommerceAccountGroupRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountCommerceAccountGroupRelModelImpl</code>.
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

	public static CommerceDiscountCommerceAccountGroupRel
		fetchCommerceDiscountCommerceAccountGroupRel(
			long commerceDiscountCommerceAccountGroupRelId) {

		return getService().fetchCommerceDiscountCommerceAccountGroupRel(
			commerceDiscountCommerceAccountGroupRelId);
	}

	public static CommerceDiscountCommerceAccountGroupRel
		fetchCommerceDiscountCommerceAccountGroupRel(
			long commerceDiscountId, long commerceAccountGroupId) {

		return getService().fetchCommerceDiscountCommerceAccountGroupRel(
			commerceDiscountId, commerceAccountGroupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce discount commerce account group rel with the primary key.
	 *
	 * @param commerceDiscountCommerceAccountGroupRelId the primary key of the commerce discount commerce account group rel
	 * @return the commerce discount commerce account group rel
	 * @throws PortalException if a commerce discount commerce account group rel with the primary key could not be found
	 */
	public static CommerceDiscountCommerceAccountGroupRel
			getCommerceDiscountCommerceAccountGroupRel(
				long commerceDiscountCommerceAccountGroupRelId)
		throws PortalException {

		return getService().getCommerceDiscountCommerceAccountGroupRel(
			commerceDiscountCommerceAccountGroupRelId);
	}

	/**
	 * Returns a range of all the commerce discount commerce account group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountCommerceAccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount commerce account group rels
	 * @param end the upper bound of the range of commerce discount commerce account group rels (not inclusive)
	 * @return the range of commerce discount commerce account group rels
	 */
	public static List<CommerceDiscountCommerceAccountGroupRel>
		getCommerceDiscountCommerceAccountGroupRels(int start, int end) {

		return getService().getCommerceDiscountCommerceAccountGroupRels(
			start, end);
	}

	public static List<CommerceDiscountCommerceAccountGroupRel>
		getCommerceDiscountCommerceAccountGroupRels(
			long commerceDiscountId, int start, int end,
			OrderByComparator<CommerceDiscountCommerceAccountGroupRel>
				orderByComparator) {

		return getService().getCommerceDiscountCommerceAccountGroupRels(
			commerceDiscountId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce discount commerce account group rels.
	 *
	 * @return the number of commerce discount commerce account group rels
	 */
	public static int getCommerceDiscountCommerceAccountGroupRelsCount() {
		return getService().getCommerceDiscountCommerceAccountGroupRelsCount();
	}

	public static int getCommerceDiscountCommerceAccountGroupRelsCount(
		long commerceDiscountId) {

		return getService().getCommerceDiscountCommerceAccountGroupRelsCount(
			commerceDiscountId);
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
	 * Updates the commerce discount commerce account group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountCommerceAccountGroupRel the commerce discount commerce account group rel
	 * @return the commerce discount commerce account group rel that was updated
	 */
	public static CommerceDiscountCommerceAccountGroupRel
		updateCommerceDiscountCommerceAccountGroupRel(
			CommerceDiscountCommerceAccountGroupRel
				commerceDiscountCommerceAccountGroupRel) {

		return getService().updateCommerceDiscountCommerceAccountGroupRel(
			commerceDiscountCommerceAccountGroupRel);
	}

	public static CommerceDiscountCommerceAccountGroupRelLocalService
		getService() {

		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<CommerceDiscountCommerceAccountGroupRelLocalService> _serviceSnapshot =
			new Snapshot<>(
				CommerceDiscountCommerceAccountGroupRelLocalServiceUtil.class,
				CommerceDiscountCommerceAccountGroupRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1336936449