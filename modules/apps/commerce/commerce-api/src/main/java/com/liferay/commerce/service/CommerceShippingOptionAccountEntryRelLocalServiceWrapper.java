/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceShippingOptionAccountEntryRelLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingOptionAccountEntryRelLocalService
 * @generated
 */
public class CommerceShippingOptionAccountEntryRelLocalServiceWrapper
	implements CommerceShippingOptionAccountEntryRelLocalService,
			   ServiceWrapper
				   <CommerceShippingOptionAccountEntryRelLocalService> {

	public CommerceShippingOptionAccountEntryRelLocalServiceWrapper() {
		this(null);
	}

	public CommerceShippingOptionAccountEntryRelLocalServiceWrapper(
		CommerceShippingOptionAccountEntryRelLocalService
			commerceShippingOptionAccountEntryRelLocalService) {

		_commerceShippingOptionAccountEntryRelLocalService =
			commerceShippingOptionAccountEntryRelLocalService;
	}

	/**
	 * Adds the commerce shipping option account entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingOptionAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingOptionAccountEntryRel the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel that was added
	 */
	@Override
	public com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
		addCommerceShippingOptionAccountEntryRel(
			com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
				commerceShippingOptionAccountEntryRel) {

		return _commerceShippingOptionAccountEntryRelLocalService.
			addCommerceShippingOptionAccountEntryRel(
				commerceShippingOptionAccountEntryRel);
	}

	@Override
	public com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
			addCommerceShippingOptionAccountEntryRel(
				long userId, long accountEntryId, long commerceChannelId,
				String commerceShippingMethodKey,
				String commerceShippingOptionKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingOptionAccountEntryRelLocalService.
			addCommerceShippingOptionAccountEntryRel(
				userId, accountEntryId, commerceChannelId,
				commerceShippingMethodKey, commerceShippingOptionKey);
	}

	/**
	 * Creates a new commerce shipping option account entry rel with the primary key. Does not add the commerce shipping option account entry rel to the database.
	 *
	 * @param CommerceShippingOptionAccountEntryRelId the primary key for the new commerce shipping option account entry rel
	 * @return the new commerce shipping option account entry rel
	 */
	@Override
	public com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
		createCommerceShippingOptionAccountEntryRel(
			long CommerceShippingOptionAccountEntryRelId) {

		return _commerceShippingOptionAccountEntryRelLocalService.
			createCommerceShippingOptionAccountEntryRel(
				CommerceShippingOptionAccountEntryRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingOptionAccountEntryRelLocalService.
			createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the commerce shipping option account entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingOptionAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingOptionAccountEntryRel the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel that was removed
	 */
	@Override
	public com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
		deleteCommerceShippingOptionAccountEntryRel(
			com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
				commerceShippingOptionAccountEntryRel) {

		return _commerceShippingOptionAccountEntryRelLocalService.
			deleteCommerceShippingOptionAccountEntryRel(
				commerceShippingOptionAccountEntryRel);
	}

	/**
	 * Deletes the commerce shipping option account entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingOptionAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CommerceShippingOptionAccountEntryRelId the primary key of the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel that was removed
	 * @throws PortalException if a commerce shipping option account entry rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
			deleteCommerceShippingOptionAccountEntryRel(
				long CommerceShippingOptionAccountEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingOptionAccountEntryRelLocalService.
			deleteCommerceShippingOptionAccountEntryRel(
				CommerceShippingOptionAccountEntryRelId);
	}

	@Override
	public void deleteCommerceShippingOptionAccountEntryRelsByAccountEntryId(
		long accountEntryId) {

		_commerceShippingOptionAccountEntryRelLocalService.
			deleteCommerceShippingOptionAccountEntryRelsByAccountEntryId(
				accountEntryId);
	}

	@Override
	public void deleteCommerceShippingOptionAccountEntryRelsByCommerceChannelId(
		long commerceChannelId) {

		_commerceShippingOptionAccountEntryRelLocalService.
			deleteCommerceShippingOptionAccountEntryRelsByCommerceChannelId(
				commerceChannelId);
	}

	@Override
	public void deleteCommerceShippingOptionAccountEntryRelsByCSFixedOptionKey(
		String commerceShippingFixedOptionKey) {

		_commerceShippingOptionAccountEntryRelLocalService.
			deleteCommerceShippingOptionAccountEntryRelsByCSFixedOptionKey(
				commerceShippingFixedOptionKey);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingOptionAccountEntryRelLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceShippingOptionAccountEntryRelLocalService.dslQuery(
			dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceShippingOptionAccountEntryRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceShippingOptionAccountEntryRelLocalService.
			dynamicQuery();
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

		return _commerceShippingOptionAccountEntryRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingOptionAccountEntryRelModelImpl</code>.
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

		return _commerceShippingOptionAccountEntryRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingOptionAccountEntryRelModelImpl</code>.
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

		return _commerceShippingOptionAccountEntryRelLocalService.dynamicQuery(
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

		return _commerceShippingOptionAccountEntryRelLocalService.
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

		return _commerceShippingOptionAccountEntryRelLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
		fetchCommerceShippingOptionAccountEntryRel(
			long CommerceShippingOptionAccountEntryRelId) {

		return _commerceShippingOptionAccountEntryRelLocalService.
			fetchCommerceShippingOptionAccountEntryRel(
				CommerceShippingOptionAccountEntryRelId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceShippingOptionAccountEntryRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce shipping option account entry rel with the primary key.
	 *
	 * @param CommerceShippingOptionAccountEntryRelId the primary key of the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel
	 * @throws PortalException if a commerce shipping option account entry rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
			getCommerceShippingOptionAccountEntryRel(
				long CommerceShippingOptionAccountEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingOptionAccountEntryRelLocalService.
			getCommerceShippingOptionAccountEntryRel(
				CommerceShippingOptionAccountEntryRelId);
	}

	/**
	 * Returns a range of all the commerce shipping option account entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingOptionAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipping option account entry rels
	 * @param end the upper bound of the range of commerce shipping option account entry rels (not inclusive)
	 * @return the range of commerce shipping option account entry rels
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel>
			getCommerceShippingOptionAccountEntryRels(int start, int end) {

		return _commerceShippingOptionAccountEntryRelLocalService.
			getCommerceShippingOptionAccountEntryRels(start, end);
	}

	/**
	 * Returns the number of commerce shipping option account entry rels.
	 *
	 * @return the number of commerce shipping option account entry rels
	 */
	@Override
	public int getCommerceShippingOptionAccountEntryRelsCount() {
		return _commerceShippingOptionAccountEntryRelLocalService.
			getCommerceShippingOptionAccountEntryRelsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceShippingOptionAccountEntryRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceShippingOptionAccountEntryRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingOptionAccountEntryRelLocalService.
			getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the commerce shipping option account entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingOptionAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingOptionAccountEntryRel the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel that was updated
	 */
	@Override
	public com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
		updateCommerceShippingOptionAccountEntryRel(
			com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
				commerceShippingOptionAccountEntryRel) {

		return _commerceShippingOptionAccountEntryRelLocalService.
			updateCommerceShippingOptionAccountEntryRel(
				commerceShippingOptionAccountEntryRel);
	}

	@Override
	public com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel
			updateCommerceShippingOptionAccountEntryRel(
				long commerceShippingOptionAccountEntryRelId,
				String commerceShippingMethodKey,
				String commerceShippingOptionKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingOptionAccountEntryRelLocalService.
			updateCommerceShippingOptionAccountEntryRel(
				commerceShippingOptionAccountEntryRelId,
				commerceShippingMethodKey, commerceShippingOptionKey);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceShippingOptionAccountEntryRelLocalService.
			getBasePersistence();
	}

	@Override
	public CommerceShippingOptionAccountEntryRelLocalService
		getWrappedService() {

		return _commerceShippingOptionAccountEntryRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceShippingOptionAccountEntryRelLocalService
			commerceShippingOptionAccountEntryRelLocalService) {

		_commerceShippingOptionAccountEntryRelLocalService =
			commerceShippingOptionAccountEntryRelLocalService;
	}

	private CommerceShippingOptionAccountEntryRelLocalService
		_commerceShippingOptionAccountEntryRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1398596061