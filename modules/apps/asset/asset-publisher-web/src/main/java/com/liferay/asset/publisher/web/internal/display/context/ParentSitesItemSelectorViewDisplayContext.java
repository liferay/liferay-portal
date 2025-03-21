/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.display.context;

import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ParentSitesItemSelectorViewDisplayContext
	extends BaseItemSelectorViewDisplayContext {

	public ParentSitesItemSelectorViewDisplayContext(
		GroupItemSelectorCriterion groupItemSelectorCriterion,
		HttpServletRequest httpServletRequest,
		AssetPublisherHelper assetPublisherHelper, PortletURL portletURL) {

		super(httpServletRequest, assetPublisherHelper, portletURL);

		_groupItemSelectorCriterion = groupItemSelectorCriterion;
	}

	@Override
	public GroupSearch getGroupSearch() throws Exception {
		GroupSearch groupSearch = new GroupSearch(
			getPortletRequest(), portletURL);

		long[] excludedGroupIds =
			_groupItemSelectorCriterion.getExcludedGroupIds();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getSiteGroup();

		List<Group> groups = ListUtil.filter(
			group.getAncestors(),
			curGroup -> {
				if (curGroup.isContentSharingWithChildrenEnabled() &&
					!ArrayUtil.contains(excludedGroupIds, group.getGroupId())) {

					return true;
				}

				return false;
			});

		groupSearch.setResultsAndTotal(() -> groups, groups.size());

		return groupSearch;
	}

	@Override
	public boolean isShowSearch() {
		return false;
	}

	private final GroupItemSelectorCriterion _groupItemSelectorCriterion;

}