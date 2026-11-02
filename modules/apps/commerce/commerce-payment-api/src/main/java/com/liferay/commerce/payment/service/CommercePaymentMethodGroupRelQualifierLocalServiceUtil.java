/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service;

import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommercePaymentMethodGroupRelQualifier. This utility wraps
 * <code>com.liferay.commerce.payment.service.impl.CommercePaymentMethodGroupRelQualifierLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Luca Pellizzon
 * @see CommercePaymentMethodGroupRelQualifierLocalService
 * @generated
 */
public class CommercePaymentMethodGroupRelQualifierLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.payment.service.impl.CommercePaymentMethodGroupRelQualifierLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce payment method group rel qualifier to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifier the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was added
	 */
	public static CommercePaymentMethodGroupRelQualifier
		addCommercePaymentMethodGroupRelQualifier(
			CommercePaymentMethodGroupRelQualifier
				commercePaymentMethodGroupRelQualifier) {

		return getService().addCommercePaymentMethodGroupRelQualifier(
			commercePaymentMethodGroupRelQualifier);
	}

	public static CommercePaymentMethodGroupRelQualifier
			addCommercePaymentMethodGroupRelQualifier(
				long userId, String className, long classPK,
				long commercePaymentMethodGroupRelId)
		throws PortalException {

		return getService().addCommercePaymentMethodGroupRelQualifier(
			userId, className, classPK, commercePaymentMethodGroupRelId);
	}

	/**
	 * Creates a new commerce payment method group rel qualifier with the primary key. Does not add the commerce payment method group rel qualifier to the database.
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key for the new commerce payment method group rel qualifier
	 * @return the new commerce payment method group rel qualifier
	 */
	public static CommercePaymentMethodGroupRelQualifier
		createCommercePaymentMethodGroupRelQualifier(
			long commercePaymentMethodGroupRelQualifierId) {

		return getService().createCommercePaymentMethodGroupRelQualifier(
			commercePaymentMethodGroupRelQualifierId);
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
	 * Deletes the commerce payment method group rel qualifier from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifier the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was removed
	 * @throws PortalException
	 */
	public static CommercePaymentMethodGroupRelQualifier
			deleteCommercePaymentMethodGroupRelQualifier(
				CommercePaymentMethodGroupRelQualifier
					commercePaymentMethodGroupRelQualifier)
		throws PortalException {

		return getService().deleteCommercePaymentMethodGroupRelQualifier(
			commercePaymentMethodGroupRelQualifier);
	}

	/**
	 * Deletes the commerce payment method group rel qualifier with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key of the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was removed
	 * @throws PortalException if a commerce payment method group rel qualifier with the primary key could not be found
	 */
	public static CommercePaymentMethodGroupRelQualifier
			deleteCommercePaymentMethodGroupRelQualifier(
				long commercePaymentMethodGroupRelQualifierId)
		throws PortalException {

		return getService().deleteCommercePaymentMethodGroupRelQualifier(
			commercePaymentMethodGroupRelQualifierId);
	}

	public static void deleteCommercePaymentMethodGroupRelQualifiers(
			long commercePaymentMethodGroupRelId)
		throws PortalException {

		getService().deleteCommercePaymentMethodGroupRelQualifiers(
			commercePaymentMethodGroupRelId);
	}

	public static void deleteCommercePaymentMethodGroupRelQualifiers(
			String className, long commercePaymentMethodGroupRelId)
		throws PortalException {

		getService().deleteCommercePaymentMethodGroupRelQualifiers(
			className, commercePaymentMethodGroupRelId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierModelImpl</code>.
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

	public static CommercePaymentMethodGroupRelQualifier
		fetchCommercePaymentMethodGroupRelQualifier(
			long commercePaymentMethodGroupRelQualifierId) {

		return getService().fetchCommercePaymentMethodGroupRelQualifier(
			commercePaymentMethodGroupRelQualifierId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CommercePaymentMethodGroupRelQualifier>
		getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiers(
			long commercePaymentMethodGroupRelId, String keywords, int start,
			int end) {

		return getService().
			getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiers(
				commercePaymentMethodGroupRelId, keywords, start, end);
	}

	public static int
		getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiersCount(
			long commercePaymentMethodGroupRelId, String keywords) {

		return getService().
			getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiersCount(
				commercePaymentMethodGroupRelId, keywords);
	}

	/**
	 * Returns the commerce payment method group rel qualifier with the primary key.
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key of the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier
	 * @throws PortalException if a commerce payment method group rel qualifier with the primary key could not be found
	 */
	public static CommercePaymentMethodGroupRelQualifier
			getCommercePaymentMethodGroupRelQualifier(
				long commercePaymentMethodGroupRelQualifierId)
		throws PortalException {

		return getService().getCommercePaymentMethodGroupRelQualifier(
			commercePaymentMethodGroupRelQualifierId);
	}

	/**
	 * Returns a range of all the commerce payment method group rel qualifiers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment method group rel qualifiers
	 * @param end the upper bound of the range of commerce payment method group rel qualifiers (not inclusive)
	 * @return the range of commerce payment method group rel qualifiers
	 */
	public static List<CommercePaymentMethodGroupRelQualifier>
		getCommercePaymentMethodGroupRelQualifiers(int start, int end) {

		return getService().getCommercePaymentMethodGroupRelQualifiers(
			start, end);
	}

	public static List<CommercePaymentMethodGroupRelQualifier>
		getCommercePaymentMethodGroupRelQualifiers(
			String className, long commercePaymentMethodGroupRelId) {

		return getService().getCommercePaymentMethodGroupRelQualifiers(
			className, commercePaymentMethodGroupRelId);
	}

	/**
	 * Returns the number of commerce payment method group rel qualifiers.
	 *
	 * @return the number of commerce payment method group rel qualifiers
	 */
	public static int getCommercePaymentMethodGroupRelQualifiersCount() {
		return getService().getCommercePaymentMethodGroupRelQualifiersCount();
	}

	public static List<CommercePaymentMethodGroupRelQualifier>
		getCommerceTermEntryCommercePaymentMethodGroupRelQualifiers(
			long commercePaymentMethodGroupRelId, String keywords, int start,
			int end) {

		return getService().
			getCommerceTermEntryCommercePaymentMethodGroupRelQualifiers(
				commercePaymentMethodGroupRelId, keywords, start, end);
	}

	public static int
		getCommerceTermEntryCommercePaymentMethodGroupRelQualifiersCount(
			long commercePaymentMethodGroupRelId, String keywords) {

		return getService().
			getCommerceTermEntryCommercePaymentMethodGroupRelQualifiersCount(
				commercePaymentMethodGroupRelId, keywords);
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
	 * Updates the commerce payment method group rel qualifier in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifier the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was updated
	 */
	public static CommercePaymentMethodGroupRelQualifier
		updateCommercePaymentMethodGroupRelQualifier(
			CommercePaymentMethodGroupRelQualifier
				commercePaymentMethodGroupRelQualifier) {

		return getService().updateCommercePaymentMethodGroupRelQualifier(
			commercePaymentMethodGroupRelQualifier);
	}

	public static CommercePaymentMethodGroupRelQualifierLocalService
		getService() {

		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<CommercePaymentMethodGroupRelQualifierLocalService> _serviceSnapshot =
			new Snapshot<>(
				CommercePaymentMethodGroupRelQualifierLocalServiceUtil.class,
				CommercePaymentMethodGroupRelQualifierLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1599242053