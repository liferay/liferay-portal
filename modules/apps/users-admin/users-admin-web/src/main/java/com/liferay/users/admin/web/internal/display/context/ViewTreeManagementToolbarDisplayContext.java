/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;
import com.liferay.users.admin.item.selector.UserOrganizationItemSelectorCriterion;
import com.liferay.users.admin.web.internal.search.OrganizationUserChecker;
import com.liferay.users.admin.web.internal.util.comparator.OrganizationUserNameComparator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class ViewTreeManagementToolbarDisplayContext {

	public ViewTreeManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse, Organization organization,
		String displayStyle) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_organization = organization;
		_displayStyle = displayStyle;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_permissionChecker = themeDisplay.getPermissionChecker();
		_themeDisplay = themeDisplay;
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemList.of(
			() -> DropdownItemBuilder.putData(
				"action", "deleteOrganizationsAndUsers"
			).putData(
				"deleteOrganizationsAndUsersURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/users_admin/delete_organizations_and_users"
				).setCMD(
					Constants.DELETE
				).buildString()
			).setIcon(
				"times-circle"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, Constants.DELETE)
			).setQuickAction(
				true
			).build(),
			() -> {
				if (Objects.equals(getNavigation(), "active")) {
					return null;
				}

				return DropdownItemBuilder.putData(
					"action", "activateUsers"
				).putData(
					"activateUsersURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/users_admin/edit_user"
					).setCMD(
						Constants.RESTORE
					).buildString()
				).setIcon(
					"undo"
				).setLabel(
					LanguageUtil.get(_httpServletRequest, Constants.RESTORE)
				).setQuickAction(
					true
				).build();
			},
			() -> {
				if (Objects.equals(getNavigation(), "inactive")) {
					return null;
				}

				return DropdownItemBuilder.putData(
					"action", "deactivateUsers"
				).putData(
					"editUsersURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/users_admin/edit_user"
					).setCMD(
						Constants.DEACTIVATE
					).buildString()
				).setIcon(
					"hidden"
				).setLabel(
					LanguageUtil.get(_httpServletRequest, Constants.DEACTIVATE)
				).setQuickAction(
					true
				).build();
			},
			() -> DropdownItemBuilder.putData(
				"action", "removeOrganizationsAndUsers"
			).putData(
				"removeOrganizationsAndUsersURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/users_admin/edit_organization_assignments"
				).setParameter(
					"assignmentsRedirect", _themeDisplay.getURLCurrent()
				).setParameter(
					"organizationId", _organization.getOrganizationId()
				).buildString()
			).setIcon(
				"minus-circle"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, Constants.REMOVE)
			).setQuickAction(
				true
			).build());
	}

	public List<String> getAvailableActions(Organization organization) {
		return Arrays.asList(
			"deleteOrganizationsAndUsers", "removeOrganizationsAndUsers");
	}

	public List<String> getAvailableActions(User user) {
		List<String> availableActions = new ArrayList<>();

		if (user.isActive()) {
			availableActions.add("deactivateUsers");
		}
		else {
			availableActions.add("activateUsers");
			availableActions.add("deleteOrganizationsAndUsers");
		}

		availableActions.add("removeOrganizationsAndUsers");

		return availableActions;
	}

	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			(String)null
		).buildString();
	}

	public CreationMenu getCreationMenu() throws PortalException {
		PortletURL currentURL = PortletURLUtil.getCurrent(
			_renderRequest, _renderResponse);

		return new CreationMenu() {
			{
				if (hasAddUserPermission()) {
					addDropdownItem(
						dropdownItem -> {
							dropdownItem.setHref(
								_renderResponse.createRenderURL(),
								"mvcRenderCommandName",
								"/users_admin/edit_user", "backURL",
								currentURL.toString(),
								"organizationsSearchContainerPrimaryKeys",
								String.valueOf(
									_organization.getOrganizationId()));
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "new-user"));
						});
				}

				if (hasAddOrganizationPermission()) {
					for (String organizationType :
							OrganizationLocalServiceUtil.getChildrenTypes(
								_organization.getType())) {

						PortletURL addOrganizationTypeURL =
							PortletURLBuilder.createRenderURL(
								_renderResponse
							).setMVCRenderCommandName(
								"/users_admin/edit_organization"
							).setBackURL(
								currentURL.toString()
							).setParameter(
								"parentOrganizationId",
								_organization.getOrganizationId()
							).setParameter(
								"type", organizationType
							).buildPortletURL();

						addDropdownItem(
							dropdownItem -> {
								dropdownItem.setHref(addOrganizationTypeURL);
								dropdownItem.setLabel(
									LanguageUtil.format(
										_httpServletRequest, "new-x",
										organizationType));
							});
					}
				}

				if (OrganizationPermissionUtil.contains(
						_permissionChecker, _organization,
						ActionKeys.ASSIGN_MEMBERS)) {

					addDropdownItem(
						dropdownItem -> {
							dropdownItem.putData("action", "selectUsers");
							dropdownItem.putData(
								"organizationId",
								String.valueOf(
									_organization.getOrganizationId()));
							dropdownItem.putData(
								"selectUsersURL", _getSelectUsersURL());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "assign-users"));
							dropdownItem.setQuickAction(true);
						});
				}
			}
		};
	}

	public List<DropdownItem> getFilterDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterNavigationDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "filter-by-status"));
			}
		).build();
	}

	public List<LabelItem> getFilterLabelItems() {
		String navigation = getNavigation();

		return LabelItemListBuilder.add(
			() -> !navigation.equals("all"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						(String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					String.format(
						"%s: %s",
						LanguageUtil.get(_httpServletRequest, "status"),
						LanguageUtil.get(_httpServletRequest, navigation)));
			}
		).build();
	}

	public String getKeywords() {
		if (_keywords == null) {
			_keywords = ParamUtil.getString(_httpServletRequest, "keywords");
		}

		return _keywords;
	}

	public String getNavigation() {
		if (_navigation == null) {
			_navigation = ParamUtil.getString(
				_renderRequest, "navigation", "all");
		}

		return _navigation;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, UsersAdminPortletKeys.USERS_ADMIN,
			"view-tree-order-by-col", "name");

		return _orderByCol;
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(true);
				dropdownItem.setHref(StringPool.BLANK);
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
			_httpServletRequest, UsersAdminPortletKeys.USERS_ADMIN,
			"view-tree-order-by-type", "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/users_admin/organizations_view_tree"
		).setKeywords(
			() -> {
				String[] keywords = ParamUtil.getStringValues(
					_httpServletRequest, "keywords");

				if (ArrayUtil.isNotEmpty(keywords)) {
					return keywords[keywords.length - 1];
				}

				return null;
			}
		).setNavigation(
			getNavigation()
		).setParameter(
			"displayStyle", _displayStyle
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).setParameter(
			"organizationId", _organization.getOrganizationId()
		).setParameter(
			"screenNavigationCategoryKey",
			UserScreenNavigationEntryConstants.CATEGORY_KEY_ORGANIZATIONS
		).setParameter(
			"usersListView",
			GetterUtil.getString(
				_httpServletRequest.getAttribute("view.jsp-usersListView"))
		).buildPortletURL();
	}

	public String getSearchActionURL() {
		PortletURL searchActionURL = getPortletURL();

		return searchActionURL.toString();
	}

	public SearchContainer<Object> getSearchContainer() throws Exception {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<Object> searchContainer = new SearchContainer(
			_renderRequest,
			PortletURLUtil.getCurrent(_renderRequest, _renderResponse),
			ListUtil.fromString("name,type,status"), "no-results-were-found");

		searchContainer.setOrderByCol(getOrderByCol());

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		searchContainer.setOrderByComparator(
			OrganizationUserNameComparator.getInstance(orderByAsc));
		searchContainer.setOrderByType(getOrderByType());

		int status = WorkflowConstants.STATUS_ANY;

		if (Objects.equals(getNavigation(), "active")) {
			status = WorkflowConstants.STATUS_APPROVED;
		}
		else if (Objects.equals(getNavigation(), "inactive")) {
			status = WorkflowConstants.STATUS_INACTIVE;
		}

		int navigationStatus = status;

		searchContainer.setResultsAndTotal(
			() -> {
				Hits hits =
					OrganizationLocalServiceUtil.searchOrganizationsAndUsers(
						_themeDisplay.getCompanyId(),
						_organization.getOrganizationId(), getKeywords(),
						navigationStatus, null, searchContainer.getStart(),
						searchContainer.getEnd(),
						new Sort[] {
							new Sort(
								"name",
								Objects.equals(
									searchContainer.getOrderByType(), "desc")),
							new Sort(
								"lastName",
								Objects.equals(
									searchContainer.getOrderByType(), "desc"))
						});

				List<Object> results = new ArrayList<>(hits.getLength());

				List<SearchResult> searchResults =
					SearchResultUtil.getSearchResults(
						hits, _themeDisplay.getLocale());

				for (SearchResult searchResult : searchResults) {
					String className = searchResult.getClassName();

					if (className.equals(Organization.class.getName())) {
						results.add(
							OrganizationLocalServiceUtil.fetchOrganization(
								searchResult.getClassPK()));
					}
					else if (className.equals(User.class.getName())) {
						results.add(
							UserLocalServiceUtil.fetchUser(
								searchResult.getClassPK()));
					}
				}

				return results;
			},
			OrganizationLocalServiceUtil.searchOrganizationsAndUsersCount(
				_themeDisplay.getCompanyId(), _organization.getOrganizationId(),
				getKeywords(), navigationStatus, null));

		searchContainer.setRowChecker(
			new OrganizationUserChecker(_renderResponse));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	public String getSortingURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildString();
	}

	public List<ViewTypeItem> getViewTypeItems() {
		return new ViewTypeItemList(getPortletURL(), _displayStyle) {
			{
				addCardViewTypeItem();
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}

	public boolean hasAddOrganizationPermission() {
		return PortalPermissionUtil.contains(
			_permissionChecker, ActionKeys.ADD_ORGANIZATION);
	}

	public boolean hasAddUserPermission() {
		return PortalPermissionUtil.contains(
			_permissionChecker, ActionKeys.ADD_USER);
	}

	public boolean showCreationMenu() throws PortalException {
		if (hasAddOrganizationPermission() || hasAddUserPermission()) {
			return true;
		}

		return false;
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems() {
		DropdownItemList navigationDropdownitems = new DropdownItemList();

		for (String navigation : new String[] {"all", "active", "inactive"}) {
			navigationDropdownitems.add(
				dropdownItem -> {
					dropdownItem.setActive(
						Objects.equals(getNavigation(), navigation));
					dropdownItem.setHref(
						getPortletURL(), "navigation", navigation);
					dropdownItem.setLabel(
						LanguageUtil.get(_httpServletRequest, navigation));
				});
		}

		return navigationDropdownitems;
	}

	private String _getSelectUsersURL() {
		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		UserOrganizationItemSelectorCriterion
			userOrganizationItemSelectorCriterion =
				new UserOrganizationItemSelectorCriterion();

		userOrganizationItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());
		userOrganizationItemSelectorCriterion.setOrganizationId(
			_organization.getOrganizationId());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_renderResponse.getNamespace() + "selectUsers",
				userOrganizationItemSelectorCriterion));
	}

	private final String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private final Organization _organization;
	private final PermissionChecker _permissionChecker;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<Object> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}