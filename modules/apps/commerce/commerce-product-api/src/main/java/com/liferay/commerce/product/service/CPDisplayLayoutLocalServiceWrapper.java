/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPDisplayLayoutLocalService}.
 *
 * @author Marco Leo
 * @see CPDisplayLayoutLocalService
 * @generated
 */
public class CPDisplayLayoutLocalServiceWrapper
	implements CPDisplayLayoutLocalService,
			   ServiceWrapper<CPDisplayLayoutLocalService> {

	public CPDisplayLayoutLocalServiceWrapper() {
		this(null);
	}

	public CPDisplayLayoutLocalServiceWrapper(
		CPDisplayLayoutLocalService cpDisplayLayoutLocalService) {

		_cpDisplayLayoutLocalService = cpDisplayLayoutLocalService;
	}

	/**
	 * Adds the cp display layout to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDisplayLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDisplayLayout the cp display layout
	 * @return the cp display layout that was added
	 */
	@Override
	public CPDisplayLayout addCPDisplayLayout(CPDisplayLayout cpDisplayLayout) {
		return _cpDisplayLayoutLocalService.addCPDisplayLayout(cpDisplayLayout);
	}

	@Override
	public CPDisplayLayout addCPDisplayLayout(
			long userId, long groupId, Class<?> clazz, long classPK,
			String layoutPageTemplateEntryUuid, String layoutUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDisplayLayoutLocalService.addCPDisplayLayout(
			userId, groupId, clazz, classPK, layoutPageTemplateEntryUuid,
			layoutUuid);
	}

	/**
	 * Creates a new cp display layout with the primary key. Does not add the cp display layout to the database.
	 *
	 * @param CPDisplayLayoutId the primary key for the new cp display layout
	 * @return the new cp display layout
	 */
	@Override
	public CPDisplayLayout createCPDisplayLayout(long CPDisplayLayoutId) {
		return _cpDisplayLayoutLocalService.createCPDisplayLayout(
			CPDisplayLayoutId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDisplayLayoutLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the cp display layout from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDisplayLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDisplayLayout the cp display layout
	 * @return the cp display layout that was removed
	 */
	@Override
	public CPDisplayLayout deleteCPDisplayLayout(
		CPDisplayLayout cpDisplayLayout) {

		return _cpDisplayLayoutLocalService.deleteCPDisplayLayout(
			cpDisplayLayout);
	}

	/**
	 * Deletes the cp display layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDisplayLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDisplayLayoutId the primary key of the cp display layout
	 * @return the cp display layout that was removed
	 * @throws PortalException if a cp display layout with the primary key could not be found
	 */
	@Override
	public CPDisplayLayout deleteCPDisplayLayout(long CPDisplayLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDisplayLayoutLocalService.deleteCPDisplayLayout(
			CPDisplayLayoutId);
	}

	@Override
	public void deleteCPDisplayLayouts(Class<?> clazz, long classPK) {
		_cpDisplayLayoutLocalService.deleteCPDisplayLayouts(clazz, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDisplayLayoutLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpDisplayLayoutLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpDisplayLayoutLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpDisplayLayoutLocalService.dynamicQuery();
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

		return _cpDisplayLayoutLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDisplayLayoutModelImpl</code>.
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

		return _cpDisplayLayoutLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDisplayLayoutModelImpl</code>.
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

		return _cpDisplayLayoutLocalService.dynamicQuery(
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

		return _cpDisplayLayoutLocalService.dynamicQueryCount(dynamicQuery);
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

		return _cpDisplayLayoutLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPDisplayLayout fetchCPDisplayLayout(long CPDisplayLayoutId) {
		return _cpDisplayLayoutLocalService.fetchCPDisplayLayout(
			CPDisplayLayoutId);
	}

	@Override
	public CPDisplayLayout fetchCPDisplayLayout(
		long groupId, Class<?> clazz, long classPK) {

		return _cpDisplayLayoutLocalService.fetchCPDisplayLayout(
			groupId, clazz, classPK);
	}

	/**
	 * Returns the cp display layout matching the UUID and group.
	 *
	 * @param uuid the cp display layout's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchCPDisplayLayoutByUuidAndGroupId(
		String uuid, long groupId) {

		return _cpDisplayLayoutLocalService.
			fetchCPDisplayLayoutByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpDisplayLayoutLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cp display layout with the primary key.
	 *
	 * @param CPDisplayLayoutId the primary key of the cp display layout
	 * @return the cp display layout
	 * @throws PortalException if a cp display layout with the primary key could not be found
	 */
	@Override
	public CPDisplayLayout getCPDisplayLayout(long CPDisplayLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDisplayLayoutLocalService.getCPDisplayLayout(
			CPDisplayLayoutId);
	}

	/**
	 * Returns the cp display layout matching the UUID and group.
	 *
	 * @param uuid the cp display layout's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp display layout
	 * @throws PortalException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout getCPDisplayLayoutByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDisplayLayoutLocalService.getCPDisplayLayoutByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the cp display layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDisplayLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp display layouts
	 * @param end the upper bound of the range of cp display layouts (not inclusive)
	 * @return the range of cp display layouts
	 */
	@Override
	public java.util.List<CPDisplayLayout> getCPDisplayLayouts(
		int start, int end) {

		return _cpDisplayLayoutLocalService.getCPDisplayLayouts(start, end);
	}

	@Override
	public java.util.List<CPDisplayLayout>
		getCPDisplayLayoutsByGroupIdAndLayoutPageTemplateEntryUuid(
			long groupId, String layoutPageTemplateEntryUuid) {

		return _cpDisplayLayoutLocalService.
			getCPDisplayLayoutsByGroupIdAndLayoutPageTemplateEntryUuid(
				groupId, layoutPageTemplateEntryUuid);
	}

	@Override
	public java.util.List<CPDisplayLayout>
		getCPDisplayLayoutsByGroupIdAndLayoutUuid(
			long groupId, String layoutUuid) {

		return _cpDisplayLayoutLocalService.
			getCPDisplayLayoutsByGroupIdAndLayoutUuid(groupId, layoutUuid);
	}

	/**
	 * Returns all the cp display layouts matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp display layouts
	 * @param companyId the primary key of the company
	 * @return the matching cp display layouts, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPDisplayLayout>
		getCPDisplayLayoutsByUuidAndCompanyId(String uuid, long companyId) {

		return _cpDisplayLayoutLocalService.
			getCPDisplayLayoutsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of cp display layouts matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp display layouts
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp display layouts
	 * @param end the upper bound of the range of cp display layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp display layouts, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPDisplayLayout>
		getCPDisplayLayoutsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CPDisplayLayout>
				orderByComparator) {

		return _cpDisplayLayoutLocalService.
			getCPDisplayLayoutsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp display layouts.
	 *
	 * @return the number of cp display layouts
	 */
	@Override
	public int getCPDisplayLayoutsCount() {
		return _cpDisplayLayoutLocalService.getCPDisplayLayoutsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpDisplayLayoutLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpDisplayLayoutLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpDisplayLayoutLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDisplayLayoutLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDisplayLayout> searchCPDisplayLayout(
				long companyId, long groupId, String className, Integer type,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDisplayLayoutLocalService.searchCPDisplayLayout(
			companyId, groupId, className, type, keywords, start, end, sort);
	}

	/**
	 * Updates the cp display layout in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDisplayLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDisplayLayout the cp display layout
	 * @return the cp display layout that was updated
	 */
	@Override
	public CPDisplayLayout updateCPDisplayLayout(
		CPDisplayLayout cpDisplayLayout) {

		return _cpDisplayLayoutLocalService.updateCPDisplayLayout(
			cpDisplayLayout);
	}

	@Override
	public CPDisplayLayout updateCPDisplayLayout(
			long cpDisplayLayoutId, long classPK,
			String layoutPageTemplateEntryUuid, String layoutUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDisplayLayoutLocalService.updateCPDisplayLayout(
			cpDisplayLayoutId, classPK, layoutPageTemplateEntryUuid,
			layoutUuid);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpDisplayLayoutLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPDisplayLayout> getCTPersistence() {
		return _cpDisplayLayoutLocalService.getCTPersistence();
	}

	@Override
	public Class<CPDisplayLayout> getModelClass() {
		return _cpDisplayLayoutLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPDisplayLayout>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpDisplayLayoutLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPDisplayLayoutLocalService getWrappedService() {
		return _cpDisplayLayoutLocalService;
	}

	@Override
	public void setWrappedService(
		CPDisplayLayoutLocalService cpDisplayLayoutLocalService) {

		_cpDisplayLayoutLocalService = cpDisplayLayoutLocalService;
	}

	private CPDisplayLayoutLocalService _cpDisplayLayoutLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-616923608