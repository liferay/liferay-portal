/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DLFileShortcutLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileShortcutLocalService
 * @generated
 */
public class DLFileShortcutLocalServiceWrapper
	implements DLFileShortcutLocalService,
			   ServiceWrapper<DLFileShortcutLocalService> {

	public DLFileShortcutLocalServiceWrapper() {
		this(null);
	}

	public DLFileShortcutLocalServiceWrapper(
		DLFileShortcutLocalService dlFileShortcutLocalService) {

		_dlFileShortcutLocalService = dlFileShortcutLocalService;
	}

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
	@Override
	public DLFileShortcut addDLFileShortcut(DLFileShortcut dlFileShortcut) {
		return _dlFileShortcutLocalService.addDLFileShortcut(dlFileShortcut);
	}

	@Override
	public DLFileShortcut addFileShortcut(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, long folderId, long toFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileShortcutLocalService.addFileShortcut(
			externalReferenceCode, userId, groupId, repositoryId, folderId,
			toFileEntryId, serviceContext);
	}

	@Override
	public void addFileShortcutResources(
			DLFileShortcut fileShortcut, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.addFileShortcutResources(
			fileShortcut, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addFileShortcutResources(
			DLFileShortcut fileShortcut,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.addFileShortcutResources(
			fileShortcut, modelPermissions);
	}

	@Override
	public void addFileShortcutResources(
			long fileShortcutId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.addFileShortcutResources(
			fileShortcutId, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addFileShortcutResources(
			long fileShortcutId,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.addFileShortcutResources(
			fileShortcutId, modelPermissions);
	}

	/**
	 * Creates a new document library file shortcut with the primary key. Does not add the document library file shortcut to the database.
	 *
	 * @param fileShortcutId the primary key for the new document library file shortcut
	 * @return the new document library file shortcut
	 */
	@Override
	public DLFileShortcut createDLFileShortcut(long fileShortcutId) {
		return _dlFileShortcutLocalService.createDLFileShortcut(fileShortcutId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileShortcutLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public DLFileShortcut deleteDLFileShortcut(DLFileShortcut dlFileShortcut) {
		return _dlFileShortcutLocalService.deleteDLFileShortcut(dlFileShortcut);
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
	@Override
	public DLFileShortcut deleteDLFileShortcut(long fileShortcutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileShortcutLocalService.deleteDLFileShortcut(fileShortcutId);
	}

	@Override
	public void deleteFileShortcut(DLFileShortcut fileShortcut)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.deleteFileShortcut(fileShortcut);
	}

	@Override
	public void deleteFileShortcut(long fileShortcutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.deleteFileShortcut(fileShortcutId);
	}

	@Override
	public void deleteFileShortcut(String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.deleteFileShortcut(
			externalReferenceCode, groupId);
	}

	@Override
	public void deleteFileShortcuts(long toFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.deleteFileShortcuts(toFileEntryId);
	}

	@Override
	public void deleteFileShortcuts(long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.deleteFileShortcuts(groupId, folderId);
	}

	@Override
	public void deleteFileShortcuts(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.deleteFileShortcuts(
			groupId, folderId, includeTrashedEntries);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileShortcutLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteRepositoryFileShortcuts(long repositoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.deleteRepositoryFileShortcuts(repositoryId);
	}

	@Override
	public void disableFileShortcuts(long toFileEntryId) {
		_dlFileShortcutLocalService.disableFileShortcuts(toFileEntryId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dlFileShortcutLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dlFileShortcutLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dlFileShortcutLocalService.dynamicQuery();
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

		return _dlFileShortcutLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _dlFileShortcutLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _dlFileShortcutLocalService.dynamicQuery(
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

		return _dlFileShortcutLocalService.dynamicQueryCount(dynamicQuery);
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

		return _dlFileShortcutLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public void enableFileShortcuts(long toFileEntryId) {
		_dlFileShortcutLocalService.enableFileShortcuts(toFileEntryId);
	}

	@Override
	public DLFileShortcut fetchDLFileShortcut(long fileShortcutId) {
		return _dlFileShortcutLocalService.fetchDLFileShortcut(fileShortcutId);
	}

	@Override
	public DLFileShortcut fetchDLFileShortcutByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _dlFileShortcutLocalService.
			fetchDLFileShortcutByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library file shortcut matching the UUID and group.
	 *
	 * @param uuid the document library file shortcut's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchDLFileShortcutByUuidAndGroupId(
		String uuid, long groupId) {

		return _dlFileShortcutLocalService.fetchDLFileShortcutByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dlFileShortcutLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the document library file shortcut with the primary key.
	 *
	 * @param fileShortcutId the primary key of the document library file shortcut
	 * @return the document library file shortcut
	 * @throws PortalException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut getDLFileShortcut(long fileShortcutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileShortcutLocalService.getDLFileShortcut(fileShortcutId);
	}

	@Override
	public DLFileShortcut getDLFileShortcutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileShortcutLocalService.
			getDLFileShortcutByExternalReferenceCode(
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
	@Override
	public DLFileShortcut getDLFileShortcutByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileShortcutLocalService.getDLFileShortcutByUuidAndGroupId(
			uuid, groupId);
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
	@Override
	public java.util.List<DLFileShortcut> getDLFileShortcuts(
		int start, int end) {

		return _dlFileShortcutLocalService.getDLFileShortcuts(start, end);
	}

	/**
	 * Returns all the document library file shortcuts matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file shortcuts
	 * @param companyId the primary key of the company
	 * @return the matching document library file shortcuts, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DLFileShortcut> getDLFileShortcutsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _dlFileShortcutLocalService.getDLFileShortcutsByUuidAndCompanyId(
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
	@Override
	public java.util.List<DLFileShortcut> getDLFileShortcutsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileShortcut>
			orderByComparator) {

		return _dlFileShortcutLocalService.getDLFileShortcutsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library file shortcuts.
	 *
	 * @return the number of document library file shortcuts
	 */
	@Override
	public int getDLFileShortcutsCount() {
		return _dlFileShortcutLocalService.getDLFileShortcutsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _dlFileShortcutLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public DLFileShortcut getFileShortcut(long fileShortcutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileShortcutLocalService.getFileShortcut(fileShortcutId);
	}

	@Override
	public java.util.List<DLFileShortcut> getFileShortcuts(long toFileEntryId) {
		return _dlFileShortcutLocalService.getFileShortcuts(toFileEntryId);
	}

	@Override
	public java.util.List<DLFileShortcut> getFileShortcuts(
		long groupId, long folderId) {

		return _dlFileShortcutLocalService.getFileShortcuts(groupId, folderId);
	}

	@Override
	public java.util.List<DLFileShortcut> getGroupFileShortcuts(long groupId) {
		return _dlFileShortcutLocalService.getGroupFileShortcuts(groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dlFileShortcutLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dlFileShortcutLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileShortcutLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void setTreePaths(long folderId, String treePath)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.setTreePaths(folderId, treePath);
	}

	@Override
	public void updateAsset(
			long userId, DLFileShortcut fileShortcut, long[] assetCategoryIds,
			String[] assetTagNames)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileShortcutLocalService.updateAsset(
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
	@Override
	public DLFileShortcut updateDLFileShortcut(DLFileShortcut dlFileShortcut) {
		return _dlFileShortcutLocalService.updateDLFileShortcut(dlFileShortcut);
	}

	@Override
	public DLFileShortcut updateFileShortcut(
			long userId, long fileShortcutId, long repositoryId, long folderId,
			long toFileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileShortcutLocalService.updateFileShortcut(
			userId, fileShortcutId, repositoryId, folderId, toFileEntryId,
			serviceContext);
	}

	@Override
	public void updateFileShortcuts(
		long oldToFileEntryId, long newToFileEntryId) {

		_dlFileShortcutLocalService.updateFileShortcuts(
			oldToFileEntryId, newToFileEntryId);
	}

	@Override
	public void updateFileShortcutsActive(long toFileEntryId, boolean active) {
		_dlFileShortcutLocalService.updateFileShortcutsActive(
			toFileEntryId, active);
	}

	@Override
	public DLFileShortcut updateStatus(
			long userId, long fileShortcutId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileShortcutLocalService.updateStatus(
			userId, fileShortcutId, status, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dlFileShortcutLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DLFileShortcut> getCTPersistence() {
		return _dlFileShortcutLocalService.getCTPersistence();
	}

	@Override
	public Class<DLFileShortcut> getModelClass() {
		return _dlFileShortcutLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DLFileShortcut>, R, E>
				updateUnsafeFunction)
		throws E {

		return _dlFileShortcutLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DLFileShortcutLocalService getWrappedService() {
		return _dlFileShortcutLocalService;
	}

	@Override
	public void setWrappedService(
		DLFileShortcutLocalService dlFileShortcutLocalService) {

		_dlFileShortcutLocalService = dlFileShortcutLocalService;
	}

	private DLFileShortcutLocalService _dlFileShortcutLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:2114302384