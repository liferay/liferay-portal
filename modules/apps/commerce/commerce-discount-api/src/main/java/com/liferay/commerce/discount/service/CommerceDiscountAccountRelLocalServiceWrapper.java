/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceDiscountAccountRelLocalService}.
 *
 * @author Marco Leo
 * @see CommerceDiscountAccountRelLocalService
 * @generated
 */
public class CommerceDiscountAccountRelLocalServiceWrapper
	implements CommerceDiscountAccountRelLocalService,
			   ServiceWrapper<CommerceDiscountAccountRelLocalService> {

	public CommerceDiscountAccountRelLocalServiceWrapper() {
		this(null);
	}

	public CommerceDiscountAccountRelLocalServiceWrapper(
		CommerceDiscountAccountRelLocalService
			commerceDiscountAccountRelLocalService) {

		_commerceDiscountAccountRelLocalService =
			commerceDiscountAccountRelLocalService;
	}

	/**
	 * Adds the commerce discount account rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountAccountRel the commerce discount account rel
	 * @return the commerce discount account rel that was added
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountAccountRel
		addCommerceDiscountAccountRel(
			com.liferay.commerce.discount.model.CommerceDiscountAccountRel
				commerceDiscountAccountRel) {

		return _commerceDiscountAccountRelLocalService.
			addCommerceDiscountAccountRel(commerceDiscountAccountRel);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountAccountRel
			addCommerceDiscountAccountRel(
				long userId, long commerceDiscountId, long commerceAccountId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountAccountRelLocalService.
			addCommerceDiscountAccountRel(
				userId, commerceDiscountId, commerceAccountId, serviceContext);
	}

	/**
	 * Creates a new commerce discount account rel with the primary key. Does not add the commerce discount account rel to the database.
	 *
	 * @param commerceDiscountAccountRelId the primary key for the new commerce discount account rel
	 * @return the new commerce discount account rel
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountAccountRel
		createCommerceDiscountAccountRel(long commerceDiscountAccountRelId) {

		return _commerceDiscountAccountRelLocalService.
			createCommerceDiscountAccountRel(commerceDiscountAccountRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountAccountRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce discount account rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountAccountRel the commerce discount account rel
	 * @return the commerce discount account rel that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountAccountRel
			deleteCommerceDiscountAccountRel(
				com.liferay.commerce.discount.model.CommerceDiscountAccountRel
					commerceDiscountAccountRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountAccountRelLocalService.
			deleteCommerceDiscountAccountRel(commerceDiscountAccountRel);
	}

	/**
	 * Deletes the commerce discount account rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountAccountRelId the primary key of the commerce discount account rel
	 * @return the commerce discount account rel that was removed
	 * @throws PortalException if a commerce discount account rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountAccountRel
			deleteCommerceDiscountAccountRel(long commerceDiscountAccountRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountAccountRelLocalService.
			deleteCommerceDiscountAccountRel(commerceDiscountAccountRelId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public void deleteCommerceDiscountAccountRelsBycommerceAccountId(
		long commerceAccountId) {

		_commerceDiscountAccountRelLocalService.
			deleteCommerceDiscountAccountRelsBycommerceAccountId(
				commerceAccountId);
	}

	@Override
	public void deleteCommerceDiscountAccountRelsByCommerceDiscountId(
			long commerceDiscountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceDiscountAccountRelLocalService.
			deleteCommerceDiscountAccountRelsByCommerceDiscountId(
				commerceDiscountId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountAccountRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceDiscountAccountRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceDiscountAccountRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceDiscountAccountRelLocalService.dynamicQuery();
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

		return _commerceDiscountAccountRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountAccountRelModelImpl</code>.
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

		return _commerceDiscountAccountRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountAccountRelModelImpl</code>.
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

		return _commerceDiscountAccountRelLocalService.dynamicQuery(
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

		return _commerceDiscountAccountRelLocalService.dynamicQueryCount(
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

		return _commerceDiscountAccountRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountAccountRel
		fetchCommerceDiscountAccountRel(long commerceDiscountAccountRelId) {

		return _commerceDiscountAccountRelLocalService.
			fetchCommerceDiscountAccountRel(commerceDiscountAccountRelId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountAccountRel
		fetchCommerceDiscountAccountRel(
			long commerceAccountId, long commerceDiscountId) {

		return _commerceDiscountAccountRelLocalService.
			fetchCommerceDiscountAccountRel(
				commerceAccountId, commerceDiscountId);
	}

	/**
	 * Returns the commerce discount account rel with the matching UUID and company.
	 *
	 * @param uuid the commerce discount account rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount account rel, or <code>null</code> if a matching commerce discount account rel could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountAccountRel
		fetchCommerceDiscountAccountRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return _commerceDiscountAccountRelLocalService.
			fetchCommerceDiscountAccountRelByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceDiscountAccountRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce discount account rel with the primary key.
	 *
	 * @param commerceDiscountAccountRelId the primary key of the commerce discount account rel
	 * @return the commerce discount account rel
	 * @throws PortalException if a commerce discount account rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountAccountRel
			getCommerceDiscountAccountRel(long commerceDiscountAccountRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountAccountRelLocalService.
			getCommerceDiscountAccountRel(commerceDiscountAccountRelId);
	}

	/**
	 * Returns the commerce discount account rel with the matching UUID and company.
	 *
	 * @param uuid the commerce discount account rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount account rel
	 * @throws PortalException if a matching commerce discount account rel could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountAccountRel
			getCommerceDiscountAccountRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountAccountRelLocalService.
			getCommerceDiscountAccountRelByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce discount account rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount account rels
	 * @param end the upper bound of the range of commerce discount account rels (not inclusive)
	 * @return the range of commerce discount account rels
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountAccountRel>
			getCommerceDiscountAccountRels(int start, int end) {

		return _commerceDiscountAccountRelLocalService.
			getCommerceDiscountAccountRels(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountAccountRel>
			getCommerceDiscountAccountRels(
				long commerceDiscountId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.discount.model.
						CommerceDiscountAccountRel> orderByComparator) {

		return _commerceDiscountAccountRelLocalService.
			getCommerceDiscountAccountRels(
				commerceDiscountId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce discount account rels.
	 *
	 * @return the number of commerce discount account rels
	 */
	@Override
	public int getCommerceDiscountAccountRelsCount() {
		return _commerceDiscountAccountRelLocalService.
			getCommerceDiscountAccountRelsCount();
	}

	@Override
	public int getCommerceDiscountAccountRelsCount(long commerceDiscountId) {
		return _commerceDiscountAccountRelLocalService.
			getCommerceDiscountAccountRelsCount(commerceDiscountId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceDiscountAccountRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceDiscountAccountRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceDiscountAccountRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountAccountRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce discount account rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountAccountRel the commerce discount account rel
	 * @return the commerce discount account rel that was updated
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountAccountRel
		updateCommerceDiscountAccountRel(
			com.liferay.commerce.discount.model.CommerceDiscountAccountRel
				commerceDiscountAccountRel) {

		return _commerceDiscountAccountRelLocalService.
			updateCommerceDiscountAccountRel(commerceDiscountAccountRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceDiscountAccountRelLocalService.getBasePersistence();
	}

	@Override
	public CommerceDiscountAccountRelLocalService getWrappedService() {
		return _commerceDiscountAccountRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceDiscountAccountRelLocalService
			commerceDiscountAccountRelLocalService) {

		_commerceDiscountAccountRelLocalService =
			commerceDiscountAccountRelLocalService;
	}

	private CommerceDiscountAccountRelLocalService
		_commerceDiscountAccountRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-579624397