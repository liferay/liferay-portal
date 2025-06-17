/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.manager;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.site.constants.SiteWebKeys;
import com.liferay.site.manager.RecentGroupManager;
import com.liferay.site.provider.GroupURLProvider;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = RecentGroupManager.class)
public class RecentGroupManagerImpl implements RecentGroupManager {

	@Override
	public void addRecentGroup(
		HttpServletRequest httpServletRequest, Group group) {

		addRecentGroup(httpServletRequest, group.getGroupId());
	}

	@Override
	public void addRecentGroup(
		HttpServletRequest httpServletRequest, long groupId) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return;
		}

		long liveGroupId = _getLiveGroupId(groupId);

		if (liveGroupId <= 0) {
			return;
		}

		Group liveGroup = _groupLocalService.fetchGroup(liveGroupId);

		if (liveGroup.isLayoutPrototype() || liveGroup.isLayoutSetPrototype()) {
			return;
		}

		String value = _getRecentGroupsValue(httpServletRequest);

		List<Long> groupIds = ListUtil.fromArray(
			ArrayUtil.toLongArray(StringUtil.split(value, 0L)));

		if (!groupIds.isEmpty() && (groupIds.get(0) == liveGroupId)) {
			return;
		}

		groupIds.remove(liveGroupId);

		groupIds.add(0, liveGroupId);

		groupIds = ListUtil.subList(
			groupIds, 0, PropsValues.RECENT_GROUPS_MAX_ELEMENTS);

		_setRecentGroupsValue(httpServletRequest, StringUtil.merge(groupIds));
	}

	@Override
	public List<Group> getRecentGroups(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return Collections.emptyList();
		}

		String value = _getRecentGroupsValue(httpServletRequest);

		try {
			PortletRequest portletRequest =
				(PortletRequest)httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST);

			return getRecentGroups(value, portletRequest);
		}
		catch (Exception exception) {
			_log.error("Unable to get recent groups", exception);
		}

		return Collections.emptyList();
	}

	protected List<Group> getRecentGroups(
			String value, PortletRequest portletRequest)
		throws Exception {

		long[] groupIds = StringUtil.split(value, 0L);

		if (ArrayUtil.isEmpty(groupIds)) {
			return Collections.emptyList();
		}

		List<Group> groups = new ArrayList<>(groupIds.length);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(
				_portal.getUser(portletRequest));

		for (long groupId : groupIds) {
			Group group = _groupLocalService.fetchGroup(groupId);

			if ((group == null) ||
				!GroupPermissionUtil.contains(
					permissionChecker, group.getGroupId(), ActionKeys.VIEW) ||
				!_groupLocalService.isLiveGroupActive(group)) {

				continue;
			}

			if (!group.isCompany()) {
				Layout layout = _layoutLocalService.fetchFirstLayout(
					group.getGroupId(), false,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

				if (layout == null) {
					layout = _layoutLocalService.fetchFirstLayout(
						group.getGroupId(), true,
						LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

					if ((layout == null) ||
						!LayoutPermissionUtil.contains(
							permissionChecker, layout, true, ActionKeys.VIEW)) {

						continue;
					}
				}
			}

			portletRequest.setAttribute(
				SiteWebKeys.GROUP_URL_PROVIDER_CONTROL_PANEL, Boolean.TRUE);

			if (Validator.isNull(
					_groupURLProvider.getGroupURL(group, portletRequest))) {

				continue;
			}

			groups.add(group);
		}

		return groups;
	}

	private long _getLiveGroupId(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return 0;
		}

		if (!group.isStagedRemotely() && group.isStagingGroup()) {
			return group.getLiveGroupId();
		}

		return groupId;
	}

	private String _getRecentGroupsValue(
		HttpServletRequest httpServletRequest) {

		return SessionClicks.get(httpServletRequest, _KEY_RECENT_GROUPS, null);
	}

	private void _setRecentGroupsValue(
		HttpServletRequest httpServletRequest, String value) {

		SessionClicks.put(httpServletRequest, _KEY_RECENT_GROUPS, value);
	}

	private static final String _KEY_RECENT_GROUPS =
		"com.liferay.site.util_recentGroups";

	private static final Log _log = LogFactoryUtil.getLog(
		RecentGroupManagerImpl.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupURLProvider _groupURLProvider;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}