/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceShippingMethod;
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
 * Provides the local service utility for CommerceShippingMethod. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceShippingMethodLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingMethodLocalService
 * @generated
 */
public class CommerceShippingMethodLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceShippingMethodLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static com.liferay.commerce.model.CommerceAddressRestriction
			addCommerceAddressRestriction(
				long userId, long groupId, long commerceShippingMethodId,
				long countryId)
		throws PortalException {

		return getService().addCommerceAddressRestriction(
			userId, groupId, commerceShippingMethodId, countryId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static com.liferay.commerce.model.CommerceAddressRestriction
			addCommerceAddressRestriction(
				long commerceShippingMethodId, long countryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceAddressRestriction(
			commerceShippingMethodId, countryId, serviceContext);
	}

	/**
	 * Adds the commerce shipping method to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingMethod the commerce shipping method
	 * @return the commerce shipping method that was added
	 */
	public static CommerceShippingMethod addCommerceShippingMethod(
		CommerceShippingMethod commerceShippingMethod) {

		return getService().addCommerceShippingMethod(commerceShippingMethod);
	}

	public static CommerceShippingMethod addCommerceShippingMethod(
			long userId, long groupId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			String engineKey, java.io.File imageFile, double priority,
			String trackingURL)
		throws PortalException {

		return getService().addCommerceShippingMethod(
			userId, groupId, nameMap, descriptionMap, active, engineKey,
			imageFile, priority, trackingURL);
	}

	/**
	 * Creates a new commerce shipping method with the primary key. Does not add the commerce shipping method to the database.
	 *
	 * @param commerceShippingMethodId the primary key for the new commerce shipping method
	 * @return the new commerce shipping method
	 */
	public static CommerceShippingMethod createCommerceShippingMethod(
		long commerceShippingMethodId) {

		return getService().createCommerceShippingMethod(
			commerceShippingMethodId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCommerceAddressRestriction(
			long commerceAddressRestrictionId)
		throws PortalException {

		getService().deleteCommerceAddressRestriction(
			commerceAddressRestrictionId);
	}

	/**
	 * Deletes the commerce shipping method from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingMethod the commerce shipping method
	 * @return the commerce shipping method that was removed
	 * @throws PortalException
	 */
	public static CommerceShippingMethod deleteCommerceShippingMethod(
			CommerceShippingMethod commerceShippingMethod)
		throws PortalException {

		return getService().deleteCommerceShippingMethod(
			commerceShippingMethod);
	}

	/**
	 * Deletes the commerce shipping method with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method that was removed
	 * @throws PortalException if a commerce shipping method with the primary key could not be found
	 */
	public static CommerceShippingMethod deleteCommerceShippingMethod(
			long commerceShippingMethodId)
		throws PortalException {

		return getService().deleteCommerceShippingMethod(
			commerceShippingMethodId);
	}

	public static void deleteCommerceShippingMethods(long groupId)
		throws PortalException {

		getService().deleteCommerceShippingMethods(groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
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

	public static CommerceShippingMethod fetchCommerceShippingMethod(
		long commerceShippingMethodId) {

		return getService().fetchCommerceShippingMethod(
			commerceShippingMethodId);
	}

	public static CommerceShippingMethod fetchCommerceShippingMethod(
		long groupId, String engineKey) {

		return getService().fetchCommerceShippingMethod(groupId, engineKey);
	}

	public static CommerceShippingMethod
		fetchCommerceShippingMethodByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommerceShippingMethodByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<com.liferay.commerce.model.CommerceAddressRestriction>
		getCommerceAddressRestrictions(
			long commerceShippingMethodId, int start, int end,
			OrderByComparator
				<com.liferay.commerce.model.CommerceAddressRestriction>
					orderByComparator) {

		return getService().getCommerceAddressRestrictions(
			commerceShippingMethodId, start, end, orderByComparator);
	}

	public static int getCommerceAddressRestrictionsCount(
		long commerceShippingMethodId) {

		return getService().getCommerceAddressRestrictionsCount(
			commerceShippingMethodId);
	}

	/**
	 * Returns the commerce shipping method with the primary key.
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method
	 * @throws PortalException if a commerce shipping method with the primary key could not be found
	 */
	public static CommerceShippingMethod getCommerceShippingMethod(
			long commerceShippingMethodId)
		throws PortalException {

		return getService().getCommerceShippingMethod(commerceShippingMethodId);
	}

	public static CommerceShippingMethod
			getCommerceShippingMethodByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceShippingMethodByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns a range of all the commerce shipping methods.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @return the range of commerce shipping methods
	 */
	public static List<CommerceShippingMethod> getCommerceShippingMethods(
		int start, int end) {

		return getService().getCommerceShippingMethods(start, end);
	}

	public static List<CommerceShippingMethod> getCommerceShippingMethods(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator) {

		return getService().getCommerceShippingMethods(
			groupId, active, start, end, orderByComparator);
	}

	public static List<CommerceShippingMethod> getCommerceShippingMethods(
		long groupId, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator) {

		return getService().getCommerceShippingMethods(
			groupId, start, end, orderByComparator);
	}

	public static List<CommerceShippingMethod> getCommerceShippingMethods(
		long groupId, long countryId, boolean active) {

		return getService().getCommerceShippingMethods(
			groupId, countryId, active);
	}

	/**
	 * Returns the number of commerce shipping methods.
	 *
	 * @return the number of commerce shipping methods
	 */
	public static int getCommerceShippingMethodsCount() {
		return getService().getCommerceShippingMethodsCount();
	}

	public static int getCommerceShippingMethodsCount(long groupId) {
		return getService().getCommerceShippingMethodsCount(groupId);
	}

	public static int getCommerceShippingMethodsCount(
		long groupId, boolean active) {

		return getService().getCommerceShippingMethodsCount(groupId, active);
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

	public static CommerceShippingMethod setActive(
			long commerceShippingMethodId, boolean active)
		throws PortalException {

		return getService().setActive(commerceShippingMethodId, active);
	}

	/**
	 * Updates the commerce shipping method in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingMethod the commerce shipping method
	 * @return the commerce shipping method that was updated
	 */
	public static CommerceShippingMethod updateCommerceShippingMethod(
		CommerceShippingMethod commerceShippingMethod) {

		return getService().updateCommerceShippingMethod(
			commerceShippingMethod);
	}

	public static CommerceShippingMethod updateCommerceShippingMethod(
			long commerceShippingMethodId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			java.io.File imageFile, double priority, String trackingURL)
		throws PortalException {

		return getService().updateCommerceShippingMethod(
			commerceShippingMethodId, nameMap, descriptionMap, active,
			imageFile, priority, trackingURL);
	}

	public static CommerceShippingMethodLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceShippingMethodLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceShippingMethodLocalServiceUtil.class,
			CommerceShippingMethodLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-581278433