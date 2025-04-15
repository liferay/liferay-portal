/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.display.context;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.RolePermissions;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.comparator.TemplateHandlerComparator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.util.PortletDisplayTemplateUtil;
import com.liferay.roles.admin.search.ResourceActionRowChecker;
import com.liferay.roles.admin.web.internal.group.type.contributor.util.GroupTypeContributorUtil;
import com.liferay.taglib.search.ResultRow;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mariano Álvaro Sáiz
 */
public class EditRolePermissionsFormDisplayContext {

	public EditRolePermissionsFormDisplayContext(
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

	public Set<String> getRelatedPortletResources() throws PortalException {
		if (_searchContainer != null) {
			return _relatedPortletResources;
		}

		_initSearchContainer();

		return _relatedPortletResources;
	}

	public Role getRole() throws PortalException {
		if (_role != null) {
			return _role;
		}

		long roleId = ParamUtil.getLong(_httpServletRequest, "roleId");

		_role = RoleServiceUtil.fetchRole(roleId);

		return _role;
	}

	public SearchContainer<?> getSearchContainer() throws PortalException {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		_initSearchContainer();

		return _searchContainer;
	}

	private void _initSearchContainer() throws PortalException {
		_searchContainer = new SearchContainer(
			_liferayPortletRequest, _liferayPortletResponse.createRenderURL(),
			new ArrayList<String>() {
				{
					add("permissions");
					add("sites");
				}
			},
			"there-are-no-applications-that-support-widget-templates");

		_searchContainer.setRowChecker(
			new ResourceActionRowChecker(_liferayPortletResponse));

		_updateSearchContainerResultRows();
	}

	private void _updateSearchContainerResultRows() throws PortalException {
		if (_relatedPortletResources != null) {
			return;
		}

		List<com.liferay.portal.kernel.dao.search.ResultRow> resultRows =
			_searchContainer.getResultRows();

		List<TemplateHandler> templateHandlers =
			PortletDisplayTemplateUtil.getPortletDisplayTemplateHandlers();

		ListUtil.sort(
			templateHandlers,
			new TemplateHandlerComparator(_themeDisplay.getLocale()));

		_relatedPortletResources = new HashSet<>();

		for (TemplateHandler templateHandler : templateHandlers) {
			String resource = templateHandler.getResourceName();

			Portlet curPortlet = PortletLocalServiceUtil.getPortletById(
				_themeDisplay.getCompanyId(), resource);

			if (curPortlet.isSystem()) {
				continue;
			}

			List<Group> groups = Collections.emptyList();

			String target = resource + ActionKeys.ADD_PORTLET_DISPLAY_TEMPLATE;

			long[] groupIdsArray = StringUtil.split(
				ParamUtil.getString(
					_httpServletRequest, "groupIds" + target, null),
				0L);

			List<String> groupNames = new ArrayList<>();
			Role role = getRole();
			int scope = ResourceConstants.SCOPE_COMPANY;

			if (_roleDisplayContext.isAllowGroupScope()) {
				groups = GroupLocalServiceUtil.search(
					_themeDisplay.getCompanyId(),
					GroupTypeContributorUtil.getClassNameIds(), null, null,
					LinkedHashMapBuilder.<String, Object>put(
						"rolePermissions",
						new RolePermissions(
							resource, ResourceConstants.SCOPE_GROUP,
							ActionKeys.ADD_PORTLET_DISPLAY_TEMPLATE,
							role.getRoleId())
					).build(),
					true, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

				groupIdsArray = new long[groups.size()];

				for (int i = 0; i < groups.size(); i++) {
					Group group = groups.get(i);

					groupIdsArray[i] = group.getGroupId();

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
					role, ActionKeys.ADD_PORTLET_DISPLAY_TEMPLATE, resource,
					target, scope, true, groups, groupIdsArray, groupNames,
					curPortlet.getPortletId()
				},
				target, _relatedPortletResources.size());

			_relatedPortletResources.add(curPortlet.getPortletId());

			row.addText(
				StringBundler.concat(
					PortalUtil.getPortletLongTitle(
						curPortlet, _servletContext, _themeDisplay.getLocale()),
					": ",
					_roleDisplayContext.getActionLabel(
						resource, ActionKeys.ADD_PORTLET_DISPLAY_TEMPLATE)));

			row.addJSP(
				"/edit_role_permissions_resource_scope.jsp", _servletContext,
				_httpServletRequest, _httpServletResponse);

			resultRows.add(row);
		}

		_searchContainer.setResultsAndTotal(
			Collections::emptyList, _relatedPortletResources.size());
	}

	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private Set<String> _relatedPortletResources;
	private Role _role;
	private final RoleDisplayContext _roleDisplayContext;
	private SearchContainer<?> _searchContainer;
	private final ServletContext _servletContext;
	private final ThemeDisplay _themeDisplay;

}