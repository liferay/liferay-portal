/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommerceChannelAccountEntryRelLocalService}.
 *
 * @author Marco Leo
 * @see CommerceChannelAccountEntryRelLocalService
 * @generated
 */
public class CommerceChannelAccountEntryRelLocalServiceWrapper
	implements CommerceChannelAccountEntryRelLocalService,
			   ServiceWrapper<CommerceChannelAccountEntryRelLocalService> {

	public CommerceChannelAccountEntryRelLocalServiceWrapper() {
		this(null);
	}

	public CommerceChannelAccountEntryRelLocalServiceWrapper(
		CommerceChannelAccountEntryRelLocalService
			commerceChannelAccountEntryRelLocalService) {

		_commerceChannelAccountEntryRelLocalService =
			commerceChannelAccountEntryRelLocalService;
	}

	/**
	 * Adds the commerce channel account entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRel the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was added
	 */
	@Override
	public CommerceChannelAccountEntryRel addCommerceChannelAccountEntryRel(
		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel) {

		return _commerceChannelAccountEntryRelLocalService.
			addCommerceChannelAccountEntryRel(commerceChannelAccountEntryRel);
	}

	@Override
	public CommerceChannelAccountEntryRel addCommerceChannelAccountEntryRel(
			long userId, long accountEntryId, String className, long classPK,
			long commerceChannelId, boolean overrideEligibility,
			double priority, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelAccountEntryRelLocalService.
			addCommerceChannelAccountEntryRel(
				userId, accountEntryId, className, classPK, commerceChannelId,
				overrideEligibility, priority, type);
	}

	/**
	 * Creates a new commerce channel account entry rel with the primary key. Does not add the commerce channel account entry rel to the database.
	 *
	 * @param commerceChannelAccountEntryRelId the primary key for the new commerce channel account entry rel
	 * @return the new commerce channel account entry rel
	 */
	@Override
	public CommerceChannelAccountEntryRel createCommerceChannelAccountEntryRel(
		long commerceChannelAccountEntryRelId) {

		return _commerceChannelAccountEntryRelLocalService.
			createCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelAccountEntryRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce channel account entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRel the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was removed
	 * @throws PortalException
	 */
	@Override
	public CommerceChannelAccountEntryRel deleteCommerceChannelAccountEntryRel(
			CommerceChannelAccountEntryRel commerceChannelAccountEntryRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelAccountEntryRelLocalService.
			deleteCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRel);
	}

	/**
	 * Deletes the commerce channel account entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRelId the primary key of the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was removed
	 * @throws PortalException if a commerce channel account entry rel with the primary key could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel deleteCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelAccountEntryRelLocalService.
			deleteCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRelId);
	}

	@Override
	public void deleteCommerceChannelAccountEntryRels(
			String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceChannelAccountEntryRelLocalService.
			deleteCommerceChannelAccountEntryRels(className, classPK);
	}

	@Override
	public void deleteCommerceChannelAccountEntryRelsByAccountEntryId(
			long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceChannelAccountEntryRelLocalService.
			deleteCommerceChannelAccountEntryRelsByAccountEntryId(
				accountEntryId);
	}

	@Override
	public void deleteCommerceChannelAccountEntryRelsByCommerceChannelId(
			long commerceChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceChannelAccountEntryRelLocalService.
			deleteCommerceChannelAccountEntryRelsByCommerceChannelId(
				commerceChannelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelAccountEntryRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceChannelAccountEntryRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceChannelAccountEntryRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceChannelAccountEntryRelLocalService.dynamicQuery();
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

		return _commerceChannelAccountEntryRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelAccountEntryRelModelImpl</code>.
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

		return _commerceChannelAccountEntryRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelAccountEntryRelModelImpl</code>.
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

		return _commerceChannelAccountEntryRelLocalService.dynamicQuery(
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

		return _commerceChannelAccountEntryRelLocalService.dynamicQueryCount(
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

		return _commerceChannelAccountEntryRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel(
		long commerceChannelAccountEntryRelId) {

		return _commerceChannelAccountEntryRelLocalService.
			fetchCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRelId);
	}

	@Override
	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel(
		long accountEntryId, long commerceChannelId, int type) {

		return _commerceChannelAccountEntryRelLocalService.
			fetchCommerceChannelAccountEntryRel(
				accountEntryId, commerceChannelId, type);
	}

	@Override
	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel(
		long accountEntryId, String className, long classPK,
		long commerceChannelId, int type) {

		return _commerceChannelAccountEntryRelLocalService.
			fetchCommerceChannelAccountEntryRel(
				accountEntryId, className, classPK, commerceChannelId, type);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceChannelAccountEntryRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce channel account entry rel with the primary key.
	 *
	 * @param commerceChannelAccountEntryRelId the primary key of the commerce channel account entry rel
	 * @return the commerce channel account entry rel
	 * @throws PortalException if a commerce channel account entry rel with the primary key could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel getCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelAccountEntryRelLocalService.
			getCommerceChannelAccountEntryRel(commerceChannelAccountEntryRelId);
	}

	/**
	 * Returns a range of all the commerce channel account entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce channel account entry rels
	 * @param end the upper bound of the range of commerce channel account entry rels (not inclusive)
	 * @return the range of commerce channel account entry rels
	 */
	@Override
	public java.util.List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(int start, int end) {

		return _commerceChannelAccountEntryRelLocalService.
			getCommerceChannelAccountEntryRels(start, end);
	}

	@Override
	public java.util.List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			long accountEntryId, int type, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceChannelAccountEntryRel> orderByComparator) {

		return _commerceChannelAccountEntryRelLocalService.
			getCommerceChannelAccountEntryRels(
				accountEntryId, type, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			long commerceChannelId, String name, int type, int start, int end) {

		return _commerceChannelAccountEntryRelLocalService.
			getCommerceChannelAccountEntryRels(
				commerceChannelId, name, type, start, end);
	}

	/**
	 * Returns the number of commerce channel account entry rels.
	 *
	 * @return the number of commerce channel account entry rels
	 */
	@Override
	public int getCommerceChannelAccountEntryRelsCount() {
		return _commerceChannelAccountEntryRelLocalService.
			getCommerceChannelAccountEntryRelsCount();
	}

	@Override
	public int getCommerceChannelAccountEntryRelsCount(
		long commerceChannelId, String name, int type) {

		return _commerceChannelAccountEntryRelLocalService.
			getCommerceChannelAccountEntryRelsCount(
				commerceChannelId, name, type);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceChannelAccountEntryRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceChannelAccountEntryRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelAccountEntryRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce channel account entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRel the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was updated
	 */
	@Override
	public CommerceChannelAccountEntryRel updateCommerceChannelAccountEntryRel(
		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel) {

		return _commerceChannelAccountEntryRelLocalService.
			updateCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRel);
	}

	@Override
	public CommerceChannelAccountEntryRel updateCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId, long commerceChannelId,
			long classPK, boolean overrideEligibility, double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelAccountEntryRelLocalService.
			updateCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRelId, commerceChannelId, classPK,
				overrideEligibility, priority);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceChannelAccountEntryRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommerceChannelAccountEntryRel> getCTPersistence() {
		return _commerceChannelAccountEntryRelLocalService.getCTPersistence();
	}

	@Override
	public Class<CommerceChannelAccountEntryRel> getModelClass() {
		return _commerceChannelAccountEntryRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommerceChannelAccountEntryRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commerceChannelAccountEntryRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public CommerceChannelAccountEntryRelLocalService getWrappedService() {
		return _commerceChannelAccountEntryRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceChannelAccountEntryRelLocalService
			commerceChannelAccountEntryRelLocalService) {

		_commerceChannelAccountEntryRelLocalService =
			commerceChannelAccountEntryRelLocalService;
	}

	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1854679817