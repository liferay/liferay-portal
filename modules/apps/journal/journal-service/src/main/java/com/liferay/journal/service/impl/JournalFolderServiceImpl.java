/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.util.comparator.StructureLinkStructureModifiedDateComparator;
import com.liferay.dynamic.data.mapping.util.comparator.StructureLinkStructureNameComparator;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.base.JournalFolderServiceBaseImpl;
import com.liferay.journal.service.persistence.JournalArticleFinder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan Fernández
 */
@Component(
	property = {
		"json.web.service.context.name=journal",
		"json.web.service.context.path=JournalFolder"
	},
	service = AopService.class
)
public class JournalFolderServiceImpl extends JournalFolderServiceBaseImpl {

	@Override
	public JournalFolder addFolder(
			String externalReferenceCode, long groupId, long parentFolderId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_journalFolderModelResourcePermission, getPermissionChecker(),
			groupId, parentFolderId, ActionKeys.ADD_FOLDER);

		return journalFolderLocalService.addFolder(
			externalReferenceCode, getUserId(), groupId, parentFolderId, name,
			description, serviceContext);
	}

	@Override
	public void deleteFolder(long folderId) throws PortalException {
		_journalFolderModelResourcePermission.check(
			getPermissionChecker(),
			journalFolderPersistence.findByPrimaryKey(folderId),
			ActionKeys.DELETE);

		journalFolderLocalService.deleteFolder(folderId);
	}

	@Override
	public void deleteFolder(long folderId, boolean includeTrashedEntries)
		throws PortalException {

		_journalFolderModelResourcePermission.check(
			getPermissionChecker(),
			journalFolderPersistence.findByPrimaryKey(folderId),
			ActionKeys.DELETE);

		journalFolderLocalService.deleteFolder(folderId, includeTrashedEntries);
	}

	@Override
	public JournalFolder fetchFolder(long folderId) throws PortalException {
		JournalFolder folder = journalFolderPersistence.fetchByPrimaryKey(
			folderId);

		if (folder != null) {
			_journalFolderModelResourcePermission.check(
				getPermissionChecker(), folder, ActionKeys.VIEW);
		}

		return folder;
	}

	@Override
	public List<DDMStructure> getDDMStructures(
			long[] groupIds, long folderId, int restrictionType)
		throws PortalException {

		return _filterStructures(
			journalFolderLocalService.getDDMStructures(
				groupIds, folderId, restrictionType));
	}

	@Override
	public List<DDMStructure> getDDMStructures(
			long[] groupIds, long folderId, int restrictionType,
			OrderByComparator<DDMStructure> orderByComparator)
		throws PortalException {

		return _filterStructures(
			journalFolderLocalService.getDDMStructures(
				groupIds, folderId, restrictionType, orderByComparator));
	}

	@Override
	public JournalFolder getFolder(long folderId) throws PortalException {
		JournalFolder folder = journalFolderPersistence.findByPrimaryKey(
			folderId);

		_journalFolderModelResourcePermission.check(
			getPermissionChecker(), folder, ActionKeys.VIEW);

		return folder;
	}

	@Override
	public JournalFolder getFolderByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		JournalFolder folder = journalFolderPersistence.findByERC_G(
			externalReferenceCode, groupId);

		_journalFolderModelResourcePermission.check(
			getPermissionChecker(), folder, ActionKeys.VIEW);

		return folder;
	}

	@Override
	public List<Long> getFolderIds(long groupId, long folderId)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_journalFolderModelResourcePermission, getPermissionChecker(),
			groupId, folderId, ActionKeys.VIEW);

		List<Long> folderIds = getSubfolderIds(groupId, folderId, true);

		folderIds.add(0, folderId);

		return folderIds;
	}

	@Override
	public List<JournalFolder> getFolders(long groupId) {
		return journalFolderPersistence.filterFindByGroupId(groupId);
	}

	@Override
	public List<JournalFolder> getFolders(long groupId, long parentFolderId) {
		return getFolders(
			groupId, parentFolderId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int status) {

		return journalFolderPersistence.filterFindByG_P_S(
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

		return journalFolderPersistence.filterFindByG_P_S(
			groupId, parentFolderId, status, start, end);
	}

	@Override
	public List<Object> getFoldersAndArticles(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<?> orderByComparator) {

		return getFoldersAndArticles(
			groupId, 0, folderId, status, start, end, orderByComparator);
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
	public List<Object> getFoldersAndArticles(
		long groupId, long userId, long folderId, int status, int start,
		int end, OrderByComparator<?> orderByComparator) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, userId, true, start, end,
			(OrderByComparator<Object>)orderByComparator);

		return journalFolderFinder.filterFindF_A_ByG_F_DDMSI(
			groupId, folderId, 0, queryDefinition);
	}

	@Override
	public List<Object> getFoldersAndArticles(
		long groupId, long userId, long folderId, int status, Locale locale,
		int start, int end, OrderByComparator<?> orderByComparator) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, userId, true, start, end,
			(OrderByComparator<Object>)orderByComparator);

		return journalFolderFinder.filterFindF_A_ByG_F_DDMSI_L(
			groupId, folderId, 0, locale, queryDefinition);
	}

	@Override
	public List<Object> getFoldersAndArticles(
		long groupId, long userId, long folderId, long ddmStructureId,
		int status, Locale locale, int start, int end,
		OrderByComparator<?> orderByComparator) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, userId, true, start, end,
			(OrderByComparator<Object>)orderByComparator);

		return journalFolderFinder.filterFindF_A_ByG_F_DDMSI_L(
			groupId, folderId, ddmStructureId, locale, queryDefinition);
	}

	@Override
	public List<Object> getFoldersAndArticles(
		long groupId, long userId, long folderId, long ddmStructureId,
		int status, Locale locale, int[] excludedStatuses, int start, int end,
		OrderByComparator<?> orderByComparator) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, userId, true, start, end,
			(OrderByComparator<Object>)orderByComparator);

		return journalFolderFinder.filterFindF_A_ByG_F_DDMSI_L_NotS(
			groupId, folderId, ddmStructureId, locale, excludedStatuses,
			queryDefinition);
	}

	@Override
	public int getFoldersAndArticlesCount(
		long groupId, List<Long> folderIds, int status) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status);

		if (folderIds.size() <= DBManagerUtil.getDBMaxParameters()) {
			return _journalArticleFinder.filterCountByG_F(
				groupId, folderIds, queryDefinition);
		}

		int start = 0;
		int end = DBManagerUtil.getDBMaxParameters();

		int articlesCount = _journalArticleFinder.filterCountByG_F(
			groupId, folderIds.subList(start, end), queryDefinition);

		List<Long> sublist = folderIds.subList(start, end);

		sublist.clear();

		articlesCount += getFoldersAndArticlesCount(groupId, folderIds, status);

		return articlesCount;
	}

	@Override
	public int getFoldersAndArticlesCount(long groupId, long folderId) {
		return getFoldersAndArticlesCount(
			groupId, folderId, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public int getFoldersAndArticlesCount(
		long groupId, long folderId, int status) {

		return getFoldersAndArticlesCount(groupId, 0, folderId, status);
	}

	@Override
	public int getFoldersAndArticlesCount(
		long groupId, long userId, long folderId, int status) {

		QueryDefinition<Object> queryDefinition = new QueryDefinition<>(
			status, userId, true);

		return journalFolderFinder.filterCountF_A_ByG_F_DDMSI(
			groupId, folderId, 0, queryDefinition);
	}

	@Override
	public int getFoldersAndArticlesCount(
		long groupId, long userId, long folderId, long ddmStructureId,
		int status) {

		QueryDefinition<Object> queryDefinition = new QueryDefinition<>(
			status, userId, true);

		return journalFolderFinder.filterCountF_A_ByG_F_DDMSI(
			groupId, folderId, ddmStructureId, queryDefinition);
	}

	@Override
	public int getFoldersAndArticlesCount(
		long groupId, long userId, long folderId, long ddmStructureId,
		int[] excludedStatuses, int status) {

		QueryDefinition<Object> queryDefinition = new QueryDefinition<>(
			status, userId, true);

		return journalFolderFinder.filterCountF_A_ByG_F_DDMSI_NotS(
			groupId, folderId, ddmStructureId, excludedStatuses,
			queryDefinition);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId) {
		return getFoldersCount(
			groupId, parentFolderId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return journalFolderPersistence.filterCountByG_P_NotS(
				groupId, parentFolderId, WorkflowConstants.STATUS_IN_TRASH);
		}

		return journalFolderPersistence.filterCountByG_P_S(
			groupId, parentFolderId, status);
	}

	@Override
	public void getSubfolderIds(
		List<Long> folderIds, long groupId, long folderId, boolean recurse) {

		List<JournalFolder> folders =
			journalFolderPersistence.filterFindByG_P_NotS(
				groupId, folderId, WorkflowConstants.STATUS_IN_TRASH);

		for (JournalFolder folder : folders) {
			folderIds.add(folder.getFolderId());

			if (recurse) {
				getSubfolderIds(
					folderIds, folder.getGroupId(), folder.getFolderId(),
					recurse);
			}
		}
	}

	@Override
	public List<Long> getSubfolderIds(
		long groupId, long folderId, boolean recurse) {

		List<Long> folderIds = new ArrayList<>();

		getSubfolderIds(folderIds, groupId, folderId, recurse);

		return folderIds;
	}

	@Override
	public JournalFolder moveFolder(
			long folderId, long parentFolderId, ServiceContext serviceContext)
		throws PortalException {

		_journalFolderModelResourcePermission.check(
			getPermissionChecker(),
			journalFolderPersistence.findByPrimaryKey(folderId),
			ActionKeys.UPDATE);

		return journalFolderLocalService.moveFolder(
			folderId, parentFolderId, serviceContext);
	}

	@Override
	public JournalFolder moveFolderFromTrash(
			long folderId, long parentFolderId, ServiceContext serviceContext)
		throws PortalException {

		_journalFolderModelResourcePermission.check(
			getPermissionChecker(),
			journalFolderPersistence.findByPrimaryKey(folderId),
			ActionKeys.UPDATE);

		return journalFolderLocalService.moveFolderFromTrash(
			getUserId(), folderId, parentFolderId, serviceContext);
	}

	@Override
	public JournalFolder moveFolderToTrash(long folderId)
		throws PortalException {

		_journalFolderModelResourcePermission.check(
			getPermissionChecker(),
			journalFolderPersistence.findByPrimaryKey(folderId),
			ActionKeys.DELETE);

		return journalFolderLocalService.moveFolderToTrash(
			getUserId(), folderId);
	}

	@Override
	public void restoreFolderFromTrash(long folderId) throws PortalException {
		_journalFolderModelResourcePermission.check(
			getPermissionChecker(),
			journalFolderPersistence.findByPrimaryKey(folderId),
			ActionKeys.UPDATE);

		journalFolderLocalService.restoreFolderFromTrash(getUserId(), folderId);
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

			return TransformUtil.transform(
				_ddmStructureLinkService.getStructureLinks(
					_classNameLocalService.getClassNameId(JournalFolder.class),
					folderId, groupIds, keywords,
					JournalArticle.class.getName(), start, end,
					_getDDMStructureLinkOrderByComparator(orderByComparator)),
				structureLink -> structureLink.getStructure());
		}

		folderId = journalFolderLocalService.getOverridedDDMStructuresFolderId(
			folderId);

		if (folderId != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return TransformUtil.transform(
				_ddmStructureLinkService.getStructureLinks(
					_classNameLocalService.getClassNameId(JournalFolder.class),
					folderId, groupIds, keywords,
					JournalArticle.class.getName(), start, end,
					_getDDMStructureLinkOrderByComparator(orderByComparator)),
				structureLink -> structureLink.getStructure());
		}

		return _ddmStructureService.search(
			companyId, groupIds,
			_classNameLocalService.getClassNameId(JournalArticle.class),
			keywords, WorkflowConstants.STATUS_ANY, start, end,
			orderByComparator);
	}

	@Override
	public int searchDDMStructuresCount(
			long companyId, long[] groupIds, long folderId, int restrictionType,
			String keywords)
		throws PortalException {

		if (restrictionType ==
				JournalFolderConstants.
					RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW) {

			return _ddmStructureLinkService.getStructureLinksCount(
				_classNameLocalService.getClassNameId(JournalFolder.class),
				folderId, groupIds, keywords, JournalArticle.class.getName());
		}

		folderId = journalFolderLocalService.getOverridedDDMStructuresFolderId(
			folderId);

		if (folderId != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return _ddmStructureLinkService.getStructureLinksCount(
				_classNameLocalService.getClassNameId(JournalFolder.class),
				folderId, groupIds, keywords, JournalArticle.class.getName());
		}

		return _ddmStructureService.searchCount(
			companyId, groupIds,
			_classNameLocalService.getClassNameId(JournalArticle.class),
			keywords, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public void subscribe(long groupId, long folderId) throws PortalException {
		ModelResourcePermissionUtil.check(
			_journalFolderModelResourcePermission, getPermissionChecker(),
			groupId, folderId, ActionKeys.SUBSCRIBE);

		journalFolderLocalService.subscribe(getUserId(), groupId, folderId);
	}

	@Override
	public void unsubscribe(long groupId, long folderId)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_journalFolderModelResourcePermission, getPermissionChecker(),
			groupId, folderId, ActionKeys.SUBSCRIBE);

		journalFolderLocalService.unsubscribe(getUserId(), groupId, folderId);
	}

	@Override
	public JournalFolder updateFolder(
			long groupId, long folderId, long parentFolderId, String name,
			String description, boolean mergeWithParentFolder,
			ServiceContext serviceContext)
		throws PortalException {

		JournalFolder folder = getFolder(folderId);

		return updateFolder(
			groupId, folderId, parentFolderId, name, description,
			TransformUtil.transformToLongArray(
				_ddmStructureLinkLocalService.getStructureLinks(
					_classNameLocalService.getClassNameId(JournalFolder.class),
					folderId),
				ddmStructureLink -> ddmStructureLink.getStructureId()),
			folder.getRestrictionType(), mergeWithParentFolder, serviceContext);
	}

	@Override
	public JournalFolder updateFolder(
			long groupId, long folderId, long parentFolderId, String name,
			String description, long[] ddmStructureIds, int restrictionType,
			boolean mergeWithParentFolder, ServiceContext serviceContext)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (ModelResourcePermissionUtil.contains(
				_journalFolderModelResourcePermission, permissionChecker,
				groupId, folderId, ActionKeys.ADVANCED_UPDATE) ||
			ModelResourcePermissionUtil.contains(
				_journalFolderModelResourcePermission, permissionChecker,
				groupId, folderId, ActionKeys.UPDATE)) {

			if (folderId == JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				return journalFolderLocalService.updateFolder(
					getUserId(), groupId, folderId, parentFolderId, name,
					description, ddmStructureIds, restrictionType,
					mergeWithParentFolder, serviceContext);
			}

			JournalFolder folder = getFolder(folderId);

			if (!ModelResourcePermissionUtil.contains(
					_journalFolderModelResourcePermission, permissionChecker,
					groupId, folderId, ActionKeys.ADVANCED_UPDATE)) {

				ddmStructureIds = TransformUtil.transformToLongArray(
					_ddmStructureLinkLocalService.getStructureLinks(
						_classNameLocalService.getClassNameId(
							JournalFolder.class),
						folderId),
					ddmStructureLink -> ddmStructureLink.getStructureId());

				restrictionType = folder.getRestrictionType();

				serviceContext.setAttribute(
					"updateWorkflowDefinitionLinks", Boolean.FALSE);
			}

			if (!ModelResourcePermissionUtil.contains(
					_journalFolderModelResourcePermission, permissionChecker,
					groupId, folderId, ActionKeys.UPDATE)) {

				parentFolderId = folder.getParentFolderId();
				name = folder.getName();
				description = folder.getDescription();
			}

			return journalFolderLocalService.updateFolder(
				getUserId(), groupId, folderId, parentFolderId, name,
				description, ddmStructureIds, restrictionType,
				mergeWithParentFolder, serviceContext);
		}

		throw new PrincipalException.MustHavePermission(
			permissionChecker, JournalFolder.class.getName(), folderId,
			ActionKeys.ADVANCED_UPDATE, ActionKeys.UPDATE);
	}

	private List<DDMStructure> _filterStructures(
			List<DDMStructure> ddmStructures)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		ddmStructures = ListUtil.copy(ddmStructures);

		Iterator<DDMStructure> iterator = ddmStructures.iterator();

		while (iterator.hasNext()) {
			DDMStructure ddmStructure = iterator.next();

			if (!_ddmStructureModelResourcePermission.contains(
					permissionChecker, ddmStructure, ActionKeys.VIEW)) {

				iterator.remove();
			}
		}

		return ddmStructures;
	}

	private OrderByComparator<DDMStructureLink>
		_getDDMStructureLinkOrderByComparator(
			OrderByComparator<DDMStructure> orderByComparator) {

		if (orderByComparator == null) {
			return null;
		}

		if (ArrayUtil.contains(
				orderByComparator.getOrderByFields(), "modifiedDate")) {

			return StructureLinkStructureModifiedDateComparator.getInstance(
				orderByComparator.isAscending());
		}

		if (!ArrayUtil.contains(orderByComparator.getOrderByFields(), "name")) {
			return null;
		}

		return new StructureLinkStructureNameComparator(
			orderByComparator.isAscending());
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDMStructureLinkLocalService _ddmStructureLinkLocalService;

	@Reference
	private DDMStructureLinkService _ddmStructureLinkService;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMStructure)"
	)
	private ModelResourcePermission<DDMStructure>
		_ddmStructureModelResourcePermission;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private JournalArticleFinder _journalArticleFinder;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalFolder)"
	)
	private ModelResourcePermission<JournalFolder>
		_journalFolderModelResourcePermission;

}