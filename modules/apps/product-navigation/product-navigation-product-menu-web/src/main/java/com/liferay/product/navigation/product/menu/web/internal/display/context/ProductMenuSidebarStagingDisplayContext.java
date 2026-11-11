/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.display.context;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.provider.GroupURLProvider;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * @author Mario Leandro
 */
public class ProductMenuSidebarStagingDisplayContext {

	public ProductMenuSidebarStagingDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getProps() throws PortalException {
		Group group = _themeDisplay.getScopeGroup();

		if ((group == null) || (!group.isStaged() && !group.isStagingGroup()) ||
			!_hasStagingPermission(group)) {

			return null;
		}

		return HashMapBuilder.<String, Object>put(
			"current",
			(group.isStagedRemotely() || group.isStagingGroup()) ? "staging" :
				"live"
		).put(
			"liveURL",
			() -> {
				try {
					return _getLiveGroupURL(group);
				}
				catch (Exception exception) {
					_log.error(
						"Unable to get live group URL: " +
							exception.getMessage());

					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					return StringPool.BLANK;
				}
			}
		).put(
			"remote", group.isStagedRemotely()
		).put(
			"stagingURL", () -> _getStagingGroupURL(group)
		).build();
	}

	private String _getLiveGroupURL(Group group) throws PortalException {
		if (group.isStagedRemotely()) {
			return _getRemoteLiveGroupURL(group);
		}

		if (!group.isStagingGroup()) {
			return StringPool.BLANK;
		}

		Group liveGroup = StagingUtil.getLiveGroup(group.getGroupId());

		if (liveGroup == null) {
			return StringPool.BLANK;
		}

		GroupURLProvider groupURLProvider = _groupURLProviderSnapshot.get();

		return groupURLProvider.getLiveGroupURL(
			liveGroup,
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST));
	}

	private String _getRemoteLiveGroupURL(Group group) throws PortalException {
		Layout layout = _themeDisplay.getLayout();

		boolean privateLayout = layout.isPrivateLayout();

		if (layout instanceof VirtualLayout) {
			VirtualLayout virtualLayout = (VirtualLayout)layout;

			Group targetGroup = virtualLayout.getGroup();

			if (!targetGroup.hasPrivateLayouts()) {
				privateLayout = false;
			}
		}

		HttpSession httpSession = _httpServletRequest.getSession();

		String remoteLiveGroupURLKey = StringBundler.concat(
			ProductMenuSidebarStagingDisplayContext.class.getName(),
			"#REMOTE_LIVE_GROUP_URL#", group.getGroupId(), StringPool.POUND,
			privateLayout);

		String remoteLiveGroupURL = (String)httpSession.getAttribute(
			remoteLiveGroupURLKey);

		if (remoteLiveGroupURL == null) {
			remoteLiveGroupURL = StagingUtil.getRemoteSiteURL(
				group, privateLayout);

			httpSession.setAttribute(remoteLiveGroupURLKey, remoteLiveGroupURL);
		}

		return remoteLiveGroupURL;
	}

	private String _getStagingGroupURL(Group group) {
		if (group.isStagedRemotely() || !group.hasStagingGroup()) {
			return StringPool.BLANK;
		}

		Group stagingGroup = StagingUtil.getStagingGroup(group.getGroupId());

		if (stagingGroup == null) {
			return StringPool.BLANK;
		}

		GroupURLProvider groupURLProvider = _groupURLProviderSnapshot.get();

		return groupURLProvider.getGroupURL(
			stagingGroup,
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST));
	}

	private boolean _hasStagingPermission(Group group) throws PortalException {
		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		if (GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.MANAGE_STAGING) ||
			GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.PUBLISH_STAGING) ||
			GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.VIEW_STAGING)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductMenuSidebarStagingDisplayContext.class);

	private static final Snapshot<GroupURLProvider> _groupURLProviderSnapshot =
		new Snapshot<>(
			ProductMenuSidebarStagingDisplayContext.class,
			GroupURLProvider.class);

	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}