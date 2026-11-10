/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceShippingOptionAccountEntryRel. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceShippingOptionAccountEntryRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingOptionAccountEntryRelLocalService
 * @generated
 */
public class CommerceShippingOptionAccountEntryRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceShippingOptionAccountEntryRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce shipping option account entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingOptionAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingOptionAccountEntryRel the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel that was added
	 */
	public static CommerceShippingOptionAccountEntryRel
		addCommerceShippingOptionAccountEntryRel(
			CommerceShippingOptionAccountEntryRel
				commerceShippingOptionAccountEntryRel) {

		return getService().addCommerceShippingOptionAccountEntryRel(
			commerceShippingOptionAccountEntryRel);
	}

	public static CommerceShippingOptionAccountEntryRel
			addCommerceShippingOptionAccountEntryRel(
				long userId, long accountEntryId, long commerceChannelId,
				String commerceShippingMethodKey,
				String commerceShippingOptionKey)
		throws PortalException {

		return getService().addCommerceShippingOptionAccountEntryRel(
			userId, accountEntryId, commerceChannelId,
			commerceShippingMethodKey, commerceShippingOptionKey);
	}

	/**
	 * Creates a new commerce shipping option account entry rel with the primary key. Does not add the commerce shipping option account entry rel to the database.
	 *
	 * @param CommerceShippingOptionAccountEntryRelId the primary key for the new commerce shipping option account entry rel
	 * @return the new commerce shipping option account entry rel
	 */
	public static CommerceShippingOptionAccountEntryRel
		createCommerceShippingOptionAccountEntryRel(
			long CommerceShippingOptionAccountEntryRelId) {

		return getService().createCommerceShippingOptionAccountEntryRel(
			CommerceShippingOptionAccountEntryRelId);
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
	 * Deletes the commerce shipping option account entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingOptionAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingOptionAccountEntryRel the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel that was removed
	 */
	public static CommerceShippingOptionAccountEntryRel
		deleteCommerceShippingOptionAccountEntryRel(
			CommerceShippingOptionAccountEntryRel
				commerceShippingOptionAccountEntryRel) {

		return getService().deleteCommerceShippingOptionAccountEntryRel(
			commerceShippingOptionAccountEntryRel);
	}

	/**
	 * Deletes the commerce shipping option account entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingOptionAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CommerceShippingOptionAccountEntryRelId the primary key of the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel that was removed
	 * @throws PortalException if a commerce shipping option account entry rel with the primary key could not be found
	 */
	public static CommerceShippingOptionAccountEntryRel
			deleteCommerceShippingOptionAccountEntryRel(
				long CommerceShippingOptionAccountEntryRelId)
		throws PortalException {

		return getService().deleteCommerceShippingOptionAccountEntryRel(
			CommerceShippingOptionAccountEntryRelId);
	}

	public static void
		deleteCommerceShippingOptionAccountEntryRelsByAccountEntryId(
			long accountEntryId) {

		getService().
			deleteCommerceShippingOptionAccountEntryRelsByAccountEntryId(
				accountEntryId);
	}

	public static void
		deleteCommerceShippingOptionAccountEntryRelsByCommerceChannelId(
			long commerceChannelId) {

		getService().
			deleteCommerceShippingOptionAccountEntryRelsByCommerceChannelId(
				commerceChannelId);
	}

	public static void
		deleteCommerceShippingOptionAccountEntryRelsByCSFixedOptionKey(
			String commerceShippingFixedOptionKey) {

		getService().
			deleteCommerceShippingOptionAccountEntryRelsByCSFixedOptionKey(
				commerceShippingFixedOptionKey);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingOptionAccountEntryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingOptionAccountEntryRelModelImpl</code>.
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

	public static CommerceShippingOptionAccountEntryRel
		fetchCommerceShippingOptionAccountEntryRel(
			long CommerceShippingOptionAccountEntryRelId) {

		return getService().fetchCommerceShippingOptionAccountEntryRel(
			CommerceShippingOptionAccountEntryRelId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce shipping option account entry rel with the primary key.
	 *
	 * @param CommerceShippingOptionAccountEntryRelId the primary key of the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel
	 * @throws PortalException if a commerce shipping option account entry rel with the primary key could not be found
	 */
	public static CommerceShippingOptionAccountEntryRel
			getCommerceShippingOptionAccountEntryRel(
				long CommerceShippingOptionAccountEntryRelId)
		throws PortalException {

		return getService().getCommerceShippingOptionAccountEntryRel(
			CommerceShippingOptionAccountEntryRelId);
	}

	/**
	 * Returns a range of all the commerce shipping option account entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingOptionAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipping option account entry rels
	 * @param end the upper bound of the range of commerce shipping option account entry rels (not inclusive)
	 * @return the range of commerce shipping option account entry rels
	 */
	public static List<CommerceShippingOptionAccountEntryRel>
		getCommerceShippingOptionAccountEntryRels(int start, int end) {

		return getService().getCommerceShippingOptionAccountEntryRels(
			start, end);
	}

	/**
	 * Returns the number of commerce shipping option account entry rels.
	 *
	 * @return the number of commerce shipping option account entry rels
	 */
	public static int getCommerceShippingOptionAccountEntryRelsCount() {
		return getService().getCommerceShippingOptionAccountEntryRelsCount();
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
	 * Updates the commerce shipping option account entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingOptionAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingOptionAccountEntryRel the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel that was updated
	 */
	public static CommerceShippingOptionAccountEntryRel
		updateCommerceShippingOptionAccountEntryRel(
			CommerceShippingOptionAccountEntryRel
				commerceShippingOptionAccountEntryRel) {

		return getService().updateCommerceShippingOptionAccountEntryRel(
			commerceShippingOptionAccountEntryRel);
	}

	public static CommerceShippingOptionAccountEntryRel
			updateCommerceShippingOptionAccountEntryRel(
				long commerceShippingOptionAccountEntryRelId,
				String commerceShippingMethodKey,
				String commerceShippingOptionKey)
		throws PortalException {

		return getService().updateCommerceShippingOptionAccountEntryRel(
			commerceShippingOptionAccountEntryRelId, commerceShippingMethodKey,
			commerceShippingOptionKey);
	}

	public static CommerceShippingOptionAccountEntryRelLocalService
		getService() {

		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<CommerceShippingOptionAccountEntryRelLocalService> _serviceSnapshot =
			new Snapshot<>(
				CommerceShippingOptionAccountEntryRelLocalServiceUtil.class,
				CommerceShippingOptionAccountEntryRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-12795038