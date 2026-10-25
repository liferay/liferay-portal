/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.journal.model.JournalFolder;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for JournalFolder. This utility wraps
 * <code>com.liferay.journal.service.impl.JournalFolderLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see JournalFolderLocalService
 * @generated
 */
public class JournalFolderLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.journal.service.impl.JournalFolderLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static JournalFolder addFolder(
			String externalReferenceCode, long userId, long groupId,
			long parentFolderId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFolder(
			externalReferenceCode, userId, groupId, parentFolderId, name,
			description, serviceContext);
	}

	/**
	 * Adds the journal folder to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFolder the journal folder
	 * @return the journal folder that was added
	 */
	public static JournalFolder addJournalFolder(JournalFolder journalFolder) {
		return getService().addJournalFolder(journalFolder);
	}

	/**
	 * Creates a new journal folder with the primary key. Does not add the journal folder to the database.
	 *
	 * @param folderId the primary key for the new journal folder
	 * @return the new journal folder
	 */
	public static JournalFolder createJournalFolder(long folderId) {
		return getService().createJournalFolder(folderId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static JournalFolder deleteFolder(JournalFolder folder)
		throws PortalException {

		return getService().deleteFolder(folder);
	}

	public static JournalFolder deleteFolder(
			JournalFolder folder, boolean includeTrashedEntries)
		throws PortalException {

		return getService().deleteFolder(folder, includeTrashedEntries);
	}

	public static JournalFolder deleteFolder(long folderId)
		throws PortalException {

		return getService().deleteFolder(folderId);
	}

	public static JournalFolder deleteFolder(
			long folderId, boolean includeTrashedEntries)
		throws PortalException {

		return getService().deleteFolder(folderId, includeTrashedEntries);
	}

	public static void deleteFolders(long groupId) throws PortalException {
		getService().deleteFolders(groupId);
	}

	/**
	 * Deletes the journal folder from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFolder the journal folder
	 * @return the journal folder that was removed
	 */
	public static JournalFolder deleteJournalFolder(
		JournalFolder journalFolder) {

		return getService().deleteJournalFolder(journalFolder);
	}

	/**
	 * Deletes the journal folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param folderId the primary key of the journal folder
	 * @return the journal folder that was removed
	 * @throws PortalException if a journal folder with the primary key could not be found
	 */
	public static JournalFolder deleteJournalFolder(long folderId)
		throws PortalException {

		return getService().deleteJournalFolder(folderId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFolderModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFolderModelImpl</code>.
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

	public static JournalFolder fetchFolder(long folderId) {
		return getService().fetchFolder(folderId);
	}

	public static JournalFolder fetchFolder(
		long groupId, long parentFolderId, String name) {

		return getService().fetchFolder(groupId, parentFolderId, name);
	}

	public static JournalFolder fetchFolder(long groupId, String name) {
		return getService().fetchFolder(groupId, name);
	}

	public static JournalFolder fetchJournalFolder(long folderId) {
		return getService().fetchJournalFolder(folderId);
	}

	public static JournalFolder fetchJournalFolderByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchJournalFolderByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the journal folder matching the UUID and group.
	 *
	 * @param uuid the journal folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	public static JournalFolder fetchJournalFolderByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchJournalFolderByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<JournalFolder> getCompanyFolders(
		long companyId, int start, int end) {

		return getService().getCompanyFolders(companyId, start, end);
	}

	public static int getCompanyFoldersCount(long companyId) {
		return getService().getCompanyFoldersCount(companyId);
	}

	public static List<com.liferay.dynamic.data.mapping.model.DDMStructure>
			getDDMStructures(
				long[] groupIds, long folderId, int restrictionType)
		throws PortalException {

		return getService().getDDMStructures(
			groupIds, folderId, restrictionType);
	}

	public static List<com.liferay.dynamic.data.mapping.model.DDMStructure>
			getDDMStructures(
				long[] groupIds, long folderId, int restrictionType,
				OrderByComparator
					<com.liferay.dynamic.data.mapping.model.DDMStructure>
						orderByComparator)
		throws PortalException {

		return getService().getDDMStructures(
			groupIds, folderId, restrictionType, orderByComparator);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static JournalFolder getFolder(long folderId)
		throws PortalException {

		return getService().getFolder(folderId);
	}

	public static List<JournalFolder> getFolders(long groupId) {
		return getService().getFolders(groupId);
	}

	public static List<JournalFolder> getFolders(
		long groupId, long parentFolderId) {

		return getService().getFolders(groupId, parentFolderId);
	}

	public static List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int status) {

		return getService().getFolders(groupId, parentFolderId, status);
	}

	public static List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int start, int end) {

		return getService().getFolders(groupId, parentFolderId, start, end);
	}

	public static List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int status, int start, int end) {

		return getService().getFolders(
			groupId, parentFolderId, status, start, end);
	}

	public static List<Object> getFoldersAndArticles(
		long groupId, long folderId) {

		return getService().getFoldersAndArticles(groupId, folderId);
	}

	public static List<Object> getFoldersAndArticles(
		long groupId, long folderId, int status) {

		return getService().getFoldersAndArticles(groupId, folderId, status);
	}

	public static List<Object> getFoldersAndArticles(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<?> orderByComparator) {

		return getService().getFoldersAndArticles(
			groupId, folderId, status, start, end, orderByComparator);
	}

	public static int getFoldersAndArticlesCount(
		long groupId, List<Long> folderIds, int status) {

		return getService().getFoldersAndArticlesCount(
			groupId, folderIds, status);
	}

	public static int getFoldersAndArticlesCount(
		long groupId, long folderId, int status) {

		return getService().getFoldersAndArticlesCount(
			groupId, folderId, status);
	}

	public static int getFoldersCount(long groupId, long parentFolderId) {
		return getService().getFoldersCount(groupId, parentFolderId);
	}

	public static int getFoldersCount(
		long groupId, long parentFolderId, int status) {

		return getService().getFoldersCount(groupId, parentFolderId, status);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static long getInheritedWorkflowFolderId(long folderId)
		throws com.liferay.journal.exception.NoSuchFolderException {

		return getService().getInheritedWorkflowFolderId(folderId);
	}

	/**
	 * Returns the journal folder with the primary key.
	 *
	 * @param folderId the primary key of the journal folder
	 * @return the journal folder
	 * @throws PortalException if a journal folder with the primary key could not be found
	 */
	public static JournalFolder getJournalFolder(long folderId)
		throws PortalException {

		return getService().getJournalFolder(folderId);
	}

	public static JournalFolder getJournalFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getJournalFolderByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the journal folder matching the UUID and group.
	 *
	 * @param uuid the journal folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal folder
	 * @throws PortalException if a matching journal folder could not be found
	 */
	public static JournalFolder getJournalFolderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getJournalFolderByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the journal folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of journal folders
	 */
	public static List<JournalFolder> getJournalFolders(int start, int end) {
		return getService().getJournalFolders(start, end);
	}

	/**
	 * Returns all the journal folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal folders
	 * @param companyId the primary key of the company
	 * @return the matching journal folders, or an empty list if no matches were found
	 */
	public static List<JournalFolder> getJournalFoldersByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getJournalFoldersByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of journal folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal folders
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching journal folders, or an empty list if no matches were found
	 */
	public static List<JournalFolder> getJournalFoldersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator) {

		return getService().getJournalFoldersByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of journal folders.
	 *
	 * @return the number of journal folders
	 */
	public static int getJournalFoldersCount() {
		return getService().getJournalFoldersCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static long getOverridedDDMStructuresFolderId(long folderId)
		throws com.liferay.journal.exception.NoSuchFolderException {

		return getService().getOverridedDDMStructuresFolderId(folderId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static void getSubfolderIds(
		List<Long> folderIds, long groupId, long folderId) {

		getService().getSubfolderIds(folderIds, groupId, folderId);
	}

	public static String getUniqueFolderName(
		String uuid, long groupId, long parentFolderId, String name,
		int count) {

		return getService().getUniqueFolderName(
			uuid, groupId, parentFolderId, name, count);
	}

	public static JournalFolder moveFolder(
			long folderId, long parentFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveFolder(
			folderId, parentFolderId, serviceContext);
	}

	public static JournalFolder moveFolderFromTrash(
			long userId, long folderId, long parentFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveFolderFromTrash(
			userId, folderId, parentFolderId, serviceContext);
	}

	public static JournalFolder moveFolderToTrash(long userId, long folderId)
		throws PortalException {

		return getService().moveFolderToTrash(userId, folderId);
	}

	public static void rebuildTree(long companyId) throws PortalException {
		getService().rebuildTree(companyId);
	}

	public static void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws PortalException {

		getService().rebuildTree(
			companyId, parentFolderId, parentTreePath, reindex);
	}

	public static void restoreFolderFromTrash(long userId, long folderId)
		throws PortalException {

		getService().restoreFolderFromTrash(userId, folderId);
	}

	public static List<com.liferay.dynamic.data.mapping.model.DDMStructure>
			searchDDMStructures(
				long companyId, long[] groupIds, long folderId,
				int restrictionType, String keywords, int start, int end,
				OrderByComparator
					<com.liferay.dynamic.data.mapping.model.DDMStructure>
						orderByComparator)
		throws PortalException {

		return getService().searchDDMStructures(
			companyId, groupIds, folderId, restrictionType, keywords, start,
			end, orderByComparator);
	}

	public static void subscribe(long userId, long groupId, long folderId)
		throws PortalException {

		getService().subscribe(userId, groupId, folderId);
	}

	public static void unsubscribe(long userId, long groupId, long folderId)
		throws PortalException {

		getService().unsubscribe(userId, groupId, folderId);
	}

	public static void updateAsset(
			long userId, JournalFolder folder, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		getService().updateAsset(
			userId, folder, assetCategoryIds, assetTagNames, assetLinkEntryIds,
			priority);
	}

	public static JournalFolder updateFolder(
			long userId, long groupId, long folderId, long parentFolderId,
			String name, String description, boolean mergeWithParentFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFolder(
			userId, groupId, folderId, parentFolderId, name, description,
			mergeWithParentFolder, serviceContext);
	}

	public static JournalFolder updateFolder(
			long userId, long groupId, long folderId, long parentFolderId,
			String name, String description, long[] ddmStructureIds,
			int restrictionType, boolean mergeWithParentFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFolder(
			userId, groupId, folderId, parentFolderId, name, description,
			ddmStructureIds, restrictionType, mergeWithParentFolder,
			serviceContext);
	}

	public static void updateFolderDDMStructures(
			JournalFolder folder, long[] ddmStructureIdsArray)
		throws PortalException {

		getService().updateFolderDDMStructures(folder, ddmStructureIdsArray);
	}

	/**
	 * Updates the journal folder in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFolder the journal folder
	 * @return the journal folder that was updated
	 */
	public static JournalFolder updateJournalFolder(
		JournalFolder journalFolder) {

		return getService().updateJournalFolder(journalFolder);
	}

	public static JournalFolder updateStatus(
			long userId, JournalFolder folder, int status)
		throws PortalException {

		return getService().updateStatus(userId, folder, status);
	}

	public static void validateFolderDDMStructures(
			long folderId, long parentFolderId)
		throws PortalException {

		getService().validateFolderDDMStructures(folderId, parentFolderId);
	}

	public static JournalFolderLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<JournalFolderLocalService> _serviceSnapshot =
		new Snapshot<>(
			JournalFolderLocalServiceUtil.class,
			JournalFolderLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:318397648