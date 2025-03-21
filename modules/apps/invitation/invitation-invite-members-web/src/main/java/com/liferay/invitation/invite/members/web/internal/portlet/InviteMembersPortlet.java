/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.web.internal.portlet;

import com.liferay.invitation.invite.members.constants.InviteMembersPortletKeys;
import com.liferay.invitation.invite.members.service.MemberRequestLocalService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.CustomSQLParam;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.UserFirstNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ryan Park
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=so-portlet-invite-members",
		"com.liferay.portlet.display-category=category.collaboration",
		"com.liferay.portlet.header-portlet-css=/invite_members/css/main.css",
		"com.liferay.portlet.icon=/icons/invite_members.png",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Invite Members",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/invite_members/view.jsp",
		"jakarta.portlet.name=" + InviteMembersPortletKeys.INVITE_MEMBERS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.supported-public-render-parameter=invitedMembersCount",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class InviteMembersPortlet extends MVCPortlet {

	public void getAvailableUsers(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		int end = ParamUtil.getInteger(resourceRequest, "end");
		String keywords = ParamUtil.getString(resourceRequest, "keywords");
		int start = ParamUtil.getInteger(resourceRequest, "start");

		JSONObject jsonObject = JSONUtil.put(
			"count",
			_getAvailableUsersCount(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
				keywords)
		).put(
			"options",
			JSONUtil.put(
				"end", end
			).put(
				"keywords", keywords
			).put(
				"start", start
			)
		);

		List<User> users = _getAvailableUsers(
			themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
			keywords, start, end);

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (User user : users) {
			jsonArray.put(
				JSONUtil.put(
					"hasPendingMemberRequest",
					_memberRequestLocalService.hasPendingMemberRequest(
						themeDisplay.getScopeGroupId(), user.getUserId())
				).put(
					"userEmailAddress", user.getEmailAddress()
				).put(
					"userFullName", user.getFullName()
				).put(
					"userId", user.getUserId()
				));
		}

		jsonObject.put("users", jsonArray);

		writeJSON(resourceRequest, resourceResponse, jsonObject);
	}

	public void sendInvites(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_sendInvite(actionRequest);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			String resourceID = resourceRequest.getResourceID();

			if (resourceID.equals("getAvailableUsers")) {
				getAvailableUsers(resourceRequest, resourceResponse);
			}
			else {
				super.serveResource(resourceRequest, resourceResponse);
			}
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	public void updateMemberRequest(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long memberRequestId = ParamUtil.getLong(
			actionRequest, "memberRequestId");
		int status = ParamUtil.getInteger(actionRequest, "status");

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			_memberRequestLocalService.updateMemberRequest(
				themeDisplay.getUserId(), memberRequestId, status);

			long userNotificationEventId = ParamUtil.getLong(
				actionRequest, "userNotificationEventId");

			if (userNotificationEventId != 0) {
				_updateArchived(
					themeDisplay.getUserId(), userNotificationEventId);
			}

			jsonObject.put("success", Boolean.TRUE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			jsonObject.put("success", Boolean.FALSE);
		}

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	private List<User> _getAvailableUsers(
			long companyId, long groupId, String keywords, int start, int end)
		throws Exception {

		return _userLocalService.search(
			companyId, keywords, WorkflowConstants.STATUS_APPROVED,
			LinkedHashMapBuilder.<String, Object>put(
				"usersInvited",
				new CustomSQLParam(
					_customSQL.get(
						getClass(),
						"com.liferay.portal.service.persistence.UserFinder." +
							"filterByUsersGroupsGroupId"),
					groupId)
			).build(),
			start, end, UserFirstNameComparator.getInstance(true));
	}

	private int _getAvailableUsersCount(
			long companyId, long groupId, String keywords)
		throws Exception {

		return _userLocalService.searchCount(
			companyId, keywords, WorkflowConstants.STATUS_APPROVED,
			LinkedHashMapBuilder.<String, Object>put(
				"usersInvited",
				new CustomSQLParam(
					_customSQL.get(
						getClass(),
						"com.liferay.portal.service.persistence.UserFinder." +
							"filterByUsersGroupsGroupId"),
					groupId)
			).build());
	}

	private long[] _getLongArray(PortletRequest portletRequest, String name) {
		String value = portletRequest.getParameter(name);

		if (value == null) {
			return null;
		}

		return StringUtil.split(GetterUtil.getString(value), 0L);
	}

	private String[] _getStringArray(
		PortletRequest portletRequest, String name) {

		String value = portletRequest.getParameter(name);

		if (value == null) {
			return null;
		}

		return StringUtil.split(GetterUtil.getString(value));
	}

	private void _sendInvite(ActionRequest actionRequest) throws Exception {
		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!_userLocalService.hasGroupUser(
				groupId, themeDisplay.getUserId())) {

			return;
		}

		long invitedTeamId = ParamUtil.getLong(actionRequest, "invitedTeamId");
		long[] receiverUserIds = _getLongArray(
			actionRequest, "receiverUserIds");
		long invitedRoleId = ParamUtil.getLong(actionRequest, "invitedRoleId");
		String[] receiverEmailAddresses = _getStringArray(
			actionRequest, "receiverEmailAddresses");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		serviceContext.setAttribute(
			"createAccountURL",
			_portal.getCreateAccountURL(
				_portal.getHttpServletRequest(actionRequest), themeDisplay));
		serviceContext.setAttribute("loginURL", themeDisplay.getURLSignIn());
		serviceContext.setAttribute(
			"redirectURL",
			String.valueOf(
				PortletProviderUtil.getPortletURL(
					actionRequest, _groupLocalService.getGroup(groupId),
					UserNotificationEvent.class.getName(),
					PortletProvider.Action.VIEW)));

		_memberRequestLocalService.addMemberRequests(
			themeDisplay.getUserId(), groupId, receiverUserIds, invitedRoleId,
			invitedTeamId, serviceContext);

		_memberRequestLocalService.addMemberRequests(
			themeDisplay.getUserId(), groupId, receiverEmailAddresses,
			invitedRoleId, invitedTeamId, serviceContext);
	}

	private void _updateArchived(long userId, long userNotificationEventId)
		throws Exception {

		UserNotificationEvent userNotificationEvent =
			_userNotificationEventLocalService.fetchUserNotificationEvent(
				userNotificationEventId);

		if (userNotificationEvent == null) {
			return;
		}

		if (userNotificationEvent.getUserId() != userId) {
			throw new PrincipalException();
		}

		userNotificationEvent.setArchived(true);

		_userNotificationEventLocalService.updateUserNotificationEvent(
			userNotificationEvent);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InviteMembersPortlet.class);

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private MemberRequestLocalService _memberRequestLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.invitation.invite.members.service)(&(release.schema.version>=2.0.0)(!(release.schema.version>=3.0.0))))"
	)
	private Release _release;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}