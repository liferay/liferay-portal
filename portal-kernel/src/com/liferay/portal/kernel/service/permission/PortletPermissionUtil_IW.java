/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.permission;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletPermissionUtil_IW {
	public static PortletPermissionUtil_IW getInstance() {
		return _instance;
	}

	public void check(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		com.liferay.portal.kernel.model.Layout layout,
		java.lang.String portletId, java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		PortletPermissionUtil.check(permissionChecker, layout, portletId,
			actionId);
	}

	public void check(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		com.liferay.portal.kernel.model.Layout layout,
		java.lang.String portletId, java.lang.String actionId, boolean strict)
		throws com.liferay.portal.kernel.exception.PortalException {
		PortletPermissionUtil.check(permissionChecker, layout, portletId,
			actionId, strict);
	}

	public void check(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, com.liferay.portal.kernel.model.Layout layout,
		java.lang.String portletId, java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		PortletPermissionUtil.check(permissionChecker, groupId, layout,
			portletId, actionId);
	}

	public void check(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, com.liferay.portal.kernel.model.Layout layout,
		java.lang.String portletId, java.lang.String actionId, boolean strict)
		throws com.liferay.portal.kernel.exception.PortalException {
		PortletPermissionUtil.check(permissionChecker, groupId, layout,
			portletId, actionId, strict);
	}

	public void check(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, com.liferay.portal.kernel.model.Layout layout,
		java.lang.String portletId, java.lang.String actionId, boolean strict,
		boolean checkStagingPermission)
		throws com.liferay.portal.kernel.exception.PortalException {
		PortletPermissionUtil.check(permissionChecker, groupId, layout,
			portletId, actionId, strict, checkStagingPermission);
	}

	public void check(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, long plid, java.lang.String portletId,
		java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		PortletPermissionUtil.check(permissionChecker, groupId, plid,
			portletId, actionId);
	}

	public void check(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, long plid, java.lang.String portletId,
		java.lang.String actionId, boolean strict)
		throws com.liferay.portal.kernel.exception.PortalException {
		PortletPermissionUtil.check(permissionChecker, groupId, plid,
			portletId, actionId, strict);
	}

	public void check(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long plid, java.lang.String portletId, java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		PortletPermissionUtil.check(permissionChecker, plid, portletId, actionId);
	}

	public void check(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long plid, java.lang.String portletId, java.lang.String actionId,
		boolean strict)
		throws com.liferay.portal.kernel.exception.PortalException {
		PortletPermissionUtil.check(permissionChecker, plid, portletId,
			actionId, strict);
	}

	public void check(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		java.lang.String portletId, java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		PortletPermissionUtil.check(permissionChecker, portletId, actionId);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		com.liferay.portal.kernel.model.Layout layout,
		com.liferay.portal.kernel.model.Portlet portlet,
		java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, layout,
			portlet, actionId);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		com.liferay.portal.kernel.model.Layout layout,
		com.liferay.portal.kernel.model.Portlet portlet,
		java.lang.String actionId, boolean strict)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, layout,
			portlet, actionId, strict);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		com.liferay.portal.kernel.model.Layout layout,
		java.lang.String portletId, java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, layout,
			portletId, actionId);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		com.liferay.portal.kernel.model.Layout layout,
		java.lang.String portletId, java.lang.String actionId, boolean strict)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, layout,
			portletId, actionId, strict);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, com.liferay.portal.kernel.model.Layout layout,
		com.liferay.portal.kernel.model.Portlet portlet,
		java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, groupId,
			layout, portlet, actionId);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, com.liferay.portal.kernel.model.Layout layout,
		com.liferay.portal.kernel.model.Portlet portlet,
		java.lang.String actionId, boolean strict)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, groupId,
			layout, portlet, actionId, strict);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, com.liferay.portal.kernel.model.Layout layout,
		com.liferay.portal.kernel.model.Portlet portlet,
		java.lang.String actionId, boolean strict,
		boolean checkStagingPermission)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, groupId,
			layout, portlet, actionId, strict, checkStagingPermission);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, com.liferay.portal.kernel.model.Layout layout,
		java.lang.String portletId, java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, groupId,
			layout, portletId, actionId);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, com.liferay.portal.kernel.model.Layout layout,
		java.lang.String portletId, java.lang.String actionId, boolean strict)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, groupId,
			layout, portletId, actionId, strict);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, com.liferay.portal.kernel.model.Layout layout,
		java.lang.String portletId, java.lang.String actionId, boolean strict,
		boolean checkStagingPermission)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, groupId,
			layout, portletId, actionId, strict, checkStagingPermission);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, long plid, java.lang.String portletId,
		java.lang.String actionId, boolean strict)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, groupId, plid,
			portletId, actionId, strict);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long plid, com.liferay.portal.kernel.model.Portlet portlet,
		java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, plid, portlet,
			actionId);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long plid, com.liferay.portal.kernel.model.Portlet portlet,
		java.lang.String actionId, boolean strict)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, plid, portlet,
			actionId, strict);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long plid, java.lang.String portletId, java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, plid,
			portletId, actionId);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long plid, java.lang.String portletId, java.lang.String actionId,
		boolean strict)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, plid,
			portletId, actionId, strict);
	}

	public boolean contains(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		java.lang.String portletId, java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.contains(permissionChecker, portletId,
			actionId);
	}

	public java.lang.String getPrimaryKey(long plid, java.lang.String portletId) {
		return PortletPermissionUtil.getPrimaryKey(plid, portletId);
	}

	public boolean hasAccessPermission(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long scopeGroupId, com.liferay.portal.kernel.model.Layout layout,
		com.liferay.portal.kernel.model.Portlet portlet,
		jakarta.portlet.PortletMode portletMode)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.hasAccessPermission(permissionChecker,
			scopeGroupId, layout, portlet, portletMode);
	}

	public boolean hasConfigurationPermission(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long groupId, com.liferay.portal.kernel.model.Layout layout,
		java.lang.String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.hasConfigurationPermission(permissionChecker,
			groupId, layout, actionId);
	}

	public boolean hasControlPanelAccessPermission(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long scopeGroupId,
		java.util.Collection<com.liferay.portal.kernel.model.Portlet> portlets)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.hasControlPanelAccessPermission(permissionChecker,
			scopeGroupId, portlets);
	}

	public boolean hasControlPanelAccessPermission(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long scopeGroupId, com.liferay.portal.kernel.model.Portlet portlet)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.hasControlPanelAccessPermission(permissionChecker,
			scopeGroupId, portlet);
	}

	public boolean hasControlPanelAccessPermission(
		com.liferay.portal.kernel.security.permission.PermissionChecker permissionChecker,
		long scopeGroupId, java.lang.String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return PortletPermissionUtil.hasControlPanelAccessPermission(permissionChecker,
			scopeGroupId, portletId);
	}

	public boolean hasLayoutManagerPermission(java.lang.String portletId,
		java.lang.String actionId) {
		return PortletPermissionUtil.hasLayoutManagerPermission(portletId,
			actionId);
	}

	private PortletPermissionUtil_IW() {
	}

	private static PortletPermissionUtil_IW _instance = new PortletPermissionUtil_IW();
}