/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.search.RoleSearch;
import com.liferay.roles.admin.search.RoleSearchTerms;
import com.liferay.roles.admin.web.internal.role.type.contributor.util.RoleTypeContributorRetrieverUtil;
import com.liferay.roles.admin.web.internal.search.RoleChecker;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class ViewRolesManagementToolbarDisplayContext {

	public ViewRolesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse, String displayStyle) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_displayStyle = displayStyle;

		_currentRoleTypeContributor =
			RoleTypeContributorRetrieverUtil.getCurrentRoleTypeContributor(
				renderRequest);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteRoles");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public CreationMenu getCreationMenu() throws PortalException {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_renderResponse.createRenderURL(), "mvcPath",
					"/edit_role.jsp", "redirect", getPortletURL(), "tabs1",
					"details", "roleType",
					String.valueOf(_currentRoleTypeContributor.getType()));
				dropdownItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest,
						_currentRoleTypeContributor.getTitle(
							_renderRequest.getLocale())));
			}
		).build();
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
			"view-roles-order-by-col", "title");

		return _orderByCol;
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(getOrderByCol(), "title"));
				dropdownItem.setHref(getPortletURL(), "orderByCol", "title");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "title"));
			}
		).build();
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, RolesAdminPortletKeys.ROLES_ADMIN,
			"view-roles-order-by-type", "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view.jsp"
		).setKeywords(
			() -> {
				if (Validator.isNotNull(getKeywords())) {
					return getKeywords();
				}

				return null;
			}
		).setParameter(
			"displayStyle", _displayStyle
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).setParameter(
			"roleType", _currentRoleTypeContributor.getType()
		).buildPortletURL();

		if (_roleSearch != null) {
			portletURL.setParameter(
				_roleSearch.getCurParam(),
				String.valueOf(_roleSearch.getCur()));
			portletURL.setParameter(
				_roleSearch.getDeltaParam(),
				String.valueOf(_roleSearch.getDelta()));
		}

		return portletURL;
	}

	public String getSearchActionURL() {
		PortletURL searchActionURL = getPortletURL();

		return searchActionURL.toString();
	}

	public SearchContainer<Role> getSearchContainer() throws Exception {
		if (_roleSearch != null) {
			return _roleSearch;
		}

		RoleSearch roleSearch = new RoleSearch(_renderRequest, getPortletURL());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		RoleSearchTerms roleSearchTerms =
			(RoleSearchTerms)roleSearch.getSearchTerms();

		roleSearch.setResultsAndTotal(
			_currentRoleTypeContributor.searchRoles(
				themeDisplay.getCompanyId(), roleSearchTerms.getKeywords(),
				roleSearch.getStart(), roleSearch.getEnd(),
				roleSearch.getOrderByComparator()));

		roleSearch.setRowChecker(
			new RoleChecker(_renderRequest, _renderResponse));

		_roleSearch = roleSearch;

		return _roleSearch;
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
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}

	public boolean showCreationMenu() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortalPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), ActionKeys.ADD_ROLE);
	}

	private final RoleTypeContributor _currentRoleTypeContributor;
	private final String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private RoleSearch _roleSearch;

}