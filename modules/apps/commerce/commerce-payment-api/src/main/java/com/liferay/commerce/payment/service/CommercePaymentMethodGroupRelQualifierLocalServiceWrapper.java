/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommercePaymentMethodGroupRelQualifierLocalService}.
 *
 * @author Luca Pellizzon
 * @see CommercePaymentMethodGroupRelQualifierLocalService
 * @generated
 */
public class CommercePaymentMethodGroupRelQualifierLocalServiceWrapper
	implements CommercePaymentMethodGroupRelQualifierLocalService,
			   ServiceWrapper
				   <CommercePaymentMethodGroupRelQualifierLocalService> {

	public CommercePaymentMethodGroupRelQualifierLocalServiceWrapper() {
		this(null);
	}

	public CommercePaymentMethodGroupRelQualifierLocalServiceWrapper(
		CommercePaymentMethodGroupRelQualifierLocalService
			commercePaymentMethodGroupRelQualifierLocalService) {

		_commercePaymentMethodGroupRelQualifierLocalService =
			commercePaymentMethodGroupRelQualifierLocalService;
	}

	/**
	 * Adds the commerce payment method group rel qualifier to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifier the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was added
	 */
	@Override
	public
		com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier
				addCommercePaymentMethodGroupRelQualifier(
					com.liferay.commerce.payment.model.
						CommercePaymentMethodGroupRelQualifier
							commercePaymentMethodGroupRelQualifier) {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			addCommercePaymentMethodGroupRelQualifier(
				commercePaymentMethodGroupRelQualifier);
	}

	@Override
	public
		com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier
					addCommercePaymentMethodGroupRelQualifier(
						long userId, String className, long classPK,
						long commercePaymentMethodGroupRelId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			addCommercePaymentMethodGroupRelQualifier(
				userId, className, classPK, commercePaymentMethodGroupRelId);
	}

	/**
	 * Creates a new commerce payment method group rel qualifier with the primary key. Does not add the commerce payment method group rel qualifier to the database.
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key for the new commerce payment method group rel qualifier
	 * @return the new commerce payment method group rel qualifier
	 */
	@Override
	public
		com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier
				createCommercePaymentMethodGroupRelQualifier(
					long commercePaymentMethodGroupRelQualifierId) {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			createCommercePaymentMethodGroupRelQualifier(
				commercePaymentMethodGroupRelQualifierId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the commerce payment method group rel qualifier from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifier the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was removed
	 * @throws PortalException
	 */
	@Override
	public
		com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier
					deleteCommercePaymentMethodGroupRelQualifier(
						com.liferay.commerce.payment.model.
							CommercePaymentMethodGroupRelQualifier
								commercePaymentMethodGroupRelQualifier)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			deleteCommercePaymentMethodGroupRelQualifier(
				commercePaymentMethodGroupRelQualifier);
	}

	/**
	 * Deletes the commerce payment method group rel qualifier with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key of the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was removed
	 * @throws PortalException if a commerce payment method group rel qualifier with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier
					deleteCommercePaymentMethodGroupRelQualifier(
						long commercePaymentMethodGroupRelQualifierId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			deleteCommercePaymentMethodGroupRelQualifier(
				commercePaymentMethodGroupRelQualifierId);
	}

	@Override
	public void deleteCommercePaymentMethodGroupRelQualifiers(
			long commercePaymentMethodGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePaymentMethodGroupRelQualifierLocalService.
			deleteCommercePaymentMethodGroupRelQualifiers(
				commercePaymentMethodGroupRelId);
	}

	@Override
	public void deleteCommercePaymentMethodGroupRelQualifiers(
			String className, long commercePaymentMethodGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePaymentMethodGroupRelQualifierLocalService.
			deleteCommercePaymentMethodGroupRelQualifiers(
				className, commercePaymentMethodGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePaymentMethodGroupRelQualifierLocalService.dslQuery(
			dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePaymentMethodGroupRelQualifierLocalService.
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

		return _commercePaymentMethodGroupRelQualifierLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierModelImpl</code>.
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

		return _commercePaymentMethodGroupRelQualifierLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierModelImpl</code>.
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

		return _commercePaymentMethodGroupRelQualifierLocalService.dynamicQuery(
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

		return _commercePaymentMethodGroupRelQualifierLocalService.
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

		return _commercePaymentMethodGroupRelQualifierLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier
				fetchCommercePaymentMethodGroupRelQualifier(
					long commercePaymentMethodGroupRelQualifierId) {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			fetchCommercePaymentMethodGroupRelQualifier(
				commercePaymentMethodGroupRelQualifierId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier>
				getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiers(
					long commercePaymentMethodGroupRelId, String keywords,
					int start, int end) {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiers(
				commercePaymentMethodGroupRelId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiersCount(
		long commercePaymentMethodGroupRelId, String keywords) {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiersCount(
				commercePaymentMethodGroupRelId, keywords);
	}

	/**
	 * Returns the commerce payment method group rel qualifier with the primary key.
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key of the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier
	 * @throws PortalException if a commerce payment method group rel qualifier with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier
					getCommercePaymentMethodGroupRelQualifier(
						long commercePaymentMethodGroupRelQualifierId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			getCommercePaymentMethodGroupRelQualifier(
				commercePaymentMethodGroupRelQualifierId);
	}

	/**
	 * Returns a range of all the commerce payment method group rel qualifiers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment method group rel qualifiers
	 * @param end the upper bound of the range of commerce payment method group rel qualifiers (not inclusive)
	 * @return the range of commerce payment method group rel qualifiers
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier>
				getCommercePaymentMethodGroupRelQualifiers(int start, int end) {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			getCommercePaymentMethodGroupRelQualifiers(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier>
				getCommercePaymentMethodGroupRelQualifiers(
					String className, long commercePaymentMethodGroupRelId) {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			getCommercePaymentMethodGroupRelQualifiers(
				className, commercePaymentMethodGroupRelId);
	}

	/**
	 * Returns the number of commerce payment method group rel qualifiers.
	 *
	 * @return the number of commerce payment method group rel qualifiers
	 */
	@Override
	public int getCommercePaymentMethodGroupRelQualifiersCount() {
		return _commercePaymentMethodGroupRelQualifierLocalService.
			getCommercePaymentMethodGroupRelQualifiersCount();
	}

	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier>
				getCommerceTermEntryCommercePaymentMethodGroupRelQualifiers(
					long commercePaymentMethodGroupRelId, String keywords,
					int start, int end) {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			getCommerceTermEntryCommercePaymentMethodGroupRelQualifiers(
				commercePaymentMethodGroupRelId, keywords, start, end);
	}

	@Override
	public int getCommerceTermEntryCommercePaymentMethodGroupRelQualifiersCount(
		long commercePaymentMethodGroupRelId, String keywords) {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			getCommerceTermEntryCommercePaymentMethodGroupRelQualifiersCount(
				commercePaymentMethodGroupRelId, keywords);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePaymentMethodGroupRelQualifierLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the commerce payment method group rel qualifier in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifier the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was updated
	 */
	@Override
	public
		com.liferay.commerce.payment.model.
			CommercePaymentMethodGroupRelQualifier
				updateCommercePaymentMethodGroupRelQualifier(
					com.liferay.commerce.payment.model.
						CommercePaymentMethodGroupRelQualifier
							commercePaymentMethodGroupRelQualifier) {

		return _commercePaymentMethodGroupRelQualifierLocalService.
			updateCommercePaymentMethodGroupRelQualifier(
				commercePaymentMethodGroupRelQualifier);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePaymentMethodGroupRelQualifierLocalService.
			getBasePersistence();
	}

	@Override
	public CommercePaymentMethodGroupRelQualifierLocalService
		getWrappedService() {

		return _commercePaymentMethodGroupRelQualifierLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePaymentMethodGroupRelQualifierLocalService
			commercePaymentMethodGroupRelQualifierLocalService) {

		_commercePaymentMethodGroupRelQualifierLocalService =
			commercePaymentMethodGroupRelQualifierLocalService;
	}

	private CommercePaymentMethodGroupRelQualifierLocalService
		_commercePaymentMethodGroupRelQualifierLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1246048616