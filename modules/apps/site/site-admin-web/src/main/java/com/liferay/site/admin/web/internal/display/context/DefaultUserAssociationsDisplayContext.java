/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.teams.item.selector.SiteTeamsItemSelectorCriterion;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class DefaultUserAssociationsDisplayContext {

	public DefaultUserAssociationsDisplayContext(
		HttpServletRequest httpServletRequest, ItemSelector itemSelector) {

		_httpServletRequest = httpServletRequest;
		_itemSelector = itemSelector;

		_groupTypeSettingsUnicodeProperties =
			(UnicodeProperties)httpServletRequest.getAttribute(
				"site.groupTypeSettings");
		_liferayPortletResponse = PortalUtil.getLiferayPortletResponse(
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE));
		_liveGroupId = (long)httpServletRequest.getAttribute(
			"site.liveGroupId");
	}

	public String getSelectSiteRolePortletNamespace() {
		String selectSiteRolePortletId = PortletProviderUtil.getPortletId(
			Role.class.getName(), PortletProvider.Action.BROWSE);

		return PortalUtil.getPortletNamespace(selectSiteRolePortletId);
	}

	public String getSelectSiteRoleURL() throws PortalException {
		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				_httpServletRequest, Role.class.getName(),
				PortletProvider.Action.BROWSE)
		).setParameter(
			"eventName",
			_liferayPortletResponse.getNamespace() + "selectSiteRole"
		).setParameter(
			"groupId", _liveGroupId
		).setParameter(
			"roleType", RoleConstants.TYPE_SITE
		).setParameter(
			"step", "2"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getSelectTeamURL() {
		SiteTeamsItemSelectorCriterion siteTeamsItemSelectorCriterion =
			new SiteTeamsItemSelectorCriterion();

		siteTeamsItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_liferayPortletResponse.getNamespace() + "selectTeam",
				siteTeamsItemSelectorCriterion));
	}

	public SearchContainer<Role> getSiteRolesSearchContainer() {
		SearchContainer<Role> siteRolesSearchContainer =
			new SearchContainer<>();

		siteRolesSearchContainer.setResultsAndTotal(
			TransformUtil.transformToList(
				StringUtil.split(
					_groupTypeSettingsUnicodeProperties.getProperty(
						"defaultSiteRoleIds"),
					0L),
				RoleLocalServiceUtil::fetchRole));

		return siteRolesSearchContainer;
	}

	public SearchContainer<Team> getTeamsSearchContainer() {
		SearchContainer<Team> teamsSearchContainer = new SearchContainer<>();

		teamsSearchContainer.setResultsAndTotal(
			TransformUtil.transformToList(
				StringUtil.split(
					_groupTypeSettingsUnicodeProperties.getProperty(
						"defaultTeamIds"),
					0L),
				TeamLocalServiceUtil::fetchTeam));

		return teamsSearchContainer;
	}

	private final UnicodeProperties _groupTypeSettingsUnicodeProperties;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final long _liveGroupId;

}