/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItemListBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.MembershipRequestConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.MembershipRequestLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.GroupDescriptiveNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.persistence.constants.UserGroupFinderConstants;
import com.liferay.site.admin.web.internal.constants.SiteAdminPortletKeys;
import com.liferay.site.admin.web.internal.search.SiteChecker;
import com.liferay.site.admin.web.internal.servlet.taglib.util.SiteActionDropdownItemsProvider;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryBuilder;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;
import com.liferay.site.provider.GroupSearchProvider;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Pavel Savinov
 * @author Marco Leo
 */
public class SiteAdminDisplayContext {

	public SiteAdminDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems(Group group)
		throws Exception {

		SiteActionDropdownItemsProvider siteActionDropdownItemsProvider =
			new SiteActionDropdownItemsProvider(
				group, _liferayPortletRequest, _liferayPortletResponse, this);

		return siteActionDropdownItemsProvider.getActionDropdownItems();
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries() throws PortalException {
		Group group = getGroup();

		if (group == null) {
			return BreadcrumbEntryListBuilder.add(
				breadcrumbEntry -> breadcrumbEntry.setTitle(
					LanguageUtil.get(_httpServletRequest, "sites"))
			).build();
		}

		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(_httpServletRequest, "sites"));
				breadcrumbEntry.setURL(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCPath(
						"/view.jsp"
					).buildString());
			}
		).addAll(
			() -> {
				List<Group> ancestorGroups = group.getAncestors();

				Collections.reverse(ancestorGroups);

				return TransformUtil.transform(
					ancestorGroups,
					ancestorGroup -> BreadcrumbEntryBuilder.setTitle(
						ancestorGroup.getDescriptiveName()
					).setURL(
						PortletURLBuilder.createRenderURL(
							_liferayPortletResponse
						).setMVCPath(
							"/view.jsp"
						).setParameter(
							"groupId", ancestorGroup.getGroupId()
						).buildString()
					).build());
			}
		).add(
			breadcrumbEntry -> {
				Group unescapedGroup = group.toUnescapedModel();

				breadcrumbEntry.setTitle(unescapedGroup.getDescriptiveName());
			}
		).build();
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest, SiteAdminPortletKeys.SITE_ADMIN, "list");

		return _displayStyle;
	}

	public Group getGroup() throws PortalException {
		long groupId = getGroupId();

		if (groupId > 0) {
			_group = GroupServiceUtil.getGroup(groupId);
		}

		return _group;
	}

	public long getGroupId() {
		if (_groupId <= 0) {
			_groupId = ParamUtil.getLong(
				_httpServletRequest, "groupId",
				GroupConstants.DEFAULT_PARENT_GROUP_ID);
		}

		return _groupId;
	}

	public GroupSearch getGroupSearch() throws PortalException {
		GroupSearch groupSearch = new GroupSearch(
			_liferayPortletRequest, getPortletURL());

		groupSearch.setId("sites");
		groupSearch.setOrderByCol("descriptive-name");
		groupSearch.setOrderByComparator(
			new GroupDescriptiveNameComparator(
				Objects.equals(groupSearch.getOrderByType(), "asc"),
				_themeDisplay.getLocale()));

		GroupSearchProvider.setResultsAndTotal(
			Arrays.asList(
				Company.class.getName(), Group.class.getName(),
				Organization.class.getName()),
			null, groupSearch, _liferayPortletRequest);

		SiteChecker siteChecker = new SiteChecker(_liferayPortletResponse);

		siteChecker.setRememberCheckBoxStateURLRegex(
			StringBundler.concat(
				"^(?!.*", _liferayPortletResponse.getNamespace(),
				"redirect).*(groupId=", getGroupId(), ")"));

		groupSearch.setRowChecker(siteChecker);

		return groupSearch;
	}

	public int getOrganizationsCount(Group group) {
		return OrganizationLocalServiceUtil.searchCount(
			_themeDisplay.getCompanyId(),
			OrganizationConstants.ANY_PARENT_ORGANIZATION_ID, null, null, null,
			null,
			LinkedHashMapBuilder.<String, Object>put(
				"groupOrganization", group.getGroupId()
			).put(
				"organizationsGroups", group.getGroupId()
			).build());
	}

	public int getPendingRequestsCount(Group group) {
		int pendingRequests = 0;

		if (group.getType() == GroupConstants.TYPE_SITE_RESTRICTED) {
			pendingRequests = MembershipRequestLocalServiceUtil.searchCount(
				group.getGroupId(), MembershipRequestConstants.STATUS_PENDING);
		}

		return pendingRequests;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setParameter(
			"displayStyle", getDisplayStyle()
		).setParameter(
			"groupId", getGroupId()
		).buildPortletURL();
	}

	public List<TabsItem> getTabsItem() {
		return TabsItemListBuilder.add(
			tabsItem -> {
				tabsItem.setActive(true);
				tabsItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "details"));
			}
		).build();
	}

	public int getUserGroupsCount(Group group) {
		return UserGroupLocalServiceUtil.searchCount(
			_themeDisplay.getCompanyId(), null,
			LinkedHashMapBuilder.<String, Object>put(
				UserGroupFinderConstants.PARAM_KEY_USER_GROUPS_GROUPS,
				group.getGroupId()
			).build());
	}

	public int getUsersCount(Group group) {
		return UserLocalServiceUtil.searchCount(
			_themeDisplay.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED,
			LinkedHashMapBuilder.<String, Object>put(
				"inherit", Boolean.TRUE
			).put(
				"usersGroups", group.getGroupId()
			).build());
	}

	public boolean hasAddChildSitePermission(Group group)
		throws PortalException {

		if (!group.isCompany() &&
			(PortalPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				ActionKeys.ADD_COMMUNITY) ||
			 GroupPermissionUtil.contains(
				 _themeDisplay.getPermissionChecker(), group,
				 ActionKeys.ADD_COMMUNITY))) {

			return true;
		}

		return false;
	}

	private String _displayStyle;
	private Group _group;
	private long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}