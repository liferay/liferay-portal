/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.dto.v1_0.UserGroup;
import com.liferay.headless.asset.library.resource.v1_0.UserGroupResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Roberto Díaz
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0_0/user-group.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = UserGroupResource.class
)
public class UserGroupResourceImpl extends BaseUserGroupResourceImpl {

	@Override
	public void
			deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String userGroupExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group group = _getGroupByExternalReferenceCode(
			assetLibraryExternalReferenceCode);

		if (group == null) {
			return;
		}

		_userGroupService.unsetGroupUserGroups(
			group.getGroupId(),
			new long[] {
				_getUserGroupIdByExternalReferenceCode(
					userGroupExternalReferenceCode)
			});
	}

	@Override
	public void deleteAssetLibraryUserGroup(
			Long assetLibraryId, Long userGroupId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		_userGroupService.unsetGroupUserGroups(
			assetLibraryId, new long[] {userGroupId});
	}

	@Override
	public Page<UserGroup> getAssetLibraryByExternalReferenceCodeUserGroupsPage(
			String externalReferenceCode, String keywords, String search,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group group = _getGroupByExternalReferenceCode(externalReferenceCode);

		if (group == null) {
			return null;
		}

		return getAssetLibraryUserGroupsPage(
			group.getGroupId(), keywords, search, filter, pagination, sorts);
	}

	@NestedField(parentClass = AssetLibrary.class, value = "userGroups")
	@Override
	public Page<UserGroup> getAssetLibraryUserGroupsPage(
			@NestedFieldId("id") Long assetLibraryId, String keywords,
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"get",
				addAction(
					ActionKeys.VIEW, 0L, "getAssetLibraryUserGroupsPage",
					_userGroupModelResourcePermission)
			).build(),
			booleanQuery -> {
			},
			filter, com.liferay.portal.kernel.model.UserGroup.class.getName(),
			search, pagination,
			queryConfig -> {
			},
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}
			},
			sorts,
			document -> _toUserGroup(
				_userGroupService.getUserGroup(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public void
			postAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode(
				String assetLibraryExternalReferenceCode,
				String userGroupExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		Group group = _getGroupByExternalReferenceCode(
			assetLibraryExternalReferenceCode);

		if (group == null) {
			return;
		}

		_userGroupService.addGroupUserGroups(
			group.getGroupId(),
			new long[] {
				_getUserGroupIdByExternalReferenceCode(
					userGroupExternalReferenceCode)
			});
	}

	@Override
	public void postAssetLibraryUserGroup(Long assetLibraryId, Long userGroupId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		_userGroupService.addGroupUserGroups(
			assetLibraryId, new long[] {userGroupId});
	}

	private DTOConverterContext _getDTOConverterContext(long userGroupId) {
		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(),
			HashMapBuilder.put(
				"delete-asset-library-user-group",
				addAction(
					ActionKeys.ASSIGN_MEMBERS, userGroupId,
					"deleteAssetLibraryUserGroup",
					_userGroupModelResourcePermission)
			).put(
				"post-asset-library-user-group",
				addAction(
					ActionKeys.ASSIGN_MEMBERS, userGroupId,
					"postAssetLibraryUserGroup",
					_userGroupModelResourcePermission)
			).build(),
			null, contextHttpServletRequest, userGroupId,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private Group _getGroupByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (_groupModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				group.getGroupId(), ActionKeys.VIEW)) {

			return group;
		}

		return null;
	}

	private long _getUserGroupIdByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		com.liferay.portal.kernel.model.UserGroup userGroup =
			_userGroupService.getUserGroupByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return userGroup.getUserGroupId();
	}

	private UserGroup _toUserGroup(
			com.liferay.portal.kernel.model.UserGroup userGroup)
		throws Exception {

		return _userGroupDTOConverter.toDTO(
			_getDTOConverterContext(userGroup.getUserGroupId()), userGroup);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Group)"
	)
	private ModelResourcePermission<Group> _groupModelResourcePermission;

	@Reference(
		target = "(component.name=com.liferay.headless.asset.library.internal.dto.v1_0.converter.UserGroupDTOConverter)"
	)
	private DTOConverter<com.liferay.portal.kernel.model.UserGroup, UserGroup>
		_userGroupDTOConverter;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.UserGroup)"
	)
	private ModelResourcePermission<com.liferay.portal.kernel.model.UserGroup>
		_userGroupModelResourcePermission;

	@Reference
	private UserGroupService _userGroupService;

}