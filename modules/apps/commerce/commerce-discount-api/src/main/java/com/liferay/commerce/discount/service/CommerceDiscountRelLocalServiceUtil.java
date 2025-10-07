/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceDiscountRel. This utility wraps
 * <code>com.liferay.commerce.discount.service.impl.CommerceDiscountRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceDiscountRelLocalService
 * @generated
 */
public class CommerceDiscountRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce discount rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRel the commerce discount rel
	 * @return the commerce discount rel that was added
	 */
	public static CommerceDiscountRel addCommerceDiscountRel(
		CommerceDiscountRel commerceDiscountRel) {

		return getService().addCommerceDiscountRel(commerceDiscountRel);
	}

	public static CommerceDiscountRel addCommerceDiscountRel(
			long commerceDiscountId, String className, long classPK,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscountRel(
			commerceDiscountId, className, classPK,
			typeSettingsUnicodeProperties, serviceContext);
	}

	/**
	 * Creates a new commerce discount rel with the primary key. Does not add the commerce discount rel to the database.
	 *
	 * @param commerceDiscountRelId the primary key for the new commerce discount rel
	 * @return the new commerce discount rel
	 */
	public static CommerceDiscountRel createCommerceDiscountRel(
		long commerceDiscountRelId) {

		return getService().createCommerceDiscountRel(commerceDiscountRelId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static CommerceDiscountRel deleteCommerceDiscountRel(
			com.liferay.commerce.discount.model.CommerceDiscount
				commerceDiscount,
			CommerceDiscountRel commerceDiscountRel)
		throws PortalException {

		return getService().deleteCommerceDiscountRel(
			commerceDiscount, commerceDiscountRel);
	}

	/**
	 * Deletes the commerce discount rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRel the commerce discount rel
	 * @return the commerce discount rel that was removed
	 */
	public static CommerceDiscountRel deleteCommerceDiscountRel(
		CommerceDiscountRel commerceDiscountRel) {

		return getService().deleteCommerceDiscountRel(commerceDiscountRel);
	}

	/**
	 * Deletes the commerce discount rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel that was removed
	 * @throws PortalException if a commerce discount rel with the primary key could not be found
	 */
	public static CommerceDiscountRel deleteCommerceDiscountRel(
			long commerceDiscountRelId)
		throws PortalException {

		return getService().deleteCommerceDiscountRel(commerceDiscountRelId);
	}

	public static void deleteCommerceDiscountRels(
			com.liferay.commerce.discount.model.CommerceDiscount
				commerceDiscount)
		throws PortalException {

		getService().deleteCommerceDiscountRels(commerceDiscount);
	}

	public static void deleteCommerceDiscountRels(
			String className, long classPK)
		throws PortalException {

		getService().deleteCommerceDiscountRels(className, classPK);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRelModelImpl</code>.
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

	public static CommerceDiscountRel fetchCommerceDiscountRel(
		long commerceDiscountRelId) {

		return getService().fetchCommerceDiscountRel(commerceDiscountRelId);
	}

	public static CommerceDiscountRel fetchCommerceDiscountRel(
		long commerceDiscountId, String className, long classPK) {

		return getService().fetchCommerceDiscountRel(
			commerceDiscountId, className, classPK);
	}

	public static CommerceDiscountRel fetchCommerceDiscountRel(
		String className, long classPK) {

		return getService().fetchCommerceDiscountRel(className, classPK);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CommerceDiscountRel> getCategoriesByCommerceDiscountId(
		long commerceDiscountId, String name, int start, int end) {

		return getService().getCategoriesByCommerceDiscountId(
			commerceDiscountId, name, start, end);
	}

	public static int getCategoriesByCommerceDiscountIdCount(
		long commerceDiscountId, String name) {

		return getService().getCategoriesByCommerceDiscountIdCount(
			commerceDiscountId, name);
	}

	public static long[] getClassPKs(
		long commerceDiscountId, String className) {

		return getService().getClassPKs(commerceDiscountId, className);
	}

	/**
	 * Returns the commerce discount rel with the primary key.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel
	 * @throws PortalException if a commerce discount rel with the primary key could not be found
	 */
	public static CommerceDiscountRel getCommerceDiscountRel(
			long commerceDiscountRelId)
		throws PortalException {

		return getService().getCommerceDiscountRel(commerceDiscountRelId);
	}

	/**
	 * Returns a range of all the commerce discount rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of commerce discount rels
	 */
	public static List<CommerceDiscountRel> getCommerceDiscountRels(
		int start, int end) {

		return getService().getCommerceDiscountRels(start, end);
	}

	public static List<CommerceDiscountRel> getCommerceDiscountRels(
		long classNameId, long classPK) {

		return getService().getCommerceDiscountRels(classNameId, classPK);
	}

	public static List<CommerceDiscountRel> getCommerceDiscountRels(
		long classNameId, long classPK, String unitOfMeasureKey) {

		return getService().getCommerceDiscountRels(
			classNameId, classPK, unitOfMeasureKey);
	}

	public static List<CommerceDiscountRel> getCommerceDiscountRels(
		long commerceDiscountId, String className) {

		return getService().getCommerceDiscountRels(
			commerceDiscountId, className);
	}

	public static List<CommerceDiscountRel> getCommerceDiscountRels(
		long commerceDiscountId, String className, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getService().getCommerceDiscountRels(
			commerceDiscountId, className, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce discount rels.
	 *
	 * @return the number of commerce discount rels
	 */
	public static int getCommerceDiscountRelsCount() {
		return getService().getCommerceDiscountRelsCount();
	}

	public static int getCommerceDiscountRelsCount(
		long commerceDiscountId, String className) {

		return getService().getCommerceDiscountRelsCount(
			commerceDiscountId, className);
	}

	public static List<CommerceDiscountRel>
		getCommercePricingClassesByCommerceDiscountId(
			long commerceDiscountId, String title, int start, int end) {

		return getService().getCommercePricingClassesByCommerceDiscountId(
			commerceDiscountId, title, start, end);
	}

	public static int getCommercePricingClassesByCommerceDiscountIdCount(
		long commerceDiscountId, String title) {

		return getService().getCommercePricingClassesByCommerceDiscountIdCount(
			commerceDiscountId, title);
	}

	public static List<CommerceDiscountRel>
		getCPDefinitionsByCommerceDiscountId(
			long commerceDiscountId, String name, String languageId, int start,
			int end) {

		return getService().getCPDefinitionsByCommerceDiscountId(
			commerceDiscountId, name, languageId, start, end);
	}

	public static int getCPDefinitionsByCommerceDiscountIdCount(
		long commerceDiscountId, String name, String languageId) {

		return getService().getCPDefinitionsByCommerceDiscountIdCount(
			commerceDiscountId, name, languageId);
	}

	public static List<CommerceDiscountRel> getCPInstancesByCommerceDiscountId(
		long commerceDiscountId, String sku, int start, int end) {

		return getService().getCPInstancesByCommerceDiscountId(
			commerceDiscountId, sku, start, end);
	}

	public static int getCPInstancesByCommerceDiscountIdCount(
		long commerceDiscountId, String sku) {

		return getService().getCPInstancesByCommerceDiscountIdCount(
			commerceDiscountId, sku);
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
	 * Updates the commerce discount rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRel the commerce discount rel
	 * @return the commerce discount rel that was updated
	 */
	public static CommerceDiscountRel updateCommerceDiscountRel(
		CommerceDiscountRel commerceDiscountRel) {

		return getService().updateCommerceDiscountRel(commerceDiscountRel);
	}

	public static CommerceDiscountRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceDiscountRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceDiscountRelLocalServiceUtil.class,
			CommerceDiscountRelLocalService.class);

}