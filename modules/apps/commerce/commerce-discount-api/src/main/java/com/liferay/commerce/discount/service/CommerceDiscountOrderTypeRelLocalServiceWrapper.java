/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceDiscountOrderTypeRelLocalService}.
 *
 * @author Marco Leo
 * @see CommerceDiscountOrderTypeRelLocalService
 * @generated
 */
public class CommerceDiscountOrderTypeRelLocalServiceWrapper
	implements CommerceDiscountOrderTypeRelLocalService,
			   ServiceWrapper<CommerceDiscountOrderTypeRelLocalService> {

	public CommerceDiscountOrderTypeRelLocalServiceWrapper() {
		this(null);
	}

	public CommerceDiscountOrderTypeRelLocalServiceWrapper(
		CommerceDiscountOrderTypeRelLocalService
			commerceDiscountOrderTypeRelLocalService) {

		_commerceDiscountOrderTypeRelLocalService =
			commerceDiscountOrderTypeRelLocalService;
	}

	/**
	 * Adds the commerce discount order type rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountOrderTypeRel the commerce discount order type rel
	 * @return the commerce discount order type rel that was added
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
		addCommerceDiscountOrderTypeRel(
			com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
				commerceDiscountOrderTypeRel) {

		return _commerceDiscountOrderTypeRelLocalService.
			addCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRel);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
			addCommerceDiscountOrderTypeRel(
				long userId, long commerceDiscountId, long commerceOrderTypeId,
				int priority,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelLocalService.
			addCommerceDiscountOrderTypeRel(
				userId, commerceDiscountId, commerceOrderTypeId, priority,
				serviceContext);
	}

	/**
	 * Creates a new commerce discount order type rel with the primary key. Does not add the commerce discount order type rel to the database.
	 *
	 * @param commerceDiscountOrderTypeRelId the primary key for the new commerce discount order type rel
	 * @return the new commerce discount order type rel
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
		createCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId) {

		return _commerceDiscountOrderTypeRelLocalService.
			createCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce discount order type rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountOrderTypeRel the commerce discount order type rel
	 * @return the commerce discount order type rel that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
			deleteCommerceDiscountOrderTypeRel(
				com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
					commerceDiscountOrderTypeRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelLocalService.
			deleteCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRel);
	}

	/**
	 * Deletes the commerce discount order type rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountOrderTypeRelId the primary key of the commerce discount order type rel
	 * @return the commerce discount order type rel that was removed
	 * @throws PortalException if a commerce discount order type rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
			deleteCommerceDiscountOrderTypeRel(
				long commerceDiscountOrderTypeRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelLocalService.
			deleteCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRelId);
	}

	@Override
	public void deleteCommerceDiscountOrderTypeRels(long commerceDiscountId) {
		_commerceDiscountOrderTypeRelLocalService.
			deleteCommerceDiscountOrderTypeRels(commerceDiscountId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceDiscountOrderTypeRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceDiscountOrderTypeRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceDiscountOrderTypeRelLocalService.dynamicQuery();
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

		return _commerceDiscountOrderTypeRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountOrderTypeRelModelImpl</code>.
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

		return _commerceDiscountOrderTypeRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountOrderTypeRelModelImpl</code>.
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

		return _commerceDiscountOrderTypeRelLocalService.dynamicQuery(
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

		return _commerceDiscountOrderTypeRelLocalService.dynamicQueryCount(
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

		return _commerceDiscountOrderTypeRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
		fetchCommerceDiscountOrderTypeRel(long commerceDiscountOrderTypeRelId) {

		return _commerceDiscountOrderTypeRelLocalService.
			fetchCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRelId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
		fetchCommerceDiscountOrderTypeRel(
			long commerceDiscountId, long commerceOrderTypeId) {

		return _commerceDiscountOrderTypeRelLocalService.
			fetchCommerceDiscountOrderTypeRel(
				commerceDiscountId, commerceOrderTypeId);
	}

	/**
	 * Returns the commerce discount order type rel with the matching UUID and company.
	 *
	 * @param uuid the commerce discount order type rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount order type rel, or <code>null</code> if a matching commerce discount order type rel could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
		fetchCommerceDiscountOrderTypeRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return _commerceDiscountOrderTypeRelLocalService.
			fetchCommerceDiscountOrderTypeRelByUuidAndCompanyId(
				uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceDiscountOrderTypeRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce discount order type rel with the primary key.
	 *
	 * @param commerceDiscountOrderTypeRelId the primary key of the commerce discount order type rel
	 * @return the commerce discount order type rel
	 * @throws PortalException if a commerce discount order type rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
			getCommerceDiscountOrderTypeRel(long commerceDiscountOrderTypeRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelLocalService.
			getCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRelId);
	}

	/**
	 * Returns the commerce discount order type rel with the matching UUID and company.
	 *
	 * @param uuid the commerce discount order type rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount order type rel
	 * @throws PortalException if a matching commerce discount order type rel could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
			getCommerceDiscountOrderTypeRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelLocalService.
			getCommerceDiscountOrderTypeRelByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce discount order type rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount order type rels
	 * @param end the upper bound of the range of commerce discount order type rels (not inclusive)
	 * @return the range of commerce discount order type rels
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel>
			getCommerceDiscountOrderTypeRels(int start, int end) {

		return _commerceDiscountOrderTypeRelLocalService.
			getCommerceDiscountOrderTypeRels(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel>
			getCommerceDiscountOrderTypeRels(long commerceDiscountId) {

		return _commerceDiscountOrderTypeRelLocalService.
			getCommerceDiscountOrderTypeRels(commerceDiscountId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel>
				getCommerceDiscountOrderTypeRels(
					long commerceDiscountId, String name, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.discount.model.
							CommerceDiscountOrderTypeRel> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelLocalService.
			getCommerceDiscountOrderTypeRels(
				commerceDiscountId, name, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce discount order type rels.
	 *
	 * @return the number of commerce discount order type rels
	 */
	@Override
	public int getCommerceDiscountOrderTypeRelsCount() {
		return _commerceDiscountOrderTypeRelLocalService.
			getCommerceDiscountOrderTypeRelsCount();
	}

	@Override
	public int getCommerceDiscountOrderTypeRelsCount(
			long commerceDiscountId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelLocalService.
			getCommerceDiscountOrderTypeRelsCount(commerceDiscountId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceDiscountOrderTypeRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceDiscountOrderTypeRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceDiscountOrderTypeRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce discount order type rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountOrderTypeRel the commerce discount order type rel
	 * @return the commerce discount order type rel that was updated
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
		updateCommerceDiscountOrderTypeRel(
			com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
				commerceDiscountOrderTypeRel) {

		return _commerceDiscountOrderTypeRelLocalService.
			updateCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRel);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
			updatePriority(long commerceDiscountOrderTypeRelId, int priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelLocalService.updatePriority(
			commerceDiscountOrderTypeRelId, priority);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceDiscountOrderTypeRelLocalService.getBasePersistence();
	}

	@Override
	public CommerceDiscountOrderTypeRelLocalService getWrappedService() {
		return _commerceDiscountOrderTypeRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceDiscountOrderTypeRelLocalService
			commerceDiscountOrderTypeRelLocalService) {

		_commerceDiscountOrderTypeRelLocalService =
			commerceDiscountOrderTypeRelLocalService;
	}

	private CommerceDiscountOrderTypeRelLocalService
		_commerceDiscountOrderTypeRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-531953368