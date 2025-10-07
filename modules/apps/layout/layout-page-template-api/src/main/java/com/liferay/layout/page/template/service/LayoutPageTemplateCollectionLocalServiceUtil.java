/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for LayoutPageTemplateCollection. This utility wraps
 * <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateCollectionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateCollectionLocalService
 * @generated
 */
public class LayoutPageTemplateCollectionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateCollectionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static LayoutPageTemplateCollection addLayoutPageTemplateCollection(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		return getService().addLayoutPageTemplateCollection(
			layoutPageTemplateCollection);
	}

	public static LayoutPageTemplateCollection addLayoutPageTemplateCollection(
			String externalReferenceCode, long userId, long groupId,
			long parentLayoutPageTemplateCollectionId,
			String layoutPageTemplateCollectionKey, String name,
			String description, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutPageTemplateCollection(
			externalReferenceCode, userId, groupId,
			parentLayoutPageTemplateCollectionId,
			layoutPageTemplateCollectionKey, name, description, type,
			serviceContext);
	}

	public static LayoutPageTemplateCollection copyLayoutPageTemplateCollection(
			long userId, long groupId,
			long sourceLayoutPageTemplateCollectionId,
			long layoutParentPageTemplateCollectionId, boolean copyPermissions,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return getService().copyLayoutPageTemplateCollection(
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
	public static LayoutPageTemplateCollection
		createLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId) {

		return getService().createLayoutPageTemplateCollection(
			layoutPageTemplateCollectionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static LayoutPageTemplateCollection
			deleteLayoutPageTemplateCollection(
				LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws PortalException {

		return getService().deleteLayoutPageTemplateCollection(
			layoutPageTemplateCollection);
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
	public static LayoutPageTemplateCollection
			deleteLayoutPageTemplateCollection(
				long layoutPageTemplateCollectionId)
		throws PortalException {

		return getService().deleteLayoutPageTemplateCollection(
			layoutPageTemplateCollectionId);
	}

	public static LayoutPageTemplateCollection
			deleteLayoutPageTemplateCollection(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteLayoutPageTemplateCollection(
			externalReferenceCode, groupId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static LayoutPageTemplateCollection
		fetchLayoutPageTemplateCollection(long layoutPageTemplateCollectionId) {

		return getService().fetchLayoutPageTemplateCollection(
			layoutPageTemplateCollectionId);
	}

	public static LayoutPageTemplateCollection
		fetchLayoutPageTemplateCollection(
			long groupId, String layoutPageTemplateCollectionKey, int type) {

		return getService().fetchLayoutPageTemplateCollection(
			groupId, layoutPageTemplateCollectionKey, type);
	}

	public static LayoutPageTemplateCollection
		fetchLayoutPageTemplateCollection(
			long groupId, String name,
			long parentLayoutPageTemplateCollectionId, int type) {

		return getService().fetchLayoutPageTemplateCollection(
			groupId, name, parentLayoutPageTemplateCollectionId, type);
	}

	public static LayoutPageTemplateCollection
		fetchLayoutPageTemplateCollectionByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().
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
	public static LayoutPageTemplateCollection
		fetchLayoutPageTemplateCollectionByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchLayoutPageTemplateCollectionByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout page template collection with the primary key.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection
	 * @throws PortalException if a layout page template collection with the primary key could not be found
	 */
	public static LayoutPageTemplateCollection getLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId)
		throws PortalException {

		return getService().getLayoutPageTemplateCollection(
			layoutPageTemplateCollectionId);
	}

	public static LayoutPageTemplateCollection
			getLayoutPageTemplateCollectionByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().
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
	public static LayoutPageTemplateCollection
			getLayoutPageTemplateCollectionByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getLayoutPageTemplateCollectionByUuidAndGroupId(
			uuid, groupId);
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
	public static List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(int start, int end) {

		return getService().getLayoutPageTemplateCollections(start, end);
	}

	public static List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(long groupId) {

		return getService().getLayoutPageTemplateCollections(groupId);
	}

	public static List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(
			long groupId, int type, int start, int end) {

		return getService().getLayoutPageTemplateCollections(
			groupId, type, start, end);
	}

	public static List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(
			long groupId, int type, int start, int end,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getService().getLayoutPageTemplateCollections(
			groupId, type, start, end, orderByComparator);
	}

	public static List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(
			long groupId, long layoutPageTemplateCollectionId) {

		return getService().getLayoutPageTemplateCollections(
			groupId, layoutPageTemplateCollectionId);
	}

	public static List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(
			long groupId, long layoutPageTemplateCollectionId, int type) {

		return getService().getLayoutPageTemplateCollections(
			groupId, layoutPageTemplateCollectionId, type);
	}

	public static List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections(
			long groupId, String name, int type, int start, int end,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getService().getLayoutPageTemplateCollections(
			groupId, name, type, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template collections matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout page template collections
	 * @param companyId the primary key of the company
	 * @return the matching layout page template collections, or an empty list if no matches were found
	 */
	public static List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollectionsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getLayoutPageTemplateCollectionsByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollectionsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getService().getLayoutPageTemplateCollectionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout page template collections.
	 *
	 * @return the number of layout page template collections
	 */
	public static int getLayoutPageTemplateCollectionsCount() {
		return getService().getLayoutPageTemplateCollectionsCount();
	}

	public static int getLayoutPageTemplateCollectionsCount(
		long groupId, int type) {

		return getService().getLayoutPageTemplateCollectionsCount(
			groupId, type);
	}

	public static int getLayoutPageTemplateCollectionsCount(
		long groupId, String name, int type) {

		return getService().getLayoutPageTemplateCollectionsCount(
			groupId, name, type);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static String getUniqueLayoutPageTemplateCollectionName(
			long groupId, long layoutPageTemplateCollectionId,
			String sourceName, int type)
		throws PortalException {

		return getService().getUniqueLayoutPageTemplateCollectionName(
			groupId, layoutPageTemplateCollectionId, sourceName, type);
	}

	public static LayoutPageTemplateCollection moveLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId,
			long parentLayoutPageTemplateCollectionId)
		throws PortalException {

		return getService().moveLayoutPageTemplateCollection(
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
	public static LayoutPageTemplateCollection
		updateLayoutPageTemplateCollection(
			LayoutPageTemplateCollection layoutPageTemplateCollection) {

		return getService().updateLayoutPageTemplateCollection(
			layoutPageTemplateCollection);
	}

	public static LayoutPageTemplateCollection
			updateLayoutPageTemplateCollection(
				long layoutPageTemplateCollectionId, String name)
		throws PortalException {

		return getService().updateLayoutPageTemplateCollection(
			layoutPageTemplateCollectionId, name);
	}

	public static LayoutPageTemplateCollection
			updateLayoutPageTemplateCollection(
				long layoutPageTemplateCollectionId, String name,
				String description)
		throws PortalException {

		return getService().updateLayoutPageTemplateCollection(
			layoutPageTemplateCollectionId, name, description);
	}

	public static LayoutPageTemplateCollectionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<LayoutPageTemplateCollectionLocalService>
		_serviceSnapshot = new Snapshot<>(
			LayoutPageTemplateCollectionLocalServiceUtil.class,
			LayoutPageTemplateCollectionLocalService.class);

}