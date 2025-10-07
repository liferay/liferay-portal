/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.user.dto.v1_0.Role;
import com.liferay.headless.admin.user.dto.v1_0.RolePermission;
import com.liferay.headless.admin.user.internal.odata.entity.v1_0.RoleEntityModel;
import com.liferay.headless.admin.user.internal.util.v1_0.ResourcePermissionUtil;
import com.liferay.headless.admin.user.resource.v1_0.RoleResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchRoleException;
import com.liferay.portal.kernel.exception.RoleAssignmentException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserGroupRoleService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 * @author Crescenzo Rega
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/role.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = RoleResource.class
)
public class RoleResourceImpl
	extends BaseRoleResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<Role> {

	@Override
	public void
			deleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation(
				String externalReferenceCode, Long userAccountId,
				Long organizationId)
		throws Exception {

		com.liferay.portal.kernel.model.Role role =
			_roleService.getRoleByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		deleteOrganizationRoleUserAccountAssociation(
			role.getRoleId(), userAccountId, organizationId);
	}

	@Override
	public void deleteOrganizationRoleUserAccountAssociation(
			Long roleId, Long userAccountId, Long organizationId)
		throws Exception {

		_checkRoleType(roleId, RoleConstants.TYPE_ORGANIZATION);

		Organization organization = _organizationService.getOrganization(
			organizationId);

		_userGroupRoleService.deleteUserGroupRoles(
			userAccountId, organization.getGroupId(), new long[] {roleId});
	}

	@Override
	public void deleteRole(Long roleId) throws Exception {
		_roleService.deleteRole(roleId);
	}

	@Override
	public void deleteRoleByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		com.liferay.portal.kernel.model.Role role =
			_roleService.getRoleByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		deleteRole(role.getRoleId());
	}

	@Override
	public void deleteRoleByExternalReferenceCodeUserAccountAssociation(
			String externalReferenceCode, Long userAccountId)
		throws Exception {

		com.liferay.portal.kernel.model.Role role =
			_roleService.getRoleByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		deleteRoleUserAccountAssociation(role.getRoleId(), userAccountId);
	}

	@Override
	public void deleteRoleUserAccountAssociation(
			Long roleId, Long userAccountId)
		throws Exception {

		_userService.deleteRoleUser(roleId, userAccountId);
	}

	@Override
	public void deleteSiteRoleByExternalReferenceCodeUserAccountAssociation(
			String externalReferenceCode, Long userAccountId, Long siteId)
		throws Exception {

		com.liferay.portal.kernel.model.Role role =
			_roleService.getRoleByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		deleteSiteRoleUserAccountAssociation(
			role.getRoleId(), userAccountId, siteId);
	}

	@Override
	public void deleteSiteRoleUserAccountAssociation(
			Long roleId, Long userAccountId, Long siteId)
		throws Exception {

		_checkRoleType(roleId, RoleConstants.TYPE_SITE);

		_userGroupRoleService.deleteUserGroupRoles(
			userAccountId, siteId, new long[] {roleId});
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportDescriptor() {

			@Override
			public String getItemClassName() {
				return com.liferay.portal.kernel.model.Role.class.getName();
			}

			@Override
			public String getPortletId() {
				return RolesAdminPortletKeys.ROLES_ADMIN;
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

		};
	}

	@Override
	public Role getRole(Long roleId) throws Exception {
		com.liferay.portal.kernel.model.Role role = _roleService.fetchRole(
			roleId);

		if (role == null) {
			throw new NoSuchRoleException(
				"No role exists with role ID " + roleId);
		}

		return _roleDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				true, _getActions(roleId), _dtoConverterRegistry, roleId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			_roleService.getRole(roleId));
	}

	@Override
	public Role getRoleByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		com.liferay.portal.kernel.model.Role role =
			_roleService.getRoleByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return getRole(role.getRoleId());
	}

	@Override
	public Page<Role> getRolesPage(
			String search, Integer[] types, Filter filter,
			Pagination pagination)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.<String, Map<String, String>>put(
				"get",
				addAction(
					ActionKeys.VIEW, "getRolesPage", Role.class.getName(), 0L)
			).build(),
			booleanQuery -> {
			},
			filter, com.liferay.portal.kernel.model.Role.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				if (ArrayUtil.isNotEmpty(types)) {
					searchContext.setAttribute("types", types);
				}

				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setUserId(contextUser.getUserId());
			},
			null,
			document -> {
				com.liferay.portal.kernel.model.Role role =
					_roleService.getRole(
						GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));

				return _roleDTOConverter.toDTO(
					new DefaultDTOConverterContext(
						true, _getActions(role.getRoleId()),
						_dtoConverterRegistry, role.getRoleId(),
						contextAcceptLanguage.getPreferredLocale(),
						contextUriInfo, contextUser),
					role);
			});
	}

	@Override
	public Role patchRole(Long roleId, Role role) throws Exception {
		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_roleService.getRole(roleId);

		serviceBuilderRole = _roleService.updateRole(
			serviceBuilderRole.getExternalReferenceCode(),
			serviceBuilderRole.getRoleId(),
			GetterUtil.get(role.getName(), serviceBuilderRole.getName()),
			(Map<Locale, String>)GetterUtil.getObject(
				_getTitleMap(role), serviceBuilderRole.getTitleMap()),
			(Map<Locale, String>)GetterUtil.getObject(
				_getDescriptionMap(role),
				serviceBuilderRole.getDescriptionMap()),
			serviceBuilderRole.getSubtype(),
			ServiceContextFactory.getInstance(contextHttpServletRequest));

		serviceBuilderRole = _roleService.updateExternalReferenceCode(
			serviceBuilderRole,
			GetterUtil.get(
				role.getExternalReferenceCode(),
				serviceBuilderRole.getExternalReferenceCode()));

		_addResourcePermission(role, serviceBuilderRole);

		serviceBuilderRole = _updateNestedResources(role, serviceBuilderRole);

		return _roleDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				true, _getActions(serviceBuilderRole.getRoleId()),
				_dtoConverterRegistry, serviceBuilderRole.getRoleId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			serviceBuilderRole);
	}

	@Override
	public Role patchRoleByExternalReferenceCode(
			String externalReferenceCode, Role role)
		throws Exception {

		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_roleService.getRoleByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return patchRole(serviceBuilderRole.getRoleId(), role);
	}

	@Override
	public void
			postOrganizationRoleByExternalReferenceCodeUserAccountAssociation(
				String externalReferenceCode, Long userAccountId,
				Long organizationId)
		throws Exception {

		com.liferay.portal.kernel.model.Role role =
			_roleService.getRoleByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		postOrganizationRoleUserAccountAssociation(
			role.getRoleId(), userAccountId, organizationId);
	}

	@Override
	public void postOrganizationRoleUserAccountAssociation(
			Long roleId, Long userAccountId, Long organizationId)
		throws Exception {

		_checkRoleType(roleId, RoleConstants.TYPE_ORGANIZATION);

		Organization organization = _organizationService.getOrganization(
			organizationId);

		_userGroupRoleService.addUserGroupRoles(
			userAccountId, organization.getGroupId(), new long[] {roleId});
	}

	@Override
	public Role postRole(Role role) throws Exception {
		String className = null;
		int type = 0;

		List<RoleTypeContributor> roleTypeContributors = ListUtil.filter(
			_roleTypeContributorProvider.getRoleTypeContributors(),
			roleTypeContributor -> {
				if (Validator.isNull(role.getRoleType())) {
					return false;
				}

				return StringUtil.equals(
					roleTypeContributor.getTypeLabel(), role.getRoleType());
			});

		if (ListUtil.isNotEmpty(roleTypeContributors)) {
			RoleTypeContributor roleTypeContributor = roleTypeContributors.get(
				0);

			className = roleTypeContributor.getClassName();
			type = roleTypeContributor.getType();
		}

		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_roleService.addRole(
				role.getExternalReferenceCode(), className, 0, role.getName(),
				_getTitleMap(role), _getDescriptionMap(role), type, null,
				ServiceContextFactory.getInstance(contextHttpServletRequest));

		_addResourcePermission(role, serviceBuilderRole);

		serviceBuilderRole = _updateNestedResources(role, serviceBuilderRole);

		return _roleDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				true, _getActions(serviceBuilderRole.getRoleId()),
				_dtoConverterRegistry, serviceBuilderRole.getRoleId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			serviceBuilderRole);
	}

	@Override
	public void postRoleByExternalReferenceCodeUserAccountAssociation(
			String externalReferenceCode, Long userAccountId)
		throws Exception {

		com.liferay.portal.kernel.model.Role role =
			_roleService.getRoleByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		postRoleUserAccountAssociation(role.getRoleId(), userAccountId);
	}

	@Override
	public void postRoleUserAccountAssociation(Long roleId, Long userAccountId)
		throws Exception {

		_checkRoleType(roleId, RoleConstants.TYPE_REGULAR);

		_userService.addRoleUsers(roleId, new long[] {userAccountId});
	}

	@Override
	public void postSiteRoleByExternalReferenceCodeUserAccountAssociation(
			String externalReferenceCode, Long userAccountId, Long siteId)
		throws Exception {

		com.liferay.portal.kernel.model.Role role =
			_roleService.getRoleByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		postSiteRoleUserAccountAssociation(
			role.getRoleId(), userAccountId, siteId);
	}

	@Override
	public void postSiteRoleUserAccountAssociation(
			Long roleId, Long userAccountId, Long siteId)
		throws Exception {

		_checkRoleType(roleId, RoleConstants.TYPE_SITE);

		_userGroupRoleService.addUserGroupRoles(
			userAccountId, siteId, new long[] {roleId});
	}

	@Override
	public Role putRole(Long roleId, Role role) throws Exception {
		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_roleService.fetchRole(roleId);

		String className = null;
		int type = 0;

		List<RoleTypeContributor> roleTypeContributors = ListUtil.filter(
			_roleTypeContributorProvider.getRoleTypeContributors(),
			roleTypeContributor -> {
				if (Validator.isNull(role.getRoleType())) {
					return false;
				}

				return StringUtil.equals(
					roleTypeContributor.getTypeLabel(), role.getRoleType());
			});

		if (ListUtil.isNotEmpty(roleTypeContributors)) {
			RoleTypeContributor roleTypeContributor = roleTypeContributors.get(
				0);

			className = roleTypeContributor.getClassName();
			type = roleTypeContributor.getType();
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			contextHttpServletRequest);

		if (serviceBuilderRole == null) {
			serviceBuilderRole = _roleService.addRole(
				role.getExternalReferenceCode(), className, 0, role.getName(),
				_getTitleMap(role), _getDescriptionMap(role), type, null,
				serviceContext);
		}
		else {
			serviceBuilderRole = _roleService.updateRole(
				serviceBuilderRole.getExternalReferenceCode(),
				serviceBuilderRole.getRoleId(),
				GetterUtil.get(role.getName(), serviceBuilderRole.getName()),
				_getTitleMap(role), _getDescriptionMap(role), null,
				serviceContext);

			serviceBuilderRole = _roleService.updateExternalReferenceCode(
				serviceBuilderRole,
				GetterUtil.get(
					role.getExternalReferenceCode(),
					serviceBuilderRole.getExternalReferenceCode()));
		}

		_addResourcePermission(role, serviceBuilderRole);

		serviceBuilderRole = _updateNestedResources(role, serviceBuilderRole);

		return _roleDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				true, _getActions(serviceBuilderRole.getRoleId()),
				_dtoConverterRegistry, serviceBuilderRole.getRoleId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			serviceBuilderRole);
	}

	@Override
	public Role putRoleByExternalReferenceCode(
			String externalReferenceCode, Role role)
		throws Exception {

		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_roleService.fetchRoleByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (serviceBuilderRole == null) {
			return putRole(0L, role);
		}

		return putRole(serviceBuilderRole.getRoleId(), role);
	}

	private void _addResourcePermission(
			Role role, com.liferay.portal.kernel.model.Role serviceBuilderRole)
		throws Exception {

		if (ArrayUtil.isNotEmpty(role.getRolePermissions())) {
			for (RolePermission rolePermission : role.getRolePermissions()) {
				if (rolePermission.getScope() ==
						ResourceConstants.SCOPE_INDIVIDUAL) {

					continue;
				}

				for (String actionId : rolePermission.getActionIds()) {
					_resourcePermissionService.addResourcePermission(
						contextUser.getGroupId(), contextCompany.getCompanyId(),
						rolePermission.getResourceName(),
						Math.toIntExact(rolePermission.getScope()),
						rolePermission.getPrimaryKey(),
						serviceBuilderRole.getRoleId(), actionId);
				}
			}
		}
	}

	private void _checkRoleType(long roleId, int type) throws Exception {
		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_roleService.getRole(roleId);

		if (serviceBuilderRole.getType() != type) {
			throw new RoleAssignmentException(
				StringBundler.concat(
					"Role type ",
					RoleConstants.getTypeLabel(serviceBuilderRole.getType()),
					" is not role type ", RoleConstants.getTypeLabel(type)));
		}
	}

	private Map<String, Map<String, String>> _getActions(Long roleId) {
		return HashMapBuilder.<String, Map<String, String>>put(
			"create-organization-role-user-account-association",
			addAction(
				ActionKeys.ASSIGN_MEMBERS, roleId,
				"postOrganizationRoleUserAccountAssociation",
				_roleModelResourcePermission)
		).put(
			"create-role-user-account-association",
			addAction(
				ActionKeys.ASSIGN_MEMBERS, roleId,
				"postRoleUserAccountAssociation", _roleModelResourcePermission)
		).put(
			"create-site-role-user-account-association",
			addAction(
				ActionKeys.ASSIGN_MEMBERS, roleId,
				"postSiteRoleUserAccountAssociation",
				_roleModelResourcePermission)
		).put(
			"delete-organization-role-user-account-association",
			addAction(
				ActionKeys.ASSIGN_MEMBERS, roleId,
				"deleteOrganizationRoleUserAccountAssociation",
				_roleModelResourcePermission)
		).put(
			"delete-role-user-account-association",
			addAction(
				ActionKeys.ASSIGN_MEMBERS, roleId,
				"deleteRoleUserAccountAssociation",
				_roleModelResourcePermission)
		).put(
			"delete-site-role-user-account-association",
			addAction(
				ActionKeys.ASSIGN_MEMBERS, roleId,
				"deleteSiteRoleUserAccountAssociation",
				_roleModelResourcePermission)
		).put(
			"get",
			addAction(
				ActionKeys.VIEW, roleId, "getRole",
				_roleModelResourcePermission)
		).build();
	}

	private Map<Locale, String> _getDescriptionMap(Role role) {
		Map<Locale, String> descriptionMap = null;

		if (MapUtil.isNotEmpty(role.getDescription_i18n())) {
			descriptionMap = LocalizedMapUtil.getLocalizedMap(
				role.getDescription_i18n());
		}

		return descriptionMap;
	}

	private Map<Locale, String> _getTitleMap(Role role) {
		Map<Locale, String> titleMap = null;

		if (MapUtil.isNotEmpty(role.getName_i18n())) {
			titleMap = LocalizedMapUtil.getLocalizedMap(role.getName_i18n());
		}

		return titleMap;
	}

	private com.liferay.portal.kernel.model.Role _updateNestedResources(
			Role role, com.liferay.portal.kernel.model.Role serviceBuilderRole)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35914")) {
			return serviceBuilderRole;
		}

		return ResourcePermissionUtil.setResourcePermissions(
			serviceBuilderRole, serviceBuilderRole.getCompanyId(),
			role.getPermissions(), _resourcePermissionLocalService,
			_roleService, _roleTypeContributorProvider);
	}

	private static final EntityModel _entityModel = new RoleEntityModel();

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private ResourcePermissionService _resourcePermissionService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.user.internal.dto.v1_0.converter.RoleDTOConverter)"
	)
	private DTOConverter<com.liferay.portal.kernel.model.Role, Role>
		_roleDTOConverter;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Role)"
	)
	private ModelResourcePermission<com.liferay.portal.kernel.model.Role>
		_roleModelResourcePermission;

	@Reference
	private RoleService _roleService;

	@Reference
	private RoleTypeContributorProvider _roleTypeContributorProvider;

	@Reference
	private UserGroupRoleService _userGroupRoleService;

	@Reference
	private UserService _userService;

}