/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ProductionReadinessIgnore. This utility wraps
 * <code>com.liferay.production.readiness.ignore.service.impl.ProductionReadinessIgnoreLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ProductionReadinessIgnoreLocalService
 * @generated
 */
public class ProductionReadinessIgnoreLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.production.readiness.ignore.service.impl.ProductionReadinessIgnoreLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ProductionReadinessIgnore addProductionReadinessIgnore(
			long userId, long companyId, String ruleKey, String reason)
		throws PortalException {

		return getService().addProductionReadinessIgnore(
			userId, companyId, ruleKey, reason);
	}

	/**
	 * Adds the production readiness ignore to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ProductionReadinessIgnoreLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param productionReadinessIgnore the production readiness ignore
	 * @return the production readiness ignore that was added
	 */
	public static ProductionReadinessIgnore addProductionReadinessIgnore(
		ProductionReadinessIgnore productionReadinessIgnore) {

		return getService().addProductionReadinessIgnore(
			productionReadinessIgnore);
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
	 * Creates a new production readiness ignore with the primary key. Does not add the production readiness ignore to the database.
	 *
	 * @param productionReadinessIgnoreId the primary key for the new production readiness ignore
	 * @return the new production readiness ignore
	 */
	public static ProductionReadinessIgnore createProductionReadinessIgnore(
		long productionReadinessIgnoreId) {

		return getService().createProductionReadinessIgnore(
			productionReadinessIgnoreId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the production readiness ignore with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ProductionReadinessIgnoreLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore that was removed
	 * @throws PortalException if a production readiness ignore with the primary key could not be found
	 */
	public static ProductionReadinessIgnore deleteProductionReadinessIgnore(
			long productionReadinessIgnoreId)
		throws PortalException {

		return getService().deleteProductionReadinessIgnore(
			productionReadinessIgnoreId);
	}

	public static void deleteProductionReadinessIgnore(
			long companyId, String ruleKey)
		throws PortalException {

		getService().deleteProductionReadinessIgnore(companyId, ruleKey);
	}

	/**
	 * Deletes the production readiness ignore from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ProductionReadinessIgnoreLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param productionReadinessIgnore the production readiness ignore
	 * @return the production readiness ignore that was removed
	 */
	public static ProductionReadinessIgnore deleteProductionReadinessIgnore(
		ProductionReadinessIgnore productionReadinessIgnore) {

		return getService().deleteProductionReadinessIgnore(
			productionReadinessIgnore);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreModelImpl</code>.
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

	public static ProductionReadinessIgnore fetchProductionReadinessIgnore(
		long productionReadinessIgnoreId) {

		return getService().fetchProductionReadinessIgnore(
			productionReadinessIgnoreId);
	}

	public static ProductionReadinessIgnore fetchProductionReadinessIgnore(
		long companyId, String ruleKey) {

		return getService().fetchProductionReadinessIgnore(companyId, ruleKey);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	 * Returns the production readiness ignore with the primary key.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore
	 * @throws PortalException if a production readiness ignore with the primary key could not be found
	 */
	public static ProductionReadinessIgnore getProductionReadinessIgnore(
			long productionReadinessIgnoreId)
		throws PortalException {

		return getService().getProductionReadinessIgnore(
			productionReadinessIgnoreId);
	}

	/**
	 * Returns a range of all the production readiness ignores.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @return the range of production readiness ignores
	 */
	public static List<ProductionReadinessIgnore> getProductionReadinessIgnores(
		int start, int end) {

		return getService().getProductionReadinessIgnores(start, end);
	}

	public static List<ProductionReadinessIgnore> getProductionReadinessIgnores(
		long companyId) {

		return getService().getProductionReadinessIgnores(companyId);
	}

	/**
	 * Returns the number of production readiness ignores.
	 *
	 * @return the number of production readiness ignores
	 */
	public static int getProductionReadinessIgnoresCount() {
		return getService().getProductionReadinessIgnoresCount();
	}

	/**
	 * Updates the production readiness ignore in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ProductionReadinessIgnoreLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param productionReadinessIgnore the production readiness ignore
	 * @return the production readiness ignore that was updated
	 */
	public static ProductionReadinessIgnore updateProductionReadinessIgnore(
		ProductionReadinessIgnore productionReadinessIgnore) {

		return getService().updateProductionReadinessIgnore(
			productionReadinessIgnore);
	}

	public static ProductionReadinessIgnoreLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ProductionReadinessIgnoreLocalService>
		_serviceSnapshot = new Snapshot<>(
			ProductionReadinessIgnoreLocalServiceUtil.class,
			ProductionReadinessIgnoreLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1915602434