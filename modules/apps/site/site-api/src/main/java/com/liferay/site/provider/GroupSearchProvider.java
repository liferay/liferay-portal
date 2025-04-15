/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.provider;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletRequest;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Julio Camarero
 */
public class GroupSearchProvider {

	public static void setResultsAndTotal(
			List<String> classNames, long[] excludedGroupIds,
			GroupSearch groupSearch, PortletRequest portletRequest)
		throws PortalException {

		long parentGroupId = _getParentGroupId(portletRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!_isSearch(portletRequest) &&
			_isFilterManageableGroups(portletRequest) && (parentGroupId <= 0)) {

			groupSearch.setResultsAndTotal(
				ListUtil.sort(
					_getAllGroups(classNames, portletRequest),
					groupSearch.getOrderByComparator()));
		}
		else if (_isSearch(portletRequest)) {
			long[] classNameIds = TransformUtil.transformToLongArray(
				classNames, PortalUtil::getClassNameId);

			groupSearch.setResultsAndTotal(
				() -> GroupLocalServiceUtil.search(
					themeDisplay.getCompanyId(), classNameIds,
					_getKeywords(portletRequest),
					_getGroupParams(
						classNames, excludedGroupIds, portletRequest,
						parentGroupId),
					groupSearch.getStart(), groupSearch.getEnd(),
					groupSearch.getOrderByComparator()),
				GroupLocalServiceUtil.searchCount(
					themeDisplay.getCompanyId(), classNameIds,
					_getKeywords(portletRequest),
					_getGroupParams(
						classNames, excludedGroupIds, portletRequest,
						parentGroupId)));
		}
		else {
			long[] classNameIds = TransformUtil.transformToLongArray(
				classNames, PortalUtil::getClassNameId);

			long groupId = ParamUtil.getLong(
				portletRequest, "groupId",
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			groupSearch.setResultsAndTotal(
				() -> GroupLocalServiceUtil.search(
					themeDisplay.getCompanyId(), classNameIds, groupId,
					_getKeywords(portletRequest),
					_getGroupParams(
						classNames, excludedGroupIds, portletRequest,
						parentGroupId),
					groupSearch.getStart(), groupSearch.getEnd(),
					groupSearch.getOrderByComparator()),
				GroupLocalServiceUtil.searchCount(
					themeDisplay.getCompanyId(), classNameIds, groupId,
					_getKeywords(portletRequest),
					_getGroupParams(
						classNames, excludedGroupIds, portletRequest,
						parentGroupId)));
		}
	}

	private static List<Group> _getAllGroups(
			List<String> classNames, PortletRequest portletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		List<Group> groups = user.getMySiteGroups(
			ArrayUtil.toStringArray(classNames), QueryUtil.ALL_POS);

		long groupId = ParamUtil.getLong(
			portletRequest, "groupId", GroupConstants.DEFAULT_PARENT_GROUP_ID);

		if (groupId != GroupConstants.DEFAULT_PARENT_GROUP_ID) {
			groups.clear();

			groups.add(GroupLocalServiceUtil.getGroup(groupId));
		}

		return groups;
	}

	private static LinkedHashMap<String, Object> _getGroupParams(
			List<String> classNames, long[] excludedGroupIds,
			PortletRequest portletRequest, long parentGroupId)
		throws PortalException {

		LinkedHashMap<String, Object> groupParams =
			LinkedHashMapBuilder.<String, Object>put(
				"actionId", ActionKeys.VIEW
			).put(
				"excludedGroupIds", ListUtil.fromArray(excludedGroupIds)
			).put(
				"site", Boolean.TRUE
			).build();

		if (_isSearch(portletRequest)) {
			if (_isFilterManageableGroups(portletRequest)) {
				groupParams.put(
					"groupsTree", _getAllGroups(classNames, portletRequest));
			}
			else if (parentGroupId > 0) {
				List<Group> groupsTree = ListUtil.fromArray(
					GroupLocalServiceUtil.getGroup(parentGroupId));

				groupParams.put("groupsTree", groupsTree);
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			if (!permissionChecker.isCompanyAdmin() &&
				!GroupPermissionUtil.contains(
					permissionChecker, ActionKeys.VIEW)) {

				User user = themeDisplay.getUser();

				groupParams.put("usersGroups", user.getUserId());
			}
		}

		return groupParams;
	}

	private static String _getKeywords(PortletRequest portletRequest) {
		return ParamUtil.getString(portletRequest, "keywords");
	}

	private static long _getParentGroupId(PortletRequest portletRequest) {
		Group group = null;

		long groupId = ParamUtil.getLong(
			portletRequest, "groupId", GroupConstants.DEFAULT_PARENT_GROUP_ID);

		if (groupId > 0) {
			group = GroupLocalServiceUtil.fetchGroup(groupId);
		}

		if (group != null) {
			return group.getGroupId();
		}

		if (_isFilterManageableGroups(portletRequest)) {
			return GroupConstants.ANY_PARENT_GROUP_ID;
		}

		return GroupConstants.DEFAULT_PARENT_GROUP_ID;
	}

	private static boolean _isFilterManageableGroups(
		PortletRequest portletRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin() ||
			GroupPermissionUtil.contains(permissionChecker, ActionKeys.VIEW)) {

			return false;
		}

		return true;
	}

	private static boolean _isSearch(PortletRequest portletRequest) {
		return Validator.isNotNull(_getKeywords(portletRequest));
	}

}