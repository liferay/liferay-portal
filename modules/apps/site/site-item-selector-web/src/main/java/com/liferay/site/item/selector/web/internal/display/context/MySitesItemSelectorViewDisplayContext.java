/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.item.selector.web.internal.display.context;

import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.provider.GroupSearchProvider;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Julio Camarero
 */
public class MySitesItemSelectorViewDisplayContext
	extends BaseSitesItemSelectorViewDisplayContext {

	public MySitesItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest,
		GroupItemSelectorCriterion groupItemSelectorCriterion,
		String itemSelectedEventName, PortletURL portletURL) {

		super(
			httpServletRequest, groupItemSelectorCriterion,
			itemSelectedEventName, portletURL);

		_portletRequest = getPortletRequest();
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_addBreadcrumbEntries();
	}

	@Override
	public GroupSearch getGroupSearch() throws Exception {
		if (_groupSearch != null) {
			return _groupSearch;
		}

		GroupItemSelectorCriterion groupItemSelectorCriterion =
			getGroupItemSelectorCriterion();

		PortletURL portletURL = getPortletURL();

		Group group = _getGroup();

		if (group != null) {
			portletURL.setParameter(
				"groupId", String.valueOf(group.getGroupId()));
		}

		_groupSearch = new GroupSearch(_portletRequest, portletURL);

		GroupSearchProvider.setResultsAndTotal(
			_getClassNames(), groupItemSelectorCriterion.getExcludedGroupIds(),
			_groupSearch, _portletRequest);

		if (_groupSearch.getStart() == 0) {
			if (groupItemSelectorCriterion.isIncludeUserPersonalSite()) {
				_prependGroup(
					_groupSearch,
					GroupLocalServiceUtil.getGroup(
						_themeDisplay.getCompanyId(),
						GroupConstants.USER_PERSONAL_SITE));
			}

			if (groupItemSelectorCriterion.isIncludeFormsSite()) {
				_prependGroup(
					_groupSearch,
					GroupLocalServiceUtil.getGroup(
						_themeDisplay.getCompanyId(), GroupConstants.FORMS));
			}
		}

		return _groupSearch;
	}

	@Override
	public boolean isShowChildSitesLink() {
		return true;
	}

	@Override
	public boolean isShowSortFilter() {
		return true;
	}

	private void _addBreadcrumbEntries() {
		Group group = _getGroup();

		if (group == null) {
			return;
		}

		try {
			PortletURL portletURL = getPortletURL();

			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, LanguageUtil.get(httpServletRequest, "all"),
				portletURL.toString());

			_addPortletBreadcrumbEntries(group, httpServletRequest, portletURL);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to add breadcrumb entries for group " +
					group.getGroupId(),
				exception);
		}
	}

	private void _addPortletBreadcrumbEntries(
			Group group, HttpServletRequest httpServletRequest,
			PortletURL portletURL)
		throws Exception {

		List<Group> ancestorGroups = group.getAncestors();

		Collections.reverse(ancestorGroups);

		for (Group ancestorGroup : ancestorGroups) {
			portletURL.setParameter(
				"groupId", String.valueOf(ancestorGroup.getGroupId()));

			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, ancestorGroup.getDescriptiveName(),
				portletURL.toString());
		}

		Group unescapedGroup = group.toUnescapedModel();

		portletURL.setParameter(
			"groupId", String.valueOf(unescapedGroup.getGroupId()));

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, unescapedGroup.getDescriptiveName(),
			portletURL.toString());
	}

	private List<String> _getClassNames() {
		if (groupItemSelectorCriterion.isIncludeCompany()) {
			return Arrays.asList(
				Company.class.getName(), Group.class.getName(),
				Organization.class.getName());
		}

		return Arrays.asList(
			Group.class.getName(), Organization.class.getName());
	}

	private Group _getGroup() {
		long groupId = ParamUtil.getLong(
			httpServletRequest, "groupId",
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		if (groupId > 0) {
			return GroupLocalServiceUtil.fetchGroup(groupId);
		}

		return null;
	}

	private void _prependGroup(GroupSearch groupSearch, Group group) {
		groupSearch.setResultsAndTotal(
			() -> ListUtil.concat(
				Arrays.asList(group), groupSearch.getResults()),
			groupSearch.getTotal() + 1);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MySitesItemSelectorViewDisplayContext.class);

	private GroupSearch _groupSearch;
	private final PortletRequest _portletRequest;
	private final ThemeDisplay _themeDisplay;

}