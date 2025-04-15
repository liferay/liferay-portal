/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.teams.web.internal.portlet;

import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.exception.DuplicateTeamException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchTeamException;
import com.liferay.portal.kernel.exception.TeamNameException;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.TeamService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.teams.web.internal.constants.SiteTeamsPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-communities",
		"com.liferay.portlet.icon=/icons/site_teams.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Site Teams",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + SiteTeamsPortletKeys.SITE_TEAMS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SiteTeamsPortlet extends MVCPortlet {

	public void addTeamUserGroups(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long teamId = ParamUtil.getLong(actionRequest, "teamId");

		long[] addUserGroupIds = ParamUtil.getLongValues(
			actionRequest, "rowIds");

		_userGroupService.addTeamUserGroups(teamId, addUserGroupIds);
	}

	public void addTeamUsers(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long teamId = ParamUtil.getLong(actionRequest, "teamId");

		long[] addUserIds = ParamUtil.getLongValues(actionRequest, "rowIds");

		_userService.addTeamUsers(teamId, addUserIds);
	}

	public void deleteTeam(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long teamId = ParamUtil.getLong(actionRequest, "teamId");

		_teamService.deleteTeam(teamId);
	}

	public void deleteTeams(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] teamIds = ParamUtil.getLongValues(actionRequest, "rowIds");

		for (long teamId : teamIds) {
			_teamService.deleteTeam(teamId);
		}
	}

	public void deleteTeamUserGroups(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long teamId = ParamUtil.getLong(actionRequest, "teamId");

		long[] removeUserGroupIds = null;

		long removeUserGroupId = ParamUtil.getLong(
			actionRequest, "removeUserGroupId");

		if (removeUserGroupId > 0) {
			removeUserGroupIds = new long[] {removeUserGroupId};
		}
		else {
			removeUserGroupIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		_userGroupService.unsetTeamUserGroups(teamId, removeUserGroupIds);
	}

	public void deleteTeamUsers(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long teamId = ParamUtil.getLong(actionRequest, "teamId");

		long[] removeUserIds = null;

		long removeUserId = ParamUtil.getLong(actionRequest, "removeUserId");

		if (removeUserId > 0) {
			removeUserIds = new long[] {removeUserId};
		}
		else {
			removeUserIds = ParamUtil.getLongValues(actionRequest, "rowIds");
		}

		_userService.unsetTeamUsers(teamId, removeUserIds);
	}

	public void editTeam(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long teamId = ParamUtil.getLong(actionRequest, "teamId");

		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");

		if (teamId <= 0) {

			// Add team

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				Team.class.getName(), actionRequest);

			_teamService.addTeam(
				themeDisplay.getSiteGroupId(), name, description,
				serviceContext);
		}
		else {

			// Update team

			_teamService.updateTeam(teamId, name, description);
		}
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(ItemSelector.class.getName(), _itemSelector);

		super.render(renderRequest, renderResponse);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof DuplicateTeamException ||
			throwable instanceof NoSuchGroupException ||
			throwable instanceof NoSuchTeamException ||
			throwable instanceof PrincipalException ||
			throwable instanceof TeamNameException ||
			super.isSessionErrorException(throwable)) {

			return true;
		}

		return false;
	}

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private TeamService _teamService;

	@Reference
	private UserGroupService _userGroupService;

	@Reference
	private UserService _userService;

}