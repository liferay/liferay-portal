/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceListAccountRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommercePriceListAccountRelLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListAccountRelLocalService
 * @generated
 */
public class CommercePriceListAccountRelLocalServiceWrapper
	implements CommercePriceListAccountRelLocalService,
			   ServiceWrapper<CommercePriceListAccountRelLocalService> {

	public CommercePriceListAccountRelLocalServiceWrapper() {
		this(null);
	}

	public CommercePriceListAccountRelLocalServiceWrapper(
		CommercePriceListAccountRelLocalService
			commercePriceListAccountRelLocalService) {

		_commercePriceListAccountRelLocalService =
			commercePriceListAccountRelLocalService;
	}

	/**
	 * Adds the commerce price list account rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRel the commerce price list account rel
	 * @return the commerce price list account rel that was added
	 */
	@Override
	public CommercePriceListAccountRel addCommercePriceListAccountRel(
		CommercePriceListAccountRel commercePriceListAccountRel) {

		return _commercePriceListAccountRelLocalService.
			addCommercePriceListAccountRel(commercePriceListAccountRel);
	}

	@Override
	public CommercePriceListAccountRel addCommercePriceListAccountRel(
			long userId, long commercePriceListId, long commerceAccountId,
			int order,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListAccountRelLocalService.
			addCommercePriceListAccountRel(
				userId, commercePriceListId, commerceAccountId, order,
				serviceContext);
	}

	/**
	 * Creates a new commerce price list account rel with the primary key. Does not add the commerce price list account rel to the database.
	 *
	 * @param commercePriceListAccountRelId the primary key for the new commerce price list account rel
	 * @return the new commerce price list account rel
	 */
	@Override
	public CommercePriceListAccountRel createCommercePriceListAccountRel(
		long commercePriceListAccountRelId) {

		return _commercePriceListAccountRelLocalService.
			createCommercePriceListAccountRel(commercePriceListAccountRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListAccountRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce price list account rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRel the commerce price list account rel
	 * @return the commerce price list account rel that was removed
	 * @throws PortalException
	 */
	@Override
	public CommercePriceListAccountRel deleteCommercePriceListAccountRel(
			CommercePriceListAccountRel commercePriceListAccountRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListAccountRelLocalService.
			deleteCommercePriceListAccountRel(commercePriceListAccountRel);
	}

	/**
	 * Deletes the commerce price list account rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRelId the primary key of the commerce price list account rel
	 * @return the commerce price list account rel that was removed
	 * @throws PortalException if a commerce price list account rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListAccountRel deleteCommercePriceListAccountRel(
			long commercePriceListAccountRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListAccountRelLocalService.
			deleteCommercePriceListAccountRel(commercePriceListAccountRelId);
	}

	@Override
	public void deleteCommercePriceListAccountRels(long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListAccountRelLocalService.
			deleteCommercePriceListAccountRels(commercePriceListId);
	}

	@Override
	public void deleteCommercePriceListAccountRelsByCommercePriceListId(
			long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListAccountRelLocalService.
			deleteCommercePriceListAccountRelsByCommercePriceListId(
				commercePriceListId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListAccountRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePriceListAccountRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePriceListAccountRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePriceListAccountRelLocalService.dynamicQuery();
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

		return _commercePriceListAccountRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelModelImpl</code>.
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

		return _commercePriceListAccountRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelModelImpl</code>.
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

		return _commercePriceListAccountRelLocalService.dynamicQuery(
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

		return _commercePriceListAccountRelLocalService.dynamicQueryCount(
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

		return _commercePriceListAccountRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommercePriceListAccountRel fetchCommercePriceListAccountRel(
		long commercePriceListAccountRelId) {

		return _commercePriceListAccountRelLocalService.
			fetchCommercePriceListAccountRel(commercePriceListAccountRelId);
	}

	@Override
	public CommercePriceListAccountRel fetchCommercePriceListAccountRel(
		long commerceAccountId, long commercePriceListId) {

		return _commercePriceListAccountRelLocalService.
			fetchCommercePriceListAccountRel(
				commerceAccountId, commercePriceListId);
	}

	/**
	 * Returns the commerce price list account rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list account rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list account rel, or <code>null</code> if a matching commerce price list account rel could not be found
	 */
	@Override
	public CommercePriceListAccountRel
		fetchCommercePriceListAccountRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return _commercePriceListAccountRelLocalService.
			fetchCommercePriceListAccountRelByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePriceListAccountRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce price list account rel with the primary key.
	 *
	 * @param commercePriceListAccountRelId the primary key of the commerce price list account rel
	 * @return the commerce price list account rel
	 * @throws PortalException if a commerce price list account rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListAccountRel getCommercePriceListAccountRel(
			long commercePriceListAccountRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListAccountRelLocalService.
			getCommercePriceListAccountRel(commercePriceListAccountRelId);
	}

	/**
	 * Returns the commerce price list account rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list account rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list account rel
	 * @throws PortalException if a matching commerce price list account rel could not be found
	 */
	@Override
	public CommercePriceListAccountRel
			getCommercePriceListAccountRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListAccountRelLocalService.
			getCommercePriceListAccountRelByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce price list account rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price list account rels
	 * @param end the upper bound of the range of commerce price list account rels (not inclusive)
	 * @return the range of commerce price list account rels
	 */
	@Override
	public java.util.List<CommercePriceListAccountRel>
		getCommercePriceListAccountRels(int start, int end) {

		return _commercePriceListAccountRelLocalService.
			getCommercePriceListAccountRels(start, end);
	}

	@Override
	public java.util.List<CommercePriceListAccountRel>
		getCommercePriceListAccountRels(long commercePriceListId) {

		return _commercePriceListAccountRelLocalService.
			getCommercePriceListAccountRels(commercePriceListId);
	}

	@Override
	public java.util.List<CommercePriceListAccountRel>
		getCommercePriceListAccountRels(
			long commercePriceListId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommercePriceListAccountRel> orderByComparator) {

		return _commercePriceListAccountRelLocalService.
			getCommercePriceListAccountRels(
				commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CommercePriceListAccountRel>
		getCommercePriceListAccountRels(
			long commercePriceListId, String name, int start, int end) {

		return _commercePriceListAccountRelLocalService.
			getCommercePriceListAccountRels(
				commercePriceListId, name, start, end);
	}

	/**
	 * Returns the number of commerce price list account rels.
	 *
	 * @return the number of commerce price list account rels
	 */
	@Override
	public int getCommercePriceListAccountRelsCount() {
		return _commercePriceListAccountRelLocalService.
			getCommercePriceListAccountRelsCount();
	}

	@Override
	public int getCommercePriceListAccountRelsCount(long commercePriceListId) {
		return _commercePriceListAccountRelLocalService.
			getCommercePriceListAccountRelsCount(commercePriceListId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commercePriceListAccountRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePriceListAccountRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceListAccountRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListAccountRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce price list account rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRel the commerce price list account rel
	 * @return the commerce price list account rel that was updated
	 */
	@Override
	public CommercePriceListAccountRel updateCommercePriceListAccountRel(
		CommercePriceListAccountRel commercePriceListAccountRel) {

		return _commercePriceListAccountRelLocalService.
			updateCommercePriceListAccountRel(commercePriceListAccountRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePriceListAccountRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommercePriceListAccountRel> getCTPersistence() {
		return _commercePriceListAccountRelLocalService.getCTPersistence();
	}

	@Override
	public Class<CommercePriceListAccountRel> getModelClass() {
		return _commercePriceListAccountRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommercePriceListAccountRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commercePriceListAccountRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public CommercePriceListAccountRelLocalService getWrappedService() {
		return _commercePriceListAccountRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePriceListAccountRelLocalService
			commercePriceListAccountRelLocalService) {

		_commercePriceListAccountRelLocalService =
			commercePriceListAccountRelLocalService;
	}

	private CommercePriceListAccountRelLocalService
		_commercePriceListAccountRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-469876053