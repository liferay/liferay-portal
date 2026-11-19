/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceTaxFixedRateLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceTaxFixedRateLocalService
 * @generated
 */
public class CommerceTaxFixedRateLocalServiceWrapper
	implements CommerceTaxFixedRateLocalService,
			   ServiceWrapper<CommerceTaxFixedRateLocalService> {

	public CommerceTaxFixedRateLocalServiceWrapper() {
		this(null);
	}

	public CommerceTaxFixedRateLocalServiceWrapper(
		CommerceTaxFixedRateLocalService commerceTaxFixedRateLocalService) {

		_commerceTaxFixedRateLocalService = commerceTaxFixedRateLocalService;
	}

	/**
	 * Adds the commerce tax fixed rate to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRate the commerce tax fixed rate
	 * @return the commerce tax fixed rate that was added
	 */
	@Override
	public com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
		addCommerceTaxFixedRate(
			com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
				commerceTaxFixedRate) {

		return _commerceTaxFixedRateLocalService.addCommerceTaxFixedRate(
			commerceTaxFixedRate);
	}

	@Override
	public com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
			addCommerceTaxFixedRate(
				long userId, long groupId, long commerceTaxMethodId,
				long cpTaxCategoryId, double rate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateLocalService.addCommerceTaxFixedRate(
			userId, groupId, commerceTaxMethodId, cpTaxCategoryId, rate);
	}

	/**
	 * Creates a new commerce tax fixed rate with the primary key. Does not add the commerce tax fixed rate to the database.
	 *
	 * @param commerceTaxFixedRateId the primary key for the new commerce tax fixed rate
	 * @return the new commerce tax fixed rate
	 */
	@Override
	public com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
		createCommerceTaxFixedRate(long commerceTaxFixedRateId) {

		return _commerceTaxFixedRateLocalService.createCommerceTaxFixedRate(
			commerceTaxFixedRateId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce tax fixed rate from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRate the commerce tax fixed rate
	 * @return the commerce tax fixed rate that was removed
	 */
	@Override
	public com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
		deleteCommerceTaxFixedRate(
			com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
				commerceTaxFixedRate) {

		return _commerceTaxFixedRateLocalService.deleteCommerceTaxFixedRate(
			commerceTaxFixedRate);
	}

	/**
	 * Deletes the commerce tax fixed rate with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRateId the primary key of the commerce tax fixed rate
	 * @return the commerce tax fixed rate that was removed
	 * @throws PortalException if a commerce tax fixed rate with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
			deleteCommerceTaxFixedRate(long commerceTaxFixedRateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateLocalService.deleteCommerceTaxFixedRate(
			commerceTaxFixedRateId);
	}

	@Override
	public void deleteCommerceTaxFixedRateByCommerceTaxMethodId(
		long commerceTaxMethodId) {

		_commerceTaxFixedRateLocalService.
			deleteCommerceTaxFixedRateByCommerceTaxMethodId(
				commerceTaxMethodId);
	}

	@Override
	public void deleteCommerceTaxFixedRateByCPTaxCategoryId(
		long cpTaxCategoryId) {

		_commerceTaxFixedRateLocalService.
			deleteCommerceTaxFixedRateByCPTaxCategoryId(cpTaxCategoryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceTaxFixedRateLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceTaxFixedRateLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceTaxFixedRateLocalService.dynamicQuery();
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

		return _commerceTaxFixedRateLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateModelImpl</code>.
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

		return _commerceTaxFixedRateLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateModelImpl</code>.
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

		return _commerceTaxFixedRateLocalService.dynamicQuery(
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

		return _commerceTaxFixedRateLocalService.dynamicQueryCount(
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

		return _commerceTaxFixedRateLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
		fetchCommerceTaxFixedRate(long commerceTaxFixedRateId) {

		return _commerceTaxFixedRateLocalService.fetchCommerceTaxFixedRate(
			commerceTaxFixedRateId);
	}

	@Override
	public com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
			fetchCommerceTaxFixedRate(
				long cpTaxCategoryId, long commerceTaxMethodId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateLocalService.fetchCommerceTaxFixedRate(
			cpTaxCategoryId, commerceTaxMethodId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceTaxFixedRateLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce tax fixed rate with the primary key.
	 *
	 * @param commerceTaxFixedRateId the primary key of the commerce tax fixed rate
	 * @return the commerce tax fixed rate
	 * @throws PortalException if a commerce tax fixed rate with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
			getCommerceTaxFixedRate(long commerceTaxFixedRateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateLocalService.getCommerceTaxFixedRate(
			commerceTaxFixedRateId);
	}

	/**
	 * Returns a range of all the commerce tax fixed rates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce tax fixed rates
	 * @param end the upper bound of the range of commerce tax fixed rates (not inclusive)
	 * @return the range of commerce tax fixed rates
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate>
			getCommerceTaxFixedRates(int start, int end) {

		return _commerceTaxFixedRateLocalService.getCommerceTaxFixedRates(
			start, end);
	}

	/**
	 * Returns the number of commerce tax fixed rates.
	 *
	 * @return the number of commerce tax fixed rates
	 */
	@Override
	public int getCommerceTaxFixedRatesCount() {
		return _commerceTaxFixedRateLocalService.
			getCommerceTaxFixedRatesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceTaxFixedRateLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceTaxFixedRateLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce tax fixed rate in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRate the commerce tax fixed rate
	 * @return the commerce tax fixed rate that was updated
	 */
	@Override
	public com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
		updateCommerceTaxFixedRate(
			com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
				commerceTaxFixedRate) {

		return _commerceTaxFixedRateLocalService.updateCommerceTaxFixedRate(
			commerceTaxFixedRate);
	}

	@Override
	public com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate
			updateCommerceTaxFixedRate(long commerceTaxFixedRateId, double rate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateLocalService.updateCommerceTaxFixedRate(
			commerceTaxFixedRateId, rate);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceTaxFixedRateLocalService.getBasePersistence();
	}

	@Override
	public CommerceTaxFixedRateLocalService getWrappedService() {
		return _commerceTaxFixedRateLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceTaxFixedRateLocalService commerceTaxFixedRateLocalService) {

		_commerceTaxFixedRateLocalService = commerceTaxFixedRateLocalService;
	}

	private CommerceTaxFixedRateLocalService _commerceTaxFixedRateLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-41343376