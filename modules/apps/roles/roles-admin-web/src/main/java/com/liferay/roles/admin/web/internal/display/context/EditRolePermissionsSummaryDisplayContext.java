/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.display.context;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Permission;
import com.liferay.portal.kernel.model.PermissionDisplay;
import com.liferay.portal.kernel.model.Resource;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.RolePermissions;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.impl.ResourceImpl;
import com.liferay.portal.security.permission.converter.PermissionConverter;
import com.liferay.roles.admin.web.internal.group.type.contributor.util.GroupTypeContributorUtil;
import com.liferay.taglib.search.ResultRow;

import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mariano Álvaro Sáiz
 */
public class EditRolePermissionsSummaryDisplayContext {

	public EditRolePermissionsSummaryDisplayContext(
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

	public SearchContainer<PermissionDisplay> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer(
			_liferayPortletRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA,
			_getPermissionsAllURL(), _getHeaderNames(),
			"this-role-does-not-have-any-permissions");

		_searchContainer.setResultsAndTotal(
			ListUtil.sort(_getPermissionDisplays()));

		_updateSearchContainerResultRows();

		return _searchContainer;
	}

	private List<String> _getHeaderNames() {
		if (_headerNames != null) {
			return _headerNames;
		}

		_headerNames = new ArrayList<String>() {
			{
				add("permissions");
			}
		};

		if (_roleDisplayContext.isAllowGroupScope()) {
			_headerNames.add("sites-and-asset-libraries");
		}

		_headerNames.add(StringPool.BLANK);

		return _headerNames;
	}

	private List<PermissionDisplay> _getPermissionDisplays()
		throws PortalException {

		if (_permissionDisplays != null) {
			return _permissionDisplays;
		}

		PermissionConverter permissionConverter =
			_permissionConverterSnapshot.get();

		List<Permission> permissions = permissionConverter.convertPermissions(
			_getRole());

		_permissionDisplays = new ArrayList<>(permissions.size());

		for (Permission permission : permissions) {
			if (!_roleDisplayContext.isValidPermission(
					_getRole(), permission)) {

				continue;
			}

			Resource resource = new ResourceImpl() {
				{
					setCompanyId(_themeDisplay.getCompanyId());
					setName(permission.getName());
					setPrimKey(permission.getPrimKey());
					setScope(permission.getScope());
				}
			};

			String currentPortletName = null;
			String currentModelName = StringPool.BLANK;
			String currentModelLabel = StringPool.BLANK;

			if (PortletLocalServiceUtil.hasPortlet(
					_themeDisplay.getCompanyId(), resource.getName())) {

				currentPortletName = resource.getName();
				currentModelLabel = StringPool.BLANK;
			}
			else {
				currentModelName = resource.getName();

				currentModelLabel = ResourceActionsUtil.getModelResource(
					_httpServletRequest, currentModelName);

				List<String> portletResources =
					ResourceActionsUtil.getModelPortletResources(
						currentModelName);

				if (!portletResources.isEmpty()) {
					currentPortletName = portletResources.get(0);
				}
			}

			if (currentPortletName == null) {
				continue;
			}

			String actionId = permission.getActionId();

			PermissionDisplay permissionDisplay = new PermissionDisplay(
				permission, resource, currentPortletName,
				PortalUtil.getPortletLongTitle(
					PortletLocalServiceUtil.getPortletById(
						_themeDisplay.getCompanyId(), currentPortletName),
					_servletContext, _themeDisplay.getLocale()),
				currentModelName, currentModelLabel, actionId,
				_roleDisplayContext.getActionLabel(
					resource.getName(), actionId));

			if (!_permissionDisplays.contains(permissionDisplay)) {
				_permissionDisplays.add(permissionDisplay);
			}
		}

		return _permissionDisplays;
	}

	private PortletURL _getPermissionsAllURL() {
		if (_permissionsAllURL != null) {
			return _permissionsAllURL;
		}

		_permissionsAllURL = PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/edit_role_permissions.jsp"
		).setCMD(
			Constants.VIEW
		).setBackURL(
			ParamUtil.getString(
				_httpServletRequest, "backURL",
				ParamUtil.getString(_httpServletRequest, "redirect"))
		).setTabs1(
			_roleDisplayContext.getEditRolePermissionsTabs1()
		).setTabs2(
			"roles"
		).setParameter(
			"accountRoleGroupScope",
			_roleDisplayContext.isAccountRoleGroupScope()
		).setParameter(
			"roleId",
			() -> {
				Role role = _getRole();

				return role.getRoleId();
			}
		).buildPortletURL();

		return _permissionsAllURL;
	}

	private Role _getRole() {
		if (_role != null) {
			return _role;
		}

		_role = (Role)_httpServletRequest.getAttribute(
			"edit_role_permissions.jsp-role");

		return _role;
	}

	private void _updateSearchContainerResultRows() throws PortalException {
		List<com.liferay.portal.kernel.dao.search.ResultRow> resultRows =
			_searchContainer.getResultRows();

		Role role = _getRole();

		List<PermissionDisplay> results = _searchContainer.getResults();

		for (int i = 0; i < results.size(); i++) {
			PermissionDisplay permissionDisplay = results.get(i);

			Resource resource = permissionDisplay.getResource();

			String currentResource = resource.getName();

			String actionId = permissionDisplay.getActionId();

			List<Group> groups = Collections.emptyList();

			int scope = ResourceConstants.SCOPE_COMPANY;

			if (_roleDisplayContext.isAllowGroupScope()) {
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

				if (groups.isEmpty()) {
					scope = ResourceConstants.SCOPE_COMPANY;
				}
				else {
					scope = ResourceConstants.SCOPE_GROUP;
				}
			}
			else {
				scope = ResourceConstants.SCOPE_GROUP_TEMPLATE;
			}

			Permission permission = permissionDisplay.getPermission();

			String[] primKeys = {permission.getPrimKey()};

			if (scope == ResourceConstants.SCOPE_GROUP) {
				primKeys = new String[groups.size()];

				for (int j = 0; j < groups.size(); j++) {
					Group group = groups.get(j);

					primKeys[j] = String.valueOf(group.getGroupId());
				}
			}

			ResultRow row = new ResultRow(
				new Object[] {permission, role, primKeys}, actionId, i);

			boolean selected =
				ResourcePermissionLocalServiceUtil.hasScopeResourcePermission(
					_themeDisplay.getCompanyId(), currentResource, scope,
					role.getRoleId(), actionId);

			if (!selected) {
				continue;
			}

			String currentPortletName = permissionDisplay.getPortletName();
			String currentPortletLabel = permissionDisplay.getPortletLabel();
			String currentModelLabel = permissionDisplay.getModelLabel();

			ResourceURL editPermissionsResourceURL =
				_liferayPortletResponse.createResourceURL();

			editPermissionsResourceURL.setParameter(
				"mvcPath", "/view_resources.jsp");
			editPermissionsResourceURL.setParameter(
				Constants.CMD, Constants.EDIT);
			editPermissionsResourceURL.setParameter("tabs2", "roles");
			editPermissionsResourceURL.setParameter(
				"roleId", String.valueOf(role.getRoleId()));
			editPermissionsResourceURL.setParameter(
				"redirect", String.valueOf(_getPermissionsAllURL()));
			editPermissionsResourceURL.setParameter(
				"portletResource", currentPortletName);
			editPermissionsResourceURL.setParameter(
				"accountRoleGroupScope",
				String.valueOf(_roleDisplayContext.isAccountRoleGroupScope()));

			PortletURL editPermissionsURL = PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCPath(
				"/edit_role_permissions.jsp"
			).setCMD(
				Constants.EDIT
			).setRedirect(
				_getPermissionsAllURL()
			).setPortletResource(
				currentPortletName
			).setTabs1(
				_roleDisplayContext.getEditRolePermissionsTabs1()
			).setTabs2(
				"roles"
			).setParameter(
				"accountRoleGroupScope",
				_roleDisplayContext.isAccountRoleGroupScope()
			).setParameter(
				"roleId", role.getRoleId()
			).buildPortletURL();

			StringBundler sb = new StringBundler(18);

			sb.append("<a class=\"permission-navigation-link\" ");
			sb.append("data-resource-href=\"");
			sb.append(editPermissionsResourceURL);
			sb.append(StringPool.POUND);
			sb.append(_roleDisplayContext.getResourceHtmlId(currentResource));
			sb.append("\" href=\"");
			sb.append(editPermissionsURL);
			sb.append(StringPool.POUND);
			sb.append(_roleDisplayContext.getResourceHtmlId(currentResource));
			sb.append("\">");
			sb.append(currentPortletLabel);

			if (Validator.isNotNull(currentModelLabel)) {
				sb.append(StringPool.SPACE);
				sb.append(StringPool.GREATER_THAN);
				sb.append(StringPool.SPACE);
				sb.append(currentModelLabel);
			}

			sb.append("</a>: <strong>");
			sb.append(permissionDisplay.getActionLabel());
			sb.append("</strong>");

			row.addText(sb.toString());

			if (scope == ResourceConstants.SCOPE_COMPANY) {
				if (_roleDisplayContext.isShowScope(
						role, currentResource, currentPortletName)) {

					row.addText(
						LanguageUtil.get(
							_httpServletRequest,
							"all-sites-and-asset-libraries"));
				}
				else {
					row.addText(StringPool.BLANK);
				}
			}
			else if (scope == ResourceConstants.SCOPE_GROUP) {
				sb = new StringBundler((groups.size() * 3) - 2);

				for (int j = 0; j < groups.size(); j++) {
					Group group = groups.get(j);

					sb.append(
						HtmlUtil.escape(
							group.getDescriptiveName(
								_themeDisplay.getLocale())));

					if (j < (groups.size() - 1)) {
						sb.append(StringPool.COMMA);
						sb.append(StringPool.SPACE);
					}
				}

				row.addText(sb.toString());
			}

			// Action

			row.addJSP(
				"/permission_action.jsp", "entry-action", _servletContext,
				_httpServletRequest, _httpServletResponse);

			resultRows.add(row);
		}
	}

	private static final Snapshot<PermissionConverter>
		_permissionConverterSnapshot = new Snapshot<>(
			EditRolePermissionsSummaryDisplayContext.class,
			PermissionConverter.class);

	private List<String> _headerNames;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private List<PermissionDisplay> _permissionDisplays;
	private PortletURL _permissionsAllURL;
	private Role _role;
	private final RoleDisplayContext _roleDisplayContext;
	private SearchContainer<PermissionDisplay> _searchContainer;
	private final ServletContext _servletContext;
	private final ThemeDisplay _themeDisplay;

}