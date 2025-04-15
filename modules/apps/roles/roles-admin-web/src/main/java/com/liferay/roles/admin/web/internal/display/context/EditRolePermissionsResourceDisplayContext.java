/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.display.context;

import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.application.list.display.context.logic.PersonalMenuEntryHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.RolePermissions;
import com.liferay.portal.kernel.security.permission.comparator.ActionComparator;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.WebAppPool;
import com.liferay.roles.admin.search.ResourceActionRowChecker;
import com.liferay.roles.admin.web.internal.group.type.contributor.util.GroupTypeContributorUtil;
import com.liferay.taglib.search.ResultRow;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Mariano Álvaro Sáiz
 */
public class EditRolePermissionsResourceDisplayContext {

	public EditRolePermissionsResourceDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		RoleDisplayContext roleDisplayContext, ServletContext servletContext) {

		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_roleDisplayContext = roleDisplayContext;
		_servletContext = servletContext;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SearchContainer<String> getSearchContainer() throws PortalException {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer(
			_liferayPortletRequest, _liferayPortletResponse.createRenderURL(),
			_getHeaderNames(), "there-are-no-actions");

		List<String> results = _getSortedResourceActions();

		_searchContainer.setResultsAndTotal(() -> results, results.size());

		_searchContainer.setRowChecker(
			new ResourceActionRowChecker(_liferayPortletResponse));

		_updateSearchContainerResultRows();

		return _searchContainer;
	}

	private String _getCurrentModelResource() {
		if (_currentModelResource != null) {
			return _currentModelResource;
		}

		_currentModelResource = (String)_httpServletRequest.getAttribute(
			"edit_role_permissions.jsp-curModelResource");

		return _currentModelResource;
	}

	private Portlet _getCurrentPortlet() {
		if ((_currentPortletId != null) || (_currentPortlet != null)) {
			return _currentPortlet;
		}

		if (Validator.isNotNull(_getCurrentPortletResource())) {
			_currentPortlet = PortletLocalServiceUtil.getPortletById(
				_themeDisplay.getCompanyId(), _getCurrentPortletResource());
		}

		return _currentPortlet;
	}

	private String _getCurrentPortletId() {
		if (_currentPortletId != null) {
			return _currentPortletId;
		}

		_currentPortletId = StringPool.BLANK;

		if (Validator.isNotNull(_getCurrentPortletResource())) {
			_currentPortlet = PortletLocalServiceUtil.getPortletById(
				_themeDisplay.getCompanyId(), _getCurrentPortletResource());

			_currentPortletId = _currentPortlet.getPortletId();
		}

		return _currentPortletId;
	}

	private String _getCurrentPortletResource() {
		if (_currentPortletResource != null) {
			return _currentPortletResource;
		}

		_currentPortletResource = (String)_httpServletRequest.getAttribute(
			"edit_role_permissions.jsp-curPortletResource");

		return _currentPortletResource;
	}

	private List<String> _getGuestUnsupportedActions() {
		if (_guestUnsupportedActions != null) {
			return _guestUnsupportedActions;
		}

		_guestUnsupportedActions =
			ResourceActionsUtil.getResourceGuestUnsupportedActions(
				_getCurrentPortletResource(), _getCurrentModelResource());

		return _guestUnsupportedActions;
	}

	private List<String> _getHeaderNames() {
		if (_headerNames != null) {
			return _headerNames;
		}

		_headerNames = new ArrayList<String>() {
			{
				add("action");
			}
		};

		if (_roleDisplayContext.isShowScope(
				_getRole(), _getCurrentModelResource(),
				_getCurrentPortletId())) {

			_headerNames.add("scope");
		}

		return _headerNames;
	}

	private String _getPortletResource() {
		if (_portletResource != null) {
			return _portletResource;
		}

		_portletResource = (String)_httpServletRequest.getAttribute(
			"edit_role_permissions.jsp-portletResource");

		return _portletResource;
	}

	private Role _getRole() {
		if (_role != null) {
			return _role;
		}

		_role = (Role)_httpServletRequest.getAttribute(
			"edit_role_permissions.jsp-role");

		return _role;
	}

	private List<String> _getSortedResourceActions() {
		if (_sortedResourceActions != null) {
			return _sortedResourceActions;
		}

		_sortedResourceActions = ListUtil.sort(
			ResourceActionsUtil.getResourceActions(
				_getCurrentPortletResource(), _getCurrentModelResource()),
			new ActionComparator(_themeDisplay.getLocale()));

		return _sortedResourceActions;
	}

	private boolean _hasHiddenPortletCategory(Portlet portlet) {
		PortletCategory portletCategory = (PortletCategory)WebAppPool.get(
			portlet.getCompanyId(), WebKeys.PORTLET_CATEGORY);

		PortletCategory hiddenPortletCategory = portletCategory.getCategory(
			"category.hidden");

		Set<String> portletIds = hiddenPortletCategory.getPortletIds();

		return portletIds.contains(portlet.getPortletId());
	}

	private void _updateSearchContainerResultRows() throws PortalException {
		List<String> guestUnsupportedActions = _getGuestUnsupportedActions();
		List<com.liferay.portal.kernel.dao.search.ResultRow> resultRows =
			_searchContainer.getResultRows();

		Role role = _getRole();

		boolean roleGuest = Objects.equals(role.getName(), RoleConstants.GUEST);

		List<String> results = _getSortedResourceActions();

		for (int i = 0; i < results.size(); i++) {
			String actionId = results.get(i);

			if (roleGuest && guestUnsupportedActions.contains(actionId)) {
				continue;
			}

			PanelCategoryHelper panelCategoryHelper =
				(PanelCategoryHelper)_httpServletRequest.getAttribute(
					ApplicationListWebKeys.PANEL_CATEGORY_HELPER);

			if (Validator.isNotNull(_getCurrentPortletResource())) {
				PersonalMenuEntryHelper personalMenuEntryHelper =
					(PersonalMenuEntryHelper)_httpServletRequest.getAttribute(
						ApplicationListWebKeys.PERSONAL_MENU_ENTRY_HELPER);

				if (actionId.equals(ActionKeys.ACCESS_IN_CONTROL_PANEL) &&
					!panelCategoryHelper.hasPanelApp(_getCurrentPortletId()) &&
					!personalMenuEntryHelper.hasPersonalMenuEntry(
						_getCurrentPortletId())) {

					continue;
				}

				if (actionId.equals(ActionKeys.ADD_TO_PAGE) &&
					_hasHiddenPortletCategory(_getCurrentPortlet())) {

					continue;
				}
			}

			String currentResource = null;

			if (Validator.isNull(_getCurrentModelResource())) {
				currentResource = _getCurrentPortletResource();
			}
			else {
				currentResource = _getCurrentModelResource();
			}

			String target = currentResource + actionId;

			long[] groupIdsArray = StringUtil.split(
				ParamUtil.getString(
					_httpServletRequest, "groupIds" + target, null),
				0L);

			int scope = ResourceConstants.SCOPE_COMPANY;
			boolean supportsFilterByGroup = false;
			List<Group> groups = Collections.emptyList();

			List<String> groupNames = new ArrayList<>();

			if (_roleDisplayContext.isAllowGroupScope()) {
				if (Validator.isNotNull(_getPortletResource())) {
					Portlet portlet = PortletLocalServiceUtil.getPortletById(
						_themeDisplay.getCompanyId(), _getPortletResource());

					if ((portlet != null) &&
						panelCategoryHelper.containsPortlet(
							portlet.getPortletId(),
							PanelCategoryKeys.SITE_ADMINISTRATION)) {

						supportsFilterByGroup = true;
					}
				}

				if (!supportsFilterByGroup &&
					!ResourceActionsUtil.isPortalModelResource(
						currentResource) &&
					!Objects.equals(
						_getPortletResource(), PortletKeys.PORTAL)) {

					supportsFilterByGroup = true;
				}

				groups = GroupLocalServiceUtil.search(
					_themeDisplay.getCompanyId(),
					GroupTypeContributorUtil.getClassNameIds(), null, null,
					LinkedHashMapBuilder.<String, Object>put(
						"rolePermissions",
						new RolePermissions(
							currentResource, ResourceConstants.SCOPE_GROUP,
							actionId, role.getRoleId())
					).build(),
					true, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

				groupIdsArray = new long[groups.size()];

				for (int j = 0; j < groups.size(); j++) {
					Group group = groups.get(j);

					groupIdsArray[j] = group.getGroupId();

					groupNames.add(
						HtmlUtil.escape(
							group.getDescriptiveName(
								_themeDisplay.getLocale())));
				}

				if (!groups.isEmpty()) {
					scope = ResourceConstants.SCOPE_GROUP;
				}
			}
			else {
				scope = ResourceConstants.SCOPE_GROUP_TEMPLATE;
			}

			ResultRow row = new ResultRow(
				new Object[] {
					role, actionId, currentResource, target, scope,
					supportsFilterByGroup, groups, groupIdsArray, groupNames,
					_getCurrentPortletId()
				},
				target, i);

			row.addText(
				_roleDisplayContext.getActionLabel(currentResource, actionId));

			if (_roleDisplayContext.isShowScope(
					_getRole(), _getCurrentModelResource(),
					_getCurrentPortletId())) {

				row.addJSP(
					"/edit_role_permissions_resource_scope.jsp",
					_servletContext, _httpServletRequest, _httpServletResponse);
			}

			resultRows.add(row);
		}
	}

	private String _currentModelResource;
	private Portlet _currentPortlet;
	private String _currentPortletId;
	private String _currentPortletResource;
	private List<String> _guestUnsupportedActions;
	private List<String> _headerNames;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _portletResource;
	private Role _role;
	private final RoleDisplayContext _roleDisplayContext;
	private SearchContainer<String> _searchContainer;
	private final ServletContext _servletContext;
	private List<String> _sortedResourceActions;
	private final ThemeDisplay _themeDisplay;

}