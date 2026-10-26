/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommercePriceModifierRel. This utility wraps
 * <code>com.liferay.commerce.pricing.service.impl.CommercePriceModifierRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Riccardo Alberti
 * @see CommercePriceModifierRelLocalService
 * @generated
 */
public class CommercePriceModifierRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.pricing.service.impl.CommercePriceModifierRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce price modifier rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceModifierRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceModifierRel the commerce price modifier rel
	 * @return the commerce price modifier rel that was added
	 */
	public static CommercePriceModifierRel addCommercePriceModifierRel(
		CommercePriceModifierRel commercePriceModifierRel) {

		return getService().addCommercePriceModifierRel(
			commercePriceModifierRel);
	}

	public static CommercePriceModifierRel addCommercePriceModifierRel(
			long commercePriceModifierId, String className, long classPK,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePriceModifierRel(
			commercePriceModifierId, className, classPK, serviceContext);
	}

	/**
	 * Creates a new commerce price modifier rel with the primary key. Does not add the commerce price modifier rel to the database.
	 *
	 * @param commercePriceModifierRelId the primary key for the new commerce price modifier rel
	 * @return the new commerce price modifier rel
	 */
	public static CommercePriceModifierRel createCommercePriceModifierRel(
		long commercePriceModifierRelId) {

		return getService().createCommercePriceModifierRel(
			commercePriceModifierRelId);
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
	 * Deletes the commerce price modifier rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceModifierRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceModifierRel the commerce price modifier rel
	 * @return the commerce price modifier rel that was removed
	 * @throws PortalException
	 */
	public static CommercePriceModifierRel deleteCommercePriceModifierRel(
			CommercePriceModifierRel commercePriceModifierRel)
		throws PortalException {

		return getService().deleteCommercePriceModifierRel(
			commercePriceModifierRel);
	}

	/**
	 * Deletes the commerce price modifier rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceModifierRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceModifierRelId the primary key of the commerce price modifier rel
	 * @return the commerce price modifier rel that was removed
	 * @throws PortalException if a commerce price modifier rel with the primary key could not be found
	 */
	public static CommercePriceModifierRel deleteCommercePriceModifierRel(
			long commercePriceModifierRelId)
		throws PortalException {

		return getService().deleteCommercePriceModifierRel(
			commercePriceModifierRelId);
	}

	public static void deleteCommercePriceModifierRels(
			long commercePriceModifierId)
		throws PortalException {

		getService().deleteCommercePriceModifierRels(commercePriceModifierId);
	}

	public static void deleteCommercePriceModifierRels(
			String className, long classPK)
		throws PortalException {

		getService().deleteCommercePriceModifierRels(className, classPK);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePriceModifierRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePriceModifierRelModelImpl</code>.
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

	public static CommercePriceModifierRel fetchCommercePriceModifierRel(
		long commercePriceModifierRelId) {

		return getService().fetchCommercePriceModifierRel(
			commercePriceModifierRelId);
	}

	public static CommercePriceModifierRel fetchCommercePriceModifierRel(
		long commercePriceModifierId, String className, long classPK) {

		return getService().fetchCommercePriceModifierRel(
			commercePriceModifierId, className, classPK);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static long[] getClassPKs(
		long commercePriceModifierId, String className) {

		return getService().getClassPKs(commercePriceModifierId, className);
	}

	/**
	 * Returns the commerce price modifier rel with the primary key.
	 *
	 * @param commercePriceModifierRelId the primary key of the commerce price modifier rel
	 * @return the commerce price modifier rel
	 * @throws PortalException if a commerce price modifier rel with the primary key could not be found
	 */
	public static CommercePriceModifierRel getCommercePriceModifierRel(
			long commercePriceModifierRelId)
		throws PortalException {

		return getService().getCommercePriceModifierRel(
			commercePriceModifierRelId);
	}

	/**
	 * Returns a range of all the commerce price modifier rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePriceModifierRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price modifier rels
	 * @param end the upper bound of the range of commerce price modifier rels (not inclusive)
	 * @return the range of commerce price modifier rels
	 */
	public static List<CommercePriceModifierRel> getCommercePriceModifierRels(
		int start, int end) {

		return getService().getCommercePriceModifierRels(start, end);
	}

	public static List<CommercePriceModifierRel> getCommercePriceModifierRels(
		long commercePriceModifierId, String className) {

		return getService().getCommercePriceModifierRels(
			commercePriceModifierId, className);
	}

	public static List<CommercePriceModifierRel> getCommercePriceModifierRels(
		long commercePriceModifierId, String className, int start, int end,
		OrderByComparator<CommercePriceModifierRel> orderByComparator) {

		return getService().getCommercePriceModifierRels(
			commercePriceModifierId, className, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce price modifier rels.
	 *
	 * @return the number of commerce price modifier rels
	 */
	public static int getCommercePriceModifierRelsCount() {
		return getService().getCommercePriceModifierRelsCount();
	}

	public static int getCommercePriceModifierRelsCount(
		long commercePriceModifierId, String className) {

		return getService().getCommercePriceModifierRelsCount(
			commercePriceModifierId, className);
	}

	public static List<CommercePriceModifierRel> getCommercePriceModifiersRels(
		String className, long classPK) {

		return getService().getCommercePriceModifiersRels(className, classPK);
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
	 * Updates the commerce price modifier rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceModifierRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceModifierRel the commerce price modifier rel
	 * @return the commerce price modifier rel that was updated
	 */
	public static CommercePriceModifierRel updateCommercePriceModifierRel(
		CommercePriceModifierRel commercePriceModifierRel) {

		return getService().updateCommercePriceModifierRel(
			commercePriceModifierRel);
	}

	public static CommercePriceModifierRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePriceModifierRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommercePriceModifierRelLocalServiceUtil.class,
			CommercePriceModifierRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:462834807