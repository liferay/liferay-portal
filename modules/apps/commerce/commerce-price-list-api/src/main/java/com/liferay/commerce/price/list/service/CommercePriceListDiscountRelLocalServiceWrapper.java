/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceListDiscountRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommercePriceListDiscountRelLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListDiscountRelLocalService
 * @generated
 */
public class CommercePriceListDiscountRelLocalServiceWrapper
	implements CommercePriceListDiscountRelLocalService,
			   ServiceWrapper<CommercePriceListDiscountRelLocalService> {

	public CommercePriceListDiscountRelLocalServiceWrapper() {
		this(null);
	}

	public CommercePriceListDiscountRelLocalServiceWrapper(
		CommercePriceListDiscountRelLocalService
			commercePriceListDiscountRelLocalService) {

		_commercePriceListDiscountRelLocalService =
			commercePriceListDiscountRelLocalService;
	}

	/**
	 * Adds the commerce price list discount rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListDiscountRel the commerce price list discount rel
	 * @return the commerce price list discount rel that was added
	 */
	@Override
	public CommercePriceListDiscountRel addCommercePriceListDiscountRel(
		CommercePriceListDiscountRel commercePriceListDiscountRel) {

		return _commercePriceListDiscountRelLocalService.
			addCommercePriceListDiscountRel(commercePriceListDiscountRel);
	}

	@Override
	public CommercePriceListDiscountRel addCommercePriceListDiscountRel(
			long userId, long commercePriceListId, long commerceDiscountId,
			int order,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListDiscountRelLocalService.
			addCommercePriceListDiscountRel(
				userId, commercePriceListId, commerceDiscountId, order,
				serviceContext);
	}

	/**
	 * Creates a new commerce price list discount rel with the primary key. Does not add the commerce price list discount rel to the database.
	 *
	 * @param commercePriceListDiscountRelId the primary key for the new commerce price list discount rel
	 * @return the new commerce price list discount rel
	 */
	@Override
	public CommercePriceListDiscountRel createCommercePriceListDiscountRel(
		long commercePriceListDiscountRelId) {

		return _commercePriceListDiscountRelLocalService.
			createCommercePriceListDiscountRel(commercePriceListDiscountRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListDiscountRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce price list discount rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListDiscountRel the commerce price list discount rel
	 * @return the commerce price list discount rel that was removed
	 * @throws PortalException
	 */
	@Override
	public CommercePriceListDiscountRel deleteCommercePriceListDiscountRel(
			CommercePriceListDiscountRel commercePriceListDiscountRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListDiscountRelLocalService.
			deleteCommercePriceListDiscountRel(commercePriceListDiscountRel);
	}

	/**
	 * Deletes the commerce price list discount rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListDiscountRelId the primary key of the commerce price list discount rel
	 * @return the commerce price list discount rel that was removed
	 * @throws PortalException if a commerce price list discount rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListDiscountRel deleteCommercePriceListDiscountRel(
			long commercePriceListDiscountRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListDiscountRelLocalService.
			deleteCommercePriceListDiscountRel(commercePriceListDiscountRelId);
	}

	@Override
	public void deleteCommercePriceListDiscountRels(long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListDiscountRelLocalService.
			deleteCommercePriceListDiscountRels(commercePriceListId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListDiscountRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePriceListDiscountRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePriceListDiscountRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePriceListDiscountRelLocalService.dynamicQuery();
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

		return _commercePriceListDiscountRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListDiscountRelModelImpl</code>.
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

		return _commercePriceListDiscountRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListDiscountRelModelImpl</code>.
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

		return _commercePriceListDiscountRelLocalService.dynamicQuery(
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

		return _commercePriceListDiscountRelLocalService.dynamicQueryCount(
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

		return _commercePriceListDiscountRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommercePriceListDiscountRel fetchCommercePriceListDiscountRel(
		long commercePriceListDiscountRelId) {

		return _commercePriceListDiscountRelLocalService.
			fetchCommercePriceListDiscountRel(commercePriceListDiscountRelId);
	}

	@Override
	public CommercePriceListDiscountRel fetchCommercePriceListDiscountRel(
		long commerceDiscountId, long commercePriceListId) {

		return _commercePriceListDiscountRelLocalService.
			fetchCommercePriceListDiscountRel(
				commerceDiscountId, commercePriceListId);
	}

	/**
	 * Returns the commerce price list discount rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list discount rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list discount rel, or <code>null</code> if a matching commerce price list discount rel could not be found
	 */
	@Override
	public CommercePriceListDiscountRel
		fetchCommercePriceListDiscountRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return _commercePriceListDiscountRelLocalService.
			fetchCommercePriceListDiscountRelByUuidAndCompanyId(
				uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePriceListDiscountRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce price list discount rel with the primary key.
	 *
	 * @param commercePriceListDiscountRelId the primary key of the commerce price list discount rel
	 * @return the commerce price list discount rel
	 * @throws PortalException if a commerce price list discount rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListDiscountRel getCommercePriceListDiscountRel(
			long commercePriceListDiscountRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListDiscountRelLocalService.
			getCommercePriceListDiscountRel(commercePriceListDiscountRelId);
	}

	/**
	 * Returns the commerce price list discount rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list discount rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list discount rel
	 * @throws PortalException if a matching commerce price list discount rel could not be found
	 */
	@Override
	public CommercePriceListDiscountRel
			getCommercePriceListDiscountRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListDiscountRelLocalService.
			getCommercePriceListDiscountRelByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce price list discount rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price list discount rels
	 * @param end the upper bound of the range of commerce price list discount rels (not inclusive)
	 * @return the range of commerce price list discount rels
	 */
	@Override
	public java.util.List<CommercePriceListDiscountRel>
		getCommercePriceListDiscountRels(int start, int end) {

		return _commercePriceListDiscountRelLocalService.
			getCommercePriceListDiscountRels(start, end);
	}

	@Override
	public java.util.List<CommercePriceListDiscountRel>
		getCommercePriceListDiscountRels(long commercePriceListId) {

		return _commercePriceListDiscountRelLocalService.
			getCommercePriceListDiscountRels(commercePriceListId);
	}

	@Override
	public java.util.List<CommercePriceListDiscountRel>
		getCommercePriceListDiscountRels(
			long commercePriceListId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommercePriceListDiscountRel> orderByComparator) {

		return _commercePriceListDiscountRelLocalService.
			getCommercePriceListDiscountRels(
				commercePriceListId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce price list discount rels.
	 *
	 * @return the number of commerce price list discount rels
	 */
	@Override
	public int getCommercePriceListDiscountRelsCount() {
		return _commercePriceListDiscountRelLocalService.
			getCommercePriceListDiscountRelsCount();
	}

	@Override
	public int getCommercePriceListDiscountRelsCount(long commercePriceListId) {
		return _commercePriceListDiscountRelLocalService.
			getCommercePriceListDiscountRelsCount(commercePriceListId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commercePriceListDiscountRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePriceListDiscountRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceListDiscountRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListDiscountRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce price list discount rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListDiscountRel the commerce price list discount rel
	 * @return the commerce price list discount rel that was updated
	 */
	@Override
	public CommercePriceListDiscountRel updateCommercePriceListDiscountRel(
		CommercePriceListDiscountRel commercePriceListDiscountRel) {

		return _commercePriceListDiscountRelLocalService.
			updateCommercePriceListDiscountRel(commercePriceListDiscountRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePriceListDiscountRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommercePriceListDiscountRel> getCTPersistence() {
		return _commercePriceListDiscountRelLocalService.getCTPersistence();
	}

	@Override
	public Class<CommercePriceListDiscountRel> getModelClass() {
		return _commercePriceListDiscountRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommercePriceListDiscountRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commercePriceListDiscountRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public CommercePriceListDiscountRelLocalService getWrappedService() {
		return _commercePriceListDiscountRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePriceListDiscountRelLocalService
			commercePriceListDiscountRelLocalService) {

		_commercePriceListDiscountRelLocalService =
			commercePriceListDiscountRelLocalService;
	}

	private CommercePriceListDiscountRelLocalService
		_commercePriceListDiscountRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1450766829