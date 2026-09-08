/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.user.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.dto.v1_0.UserGroup;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.odata.entity.v1_0.UserGroupEntityModel;
import com.liferay.headless.admin.user.internal.util.v1_0.ResourcePermissionUtil;
import com.liferay.headless.admin.user.resource.v1_0.UserGroupResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ResourcePermissionService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.util.DTOConverterUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;
import com.liferay.user.groups.admin.constants.UserGroupsAdminPortletKeys;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user-group.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = UserGroupResource.class
)
public class UserGroupResourceImpl
	extends BaseUserGroupResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<UserGroup> {

	@Override
	public void deleteUserGroup(Long userGroupId) throws PortalException {
		_userGroupService.deleteUserGroup(userGroupId);
	}

	@Override
	public void deleteUserGroupByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		deleteUserGroup(
			DTOConverterUtil.getModelPrimaryKey(
				_userGroupResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public void deleteUserGroupByExternalReferenceCodeUsers(
			String externalReferenceCode, Long[] userIds)
		throws Exception {

		com.liferay.portal.kernel.model.UserGroup userGroup =
			_userGroupService.getUserGroupByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		deleteUserGroupUsers(userGroup.getUserGroupId(), userIds);
	}

	@Override
	public void deleteUserGroupUsers(Long userGroupId, Long[] userIds)
		throws Exception {

		_userService.unsetUserGroupUsers(
			userGroupId, ArrayUtil.toArray(userIds));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<com.liferay.portal.kernel.model.UserGroup>
		getExportImportDescriptor() {

		return new ExportImportDescriptor<>() {

			@Override
			public String getKey() {
				return UserGroupResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "user-groups";
			}

			@Override
			public Class<com.liferay.portal.kernel.model.UserGroup>
				getModelClass() {

				return com.liferay.portal.kernel.model.UserGroup.class;
			}

			@Override
			public List<String> getNestedFields() {
				return List.of("creator", "roleBriefs");
			}

			@Override
			public String getPortletId() {
				return UserGroupsAdminPortletKeys.USER_GROUPS_ADMIN;
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

		};
	}

	@Override
	public UserGroup getUserGroup(Long userGroupId) throws Exception {
		return _toUserGroup(_userGroupService.getUserGroup(userGroupId));
	}

	@Override
	public UserGroup getUserGroupByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return _toUserGroup(
			_userGroupResourceDTOConverter.getObject(externalReferenceCode));
	}

	@Override
	public Page<UserGroup> getUserGroupsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.<String, Map<String, String>>put(
				"create",
				addAction(
					ActionKeys.ADD_USER_GROUP, "postUserGroup",
					PortletKeys.PORTAL, 0L)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, 0L, "getUserGroupsPage",
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
	public Page<UserGroup> getUserUserGroups(Long userAccountId)
		throws Exception {

		return Page.of(
			transform(
				_userGroupService.getUserUserGroups(userAccountId),
				this::_toUserGroup));
	}

	@Override
	public UserGroup patchUserGroup(Long userGroupId, UserGroup userGroup)
		throws Exception {

		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup =
			_userGroupService.getUserGroup(userGroupId);

		serviceBuilderUserGroup = _userGroupService.updateUserGroup(
			GetterUtil.getString(
				userGroup.getExternalReferenceCode(),
				serviceBuilderUserGroup.getExternalReferenceCode()),
			userGroupId,
			GetterUtil.getString(
				userGroup.getName(), serviceBuilderUserGroup.getName()),
			GetterUtil.getString(
				userGroup.getDescription(),
				serviceBuilderUserGroup.getDescription()),
			null);

		return _toUserGroup(
			_updateNestedResources(userGroup, serviceBuilderUserGroup));
	}

	@Override
	public UserGroup patchUserGroupByExternalReferenceCode(
			String externalReferenceCode, UserGroup userGroup)
		throws Exception {

		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup =
			_userGroupService.getUserGroupByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return patchUserGroup(
			serviceBuilderUserGroup.getUserGroupId(), userGroup);
	}

	@Override
	public UserGroup postUserGroup(UserGroup userGroup) throws Exception {
		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup =
			_userGroupService.addUserGroup(
				userGroup.getExternalReferenceCode(), userGroup.getName(),
				userGroup.getDescription(), null);

		return _toUserGroup(
			_updateNestedResources(userGroup, serviceBuilderUserGroup));
	}

	@Override
	public void postUserGroupByExternalReferenceCodeUsers(
			String externalReferenceCode, Long[] userIds)
		throws Exception {

		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup =
			_userGroupService.getUserGroupByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		postUserGroupUsers(serviceBuilderUserGroup.getUserGroupId(), userIds);
	}

	@Override
	public void postUserGroupUsers(Long userGroupId, Long[] userIds)
		throws Exception {

		_userService.addUserGroupUsers(userGroupId, ArrayUtil.toArray(userIds));
	}

	@Override
	public UserGroup putUserGroup(Long userGroupId, UserGroup userGroup)
		throws Exception {

		if (userGroupId <= 0) {
			return postUserGroup(userGroup);
		}

		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup =
			_userGroupService.updateUserGroup(
				userGroup.getExternalReferenceCode(), userGroupId,
				userGroup.getName(), userGroup.getDescription(), null);

		return _toUserGroup(
			_updateNestedResources(userGroup, serviceBuilderUserGroup));
	}

	@Override
	public UserGroup putUserGroupByExternalReferenceCode(
			String externalReferenceCode, UserGroup userGroup)
		throws Exception {

		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup =
			_userGroupService.fetchUserGroupByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (serviceBuilderUserGroup == null) {
			userGroup.setExternalReferenceCode(() -> externalReferenceCode);

			return postUserGroup(userGroup);
		}

		serviceBuilderUserGroup = _userGroupService.updateUserGroup(
			externalReferenceCode, serviceBuilderUserGroup.getUserGroupId(),
			userGroup.getName(), userGroup.getDescription(), null);

		return _toUserGroup(
			_updateNestedResources(userGroup, serviceBuilderUserGroup));
	}

	private com.liferay.portal.kernel.model.UserGroup _addGroupRole(
			com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup,
			RoleBrief roleBrief)
		throws Exception {

		String externalReferenceCode = roleBrief.getExternalReferenceCode();

		if (Validator.isNull(externalReferenceCode)) {
			return serviceBuilderUserGroup;
		}

		Role role = _roleService.getOrAddEmptyRole(
			externalReferenceCode, Role.class.getName(), 0, roleBrief.getName(),
			GetterUtil.getInteger(
				roleBrief.getRoleType(), RoleConstants.TYPE_REGULAR));

		_roleLocalService.addGroupRole(
			serviceBuilderUserGroup.getGroupId(), role.getRoleId());

		return serviceBuilderUserGroup;
	}

	private DTOConverterContext _getDTOConverterContext(long userGroupId) {
		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(),
			HashMapBuilder.<String, Map<String, String>>put(
				"delete",
				addAction(
					ActionKeys.DELETE, userGroupId, "deleteUserGroup",
					_userGroupModelResourcePermission)
			).put(
				"delete-by-external-reference-code",
				addAction(
					ActionKeys.DELETE, userGroupId,
					"deleteUserGroupByExternalReferenceCode",
					_userGroupModelResourcePermission)
			).put(
				"delete-user-group-users",
				addAction(
					ActionKeys.ASSIGN_MEMBERS, userGroupId,
					"deleteUserGroupUsers", _userGroupModelResourcePermission)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, userGroupId, "getUserGroup",
					_userGroupModelResourcePermission)
			).put(
				"get-by-external-reference-code",
				addAction(
					ActionKeys.VIEW, userGroupId,
					"getUserGroupByExternalReferenceCode",
					_userGroupModelResourcePermission)
			).put(
				"patch",
				addAction(
					ActionKeys.UPDATE, userGroupId, "patchUserGroup",
					_userGroupModelResourcePermission)
			).put(
				"patch-by-external-reference-code",
				addAction(
					ActionKeys.UPDATE, userGroupId,
					"patchUserGroupByExternalReferenceCode",
					_userGroupModelResourcePermission)
			).put(
				"post-user-group-users",
				addAction(
					ActionKeys.ASSIGN_MEMBERS, userGroupId,
					"postUserGroupUsers", _userGroupModelResourcePermission)
			).put(
				"put",
				addAction(
					ActionKeys.UPDATE, userGroupId, "putUserGroup",
					_userGroupModelResourcePermission)
			).put(
				"put-by-external-reference-code",
				addAction(
					ActionKeys.UPDATE, userGroupId,
					"putUserGroupByExternalReferenceCode",
					_userGroupModelResourcePermission)
			).build(),
			null, contextHttpServletRequest, userGroupId,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private UserGroup _toUserGroup(
			com.liferay.portal.kernel.model.UserGroup userGroup)
		throws Exception {

		return _userGroupResourceDTOConverter.toDTO(
			_getDTOConverterContext(userGroup.getUserGroupId()), userGroup);
	}

	private com.liferay.portal.kernel.model.UserGroup _updateNestedResources(
			UserGroup userGroup,
			com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup)
		throws Exception {

		RoleBrief[] roleBriefs = userGroup.getRoleBriefs();

		if (ArrayUtil.isNotEmpty(roleBriefs)) {
			for (RoleBrief roleBrief : roleBriefs) {
				serviceBuilderUserGroup = _addGroupRole(
					serviceBuilderUserGroup, roleBrief);
			}
		}

		return ResourcePermissionUtil.setResourcePermissions(
			serviceBuilderUserGroup, serviceBuilderUserGroup.getCompanyId(),
			userGroup.getPermissions(), _resourcePermissionService,
			_roleService, _roleTypeContributorProvider);
	}

	private static final EntityModel _entityModel = new UserGroupEntityModel();

	@Reference
	private ResourcePermissionService _resourcePermissionService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private RoleService _roleService;

	@Reference
	private RoleTypeContributorProvider _roleTypeContributorProvider;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.UserGroup)"
	)
	private ModelResourcePermission<com.liferay.portal.kernel.model.UserGroup>
		_userGroupModelResourcePermission;

	@Reference(target = DTOConverterConstants.USER_GROUP_RESOURCE_DTO_CONVERTER)
	private DTOConverter<com.liferay.portal.kernel.model.UserGroup, UserGroup>
		_userGroupResourceDTOConverter;

	@Reference
	private UserGroupService _userGroupService;

	@Reference
	private UserService _userService;

}