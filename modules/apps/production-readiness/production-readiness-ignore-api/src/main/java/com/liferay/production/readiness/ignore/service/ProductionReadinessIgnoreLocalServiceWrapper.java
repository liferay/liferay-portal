/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ProductionReadinessIgnoreLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ProductionReadinessIgnoreLocalService
 * @generated
 */
public class ProductionReadinessIgnoreLocalServiceWrapper
	implements ProductionReadinessIgnoreLocalService,
			   ServiceWrapper<ProductionReadinessIgnoreLocalService> {

	public ProductionReadinessIgnoreLocalServiceWrapper() {
		this(null);
	}

	public ProductionReadinessIgnoreLocalServiceWrapper(
		ProductionReadinessIgnoreLocalService
			productionReadinessIgnoreLocalService) {

		_productionReadinessIgnoreLocalService =
			productionReadinessIgnoreLocalService;
	}

	@Override
	public
		com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore
				addProductionReadinessIgnore(
					long userId, long companyId, String ruleKey, String reason)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _productionReadinessIgnoreLocalService.
			addProductionReadinessIgnore(userId, companyId, ruleKey, reason);
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
	@Override
	public
		com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore
			addProductionReadinessIgnore(
				com.liferay.production.readiness.ignore.model.
					ProductionReadinessIgnore productionReadinessIgnore) {

		return _productionReadinessIgnoreLocalService.
			addProductionReadinessIgnore(productionReadinessIgnore);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _productionReadinessIgnoreLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new production readiness ignore with the primary key. Does not add the production readiness ignore to the database.
	 *
	 * @param productionReadinessIgnoreId the primary key for the new production readiness ignore
	 * @return the new production readiness ignore
	 */
	@Override
	public
		com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore
			createProductionReadinessIgnore(long productionReadinessIgnoreId) {

		return _productionReadinessIgnoreLocalService.
			createProductionReadinessIgnore(productionReadinessIgnoreId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _productionReadinessIgnoreLocalService.deletePersistedModel(
			persistedModel);
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
	@Override
	public
		com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore
				deleteProductionReadinessIgnore(
					long productionReadinessIgnoreId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _productionReadinessIgnoreLocalService.
			deleteProductionReadinessIgnore(productionReadinessIgnoreId);
	}

	@Override
	public void deleteProductionReadinessIgnore(long companyId, String ruleKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_productionReadinessIgnoreLocalService.deleteProductionReadinessIgnore(
			companyId, ruleKey);
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
	@Override
	public
		com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore
			deleteProductionReadinessIgnore(
				com.liferay.production.readiness.ignore.model.
					ProductionReadinessIgnore productionReadinessIgnore) {

		return _productionReadinessIgnoreLocalService.
			deleteProductionReadinessIgnore(productionReadinessIgnore);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _productionReadinessIgnoreLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _productionReadinessIgnoreLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _productionReadinessIgnoreLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _productionReadinessIgnoreLocalService.dynamicQuery(
			dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _productionReadinessIgnoreLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _productionReadinessIgnoreLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _productionReadinessIgnoreLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _productionReadinessIgnoreLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore
			fetchProductionReadinessIgnore(long productionReadinessIgnoreId) {

		return _productionReadinessIgnoreLocalService.
			fetchProductionReadinessIgnore(productionReadinessIgnoreId);
	}

	@Override
	public
		com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore
			fetchProductionReadinessIgnore(long companyId, String ruleKey) {

		return _productionReadinessIgnoreLocalService.
			fetchProductionReadinessIgnore(companyId, ruleKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _productionReadinessIgnoreLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _productionReadinessIgnoreLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _productionReadinessIgnoreLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _productionReadinessIgnoreLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns the production readiness ignore with the primary key.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore
	 * @throws PortalException if a production readiness ignore with the primary key could not be found
	 */
	@Override
	public
		com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore
				getProductionReadinessIgnore(long productionReadinessIgnoreId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _productionReadinessIgnoreLocalService.
			getProductionReadinessIgnore(productionReadinessIgnoreId);
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
	@Override
	public java.util.List
		<com.liferay.production.readiness.ignore.model.
			ProductionReadinessIgnore> getProductionReadinessIgnores(
				int start, int end) {

		return _productionReadinessIgnoreLocalService.
			getProductionReadinessIgnores(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.production.readiness.ignore.model.
			ProductionReadinessIgnore> getProductionReadinessIgnores(
				long companyId) {

		return _productionReadinessIgnoreLocalService.
			getProductionReadinessIgnores(companyId);
	}

	/**
	 * Returns the number of production readiness ignores.
	 *
	 * @return the number of production readiness ignores
	 */
	@Override
	public int getProductionReadinessIgnoresCount() {
		return _productionReadinessIgnoreLocalService.
			getProductionReadinessIgnoresCount();
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
	@Override
	public
		com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore
			updateProductionReadinessIgnore(
				com.liferay.production.readiness.ignore.model.
					ProductionReadinessIgnore productionReadinessIgnore) {

		return _productionReadinessIgnoreLocalService.
			updateProductionReadinessIgnore(productionReadinessIgnore);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _productionReadinessIgnoreLocalService.getBasePersistence();
	}

	@Override
	public ProductionReadinessIgnoreLocalService getWrappedService() {
		return _productionReadinessIgnoreLocalService;
	}

	@Override
	public void setWrappedService(
		ProductionReadinessIgnoreLocalService
			productionReadinessIgnoreLocalService) {

		_productionReadinessIgnoreLocalService =
			productionReadinessIgnoreLocalService;
	}

	private ProductionReadinessIgnoreLocalService
		_productionReadinessIgnoreLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-754688194