/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceShippingFixedOptionQualifierLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingFixedOptionQualifierLocalService
 * @generated
 */
public class CommerceShippingFixedOptionQualifierLocalServiceWrapper
	implements CommerceShippingFixedOptionQualifierLocalService,
			   ServiceWrapper
				   <CommerceShippingFixedOptionQualifierLocalService> {

	public CommerceShippingFixedOptionQualifierLocalServiceWrapper() {
		this(null);
	}

	public CommerceShippingFixedOptionQualifierLocalServiceWrapper(
		CommerceShippingFixedOptionQualifierLocalService
			commerceShippingFixedOptionQualifierLocalService) {

		_commerceShippingFixedOptionQualifierLocalService =
			commerceShippingFixedOptionQualifierLocalService;
	}

	/**
	 * Adds the commerce shipping fixed option qualifier to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionQualifier the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier that was added
	 */
	@Override
	public com.liferay.commerce.shipping.engine.fixed.model.
		CommerceShippingFixedOptionQualifier
			addCommerceShippingFixedOptionQualifier(
				com.liferay.commerce.shipping.engine.fixed.model.
					CommerceShippingFixedOptionQualifier
						commerceShippingFixedOptionQualifier) {

		return _commerceShippingFixedOptionQualifierLocalService.
			addCommerceShippingFixedOptionQualifier(
				commerceShippingFixedOptionQualifier);
	}

	@Override
	public com.liferay.commerce.shipping.engine.fixed.model.
		CommerceShippingFixedOptionQualifier
				addCommerceShippingFixedOptionQualifier(
					long userId, String className, long classPK,
					long commerceShippingFixedOptionId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionQualifierLocalService.
			addCommerceShippingFixedOptionQualifier(
				userId, className, classPK, commerceShippingFixedOptionId);
	}

	/**
	 * Creates a new commerce shipping fixed option qualifier with the primary key. Does not add the commerce shipping fixed option qualifier to the database.
	 *
	 * @param commerceShippingFixedOptionQualifierId the primary key for the new commerce shipping fixed option qualifier
	 * @return the new commerce shipping fixed option qualifier
	 */
	@Override
	public com.liferay.commerce.shipping.engine.fixed.model.
		CommerceShippingFixedOptionQualifier
			createCommerceShippingFixedOptionQualifier(
				long commerceShippingFixedOptionQualifierId) {

		return _commerceShippingFixedOptionQualifierLocalService.
			createCommerceShippingFixedOptionQualifier(
				commerceShippingFixedOptionQualifierId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionQualifierLocalService.
			createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the commerce shipping fixed option qualifier from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionQualifier the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier that was removed
	 */
	@Override
	public com.liferay.commerce.shipping.engine.fixed.model.
		CommerceShippingFixedOptionQualifier
			deleteCommerceShippingFixedOptionQualifier(
				com.liferay.commerce.shipping.engine.fixed.model.
					CommerceShippingFixedOptionQualifier
						commerceShippingFixedOptionQualifier) {

		return _commerceShippingFixedOptionQualifierLocalService.
			deleteCommerceShippingFixedOptionQualifier(
				commerceShippingFixedOptionQualifier);
	}

	/**
	 * Deletes the commerce shipping fixed option qualifier with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionQualifierId the primary key of the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier that was removed
	 * @throws PortalException if a commerce shipping fixed option qualifier with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.shipping.engine.fixed.model.
		CommerceShippingFixedOptionQualifier
				deleteCommerceShippingFixedOptionQualifier(
					long commerceShippingFixedOptionQualifierId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionQualifierLocalService.
			deleteCommerceShippingFixedOptionQualifier(
				commerceShippingFixedOptionQualifierId);
	}

	@Override
	public void deleteCommerceShippingFixedOptionQualifiers(
			long commerceShippingFixedOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceShippingFixedOptionQualifierLocalService.
			deleteCommerceShippingFixedOptionQualifiers(
				commerceShippingFixedOptionId);
	}

	@Override
	public void deleteCommerceShippingFixedOptionQualifiers(
			String className, long commerceShippingFixedOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceShippingFixedOptionQualifierLocalService.
			deleteCommerceShippingFixedOptionQualifiers(
				className, commerceShippingFixedOptionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionQualifierLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceShippingFixedOptionQualifierLocalService.dslQuery(
			dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceShippingFixedOptionQualifierLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceShippingFixedOptionQualifierLocalService.dynamicQuery();
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

		return _commerceShippingFixedOptionQualifierLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionQualifierModelImpl</code>.
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

		return _commerceShippingFixedOptionQualifierLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionQualifierModelImpl</code>.
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

		return _commerceShippingFixedOptionQualifierLocalService.dynamicQuery(
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

		return _commerceShippingFixedOptionQualifierLocalService.
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

		return _commerceShippingFixedOptionQualifierLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.shipping.engine.fixed.model.
		CommerceShippingFixedOptionQualifier
			fetchCommerceShippingFixedOptionQualifier(
				long commerceShippingFixedOptionQualifierId) {

		return _commerceShippingFixedOptionQualifierLocalService.
			fetchCommerceShippingFixedOptionQualifier(
				commerceShippingFixedOptionQualifierId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceShippingFixedOptionQualifierLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOptionQualifier>
				getCommerceOrderTypeCommerceShippingFixedOptionQualifiers(
					long commerceShippingFixedOptionId, String keywords,
					int start, int end) {

		return _commerceShippingFixedOptionQualifierLocalService.
			getCommerceOrderTypeCommerceShippingFixedOptionQualifiers(
				commerceShippingFixedOptionId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCommerceShippingFixedOptionQualifiersCount(
		long commerceShippingFixedOptionId, String keywords) {

		return _commerceShippingFixedOptionQualifierLocalService.
			getCommerceOrderTypeCommerceShippingFixedOptionQualifiersCount(
				commerceShippingFixedOptionId, keywords);
	}

	/**
	 * Returns the commerce shipping fixed option qualifier with the primary key.
	 *
	 * @param commerceShippingFixedOptionQualifierId the primary key of the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier
	 * @throws PortalException if a commerce shipping fixed option qualifier with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.shipping.engine.fixed.model.
		CommerceShippingFixedOptionQualifier
				getCommerceShippingFixedOptionQualifier(
					long commerceShippingFixedOptionQualifierId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionQualifierLocalService.
			getCommerceShippingFixedOptionQualifier(
				commerceShippingFixedOptionQualifierId);
	}

	/**
	 * Returns a range of all the commerce shipping fixed option qualifiers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionQualifierModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipping fixed option qualifiers
	 * @param end the upper bound of the range of commerce shipping fixed option qualifiers (not inclusive)
	 * @return the range of commerce shipping fixed option qualifiers
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOptionQualifier>
				getCommerceShippingFixedOptionQualifiers(int start, int end) {

		return _commerceShippingFixedOptionQualifierLocalService.
			getCommerceShippingFixedOptionQualifiers(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOptionQualifier>
				getCommerceShippingFixedOptionQualifiers(
					String className, long commerceShippingFixedOptionId) {

		return _commerceShippingFixedOptionQualifierLocalService.
			getCommerceShippingFixedOptionQualifiers(
				className, commerceShippingFixedOptionId);
	}

	/**
	 * Returns the number of commerce shipping fixed option qualifiers.
	 *
	 * @return the number of commerce shipping fixed option qualifiers
	 */
	@Override
	public int getCommerceShippingFixedOptionQualifiersCount() {
		return _commerceShippingFixedOptionQualifierLocalService.
			getCommerceShippingFixedOptionQualifiersCount();
	}

	@Override
	public java.util.List
		<com.liferay.commerce.shipping.engine.fixed.model.
			CommerceShippingFixedOptionQualifier>
				getCommerceTermEntryCommerceShippingFixedOptionQualifiers(
					long commerceShippingFixedOptionId, String keywords,
					int start, int end) {

		return _commerceShippingFixedOptionQualifierLocalService.
			getCommerceTermEntryCommerceShippingFixedOptionQualifiers(
				commerceShippingFixedOptionId, keywords, start, end);
	}

	@Override
	public int getCommerceTermEntryCommerceShippingFixedOptionQualifiersCount(
		long commerceShippingFixedOptionId, String keywords) {

		return _commerceShippingFixedOptionQualifierLocalService.
			getCommerceTermEntryCommerceShippingFixedOptionQualifiersCount(
				commerceShippingFixedOptionId, keywords);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceShippingFixedOptionQualifierLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceShippingFixedOptionQualifierLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShippingFixedOptionQualifierLocalService.
			getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the commerce shipping fixed option qualifier in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionQualifier the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier that was updated
	 */
	@Override
	public com.liferay.commerce.shipping.engine.fixed.model.
		CommerceShippingFixedOptionQualifier
			updateCommerceShippingFixedOptionQualifier(
				com.liferay.commerce.shipping.engine.fixed.model.
					CommerceShippingFixedOptionQualifier
						commerceShippingFixedOptionQualifier) {

		return _commerceShippingFixedOptionQualifierLocalService.
			updateCommerceShippingFixedOptionQualifier(
				commerceShippingFixedOptionQualifier);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceShippingFixedOptionQualifierLocalService.
			getBasePersistence();
	}

	@Override
	public CommerceShippingFixedOptionQualifierLocalService
		getWrappedService() {

		return _commerceShippingFixedOptionQualifierLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceShippingFixedOptionQualifierLocalService
			commerceShippingFixedOptionQualifierLocalService) {

		_commerceShippingFixedOptionQualifierLocalService =
			commerceShippingFixedOptionQualifierLocalService;
	}

	private CommerceShippingFixedOptionQualifierLocalService
		_commerceShippingFixedOptionQualifierLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1460739998