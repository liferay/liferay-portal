/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.my.sites.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.site.my.sites.web.internal.constants.MySitesPortletKeys;
import com.liferay.site.my.sites.web.internal.servlet.taglib.util.SiteActionDropdownItemsProvider;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SiteMySitesDisplayContext {

	public SiteMySitesDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest, MySitesPortletKeys.MY_SITES, "descriptive");

		return _displayStyle;
	}

	public List<DropdownItem> getGroupActionDropdownItems(Group group)
		throws Exception {

		SiteActionDropdownItemsProvider siteActionDropdownItemsProvider =
			new SiteActionDropdownItemsProvider(
				group, _renderRequest, _renderResponse, getTabs1());

		return siteActionDropdownItemsProvider.getActionDropdownItems();
	}

	public int getGroupOrganizationsCount(long groupId) {
		if (_groupOrganizationsCounts != null) {
			return GetterUtil.getInteger(
				_groupOrganizationsCounts.get(groupId));
		}

		_groupOrganizationsCounts = new HashMap<>();

		GroupSearch groupSearch = getGroupSearchContainer();

		long[] groupIds = ListUtil.toLongArray(
			groupSearch.getResults(), Group.GROUP_ID_ACCESSOR);

		for (long curGroupId : groupIds) {
			_groupOrganizationsCounts.put(
				curGroupId,
				OrganizationLocalServiceUtil.getGroupOrganizationsCount(
					curGroupId));
		}

		return GetterUtil.getInteger(_groupOrganizationsCounts.get(groupId));
	}

	public GroupSearch getGroupSearchContainer() {
		if (_groupSearch != null) {
			return _groupSearch;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		GroupSearch groupSearch = new GroupSearch(
			_renderRequest, getPortletURL());

		groupSearch.setOrderByCol(getOrderByCol());
		groupSearch.setOrderByComparator(
			UsersAdminUtil.getGroupOrderByComparator(
				getOrderByCol(), getOrderByType()));
		groupSearch.setOrderByType(getOrderByType());

		LinkedHashMap<String, Object> groupParams =
			LinkedHashMapBuilder.<String, Object>put(
				"site", Boolean.TRUE
			).build();

		if (Objects.equals(getTabs1(), "my-sites")) {
			groupParams.put("usersGroups", themeDisplay.getUserId());
			groupParams.put("active", Boolean.TRUE);
		}
		else {
			List<Integer> types = new ArrayList<>();

			types.add(GroupConstants.TYPE_SITE_OPEN);
			types.add(GroupConstants.TYPE_SITE_RESTRICTED);

			groupParams.put("types", types);

			groupParams.put("active", Boolean.TRUE);
		}

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		groupSearch.setResultsAndTotal(
			() -> GroupLocalServiceUtil.search(
				themeDisplay.getCompanyId(), keywords, groupParams,
				groupSearch.getStart(), groupSearch.getEnd(),
				groupSearch.getOrderByComparator()),
			GroupLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), keywords, groupParams));

		_groupSearch = groupSearch;

		return _groupSearch;
	}

	public int getGroupUserGroupsCount(long groupId) {
		if (_groupUserGroupsCounts != null) {
			return GetterUtil.getInteger(_groupUserGroupsCounts.get(groupId));
		}

		_groupUserGroupsCounts = new HashMap<>();

		GroupSearch groupSearch = getGroupSearchContainer();

		long[] groupIds = ListUtil.toLongArray(
			groupSearch.getResults(), Group.GROUP_ID_ACCESSOR);

		for (long curGroupId : groupIds) {
			_groupUserGroupsCounts.put(
				curGroupId,
				UserGroupLocalServiceUtil.getGroupUserGroupsCount(curGroupId));
		}

		return GetterUtil.getInteger(_groupUserGroupsCounts.get(groupId));
	}

	public int getGroupUsersCounts(long groupId) {
		if (_groupUsersCounts != null) {
			return GetterUtil.getInteger(_groupUsersCounts.get(groupId));
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		GroupSearch groupSearch = getGroupSearchContainer();

		_groupUsersCounts = UserLocalServiceUtil.searchCounts(
			themeDisplay.getCompanyId(), WorkflowConstants.STATUS_APPROVED,
			ListUtil.toLongArray(
				groupSearch.getResults(), Group.GROUP_ID_ACCESSOR));

		return GetterUtil.getInteger(_groupUsersCounts.get(groupId));
	}

	public List<NavigationItem> getNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(getTabs1(), "my-sites"));
				navigationItem.setHref(getPortletURL(), "tabs1", "my-sites");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "my-sites"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(getTabs1(), "available-sites"));
				navigationItem.setHref(
					getPortletURL(), "tabs1", "available-sites");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "available-sites"));
			}
		).build();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, MySitesPortletKeys.MY_SITES, "name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, MySitesPortletKeys.MY_SITES, "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setTabs1(
			getTabs1()
		).setParameter(
			"displayStyle", getDisplayStyle()
		).buildPortletURL();
	}

	public String getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(_renderRequest, "tabs1", "my-sites");

		if (!_tabs1.equals("my-sites") && !_tabs1.equals("available-sites")) {
			_tabs1 = "my-sites";
		}

		return _tabs1;
	}

	public int getTotalItems() {
		GroupSearch groupSearch = getGroupSearchContainer();

		return groupSearch.getTotal();
	}

	private String _displayStyle;
	private Map<Long, Integer> _groupOrganizationsCounts;
	private GroupSearch _groupSearch;
	private Map<Long, Integer> _groupUserGroupsCounts;
	private Map<Long, Integer> _groupUsersCounts;
	private final HttpServletRequest _httpServletRequest;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private String _tabs1;

}