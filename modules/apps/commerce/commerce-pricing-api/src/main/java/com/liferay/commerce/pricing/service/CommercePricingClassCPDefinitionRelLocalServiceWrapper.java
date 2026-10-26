/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommercePricingClassCPDefinitionRelLocalService}.
 *
 * @author Riccardo Alberti
 * @see CommercePricingClassCPDefinitionRelLocalService
 * @generated
 */
public class CommercePricingClassCPDefinitionRelLocalServiceWrapper
	implements CommercePricingClassCPDefinitionRelLocalService,
			   ServiceWrapper<CommercePricingClassCPDefinitionRelLocalService> {

	public CommercePricingClassCPDefinitionRelLocalServiceWrapper() {
		this(null);
	}

	public CommercePricingClassCPDefinitionRelLocalServiceWrapper(
		CommercePricingClassCPDefinitionRelLocalService
			commercePricingClassCPDefinitionRelLocalService) {

		_commercePricingClassCPDefinitionRelLocalService =
			commercePricingClassCPDefinitionRelLocalService;
	}

	/**
	 * Adds the commerce pricing class cp definition rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassCPDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePricingClassCPDefinitionRel the commerce pricing class cp definition rel
	 * @return the commerce pricing class cp definition rel that was added
	 */
	@Override
	public CommercePricingClassCPDefinitionRel
		addCommercePricingClassCPDefinitionRel(
			CommercePricingClassCPDefinitionRel
				commercePricingClassCPDefinitionRel) {

		return _commercePricingClassCPDefinitionRelLocalService.
			addCommercePricingClassCPDefinitionRel(
				commercePricingClassCPDefinitionRel);
	}

	@Override
	public CommercePricingClassCPDefinitionRel
			addCommercePricingClassCPDefinitionRel(
				long commercePricingClassId, long cpDefinitionId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelLocalService.
			addCommercePricingClassCPDefinitionRel(
				commercePricingClassId, cpDefinitionId, serviceContext);
	}

	/**
	 * Creates a new commerce pricing class cp definition rel with the primary key. Does not add the commerce pricing class cp definition rel to the database.
	 *
	 * @param CommercePricingClassCPDefinitionRelId the primary key for the new commerce pricing class cp definition rel
	 * @return the new commerce pricing class cp definition rel
	 */
	@Override
	public CommercePricingClassCPDefinitionRel
		createCommercePricingClassCPDefinitionRel(
			long CommercePricingClassCPDefinitionRelId) {

		return _commercePricingClassCPDefinitionRelLocalService.
			createCommercePricingClassCPDefinitionRel(
				CommercePricingClassCPDefinitionRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelLocalService.
			createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the commerce pricing class cp definition rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassCPDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePricingClassCPDefinitionRel the commerce pricing class cp definition rel
	 * @return the commerce pricing class cp definition rel that was removed
	 * @throws PortalException
	 */
	@Override
	public CommercePricingClassCPDefinitionRel
			deleteCommercePricingClassCPDefinitionRel(
				CommercePricingClassCPDefinitionRel
					commercePricingClassCPDefinitionRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelLocalService.
			deleteCommercePricingClassCPDefinitionRel(
				commercePricingClassCPDefinitionRel);
	}

	/**
	 * Deletes the commerce pricing class cp definition rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassCPDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CommercePricingClassCPDefinitionRelId the primary key of the commerce pricing class cp definition rel
	 * @return the commerce pricing class cp definition rel that was removed
	 * @throws PortalException if a commerce pricing class cp definition rel with the primary key could not be found
	 */
	@Override
	public CommercePricingClassCPDefinitionRel
			deleteCommercePricingClassCPDefinitionRel(
				long CommercePricingClassCPDefinitionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelLocalService.
			deleteCommercePricingClassCPDefinitionRel(
				CommercePricingClassCPDefinitionRelId);
	}

	@Override
	public void deleteCommercePricingClassCPDefinitionRels(
			long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePricingClassCPDefinitionRelLocalService.
			deleteCommercePricingClassCPDefinitionRels(commercePricingClassId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePricingClassCPDefinitionRelLocalService.dslQuery(
			dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePricingClassCPDefinitionRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePricingClassCPDefinitionRelLocalService.dynamicQuery();
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

		return _commercePricingClassCPDefinitionRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePricingClassCPDefinitionRelModelImpl</code>.
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

		return _commercePricingClassCPDefinitionRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePricingClassCPDefinitionRelModelImpl</code>.
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

		return _commercePricingClassCPDefinitionRelLocalService.dynamicQuery(
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

		return _commercePricingClassCPDefinitionRelLocalService.
			dynamicQueryCount(dynamicQuery);
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

		return _commercePricingClassCPDefinitionRelLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public CommercePricingClassCPDefinitionRel
		fetchCommercePricingClassCPDefinitionRel(
			long CommercePricingClassCPDefinitionRelId) {

		return _commercePricingClassCPDefinitionRelLocalService.
			fetchCommercePricingClassCPDefinitionRel(
				CommercePricingClassCPDefinitionRelId);
	}

	@Override
	public CommercePricingClassCPDefinitionRel
		fetchCommercePricingClassCPDefinitionRel(
			long commercePricingClassId, long cpDefinitionId) {

		return _commercePricingClassCPDefinitionRelLocalService.
			fetchCommercePricingClassCPDefinitionRel(
				commercePricingClassId, cpDefinitionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePricingClassCPDefinitionRelLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List<CommercePricingClassCPDefinitionRel>
		getCommercePricingClassByCPDefinitionId(long cpDefinitionId) {

		return _commercePricingClassCPDefinitionRelLocalService.
			getCommercePricingClassByCPDefinitionId(cpDefinitionId);
	}

	/**
	 * Returns the commerce pricing class cp definition rel with the primary key.
	 *
	 * @param CommercePricingClassCPDefinitionRelId the primary key of the commerce pricing class cp definition rel
	 * @return the commerce pricing class cp definition rel
	 * @throws PortalException if a commerce pricing class cp definition rel with the primary key could not be found
	 */
	@Override
	public CommercePricingClassCPDefinitionRel
			getCommercePricingClassCPDefinitionRel(
				long CommercePricingClassCPDefinitionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelLocalService.
			getCommercePricingClassCPDefinitionRel(
				CommercePricingClassCPDefinitionRelId);
	}

	/**
	 * Returns a range of all the commerce pricing class cp definition rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePricingClassCPDefinitionRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce pricing class cp definition rels
	 * @param end the upper bound of the range of commerce pricing class cp definition rels (not inclusive)
	 * @return the range of commerce pricing class cp definition rels
	 */
	@Override
	public java.util.List<CommercePricingClassCPDefinitionRel>
		getCommercePricingClassCPDefinitionRels(int start, int end) {

		return _commercePricingClassCPDefinitionRelLocalService.
			getCommercePricingClassCPDefinitionRels(start, end);
	}

	@Override
	public java.util.List<CommercePricingClassCPDefinitionRel>
		getCommercePricingClassCPDefinitionRels(long commercePricingClassId) {

		return _commercePricingClassCPDefinitionRelLocalService.
			getCommercePricingClassCPDefinitionRels(commercePricingClassId);
	}

	@Override
	public java.util.List<CommercePricingClassCPDefinitionRel>
		getCommercePricingClassCPDefinitionRels(
			long commercePricingClassId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommercePricingClassCPDefinitionRel> orderByComparator) {

		return _commercePricingClassCPDefinitionRelLocalService.
			getCommercePricingClassCPDefinitionRels(
				commercePricingClassId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce pricing class cp definition rels.
	 *
	 * @return the number of commerce pricing class cp definition rels
	 */
	@Override
	public int getCommercePricingClassCPDefinitionRelsCount() {
		return _commercePricingClassCPDefinitionRelLocalService.
			getCommercePricingClassCPDefinitionRelsCount();
	}

	@Override
	public int getCommercePricingClassCPDefinitionRelsCount(
		long commercePricingClassId) {

		return _commercePricingClassCPDefinitionRelLocalService.
			getCommercePricingClassCPDefinitionRelsCount(
				commercePricingClassId);
	}

	@Override
	public long[] getCPDefinitionIds(long commercePricingClassId) {
		return _commercePricingClassCPDefinitionRelLocalService.
			getCPDefinitionIds(commercePricingClassId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePricingClassCPDefinitionRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePricingClassCPDefinitionRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelLocalService.
			getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the commerce pricing class cp definition rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassCPDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePricingClassCPDefinitionRel the commerce pricing class cp definition rel
	 * @return the commerce pricing class cp definition rel that was updated
	 */
	@Override
	public CommercePricingClassCPDefinitionRel
		updateCommercePricingClassCPDefinitionRel(
			CommercePricingClassCPDefinitionRel
				commercePricingClassCPDefinitionRel) {

		return _commercePricingClassCPDefinitionRelLocalService.
			updateCommercePricingClassCPDefinitionRel(
				commercePricingClassCPDefinitionRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePricingClassCPDefinitionRelLocalService.
			getBasePersistence();
	}

	@Override
	public CTPersistence<CommercePricingClassCPDefinitionRel>
		getCTPersistence() {

		return _commercePricingClassCPDefinitionRelLocalService.
			getCTPersistence();
	}

	@Override
	public Class<CommercePricingClassCPDefinitionRel> getModelClass() {
		return _commercePricingClassCPDefinitionRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction
				<CTPersistence<CommercePricingClassCPDefinitionRel>, R, E>
					updateUnsafeFunction)
		throws E {

		return _commercePricingClassCPDefinitionRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public CommercePricingClassCPDefinitionRelLocalService getWrappedService() {
		return _commercePricingClassCPDefinitionRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePricingClassCPDefinitionRelLocalService
			commercePricingClassCPDefinitionRelLocalService) {

		_commercePricingClassCPDefinitionRelLocalService =
			commercePricingClassCPDefinitionRelLocalService;
	}

	private CommercePricingClassCPDefinitionRelLocalService
		_commercePricingClassCPDefinitionRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:239822637