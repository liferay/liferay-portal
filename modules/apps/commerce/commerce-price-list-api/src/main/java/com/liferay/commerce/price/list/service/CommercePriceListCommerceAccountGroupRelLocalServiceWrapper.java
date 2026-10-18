/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceListCommerceAccountGroupRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommercePriceListCommerceAccountGroupRelLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListCommerceAccountGroupRelLocalService
 * @generated
 */
public class CommercePriceListCommerceAccountGroupRelLocalServiceWrapper
	implements CommercePriceListCommerceAccountGroupRelLocalService,
			   ServiceWrapper
				   <CommercePriceListCommerceAccountGroupRelLocalService> {

	public CommercePriceListCommerceAccountGroupRelLocalServiceWrapper() {
		this(null);
	}

	public CommercePriceListCommerceAccountGroupRelLocalServiceWrapper(
		CommercePriceListCommerceAccountGroupRelLocalService
			commercePriceListCommerceAccountGroupRelLocalService) {

		_commercePriceListCommerceAccountGroupRelLocalService =
			commercePriceListCommerceAccountGroupRelLocalService;
	}

	/**
	 * Adds the commerce price list commerce account group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListCommerceAccountGroupRel the commerce price list commerce account group rel
	 * @return the commerce price list commerce account group rel that was added
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel
		addCommercePriceListCommerceAccountGroupRel(
			CommercePriceListCommerceAccountGroupRel
				commercePriceListCommerceAccountGroupRel) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			addCommercePriceListCommerceAccountGroupRel(
				commercePriceListCommerceAccountGroupRel);
	}

	@Override
	public CommercePriceListCommerceAccountGroupRel
			addCommercePriceListCommerceAccountGroupRel(
				long userId, long commercePriceListId,
				long commerceAccountGroupId, int order,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			addCommercePriceListCommerceAccountGroupRel(
				userId, commercePriceListId, commerceAccountGroupId, order,
				serviceContext);
	}

	/**
	 * Creates a new commerce price list commerce account group rel with the primary key. Does not add the commerce price list commerce account group rel to the database.
	 *
	 * @param commercePriceListCommerceAccountGroupRelId the primary key for the new commerce price list commerce account group rel
	 * @return the new commerce price list commerce account group rel
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel
		createCommercePriceListCommerceAccountGroupRel(
			long commercePriceListCommerceAccountGroupRelId) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			createCommercePriceListCommerceAccountGroupRel(
				commercePriceListCommerceAccountGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteCommercePriceListAccountGroupRelsByCommercePriceListId(
		long commercePriceListId) {

		_commercePriceListCommerceAccountGroupRelLocalService.
			deleteCommercePriceListAccountGroupRelsByCommercePriceListId(
				commercePriceListId);
	}

	/**
	 * Deletes the commerce price list commerce account group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListCommerceAccountGroupRel the commerce price list commerce account group rel
	 * @return the commerce price list commerce account group rel that was removed
	 * @throws PortalException
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel
			deleteCommercePriceListCommerceAccountGroupRel(
				CommercePriceListCommerceAccountGroupRel
					commercePriceListCommerceAccountGroupRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			deleteCommercePriceListCommerceAccountGroupRel(
				commercePriceListCommerceAccountGroupRel);
	}

	/**
	 * Deletes the commerce price list commerce account group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListCommerceAccountGroupRelId the primary key of the commerce price list commerce account group rel
	 * @return the commerce price list commerce account group rel that was removed
	 * @throws PortalException if a commerce price list commerce account group rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel
			deleteCommercePriceListCommerceAccountGroupRel(
				long commercePriceListCommerceAccountGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			deleteCommercePriceListCommerceAccountGroupRel(
				commercePriceListCommerceAccountGroupRelId);
	}

	@Override
	public void deleteCommercePriceListCommerceAccountGroupRels(
			long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListCommerceAccountGroupRelLocalService.
			deleteCommercePriceListCommerceAccountGroupRels(
				commercePriceListId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePriceListCommerceAccountGroupRelLocalService.dslQuery(
			dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePriceListCommerceAccountGroupRelLocalService.
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

		return _commercePriceListCommerceAccountGroupRelLocalService.
			dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListCommerceAccountGroupRelModelImpl</code>.
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

		return _commercePriceListCommerceAccountGroupRelLocalService.
			dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListCommerceAccountGroupRelModelImpl</code>.
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

		return _commercePriceListCommerceAccountGroupRelLocalService.
			dynamicQuery(dynamicQuery, start, end, orderByComparator);
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

		return _commercePriceListCommerceAccountGroupRelLocalService.
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

		return _commercePriceListCommerceAccountGroupRelLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public CommercePriceListCommerceAccountGroupRel
		fetchCommercePriceListCommerceAccountGroupRel(
			long commercePriceListCommerceAccountGroupRelId) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			fetchCommercePriceListCommerceAccountGroupRel(
				commercePriceListCommerceAccountGroupRelId);
	}

	@Override
	public CommercePriceListCommerceAccountGroupRel
		fetchCommercePriceListCommerceAccountGroupRel(
			long commercePriceListId, long commerceAccountGroupId) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			fetchCommercePriceListCommerceAccountGroupRel(
				commercePriceListId, commerceAccountGroupId);
	}

	/**
	 * Returns the commerce price list commerce account group rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list commerce account group rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list commerce account group rel, or <code>null</code> if a matching commerce price list commerce account group rel could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel
		fetchCommercePriceListCommerceAccountGroupRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			fetchCommercePriceListCommerceAccountGroupRelByUuidAndCompanyId(
				uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce price list commerce account group rel with the primary key.
	 *
	 * @param commercePriceListCommerceAccountGroupRelId the primary key of the commerce price list commerce account group rel
	 * @return the commerce price list commerce account group rel
	 * @throws PortalException if a commerce price list commerce account group rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel
			getCommercePriceListCommerceAccountGroupRel(
				long commercePriceListCommerceAccountGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getCommercePriceListCommerceAccountGroupRel(
				commercePriceListCommerceAccountGroupRelId);
	}

	/**
	 * Returns the commerce price list commerce account group rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list commerce account group rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list commerce account group rel
	 * @throws PortalException if a matching commerce price list commerce account group rel could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel
			getCommercePriceListCommerceAccountGroupRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getCommercePriceListCommerceAccountGroupRelByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce price list commerce account group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListCommerceAccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price list commerce account group rels
	 * @param end the upper bound of the range of commerce price list commerce account group rels (not inclusive)
	 * @return the range of commerce price list commerce account group rels
	 */
	@Override
	public java.util.List<CommercePriceListCommerceAccountGroupRel>
		getCommercePriceListCommerceAccountGroupRels(int start, int end) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getCommercePriceListCommerceAccountGroupRels(start, end);
	}

	@Override
	public java.util.List<CommercePriceListCommerceAccountGroupRel>
		getCommercePriceListCommerceAccountGroupRels(long commercePriceListId) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getCommercePriceListCommerceAccountGroupRels(commercePriceListId);
	}

	@Override
	public java.util.List<CommercePriceListCommerceAccountGroupRel>
		getCommercePriceListCommerceAccountGroupRels(
			long commercePriceListId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommercePriceListCommerceAccountGroupRel> orderByComparator) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getCommercePriceListCommerceAccountGroupRels(
				commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CommercePriceListCommerceAccountGroupRel>
		getCommercePriceListCommerceAccountGroupRels(
			long commercePriceListId, String name, int start, int end) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getCommercePriceListCommerceAccountGroupRels(
				commercePriceListId, name, start, end);
	}

	/**
	 * Returns the number of commerce price list commerce account group rels.
	 *
	 * @return the number of commerce price list commerce account group rels
	 */
	@Override
	public int getCommercePriceListCommerceAccountGroupRelsCount() {
		return _commercePriceListCommerceAccountGroupRelLocalService.
			getCommercePriceListCommerceAccountGroupRelsCount();
	}

	@Override
	public int getCommercePriceListCommerceAccountGroupRelsCount(
		long commercePriceListId) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getCommercePriceListCommerceAccountGroupRelsCount(
				commercePriceListId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceListCommerceAccountGroupRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the commerce price list commerce account group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListCommerceAccountGroupRel the commerce price list commerce account group rel
	 * @return the commerce price list commerce account group rel that was updated
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel
		updateCommercePriceListCommerceAccountGroupRel(
			CommercePriceListCommerceAccountGroupRel
				commercePriceListCommerceAccountGroupRel) {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			updateCommercePriceListCommerceAccountGroupRel(
				commercePriceListCommerceAccountGroupRel);
	}

	@Override
	public CommercePriceListCommerceAccountGroupRel
			updateCommercePriceListCommerceAccountGroupRel(
				long commercePriceListCommerceAccountGroupRelId, int order,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			updateCommercePriceListCommerceAccountGroupRel(
				commercePriceListCommerceAccountGroupRelId, order,
				serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePriceListCommerceAccountGroupRelLocalService.
			getBasePersistence();
	}

	@Override
	public CTPersistence<CommercePriceListCommerceAccountGroupRel>
		getCTPersistence() {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			getCTPersistence();
	}

	@Override
	public Class<CommercePriceListCommerceAccountGroupRel> getModelClass() {
		return _commercePriceListCommerceAccountGroupRelLocalService.
			getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction
				<CTPersistence<CommercePriceListCommerceAccountGroupRel>, R, E>
					updateUnsafeFunction)
		throws E {

		return _commercePriceListCommerceAccountGroupRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public CommercePriceListCommerceAccountGroupRelLocalService
		getWrappedService() {

		return _commercePriceListCommerceAccountGroupRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePriceListCommerceAccountGroupRelLocalService
			commercePriceListCommerceAccountGroupRelLocalService) {

		_commercePriceListCommerceAccountGroupRelLocalService =
			commercePriceListCommerceAccountGroupRelLocalService;
	}

	private CommercePriceListCommerceAccountGroupRelLocalService
		_commercePriceListCommerceAccountGroupRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:237335463