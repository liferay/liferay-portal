/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommercePriceListOrderTypeRelLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListOrderTypeRelLocalService
 * @generated
 */
public class CommercePriceListOrderTypeRelLocalServiceWrapper
	implements CommercePriceListOrderTypeRelLocalService,
			   ServiceWrapper<CommercePriceListOrderTypeRelLocalService> {

	public CommercePriceListOrderTypeRelLocalServiceWrapper() {
		this(null);
	}

	public CommercePriceListOrderTypeRelLocalServiceWrapper(
		CommercePriceListOrderTypeRelLocalService
			commercePriceListOrderTypeRelLocalService) {

		_commercePriceListOrderTypeRelLocalService =
			commercePriceListOrderTypeRelLocalService;
	}

	/**
	 * Adds the commerce price list order type rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListOrderTypeRel the commerce price list order type rel
	 * @return the commerce price list order type rel that was added
	 */
	@Override
	public CommercePriceListOrderTypeRel addCommercePriceListOrderTypeRel(
		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel) {

		return _commercePriceListOrderTypeRelLocalService.
			addCommercePriceListOrderTypeRel(commercePriceListOrderTypeRel);
	}

	@Override
	public CommercePriceListOrderTypeRel addCommercePriceListOrderTypeRel(
			long userId, long commercePriceListId, long commerceOrderTypeId,
			int priority,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListOrderTypeRelLocalService.
			addCommercePriceListOrderTypeRel(
				userId, commercePriceListId, commerceOrderTypeId, priority,
				serviceContext);
	}

	/**
	 * Creates a new commerce price list order type rel with the primary key. Does not add the commerce price list order type rel to the database.
	 *
	 * @param commercePriceListOrderTypeRelId the primary key for the new commerce price list order type rel
	 * @return the new commerce price list order type rel
	 */
	@Override
	public CommercePriceListOrderTypeRel createCommercePriceListOrderTypeRel(
		long commercePriceListOrderTypeRelId) {

		return _commercePriceListOrderTypeRelLocalService.
			createCommercePriceListOrderTypeRel(
				commercePriceListOrderTypeRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListOrderTypeRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce price list order type rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListOrderTypeRel the commerce price list order type rel
	 * @return the commerce price list order type rel that was removed
	 * @throws PortalException
	 */
	@Override
	public CommercePriceListOrderTypeRel deleteCommercePriceListOrderTypeRel(
			CommercePriceListOrderTypeRel commercePriceListOrderTypeRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListOrderTypeRelLocalService.
			deleteCommercePriceListOrderTypeRel(commercePriceListOrderTypeRel);
	}

	/**
	 * Deletes the commerce price list order type rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListOrderTypeRelId the primary key of the commerce price list order type rel
	 * @return the commerce price list order type rel that was removed
	 * @throws PortalException if a commerce price list order type rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel deleteCommercePriceListOrderTypeRel(
			long commercePriceListOrderTypeRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListOrderTypeRelLocalService.
			deleteCommercePriceListOrderTypeRel(
				commercePriceListOrderTypeRelId);
	}

	@Override
	public void deleteCommercePriceListOrderTypeRels(long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListOrderTypeRelLocalService.
			deleteCommercePriceListOrderTypeRels(commercePriceListId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListOrderTypeRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePriceListOrderTypeRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePriceListOrderTypeRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePriceListOrderTypeRelLocalService.dynamicQuery();
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

		return _commercePriceListOrderTypeRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListOrderTypeRelModelImpl</code>.
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

		return _commercePriceListOrderTypeRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListOrderTypeRelModelImpl</code>.
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

		return _commercePriceListOrderTypeRelLocalService.dynamicQuery(
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

		return _commercePriceListOrderTypeRelLocalService.dynamicQueryCount(
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

		return _commercePriceListOrderTypeRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommercePriceListOrderTypeRel fetchCommercePriceListOrderTypeRel(
		long commercePriceListOrderTypeRelId) {

		return _commercePriceListOrderTypeRelLocalService.
			fetchCommercePriceListOrderTypeRel(commercePriceListOrderTypeRelId);
	}

	@Override
	public CommercePriceListOrderTypeRel fetchCommercePriceListOrderTypeRel(
		long commercePriceListId, long commerceOrderTypeId) {

		return _commercePriceListOrderTypeRelLocalService.
			fetchCommercePriceListOrderTypeRel(
				commercePriceListId, commerceOrderTypeId);
	}

	/**
	 * Returns the commerce price list order type rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list order type rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list order type rel, or <code>null</code> if a matching commerce price list order type rel could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel
		fetchCommercePriceListOrderTypeRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return _commercePriceListOrderTypeRelLocalService.
			fetchCommercePriceListOrderTypeRelByUuidAndCompanyId(
				uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePriceListOrderTypeRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce price list order type rel with the primary key.
	 *
	 * @param commercePriceListOrderTypeRelId the primary key of the commerce price list order type rel
	 * @return the commerce price list order type rel
	 * @throws PortalException if a commerce price list order type rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel getCommercePriceListOrderTypeRel(
			long commercePriceListOrderTypeRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListOrderTypeRelLocalService.
			getCommercePriceListOrderTypeRel(commercePriceListOrderTypeRelId);
	}

	/**
	 * Returns the commerce price list order type rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list order type rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list order type rel
	 * @throws PortalException if a matching commerce price list order type rel could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel
			getCommercePriceListOrderTypeRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListOrderTypeRelLocalService.
			getCommercePriceListOrderTypeRelByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce price list order type rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price list order type rels
	 * @param end the upper bound of the range of commerce price list order type rels (not inclusive)
	 * @return the range of commerce price list order type rels
	 */
	@Override
	public java.util.List<CommercePriceListOrderTypeRel>
		getCommercePriceListOrderTypeRels(int start, int end) {

		return _commercePriceListOrderTypeRelLocalService.
			getCommercePriceListOrderTypeRels(start, end);
	}

	@Override
	public java.util.List<CommercePriceListOrderTypeRel>
		getCommercePriceListOrderTypeRels(long commercePriceListId) {

		return _commercePriceListOrderTypeRelLocalService.
			getCommercePriceListOrderTypeRels(commercePriceListId);
	}

	@Override
	public java.util.List<CommercePriceListOrderTypeRel>
			getCommercePriceListOrderTypeRels(
				long commercePriceListId, String name, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<CommercePriceListOrderTypeRel> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListOrderTypeRelLocalService.
			getCommercePriceListOrderTypeRels(
				commercePriceListId, name, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce price list order type rels.
	 *
	 * @return the number of commerce price list order type rels
	 */
	@Override
	public int getCommercePriceListOrderTypeRelsCount() {
		return _commercePriceListOrderTypeRelLocalService.
			getCommercePriceListOrderTypeRelsCount();
	}

	@Override
	public int getCommercePriceListOrderTypeRelsCount(
			long commercePriceListId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListOrderTypeRelLocalService.
			getCommercePriceListOrderTypeRelsCount(commercePriceListId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commercePriceListOrderTypeRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePriceListOrderTypeRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceListOrderTypeRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListOrderTypeRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce price list order type rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListOrderTypeRel the commerce price list order type rel
	 * @return the commerce price list order type rel that was updated
	 */
	@Override
	public CommercePriceListOrderTypeRel updateCommercePriceListOrderTypeRel(
		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel) {

		return _commercePriceListOrderTypeRelLocalService.
			updateCommercePriceListOrderTypeRel(commercePriceListOrderTypeRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePriceListOrderTypeRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommercePriceListOrderTypeRel> getCTPersistence() {
		return _commercePriceListOrderTypeRelLocalService.getCTPersistence();
	}

	@Override
	public Class<CommercePriceListOrderTypeRel> getModelClass() {
		return _commercePriceListOrderTypeRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommercePriceListOrderTypeRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commercePriceListOrderTypeRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public CommercePriceListOrderTypeRelLocalService getWrappedService() {
		return _commercePriceListOrderTypeRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePriceListOrderTypeRelLocalService
			commercePriceListOrderTypeRelLocalService) {

		_commercePriceListOrderTypeRelLocalService =
			commercePriceListOrderTypeRelLocalService;
	}

	private CommercePriceListOrderTypeRelLocalService
		_commercePriceListOrderTypeRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:254834312