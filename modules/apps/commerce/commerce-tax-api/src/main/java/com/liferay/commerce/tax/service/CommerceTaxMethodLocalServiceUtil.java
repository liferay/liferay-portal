/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service;

import com.liferay.commerce.tax.model.CommerceTaxMethod;
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
 * Provides the local service utility for CommerceTaxMethod. This utility wraps
 * <code>com.liferay.commerce.tax.service.impl.CommerceTaxMethodLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceTaxMethodLocalService
 * @generated
 */
public class CommerceTaxMethodLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.tax.service.impl.CommerceTaxMethodLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce tax method to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxMethod the commerce tax method
	 * @return the commerce tax method that was added
	 */
	public static CommerceTaxMethod addCommerceTaxMethod(
		CommerceTaxMethod commerceTaxMethod) {

		return getService().addCommerceTaxMethod(commerceTaxMethod);
	}

	public static CommerceTaxMethod addCommerceTaxMethod(
			long userId, long groupId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, String engineKey,
			boolean percentage, boolean active)
		throws PortalException {

		return getService().addCommerceTaxMethod(
			userId, groupId, nameMap, descriptionMap, engineKey, percentage,
			active);
	}

	/**
	 * Creates a new commerce tax method with the primary key. Does not add the commerce tax method to the database.
	 *
	 * @param commerceTaxMethodId the primary key for the new commerce tax method
	 * @return the new commerce tax method
	 */
	public static CommerceTaxMethod createCommerceTaxMethod(
		long commerceTaxMethodId) {

		return getService().createCommerceTaxMethod(commerceTaxMethodId);
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
	 * Deletes the commerce tax method from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxMethod the commerce tax method
	 * @return the commerce tax method that was removed
	 */
	public static CommerceTaxMethod deleteCommerceTaxMethod(
		CommerceTaxMethod commerceTaxMethod) {

		return getService().deleteCommerceTaxMethod(commerceTaxMethod);
	}

	/**
	 * Deletes the commerce tax method with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxMethodId the primary key of the commerce tax method
	 * @return the commerce tax method that was removed
	 * @throws PortalException if a commerce tax method with the primary key could not be found
	 */
	public static CommerceTaxMethod deleteCommerceTaxMethod(
			long commerceTaxMethodId)
		throws PortalException {

		return getService().deleteCommerceTaxMethod(commerceTaxMethodId);
	}

	public static void deleteCommerceTaxMethods(long groupId)
		throws PortalException {

		getService().deleteCommerceTaxMethods(groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxMethodModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxMethodModelImpl</code>.
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

	public static CommerceTaxMethod fetchCommerceTaxMethod(
		long commerceTaxMethodId) {

		return getService().fetchCommerceTaxMethod(commerceTaxMethodId);
	}

	public static CommerceTaxMethod fetchCommerceTaxMethod(
		long groupId, String engineKey) {

		return getService().fetchCommerceTaxMethod(groupId, engineKey);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce tax method with the primary key.
	 *
	 * @param commerceTaxMethodId the primary key of the commerce tax method
	 * @return the commerce tax method
	 * @throws PortalException if a commerce tax method with the primary key could not be found
	 */
	public static CommerceTaxMethod getCommerceTaxMethod(
			long commerceTaxMethodId)
		throws PortalException {

		return getService().getCommerceTaxMethod(commerceTaxMethodId);
	}

	/**
	 * Returns a range of all the commerce tax methods.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxMethodModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce tax methods
	 * @param end the upper bound of the range of commerce tax methods (not inclusive)
	 * @return the range of commerce tax methods
	 */
	public static List<CommerceTaxMethod> getCommerceTaxMethods(
		int start, int end) {

		return getService().getCommerceTaxMethods(start, end);
	}

	public static List<CommerceTaxMethod> getCommerceTaxMethods(
		long groupId, boolean active) {

		return getService().getCommerceTaxMethods(groupId, active);
	}

	/**
	 * Returns the number of commerce tax methods.
	 *
	 * @return the number of commerce tax methods
	 */
	public static int getCommerceTaxMethodsCount() {
		return getService().getCommerceTaxMethodsCount();
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

	public static CommerceTaxMethod setActive(
			long commerceTaxMethodId, boolean active)
		throws PortalException {

		return getService().setActive(commerceTaxMethodId, active);
	}

	/**
	 * Updates the commerce tax method in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxMethod the commerce tax method
	 * @return the commerce tax method that was updated
	 */
	public static CommerceTaxMethod updateCommerceTaxMethod(
		CommerceTaxMethod commerceTaxMethod) {

		return getService().updateCommerceTaxMethod(commerceTaxMethod);
	}

	public static CommerceTaxMethod updateCommerceTaxMethod(
			long commerceTaxMethodId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean percentage,
			boolean active)
		throws PortalException {

		return getService().updateCommerceTaxMethod(
			commerceTaxMethodId, nameMap, descriptionMap, percentage, active);
	}

	public static CommerceTaxMethodLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceTaxMethodLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceTaxMethodLocalServiceUtil.class,
			CommerceTaxMethodLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1494639020