/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Plugin;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.PluginSettingLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.service.base.LayoutSetServiceBaseImpl;

import java.io.File;
import java.io.InputStream;

import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutSetServiceImpl extends LayoutSetServiceBaseImpl {

	@Override
	public void updateFaviconFileEntryId(
			long groupId, boolean privateLayout, long faviconFileEntryId)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.MANAGE_LAYOUTS);

		layoutSetLocalService.updateFaviconFileEntryId(
			groupId, privateLayout, faviconFileEntryId);
	}

	@Override
	public void updateLayoutSetPrototypeLinkEnabled(
			long groupId, boolean mergeLayoutSetPrototype,
			boolean privateLayout, boolean layoutSetPrototypeLinkEnabled,
			String layoutSetPrototypeUuid)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.UPDATE);

		LayoutSet layoutSet = layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		if (layoutSet.isLayoutSetPrototypeLinkEnabled() &&
			!layoutSetPrototypeLinkEnabled) {

			PortalPermissionUtil.check(
				getPermissionChecker(), ActionKeys.UNLINK_LAYOUT_SET_PROTOTYPE);
		}

		layoutSetLocalService.updateLayoutSetPrototypeLinkEnabled(
			groupId, mergeLayoutSetPrototype, privateLayout,
			layoutSetPrototypeLinkEnabled, layoutSetPrototypeUuid);
	}

	/**
	 * Updates the state of the layout set prototype link.
	 *
	 * <p>
	 * <strong>Important:</strong> Setting
	 * <code>layoutSetPrototypeLinkEnabled</code> to <code>true</code> and
	 * <code>layoutSetPrototypeUuid</code> to <code>null</code> when the layout
	 * set prototype's current uuid is <code>null</code> will result in an
	 * <code>IllegalStateException</code>.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout set is private to the group
	 * @param layoutSetPrototypeLinkEnabled whether the layout set prototype is
	 *        link enabled
	 * @param layoutSetPrototypeUuid the uuid of the layout set prototype to
	 *        link with
	 */
	@Override
	public void updateLayoutSetPrototypeLinkEnabled(
			long groupId, boolean privateLayout,
			boolean layoutSetPrototypeLinkEnabled,
			String layoutSetPrototypeUuid)
		throws PortalException {

		layoutSetService.updateLayoutSetPrototypeLinkEnabled(
			groupId, true, privateLayout, layoutSetPrototypeLinkEnabled,
			layoutSetPrototypeUuid);
	}

	@Override
	public void updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo, byte[] bytes)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.MANAGE_LAYOUTS);

		layoutSetLocalService.updateLogo(
			groupId, privateLayout, hasLogo, bytes);
	}

	@Override
	public void updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo, File file)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.MANAGE_LAYOUTS);

		layoutSetLocalService.updateLogo(groupId, privateLayout, hasLogo, file);
	}

	@Override
	public void updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo,
			InputStream inputStream)
		throws PortalException {

		updateLogo(groupId, privateLayout, hasLogo, inputStream, true);
	}

	@Override
	public void updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo,
			InputStream inputStream, boolean cleanUpStream)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.MANAGE_LAYOUTS);

		layoutSetLocalService.updateLogo(
			groupId, privateLayout, hasLogo, inputStream, cleanUpStream);
	}

	@Override
	public LayoutSet updateLookAndFeel(
			long groupId, boolean privateLayout, String themeId,
			String colorSchemeId, String css)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		GroupPermissionUtil.check(
			permissionChecker, groupId, ActionKeys.MANAGE_LAYOUTS);

		_pluginSettingLocalService.checkPermission(
			permissionChecker.getUserId(), themeId, Plugin.TYPE_THEME);

		return layoutSetLocalService.updateLookAndFeel(
			groupId, privateLayout, themeId, colorSchemeId, css);
	}

	@Override
	public LayoutSet updateSettings(
			long groupId, boolean privateLayout, String settings)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.MANAGE_LAYOUTS);

		return layoutSetLocalService.updateSettings(
			groupId, privateLayout, settings);
	}

	@Override
	public LayoutSet updateVirtualHosts(
			long groupId, boolean privateLayout,
			TreeMap<String, String> virtualHostnames)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.UPDATE);

		return layoutSetLocalService.updateVirtualHosts(
			groupId, privateLayout, virtualHostnames);
	}

	@BeanReference(type = PluginSettingLocalService.class)
	private PluginSettingLocalService _pluginSettingLocalService;

}