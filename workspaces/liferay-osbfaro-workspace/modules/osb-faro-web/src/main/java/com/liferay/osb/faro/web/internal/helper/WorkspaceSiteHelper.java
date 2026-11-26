/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.web.internal.util.FaroNavigationUtil;
import com.liferay.osb.faro.web.internal.util.SiteInitializerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.initializer.SiteInitializer;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = WorkspaceSiteHelper.class)
public class WorkspaceSiteHelper {

	public synchronized void initialize(FaroProject faroProject)
		throws Exception {

		Group group = _groupLocalService.getGroup(faroProject.getGroupId());

		SiteInitializerUtil.initialize(
			group, _siteInitializer, _SITE_INITIALIZER_VERSION);

		boolean ldpEnabled = FaroNavigationUtil.isLDPEnabled(faroProject);

		updateLayoutPermissions(faroProject, ldpEnabled);

		if (!ldpEnabled) {
			updateMasterLayouts("AC_FAVICON", group.getGroupId());
		}

		for (FaroUser faroUser :
				_faroUserLocalService.getFaroUsersByStatus(
					group.getGroupId(), FaroUserConstants.STATUS_APPROVED)) {

			updateUserGroupRoles(faroUser);
		}
	}

	public boolean isInitialized(FaroProject faroProject) throws Exception {
		return SiteInitializerUtil.isInitialized(
			_groupLocalService.getGroup(faroProject.getGroupId()),
			_siteInitializer, _SITE_INITIALIZER_VERSION);
	}

	public void updateLayoutPermissions(
			FaroProject faroProject, boolean ldpEnabled)
		throws Exception {

		List<Layout> layouts = _layoutLocalService.getLayouts(
			faroProject.getGroupId(), false);

		Role role = _roleLocalService.getRole(
			faroProject.getCompanyId(), RoleConstants.SITE_MEMBER);

		for (Layout layout : layouts) {
			if (layout.isDraftLayout()) {
				continue;
			}

			String faroRequirement = layout.getTypeSettingsProperty(
				"faroRequirement");

			if (Validator.isNull(faroRequirement)) {
				continue;
			}

			_updateLayoutPermission(
				faroProject, faroRequirement, layout, ldpEnabled, role);
		}
	}

	public void updateMasterLayouts(String faviconFileEntryERC, long groupId) {
		_updateMasterLayout(
			faviconFileEntryERC, groupId,
			"liferay-data-platform-editor-master");
		_updateMasterLayout(
			faviconFileEntryERC, groupId, "liferay-data-platform-master");
		_updateMasterLayout(
			faviconFileEntryERC, groupId,
			"liferay-data-platform-settings-master");
	}

	public void updateUserGroupRoles(FaroUser faroUser) throws PortalException {
		if (faroUser.getLiveUserId() <= 0) {
			return;
		}

		Role role = _roleLocalService.fetchRole(
			faroUser.getCompanyId(), "Workspace Administrator");

		if (role == null) {
			return;
		}

		if (_isWorkspaceAdministrator(faroUser)) {
			_userGroupRoleLocalService.addUserGroupRoles(
				faroUser.getLiveUserId(), faroUser.getGroupId(),
				new long[] {role.getRoleId()});
		}
		else {
			_userGroupRoleLocalService.deleteUserGroupRoles(
				faroUser.getLiveUserId(), faroUser.getGroupId(),
				new long[] {role.getRoleId()});
		}
	}

	private boolean _isWorkspaceAdministrator(FaroUser faroUser) {
		if (faroUser.getStatus() != FaroUserConstants.STATUS_APPROVED) {
			return false;
		}

		Role role = _roleLocalService.fetchRole(faroUser.getRoleId());

		if (role == null) {
			return false;
		}

		if (Objects.equals(role.getName(), RoleConstants.SITE_ADMINISTRATOR) ||
			Objects.equals(role.getName(), RoleConstants.SITE_OWNER)) {

			return true;
		}

		return false;
	}

	private void _updateLayoutPermission(
			FaroProject faroProject, String faroRequirement, Layout layout,
			boolean ldpEnabled, Role role)
		throws Exception {

		String[] actionIds = {};

		if ((faroRequirement.equals("ac") && !ldpEnabled) ||
			(faroRequirement.equals("ldp") && ldpEnabled) ||
			(faroRequirement.equals("recommendations") &&
			 faroProject.isRecommendationsEnabled())) {

			actionIds = new String[] {ActionKeys.VIEW};
		}

		_resourcePermissionLocalService.setResourcePermissions(
			layout.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(layout.getPlid()), role.getRoleId(), actionIds);
	}

	private void _updateMasterLayout(
		String faviconFileEntryERC, long groupId,
		String layoutPageTemplateEntryKey) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				groupId, layoutPageTemplateEntryKey);

		if (layoutPageTemplateEntry == null) {
			return;
		}

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		layout.setFaviconFileEntryERC(faviconFileEntryERC);

		_layoutLocalService.updateLayout(layout);
	}

	private static final String _SITE_INITIALIZER_VERSION = "0.1";

	@Reference
	private FaroUserLocalService _faroUserLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference(
		target = "(site.initializer.key=com.liferay.osb.faro.workspace.site.initializer)"
	)
	private SiteInitializer _siteInitializer;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}