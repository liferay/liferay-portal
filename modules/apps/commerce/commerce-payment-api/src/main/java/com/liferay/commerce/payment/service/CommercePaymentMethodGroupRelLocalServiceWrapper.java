/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommercePaymentMethodGroupRelLocalService}.
 *
 * @author Luca Pellizzon
 * @see CommercePaymentMethodGroupRelLocalService
 * @generated
 */
public class CommercePaymentMethodGroupRelLocalServiceWrapper
	implements CommercePaymentMethodGroupRelLocalService,
			   ServiceWrapper<CommercePaymentMethodGroupRelLocalService> {

	public CommercePaymentMethodGroupRelLocalServiceWrapper() {
		this(null);
	}

	public CommercePaymentMethodGroupRelLocalServiceWrapper(
		CommercePaymentMethodGroupRelLocalService
			commercePaymentMethodGroupRelLocalService) {

		_commercePaymentMethodGroupRelLocalService =
			commercePaymentMethodGroupRelLocalService;
	}

	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
			addCommerceAddressRestriction(
				long userId, long groupId, long commercePaymentMethodGroupRelId,
				long countryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelLocalService.
			addCommerceAddressRestriction(
				userId, groupId, commercePaymentMethodGroupRelId, countryId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public com.liferay.commerce.model.CommerceAddressRestriction
			addCommerceAddressRestriction(
				long commercePaymentMethodGroupRelId, long countryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelLocalService.
			addCommerceAddressRestriction(
				commercePaymentMethodGroupRelId, countryId, serviceContext);
	}

	/**
	 * Adds the commerce payment method group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRel the commerce payment method group rel
	 * @return the commerce payment method group rel that was added
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
		addCommercePaymentMethodGroupRel(
			com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
				commercePaymentMethodGroupRel) {

		return _commercePaymentMethodGroupRelLocalService.
			addCommercePaymentMethodGroupRel(commercePaymentMethodGroupRel);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
			addCommercePaymentMethodGroupRel(
				long userId, long groupId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				boolean active, java.io.File imageFile,
				String paymentIntegrationKey, double priority,
				String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelLocalService.
			addCommercePaymentMethodGroupRel(
				userId, groupId, nameMap, descriptionMap, active, imageFile,
				paymentIntegrationKey, priority, typeSettings);
	}

	/**
	 * Creates a new commerce payment method group rel with the primary key. Does not add the commerce payment method group rel to the database.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key for the new commerce payment method group rel
	 * @return the new commerce payment method group rel
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
		createCommercePaymentMethodGroupRel(
			long commercePaymentMethodGroupRelId) {

		return _commercePaymentMethodGroupRelLocalService.
			createCommercePaymentMethodGroupRel(
				commercePaymentMethodGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCommerceAddressRestriction(
			long commerceAddressRestrictionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePaymentMethodGroupRelLocalService.
			deleteCommerceAddressRestriction(commerceAddressRestrictionId);
	}

	/**
	 * Deletes the commerce payment method group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRel the commerce payment method group rel
	 * @return the commerce payment method group rel that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
			deleteCommercePaymentMethodGroupRel(
				com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
					commercePaymentMethodGroupRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelLocalService.
			deleteCommercePaymentMethodGroupRel(commercePaymentMethodGroupRel);
	}

	/**
	 * Deletes the commerce payment method group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel that was removed
	 * @throws PortalException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
			deleteCommercePaymentMethodGroupRel(
				long commercePaymentMethodGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelLocalService.
			deleteCommercePaymentMethodGroupRel(
				commercePaymentMethodGroupRelId);
	}

	@Override
	public void deleteCommercePaymentMethodGroupRels(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePaymentMethodGroupRelLocalService.
			deleteCommercePaymentMethodGroupRels(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePaymentMethodGroupRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePaymentMethodGroupRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePaymentMethodGroupRelLocalService.dynamicQuery();
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

		return _commercePaymentMethodGroupRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelModelImpl</code>.
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

		return _commercePaymentMethodGroupRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelModelImpl</code>.
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

		return _commercePaymentMethodGroupRelLocalService.dynamicQuery(
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

		return _commercePaymentMethodGroupRelLocalService.dynamicQueryCount(
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

		return _commercePaymentMethodGroupRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
		fetchCommercePaymentMethodGroupRel(
			long commercePaymentMethodGroupRelId) {

		return _commercePaymentMethodGroupRelLocalService.
			fetchCommercePaymentMethodGroupRel(commercePaymentMethodGroupRelId);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
		fetchCommercePaymentMethodGroupRel(
			long groupId, String paymentIntegrationKey) {

		return _commercePaymentMethodGroupRelLocalService.
			fetchCommercePaymentMethodGroupRel(groupId, paymentIntegrationKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePaymentMethodGroupRelLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceAddressRestriction>
		getCommerceAddressRestrictions(
			long commercePaymentMethodGroupRelId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceAddressRestriction>
					orderByComparator) {

		return _commercePaymentMethodGroupRelLocalService.
			getCommerceAddressRestrictions(
				commercePaymentMethodGroupRelId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceAddressRestrictionsCount(
		long commercePaymentMethodGroupRelId) {

		return _commercePaymentMethodGroupRelLocalService.
			getCommerceAddressRestrictionsCount(
				commercePaymentMethodGroupRelId);
	}

	/**
	 * Returns the commerce payment method group rel with the primary key.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel
	 * @throws PortalException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
			getCommercePaymentMethodGroupRel(
				long commercePaymentMethodGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRel(commercePaymentMethodGroupRelId);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
			getCommercePaymentMethodGroupRel(
				long groupId, String paymentIntegrationKey)
		throws com.liferay.commerce.payment.exception.
			NoSuchPaymentMethodGroupRelException {

		return _commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRel(groupId, paymentIntegrationKey);
	}

	/**
	 * Returns a range of all the commerce payment method group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @return the range of commerce payment method group rels
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels(int start, int end) {

		return _commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRels(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels(long groupId, boolean active) {

		return _commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRels(groupId, active);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels(
				long groupId, boolean active, int start, int end) {

		return _commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRels(groupId, active, start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels(
				long groupId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.payment.model.
						CommercePaymentMethodGroupRel> orderByComparator) {

		return _commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRels(
				groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels(
				long groupId, long countryId, boolean active) {

		return _commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRels(groupId, countryId, active);
	}

	/**
	 * Returns the number of commerce payment method group rels.
	 *
	 * @return the number of commerce payment method group rels
	 */
	@Override
	public int getCommercePaymentMethodGroupRelsCount() {
		return _commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRelsCount();
	}

	@Override
	public int getCommercePaymentMethodGroupRelsCount(
		long groupId, boolean active) {

		return _commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRelsCount(groupId, active);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePaymentMethodGroupRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePaymentMethodGroupRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
			setActive(long commercePaymentMethodGroupRelId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelLocalService.setActive(
			commercePaymentMethodGroupRelId, active);
	}

	/**
	 * Updates the commerce payment method group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRel the commerce payment method group rel
	 * @return the commerce payment method group rel that was updated
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
		updateCommercePaymentMethodGroupRel(
			com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
				commercePaymentMethodGroupRel) {

		return _commercePaymentMethodGroupRelLocalService.
			updateCommercePaymentMethodGroupRel(commercePaymentMethodGroupRel);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel
			updateCommercePaymentMethodGroupRel(
				long commercePaymentMethodGroupRelId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				java.io.File imageFile, double priority, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentMethodGroupRelLocalService.
			updateCommercePaymentMethodGroupRel(
				commercePaymentMethodGroupRelId, nameMap, descriptionMap,
				imageFile, priority, active);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePaymentMethodGroupRelLocalService.getBasePersistence();
	}

	@Override
	public CommercePaymentMethodGroupRelLocalService getWrappedService() {
		return _commercePaymentMethodGroupRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePaymentMethodGroupRelLocalService
			commercePaymentMethodGroupRelLocalService) {

		_commercePaymentMethodGroupRelLocalService =
			commercePaymentMethodGroupRelLocalService;
	}

	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1887069673