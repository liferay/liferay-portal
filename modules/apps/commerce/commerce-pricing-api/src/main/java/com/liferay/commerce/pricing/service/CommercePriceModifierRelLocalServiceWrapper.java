/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommercePriceModifierRelLocalService}.
 *
 * @author Riccardo Alberti
 * @see CommercePriceModifierRelLocalService
 * @generated
 */
public class CommercePriceModifierRelLocalServiceWrapper
	implements CommercePriceModifierRelLocalService,
			   ServiceWrapper<CommercePriceModifierRelLocalService> {

	public CommercePriceModifierRelLocalServiceWrapper() {
		this(null);
	}

	public CommercePriceModifierRelLocalServiceWrapper(
		CommercePriceModifierRelLocalService
			commercePriceModifierRelLocalService) {

		_commercePriceModifierRelLocalService =
			commercePriceModifierRelLocalService;
	}

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
	@Override
	public CommercePriceModifierRel addCommercePriceModifierRel(
		CommercePriceModifierRel commercePriceModifierRel) {

		return _commercePriceModifierRelLocalService.
			addCommercePriceModifierRel(commercePriceModifierRel);
	}

	@Override
	public CommercePriceModifierRel addCommercePriceModifierRel(
			long commercePriceModifierId, String className, long classPK,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelLocalService.
			addCommercePriceModifierRel(
				commercePriceModifierId, className, classPK, serviceContext);
	}

	/**
	 * Creates a new commerce price modifier rel with the primary key. Does not add the commerce price modifier rel to the database.
	 *
	 * @param commercePriceModifierRelId the primary key for the new commerce price modifier rel
	 * @return the new commerce price modifier rel
	 */
	@Override
	public CommercePriceModifierRel createCommercePriceModifierRel(
		long commercePriceModifierRelId) {

		return _commercePriceModifierRelLocalService.
			createCommercePriceModifierRel(commercePriceModifierRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public CommercePriceModifierRel deleteCommercePriceModifierRel(
			CommercePriceModifierRel commercePriceModifierRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelLocalService.
			deleteCommercePriceModifierRel(commercePriceModifierRel);
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
	@Override
	public CommercePriceModifierRel deleteCommercePriceModifierRel(
			long commercePriceModifierRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelLocalService.
			deleteCommercePriceModifierRel(commercePriceModifierRelId);
	}

	@Override
	public void deleteCommercePriceModifierRels(long commercePriceModifierId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceModifierRelLocalService.deleteCommercePriceModifierRels(
			commercePriceModifierId);
	}

	@Override
	public void deleteCommercePriceModifierRels(String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceModifierRelLocalService.deleteCommercePriceModifierRels(
			className, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePriceModifierRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePriceModifierRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePriceModifierRelLocalService.dynamicQuery();
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

		return _commercePriceModifierRelLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commercePriceModifierRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commercePriceModifierRelLocalService.dynamicQuery(
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

		return _commercePriceModifierRelLocalService.dynamicQueryCount(
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

		return _commercePriceModifierRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommercePriceModifierRel fetchCommercePriceModifierRel(
		long commercePriceModifierRelId) {

		return _commercePriceModifierRelLocalService.
			fetchCommercePriceModifierRel(commercePriceModifierRelId);
	}

	@Override
	public CommercePriceModifierRel fetchCommercePriceModifierRel(
		long commercePriceModifierId, String className, long classPK) {

		return _commercePriceModifierRelLocalService.
			fetchCommercePriceModifierRel(
				commercePriceModifierId, className, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePriceModifierRelLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public long[] getClassPKs(long commercePriceModifierId, String className) {
		return _commercePriceModifierRelLocalService.getClassPKs(
			commercePriceModifierId, className);
	}

	/**
	 * Returns the commerce price modifier rel with the primary key.
	 *
	 * @param commercePriceModifierRelId the primary key of the commerce price modifier rel
	 * @return the commerce price modifier rel
	 * @throws PortalException if a commerce price modifier rel with the primary key could not be found
	 */
	@Override
	public CommercePriceModifierRel getCommercePriceModifierRel(
			long commercePriceModifierRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelLocalService.
			getCommercePriceModifierRel(commercePriceModifierRelId);
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
	@Override
	public java.util.List<CommercePriceModifierRel>
		getCommercePriceModifierRels(int start, int end) {

		return _commercePriceModifierRelLocalService.
			getCommercePriceModifierRels(start, end);
	}

	@Override
	public java.util.List<CommercePriceModifierRel>
		getCommercePriceModifierRels(
			long commercePriceModifierId, String className) {

		return _commercePriceModifierRelLocalService.
			getCommercePriceModifierRels(commercePriceModifierId, className);
	}

	@Override
	public java.util.List<CommercePriceModifierRel>
		getCommercePriceModifierRels(
			long commercePriceModifierId, String className, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommercePriceModifierRel> orderByComparator) {

		return _commercePriceModifierRelLocalService.
			getCommercePriceModifierRels(
				commercePriceModifierId, className, start, end,
				orderByComparator);
	}

	/**
	 * Returns the number of commerce price modifier rels.
	 *
	 * @return the number of commerce price modifier rels
	 */
	@Override
	public int getCommercePriceModifierRelsCount() {
		return _commercePriceModifierRelLocalService.
			getCommercePriceModifierRelsCount();
	}

	@Override
	public int getCommercePriceModifierRelsCount(
		long commercePriceModifierId, String className) {

		return _commercePriceModifierRelLocalService.
			getCommercePriceModifierRelsCount(
				commercePriceModifierId, className);
	}

	@Override
	public java.util.List<CommercePriceModifierRel>
		getCommercePriceModifiersRels(String className, long classPK) {

		return _commercePriceModifierRelLocalService.
			getCommercePriceModifiersRels(className, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePriceModifierRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceModifierRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public CommercePriceModifierRel updateCommercePriceModifierRel(
		CommercePriceModifierRel commercePriceModifierRel) {

		return _commercePriceModifierRelLocalService.
			updateCommercePriceModifierRel(commercePriceModifierRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePriceModifierRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommercePriceModifierRel> getCTPersistence() {
		return _commercePriceModifierRelLocalService.getCTPersistence();
	}

	@Override
	public Class<CommercePriceModifierRel> getModelClass() {
		return _commercePriceModifierRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommercePriceModifierRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commercePriceModifierRelLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CommercePriceModifierRelLocalService getWrappedService() {
		return _commercePriceModifierRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePriceModifierRelLocalService
			commercePriceModifierRelLocalService) {

		_commercePriceModifierRelLocalService =
			commercePriceModifierRelLocalService;
	}

	private CommercePriceModifierRelLocalService
		_commercePriceModifierRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:2435058