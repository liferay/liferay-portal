/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceOrderPaymentLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderPaymentLocalService
 * @generated
 */
public class CommerceOrderPaymentLocalServiceWrapper
	implements CommerceOrderPaymentLocalService,
			   ServiceWrapper<CommerceOrderPaymentLocalService> {

	public CommerceOrderPaymentLocalServiceWrapper() {
		this(null);
	}

	public CommerceOrderPaymentLocalServiceWrapper(
		CommerceOrderPaymentLocalService commerceOrderPaymentLocalService) {

		_commerceOrderPaymentLocalService = commerceOrderPaymentLocalService;
	}

	/**
	 * Adds the commerce order payment to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderPaymentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderPayment the commerce order payment
	 * @return the commerce order payment that was added
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderPayment
		addCommerceOrderPayment(
			com.liferay.commerce.model.CommerceOrderPayment
				commerceOrderPayment) {

		return _commerceOrderPaymentLocalService.addCommerceOrderPayment(
			commerceOrderPayment);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderPayment
			addCommerceOrderPayment(
				long commerceOrderId, int status, String result)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderPaymentLocalService.addCommerceOrderPayment(
			commerceOrderId, status, result);
	}

	/**
	 * Creates a new commerce order payment with the primary key. Does not add the commerce order payment to the database.
	 *
	 * @param commerceOrderPaymentId the primary key for the new commerce order payment
	 * @return the new commerce order payment
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderPayment
		createCommerceOrderPayment(long commerceOrderPaymentId) {

		return _commerceOrderPaymentLocalService.createCommerceOrderPayment(
			commerceOrderPaymentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderPaymentLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce order payment from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderPaymentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderPayment the commerce order payment
	 * @return the commerce order payment that was removed
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderPayment
		deleteCommerceOrderPayment(
			com.liferay.commerce.model.CommerceOrderPayment
				commerceOrderPayment) {

		return _commerceOrderPaymentLocalService.deleteCommerceOrderPayment(
			commerceOrderPayment);
	}

	/**
	 * Deletes the commerce order payment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderPaymentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderPaymentId the primary key of the commerce order payment
	 * @return the commerce order payment that was removed
	 * @throws PortalException if a commerce order payment with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderPayment
			deleteCommerceOrderPayment(long commerceOrderPaymentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderPaymentLocalService.deleteCommerceOrderPayment(
			commerceOrderPaymentId);
	}

	@Override
	public void deleteCommerceOrderPayments(long commerceOrderId) {
		_commerceOrderPaymentLocalService.deleteCommerceOrderPayments(
			commerceOrderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderPaymentLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceOrderPaymentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceOrderPaymentLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceOrderPaymentLocalService.dynamicQuery();
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

		return _commerceOrderPaymentLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderPaymentModelImpl</code>.
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

		return _commerceOrderPaymentLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderPaymentModelImpl</code>.
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

		return _commerceOrderPaymentLocalService.dynamicQuery(
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

		return _commerceOrderPaymentLocalService.dynamicQueryCount(
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

		return _commerceOrderPaymentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderPayment
		fetchCommerceOrderPayment(long commerceOrderPaymentId) {

		return _commerceOrderPaymentLocalService.fetchCommerceOrderPayment(
			commerceOrderPaymentId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderPayment
			fetchLatestCommerceOrderPayment(long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderPaymentLocalService.
			fetchLatestCommerceOrderPayment(commerceOrderId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceOrderPaymentLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce order payment with the primary key.
	 *
	 * @param commerceOrderPaymentId the primary key of the commerce order payment
	 * @return the commerce order payment
	 * @throws PortalException if a commerce order payment with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderPayment
			getCommerceOrderPayment(long commerceOrderPaymentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderPaymentLocalService.getCommerceOrderPayment(
			commerceOrderPaymentId);
	}

	/**
	 * Returns a range of all the commerce order payments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderPaymentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order payments
	 * @param end the upper bound of the range of commerce order payments (not inclusive)
	 * @return the range of commerce order payments
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderPayment>
		getCommerceOrderPayments(int start, int end) {

		return _commerceOrderPaymentLocalService.getCommerceOrderPayments(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderPayment>
		getCommerceOrderPayments(
			long commerceOrderId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrderPayment>
					orderByComparator) {

		return _commerceOrderPaymentLocalService.getCommerceOrderPayments(
			commerceOrderId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce order payments.
	 *
	 * @return the number of commerce order payments
	 */
	@Override
	public int getCommerceOrderPaymentsCount() {
		return _commerceOrderPaymentLocalService.
			getCommerceOrderPaymentsCount();
	}

	@Override
	public int getCommerceOrderPaymentsCount(long commerceOrderId) {
		return _commerceOrderPaymentLocalService.getCommerceOrderPaymentsCount(
			commerceOrderId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceOrderPaymentLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceOrderPaymentLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderPaymentLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce order payment in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderPaymentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderPayment the commerce order payment
	 * @return the commerce order payment that was updated
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderPayment
		updateCommerceOrderPayment(
			com.liferay.commerce.model.CommerceOrderPayment
				commerceOrderPayment) {

		return _commerceOrderPaymentLocalService.updateCommerceOrderPayment(
			commerceOrderPayment);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceOrderPaymentLocalService.getBasePersistence();
	}

	@Override
	public CommerceOrderPaymentLocalService getWrappedService() {
		return _commerceOrderPaymentLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceOrderPaymentLocalService commerceOrderPaymentLocalService) {

		_commerceOrderPaymentLocalService = commerceOrderPaymentLocalService;
	}

	private CommerceOrderPaymentLocalService _commerceOrderPaymentLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:2102860915