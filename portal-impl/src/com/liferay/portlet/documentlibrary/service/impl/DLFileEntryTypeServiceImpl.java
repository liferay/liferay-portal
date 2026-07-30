/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeTable;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portlet.documentlibrary.service.base.DLFileEntryTypeServiceBaseImpl;
import com.liferay.portlet.documentlibrary.util.DLPortletResourcePermissionUtil;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Provides the remote service for accessing, adding, deleting, and updating
 * file and folder file entry types. Its methods include permission checks.
 *
 * @author Alexander Chow
 */
public class DLFileEntryTypeServiceImpl extends DLFileEntryTypeServiceBaseImpl {

	@Override
	public DLFileEntryType addFileEntryType(
			String externalReferenceCode, long groupId, long dataDefinitionId,
			String fileEntryTypeKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			DLPortletResourcePermissionUtil.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_DOCUMENT_TYPE);

		return dlFileEntryTypeLocalService.addFileEntryType(
			externalReferenceCode, getUserId(), groupId, dataDefinitionId,
			fileEntryTypeKey, nameMap, descriptionMap, serviceContext);
	}

	@Override
	public void deleteFileEntryType(long fileEntryTypeId)
		throws PortalException {

		ModelResourcePermission<DLFileEntryType>
			dlFileEntryTypeModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFileEntryType.class.getName());

		dlFileEntryTypeModelResourcePermission.check(
			getPermissionChecker(), fileEntryTypeId, ActionKeys.DELETE);

		dlFileEntryTypeLocalService.deleteFileEntryType(fileEntryTypeId);
	}

	@Override
	public void deleteFileEntryTypeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		ModelResourcePermission<DLFileEntryType>
			dlFileEntryTypeModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFileEntryType.class.getName());

		DLFileEntryType dlFileEntryType =
			dlFileEntryTypePersistence.findByERC_G(
				externalReferenceCode, groupId);

		dlFileEntryTypeModelResourcePermission.check(
			getPermissionChecker(), dlFileEntryType, ActionKeys.DELETE);

		dlFileEntryTypeLocalService.deleteFileEntryType(dlFileEntryType);
	}

	@Override
	public DLFileEntryType fetchFileEntryTypeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		DLFileEntryType dlFileEntryType =
			dlFileEntryTypePersistence.fetchByERC_G(
				externalReferenceCode, groupId);

		if (dlFileEntryType == null) {
			return null;
		}

		if (dlFileEntryType.getFileEntryTypeId() ==
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

			return dlFileEntryType;
		}

		ModelResourcePermission<DLFileEntryType>
			dlFileEntryTypeModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFileEntryType.class.getName());

		dlFileEntryTypeModelResourcePermission.check(
			getPermissionChecker(), dlFileEntryType, ActionKeys.VIEW);

		return dlFileEntryType;
	}

	@Override
	public DLFileEntryType getFileEntryType(long fileEntryTypeId)
		throws PortalException {

		if (fileEntryTypeId !=
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

			ModelResourcePermission<DLFileEntryType>
				dlFileEntryTypeModelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(
							DLFileEntryType.class.getName());

			dlFileEntryTypeModelResourcePermission.check(
				getPermissionChecker(), fileEntryTypeId, ActionKeys.VIEW);
		}

		return dlFileEntryTypePersistence.findByPrimaryKey(fileEntryTypeId);
	}

	@Override
	public DLFileEntryType getFileEntryTypeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		DLFileEntryType dlFileEntryType =
			dlFileEntryTypePersistence.findByERC_G(
				externalReferenceCode, groupId);

		if (dlFileEntryType.getFileEntryTypeId() ==
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

			return dlFileEntryType;
		}

		ModelResourcePermission<DLFileEntryType>
			dlFileEntryTypeModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFileEntryType.class.getName());

		dlFileEntryTypeModelResourcePermission.check(
			getPermissionChecker(), dlFileEntryType, ActionKeys.VIEW);

		return dlFileEntryType;
	}

	@Override
	public List<DLFileEntryType> getFileEntryTypes(long[] groupIds) {
		List<DLFileEntryType> dlFileEntryTypes = new ArrayList<>(
			dlFileEntryTypePersistence.filterFindByGroupId(groupIds));

		DLFileEntryType basicDocumentDLFileEntryType =
			dlFileEntryTypePersistence.fetchByPrimaryKey(
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);

		if (basicDocumentDLFileEntryType != null) {
			dlFileEntryTypes.add(0, basicDocumentDLFileEntryType);
		}

		return dlFileEntryTypes;
	}

	@Override
	public List<DLFileEntryType> getFileEntryTypes(
		long[] groupIds, int start, int end) {

		return dlFileEntryTypePersistence.filterFindByGroupId(
			groupIds, start, end);
	}

	@Override
	public int getFileEntryTypesCount(long[] groupIds) {
		return dlFileEntryTypePersistence.filterCountByGroupId(groupIds);
	}

	@Override
	public List<DLFileEntryType> getFolderFileEntryTypes(
			long[] groupIds, long folderId, boolean inherited)
		throws PortalException {

		return filterFileEntryTypes(
			dlFileEntryTypeLocalService.getFolderFileEntryTypes(
				groupIds, folderId, inherited));
	}

	@Override
	public List<DLFileEntryType> search(
			long companyId, long folderId, long[] groupIds, String keywords,
			boolean includeBasicFileEntryType, boolean inherited, int start,
			int end)
		throws PortalException {

		return dlFileEntryTypeFinder.filterFindByKeywords(
			companyId, folderId, groupIds, keywords, includeBasicFileEntryType,
			inherited, start, end);
	}

	@Override
	public List<DLFileEntryType> search(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, int scope, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator) {

		return dlFileEntryTypePersistence.dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					DLFileEntryTypeTable.INSTANCE),
				companyId, groupIds, keywords, includeBasicFileEntryType, scope
			).orderBy(
				DLFileEntryTypeTable.INSTANCE, orderByComparator
			).limit(
				start, end
			));
	}

	@Override
	public List<DLFileEntryType> search(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator) {

		return dlFileEntryTypeFinder.filterFindByKeywords(
			companyId, groupIds, keywords, includeBasicFileEntryType, start,
			end, orderByComparator);
	}

	@Override
	public int searchCount(
		long companyId, long folderId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, boolean inherited) {

		return dlFileEntryTypeFinder.filterCountByKeywords(
			companyId, folderId, groupIds, keywords, includeBasicFileEntryType,
			inherited);
	}

	@Override
	public int searchCount(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType) {

		return dlFileEntryTypeFinder.filterCountByKeywords(
			companyId, groupIds, keywords, includeBasicFileEntryType);
	}

	@Override
	public int searchCount(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, int scope) {

		return dlFileEntryTypePersistence.dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					DLFileEntryTypeTable.INSTANCE.fileEntryTypeId),
				companyId, groupIds, keywords, includeBasicFileEntryType,
				scope));
	}

	@Override
	public DLFileEntryType updateFileEntryType(
			long fileEntryTypeId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap)
		throws PortalException {

		ModelResourcePermission<DLFileEntryType>
			dlFileEntryTypeModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFileEntryType.class.getName());

		dlFileEntryTypeModelResourcePermission.check(
			getPermissionChecker(), fileEntryTypeId, ActionKeys.UPDATE);

		return dlFileEntryTypeLocalService.updateFileEntryType(
			fileEntryTypeId, nameMap, descriptionMap);
	}

	protected List<DLFileEntryType> filterFileEntryTypes(
			List<DLFileEntryType> fileEntryTypes)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		fileEntryTypes = ListUtil.copy(fileEntryTypes);

		Iterator<DLFileEntryType> iterator = fileEntryTypes.iterator();

		while (iterator.hasNext()) {
			ModelResourcePermission<DLFileEntryType>
				dlFileEntryTypeModelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(
							DLFileEntryType.class.getName());

			DLFileEntryType fileEntryType = iterator.next();

			if ((fileEntryType.getFileEntryTypeId() > 0) &&
				!dlFileEntryTypeModelResourcePermission.contains(
					permissionChecker, fileEntryType, ActionKeys.VIEW)) {

				iterator.remove();
			}
		}

		return fileEntryTypes;
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, int scope) {

		return fromStep.from(
			DLFileEntryTypeTable.INSTANCE
		).where(
			() -> {
				Predicate predicate =
					DLFileEntryTypeTable.INSTANCE.companyId.eq(companyId);

				Predicate groupIdsPredicate = null;

				for (long groupId : groupIds) {
					Predicate groupIdPredicate =
						DLFileEntryTypeTable.INSTANCE.groupId.eq(groupId);

					if (groupIdsPredicate == null) {
						groupIdsPredicate = groupIdPredicate;
					}
					else {
						groupIdsPredicate = groupIdsPredicate.or(
							groupIdPredicate);
					}
				}

				if (groupIdsPredicate != null) {
					predicate = predicate.and(
						groupIdsPredicate.withParentheses());
				}

				if (includeBasicFileEntryType) {
					predicate = predicate.withParentheses(
					).or(
						DLFileEntryTypeTable.INSTANCE.groupId.eq(0L)
					);
				}

				predicate = predicate.withParentheses(
				).and(
					DLFileEntryTypeTable.INSTANCE.scope.eq(scope)
				);

				Predicate keywordsPredicate = null;

				for (String keyword : CustomSQLUtil.keywords(keywords, true)) {
					if (keyword == null) {
						continue;
					}

					Predicate keywordPredicate = DSLFunctionFactoryUtil.lower(
						DLFileEntryTypeTable.INSTANCE.name
					).like(
						keyword
					).or(
						DSLFunctionFactoryUtil.lower(
							DLFileEntryTypeTable.INSTANCE.description
						).like(
							keyword
						)
					);

					if (keywordsPredicate == null) {
						keywordsPredicate = keywordPredicate;
					}
					else {
						keywordsPredicate = keywordsPredicate.or(
							keywordPredicate);
					}
				}

				if (keywordsPredicate != null) {
					predicate = predicate.and(
						keywordsPredicate.withParentheses());
				}

				return predicate;
			}
		);
	}

}