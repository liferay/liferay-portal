/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.display.context;

import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.service.AnnouncementsEntryLocalServiceUtil;
import com.liferay.announcements.web.internal.search.AnnouncementsEntryChecker;
import com.liferay.announcements.web.internal.util.AnnouncementsUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.announcements.service.permission.AnnouncementsEntryPermission;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Roberto Díaz
 */
public class AnnouncementsAdminViewDisplayContext {

	public AnnouncementsAdminViewDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		RenderRequest renderRequest) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_renderRequest = renderRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<String> getAvailableActions(
			AnnouncementsEntry announcementsEntry)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (AnnouncementsEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), announcementsEntry,
				ActionKeys.DELETE)) {

			availableActions.add("deleteEntries");
		}

		return availableActions;
	}

	public String getCurrentDistributionScopeLabel() throws Exception {
		String distributionScope = ParamUtil.getString(
			_httpServletRequest, "distributionScope");

		if (Validator.isNotNull(distributionScope)) {
			Map<String, String> distributionScopes = getDistributionScopes();

			for (Map.Entry<String, String> entry :
					distributionScopes.entrySet()) {

				String value = entry.getValue();

				if (value.equals(distributionScope)) {
					return entry.getKey();
				}
			}
		}

		return "general";
	}

	public String getDistributionScope() {
		return ParamUtil.getString(_httpServletRequest, "distributionScope");
	}

	public Map<String, String> getDistributionScopes() throws Exception {
		Map<String, String> distributionScopes = new LinkedHashMap<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (PortalPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				ActionKeys.ADD_GENERAL_ANNOUNCEMENTS)) {

			distributionScopes.put("general", "0,0");
		}

		List<Group> groups = AnnouncementsUtil.getGroups(themeDisplay);

		for (Group group : groups) {
			distributionScopes.put(
				StringBundler.concat(
					group.getDescriptiveName(themeDisplay.getLocale()), " (",
					LanguageUtil.get(_httpServletRequest, "site"), ")"),
				PortalUtil.getClassNameId(Group.class) + StringPool.COMMA +
					group.getGroupId());
		}

		List<Organization> organizations = AnnouncementsUtil.getOrganizations(
			themeDisplay);

		for (Organization organization : organizations) {
			String name = StringBundler.concat(
				organization.getName(), " (",
				LanguageUtil.get(_httpServletRequest, "organization"), ")");

			distributionScopes.put(
				name,
				PortalUtil.getClassNameId(Organization.class) +
					StringPool.COMMA + organization.getOrganizationId());
		}

		List<Role> roles = AnnouncementsUtil.getRoles(themeDisplay);

		for (Role role : roles) {
			distributionScopes.put(
				StringBundler.concat(
					role.getDescriptiveName(), " (",
					LanguageUtil.get(_httpServletRequest, "role"), ")"),
				PortalUtil.getClassNameId(Role.class) + StringPool.COMMA +
					role.getRoleId());
		}

		List<UserGroup> userGroups = AnnouncementsUtil.getUserGroups(
			themeDisplay);

		for (UserGroup userGroup : userGroups) {
			distributionScopes.put(
				StringBundler.concat(
					userGroup.getName(), " (",
					LanguageUtil.get(_httpServletRequest, "user-group"), ")"),
				PortalUtil.getClassNameId(UserGroup.class) + StringPool.COMMA +
					userGroup.getUserGroupId());
		}

		return distributionScopes;
	}

	public String getNavigation() {
		return ParamUtil.getString(
			_httpServletRequest, "navigation", "announcements");
	}

	public SearchContainer<AnnouncementsEntry> getSearchContainer() {
		SearchContainer<AnnouncementsEntry>
			announcementsEntriesSearchContainer = new SearchContainer<>(
				_renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM,
				SearchContainer.DEFAULT_DELTA,
				PortletURLUtil.getCurrent(
					_liferayPortletRequest, _liferayPortletResponse),
				null, "no-entries-were-found");

		announcementsEntriesSearchContainer.setId(getSearchContainerId());

		long classNameId = 0;
		long classPK = 0;

		String[] distributionScopeArray = StringUtil.split(
			getDistributionScope());

		if (distributionScopeArray.length == 2) {
			classNameId = GetterUtil.getLong(distributionScopeArray[0]);
			classPK = GetterUtil.getLong(distributionScopeArray[1]);
		}

		long announcementsClassNameId = classNameId;
		long announcementsClassPK = classPK;

		announcementsEntriesSearchContainer.setResultsAndTotal(
			() -> AnnouncementsEntryLocalServiceUtil.getEntries(
				_themeDisplay.getCompanyId(), announcementsClassNameId,
				announcementsClassPK, Objects.equals(getNavigation(), "alerts"),
				announcementsEntriesSearchContainer.getStart(),
				announcementsEntriesSearchContainer.getEnd()),
			AnnouncementsEntryLocalServiceUtil.getEntriesCount(
				_themeDisplay.getCompanyId(), announcementsClassNameId,
				announcementsClassPK,
				Objects.equals(getNavigation(), "alerts")));

		announcementsEntriesSearchContainer.setRowChecker(
			new AnnouncementsEntryChecker(
				_liferayPortletRequest, _liferayPortletResponse));

		return announcementsEntriesSearchContainer;
	}

	public String getSearchContainerId() {
		if (Objects.equals(getNavigation(), "alerts")) {
			return "alertsEntries";
		}

		return "announcementsEntries";
	}

	public UUID getUuid() {
		return _UUID;
	}

	private static final UUID _UUID = UUID.fromString(
		"14f20793-d4e2-4173-acd7-7f1c9cda9a36");

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final RenderRequest _renderRequest;
	private final ThemeDisplay _themeDisplay;

}