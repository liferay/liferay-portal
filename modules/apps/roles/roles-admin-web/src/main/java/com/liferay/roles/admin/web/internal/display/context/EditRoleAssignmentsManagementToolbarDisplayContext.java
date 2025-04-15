/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.organizations.search.OrganizationSearch;
import com.liferay.organizations.search.OrganizationSearchTerms;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.persistence.constants.UserGroupFinderConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;
import com.liferay.roles.admin.constants.RolesAdminWebKeys;
import com.liferay.roles.admin.search.GroupRoleChecker;
import com.liferay.roles.admin.search.OrganizationRoleChecker;
import com.liferay.roles.admin.search.SetUserRoleChecker;
import com.liferay.roles.admin.search.UnsetUserRoleChecker;
import com.liferay.roles.admin.search.UserGroupRoleChecker;
import com.liferay.roles.admin.web.internal.dao.search.SegmentsEntrySearchContainerFactory;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.site.search.GroupSearch;
import com.liferay.users.admin.search.UserSearch;
import com.liferay.users.admin.search.UserSearchTerms;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class EditRoleAssignmentsManagementToolbarDisplayContext {

	public EditRoleAssignmentsManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest, RenderRequest renderRequest,
			RenderResponse renderResponse, String displayStyle, String tabs3)
		throws PortalException {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_displayStyle = displayStyle;
		_tabs3 = tabs3;

		_role = RoleServiceUtil.fetchRole(
			ParamUtil.getLong(httpServletRequest, "roleId"));
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		SearchContainer<?> searchContainer = getSearchContainer();

		if (Objects.equals(getTabs2(), "users") &&
			Objects.equals(_role.getName(), RoleConstants.ADMINISTRATOR) &&
			(searchContainer.getTotal() == 1)) {

			return null;
		}

		return DropdownItemList.of(
			DropdownItemBuilder.putData(
				"action", "unsetRoleAssignments"
			).setIcon(
				"times-circle"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "remove")
			).setQuickAction(
				true
			).build());
	}

	public String getClearResultsURL() throws PortalException {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public CreationMenu getCreationMenu() throws PortalException {
		if (!_tabs2.equals("segments")) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "addSegmentEntry");

				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				Group companyGroup = GroupLocalServiceUtil.fetchCompanyGroup(
					themeDisplay.getCompanyId());

				dropdownItem.putData(
					"addSegmentEntryURL",
					PortletURLBuilder.create(
						PortletProviderUtil.getPortletURL(
							_renderRequest, companyGroup,
							SegmentsEntry.class.getName(),
							PortletProvider.Action.EDIT)
					).setRedirect(
						ParamUtil.getString(_httpServletRequest, "redirect")
					).setBackURL(
						ParamUtil.getString(_httpServletRequest, "backURL")
					).setParameter(
						"groupId", themeDisplay.getCompanyGroupId()
					).buildString());

				dropdownItem.putData(
					"sessionKey", RolesAdminWebKeys.MODAL_SEGMENT_STATE);

				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new-segment"));
			}
		).build();
	}

	public SearchContainer<Group> getGroupSearchContainer()
		throws PortalException {

		GroupSearch groupSearch = new GroupSearch(
			_renderRequest, getPortletURL());

		if (_tabs3.equals("available")) {
			groupSearch.setRowChecker(
				new GroupRoleChecker(_renderResponse, _role));
		}
		else {
			groupSearch.setRowChecker(
				new EmptyOnClickRowChecker(_renderResponse));
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LinkedHashMap<String, Object> groupParams = new LinkedHashMap<>();

		if (_tabs3.equals("current")) {
			groupParams.put("groupsRoles", Long.valueOf(_role.getRoleId()));
			groupParams.put("site", Boolean.TRUE);
		}

		groupSearch.setResultsAndTotal(
			() -> GroupLocalServiceUtil.search(
				themeDisplay.getCompanyId(), getKeywords(), groupParams,
				groupSearch.getStart(), groupSearch.getEnd(),
				groupSearch.getOrderByComparator()),
			GroupLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), getKeywords(), groupParams));

		return groupSearch;
	}

	public String getKeywords() {
		if (Validator.isNull(_keywords)) {
			_keywords = ParamUtil.getString(_httpServletRequest, "keywords");
		}

		return _keywords;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, RolesAdminPortletKeys.ROLES_ADMIN,
			"edit-role-order-by-col", "name");

		return _orderByCol;
	}

	public List<DropdownItem> getOrderByDropDownItems() throws PortalException {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(Objects.equals(getOrderByCol(), "name"));
				dropdownItem.setHref(getPortletURL(), "orderByCol", "name");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "name"));
			}
		).build();
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, RolesAdminPortletKeys.ROLES_ADMIN,
			"edit-role-order-by-type", "asc");

		return _orderByType;
	}

	public SearchContainer<Organization> getOrganizationSearchContainer()
		throws PortalException {

		OrganizationSearch organizationSearch = new OrganizationSearch(
			_renderRequest, getPortletURL());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long parentOrganizationId =
			OrganizationConstants.ANY_PARENT_ORGANIZATION_ID;

		LinkedHashMap<String, Object> organizationParams =
			new LinkedHashMap<>();

		if (_tabs3.equals("current")) {
			organizationParams.put(
				"organizationsRoles", Long.valueOf(_role.getRoleId()));
		}

		OrganizationSearchTerms searchTerms =
			(OrganizationSearchTerms)organizationSearch.getSearchTerms();

		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			Organization.class);

		if (!_tabs3.equals("current") && indexer.isIndexerEnabled() &&
			PropsValues.ORGANIZATIONS_SEARCH_WITH_INDEX) {

			organizationParams.put("expandoAttributes", getKeywords());

			organizationSearch.setResultsAndTotal(
				OrganizationLocalServiceUtil.searchOrganizations(
					themeDisplay.getCompanyId(), parentOrganizationId,
					getKeywords(), organizationParams,
					organizationSearch.getStart(), organizationSearch.getEnd(),
					SortFactoryUtil.getSort(
						Organization.class, organizationSearch.getOrderByCol(),
						organizationSearch.getOrderByType())));
		}
		else {
			organizationSearch.setResultsAndTotal(
				() -> OrganizationLocalServiceUtil.search(
					themeDisplay.getCompanyId(), parentOrganizationId,
					getKeywords(), searchTerms.getType(),
					searchTerms.getRegionIdObj(), searchTerms.getCountryIdObj(),
					organizationParams, organizationSearch.getStart(),
					organizationSearch.getEnd(),
					organizationSearch.getOrderByComparator()),
				OrganizationLocalServiceUtil.searchCount(
					themeDisplay.getCompanyId(), parentOrganizationId,
					searchTerms.getKeywords(), searchTerms.getType(),
					searchTerms.getRegionIdObj(), searchTerms.getCountryIdObj(),
					organizationParams));
		}

		if (_tabs3.equals("available")) {
			organizationSearch.setRowChecker(
				new OrganizationRoleChecker(_renderResponse, _role));
		}
		else {
			organizationSearch.setRowChecker(
				new EmptyOnClickRowChecker(_renderResponse));
		}

		return organizationSearch;
	}

	public PortletURL getPortletURL() throws PortalException {
		PortletURL portletURL = _renderResponse.createRenderURL();

		if (_tabs3.equals("current")) {
			portletURL.setParameter("mvcPath", "/edit_role_assignments.jsp");
		}
		else {
			portletURL.setParameter("mvcPath", "/select_assignees.jsp");
		}

		portletURL.setParameter("tabs1", "assignees");
		portletURL.setParameter("tabs2", getTabs2());
		portletURL.setParameter("tabs3", _tabs3);
		portletURL.setParameter(
			"redirect", ParamUtil.getString(_httpServletRequest, "redirect"));
		portletURL.setParameter(
			"backURL", ParamUtil.getString(_httpServletRequest, "backURL"));
		portletURL.setParameter("roleId", String.valueOf(_role.getRoleId()));
		portletURL.setParameter("displayStyle", _displayStyle);

		if (Validator.isNotNull(getKeywords())) {
			portletURL.setParameter("keywords", getKeywords());
		}

		portletURL.setParameter("orderByCol", getOrderByCol());
		portletURL.setParameter("orderByType", getOrderByType());

		if (_searchContainer != null) {
			portletURL.setParameter(
				_searchContainer.getCurParam(),
				String.valueOf(_searchContainer.getCur()));
			portletURL.setParameter(
				_searchContainer.getDeltaParam(),
				String.valueOf(_searchContainer.getDelta()));
		}

		return portletURL;
	}

	public String getSearchActionURL() throws PortalException {
		return PortletURLBuilder.create(
			getPortletURL()
		).setRedirect(
			() -> {
				PortletURL currentURL = PortletURLUtil.getCurrent(
					_renderRequest, _renderResponse);

				return currentURL.toString();
			}
		).buildString();
	}

	public SearchContainer<?> getSearchContainer() throws Exception {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		if (Objects.equals(getTabs2(), "organizations")) {
			_searchContainer = getOrganizationSearchContainer();
		}
		else if (Objects.equals(getTabs2(), "segments")) {
			_searchContainer = SegmentsEntrySearchContainerFactory.create(
				_renderRequest, _renderResponse);
		}
		else if (Objects.equals(getTabs2(), "sites")) {
			_searchContainer = getGroupSearchContainer();
		}
		else if (Objects.equals(getTabs2(), "user-groups")) {
			_searchContainer = getUserGroupSearchContainer();
		}
		else {
			_searchContainer = getUserSearchContainer();
		}

		return _searchContainer;
	}

	public String getSortingURL() throws PortalException {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildString();
	}

	public String getTabs2() throws PortalException {
		if (Validator.isNull(_tabs2)) {
			_tabs2 = ParamUtil.getString(_httpServletRequest, "tabs2", "users");
		}

		Role role = RoleServiceUtil.fetchRole(
			ParamUtil.getLong(_httpServletRequest, "roleId"));

		if (StringUtil.equals(_tabs2, "segments") &&
			Objects.equals(RoleConstants.ADMINISTRATOR, role.getName())) {

			_tabs2 = "users";
		}

		return _tabs2;
	}

	public SearchContainer<UserGroup> getUserGroupSearchContainer()
		throws PortalException {

		SearchContainer<UserGroup> userGroupSearchContainer =
			new SearchContainer<>(
				_renderRequest, getPortletURL(), null,
				"no-user-groups-were-found");

		userGroupSearchContainer.setOrderByCol(getOrderByCol());
		userGroupSearchContainer.setOrderByComparator(
			UsersAdminUtil.getUserGroupOrderByComparator(
				getOrderByCol(), getOrderByType()));
		userGroupSearchContainer.setOrderByType(getOrderByType());

		if (_tabs3.equals("available")) {
			userGroupSearchContainer.setRowChecker(
				new UserGroupRoleChecker(_renderResponse, _role));
		}
		else {
			userGroupSearchContainer.setRowChecker(
				new EmptyOnClickRowChecker(_renderResponse));
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LinkedHashMap<String, Object> userGroupParams = new LinkedHashMap<>();

		if (_tabs3.equals("current")) {
			userGroupParams.put(
				UserGroupFinderConstants.PARAM_KEY_USER_GROUPS_ROLES,
				Long.valueOf(_role.getRoleId()));
		}

		String keywords = getKeywords();

		if (Validator.isNotNull(keywords)) {
			userGroupParams.put("expandoAttributes", keywords);
		}

		userGroupSearchContainer.setResultsAndTotal(
			() -> UserGroupLocalServiceUtil.search(
				themeDisplay.getCompanyId(), keywords, userGroupParams,
				userGroupSearchContainer.getStart(),
				userGroupSearchContainer.getEnd(),
				userGroupSearchContainer.getOrderByComparator()),
			UserGroupLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), keywords, userGroupParams));

		return userGroupSearchContainer;
	}

	public SearchContainer<User> getUserSearchContainer()
		throws PortalException {

		UserSearch userSearch = new UserSearch(_renderRequest, getPortletURL());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LinkedHashMap<String, Object> userParams = new LinkedHashMap<>();

		if (_tabs3.equals("current")) {
			userParams.put("usersRoles", Long.valueOf(_role.getRoleId()));
		}

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearch.getSearchTerms();

		userSearch.setResultsAndTotal(
			() -> UserLocalServiceUtil.search(
				themeDisplay.getCompanyId(), getKeywords(),
				searchTerms.getStatus(), userParams, userSearch.getStart(),
				userSearch.getEnd(), userSearch.getOrderByComparator()),
			UserLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), getKeywords(),
				searchTerms.getStatus(), userParams));

		if (_tabs3.equals("available")) {
			userSearch.setRowChecker(
				new SetUserRoleChecker(_renderResponse, _role));
		}
		else {
			userSearch.setRowChecker(
				new UnsetUserRoleChecker(_renderResponse, _role));
		}

		return userSearch;
	}

	public List<ViewTypeItem> getViewTypeItems() throws PortalException {
		if (_tabs2.equals("segments")) {
			return null;
		}

		return new ViewTypeItemList(getPortletURL(), _displayStyle) {
			{
				addCardViewTypeItem();
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}

	private final String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final Role _role;
	private SearchContainer<?> _searchContainer;
	private String _tabs2;
	private final String _tabs3;

}