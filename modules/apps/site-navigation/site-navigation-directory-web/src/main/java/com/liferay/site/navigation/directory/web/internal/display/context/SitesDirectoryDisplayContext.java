/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.directory.web.internal.display.context;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;
import com.liferay.site.navigation.directory.web.internal.configuration.SitesDirectoryPortletInstanceConfiguration;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Juergen Kappler
 */
public class SitesDirectoryDisplayContext {

	public SitesDirectoryDisplayContext(HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		_httpServletRequest = httpServletRequest;

		_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);

		_sitesDirectoryPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				SitesDirectoryPortletInstanceConfiguration.class,
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY));
	}

	public List<Group> getBranchGroups() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		return new ArrayList<Group>() {
			{
				add(group);
				addAll(group.getAncestors());
			}
		};
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		_displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle",
			_sitesDirectoryPortletInstanceConfiguration.displayStyle());

		return _displayStyle;
	}

	public Group getRootGroup() {
		Group rootGroup = null;

		List<Group> branchGroups = getBranchGroups();

		if (Objects.equals(getSites(), _SITES_CHILDREN) &&
			!branchGroups.isEmpty()) {

			rootGroup = branchGroups.get(0);
		}
		else if (Objects.equals(getSites(), _SITES_SIBLINGS) &&
				 (branchGroups.size() > 1)) {

			rootGroup = branchGroups.get(1);
		}
		else if (Objects.equals(getSites(), _SITES_PARENT_LEVEL) &&
				 (branchGroups.size() > 2)) {

			rootGroup = branchGroups.get(2);
		}

		return rootGroup;
	}

	public SearchContainer<Group> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletURL portletURL = PortletURLFactoryUtil.create(
			_httpServletRequest, portletDisplay.getId(),
			PortletRequest.RENDER_PHASE);

		_searchContainer = new SearchContainer(
			_portletRequest, portletURL, null, "no-sites-were-found");

		List<Group> childGroups = null;

		Group rootGroup = getRootGroup();

		if (rootGroup != null) {
			childGroups = rootGroup.getChildrenWithLayouts(
				true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new GroupNameComparator(true, themeDisplay.getLocale()));
		}
		else {
			childGroups = GroupLocalServiceUtil.getLayoutsGroups(
				themeDisplay.getCompanyId(),
				GroupConstants.DEFAULT_LIVE_GROUP_ID, true, true,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new GroupNameComparator(true, themeDisplay.getLocale()));
		}

		Set<Group> visibleGroups = new LinkedHashSet<>();

		for (Group childGroup : childGroups) {
			if (childGroup.hasPublicLayouts()) {
				visibleGroups.add(childGroup);
			}
			else if (GroupLocalServiceUtil.hasUserGroup(
						themeDisplay.getUserId(), childGroup.getGroupId())) {

				visibleGroups.add(childGroup);
			}
		}

		_searchContainer.setResultsAndTotal(new ArrayList<>(visibleGroups));

		return _searchContainer;
	}

	public String getSites() {
		if (_sites != null) {
			return _sites;
		}

		_sites = ParamUtil.getString(
			_httpServletRequest, "sites",
			_sitesDirectoryPortletInstanceConfiguration.sites());

		return _sites;
	}

	public boolean isHidden() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<Group> branchGroups = getBranchGroups();

		Group group = themeDisplay.getScopeGroup();

		if (Objects.equals(getSites(), _SITES_TOP_LEVEL) ||
			(Objects.equals(getSites(), _SITES_CHILDREN) &&
			 !branchGroups.isEmpty()) ||
			(Objects.equals(getSites(), _SITES_SIBLINGS) &&
			 (branchGroups.size() > 1)) ||
			(Objects.equals(getSites(), _SITES_SIBLINGS) && group.isRoot()) ||
			(Objects.equals(getSites(), _SITES_PARENT_LEVEL) &&
			 (branchGroups.size() > 2)) ||
			(Objects.equals(getSites(), _SITES_PARENT_LEVEL) &&
			 (branchGroups.size() == 2))) {

			return false;
		}

		return true;
	}

	private static final String _SITES_CHILDREN = "children";

	private static final String _SITES_PARENT_LEVEL = "parent-level";

	private static final String _SITES_SIBLINGS = "siblings";

	private static final String _SITES_TOP_LEVEL = "top-level";

	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final PortletRequest _portletRequest;
	private SearchContainer<Group> _searchContainer;
	private String _sites;
	private final SitesDirectoryPortletInstanceConfiguration
		_sitesDirectoryPortletInstanceConfiguration;

}