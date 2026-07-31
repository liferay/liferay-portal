/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.manager;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.PortletSessionListenerManager;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.constants.SiteWebKeys;
import com.liferay.site.manager.RecentGroupManager;
import com.liferay.site.provider.GroupURLProvider;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
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

		if (liveGroup.isCMS() || liveGroup.isLayoutPrototype() ||
			liveGroup.isLayoutSetPrototype() ||
			Objects.equals(GroupConstants.DSR, liveGroup.getGroupKey())) {

			return;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		String value = _getRecentGroupsValue(httpServletRequest, httpSession);

		List<Long> groupIds = ListUtil.fromArray(
			ArrayUtil.toLongArray(StringUtil.split(value, 0L)));

		if (!groupIds.isEmpty() && (groupIds.get(0) == liveGroupId)) {
			return;
		}

		groupIds.remove(liveGroupId);

		groupIds.add(0, liveGroupId);

		groupIds = ListUtil.subList(
			groupIds, 0, PropsValues.RECENT_GROUPS_MAX_ELEMENTS);

		_setRecentGroupsValue(httpSession, StringUtil.merge(groupIds));
	}

	@Override
	public List<Group> getRecentGroups(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return Collections.emptyList();
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		String value = _getRecentGroupsValue(httpServletRequest, httpSession);

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

	@Activate
	protected void activate() {
		_httpSessionListener = new HttpSessionListener() {

			@Override
			public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
				HttpSession httpSession = httpSessionEvent.getSession();

				User user = (User)httpSession.getAttribute(WebKeys.USER);

				String recentGroupsValue = (String)httpSession.getAttribute(
					_SESSION_KEY_RECENT_GROUPS);

				if ((user != null) && (recentGroupsValue != null)) {
					PortalPreferences portalPreferences =
						PortletPreferencesFactoryUtil.getPortalPreferences(
							user.getUserId(), true);

					portalPreferences.setValue(
						SessionClicks.class.getName(), _KEY_RECENT_GROUPS,
						recentGroupsValue);
				}
			}

		};

		PortletSessionListenerManager.addHttpSessionListener(
			_httpSessionListener);
	}

	@Deactivate
	protected void deactivate() {
		PortletSessionListenerManager.removeHttpSessionListener(
			_httpSessionListener);
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

			if ((group == null) || group.isCompany() ||
				!GroupPermissionUtil.contains(
					permissionChecker, group.getGroupId(), ActionKeys.VIEW) ||
				!_groupLocalService.isLiveGroupActive(group)) {

				continue;
			}

			Layout privateLayout = null;

			Layout layout = _layoutLocalService.fetchFirstLayout(
				group.getGroupId(), false,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

			if (layout == null) {
				privateLayout = _layoutLocalService.fetchFirstLayout(
					group.getGroupId(), true,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);
			}

			boolean hasLayout = false;

			if ((layout != null) ||
				((privateLayout != null) &&
				 LayoutPermissionUtil.contains(
					 permissionChecker, privateLayout, true,
					 ActionKeys.VIEW))) {

				hasLayout = true;
			}

			if (hasLayout) {
				portletRequest.setAttribute(
					SiteWebKeys.GROUP_URL_PROVIDER_CONTROL_PANEL, Boolean.TRUE);
			}

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
		HttpServletRequest httpServletRequest, HttpSession httpSession) {

		String recentGroupsValue = SessionClicks.get(
			httpSession, _KEY_RECENT_GROUPS, null);

		if (recentGroupsValue == null) {
			recentGroupsValue = SessionClicks.get(
				httpServletRequest, _KEY_RECENT_GROUPS, null);

			if (recentGroupsValue != null) {
				_setRecentGroupsValue(httpSession, recentGroupsValue);
			}
		}

		return recentGroupsValue;
	}

	private void _setRecentGroupsValue(HttpSession httpSession, String value) {
		SessionClicks.put(httpSession, _KEY_RECENT_GROUPS, value);
	}

	private static final String _KEY_RECENT_GROUPS =
		"com.liferay.site.util_recentGroups";

	private static final String _SESSION_KEY_RECENT_GROUPS =
		StringBundler.concat(
			SessionClicks.class.getName(), StringPool.COLON,
			_KEY_RECENT_GROUPS);

	private static final Log _log = LogFactoryUtil.getLog(
		RecentGroupManagerImpl.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupURLProvider _groupURLProvider;

	private HttpSessionListener _httpSessionListener;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}