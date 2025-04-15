/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.display.context;

import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.application.list.display.context.logic.PersonalMenuEntryHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Permission;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.web.internal.role.type.contributor.util.RoleTypeContributorRetrieverUtil;
import com.liferay.segments.service.SegmentsEntryRoleLocalServiceUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class RoleDisplayContext {

	public RoleDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;

		_currentRoleTypeContributor =
			RoleTypeContributorRetrieverUtil.getCurrentRoleTypeContributor(
				httpServletRequest);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getActionLabel(String resourceName, String actionId) {
		String actionLabel = null;

		if (actionId.equals(ActionKeys.ACCESS_IN_CONTROL_PANEL)) {
			PanelCategoryHelper panelCategoryHelper =
				(PanelCategoryHelper)_httpServletRequest.getAttribute(
					ApplicationListWebKeys.PANEL_CATEGORY_HELPER);
			PersonalMenuEntryHelper personalMenuEntryHelper =
				(PersonalMenuEntryHelper)_httpServletRequest.getAttribute(
					ApplicationListWebKeys.PERSONAL_MENU_ENTRY_HELPER);

			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				_themeDisplay.getCompanyId(), resourceName);

			if (panelCategoryHelper.containsPortlet(
					portlet.getPortletId(),
					PanelCategoryKeys.SITE_ADMINISTRATION)) {

				actionLabel = LanguageUtil.get(
					_httpServletRequest, "access-in-site-administration");
			}
			else if (panelCategoryHelper.containsPortlet(
						portlet.getPortletId(), PanelCategoryKeys.USER)) {

				actionLabel = LanguageUtil.get(
					_httpServletRequest, "access-in-my-account");
			}
			else if (personalMenuEntryHelper.hasPersonalMenuEntry(
						portlet.getPortletId())) {

				actionLabel = LanguageUtil.get(
					_httpServletRequest, "access-in-personal-menu");
			}
		}

		if (actionId.equals("ADD_STRUCTURE") &&
			resourceName.equals("com.liferay.document.library")) {

			actionLabel = LanguageUtil.get(
				_httpServletRequest, "add-metadata-set");
		}

		if (actionLabel == null) {
			actionLabel = ResourceActionsUtil.getAction(
				_httpServletRequest, actionId);
		}

		return actionLabel;
	}

	public String getAssigneesMessage(Role role) throws Exception {
		if (isAutomaticallyAssigned(role)) {
			return LanguageUtil.get(
				_httpServletRequest, "this-role-is-automatically-assigned");
		}

		int count = getAssigneesTotal(role.getRoleId());

		if (count == 1) {
			return LanguageUtil.get(_httpServletRequest, "one-assignee");
		}

		return LanguageUtil.format(_httpServletRequest, "x-assignees", count);
	}

	public int getAssigneesTotal(long roleId) throws Exception {
		int segmentsEntryRolesCountByRoleId =
			SegmentsEntryRoleLocalServiceUtil.
				getSegmentsEntryRolesCountByRoleId(roleId);

		return RoleLocalServiceUtil.getAssigneesTotal(roleId) +
			segmentsEntryRolesCountByRoleId;
	}

	public List<NavigationItem> getEditRoleNavigationItems() throws Exception {
		List<String> tabsNames = _getTabsNames();
		Map<String, String> tabsURLs = _getTabsURLs();

		String tabs1 = ParamUtil.getString(_httpServletRequest, "tabs1");

		return new NavigationItemList() {
			{
				for (String tabsName : tabsNames) {
					add(
						navigationItem -> {
							navigationItem.setActive(tabsName.equals(tabs1));
							navigationItem.setHref(tabsURLs.get(tabsName));
							navigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, tabsName));
						});
				}
			}
		};
	}

	public String getEditRolePermissionsTabs1() {
		if (isAccountRoleGroupScope()) {
			return "define-group-scope-permissions";
		}

		return "define-permissions";
	}

	public StringBundler getResourceHtmlId(String resource) {
		StringBundler sb = new StringBundler(2);

		sb.append("resource_");
		sb.append(StringUtil.replace(resource, '.', '_'));

		return sb;
	}

	public List<NavigationItem> getRoleAssignmentsNavigationItems(
			PortletURL portletURL)
		throws Exception {

		Role role = _getSelectedRole();

		if (role == null) {
			return Collections.emptyList();
		}

		String tabs2 = ParamUtil.getString(
			_httpServletRequest, "tabs2", "users");

		return new NavigationItemList() {
			{
				for (String assigneeTypeName :
						_getAssigneeTypeNamesByRoleId(portletURL)) {

					if (_isAssigneeTypeVisible(role, assigneeTypeName)) {
						add(
							navigationItem -> {
								navigationItem.setActive(
									assigneeTypeName.equals(tabs2));
								navigationItem.setHref(
									portletURL, "tabs2", assigneeTypeName);
								navigationItem.setLabel(
									LanguageUtil.get(
										_httpServletRequest, assigneeTypeName));
							});
					}
				}
			}
		};
	}

	public List<NavigationItem> getViewRoleNavigationItems(
			LiferayPortletResponse liferayPortletResponse,
			PortletURL portletURL)
		throws Exception {

		NavigationItemList navigationItemList = new NavigationItemList();

		for (RoleTypeContributor roleTypeContributor :
				RoleTypeContributorRetrieverUtil.getRoleTypeContributors(
					_httpServletRequest)) {

			navigationItemList.add(
				navigationItem -> {
					navigationItem.setActive(
						_currentRoleTypeContributor.getType() ==
							roleTypeContributor.getType());

					PortletURL viewRegularRoleNavigationURL =
						PortletURLUtil.clone(
							portletURL, liferayPortletResponse);

					navigationItem.setHref(
						viewRegularRoleNavigationURL, "roleType",
						roleTypeContributor.getType());

					navigationItem.setLabel(
						LanguageUtil.get(
							_httpServletRequest,
							roleTypeContributor.getTabTitle(
								_httpServletRequest.getLocale())));
				});
		}

		return navigationItemList;
	}

	public boolean isAccountRoleGroupScope() {
		if (_accountRoleGroupScope == null) {
			_accountRoleGroupScope = false;

			if ((_currentRoleTypeContributor.getType() ==
					RoleConstants.TYPE_ACCOUNT) &&
				ParamUtil.getBoolean(
					_httpServletRequest, "accountRoleGroupScope")) {

				_accountRoleGroupScope = true;
			}
		}

		return _accountRoleGroupScope;
	}

	public boolean isAllowGroupScope() {
		if (_allowGroupScope == null) {
			_allowGroupScope = false;

			if ((_currentRoleTypeContributor.getType() ==
					RoleConstants.TYPE_REGULAR) ||
				isAccountRoleGroupScope()) {

				_allowGroupScope = true;
			}
		}

		return _allowGroupScope;
	}

	public boolean isAutomaticallyAssigned(Role role) {
		List<RoleTypeContributor> roleTypeContributors =
			RoleTypeContributorRetrieverUtil.getRoleTypeContributors(
				_httpServletRequest);

		for (RoleTypeContributor roleTypeContributor : roleTypeContributors) {
			if (roleTypeContributor.isAutomaticallyAssigned(role)) {
				return true;
			}
		}

		return false;
	}

	public boolean isShowScope(
		Role role, String currentModelResource, String currentPortletResource) {

		boolean showScope = true;

		if (currentPortletResource.equals(PortletKeys.PORTAL)) {
			showScope = false;
		}
		else if (!isAllowGroupScope()) {
			showScope = false;
		}
		else if (Validator.isNotNull(currentPortletResource)) {
			Portlet currentPortlet = PortletLocalServiceUtil.getPortletById(
				role.getCompanyId(), currentPortletResource);

			if (currentPortlet != null) {
				PanelCategoryHelper panelCategoryHelper =
					(PanelCategoryHelper)_httpServletRequest.getAttribute(
						ApplicationListWebKeys.PANEL_CATEGORY_HELPER);

				if (panelCategoryHelper.hasPanelApp(
						currentPortlet.getPortletId()) &&
					!panelCategoryHelper.containsPortlet(
						currentPortlet.getPortletId(),
						PanelCategoryKeys.SITE_ADMINISTRATION)) {

					showScope = false;
				}
			}
		}

		if (Validator.isNotNull(currentModelResource) &&
			currentModelResource.equals(Group.class.getName())) {

			showScope = true;
		}

		return showScope;
	}

	public boolean isValidPermission(Role role, Permission permission) {
		if (role.getType() != RoleConstants.TYPE_ACCOUNT) {
			return true;
		}

		if (isAccountRoleGroupScope()) {
			if ((permission.getScope() == ResourceConstants.SCOPE_COMPANY) ||
				(permission.getScope() == ResourceConstants.SCOPE_GROUP)) {

				return true;
			}
		}
		else if (permission.getScope() ==
					ResourceConstants.SCOPE_GROUP_TEMPLATE) {

			return true;
		}

		return false;
	}

	private String[] _getAssigneeTypeNamesByRoleId(PortletURL portletURL)
		throws Exception {

		String[] assigneeTypeNames = _ASSIGNEE_TYPE_NAMES;

		Map<String, String[]> parameterMap = portletURL.getParameterMap();

		String[] roleIds = parameterMap.get("roleId");

		if (roleIds.length > 0) {
			long roleId = GetterUtil.getLong(roleIds[0]);

			Role role = RoleServiceUtil.fetchRole(roleId);

			if ((role != null) &&
				Objects.equals(RoleConstants.ADMINISTRATOR, role.getName())) {

				assigneeTypeNames = ArrayUtil.filter(
					assigneeTypeNames, name -> !name.equals("segments"));
			}
		}

		return assigneeTypeNames;
	}

	private Role _getSelectedRole() throws Exception {
		if (_role != null) {
			return _role;
		}

		_role = RoleServiceUtil.fetchRole(
			ParamUtil.getLong(_httpServletRequest, "roleId"));

		return _role;
	}

	private List<String> _getTabsNames() throws Exception {
		List<String> tabsNames = new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		Role role = _getSelectedRole();

		if (RolePermissionUtil.contains(
				permissionChecker, role.getRoleId(), ActionKeys.UPDATE)) {

			tabsNames.add("details");
		}

		if (_currentRoleTypeContributor.isAllowDefinePermissions(role) &&
			RolePermissionUtil.contains(
				permissionChecker, role.getRoleId(),
				ActionKeys.DEFINE_PERMISSIONS)) {

			tabsNames.add("define-permissions");

			if (role.getType() == RoleConstants.TYPE_ACCOUNT) {
				tabsNames.add("define-group-scope-permissions");
			}
		}

		if (_currentRoleTypeContributor.isAllowAssignMembers(role) &&
			RolePermissionUtil.contains(
				permissionChecker, role.getRoleId(),
				ActionKeys.ASSIGN_MEMBERS)) {

			tabsNames.add("assignees");
		}

		return tabsNames;
	}

	private Map<String, String> _getTabsURLs() throws Exception {
		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		String backURL = ParamUtil.getString(
			_httpServletRequest, "backURL", redirect);

		Role role = _getSelectedRole();

		return HashMapBuilder.put(
			"assignees",
			() -> PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCPath(
				"/edit_role_assignments.jsp"
			).setRedirect(
				redirect
			).setBackURL(
				backURL
			).setTabs1(
				"assignees"
			).setParameter(
				"roleId", role.getRoleId()
			).buildString()
		).put(
			"define-group-scope-permissions",
			() -> PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCPath(
				"/edit_role_permissions.jsp"
			).setCMD(
				Constants.VIEW
			).setRedirect(
				redirect
			).setBackURL(
				backURL
			).setTabs1(
				"define-group-scope-permissions"
			).setParameter(
				"accountRoleGroupScope", true
			).setParameter(
				"roleId", role.getRoleId()
			).buildString()
		).put(
			"define-permissions",
			() -> PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCPath(
				"/edit_role_permissions.jsp"
			).setCMD(
				Constants.VIEW
			).setRedirect(
				redirect
			).setBackURL(
				backURL
			).setTabs1(
				"define-permissions"
			).setParameter(
				"roleId", role.getRoleId()
			).buildString()
		).put(
			"details",
			() -> PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCPath(
				"/edit_role.jsp"
			).setRedirect(
				redirect
			).setBackURL(
				backURL
			).setTabs1(
				"details"
			).setParameter(
				"roleId", role.getRoleId()
			).buildString()
		).build();
	}

	private boolean _isAssigneeTypeVisible(Role role, String assigneeTypeName) {
		if (StringUtil.equals("segments", assigneeTypeName) &&
			StringUtil.equals(RoleConstants.ADMINISTRATOR, role.getName())) {

			return false;
		}

		return true;
	}

	private static final String[] _ASSIGNEE_TYPE_NAMES = {
		"users", "sites", "organizations", "user-groups", "segments"
	};

	private Boolean _accountRoleGroupScope;
	private Boolean _allowGroupScope;
	private final RoleTypeContributor _currentRoleTypeContributor;
	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;
	private Role _role;
	private final ThemeDisplay _themeDisplay;

}