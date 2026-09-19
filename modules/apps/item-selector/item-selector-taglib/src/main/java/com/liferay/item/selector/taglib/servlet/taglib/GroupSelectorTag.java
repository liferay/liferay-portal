/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.servlet.taglib;

import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.item.selector.taglib.internal.servlet.ServletContextUtil;
import com.liferay.item.selector.taglib.internal.util.GroupItemSelectorProviderRegistryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Roberto Díaz
 */
public class GroupSelectorTag extends IncludeTag {

	public List<Group> getGroups() {
		return _groups;
	}

	public int getGroupsCount() {
		return _groupsCount;
	}

	public void setGroups(List<Group> groups) {
		_groups = groups;
	}

	public void setGroupsCount(int groupsCount) {
		_groupsCount = groupsCount;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_groupType = null;
		_groups = null;
		_groupsCount = -1;
		_keywords = null;
		_scopeGroupType = null;
	}

	@Override
	protected String getPage() {
		return "/group_selector/page.jsp";
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		boolean scopeGroupTypeAndSiteGroupType =
			_isScopeGroupTypeAndSiteGroupType(httpServletRequest);

		_groups = _getGroups(
			httpServletRequest, scopeGroupTypeAndSiteGroupType);

		_groupsCount = scopeGroupTypeAndSiteGroupType ? _groups.size() :
			_getGroupSelectorProviderGroupsCount(httpServletRequest);

		httpServletRequest.setAttribute(
			"liferay-item-selector:group-selector:groups", _groups);

		httpServletRequest.setAttribute(
			"liferay-item-selector:group-selector:groupsCount", _groupsCount);
	}

	private Group _getGroup(ThemeDisplay themeDisplay) {
		if (themeDisplay.getRefererGroup() != null) {
			return themeDisplay.getRefererGroup();
		}

		return themeDisplay.getScopeGroup();
	}

	private int _getGroupSelectorProviderGroupsCount(
		HttpServletRequest httpServletRequest) {

		GroupItemSelectorProvider groupSelectorProvider =
			GroupItemSelectorProviderRegistryUtil.getGroupItemSelectorProvider(
				_getGroupType(httpServletRequest));

		if (groupSelectorProvider == null) {
			return 0;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = _getGroup(themeDisplay);

		return groupSelectorProvider.getGroupsCount(
			group.getCompanyId(), group.getGroupId(),
			_getKeywords(httpServletRequest));
	}

	private String _getGroupType(HttpServletRequest httpServletRequest) {
		if (_groupType != null) {
			return _groupType;
		}

		_groupType = ParamUtil.getString(httpServletRequest, "groupType");

		return _groupType;
	}

	private List<Group> _getGroups(
		HttpServletRequest httpServletRequest,
		boolean scopeGroupTypeAndSiteGroupType) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = _getGroup(themeDisplay);

		if (scopeGroupTypeAndSiteGroupType) {
			return _getScopeGroupTypeAndSiteGroupTypeGroups(group);
		}

		GroupItemSelectorProvider groupItemSelectorProvider =
			GroupItemSelectorProviderRegistryUtil.getGroupItemSelectorProvider(
				_getGroupType(httpServletRequest));

		if (groupItemSelectorProvider == null) {
			return new ArrayList<>();
		}

		int cur = ParamUtil.getInteger(
			httpServletRequest, SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_CUR);
		int delta = ParamUtil.getInteger(
			httpServletRequest, SearchContainer.DEFAULT_DELTA_PARAM,
			SearchContainer.DEFAULT_DELTA);

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			cur, delta);

		List<Group> groups = groupItemSelectorProvider.getGroups(
			group.getCompanyId(), group.getGroupId(),
			_getKeywords(httpServletRequest), startAndEnd[0], startAndEnd[1]);

		if (groups == null) {
			return new ArrayList<>();
		}

		return groups;
	}

	private String _getKeywords(HttpServletRequest httpServletRequest) {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(httpServletRequest, "keywords");

		return _keywords;
	}

	private List<Group> _getScopeGroupTypeAndSiteGroupTypeGroups(Group group) {
		List<Group> groups = new ArrayList<>();

		if (group == null) {
			return groups;
		}

		if (group.isCompany()) {
			groups.add(group);

			return groups;
		}

		try {
			long[] currentAndAncestorSiteGroupIds =
				PortalUtil.getCurrentAndAncestorSiteGroupIds(
					group.getGroupId(), true);

			if (currentAndAncestorSiteGroupIds == null) {
				return groups;
			}

			for (long groupId : currentAndAncestorSiteGroupIds) {
				groups.add(GroupLocalServiceUtil.getGroup(groupId));
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return groups;
	}

	private boolean _isScopeGroupType(HttpServletRequest httpServletRequest) {
		if (_scopeGroupType != null) {
			return _scopeGroupType;
		}

		_scopeGroupType = ParamUtil.getBoolean(
			httpServletRequest, "scopeGroupType");

		return _scopeGroupType;
	}

	private boolean _isScopeGroupTypeAndSiteGroupType(
		HttpServletRequest httpServletRequest) {

		if (_isScopeGroupType(httpServletRequest) &&
			Objects.equals(_getGroupType(httpServletRequest), "site")) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupSelectorTag.class);

	private String _groupType;
	private List<Group> _groups;
	private int _groupsCount = -1;
	private String _keywords;
	private Boolean _scopeGroupType;

}