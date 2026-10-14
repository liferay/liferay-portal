/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceAddressRestriction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceAddressRestriction. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceAddressRestrictionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAddressRestrictionLocalService
 * @generated
 */
public class CommerceAddressRestrictionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceAddressRestrictionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce address restriction to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAddressRestrictionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAddressRestriction the commerce address restriction
	 * @return the commerce address restriction that was added
	 */
	public static CommerceAddressRestriction addCommerceAddressRestriction(
		CommerceAddressRestriction commerceAddressRestriction) {

		return getService().addCommerceAddressRestriction(
			commerceAddressRestriction);
	}

	public static CommerceAddressRestriction addCommerceAddressRestriction(
			long userId, long groupId, String className, long classPK,
			long countryId)
		throws PortalException {

		return getService().addCommerceAddressRestriction(
			userId, groupId, className, classPK, countryId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static CommerceAddressRestriction addCommerceAddressRestriction(
			String className, long classPK, long countryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceAddressRestriction(
			className, classPK, countryId, serviceContext);
	}

	/**
	 * Creates a new commerce address restriction with the primary key. Does not add the commerce address restriction to the database.
	 *
	 * @param commerceAddressRestrictionId the primary key for the new commerce address restriction
	 * @return the new commerce address restriction
	 */
	public static CommerceAddressRestriction createCommerceAddressRestriction(
		long commerceAddressRestrictionId) {

		return getService().createCommerceAddressRestriction(
			commerceAddressRestrictionId);
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
	 * Deletes the commerce address restriction from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAddressRestrictionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAddressRestriction the commerce address restriction
	 * @return the commerce address restriction that was removed
	 */
	public static CommerceAddressRestriction deleteCommerceAddressRestriction(
		CommerceAddressRestriction commerceAddressRestriction) {

		return getService().deleteCommerceAddressRestriction(
			commerceAddressRestriction);
	}

	/**
	 * Deletes the commerce address restriction with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAddressRestrictionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAddressRestrictionId the primary key of the commerce address restriction
	 * @return the commerce address restriction that was removed
	 * @throws PortalException if a commerce address restriction with the primary key could not be found
	 */
	public static CommerceAddressRestriction deleteCommerceAddressRestriction(
			long commerceAddressRestrictionId)
		throws PortalException {

		return getService().deleteCommerceAddressRestriction(
			commerceAddressRestrictionId);
	}

	public static void deleteCommerceAddressRestrictions(
		String className, long classPK) {

		getService().deleteCommerceAddressRestrictions(className, classPK);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAddressRestrictionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAddressRestrictionModelImpl</code>.
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

	public static CommerceAddressRestriction fetchCommerceAddressRestriction(
		long commerceAddressRestrictionId) {

		return getService().fetchCommerceAddressRestriction(
			commerceAddressRestrictionId);
	}

	public static CommerceAddressRestriction fetchCommerceAddressRestriction(
		String className, long classPK, long countryId) {

		return getService().fetchCommerceAddressRestriction(
			className, classPK, countryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce address restriction with the primary key.
	 *
	 * @param commerceAddressRestrictionId the primary key of the commerce address restriction
	 * @return the commerce address restriction
	 * @throws PortalException if a commerce address restriction with the primary key could not be found
	 */
	public static CommerceAddressRestriction getCommerceAddressRestriction(
			long commerceAddressRestrictionId)
		throws PortalException {

		return getService().getCommerceAddressRestriction(
			commerceAddressRestrictionId);
	}

	/**
	 * Returns a range of all the commerce address restrictions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAddressRestrictionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce address restrictions
	 * @param end the upper bound of the range of commerce address restrictions (not inclusive)
	 * @return the range of commerce address restrictions
	 */
	public static List<CommerceAddressRestriction>
		getCommerceAddressRestrictions(int start, int end) {

		return getService().getCommerceAddressRestrictions(start, end);
	}

	public static List<CommerceAddressRestriction>
		getCommerceAddressRestrictions(
			String className, long classPK, int start, int end,
			OrderByComparator<CommerceAddressRestriction> orderByComparator) {

		return getService().getCommerceAddressRestrictions(
			className, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce address restrictions.
	 *
	 * @return the number of commerce address restrictions
	 */
	public static int getCommerceAddressRestrictionsCount() {
		return getService().getCommerceAddressRestrictionsCount();
	}

	public static int getCommerceAddressRestrictionsCount(
		String className, long classPK) {

		return getService().getCommerceAddressRestrictionsCount(
			className, classPK);
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

	public static boolean isCommerceAddressRestricted(
		String className, long classPK, long countryId) {

		return getService().isCommerceAddressRestricted(
			className, classPK, countryId);
	}

	public static boolean isCommerceShippingMethodRestricted(
		long commerceShippingMethodId, long countryId) {

		return getService().isCommerceShippingMethodRestricted(
			commerceShippingMethodId, countryId);
	}

	/**
	 * Updates the commerce address restriction in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAddressRestrictionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAddressRestriction the commerce address restriction
	 * @return the commerce address restriction that was updated
	 */
	public static CommerceAddressRestriction updateCommerceAddressRestriction(
		CommerceAddressRestriction commerceAddressRestriction) {

		return getService().updateCommerceAddressRestriction(
			commerceAddressRestriction);
	}

	public static CommerceAddressRestrictionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceAddressRestrictionLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceAddressRestrictionLocalServiceUtil.class,
			CommerceAddressRestrictionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:328843842