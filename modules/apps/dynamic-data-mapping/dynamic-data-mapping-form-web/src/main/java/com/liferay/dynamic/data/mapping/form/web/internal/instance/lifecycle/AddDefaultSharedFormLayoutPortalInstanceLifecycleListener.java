/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.instance.lifecycle;

import com.liferay.dynamic.data.mapping.form.web.internal.layout.type.constants.DDMFormPortletLayoutTypeConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class AddDefaultSharedFormLayoutPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		Group group = _groupLocalService.fetchFriendlyURLGroup(
			company.getCompanyId(), "/forms");

		if (group == null) {
			group = _addFormsGroup(company.getCompanyId());
		}

		Layout sharedLayout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/shared");

		if (sharedLayout == null) {
			sharedLayout = _addPublicLayout(
				company.getCompanyId(), group.getGroupId());
		}

		_verifyLayout(sharedLayout);

		Layout privateLayout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), true, "/shared");

		if (privateLayout == null) {
			privateLayout = _addPrivateLayout(
				company.getCompanyId(), group.getGroupId());
		}

		_verifyLayout(privateLayout);
	}

	private Group _addFormsGroup(long companyId) throws Exception {
		return _groupLocalService.addGroup(
			StringPool.BLANK, _userLocalService.getGuestUserId(companyId),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), GroupConstants.FORMS
			).build(),
			null, GroupConstants.TYPE_SITE_PRIVATE, null, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			GroupConstants.FORMS_FRIENDLY_URL, false, false, true, null);
	}

	private Layout _addPrivateLayout(long companyId, long groupId)
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);
		serviceContext.setAttribute("layoutUpdateable", Boolean.FALSE);
		serviceContext.setScopeGroupId(groupId);

		long guestUserId = _userLocalService.getGuestUserId(companyId);

		serviceContext.setUserId(guestUserId);

		Layout layout = _layoutLocalService.addLayout(
			null, guestUserId, groupId, true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, "Shared",
			StringPool.BLANK, StringPool.BLANK,
			DDMFormPortletLayoutTypeConstants.LAYOUT_TYPE, true, "/shared",
			serviceContext);

		_updateUserLayoutViewPermissionPermission(companyId, layout);

		return layout;
	}

	private Layout _addPublicLayout(long companyId, long groupId)
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);
		serviceContext.setAttribute("layoutUpdateable", Boolean.FALSE);
		serviceContext.setScopeGroupId(groupId);

		long guestUserId = _userLocalService.getGuestUserId(companyId);

		serviceContext.setUserId(guestUserId);

		return _layoutLocalService.addLayout(
			null, guestUserId, groupId, false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, "Shared",
			StringPool.BLANK, StringPool.BLANK,
			DDMFormPortletLayoutTypeConstants.LAYOUT_TYPE, true, "/shared",
			serviceContext);
	}

	private void _updateUserLayoutViewPermissionPermission(
			long companyId, Layout layout)
		throws Exception {

		Role role = _roleLocalService.getRole(companyId, RoleConstants.USER);

		if (_resourcePermissionLocalService.hasResourcePermission(
				role.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(layout.getCompanyId()), role.getRoleId(),
				ActionKeys.VIEW)) {

			return;
		}

		_resourcePermissionLocalService.addResourcePermission(
			role.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(layout.getGroupId()),
			role.getRoleId(), ActionKeys.VIEW);
	}

	private void _verifyLayout(Layout layout) throws Exception {
		if (StringUtil.equals(
				layout.getType(),
				DDMFormPortletLayoutTypeConstants.LAYOUT_TYPE)) {

			return;
		}

		layout.setType(DDMFormPortletLayoutTypeConstants.LAYOUT_TYPE);

		_layoutLocalService.updateLayout(layout);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}