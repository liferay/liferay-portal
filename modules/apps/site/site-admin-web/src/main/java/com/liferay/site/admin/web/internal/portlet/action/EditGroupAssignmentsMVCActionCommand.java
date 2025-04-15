/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.site.admin.web.internal.constants.SiteAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SiteAdminPortletKeys.SITE_ADMIN,
		"mvc.command.name=/site_admin/edit_group_assignments"
	},
	service = MVCActionCommand.class
)
public class EditGroupAssignmentsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		long[] removeUserIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "removeUserIds"), 0L);

		removeUserIds = _filterRemoveUserIds(groupId, removeUserIds);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		_userService.unsetGroupUsers(groupId, removeUserIds, serviceContext);

		LiveUsers.leaveGroup(
			themeDisplay.getCompanyId(), groupId, removeUserIds);
	}

	private long[] _filterRemoveUserIds(long groupId, long[] userIds) {
		Set<Long> filteredUserIds = new HashSet<>();

		for (long userId : userIds) {
			if (_userLocalService.hasGroupUser(groupId, userId)) {
				filteredUserIds.add(userId);
			}
		}

		return ArrayUtil.toArray(filteredUserIds.toArray(new Long[0]));
	}

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserService _userService;

}