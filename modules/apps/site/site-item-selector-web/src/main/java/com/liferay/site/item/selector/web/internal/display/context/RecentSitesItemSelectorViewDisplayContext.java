/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.item.selector.web.internal.display.context;

import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.item.selector.display.context.SitesItemSelectorViewDisplayContext;
import com.liferay.site.manager.RecentGroupManager;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Julio Camarero
 */
public class RecentSitesItemSelectorViewDisplayContext
	extends BaseSitesItemSelectorViewDisplayContext
	implements SitesItemSelectorViewDisplayContext {

	public RecentSitesItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest,
		GroupItemSelectorCriterion groupItemSelectorCriterion,
		String itemSelectedEventName, PortletURL portletURL,
		RecentGroupManager recentGroupManager) {

		super(
			httpServletRequest, groupItemSelectorCriterion,
			itemSelectedEventName, portletURL);

		_recentGroupManager = recentGroupManager;
	}

	@Override
	public String getGroupName(Group group) throws PortalException {
		String groupName = super.getGroupName(group);

		if (group.isStaged() && group.isStagingGroup()) {
			groupName = StringBundler.concat(
				groupName, StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
				LanguageUtil.get(httpServletRequest, "staging"),
				StringPool.CLOSE_PARENTHESIS);
		}

		return groupName;
	}

	@Override
	public GroupSearch getGroupSearch() throws Exception {
		GroupSearch groupSearch = new GroupSearch(
			getPortletRequest(), getPortletURL());

		groupSearch.setEmptyResultsMessage(
			"you-have-not-visited-any-sites-recently");

		GroupItemSelectorCriterion groupItemSelectorCriterion =
			getGroupItemSelectorCriterion();

		groupSearch.setResultsAndTotal(
			ListUtil.filter(
				_recentGroupManager.getRecentGroups(httpServletRequest),
				group -> !ArrayUtil.contains(
					groupItemSelectorCriterion.getExcludedGroupIds(),
					group.getGroupId())));

		return groupSearch;
	}

	@Override
	public boolean isShowSearch() {
		return false;
	}

	private final RecentGroupManager _recentGroupManager;

}