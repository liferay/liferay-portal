/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.journal.exception.NoSuchFolderException;
import com.liferay.journal.model.JournalFolder;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for JournalFolder. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see JournalFolderLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface JournalFolderLocalService
	extends BaseLocalService, CTService<JournalFolder>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.journal.service.impl.JournalFolderLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the journal folder local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link JournalFolderLocalServiceUtil} if injection and service tracking are not available.
	 */
	public JournalFolder addFolder(
			String externalReferenceCode, long userId, long groupId,
			long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public JournalFolder addJournalFolder(JournalFolder journalFolder);

	/**
	 * Creates a new journal folder with the primary key. Does not add the journal folder to the database.
	 *
	 * @param folderId the primary key for the new journal folder
	 * @return the new journal folder
	 */
	@Transactional(enabled = false)
	public JournalFolder createJournalFolder(long folderId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public JournalFolder deleteFolder(JournalFolder folder)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public JournalFolder deleteFolder(
			JournalFolder folder, boolean includeTrashedEntries)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public JournalFolder deleteFolder(long folderId) throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public JournalFolder deleteFolder(
			long folderId, boolean includeTrashedEntries)
		throws PortalException;

	public void deleteFolders(long groupId) throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public JournalFolder deleteJournalFolder(JournalFolder journalFolder);

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
	@Indexable(type = IndexableType.DELETE)
	public JournalFolder deleteJournalFolder(long folderId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFolder fetchFolder(long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFolder fetchFolder(
		long groupId, long parentFolderId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFolder fetchFolder(long groupId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFolder fetchJournalFolder(long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFolder fetchJournalFolderByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the journal folder matching the UUID and group.
	 *
	 * @param uuid the journal folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFolder fetchJournalFolderByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFolder> getCompanyFolders(
		long companyId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyFoldersCount(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMStructure> getDDMStructures(
			long[] groupIds, long folderId, int restrictionType)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMStructure> getDDMStructures(
			long[] groupIds, long folderId, int restrictionType,
			OrderByComparator<DDMStructure> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFolder getFolder(long folderId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFolder> getFolders(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFolder> getFolders(long groupId, long parentFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndArticles(long groupId, long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndArticles(
		long groupId, long folderId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndArticles(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<?> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndArticlesCount(
		long groupId, List<Long> folderIds, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndArticlesCount(
		long groupId, long folderId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(long groupId, long parentFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(long groupId, long parentFolderId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getInheritedWorkflowFolderId(long folderId)
		throws NoSuchFolderException;

	/**
	 * Returns the journal folder with the primary key.
	 *
	 * @param folderId the primary key of the journal folder
	 * @return the journal folder
	 * @throws PortalException if a journal folder with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFolder getJournalFolder(long folderId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFolder getJournalFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the journal folder matching the UUID and group.
	 *
	 * @param uuid the journal folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal folder
	 * @throws PortalException if a matching journal folder could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFolder getJournalFolderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFolder> getJournalFolders(int start, int end);

	/**
	 * Returns all the journal folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal folders
	 * @param companyId the primary key of the company
	 * @return the matching journal folders, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFolder> getJournalFoldersByUuidAndCompanyId(
		String uuid, long companyId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFolder> getJournalFoldersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<JournalFolder> orderByComparator);

	/**
	 * Returns the number of journal folders.
	 *
	 * @return the number of journal folders
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getJournalFoldersCount();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getOverridedDDMStructuresFolderId(long folderId)
		throws NoSuchFolderException;

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void getSubfolderIds(
		List<Long> folderIds, long groupId, long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getUniqueFolderName(
		String uuid, long groupId, long parentFolderId, String name, int count);

	@Indexable(type = IndexableType.REINDEX)
	public JournalFolder moveFolder(
			long folderId, long parentFolderId, ServiceContext serviceContext)
		throws PortalException;

	public JournalFolder moveFolderFromTrash(
			long userId, long folderId, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public JournalFolder moveFolderToTrash(long userId, long folderId)
		throws PortalException;

	public void rebuildTree(long companyId) throws PortalException;

	public void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws PortalException;

	public void restoreFolderFromTrash(long userId, long folderId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMStructure> searchDDMStructures(
			long companyId, long[] groupIds, long folderId, int restrictionType,
			String keywords, int start, int end,
			OrderByComparator<DDMStructure> orderByComparator)
		throws PortalException;

	public void subscribe(long userId, long groupId, long folderId)
		throws PortalException;

	public void unsubscribe(long userId, long groupId, long folderId)
		throws PortalException;

	public void updateAsset(
			long userId, JournalFolder folder, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public JournalFolder updateFolder(
			long userId, long groupId, long folderId, long parentFolderId,
			String name, String description, boolean mergeWithParentFolder,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public JournalFolder updateFolder(
			long userId, long groupId, long folderId, long parentFolderId,
			String name, String description, long[] ddmStructureIds,
			int restrictionType, boolean mergeWithParentFolder,
			ServiceContext serviceContext)
		throws PortalException;

	public void updateFolderDDMStructures(
			JournalFolder folder, long[] ddmStructureIdsArray)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public JournalFolder updateJournalFolder(JournalFolder journalFolder);

	public JournalFolder updateStatus(
			long userId, JournalFolder folder, int status)
		throws PortalException;

	public void validateFolderDDMStructures(long folderId, long parentFolderId)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<JournalFolder> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<JournalFolder> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<JournalFolder>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2128457890