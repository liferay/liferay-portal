/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.my.sites.web.internal.portlet;

import com.liferay.portal.kernel.exception.MembershipRequestCommentsException;
import com.liferay.portal.kernel.model.MembershipRequestConstants;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.MembershipRequestLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.site.my.sites.web.internal.constants.MySitesPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-my-sites",
		"com.liferay.portlet.display-category=category.community",
		"com.liferay.portlet.icon=/icons/communities.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=My Sites",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + MySitesPortletKeys.MY_SITES,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class MySitesPortlet extends MVCPortlet {

	public void postMembershipRequest(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		long userId = serviceContext.getUserId();

		if (_membershipRequestLocalService.hasMembershipRequest(
				userId, groupId, MembershipRequestConstants.STATUS_PENDING)) {

			SessionErrors.add(actionRequest, "membershipAlreadyRequested");

			hideDefaultErrorMessage(actionRequest);
		}
		else {
			String comments = ParamUtil.getString(actionRequest, "comments");

			_membershipRequestLocalService.addMembershipRequest(
				userId, groupId, comments, serviceContext);

			SessionMessages.add(actionRequest, "membershipRequestSent");

			addSuccessMessage(actionRequest, actionResponse);

			hideDefaultSuccessMessage(actionRequest);
		}

		sendRedirect(actionRequest, actionResponse);
	}

	public void updateGroupUsers(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		long[] addUserIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "addUserIds"), 0L);

		addUserIds = _filterAddUserIds(groupId, addUserIds);

		long[] removeUserIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "removeUserIds"), 0L);

		removeUserIds = _filterRemoveUserIds(groupId, removeUserIds);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		_userService.addGroupUsers(groupId, addUserIds, serviceContext);
		_userService.unsetGroupUsers(groupId, removeUserIds, serviceContext);

		LiveUsers.joinGroup(themeDisplay.getCompanyId(), groupId, addUserIds);
		LiveUsers.leaveGroup(
			themeDisplay.getCompanyId(), groupId, removeUserIds);
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof MembershipRequestCommentsException ||
			throwable instanceof PrincipalException ||
			super.isSessionErrorException(throwable)) {

			return true;
		}

		return false;
	}

	private long[] _filterAddUserIds(long groupId, long[] userIds)
		throws Exception {

		Set<Long> filteredUserIds = new HashSet<>();

		for (long userId : userIds) {
			if (!_userLocalService.hasGroupUser(groupId, userId)) {
				filteredUserIds.add(userId);
			}
		}

		return ArrayUtil.toArray(filteredUserIds.toArray(new Long[0]));
	}

	private long[] _filterRemoveUserIds(long groupId, long[] userIds)
		throws Exception {

		Set<Long> filteredUserIds = new HashSet<>();

		for (long userId : userIds) {
			if (_userLocalService.hasGroupUser(groupId, userId)) {
				filteredUserIds.add(userId);
			}
		}

		return ArrayUtil.toArray(filteredUserIds.toArray(new Long[0]));
	}

	@Reference
	private MembershipRequestLocalService _membershipRequestLocalService;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.site.my.sites.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserService _userService;

}