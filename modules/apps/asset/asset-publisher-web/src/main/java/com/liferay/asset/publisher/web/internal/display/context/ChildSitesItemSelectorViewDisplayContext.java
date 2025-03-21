/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.display.context;

import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ChildSitesItemSelectorViewDisplayContext
	extends BaseItemSelectorViewDisplayContext {

	public ChildSitesItemSelectorViewDisplayContext(
		GroupItemSelectorCriterion groupItemSelectorCriterion,
		HttpServletRequest httpServletRequest,
		AssetPublisherHelper assetPublisherHelper, PortletURL portletURL) {

		super(httpServletRequest, assetPublisherHelper, portletURL);

		_groupItemSelectorCriterion = groupItemSelectorCriterion;
	}

	@Override
	public GroupSearch getGroupSearch() throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		GroupSearch groupSearch = new GroupSearch(
			getPortletRequest(), portletURL);

		groupSearch.setResultsAndTotal(
			_filterGroups(
				GroupLocalServiceUtil.search(
					themeDisplay.getCompanyId(),
					new long[] {
						PortalUtil.getClassNameId(Group.class),
						PortalUtil.getClassNameId(Organization.class)
					},
					ParamUtil.getString(httpServletRequest, "keywords"),
					_getGroupParams(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					groupSearch.getOrderByComparator()),
				themeDisplay.getPermissionChecker()));

		return groupSearch;
	}

	private List<Group> _filterGroups(
		List<Group> groups, PermissionChecker permissionChecker) {

		return TransformUtil.transform(
			groups,
			group -> {
				if (permissionChecker.isGroupAdmin(group.getGroupId())) {
					return group;
				}

				return null;
			});
	}

	private LinkedHashMap<String, Object> _getGroupParams() {
		if (_groupParams != null) {
			return _groupParams;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_groupParams = LinkedHashMapBuilder.<String, Object>put(
			"active", Boolean.TRUE
		).put(
			"groupsTree", ListUtil.fromArray(themeDisplay.getSiteGroup())
		).put(
			"site", Boolean.TRUE
		).put(
			"excludedGroupIds",
			() -> {
				List<Long> excludedGroupIds = new ArrayList<>();

				if (_groupItemSelectorCriterion.getExcludedGroupIds() != null) {
					Collections.addAll(
						excludedGroupIds,
						ArrayUtil.toLongArray(
							_groupItemSelectorCriterion.getExcludedGroupIds()));
				}

				Group group = themeDisplay.getSiteGroup();

				if (group.isStagingGroup()) {
					excludedGroupIds.add(group.getLiveGroupId());
				}
				else {
					excludedGroupIds.add(themeDisplay.getSiteGroupId());
				}

				return excludedGroupIds;
			}
		).build();

		return _groupParams;
	}

	private final GroupItemSelectorCriterion _groupItemSelectorCriterion;
	private LinkedHashMap<String, Object> _groupParams;

}