/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutPageTemplateCollectionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateCollectionLocalService
 * @generated
 */
public class LayoutPageTemplateCollectionLocalServiceWrapper
	implements LayoutPageTemplateCollectionLocalService,
			   ServiceWrapper<LayoutPageTemplateCollectionLocalService> {

	public LayoutPageTemplateCollectionLocalServiceWrapper() {
		this(null);
	}

	public LayoutPageTemplateCollectionLocalServiceWrapper(
		LayoutPageTemplateCollectionLocalService
			layoutPageTemplateCollectionLocalService) {

		_layoutPageTemplateCollectionLocalService =
			layoutPageTemplateCollectionLocalService;
	}

	/**
	 * Adds the layout page template collection to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateCollection the layout page template collection
	 * @return the layout page template collection that was added
	 */
	@Override
	public LayoutPageTemplateCollection addLayoutPageTemplateCollection(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		return _layoutPageTemplateCollectionLocalService.
			addLayoutPageTemplateCollection(layoutPageTemplateCollection);
	}

	@Override
	public LayoutPageTemplateCollection addLayoutPageTemplateCollection(
			String externalReferenceCode, long userId, long groupId,
			long parentLayoutPageTemplateCollectionId,
			String layoutPageTemplateCollectionKey, String name,
			String description, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.
			addLayoutPageTemplateCollection(
				externalReferenceCode, userId, groupId,
				parentLayoutPageTemplateCollectionId,
				layoutPageTemplateCollectionKey, name, description, type,
				serviceContext);
	}

	@Override
	public LayoutPageTemplateCollection copyLayoutPageTemplateCollection(
			long userId, long groupId,
			long sourceLayoutPageTemplateCollectionId,
			long layoutParentPageTemplateCollectionId, boolean copyPermissions,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return _layoutPageTemplateCollectionLocalService.
			copyLayoutPageTemplateCollection(
				userId, groupId, sourceLayoutPageTemplateCollectionId,
				layoutParentPageTemplateCollectionId, copyPermissions,
				serviceContext);
	}

	/**
	 * Creates a new layout page template collection with the primary key. Does not add the layout page template collection to the database.
	 *
	 * @param layoutPageTemplateCollectionId the primary key for the new layout page template collection
	 * @return the new layout page template collection
	 */
	@Override
	public LayoutPageTemplateCollection createLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId) {

		return _layoutPageTemplateCollectionLocalService.
			createLayoutPageTemplateCollection(layoutPageTemplateCollectionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the layout page template collection from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateCollection the layout page template collection
	 * @return the layout page template collection that was removed
	 * @throws PortalException
	 */
	@Override
	public LayoutPageTemplateCollection deleteLayoutPageTemplateCollection(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.
			deleteLayoutPageTemplateCollection(layoutPageTemplateCollection);
	}

	/**
	 * Deletes the layout page template collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection that was removed
	 * @throws PortalException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection deleteLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.
			deleteLayoutPageTemplateCollection(layoutPageTemplateCollectionId);
	}

	@Override
	public LayoutPageTemplateCollection deleteLayoutPageTemplateCollection(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.
			deleteLayoutPageTemplateCollection(externalReferenceCode, groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutPageTemplateCollectionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutPageTemplateCollectionLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutPageTemplateCollectionLocalService.dynamicQuery();
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

		return _layoutPageTemplateCollectionLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
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

		return _layoutPageTemplateCollectionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
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

		return _layoutPageTemplateCollectionLocalService.dynamicQuery(
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

		return _layoutPageTemplateCollectionLocalService.dynamicQueryCount(
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

		return _layoutPageTemplateCollectionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public LayoutPageTemplateCollection fetchLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId) {

		return _layoutPageTemplateCollectionLocalService.
			fetchLayoutPageTemplateCollection(layoutPageTemplateCollectionId);
	}

	@Override
	public LayoutPageTemplateCollection fetchLayoutPageTemplateCollection(
		long groupId, String layoutPageTemplateCollectionKey, int type) {

		return _layoutPageTemplateCollectionLocalService.
			fetchLayoutPageTemplateCollection(
				groupId, layoutPageTemplateCollectionKey, type);
	}

	@Override
	public LayoutPageTemplateCollection fetchLayoutPageTemplateCollection(
		long groupId, String name, long parentLayoutPageTemplateCollectionId,
		int type) {

		return _layoutPageTemplateCollectionLocalService.
			fetchLayoutPageTemplateCollection(
				groupId, name, parentLayoutPageTemplateCollectionId, type);
	}

	@Override
	public LayoutPageTemplateCollection
		fetchLayoutPageTemplateCollectionByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return _layoutPageTemplateCollectionLocalService.
			fetchLayoutPageTemplateCollectionByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template collection matching the UUID and group.
	 *
	 * @param uuid the layout page template collection's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection
		fetchLayoutPageTemplateCollectionByUuidAndGroupId(
			String uuid, long groupId) {

		return _layoutPageTemplateCollectionLocalService.
			fetchLayoutPageTemplateCollectionByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutPageTemplateCollectionLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _layoutPageTemplateCollectionLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutPageTemplateCollectionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout page template collection with the primary key.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection
	 * @throws PortalException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection getLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollection(layoutPageTemplateCollectionId);
	}

	@Override
	public LayoutPageTemplateCollection
			getLayoutPageTemplateCollectionByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollectionByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template collection matching the UUID and group.
	 *
	 * @param uuid the layout page template collection's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout page template collection
	 * @throws PortalException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection
			getLayoutPageTemplateCollectionByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollectionByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the layout page template collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of layout page template collections
	 */
	@Override
	public java.util.List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(int start, int end) {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollections(start, end);
	}

	@Override
	public java.util.List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(long groupId) {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollections(groupId);
	}

	@Override
	public java.util.List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(
			long groupId, int type, int start, int end) {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollections(groupId, type, start, end);
	}

	@Override
	public java.util.List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(
			long groupId, int type, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateCollection> orderByComparator) {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollections(
				groupId, type, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(
			long groupId, long layoutPageTemplateCollectionId) {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollections(
				groupId, layoutPageTemplateCollectionId);
	}

	@Override
	public java.util.List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(
			long groupId, long layoutPageTemplateCollectionId, int type) {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollections(
				groupId, layoutPageTemplateCollectionId, type);
	}

	@Override
	public java.util.List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(
			long groupId, String name, int type, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateCollection> orderByComparator) {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollections(
				groupId, name, type, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template collections matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template collections
	 * @param companyId the primary key of the company
	 * @return the matching layout page template collections, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollectionsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollectionsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of layout page template collections matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template collections
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout page template collections, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollectionsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateCollection> orderByComparator) {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollectionsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout page template collections.
	 *
	 * @return the number of layout page template collections
	 */
	@Override
	public int getLayoutPageTemplateCollectionsCount() {
		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollectionsCount();
	}

	@Override
	public int getLayoutPageTemplateCollectionsCount(long groupId, int type) {
		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollectionsCount(groupId, type);
	}

	@Override
	public int getLayoutPageTemplateCollectionsCount(
		long groupId, String name, int type) {

		return _layoutPageTemplateCollectionLocalService.
			getLayoutPageTemplateCollectionsCount(groupId, name, type);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutPageTemplateCollectionLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public String getUniqueLayoutPageTemplateCollectionName(
			long groupId, long layoutPageTemplateCollectionId,
			String sourceName, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.
			getUniqueLayoutPageTemplateCollectionName(
				groupId, layoutPageTemplateCollectionId, sourceName, type);
	}

	@Override
	public LayoutPageTemplateCollection moveLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId,
			long parentLayoutPageTemplateCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.
			moveLayoutPageTemplateCollection(
				layoutPageTemplateCollectionId,
				parentLayoutPageTemplateCollectionId);
	}

	/**
	 * Updates the layout page template collection in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutPageTemplateCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutPageTemplateCollection the layout page template collection
	 * @return the layout page template collection that was updated
	 */
	@Override
	public LayoutPageTemplateCollection updateLayoutPageTemplateCollection(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		return _layoutPageTemplateCollectionLocalService.
			updateLayoutPageTemplateCollection(layoutPageTemplateCollection);
	}

	@Override
	public LayoutPageTemplateCollection updateLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.
			updateLayoutPageTemplateCollection(
				layoutPageTemplateCollectionId, name);
	}

	@Override
	public LayoutPageTemplateCollection updateLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId, String name,
			String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateCollectionLocalService.
			updateLayoutPageTemplateCollection(
				layoutPageTemplateCollectionId, name, description);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutPageTemplateCollectionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<LayoutPageTemplateCollection> getCTPersistence() {
		return _layoutPageTemplateCollectionLocalService.getCTPersistence();
	}

	@Override
	public Class<LayoutPageTemplateCollection> getModelClass() {
		return _layoutPageTemplateCollectionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutPageTemplateCollection>, R, E>
				updateUnsafeFunction)
		throws E {

		return _layoutPageTemplateCollectionLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public LayoutPageTemplateCollectionLocalService getWrappedService() {
		return _layoutPageTemplateCollectionLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutPageTemplateCollectionLocalService
			layoutPageTemplateCollectionLocalService) {

		_layoutPageTemplateCollectionLocalService =
			layoutPageTemplateCollectionLocalService;
	}

	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

}