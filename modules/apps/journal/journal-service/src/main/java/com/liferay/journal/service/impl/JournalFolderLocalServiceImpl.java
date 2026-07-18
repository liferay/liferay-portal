/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.exception.DuplicateFolderExternalReferenceCodeException;
import com.liferay.journal.exception.NoSuchFolderException;
import com.liferay.journal.internal.util.JournalTreePathUtil;
import com.liferay.journal.internal.validation.JournalFolderModelValidator;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.base.JournalFolderLocalServiceBaseImpl;
import com.liferay.journal.service.persistence.JournalArticleFinder;
import com.liferay.journal.service.persistence.JournalArticlePersistence;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.social.SocialActivityManagerUtil;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.validation.ModelValidator;
import com.liferay.portal.validation.ModelValidatorRegistryUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.trash.service.TrashVersionLocalService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan Fernández
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalFolder",
	service = AopService.class
)
public class JournalFolderLocalServiceImpl
	extends JournalFolderLocalServiceBaseImpl {

	@Override
	public JournalFolder addFolder(
			String externalReferenceCode, long userId, long groupId,
			long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		// Folder

		User user = _userLocalService.getUser(userId);

		parentFolderId = _getParentFolderId(groupId, parentFolderId);

		_validateFolder(0, groupId, parentFolderId, name);

		long folderId = counterLocalService.increment();

		_validateExternalReferenceCode(externalReferenceCode, groupId);

		JournalFolder folder = journalFolderPersistence.create(folderId);

		folder.setUuid(serviceContext.getUuid());
		folder.setExternalReferenceCode(externalReferenceCode);
		folder.setGroupId(groupId);
		folder.setCompanyId(user.getCompanyId());
		folder.setUserId(user.getUserId());
		folder.setUserName(user.getFullName());
		folder.setParentFolderId(parentFolderId);
		folder.setTreePath(folder.buildTreePath());
		folder.setName(name);
		folder.setDescription(description);
		folder.setStatusByUserId(user.getUserId());
		folder.setStatusByUserName(user.getFullName());
		folder.setStatusDate(serviceContext.getModifiedDate(new Date()));
		folder.setExpandoBridgeAttributes(serviceContext);

		folder = journalFolderPersistence.update(folder);

		// Resources

		_resourceLocalService.addModelResources(folder, serviceContext);

		// Asset

		updateAsset(
			userId, folder, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		return folder;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public JournalFolder deleteFolder(JournalFolder folder)
		throws PortalException {

		return deleteFolder(folder, true);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public JournalFolder deleteFolder(
			JournalFolder folder, boolean includeTrashedEntries)
		throws PortalException {

		// Folders

		List<JournalFolder> folders = journalFolderPersistence.findByG_P(
			folder.getGroupId(), folder.getFolderId());

		for (JournalFolder curFolder : folders) {
			if (includeTrashedEntries ||
				!_trashHelper.isInTrashExplicitly(curFolder)) {

				journalFolderLocalService.deleteFolder(
					curFolder, includeTrashedEntries);
			}
		}

		// Folder

		journalFolderPersistence.remove(folder);

		// Resources

		_resourceLocalService.deleteResource(
			folder, ResourceConstants.SCOPE_INDIVIDUAL);

		// Entries

		_journalArticleLocalService.deleteArticles(
			folder.getGroupId(), folder.getFolderId(), includeTrashedEntries);

		// Asset

		_assetEntryLocalService.deleteEntry(
			JournalFolder.class.getName(), folder.getFolderId());

		// Expando

		_expandoValueLocalService.deleteValues(
			JournalFolder.class.getName(), folder.getFolderId());

		// Ratings

		_ratingsStatsLocalService.deleteStats(
			JournalFolder.class.getName(), folder.getFolderId());

		// Trash

		if (_trashHelper.isInTrashExplicitly(folder)) {
			_trashEntryLocalService.deleteEntry(
				JournalFolder.class.getName(), folder.getFolderId());
		}
		else {
			_trashVersionLocalService.deleteTrashVersion(
				JournalFolder.class.getName(), folder.getFolderId());
		}

		// Workflow

		List<DDMStructureLink> ddmStructureLinks =
			_ddmStructureLinkLocalService.getStructureLinks(
				_classNameLocalService.getClassNameId(JournalFolder.class),
				folder.getFolderId());

		if (ddmStructureLinks.isEmpty()) {
			WorkflowDefinitionLink workflowDefinitionLink =
				_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
					folder.getCompanyId(), folder.getGroupId(),
					JournalFolder.class.getName(), folder.getFolderId(),
					JournalArticleConstants.DDM_STRUCTURE_ID_ALL);

			if (workflowDefinitionLink != null) {
				_workflowDefinitionLinkLocalService.
					deleteWorkflowDefinitionLink(workflowDefinitionLink);
			}
		}

		for (DDMStructureLink ddmStructureLink : ddmStructureLinks) {
			_ddmStructureLinkLocalService.deleteStructureLink(
				ddmStructureLink.getStructureLinkId());

			WorkflowDefinitionLink workflowDefinitionLink =
				_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
					folder.getCompanyId(), folder.getGroupId(),
					JournalFolder.class.getName(), folder.getFolderId(),
					ddmStructureLink.getStructureId());

			if (workflowDefinitionLink != null) {
				_workflowDefinitionLinkLocalService.
					deleteWorkflowDefinitionLink(workflowDefinitionLink);
			}
		}

		return folder;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public JournalFolder deleteFolder(long folderId) throws PortalException {
		JournalFolder folder = journalFolderPersistence.findByPrimaryKey(
			folderId);

		return journalFolderLocalService.deleteFolder(folder, true);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public JournalFolder deleteFolder(
			long folderId, boolean includeTrashedEntries)
		throws PortalException {

		JournalFolder folder = journalFolderPersistence.findByPrimaryKey(
			folderId);

		return journalFolderLocalService.deleteFolder(
			folder, includeTrashedEntries);
	}

	@Override
	public void deleteFolders(long groupId) throws PortalException {
		List<JournalFolder> folders = journalFolderPersistence.findByGroupId(
			groupId);

		for (JournalFolder folder : folders) {
			journalFolderLocalService.deleteFolder(folder);
		}
	}

	@Override
	public JournalFolder fetchFolder(long folderId) {
		return journalFolderPersistence.fetchByPrimaryKey(folderId);
	}

	@Override
	public JournalFolder fetchFolder(
		long groupId, long parentFolderId, String name) {

		return journalFolderPersistence.fetchByG_P_N(
			groupId, parentFolderId, name);
	}

	@Override
	public JournalFolder fetchFolder(long groupId, String name) {
		return journalFolderPersistence.fetchByG_N(groupId, name);
	}

	@Override
	public List<JournalFolder> getCompanyFolders(
		long companyId, int start, int end) {

		return journalFolderPersistence.findByCompanyId(companyId, start, end);
	}

	@Override
	public int getCompanyFoldersCount(long companyId) {
		return journalFolderPersistence.countByCompanyId(companyId);
	}

	@Override
	public List<DDMStructure> getDDMStructures(
			long[] groupIds, long folderId, int restrictionType)
		throws PortalException {

		return getDDMStructures(groupIds, folderId, restrictionType, null);
	}

	@Override
	public List<DDMStructure> getDDMStructures(
			long[] groupIds, long folderId, int restrictionType,
			OrderByComparator<DDMStructure> orderByComparator)
		throws PortalException {

		if (restrictionType ==
				JournalFolderConstants.
					RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW) {

			return _ddmStructureLinkLocalService.getStructureLinkStructures(
				_classNameLocalService.getClassNameId(JournalFolder.class),
				folderId);
		}

		folderId = getOverridedDDMStructuresFolderId(folderId);

		if (folderId != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return _ddmStructureLinkLocalService.getStructureLinkStructures(
				_classNameLocalService.getClassNameId(JournalFolder.class),
				folderId);
		}

		return _ddmStructureLocalService.getStructures(
			groupIds,
			_classNameLocalService.getClassNameId(JournalArticle.class),
			orderByComparator);
	}

	@Override
	public JournalFolder getFolder(long folderId) throws PortalException {
		return journalFolderPersistence.findByPrimaryKey(folderId);
	}

	@Override
	public List<JournalFolder> getFolders(long groupId) {
		return journalFolderPersistence.findByGroupId(groupId);
	}

	@Override
	public List<JournalFolder> getFolders(long groupId, long parentFolderId) {
		return getFolders(
			groupId, parentFolderId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int status) {

		return journalFolderPersistence.findByG_P_S(
			groupId, parentFolderId, status);
	}

	@Override
	public List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int start, int end) {

		return getFolders(
			groupId, parentFolderId, WorkflowConstants.STATUS_APPROVED, start,
			end);
	}

	@Override
	public List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int status, int start, int end) {

		return journalFolderPersistence.findByG_P_S(
			groupId, parentFolderId, status, start, end);
	}

	@Override
	public List<Object> getFoldersAndArticles(long groupId, long folderId) {
		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		return journalFolderFinder.findF_A_ByG_F_DDMSI(
			groupId, folderId, 0, queryDefinition);
	}

	@Override
	public List<Object> getFoldersAndArticles(
		long groupId, long folderId, int status) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return journalFolderFinder.findF_A_ByG_F_DDMSI(
			groupId, folderId, 0, queryDefinition);
	}

	@Override
	public List<Object> getFoldersAndArticles(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<?> orderByComparator) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, start, end, (OrderByComparator<Object>)orderByComparator);

		return journalFolderFinder.findF_A_ByG_F_DDMSI(
			groupId, folderId, 0, queryDefinition);
	}

	@Override
	public List<Object> getFoldersAndArticles(
		long groupId, long folderId, int start, int end,
		OrderByComparator<?> orderByComparator) {

		return getFoldersAndArticles(
			groupId, folderId, WorkflowConstants.STATUS_ANY, start, end,
			orderByComparator);
	}

	@Override
	public int getFoldersAndArticlesCount(
		long groupId, List<Long> folderIds, int status) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status);

		if (folderIds.size() <= DBManagerUtil.getDBMaxParameters()) {
			return _journalArticleFinder.countByG_F(
				groupId, folderIds, queryDefinition);
		}

		int start = 0;
		int end = DBManagerUtil.getDBMaxParameters();

		int articlesCount = _journalArticleFinder.countByG_F(
			groupId, folderIds.subList(start, end), queryDefinition);

		List<Long> sublist = folderIds.subList(start, end);

		sublist.clear();

		articlesCount += getFoldersAndArticlesCount(groupId, folderIds, status);

		return articlesCount;
	}

	@Override
	public int getFoldersAndArticlesCount(long groupId, long folderId) {
		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		return journalFolderFinder.countF_A_ByG_F_DDMSI(
			groupId, folderId, 0, queryDefinition);
	}

	@Override
	public int getFoldersAndArticlesCount(
		long groupId, long folderId, int status) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, 0, false);

		return journalFolderFinder.countF_A_ByG_F_DDMSI(
			groupId, folderId, 0, queryDefinition);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId) {
		return getFoldersCount(
			groupId, parentFolderId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId, int status) {
		return journalFolderPersistence.countByG_P_S(
			groupId, parentFolderId, status);
	}

	@Override
	public long getInheritedWorkflowFolderId(long folderId)
		throws NoSuchFolderException {

		while (folderId != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			JournalFolder folder = journalFolderPersistence.findByPrimaryKey(
				folderId);

			if (folder.getRestrictionType() !=
					JournalFolderConstants.RESTRICTION_TYPE_INHERIT) {

				break;
			}

			folderId = folder.getParentFolderId();
		}

		return folderId;
	}

	@Override
	public List<JournalFolder> getNoAssetFolders() {
		return journalFolderFinder.findF_ByNoAssets();
	}

	@Override
	public long getOverridedDDMStructuresFolderId(long folderId)
		throws NoSuchFolderException {

		while (folderId != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			JournalFolder folder = journalFolderPersistence.findByPrimaryKey(
				folderId);

			if (folder.getRestrictionType() ==
					JournalFolderConstants.
						RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW) {

				break;
			}

			folderId = folder.getParentFolderId();
		}

		return folderId;
	}

	@Override
	public void getSubfolderIds(
		List<Long> folderIds, long groupId, long folderId) {

		List<JournalFolder> folders = journalFolderPersistence.findByG_P(
			groupId, folderId);

		for (JournalFolder folder : folders) {
			folderIds.add(folder.getFolderId());

			getSubfolderIds(
				folderIds, folder.getGroupId(), folder.getFolderId());
		}
	}

	@Override
	public String getUniqueFolderName(
		String uuid, long groupId, long parentFolderId, String name,
		int count) {

		JournalFolder folder = journalFolderPersistence.fetchByG_P_N(
			groupId, parentFolderId, name);

		if ((folder == null) ||
			(Validator.isNotNull(uuid) && uuid.equals(folder.getUuid()))) {

			return name;
		}

		name = StringUtil.appendParentheticalSuffix(name, count);

		return getUniqueFolderName(
			uuid, groupId, parentFolderId, name, ++count);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalFolder moveFolder(
			long folderId, long parentFolderId, ServiceContext serviceContext)
		throws PortalException {

		JournalFolder folder = journalFolderPersistence.findByPrimaryKey(
			folderId);

		_validateParentFolder(folder, parentFolderId);

		parentFolderId = _getParentFolderId(folder, parentFolderId);

		if (folder.getParentFolderId() == parentFolderId) {
			return folder;
		}

		validateFolderDDMStructures(folder.getFolderId(), parentFolderId);

		_validateFolder(
			folder.getFolderId(), folder.getGroupId(), parentFolderId,
			folder.getName());

		folder.setParentFolderId(parentFolderId);
		folder.setTreePath(folder.buildTreePath());
		folder.setExpandoBridgeAttributes(serviceContext);

		folder = journalFolderPersistence.update(folder);

		rebuildTree(
			folder.getCompanyId(), folderId, folder.getTreePath(), true);

		return folder;
	}

	@Override
	public JournalFolder moveFolderFromTrash(
			long userId, long folderId, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		JournalFolder folder = journalFolderPersistence.findByPrimaryKey(
			folderId);

		if (!folder.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(folder)) {
			restoreFolderFromTrash(userId, folderId);
		}
		else {

			// Folder

			TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
				JournalFolder.class.getName(), folderId);

			int status = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				status = trashVersion.getStatus();
			}

			updateStatus(userId, folder, status);

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}

			// Folders and articles

			_restoreDependentsFromTrash(
				journalFolderLocalService.getFoldersAndArticles(
					folder.getGroupId(), folder.getFolderId(),
					WorkflowConstants.STATUS_IN_TRASH));
		}

		return journalFolderLocalService.moveFolder(
			folderId, parentFolderId, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalFolder moveFolderToTrash(long userId, long folderId)
		throws PortalException {

		// Folder

		JournalFolder folder = journalFolderPersistence.findByPrimaryKey(
			folderId);

		if (folder.isInTrash()) {
			throw new TrashEntryException();
		}

		String title = folder.getName();

		folder = updateStatus(
			userId, folder, WorkflowConstants.STATUS_IN_TRASH);

		// Trash

		TrashEntry trashEntry = _trashEntryLocalService.addTrashEntry(
			userId, folder.getGroupId(), JournalFolder.class.getName(),
			folder.getFolderId(), folder.getUuid(), null,
			WorkflowConstants.STATUS_APPROVED, null,
			UnicodePropertiesBuilder.put(
				"title", folder.getName()
			).build());

		folder.setName(_trashHelper.getTrashTitle(trashEntry.getEntryId()));

		folder = journalFolderPersistence.update(folder);

		// Folders and articles

		_moveDependentsToTrash(
			journalFolderLocalService.getFoldersAndArticles(
				folder.getGroupId(), folder.getFolderId()),
			trashEntry.getEntryId());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put("title", title);

		SocialActivityManagerUtil.addActivity(
			userId, folder, SocialActivityConstants.TYPE_MOVE_TO_TRASH,
			extraDataJSONObject.toString(), 0);

		return folder;
	}

	@Override
	public void rebuildTree(long companyId) throws PortalException {
		rebuildTree(
			companyId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringPool.SLASH, false);
	}

	@Override
	public void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws PortalException {

		JournalTreePathUtil.rebuildTree(
			companyId, parentFolderId, parentTreePath, journalFolderPersistence,
			_journalArticleLocalService);
	}

	@Override
	public void restoreFolderFromTrash(long userId, long folderId)
		throws PortalException {

		// Folder

		JournalFolder folder = journalFolderPersistence.findByPrimaryKey(
			folderId);

		if (!folder.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		String originalName = _trashHelper.getOriginalTitle(folder.getName());

		folder.setName(
			journalFolderLocalService.getUniqueFolderName(
				folder.getUuid(), folder.getGroupId(),
				folder.getParentFolderId(), originalName, 2));

		folder = journalFolderPersistence.update(folder);

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			JournalFolder.class.getName(), folderId);

		updateStatus(userId, folder, trashEntry.getStatus());

		// Folders and articles

		_restoreDependentsFromTrash(
			journalFolderLocalService.getFoldersAndArticles(
				folder.getGroupId(), folder.getFolderId(),
				WorkflowConstants.STATUS_IN_TRASH));

		// Trash

		_trashEntryLocalService.deleteEntry(
			JournalFolder.class.getName(), folder.getFolderId());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", folder.getName());

		SocialActivityManagerUtil.addActivity(
			userId, folder, SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			extraDataJSONObject.toString(), 0);
	}

	@Override
	public List<DDMStructure> searchDDMStructures(
			long companyId, long[] groupIds, long folderId, int restrictionType,
			String keywords, int start, int end,
			OrderByComparator<DDMStructure> orderByComparator)
		throws PortalException {

		if (restrictionType ==
				JournalFolderConstants.
					RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW) {

			return _ddmStructureLocalService.search(
				companyId, groupIds,
				_classNameLocalService.getClassNameId(JournalFolder.class),
				folderId, keywords, start, end, orderByComparator);
		}

		folderId = getOverridedDDMStructuresFolderId(folderId);

		if (folderId != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return _ddmStructureLocalService.search(
				companyId, groupIds,
				_classNameLocalService.getClassNameId(JournalFolder.class),
				folderId, keywords, start, end, orderByComparator);
		}

		return _ddmStructureLocalService.search(
			companyId, groupIds,
			_classNameLocalService.getClassNameId(JournalArticle.class),
			keywords, WorkflowConstants.STATUS_ANY, start, end,
			orderByComparator);
	}

	@Override
	public void subscribe(long userId, long groupId, long folderId)
		throws PortalException {

		if (folderId == JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			folderId = groupId;
		}

		_subscriptionLocalService.addSubscription(
			userId, groupId, JournalFolder.class.getName(), folderId);
	}

	@Override
	public void unsubscribe(long userId, long groupId, long folderId)
		throws PortalException {

		if (folderId == JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			folderId = groupId;
		}

		_subscriptionLocalService.deleteSubscription(
			userId, JournalFolder.class.getName(), folderId);
	}

	@Override
	public void updateAsset(
			long userId, JournalFolder folder, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
			userId, folder.getGroupId(), folder.getCreateDate(),
			folder.getModifiedDate(), JournalFolder.class.getName(),
			folder.getFolderId(), folder.getUuid(), 0, assetCategoryIds,
			assetTagNames, true, true, null, null, folder.getCreateDate(), null,
			ContentTypes.TEXT_PLAIN, folder.getName(), folder.getDescription(),
			null, null, null, 0, 0, priority);

		_assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalFolder updateFolder(
			long userId, long groupId, long folderId, long parentFolderId,
			String name, String description, boolean mergeWithParentFolder,
			ServiceContext serviceContext)
		throws PortalException {

		return updateFolder(
			userId, groupId, folderId, parentFolderId, name, description,
			new long[0], JournalFolderConstants.RESTRICTION_TYPE_INHERIT,
			mergeWithParentFolder, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalFolder updateFolder(
			long userId, long groupId, long folderId, long parentFolderId,
			String name, String description, long[] ddmStructureIds,
			int restrictionType, boolean mergeWithParentFolder,
			ServiceContext serviceContext)
		throws PortalException {

		JournalFolder folder = null;

		Set<Long> originalDDMStructureIds = new HashSet<>();

		if (folderId > JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			originalDDMStructureIds = _getDDMStructureIds(
				_ddmStructureLinkLocalService.getStructureLinks(
					_classNameLocalService.getClassNameId(JournalFolder.class),
					folderId));

			folder = _updateFolder(
				userId, folderId, parentFolderId, name, description,
				ddmStructureIds, restrictionType, mergeWithParentFolder,
				serviceContext);
		}

		_updateWorkflowDefinitionLinks(
			userId, groupId, folderId, ddmStructureIds, restrictionType,
			serviceContext, originalDDMStructureIds);

		return folder;
	}

	@Override
	public void updateFolderDDMStructures(
			JournalFolder folder, long[] ddmStructureIdsArray)
		throws PortalException {

		Set<Long> ddmStructureIds = SetUtil.fromArray(ddmStructureIdsArray);

		List<DDMStructureLink> ddmStructureLinks =
			_ddmStructureLinkLocalService.getStructureLinks(
				_classNameLocalService.getClassNameId(JournalFolder.class),
				folder.getFolderId());

		Set<Long> originalDDMStructureIds = _getDDMStructureIds(
			ddmStructureLinks);

		if (ddmStructureIds.equals(originalDDMStructureIds)) {
			return;
		}

		for (Long ddmStructureId : ddmStructureIds) {
			if (!originalDDMStructureIds.contains(ddmStructureId)) {
				_ddmStructureLinkLocalService.addStructureLink(
					_classNameLocalService.getClassNameId(JournalFolder.class),
					folder.getFolderId(), ddmStructureId);
			}
		}

		for (Long originalDDMStructureId : originalDDMStructureIds) {
			if (!ddmStructureIds.contains(originalDDMStructureId)) {
				_ddmStructureLinkLocalService.deleteStructureLink(
					_classNameLocalService.getClassNameId(JournalFolder.class),
					folder.getFolderId(), originalDDMStructureId);
			}
		}
	}

	@Override
	public JournalFolder updateStatus(
			long userId, JournalFolder folder, int status)
		throws PortalException {

		// Folder

		User user = _userLocalService.getUser(userId);

		folder.setStatus(status);
		folder.setStatusByUserId(userId);
		folder.setStatusByUserName(user.getFullName());
		folder.setStatusDate(new Date());

		folder = journalFolderPersistence.update(folder);

		// Asset

		if (status == WorkflowConstants.STATUS_APPROVED) {
			_assetEntryLocalService.updateVisible(
				JournalFolder.class.getName(), folder.getFolderId(), true);
		}
		else if (status == WorkflowConstants.STATUS_IN_TRASH) {
			_assetEntryLocalService.updateVisible(
				JournalFolder.class.getName(), folder.getFolderId(), false);
		}

		// Indexer

		Indexer<JournalFolder> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			JournalFolder.class);

		indexer.reindex(folder);

		return folder;
	}

	@Override
	public void validateFolderDDMStructures(long folderId, long parentFolderId)
		throws PortalException {

		JournalFolderModelValidator journalFolderModelValidator =
			_getJournalFolderModelValidator();

		journalFolderModelValidator.validateFolderDDMStructures(
			folderId, parentFolderId);
	}

	private Set<Long> _getDDMStructureIds(
		List<DDMStructureLink> ddmStructureLinks) {

		Set<Long> ddmStructureIds = new HashSet<>();

		for (DDMStructureLink ddmStructureLink : ddmStructureLinks) {
			ddmStructureIds.add(ddmStructureLink.getStructureId());
		}

		return ddmStructureIds;
	}

	private JournalFolderModelValidator _getJournalFolderModelValidator() {
		ModelValidator<JournalFolder> modelValidator =
			ModelValidatorRegistryUtil.getModelValidator(JournalFolder.class);

		return (JournalFolderModelValidator)modelValidator;
	}

	private long _getParentFolderId(JournalFolder folder, long parentFolderId) {
		if (parentFolderId == JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return parentFolderId;
		}

		if (folder.getFolderId() == parentFolderId) {
			return folder.getParentFolderId();
		}

		JournalFolder parentFolder = journalFolderPersistence.fetchByPrimaryKey(
			parentFolderId);

		if ((parentFolder == null) ||
			(folder.getGroupId() != parentFolder.getGroupId())) {

			return folder.getParentFolderId();
		}

		List<Long> subfolderIds = new ArrayList<>();

		getSubfolderIds(
			subfolderIds, folder.getGroupId(), folder.getFolderId());

		if (subfolderIds.contains(parentFolderId)) {
			return folder.getParentFolderId();
		}

		return parentFolderId;
	}

	private long _getParentFolderId(long groupId, long parentFolderId) {
		if (parentFolderId != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			JournalFolder parentFolder =
				journalFolderPersistence.fetchByPrimaryKey(parentFolderId);

			if ((parentFolder == null) ||
				(groupId != parentFolder.getGroupId())) {

				parentFolderId =
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			}
		}

		return parentFolderId;
	}

	private JournalFolder _getRestrictedAncestorFolder(JournalFolder folder)
		throws PortalException {

		if (folder.getRestrictionType() ==
				JournalFolderConstants.
					RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW) {

			return folder;
		}

		if (folder.isRoot()) {
			return null;
		}

		return _getRestrictedAncestorFolder(folder.getParentFolder());
	}

	private void _mergeFolders(JournalFolder fromFolder, long toFolderId)
		throws PortalException {

		List<JournalFolder> folders = journalFolderPersistence.findByG_P(
			fromFolder.getGroupId(), fromFolder.getFolderId());

		for (JournalFolder folder : folders) {
			_mergeFolders(folder, toFolderId);
		}

		List<JournalArticle> articles = _journalArticlePersistence.findByG_F(
			fromFolder.getGroupId(), fromFolder.getFolderId());

		for (JournalArticle article : articles) {
			article.setFolderId(toFolderId);
			article.setTreePath(article.buildTreePath());

			article = _journalArticlePersistence.update(article);

			Indexer<JournalArticle> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(JournalArticle.class);

			indexer.reindex(article);
		}

		journalFolderLocalService.deleteFolder(fromFolder);
	}

	private void _moveDependentsToTrash(
			List<Object> foldersAndArticles, long trashEntryId)
		throws PortalException {

		for (Object object : foldersAndArticles) {
			if (object instanceof JournalArticle) {

				// Article

				JournalArticle article = (JournalArticle)object;

				if (article.getStatus() == WorkflowConstants.STATUS_IN_TRASH) {
					continue;
				}

				// Articles

				List<JournalArticle> articles =
					_journalArticlePersistence.findByG_A(
						article.getGroupId(), article.getArticleId());

				for (JournalArticle curArticle : articles) {

					// Article

					int curArticleOldStatus = curArticle.getStatus();

					curArticle.setStatus(WorkflowConstants.STATUS_IN_TRASH);

					curArticle = _journalArticlePersistence.update(curArticle);

					// Trash

					int status = curArticleOldStatus;

					if (curArticleOldStatus ==
							WorkflowConstants.STATUS_PENDING) {

						status = WorkflowConstants.STATUS_DRAFT;
					}

					if (curArticleOldStatus !=
							WorkflowConstants.STATUS_APPROVED) {

						_trashVersionLocalService.addTrashVersion(
							trashEntryId, JournalArticle.class.getName(),
							curArticle.getId(), status, null);
					}

					// Workflow

					if (curArticleOldStatus ==
							WorkflowConstants.STATUS_PENDING) {

						_workflowInstanceLinkLocalService.
							deleteWorkflowInstanceLink(
								curArticle.getCompanyId(),
								curArticle.getGroupId(),
								JournalArticle.class.getName(),
								curArticle.getId());
					}
				}

				// Asset

				_assetEntryLocalService.updateVisible(
					JournalArticle.class.getName(),
					article.getResourcePrimKey(), false);

				// Indexer

				Indexer<JournalArticle> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(
						JournalArticle.class);

				indexer.reindex(article);
			}
			else if (object instanceof JournalFolder) {

				// Folder

				JournalFolder folder = (JournalFolder)object;

				if (_trashHelper.isInTrashExplicitly(folder)) {
					continue;
				}

				int oldStatus = folder.getStatus();

				folder.setStatus(WorkflowConstants.STATUS_IN_TRASH);

				folder = journalFolderPersistence.update(folder);

				// Trash

				if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
					_trashVersionLocalService.addTrashVersion(
						trashEntryId, JournalFolder.class.getName(),
						folder.getFolderId(), oldStatus, null);
				}

				// Folders and articles

				List<Object> curFoldersAndArticles = getFoldersAndArticles(
					folder.getGroupId(), folder.getFolderId());

				_moveDependentsToTrash(curFoldersAndArticles, trashEntryId);

				// Asset

				_assetEntryLocalService.updateVisible(
					JournalFolder.class.getName(), folder.getFolderId(), false);

				// Indexer

				Indexer<JournalFolder> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(JournalFolder.class);

				indexer.reindex(folder);
			}
		}
	}

	private void _restoreDependentsFromTrash(List<Object> foldersAndArticles)
		throws PortalException {

		for (Object object : foldersAndArticles) {
			if (object instanceof JournalArticle) {

				// Article

				JournalArticle article = (JournalArticle)object;

				if (!_trashHelper.isInTrashImplicitly(article)) {
					continue;
				}

				TrashVersion trashVersion =
					_trashVersionLocalService.fetchVersion(
						JournalArticle.class.getName(), article.getId());

				int oldStatus = WorkflowConstants.STATUS_APPROVED;

				if (trashVersion != null) {
					oldStatus = trashVersion.getStatus();
				}

				// Articles

				List<JournalArticle> articles =
					_journalArticlePersistence.findByG_A(
						article.getGroupId(), article.getArticleId());

				for (JournalArticle curArticle : articles) {

					// Article

					trashVersion = _trashVersionLocalService.fetchVersion(
						JournalArticle.class.getName(), curArticle.getId());

					int curArticleOldStatus = WorkflowConstants.STATUS_APPROVED;

					if (trashVersion != null) {
						curArticleOldStatus = trashVersion.getStatus();
					}

					curArticle.setStatus(curArticleOldStatus);

					_journalArticlePersistence.update(curArticle);

					// Trash

					if (trashVersion != null) {
						_trashVersionLocalService.deleteTrashVersion(
							trashVersion);
					}
				}

				// Asset

				if (oldStatus == WorkflowConstants.STATUS_APPROVED) {
					_assetEntryLocalService.updateVisible(
						JournalArticle.class.getName(),
						article.getResourcePrimKey(), true);
				}

				// Indexer

				Indexer<JournalArticle> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(
						JournalArticle.class);

				indexer.reindex(article);
			}
			else if (object instanceof JournalFolder) {

				// Folder

				JournalFolder folder = (JournalFolder)object;

				if (!_trashHelper.isInTrashImplicitly(folder)) {
					continue;
				}

				TrashVersion trashVersion =
					_trashVersionLocalService.fetchVersion(
						JournalFolder.class.getName(), folder.getFolderId());

				int oldStatus = WorkflowConstants.STATUS_APPROVED;

				if (trashVersion != null) {
					oldStatus = trashVersion.getStatus();
				}

				folder.setStatus(oldStatus);

				folder = journalFolderPersistence.update(folder);

				// Folders and articles

				List<Object> curFoldersAndArticles = getFoldersAndArticles(
					folder.getGroupId(), folder.getFolderId(),
					WorkflowConstants.STATUS_IN_TRASH);

				_restoreDependentsFromTrash(curFoldersAndArticles);

				// Trash

				if (trashVersion != null) {
					_trashVersionLocalService.deleteTrashVersion(trashVersion);
				}

				// Asset

				_assetEntryLocalService.updateVisible(
					JournalFolder.class.getName(), folder.getFolderId(), true);

				// Indexer

				Indexer<JournalFolder> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(JournalFolder.class);

				indexer.reindex(folder);
			}
		}
	}

	private JournalFolder _updateFolder(
			long userId, long folderId, long parentFolderId, String name,
			String description, long[] ddmStructureIds, int restrictionType,
			boolean mergeWithParentFolder, ServiceContext serviceContext)
		throws PortalException {

		// Merge folders

		if ((restrictionType !=
				JournalFolderConstants.
					RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW) &&
			(parentFolderId !=
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

			JournalFolder restrictedAncestorFolder =
				_getRestrictedAncestorFolder(getFolder(parentFolderId));

			if (restrictedAncestorFolder != null) {
				_validateArticleDDMStructures(
					folderId,
					TransformUtil.transformToLongArray(
						_ddmStructureLinkLocalService.getStructureLinks(
							_classNameLocalService.getClassNameId(
								JournalFolder.class),
							restrictedAncestorFolder.getFolderId()),
						DDMStructureLink::getStructureId));
			}
		}

		_validateArticleDDMStructures(folderId, ddmStructureIds);

		JournalFolder folder = journalFolderPersistence.findByPrimaryKey(
			folderId);

		parentFolderId = _getParentFolderId(folder, parentFolderId);

		if (mergeWithParentFolder && (folderId != parentFolderId)) {
			_mergeFolders(folder, parentFolderId);

			return folder;
		}

		// Folder

		_validateFolder(folderId, folder.getGroupId(), parentFolderId, name);

		long oldParentFolderId = folder.getParentFolderId();

		if (oldParentFolderId != parentFolderId) {
			folder.setParentFolderId(parentFolderId);
			folder.setTreePath(folder.buildTreePath());
		}

		folder.setName(name);
		folder.setDescription(description);
		folder.setRestrictionType(restrictionType);

		User user = _userLocalService.getUser(userId);

		folder.setStatusByUserId(user.getUserId());
		folder.setStatusByUserName(user.getFullName());

		folder.setStatusDate(serviceContext.getModifiedDate(new Date()));
		folder.setExpandoBridgeAttributes(serviceContext);

		folder = journalFolderPersistence.update(folder);

		// Asset

		updateAsset(
			userId, folder, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Dynamic data mapping

		if (ddmStructureIds != null) {
			updateFolderDDMStructures(folder, ddmStructureIds);
		}

		if (oldParentFolderId != parentFolderId) {
			rebuildTree(
				folder.getCompanyId(), folderId, folder.getTreePath(), true);

			folder = journalFolderPersistence.findByPrimaryKey(
				folder.getPrimaryKey());
		}

		return folder;
	}

	private void _updateWorkflowDefinitionLinks(
			long userId, long groupId, long folderId, long[] ddmStructureIds,
			int restrictionType, ServiceContext serviceContext,
			Set<Long> originalDDMStructureIds)
		throws PortalException {

		if (!GetterUtil.getBoolean(
				serviceContext.getAttribute("updateWorkflowDefinitionLinks"),
				true)) {

			return;
		}

		List<ObjectValuePair<Long, String>> workflowDefinitionOVPs =
			new ArrayList<>();

		if (restrictionType ==
				JournalFolderConstants.
					RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW) {

			workflowDefinitionOVPs.add(
				new ObjectValuePair<Long, String>(
					JournalArticleConstants.DDM_STRUCTURE_ID_ALL,
					StringPool.BLANK));

			for (long ddmStructureId : ddmStructureIds) {
				String workflowDefinition = ParamUtil.getString(
					serviceContext, "workflowDefinition" + ddmStructureId);

				workflowDefinitionOVPs.add(
					new ObjectValuePair<Long, String>(
						ddmStructureId, workflowDefinition));
			}
		}
		else if (restrictionType ==
					JournalFolderConstants.RESTRICTION_TYPE_INHERIT) {

			if (originalDDMStructureIds.isEmpty()) {
				originalDDMStructureIds.add(
					JournalArticleConstants.DDM_STRUCTURE_ID_ALL);
			}

			for (long originalDDMStructureId : originalDDMStructureIds) {
				workflowDefinitionOVPs.add(
					new ObjectValuePair<Long, String>(
						originalDDMStructureId, StringPool.BLANK));
			}
		}
		else if (restrictionType ==
					JournalFolderConstants.RESTRICTION_TYPE_WORKFLOW) {

			String workflowDefinition = ParamUtil.getString(
				serviceContext,
				"workflowDefinition" +
					JournalArticleConstants.DDM_STRUCTURE_ID_ALL);

			workflowDefinitionOVPs.add(
				new ObjectValuePair<Long, String>(
					JournalArticleConstants.DDM_STRUCTURE_ID_ALL,
					workflowDefinition));

			for (long originalDDMStructureId : originalDDMStructureIds) {
				workflowDefinitionOVPs.add(
					new ObjectValuePair<Long, String>(
						originalDDMStructureId, StringPool.BLANK));
			}
		}

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLinks(
			userId, serviceContext.getCompanyId(), groupId,
			JournalFolder.class.getName(), folderId, workflowDefinitionOVPs);
	}

	private void _validateArticleDDMStructures(
			long folderId, long[] ddmStructureIds)
		throws PortalException {

		JournalFolderModelValidator journalFolderModelValidator =
			_getJournalFolderModelValidator();

		journalFolderModelValidator.validateArticleDDMStructures(
			folderId, ddmStructureIds);
	}

	private void _validateExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		JournalFolder journalFolder = journalFolderPersistence.fetchByERC_G(
			externalReferenceCode, groupId);

		if (journalFolder != null) {
			throw new DuplicateFolderExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate journal folder external reference code ",
					externalReferenceCode, " in group ", groupId));
		}
	}

	private void _validateFolder(
			long folderId, long groupId, long parentFolderId, String name)
		throws PortalException {

		JournalFolderModelValidator journalFolderModelValidator =
			_getJournalFolderModelValidator();

		journalFolderModelValidator.validateFolder(
			folderId, groupId, parentFolderId, name);
	}

	private void _validateParentFolder(
			JournalFolder folder, long parentFolderId)
		throws PortalException {

		JournalFolderModelValidator journalFolderModelValidator =
			_getJournalFolderModelValidator();

		journalFolderModelValidator.validateParentFolder(
			folder, parentFolderId);
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDMStructureLinkLocalService _ddmStructureLinkLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private JournalArticleFinder _journalArticleFinder;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalArticlePersistence _journalArticlePersistence;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private TrashVersionLocalService _trashVersionLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}