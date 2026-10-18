/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceChannelRel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CommerceChannelRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceChannelRelLocalService
 * @generated
 */
public class CommerceChannelRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CommerceChannelRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce channel rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelRel the commerce channel rel
	 * @return the commerce channel rel that was added
	 */
	public static CommerceChannelRel addCommerceChannelRel(
		CommerceChannelRel commerceChannelRel) {

		return getService().addCommerceChannelRel(commerceChannelRel);
	}

	public static CommerceChannelRel addCommerceChannelRel(
			String className, long classPK, long commerceChannelId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceChannelRel(
			className, classPK, commerceChannelId, serviceContext);
	}

	public static List<CommerceChannelRel> addCommerceChannelRels(
		String className, long[] classPKs, long commerceChannelId,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().addCommerceChannelRels(
			className, classPKs, commerceChannelId, serviceContext);
	}

	/**
	 * Creates a new commerce channel rel with the primary key. Does not add the commerce channel rel to the database.
	 *
	 * @param commerceChannelRelId the primary key for the new commerce channel rel
	 * @return the new commerce channel rel
	 */
	public static CommerceChannelRel createCommerceChannelRel(
		long commerceChannelRelId) {

		return getService().createCommerceChannelRel(commerceChannelRelId);
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
	 * Deletes the commerce channel rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelRel the commerce channel rel
	 * @return the commerce channel rel that was removed
	 */
	public static CommerceChannelRel deleteCommerceChannelRel(
		CommerceChannelRel commerceChannelRel) {

		return getService().deleteCommerceChannelRel(commerceChannelRel);
	}

	/**
	 * Deletes the commerce channel rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelRelId the primary key of the commerce channel rel
	 * @return the commerce channel rel that was removed
	 * @throws PortalException if a commerce channel rel with the primary key could not be found
	 */
	public static CommerceChannelRel deleteCommerceChannelRel(
			long commerceChannelRelId)
		throws PortalException {

		return getService().deleteCommerceChannelRel(commerceChannelRelId);
	}

	public static void deleteCommerceChannelRels(long commerceChannelId) {
		getService().deleteCommerceChannelRels(commerceChannelId);
	}

	public static void deleteCommerceChannelRels(
		String className, long classPK) {

		getService().deleteCommerceChannelRels(className, classPK);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelRelModelImpl</code>.
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

	public static CommerceChannelRel fetchCommerceChannelRel(
		long commerceChannelRelId) {

		return getService().fetchCommerceChannelRel(commerceChannelRelId);
	}

	public static CommerceChannelRel fetchCommerceChannelRel(
		String className, long classPK, long commerceChannelId) {

		return getService().fetchCommerceChannelRel(
			className, classPK, commerceChannelId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce channel rel with the primary key.
	 *
	 * @param commerceChannelRelId the primary key of the commerce channel rel
	 * @return the commerce channel rel
	 * @throws PortalException if a commerce channel rel with the primary key could not be found
	 */
	public static CommerceChannelRel getCommerceChannelRel(
			long commerceChannelRelId)
		throws PortalException {

		return getService().getCommerceChannelRel(commerceChannelRelId);
	}

	/**
	 * Returns a range of all the commerce channel rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce channel rels
	 * @param end the upper bound of the range of commerce channel rels (not inclusive)
	 * @return the range of commerce channel rels
	 */
	public static List<CommerceChannelRel> getCommerceChannelRels(
		int start, int end) {

		return getService().getCommerceChannelRels(start, end);
	}

	public static List<CommerceChannelRel> getCommerceChannelRels(
		long commerceChannelId, int start, int end,
		OrderByComparator<CommerceChannelRel> orderByComparator) {

		return getService().getCommerceChannelRels(
			commerceChannelId, start, end, orderByComparator);
	}

	public static List<CommerceChannelRel> getCommerceChannelRels(
		String className, long classPK, int start, int end,
		OrderByComparator<CommerceChannelRel> orderByComparator) {

		return getService().getCommerceChannelRels(
			className, classPK, start, end, orderByComparator);
	}

	public static List<CommerceChannelRel> getCommerceChannelRels(
		String className, long classPK, String name, int start, int end) {

		return getService().getCommerceChannelRels(
			className, classPK, name, start, end);
	}

	/**
	 * Returns the number of commerce channel rels.
	 *
	 * @return the number of commerce channel rels
	 */
	public static int getCommerceChannelRelsCount() {
		return getService().getCommerceChannelRelsCount();
	}

	public static int getCommerceChannelRelsCount(long commerceChannelId) {
		return getService().getCommerceChannelRelsCount(commerceChannelId);
	}

	public static int getCommerceChannelRelsCount(
		String className, long classPK) {

		return getService().getCommerceChannelRelsCount(className, classPK);
	}

	public static List<CommerceChannelRel>
		getCommerceCurrencyCommerceChannelRels(
			long commerceChannelId, String name, int start, int end) {

		return getService().getCommerceCurrencyCommerceChannelRels(
			commerceChannelId, name, start, end);
	}

	public static int getCommerceCurrencyCommerceChannelRelsCount(
		long commerceChannelId, String name) {

		return getService().getCommerceCurrencyCommerceChannelRelsCount(
			commerceChannelId, name);
	}

	public static List<CommerceChannelRel> getCountryCommerceChannelRels(
		long commerceChannelId, String name, int start, int end) {

		return getService().getCountryCommerceChannelRels(
			commerceChannelId, name, start, end);
	}

	public static int getCountryCommerceChannelRelsCount(
		long commerceChannelId, String name) {

		return getService().getCountryCommerceChannelRelsCount(
			commerceChannelId, name);
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
	 * Updates the commerce channel rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelRel the commerce channel rel
	 * @return the commerce channel rel that was updated
	 */
	public static CommerceChannelRel updateCommerceChannelRel(
		CommerceChannelRel commerceChannelRel) {

		return getService().updateCommerceChannelRel(commerceChannelRel);
	}

	public static CommerceChannelRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceChannelRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceChannelRelLocalServiceUtil.class,
			CommerceChannelRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1440390388