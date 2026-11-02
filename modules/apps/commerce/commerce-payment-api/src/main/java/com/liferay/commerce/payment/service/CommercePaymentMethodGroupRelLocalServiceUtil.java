/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service;

import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
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
 * Provides the local service utility for CommercePaymentMethodGroupRel. This utility wraps
 * <code>com.liferay.commerce.payment.service.impl.CommercePaymentMethodGroupRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Luca Pellizzon
 * @see CommercePaymentMethodGroupRelLocalService
 * @generated
 */
public class CommercePaymentMethodGroupRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.payment.service.impl.CommercePaymentMethodGroupRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static com.liferay.commerce.model.CommerceAddressRestriction
			addCommerceAddressRestriction(
				long userId, long groupId, long commercePaymentMethodGroupRelId,
				long countryId)
		throws PortalException {

		return getService().addCommerceAddressRestriction(
			userId, groupId, commercePaymentMethodGroupRelId, countryId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static com.liferay.commerce.model.CommerceAddressRestriction
			addCommerceAddressRestriction(
				long commercePaymentMethodGroupRelId, long countryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceAddressRestriction(
			commercePaymentMethodGroupRelId, countryId, serviceContext);
	}

	/**
	 * Adds the commerce payment method group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRel the commerce payment method group rel
	 * @return the commerce payment method group rel that was added
	 */
	public static CommercePaymentMethodGroupRel
		addCommercePaymentMethodGroupRel(
			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel) {

		return getService().addCommercePaymentMethodGroupRel(
			commercePaymentMethodGroupRel);
	}

	public static CommercePaymentMethodGroupRel
			addCommercePaymentMethodGroupRel(
				long userId, long groupId,
				Map<java.util.Locale, String> nameMap,
				Map<java.util.Locale, String> descriptionMap, boolean active,
				java.io.File imageFile, String paymentIntegrationKey,
				double priority, String typeSettings)
		throws PortalException {

		return getService().addCommercePaymentMethodGroupRel(
			userId, groupId, nameMap, descriptionMap, active, imageFile,
			paymentIntegrationKey, priority, typeSettings);
	}

	/**
	 * Creates a new commerce payment method group rel with the primary key. Does not add the commerce payment method group rel to the database.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key for the new commerce payment method group rel
	 * @return the new commerce payment method group rel
	 */
	public static CommercePaymentMethodGroupRel
		createCommercePaymentMethodGroupRel(
			long commercePaymentMethodGroupRelId) {

		return getService().createCommercePaymentMethodGroupRel(
			commercePaymentMethodGroupRelId);
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
	 * Deletes the commerce payment method group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRel the commerce payment method group rel
	 * @return the commerce payment method group rel that was removed
	 * @throws PortalException
	 */
	public static CommercePaymentMethodGroupRel
			deleteCommercePaymentMethodGroupRel(
				CommercePaymentMethodGroupRel commercePaymentMethodGroupRel)
		throws PortalException {

		return getService().deleteCommercePaymentMethodGroupRel(
			commercePaymentMethodGroupRel);
	}

	/**
	 * Deletes the commerce payment method group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel that was removed
	 * @throws PortalException if a commerce payment method group rel with the primary key could not be found
	 */
	public static CommercePaymentMethodGroupRel
			deleteCommercePaymentMethodGroupRel(
				long commercePaymentMethodGroupRelId)
		throws PortalException {

		return getService().deleteCommercePaymentMethodGroupRel(
			commercePaymentMethodGroupRelId);
	}

	public static void deleteCommercePaymentMethodGroupRels(long groupId)
		throws PortalException {

		getService().deleteCommercePaymentMethodGroupRels(groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelModelImpl</code>.
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

	public static CommercePaymentMethodGroupRel
		fetchCommercePaymentMethodGroupRel(
			long commercePaymentMethodGroupRelId) {

		return getService().fetchCommercePaymentMethodGroupRel(
			commercePaymentMethodGroupRelId);
	}

	public static CommercePaymentMethodGroupRel
		fetchCommercePaymentMethodGroupRel(
			long groupId, String paymentIntegrationKey) {

		return getService().fetchCommercePaymentMethodGroupRel(
			groupId, paymentIntegrationKey);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<com.liferay.commerce.model.CommerceAddressRestriction>
		getCommerceAddressRestrictions(
			long commercePaymentMethodGroupRelId, int start, int end,
			OrderByComparator
				<com.liferay.commerce.model.CommerceAddressRestriction>
					orderByComparator) {

		return getService().getCommerceAddressRestrictions(
			commercePaymentMethodGroupRelId, start, end, orderByComparator);
	}

	public static int getCommerceAddressRestrictionsCount(
		long commercePaymentMethodGroupRelId) {

		return getService().getCommerceAddressRestrictionsCount(
			commercePaymentMethodGroupRelId);
	}

	/**
	 * Returns the commerce payment method group rel with the primary key.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel
	 * @throws PortalException if a commerce payment method group rel with the primary key could not be found
	 */
	public static CommercePaymentMethodGroupRel
			getCommercePaymentMethodGroupRel(
				long commercePaymentMethodGroupRelId)
		throws PortalException {

		return getService().getCommercePaymentMethodGroupRel(
			commercePaymentMethodGroupRelId);
	}

	public static CommercePaymentMethodGroupRel
			getCommercePaymentMethodGroupRel(
				long groupId, String paymentIntegrationKey)
		throws com.liferay.commerce.payment.exception.
			NoSuchPaymentMethodGroupRelException {

		return getService().getCommercePaymentMethodGroupRel(
			groupId, paymentIntegrationKey);
	}

	/**
	 * Returns a range of all the commerce payment method group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @return the range of commerce payment method group rels
	 */
	public static List<CommercePaymentMethodGroupRel>
		getCommercePaymentMethodGroupRels(int start, int end) {

		return getService().getCommercePaymentMethodGroupRels(start, end);
	}

	public static List<CommercePaymentMethodGroupRel>
		getCommercePaymentMethodGroupRels(long groupId, boolean active) {

		return getService().getCommercePaymentMethodGroupRels(groupId, active);
	}

	public static List<CommercePaymentMethodGroupRel>
		getCommercePaymentMethodGroupRels(
			long groupId, boolean active, int start, int end) {

		return getService().getCommercePaymentMethodGroupRels(
			groupId, active, start, end);
	}

	public static List<CommercePaymentMethodGroupRel>
		getCommercePaymentMethodGroupRels(
			long groupId, int start, int end,
			OrderByComparator<CommercePaymentMethodGroupRel>
				orderByComparator) {

		return getService().getCommercePaymentMethodGroupRels(
			groupId, start, end, orderByComparator);
	}

	public static List<CommercePaymentMethodGroupRel>
		getCommercePaymentMethodGroupRels(
			long groupId, long countryId, boolean active) {

		return getService().getCommercePaymentMethodGroupRels(
			groupId, countryId, active);
	}

	/**
	 * Returns the number of commerce payment method group rels.
	 *
	 * @return the number of commerce payment method group rels
	 */
	public static int getCommercePaymentMethodGroupRelsCount() {
		return getService().getCommercePaymentMethodGroupRelsCount();
	}

	public static int getCommercePaymentMethodGroupRelsCount(
		long groupId, boolean active) {

		return getService().getCommercePaymentMethodGroupRelsCount(
			groupId, active);
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

	public static CommercePaymentMethodGroupRel setActive(
			long commercePaymentMethodGroupRelId, boolean active)
		throws PortalException {

		return getService().setActive(commercePaymentMethodGroupRelId, active);
	}

	/**
	 * Updates the commerce payment method group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRel the commerce payment method group rel
	 * @return the commerce payment method group rel that was updated
	 */
	public static CommercePaymentMethodGroupRel
		updateCommercePaymentMethodGroupRel(
			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel) {

		return getService().updateCommercePaymentMethodGroupRel(
			commercePaymentMethodGroupRel);
	}

	public static CommercePaymentMethodGroupRel
			updateCommercePaymentMethodGroupRel(
				long commercePaymentMethodGroupRelId,
				Map<java.util.Locale, String> nameMap,
				Map<java.util.Locale, String> descriptionMap,
				java.io.File imageFile, double priority, boolean active)
		throws PortalException {

		return getService().updateCommercePaymentMethodGroupRel(
			commercePaymentMethodGroupRelId, nameMap, descriptionMap, imageFile,
			priority, active);
	}

	public static CommercePaymentMethodGroupRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePaymentMethodGroupRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommercePaymentMethodGroupRelLocalServiceUtil.class,
			CommercePaymentMethodGroupRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-805430474