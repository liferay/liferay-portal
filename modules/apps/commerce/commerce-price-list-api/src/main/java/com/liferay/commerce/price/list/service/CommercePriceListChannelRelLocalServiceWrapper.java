/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceListChannelRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommercePriceListChannelRelLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListChannelRelLocalService
 * @generated
 */
public class CommercePriceListChannelRelLocalServiceWrapper
	implements CommercePriceListChannelRelLocalService,
			   ServiceWrapper<CommercePriceListChannelRelLocalService> {

	public CommercePriceListChannelRelLocalServiceWrapper() {
		this(null);
	}

	public CommercePriceListChannelRelLocalServiceWrapper(
		CommercePriceListChannelRelLocalService
			commercePriceListChannelRelLocalService) {

		_commercePriceListChannelRelLocalService =
			commercePriceListChannelRelLocalService;
	}

	/**
	 * Adds the commerce price list channel rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListChannelRel the commerce price list channel rel
	 * @return the commerce price list channel rel that was added
	 */
	@Override
	public CommercePriceListChannelRel addCommercePriceListChannelRel(
		CommercePriceListChannelRel commercePriceListChannelRel) {

		return _commercePriceListChannelRelLocalService.
			addCommercePriceListChannelRel(commercePriceListChannelRel);
	}

	@Override
	public CommercePriceListChannelRel addCommercePriceListChannelRel(
			long userId, long commercePriceListId, long commerceChannelId,
			int order,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListChannelRelLocalService.
			addCommercePriceListChannelRel(
				userId, commercePriceListId, commerceChannelId, order,
				serviceContext);
	}

	/**
	 * Creates a new commerce price list channel rel with the primary key. Does not add the commerce price list channel rel to the database.
	 *
	 * @param CommercePriceListChannelRelId the primary key for the new commerce price list channel rel
	 * @return the new commerce price list channel rel
	 */
	@Override
	public CommercePriceListChannelRel createCommercePriceListChannelRel(
		long CommercePriceListChannelRelId) {

		return _commercePriceListChannelRelLocalService.
			createCommercePriceListChannelRel(CommercePriceListChannelRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListChannelRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce price list channel rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListChannelRel the commerce price list channel rel
	 * @return the commerce price list channel rel that was removed
	 * @throws PortalException
	 */
	@Override
	public CommercePriceListChannelRel deleteCommercePriceListChannelRel(
			CommercePriceListChannelRel commercePriceListChannelRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListChannelRelLocalService.
			deleteCommercePriceListChannelRel(commercePriceListChannelRel);
	}

	/**
	 * Deletes the commerce price list channel rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CommercePriceListChannelRelId the primary key of the commerce price list channel rel
	 * @return the commerce price list channel rel that was removed
	 * @throws PortalException if a commerce price list channel rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListChannelRel deleteCommercePriceListChannelRel(
			long CommercePriceListChannelRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListChannelRelLocalService.
			deleteCommercePriceListChannelRel(CommercePriceListChannelRelId);
	}

	@Override
	public void deleteCommercePriceListChannelRels(long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListChannelRelLocalService.
			deleteCommercePriceListChannelRels(commercePriceListId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListChannelRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePriceListChannelRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePriceListChannelRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePriceListChannelRelLocalService.dynamicQuery();
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

		return _commercePriceListChannelRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListChannelRelModelImpl</code>.
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

		return _commercePriceListChannelRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListChannelRelModelImpl</code>.
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

		return _commercePriceListChannelRelLocalService.dynamicQuery(
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

		return _commercePriceListChannelRelLocalService.dynamicQueryCount(
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

		return _commercePriceListChannelRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommercePriceListChannelRel fetchCommercePriceListChannelRel(
		long CommercePriceListChannelRelId) {

		return _commercePriceListChannelRelLocalService.
			fetchCommercePriceListChannelRel(CommercePriceListChannelRelId);
	}

	@Override
	public CommercePriceListChannelRel fetchCommercePriceListChannelRel(
		long commerceChannelId, long commercePriceListId) {

		return _commercePriceListChannelRelLocalService.
			fetchCommercePriceListChannelRel(
				commerceChannelId, commercePriceListId);
	}

	/**
	 * Returns the commerce price list channel rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list channel rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list channel rel, or <code>null</code> if a matching commerce price list channel rel could not be found
	 */
	@Override
	public CommercePriceListChannelRel
		fetchCommercePriceListChannelRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return _commercePriceListChannelRelLocalService.
			fetchCommercePriceListChannelRelByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePriceListChannelRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce price list channel rel with the primary key.
	 *
	 * @param CommercePriceListChannelRelId the primary key of the commerce price list channel rel
	 * @return the commerce price list channel rel
	 * @throws PortalException if a commerce price list channel rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListChannelRel getCommercePriceListChannelRel(
			long CommercePriceListChannelRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListChannelRelLocalService.
			getCommercePriceListChannelRel(CommercePriceListChannelRelId);
	}

	/**
	 * Returns the commerce price list channel rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list channel rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list channel rel
	 * @throws PortalException if a matching commerce price list channel rel could not be found
	 */
	@Override
	public CommercePriceListChannelRel
			getCommercePriceListChannelRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListChannelRelLocalService.
			getCommercePriceListChannelRelByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce price list channel rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListChannelRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price list channel rels
	 * @param end the upper bound of the range of commerce price list channel rels (not inclusive)
	 * @return the range of commerce price list channel rels
	 */
	@Override
	public java.util.List<CommercePriceListChannelRel>
		getCommercePriceListChannelRels(int start, int end) {

		return _commercePriceListChannelRelLocalService.
			getCommercePriceListChannelRels(start, end);
	}

	@Override
	public java.util.List<CommercePriceListChannelRel>
		getCommercePriceListChannelRels(long commercePriceListId) {

		return _commercePriceListChannelRelLocalService.
			getCommercePriceListChannelRels(commercePriceListId);
	}

	@Override
	public java.util.List<CommercePriceListChannelRel>
		getCommercePriceListChannelRels(
			long commercePriceListId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommercePriceListChannelRel> orderByComparator) {

		return _commercePriceListChannelRelLocalService.
			getCommercePriceListChannelRels(
				commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CommercePriceListChannelRel>
		getCommercePriceListChannelRels(
			long commercePriceListId, String name, int start, int end) {

		return _commercePriceListChannelRelLocalService.
			getCommercePriceListChannelRels(
				commercePriceListId, name, start, end);
	}

	/**
	 * Returns the number of commerce price list channel rels.
	 *
	 * @return the number of commerce price list channel rels
	 */
	@Override
	public int getCommercePriceListChannelRelsCount() {
		return _commercePriceListChannelRelLocalService.
			getCommercePriceListChannelRelsCount();
	}

	@Override
	public int getCommercePriceListChannelRelsCount(long commercePriceListId) {
		return _commercePriceListChannelRelLocalService.
			getCommercePriceListChannelRelsCount(commercePriceListId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commercePriceListChannelRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePriceListChannelRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceListChannelRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListChannelRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce price list channel rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListChannelRel the commerce price list channel rel
	 * @return the commerce price list channel rel that was updated
	 */
	@Override
	public CommercePriceListChannelRel updateCommercePriceListChannelRel(
		CommercePriceListChannelRel commercePriceListChannelRel) {

		return _commercePriceListChannelRelLocalService.
			updateCommercePriceListChannelRel(commercePriceListChannelRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePriceListChannelRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommercePriceListChannelRel> getCTPersistence() {
		return _commercePriceListChannelRelLocalService.getCTPersistence();
	}

	@Override
	public Class<CommercePriceListChannelRel> getModelClass() {
		return _commercePriceListChannelRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommercePriceListChannelRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commercePriceListChannelRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public CommercePriceListChannelRelLocalService getWrappedService() {
		return _commercePriceListChannelRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePriceListChannelRelLocalService
			commercePriceListChannelRelLocalService) {

		_commercePriceListChannelRelLocalService =
			commercePriceListChannelRelLocalService;
	}

	private CommercePriceListChannelRelLocalService
		_commercePriceListChannelRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-913894364