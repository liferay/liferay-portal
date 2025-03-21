/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SiteMembershipsDisplayContext {

	public SiteMembershipsDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public Group getGroup() {
		if (_group != null) {
			return _group;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(
			_httpServletRequest, "groupId",
			themeDisplay.getSiteGroupIdOrLiveGroupId());

		_group = GroupLocalServiceUtil.fetchGroup(groupId);

		return _group;
	}

	public long getGroupId() {
		Group group = getGroup();

		if (group == null) {
			return 0;
		}

		return group.getGroupId();
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/view.jsp"
		).setTabs1(
			getTabs1()
		).setParameter(
			"groupId", getGroupId()
		).buildPortletURL();
	}

	public User getSelUser() throws PortalException {
		if (_selUser != null) {
			return _selUser;
		}

		_selUser = PortalUtil.getSelectedUser(_httpServletRequest, false);

		return _selUser;
	}

	public String getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(_httpServletRequest, "tabs1", "users");

		return _tabs1;
	}

	public long getUserId() throws PortalException {
		User selUser = getSelUser();

		if (selUser != null) {
			return selUser.getUserId();
		}

		return 0;
	}

	public List<NavigationItem> getViewNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(Objects.equals(getTabs1(), "users"));
				navigationItem.setHref(getPortletURL(), "tabs1", "users");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "users"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(getTabs1(), "organizations"));
				navigationItem.setHref(
					getPortletURL(), "tabs1", "organizations");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "organizations"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(getTabs1(), "user-groups"));
				navigationItem.setHref(getPortletURL(), "tabs1", "user-groups");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "user-groups"));
			}
		).build();
	}

	private Group _group;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private User _selUser;
	private String _tabs1;

}