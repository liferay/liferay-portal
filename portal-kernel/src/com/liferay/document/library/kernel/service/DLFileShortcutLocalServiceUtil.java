/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DLFileShortcut. This utility wraps
 * <code>com.liferay.portlet.documentlibrary.service.impl.DLFileShortcutLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileShortcutLocalService
 * @generated
 */
public class DLFileShortcutLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLFileShortcutLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the document library file shortcut to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileShortcutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileShortcut the document library file shortcut
	 * @return the document library file shortcut that was added
	 */
	public static DLFileShortcut addDLFileShortcut(
		DLFileShortcut dlFileShortcut) {

		return getService().addDLFileShortcut(dlFileShortcut);
	}

	public static DLFileShortcut addFileShortcut(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, long folderId, long toFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFileShortcut(
			externalReferenceCode, userId, groupId, repositoryId, folderId,
			toFileEntryId, serviceContext);
	}

	public static void addFileShortcutResources(
			DLFileShortcut fileShortcut, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addFileShortcutResources(
			fileShortcut, addGroupPermissions, addGuestPermissions);
	}

	public static void addFileShortcutResources(
			DLFileShortcut fileShortcut,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addFileShortcutResources(fileShortcut, modelPermissions);
	}

	public static void addFileShortcutResources(
			long fileShortcutId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addFileShortcutResources(
			fileShortcutId, addGroupPermissions, addGuestPermissions);
	}

	public static void addFileShortcutResources(
			long fileShortcutId,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addFileShortcutResources(fileShortcutId, modelPermissions);
	}

	/**
	 * Creates a new document library file shortcut with the primary key. Does not add the document library file shortcut to the database.
	 *
	 * @param fileShortcutId the primary key for the new document library file shortcut
	 * @return the new document library file shortcut
	 */
	public static DLFileShortcut createDLFileShortcut(long fileShortcutId) {
		return getService().createDLFileShortcut(fileShortcutId);
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
	 * Deletes the document library file shortcut from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileShortcutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileShortcut the document library file shortcut
	 * @return the document library file shortcut that was removed
	 */
	public static DLFileShortcut deleteDLFileShortcut(
		DLFileShortcut dlFileShortcut) {

		return getService().deleteDLFileShortcut(dlFileShortcut);
	}

	/**
	 * Deletes the document library file shortcut with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileShortcutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fileShortcutId the primary key of the document library file shortcut
	 * @return the document library file shortcut that was removed
	 * @throws PortalException if a document library file shortcut with the primary key could not be found
	 */
	public static DLFileShortcut deleteDLFileShortcut(long fileShortcutId)
		throws PortalException {

		return getService().deleteDLFileShortcut(fileShortcutId);
	}

	public static void deleteFileShortcut(DLFileShortcut fileShortcut)
		throws PortalException {

		getService().deleteFileShortcut(fileShortcut);
	}

	public static void deleteFileShortcut(long fileShortcutId)
		throws PortalException {

		getService().deleteFileShortcut(fileShortcutId);
	}

	public static void deleteFileShortcut(
			String externalReferenceCode, long groupId)
		throws PortalException {

		getService().deleteFileShortcut(externalReferenceCode, groupId);
	}

	public static void deleteFileShortcuts(long toFileEntryId)
		throws PortalException {

		getService().deleteFileShortcuts(toFileEntryId);
	}

	public static void deleteFileShortcuts(long groupId, long folderId)
		throws PortalException {

		getService().deleteFileShortcuts(groupId, folderId);
	}

	public static void deleteFileShortcuts(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws PortalException {

		getService().deleteFileShortcuts(
			groupId, folderId, includeTrashedEntries);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteRepositoryFileShortcuts(long repositoryId)
		throws PortalException {

		getService().deleteRepositoryFileShortcuts(repositoryId);
	}

	public static void disableFileShortcuts(long toFileEntryId) {
		getService().disableFileShortcuts(toFileEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileShortcutModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileShortcutModelImpl</code>.
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

	public static void enableFileShortcuts(long toFileEntryId) {
		getService().enableFileShortcuts(toFileEntryId);
	}

	public static DLFileShortcut fetchDLFileShortcut(long fileShortcutId) {
		return getService().fetchDLFileShortcut(fileShortcutId);
	}

	public static DLFileShortcut fetchDLFileShortcutByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchDLFileShortcutByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library file shortcut matching the UUID and group.
	 *
	 * @param uuid the document library file shortcut's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	public static DLFileShortcut fetchDLFileShortcutByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDLFileShortcutByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the document library file shortcut with the primary key.
	 *
	 * @param fileShortcutId the primary key of the document library file shortcut
	 * @return the document library file shortcut
	 * @throws PortalException if a document library file shortcut with the primary key could not be found
	 */
	public static DLFileShortcut getDLFileShortcut(long fileShortcutId)
		throws PortalException {

		return getService().getDLFileShortcut(fileShortcutId);
	}

	public static DLFileShortcut getDLFileShortcutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getDLFileShortcutByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library file shortcut matching the UUID and group.
	 *
	 * @param uuid the document library file shortcut's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file shortcut
	 * @throws PortalException if a matching document library file shortcut could not be found
	 */
	public static DLFileShortcut getDLFileShortcutByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDLFileShortcutByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the document library file shortcuts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of document library file shortcuts
	 */
	public static List<DLFileShortcut> getDLFileShortcuts(int start, int end) {
		return getService().getDLFileShortcuts(start, end);
	}

	/**
	 * Returns all the document library file shortcuts matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file shortcuts
	 * @param companyId the primary key of the company
	 * @return the matching document library file shortcuts, or an empty list if no matches were found
	 */
	public static List<DLFileShortcut> getDLFileShortcutsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDLFileShortcutsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of document library file shortcuts matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file shortcuts
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching document library file shortcuts, or an empty list if no matches were found
	 */
	public static List<DLFileShortcut> getDLFileShortcutsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return getService().getDLFileShortcutsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library file shortcuts.
	 *
	 * @return the number of document library file shortcuts
	 */
	public static int getDLFileShortcutsCount() {
		return getService().getDLFileShortcutsCount();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static DLFileShortcut getFileShortcut(long fileShortcutId)
		throws PortalException {

		return getService().getFileShortcut(fileShortcutId);
	}

	public static List<DLFileShortcut> getFileShortcuts(long toFileEntryId) {
		return getService().getFileShortcuts(toFileEntryId);
	}

	public static List<DLFileShortcut> getFileShortcuts(
		long groupId, long folderId) {

		return getService().getFileShortcuts(groupId, folderId);
	}

	public static List<DLFileShortcut> getGroupFileShortcuts(long groupId) {
		return getService().getGroupFileShortcuts(groupId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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

	public static void rebuildTree(long companyId) throws PortalException {
		getService().rebuildTree(companyId);
	}

	public static void setTreePaths(long folderId, String treePath)
		throws PortalException {

		getService().setTreePaths(folderId, treePath);
	}

	public static void updateAsset(
			long userId, DLFileShortcut fileShortcut, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException {

		getService().updateAsset(
			userId, fileShortcut, assetCategoryIds, assetTagNames);
	}

	/**
	 * Updates the document library file shortcut in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileShortcutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileShortcut the document library file shortcut
	 * @return the document library file shortcut that was updated
	 */
	public static DLFileShortcut updateDLFileShortcut(
		DLFileShortcut dlFileShortcut) {

		return getService().updateDLFileShortcut(dlFileShortcut);
	}

	public static DLFileShortcut updateFileShortcut(
			long userId, long fileShortcutId, long repositoryId, long folderId,
			long toFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFileShortcut(
			userId, fileShortcutId, repositoryId, folderId, toFileEntryId,
			serviceContext);
	}

	public static void updateFileShortcuts(
		long oldToFileEntryId, long newToFileEntryId) {

		getService().updateFileShortcuts(oldToFileEntryId, newToFileEntryId);
	}

	public static void updateFileShortcutsActive(
		long toFileEntryId, boolean active) {

		getService().updateFileShortcutsActive(toFileEntryId, active);
	}

	public static DLFileShortcut updateStatus(
			long userId, long fileShortcutId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(
			userId, fileShortcutId, status, serviceContext);
	}

	public static DLFileShortcutLocalService getService() {
		return _service;
	}

	public static void setService(DLFileShortcutLocalService service) {
		_service = service;
	}

	private static volatile DLFileShortcutLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1404503981