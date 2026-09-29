/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.object.deployer;

import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.deployer.ObjectDefinitionDeployer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.digital.sales.room.constants.DSRRoleConstants;
import com.liferay.digital.sales.room.web.internal.security.permission.resource.DSRDefaultPermissionObjectEntryModelResourcePermission;
import com.liferay.digital.sales.room.web.internal.util.SiteInitializerUtil;
import com.liferay.site.initializer.SiteInitializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = ObjectDefinitionDeployer.class)
public class ObjectDefinitionDeployerImpl implements ObjectDefinitionDeployer {

	@Override
	public List<ServiceRegistration<?>> deploy(
		ObjectDefinition objectDefinition) {

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_DSR_ROOM")) {

			return Collections.emptyList();
		}

		try {
			LayoutSetPrototype layoutSetPrototype = _addLayoutSetPrototype(
				objectDefinition.getCompanyId());

			_setResourcePermissions(
				objectDefinition.getCompanyId(), layoutSetPrototype,
				objectDefinition);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				objectDefinition.getClassName());

		if (modelResourcePermission == null) {
			return Collections.emptyList();
		}

		return ListUtil.fromArray(
			_bundleContext.registerService(
				ModelResourcePermission.class,
				new DSRDefaultPermissionObjectEntryModelResourcePermission(
					modelResourcePermission, _objectEntryLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"model.class.name", objectDefinition.getClassName()
				).put(
					"service.ranking", Integer.valueOf(200)
				).build()));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private LayoutSetPrototype _addLayoutSetPrototype(long companyId)
		throws PortalException {

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.
				fetchLayoutSetPrototypeByUuidAndCompanyId(
					"L_DSR_LAYOUT_SET_PROTOTYPE", companyId);

		if (layoutSetPrototype != null) {
			return layoutSetPrototype;
		}

		User user = _userLocalService.getGuestUser(companyId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute("addDefaultLayout", Boolean.FALSE);
		serviceContext.setUuid("L_DSR_LAYOUT_SET_PROTOTYPE");

		layoutSetPrototype =
			_layoutSetPrototypeLocalService.addLayoutSetPrototype(
				user.getUserId(), companyId,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), GroupConstants.DSR
				).build(),
				null, true, true, serviceContext);

		SiteInitializerUtil.initialize(
			companyId, layoutSetPrototype.getGroup(), _siteInitializer);

		return layoutSetPrototype;
	}

	private void _setResourcePermissions(
			long companyId, LayoutSetPrototype layoutSetPrototype,
			ObjectDefinition objectDefinition)
		throws PortalException {

		Group group = layoutSetPrototype.getGroup();

		Role role = _roleLocalService.fetchRoleByExternalReferenceCode(
			DSRRoleConstants.EXTERNAL_REFERENCE_CODE_DSR_CONTENT_CONTRIBUTOR,
			companyId);

		if (role == null) {
			User user = _userLocalService.getGuestUser(companyId);

			role = _roleLocalService.addRole(
				DSRRoleConstants.
					EXTERNAL_REFERENCE_CODE_DSR_CONTENT_CONTRIBUTOR,
				user.getUserId(), null, 0,
				DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR, null, null,
				RoleConstants.TYPE_SITE, null, null);
		}

		_resourcePermissionLocalService.addResourcePermission(
			companyId, Group.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			role.getRoleId(), ActionKeys.ASSIGN_MEMBERS);

		role = _roleLocalService.fetchRoleByExternalReferenceCode(
			DSRRoleConstants.EXTERNAL_REFERENCE_CODE_DSR_ROOM_COLLABORATOR,
			companyId);

		if (role == null) {
			User user = _userLocalService.getGuestUser(companyId);

			role = _roleLocalService.addRole(
				DSRRoleConstants.EXTERNAL_REFERENCE_CODE_DSR_ROOM_COLLABORATOR,
				user.getUserId(), null, 0,
				DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR, null, null,
				RoleConstants.TYPE_SITE, null, null);
		}

		_resourcePermissionLocalService.addResourcePermission(
			companyId, Group.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			role.getRoleId(), ActionKeys.ASSIGN_MEMBERS);
		_resourcePermissionLocalService.addResourcePermission(
			companyId, Group.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			role.getRoleId(), ActionKeys.MANAGE_LAYOUTS);
		_resourcePermissionLocalService.addResourcePermission(
			companyId, Layout.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			role.getRoleId(), ActionKeys.UPDATE);
		_resourcePermissionLocalService.addResourcePermission(
			companyId, Layout.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			role.getRoleId(), ActionKeys.VIEW);

		role = _roleLocalService.fetchRoleByExternalReferenceCode(
			DSRRoleConstants.EXTERNAL_REFERENCE_CODE_DSR_SELLER, companyId);

		if (role == null) {
			User user = _userLocalService.getGuestUser(companyId);

			role = _roleLocalService.addRole(
				DSRRoleConstants.EXTERNAL_REFERENCE_CODE_DSR_SELLER,
				user.getUserId(), null, 0, DSRRoleConstants.NAME_DSR_SELLER,
				null, null, RoleConstants.TYPE_REGULAR, null, null);
		}

		_resourcePermissionLocalService.addResourcePermission(
			companyId, Group.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			role.getRoleId(), ActionKeys.ASSIGN_MEMBERS);

		_resourcePermissionLocalService.addResourcePermission(
			companyId, PortletKeys.PORTAL, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(companyId), role.getRoleId(),
			ActionKeys.VIEW_CONTROL_PANEL);
		_resourcePermissionLocalService.addResourcePermission(
			companyId, objectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
			role.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY);

		if (group.getDefaultPrivatePlid() > 0) {
			_resourcePermissionLocalService.setResourcePermissions(
				companyId, Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(group.getDefaultPrivatePlid()), role.getRoleId(),
				new String[] {ActionKeys.VIEW});
		}

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, LayoutSetPrototype.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(layoutSetPrototype.getLayoutSetPrototypeId()),
			role.getRoleId(), new String[] {ActionKeys.VIEW});

		Group dsrGroup = _groupLocalService.getGroupByExternalReferenceCode(
			"L_" + GroupConstants.DSR, companyId);

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, Layout.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(dsrGroup.getGroupId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		Map<String, String[]> permissionsMap = HashMapBuilder.put(
			DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR,
			new String[] {
				ActionKeys.ADD_DOCUMENT, ActionKeys.ADVANCED_UPDATE,
				ActionKeys.UPDATE, ActionKeys.SUBSCRIBE, ActionKeys.VIEW
			}
		).put(
			DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR,
			new String[] {
				ActionKeys.ADD_DOCUMENT, ActionKeys.ADVANCED_UPDATE,
				ActionKeys.UPDATE, ActionKeys.SUBSCRIBE, ActionKeys.VIEW
			}
		).put(
			DSRRoleConstants.NAME_DSR_SELLER, new String[] {ActionKeys.VIEW}
		).put(
			RoleConstants.OWNER,
			new String[] {
				ActionKeys.ADD_DOCUMENT, ActionKeys.ADVANCED_UPDATE,
				ActionKeys.UPDATE, ActionKeys.SUBSCRIBE, ActionKeys.VIEW
			}
		).put(
			RoleConstants.SITE_MEMBER,
			new String[] {ActionKeys.SUBSCRIBE, ActionKeys.VIEW}
		).put(
			RoleConstants.USER, new String[] {ActionKeys.VIEW}
		).build();

		for (Role currentRole :
				_roleLocalService.getGroupRolesAndTeamRoles(
					companyId, null, Arrays.asList(RoleConstants.ADMINISTRATOR),
					null, null,
					new int[] {
						RoleConstants.TYPE_REGULAR, RoleConstants.TYPE_SITE
					},
					null, 0, 0, QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			String[] actionIds = permissionsMap.get(currentRole.getName());

			if (actionIds == null) {
				actionIds = new String[0];
			}

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, "com.liferay.document.library",
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(group.getGroupId()), currentRole.getRoleId(),
				actionIds);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectDefinitionDeployerImpl.class);

	private BundleContext _bundleContext;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference(
		target = "(site.initializer.key=com.liferay.digital.sales.room.room.site.initializer)"
	)
	private SiteInitializer _siteInitializer;

	@Reference
	private UserLocalService _userLocalService;

}