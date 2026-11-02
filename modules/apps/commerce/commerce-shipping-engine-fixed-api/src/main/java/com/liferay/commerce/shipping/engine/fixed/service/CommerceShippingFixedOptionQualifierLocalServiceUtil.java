/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service;

import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionQualifier;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceShippingFixedOptionQualifier. This utility wraps
 * <code>com.liferay.commerce.shipping.engine.fixed.service.impl.CommerceShippingFixedOptionQualifierLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingFixedOptionQualifierLocalService
 * @generated
 */
public class CommerceShippingFixedOptionQualifierLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.shipping.engine.fixed.service.impl.CommerceShippingFixedOptionQualifierLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce shipping fixed option qualifier to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionQualifier the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier that was added
	 */
	public static CommerceShippingFixedOptionQualifier
		addCommerceShippingFixedOptionQualifier(
			CommerceShippingFixedOptionQualifier
				commerceShippingFixedOptionQualifier) {

		return getService().addCommerceShippingFixedOptionQualifier(
			commerceShippingFixedOptionQualifier);
	}

	public static CommerceShippingFixedOptionQualifier
			addCommerceShippingFixedOptionQualifier(
				long userId, String className, long classPK,
				long commerceShippingFixedOptionId)
		throws PortalException {

		return getService().addCommerceShippingFixedOptionQualifier(
			userId, className, classPK, commerceShippingFixedOptionId);
	}

	/**
	 * Creates a new commerce shipping fixed option qualifier with the primary key. Does not add the commerce shipping fixed option qualifier to the database.
	 *
	 * @param commerceShippingFixedOptionQualifierId the primary key for the new commerce shipping fixed option qualifier
	 * @return the new commerce shipping fixed option qualifier
	 */
	public static CommerceShippingFixedOptionQualifier
		createCommerceShippingFixedOptionQualifier(
			long commerceShippingFixedOptionQualifierId) {

		return getService().createCommerceShippingFixedOptionQualifier(
			commerceShippingFixedOptionQualifierId);
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
	 * Deletes the commerce shipping fixed option qualifier from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionQualifier the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier that was removed
	 */
	public static CommerceShippingFixedOptionQualifier
		deleteCommerceShippingFixedOptionQualifier(
			CommerceShippingFixedOptionQualifier
				commerceShippingFixedOptionQualifier) {

		return getService().deleteCommerceShippingFixedOptionQualifier(
			commerceShippingFixedOptionQualifier);
	}

	/**
	 * Deletes the commerce shipping fixed option qualifier with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionQualifierId the primary key of the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier that was removed
	 * @throws PortalException if a commerce shipping fixed option qualifier with the primary key could not be found
	 */
	public static CommerceShippingFixedOptionQualifier
			deleteCommerceShippingFixedOptionQualifier(
				long commerceShippingFixedOptionQualifierId)
		throws PortalException {

		return getService().deleteCommerceShippingFixedOptionQualifier(
			commerceShippingFixedOptionQualifierId);
	}

	public static void deleteCommerceShippingFixedOptionQualifiers(
			long commerceShippingFixedOptionId)
		throws PortalException {

		getService().deleteCommerceShippingFixedOptionQualifiers(
			commerceShippingFixedOptionId);
	}

	public static void deleteCommerceShippingFixedOptionQualifiers(
			String className, long commerceShippingFixedOptionId)
		throws PortalException {

		getService().deleteCommerceShippingFixedOptionQualifiers(
			className, commerceShippingFixedOptionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionQualifierModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionQualifierModelImpl</code>.
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

	public static CommerceShippingFixedOptionQualifier
		fetchCommerceShippingFixedOptionQualifier(
			long commerceShippingFixedOptionQualifierId) {

		return getService().fetchCommerceShippingFixedOptionQualifier(
			commerceShippingFixedOptionQualifierId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CommerceShippingFixedOptionQualifier>
		getCommerceOrderTypeCommerceShippingFixedOptionQualifiers(
			long commerceShippingFixedOptionId, String keywords, int start,
			int end) {

		return getService().
			getCommerceOrderTypeCommerceShippingFixedOptionQualifiers(
				commerceShippingFixedOptionId, keywords, start, end);
	}

	public static int
		getCommerceOrderTypeCommerceShippingFixedOptionQualifiersCount(
			long commerceShippingFixedOptionId, String keywords) {

		return getService().
			getCommerceOrderTypeCommerceShippingFixedOptionQualifiersCount(
				commerceShippingFixedOptionId, keywords);
	}

	/**
	 * Returns the commerce shipping fixed option qualifier with the primary key.
	 *
	 * @param commerceShippingFixedOptionQualifierId the primary key of the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier
	 * @throws PortalException if a commerce shipping fixed option qualifier with the primary key could not be found
	 */
	public static CommerceShippingFixedOptionQualifier
			getCommerceShippingFixedOptionQualifier(
				long commerceShippingFixedOptionQualifierId)
		throws PortalException {

		return getService().getCommerceShippingFixedOptionQualifier(
			commerceShippingFixedOptionQualifierId);
	}

	/**
	 * Returns a range of all the commerce shipping fixed option qualifiers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionQualifierModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipping fixed option qualifiers
	 * @param end the upper bound of the range of commerce shipping fixed option qualifiers (not inclusive)
	 * @return the range of commerce shipping fixed option qualifiers
	 */
	public static List<CommerceShippingFixedOptionQualifier>
		getCommerceShippingFixedOptionQualifiers(int start, int end) {

		return getService().getCommerceShippingFixedOptionQualifiers(
			start, end);
	}

	public static List<CommerceShippingFixedOptionQualifier>
		getCommerceShippingFixedOptionQualifiers(
			String className, long commerceShippingFixedOptionId) {

		return getService().getCommerceShippingFixedOptionQualifiers(
			className, commerceShippingFixedOptionId);
	}

	/**
	 * Returns the number of commerce shipping fixed option qualifiers.
	 *
	 * @return the number of commerce shipping fixed option qualifiers
	 */
	public static int getCommerceShippingFixedOptionQualifiersCount() {
		return getService().getCommerceShippingFixedOptionQualifiersCount();
	}

	public static List<CommerceShippingFixedOptionQualifier>
		getCommerceTermEntryCommerceShippingFixedOptionQualifiers(
			long commerceShippingFixedOptionId, String keywords, int start,
			int end) {

		return getService().
			getCommerceTermEntryCommerceShippingFixedOptionQualifiers(
				commerceShippingFixedOptionId, keywords, start, end);
	}

	public static int
		getCommerceTermEntryCommerceShippingFixedOptionQualifiersCount(
			long commerceShippingFixedOptionId, String keywords) {

		return getService().
			getCommerceTermEntryCommerceShippingFixedOptionQualifiersCount(
				commerceShippingFixedOptionId, keywords);
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
	 * Updates the commerce shipping fixed option qualifier in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionQualifier the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier that was updated
	 */
	public static CommerceShippingFixedOptionQualifier
		updateCommerceShippingFixedOptionQualifier(
			CommerceShippingFixedOptionQualifier
				commerceShippingFixedOptionQualifier) {

		return getService().updateCommerceShippingFixedOptionQualifier(
			commerceShippingFixedOptionQualifier);
	}

	public static CommerceShippingFixedOptionQualifierLocalService
		getService() {

		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<CommerceShippingFixedOptionQualifierLocalService> _serviceSnapshot =
			new Snapshot<>(
				CommerceShippingFixedOptionQualifierLocalServiceUtil.class,
				CommerceShippingFixedOptionQualifierLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1864832843