/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceChannelAccountEntryRel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CommerceChannelAccountEntryRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceChannelAccountEntryRelLocalService
 * @generated
 */
public class CommerceChannelAccountEntryRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CommerceChannelAccountEntryRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce channel account entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRel the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was added
	 */
	public static CommerceChannelAccountEntryRel
		addCommerceChannelAccountEntryRel(
			CommerceChannelAccountEntryRel commerceChannelAccountEntryRel) {

		return getService().addCommerceChannelAccountEntryRel(
			commerceChannelAccountEntryRel);
	}

	public static CommerceChannelAccountEntryRel
			addCommerceChannelAccountEntryRel(
				long userId, long accountEntryId, String className,
				long classPK, long commerceChannelId,
				boolean overrideEligibility, double priority, int type)
		throws PortalException {

		return getService().addCommerceChannelAccountEntryRel(
			userId, accountEntryId, className, classPK, commerceChannelId,
			overrideEligibility, priority, type);
	}

	/**
	 * Creates a new commerce channel account entry rel with the primary key. Does not add the commerce channel account entry rel to the database.
	 *
	 * @param commerceChannelAccountEntryRelId the primary key for the new commerce channel account entry rel
	 * @return the new commerce channel account entry rel
	 */
	public static CommerceChannelAccountEntryRel
		createCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId) {

		return getService().createCommerceChannelAccountEntryRel(
			commerceChannelAccountEntryRelId);
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
	 * Deletes the commerce channel account entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRel the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was removed
	 * @throws PortalException
	 */
	public static CommerceChannelAccountEntryRel
			deleteCommerceChannelAccountEntryRel(
				CommerceChannelAccountEntryRel commerceChannelAccountEntryRel)
		throws PortalException {

		return getService().deleteCommerceChannelAccountEntryRel(
			commerceChannelAccountEntryRel);
	}

	/**
	 * Deletes the commerce channel account entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRelId the primary key of the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was removed
	 * @throws PortalException if a commerce channel account entry rel with the primary key could not be found
	 */
	public static CommerceChannelAccountEntryRel
			deleteCommerceChannelAccountEntryRel(
				long commerceChannelAccountEntryRelId)
		throws PortalException {

		return getService().deleteCommerceChannelAccountEntryRel(
			commerceChannelAccountEntryRelId);
	}

	public static void deleteCommerceChannelAccountEntryRels(
			String className, long classPK)
		throws PortalException {

		getService().deleteCommerceChannelAccountEntryRels(className, classPK);
	}

	public static void deleteCommerceChannelAccountEntryRelsByAccountEntryId(
			long accountEntryId)
		throws PortalException {

		getService().deleteCommerceChannelAccountEntryRelsByAccountEntryId(
			accountEntryId);
	}

	public static void deleteCommerceChannelAccountEntryRelsByCommerceChannelId(
			long commerceChannelId)
		throws PortalException {

		getService().deleteCommerceChannelAccountEntryRelsByCommerceChannelId(
			commerceChannelId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelAccountEntryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelAccountEntryRelModelImpl</code>.
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

	public static CommerceChannelAccountEntryRel
		fetchCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId) {

		return getService().fetchCommerceChannelAccountEntryRel(
			commerceChannelAccountEntryRelId);
	}

	public static CommerceChannelAccountEntryRel
		fetchCommerceChannelAccountEntryRel(
			long accountEntryId, long commerceChannelId, int type) {

		return getService().fetchCommerceChannelAccountEntryRel(
			accountEntryId, commerceChannelId, type);
	}

	public static CommerceChannelAccountEntryRel
		fetchCommerceChannelAccountEntryRel(
			long accountEntryId, String className, long classPK,
			long commerceChannelId, int type) {

		return getService().fetchCommerceChannelAccountEntryRel(
			accountEntryId, className, classPK, commerceChannelId, type);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce channel account entry rel with the primary key.
	 *
	 * @param commerceChannelAccountEntryRelId the primary key of the commerce channel account entry rel
	 * @return the commerce channel account entry rel
	 * @throws PortalException if a commerce channel account entry rel with the primary key could not be found
	 */
	public static CommerceChannelAccountEntryRel
			getCommerceChannelAccountEntryRel(
				long commerceChannelAccountEntryRelId)
		throws PortalException {

		return getService().getCommerceChannelAccountEntryRel(
			commerceChannelAccountEntryRelId);
	}

	/**
	 * Returns a range of all the commerce channel account entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce channel account entry rels
	 * @param end the upper bound of the range of commerce channel account entry rels (not inclusive)
	 * @return the range of commerce channel account entry rels
	 */
	public static List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(int start, int end) {

		return getService().getCommerceChannelAccountEntryRels(start, end);
	}

	public static List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			long accountEntryId, int type, int start, int end,
			OrderByComparator<CommerceChannelAccountEntryRel>
				orderByComparator) {

		return getService().getCommerceChannelAccountEntryRels(
			accountEntryId, type, start, end, orderByComparator);
	}

	public static List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			long commerceChannelId, String name, int type, int start, int end) {

		return getService().getCommerceChannelAccountEntryRels(
			commerceChannelId, name, type, start, end);
	}

	/**
	 * Returns the number of commerce channel account entry rels.
	 *
	 * @return the number of commerce channel account entry rels
	 */
	public static int getCommerceChannelAccountEntryRelsCount() {
		return getService().getCommerceChannelAccountEntryRelsCount();
	}

	public static int getCommerceChannelAccountEntryRelsCount(
		long commerceChannelId, String name, int type) {

		return getService().getCommerceChannelAccountEntryRelsCount(
			commerceChannelId, name, type);
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
	 * Updates the commerce channel account entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRel the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was updated
	 */
	public static CommerceChannelAccountEntryRel
		updateCommerceChannelAccountEntryRel(
			CommerceChannelAccountEntryRel commerceChannelAccountEntryRel) {

		return getService().updateCommerceChannelAccountEntryRel(
			commerceChannelAccountEntryRel);
	}

	public static CommerceChannelAccountEntryRel
			updateCommerceChannelAccountEntryRel(
				long commerceChannelAccountEntryRelId, long commerceChannelId,
				long classPK, boolean overrideEligibility, double priority)
		throws PortalException {

		return getService().updateCommerceChannelAccountEntryRel(
			commerceChannelAccountEntryRelId, commerceChannelId, classPK,
			overrideEligibility, priority);
	}

	public static CommerceChannelAccountEntryRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceChannelAccountEntryRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceChannelAccountEntryRelLocalServiceUtil.class,
			CommerceChannelAccountEntryRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-421125514