/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.display.context;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Máté Thurzó
 */
public class GroupDisplayContextHelper {

	public GroupDisplayContextHelper(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries() {
		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		breadcrumbEntry.setTitle(
			LanguageUtil.get(themeDisplay.getLocale(), "staging"));

		return Collections.singletonList(breadcrumbEntry);
	}

	public Group getGroup() {
		if (_group != null) {
			return _group;
		}

		if (!PropsValues.STAGING_LIVE_GROUP_LOCKING_ENABLED &&
			(getSelGroup() != null)) {

			_group = getSelGroup();

			return _group;
		}

		if (getStagingGroup() != null) {
			_group = getStagingGroup();
		}
		else {
			_group = getLiveGroup();
		}

		if (_group == null) {
			_group = getSelGroup();
		}

		return _group;
	}

	public Long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		Group group = getGroup();

		if (group != null) {
			_groupId = group.getGroupId();
		}

		return _groupId;
	}

	public UnicodeProperties getGroupTypeSettings() {
		if (_groupTypeSettingsUnicodeProperties != null) {
			return _groupTypeSettingsUnicodeProperties;
		}

		Group group = getGroup();

		if (group != null) {
			_groupTypeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();
		}
		else {
			_groupTypeSettingsUnicodeProperties = new UnicodeProperties();
		}

		return _groupTypeSettingsUnicodeProperties;
	}

	public Group getLiveGroup() {
		if (_liveGroup != null) {
			return _liveGroup;
		}

		Group group = getSelGroup();

		if (group == null) {
			return null;
		}

		_liveGroup = StagingUtil.getLiveGroup(group.getGroupId());

		if (_liveGroup == null) {
			_liveGroup = group;
		}

		return _liveGroup;
	}

	public Long getLiveGroupId() {
		if (_liveGroupId != null) {
			return _liveGroupId;
		}

		Group liveGroup = getLiveGroup();

		if (liveGroup != null) {
			_liveGroupId = liveGroup.getGroupId();
		}

		return _liveGroupId;
	}

	public Group getSelGroup() {
		if (_selGroup != null) {
			return _selGroup;
		}

		long groupId = ParamUtil.getLong(_httpServletRequest, "groupId");

		_selGroup = GroupLocalServiceUtil.fetchGroup(groupId);

		if (_selGroup == null) {
			_selGroup = (Group)_httpServletRequest.getAttribute(WebKeys.GROUP);
		}

		if (_selGroup == null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_selGroup = themeDisplay.getScopeGroup();
		}

		return _selGroup;
	}

	public Group getStagingGroup() {
		if (_stagingGroup != null) {
			return _stagingGroup;
		}

		Group group = getSelGroup();

		if (group == null) {
			return null;
		}

		_stagingGroup = StagingUtil.getStagingGroup(group.getGroupId());

		return _stagingGroup;
	}

	public Long getStagingGroupId() {
		if (_stagingGroupId != null) {
			return _stagingGroupId;
		}

		Group stagingGroup = getStagingGroup();

		if (stagingGroup != null) {
			_stagingGroupId = stagingGroup.getGroupId();
		}

		return _stagingGroupId;
	}

	private Group _group;
	private Long _groupId;
	private UnicodeProperties _groupTypeSettingsUnicodeProperties;
	private final HttpServletRequest _httpServletRequest;
	private Group _liveGroup;
	private Long _liveGroupId;
	private Group _selGroup;
	private Group _stagingGroup;
	private Long _stagingGroupId;

}