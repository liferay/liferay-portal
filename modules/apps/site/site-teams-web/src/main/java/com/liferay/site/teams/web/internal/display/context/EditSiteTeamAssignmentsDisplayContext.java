/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.teams.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class EditSiteTeamAssignmentsDisplayContext {

	public EditSiteTeamAssignmentsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		this.httpServletRequest = httpServletRequest;
		this.renderRequest = renderRequest;
		this.renderResponse = renderResponse;
	}

	public PortletURL getEditTeamAssignmentsURL() {
		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCPath(
			"/edit_team_assignments.jsp"
		).setTabs1(
			getTabs1()
		).setParameter(
			"teamId", getTeamId()
		).buildPortletURL();
	}

	public List<NavigationItem> getNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(Objects.equals(getTabs1(), "users"));
				navigationItem.setHref(
					getEditTeamAssignmentsURL(), "tabs1", "users");
				navigationItem.setLabel(
					LanguageUtil.get(httpServletRequest, "users"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(getTabs1(), "user-groups"));
				navigationItem.setHref(
					getEditTeamAssignmentsURL(), "tabs1", "user-groups");
				navigationItem.setLabel(
					LanguageUtil.get(httpServletRequest, "user-groups"));
			}
		).build();
	}

	public String getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(httpServletRequest, "tabs1", "users");

		return _tabs1;
	}

	public Team getTeam() {
		if (_team != null) {
			return _team;
		}

		_team = TeamLocalServiceUtil.fetchTeam(getTeamId());

		return _team;
	}

	public long getTeamId() {
		if (_teamId != null) {
			return _teamId;
		}

		_teamId = ParamUtil.getLong(httpServletRequest, "teamId");

		return _teamId;
	}

	public String getTeamName() {
		if (_teamName != null) {
			return _teamName;
		}

		Team team = getTeam();

		_teamName = team.getName();

		return _teamName;
	}

	protected final HttpServletRequest httpServletRequest;
	protected final RenderRequest renderRequest;
	protected final RenderResponse renderResponse;

	private String _tabs1;
	private Team _team;
	private Long _teamId;
	private String _teamName;

}