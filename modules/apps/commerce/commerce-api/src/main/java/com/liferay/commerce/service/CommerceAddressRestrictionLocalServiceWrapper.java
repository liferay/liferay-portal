/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceAddressRestrictionLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAddressRestrictionLocalService
 * @generated
 */
public class CommerceAddressRestrictionLocalServiceWrapper
	implements CommerceAddressRestrictionLocalService,
			   ServiceWrapper<CommerceAddressRestrictionLocalService> {

	public CommerceAddressRestrictionLocalServiceWrapper() {
		this(null);
	}

	public CommerceAddressRestrictionLocalServiceWrapper(
		CommerceAddressRestrictionLocalService
			commerceAddressRestrictionLocalService) {

		_commerceAddressRestrictionLocalService =
			commerceAddressRestrictionLocalService;
	}

	/**
	 * Adds the commerce address restriction to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAddressRestrictionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAddressRestriction the commerce address restriction
	 * @return the commerce address restriction that was added
	 */
	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
		addCommerceAddressRestriction(
			com.liferay.commerce.model.CommerceAddressRestriction
				commerceAddressRestriction) {

		return _commerceAddressRestrictionLocalService.
			addCommerceAddressRestriction(commerceAddressRestriction);
	}

	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
			addCommerceAddressRestriction(
				long userId, long groupId, String className, long classPK,
				long countryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAddressRestrictionLocalService.
			addCommerceAddressRestriction(
				userId, groupId, className, classPK, countryId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
			addCommerceAddressRestriction(
				String className, long classPK, long countryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAddressRestrictionLocalService.
			addCommerceAddressRestriction(
				className, classPK, countryId, serviceContext);
	}

	/**
	 * Creates a new commerce address restriction with the primary key. Does not add the commerce address restriction to the database.
	 *
	 * @param commerceAddressRestrictionId the primary key for the new commerce address restriction
	 * @return the new commerce address restriction
	 */
	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
		createCommerceAddressRestriction(long commerceAddressRestrictionId) {

		return _commerceAddressRestrictionLocalService.
			createCommerceAddressRestriction(commerceAddressRestrictionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAddressRestrictionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce address restriction from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAddressRestrictionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAddressRestriction the commerce address restriction
	 * @return the commerce address restriction that was removed
	 */
	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
		deleteCommerceAddressRestriction(
			com.liferay.commerce.model.CommerceAddressRestriction
				commerceAddressRestriction) {

		return _commerceAddressRestrictionLocalService.
			deleteCommerceAddressRestriction(commerceAddressRestriction);
	}

	/**
	 * Deletes the commerce address restriction with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAddressRestrictionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAddressRestrictionId the primary key of the commerce address restriction
	 * @return the commerce address restriction that was removed
	 * @throws PortalException if a commerce address restriction with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
			deleteCommerceAddressRestriction(long commerceAddressRestrictionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAddressRestrictionLocalService.
			deleteCommerceAddressRestriction(commerceAddressRestrictionId);
	}

	@Override
	public void deleteCommerceAddressRestrictions(
		String className, long classPK) {

		_commerceAddressRestrictionLocalService.
			deleteCommerceAddressRestrictions(className, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAddressRestrictionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceAddressRestrictionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceAddressRestrictionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceAddressRestrictionLocalService.dynamicQuery();
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

		return _commerceAddressRestrictionLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAddressRestrictionModelImpl</code>.
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

		return _commerceAddressRestrictionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAddressRestrictionModelImpl</code>.
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

		return _commerceAddressRestrictionLocalService.dynamicQuery(
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

		return _commerceAddressRestrictionLocalService.dynamicQueryCount(
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

		return _commerceAddressRestrictionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
		fetchCommerceAddressRestriction(long commerceAddressRestrictionId) {

		return _commerceAddressRestrictionLocalService.
			fetchCommerceAddressRestriction(commerceAddressRestrictionId);
	}

	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
		fetchCommerceAddressRestriction(
			String className, long classPK, long countryId) {

		return _commerceAddressRestrictionLocalService.
			fetchCommerceAddressRestriction(className, classPK, countryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceAddressRestrictionLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce address restriction with the primary key.
	 *
	 * @param commerceAddressRestrictionId the primary key of the commerce address restriction
	 * @return the commerce address restriction
	 * @throws PortalException if a commerce address restriction with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
			getCommerceAddressRestriction(long commerceAddressRestrictionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAddressRestrictionLocalService.
			getCommerceAddressRestriction(commerceAddressRestrictionId);
	}

	/**
	 * Returns a range of all the commerce address restrictions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAddressRestrictionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce address restrictions
	 * @param end the upper bound of the range of commerce address restrictions (not inclusive)
	 * @return the range of commerce address restrictions
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceAddressRestriction>
		getCommerceAddressRestrictions(int start, int end) {

		return _commerceAddressRestrictionLocalService.
			getCommerceAddressRestrictions(start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceAddressRestriction>
		getCommerceAddressRestrictions(
			String className, long classPK, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceAddressRestriction>
					orderByComparator) {

		return _commerceAddressRestrictionLocalService.
			getCommerceAddressRestrictions(
				className, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce address restrictions.
	 *
	 * @return the number of commerce address restrictions
	 */
	@Override
	public int getCommerceAddressRestrictionsCount() {
		return _commerceAddressRestrictionLocalService.
			getCommerceAddressRestrictionsCount();
	}

	@Override
	public int getCommerceAddressRestrictionsCount(
		String className, long classPK) {

		return _commerceAddressRestrictionLocalService.
			getCommerceAddressRestrictionsCount(className, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceAddressRestrictionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceAddressRestrictionLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAddressRestrictionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public boolean isCommerceAddressRestricted(
		String className, long classPK, long countryId) {

		return _commerceAddressRestrictionLocalService.
			isCommerceAddressRestricted(className, classPK, countryId);
	}

	@Override
	public boolean isCommerceShippingMethodRestricted(
		long commerceShippingMethodId, long countryId) {

		return _commerceAddressRestrictionLocalService.
			isCommerceShippingMethodRestricted(
				commerceShippingMethodId, countryId);
	}

	/**
	 * Updates the commerce address restriction in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAddressRestrictionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAddressRestriction the commerce address restriction
	 * @return the commerce address restriction that was updated
	 */
	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
		updateCommerceAddressRestriction(
			com.liferay.commerce.model.CommerceAddressRestriction
				commerceAddressRestriction) {

		return _commerceAddressRestrictionLocalService.
			updateCommerceAddressRestriction(commerceAddressRestriction);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceAddressRestrictionLocalService.getBasePersistence();
	}

	@Override
	public CommerceAddressRestrictionLocalService getWrappedService() {
		return _commerceAddressRestrictionLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceAddressRestrictionLocalService
			commerceAddressRestrictionLocalService) {

		_commerceAddressRestrictionLocalService =
			commerceAddressRestrictionLocalService;
	}

	private CommerceAddressRestrictionLocalService
		_commerceAddressRestrictionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:863093706