/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.headless.admin.user.dto.v1_0.UserGroup;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.odata.entity.v1_0.UserGroupEntityModel;
import com.liferay.headless.admin.user.resource.v1_0.UserGroupResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
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

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user-group.properties",
	scope = ServiceScope.PROTOTYPE, service = UserGroupResource.class
)
public class UserGroupResourceImpl extends BaseUserGroupResourceImpl {

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

		return _toUserGroup(
			_userGroupService.updateUserGroup(
				GetterUtil.getString(
					userGroup.getExternalReferenceCode(),
					serviceBuilderUserGroup.getExternalReferenceCode()),
				userGroupId,
				GetterUtil.getString(
					userGroup.getName(), serviceBuilderUserGroup.getName()),
				GetterUtil.getString(
					userGroup.getDescription(),
					serviceBuilderUserGroup.getDescription()),
				null));
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
		return _toUserGroup(
			_userGroupService.addUserGroup(
				userGroup.getExternalReferenceCode(), userGroup.getName(),
				userGroup.getDescription(), null));
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

		return _toUserGroup(
			_userGroupService.updateUserGroup(
				userGroup.getExternalReferenceCode(), userGroupId,
				userGroup.getName(), userGroup.getDescription(), null));
	}

	@Override
	public UserGroup putUserGroupByExternalReferenceCode(
			String externalReferenceCode, UserGroup userGroup)
		throws Exception {

		return _toUserGroup(
			_userGroupService.addOrUpdateUserGroup(
				externalReferenceCode, userGroup.getName(),
				userGroup.getDescription(), null));
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

	private final EntityModel _entityModel = new UserGroupEntityModel();

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