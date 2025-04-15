/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.defaultpermissions.configuration.manager.PortalDefaultPermissionsConfigurationManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;
import com.liferay.taglib.util.PortalIncludeUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @author Wilson S. Man
 */
public class InputPermissionsTag extends IncludeTag {

	public static String doTag(
			String formName, String modelName, PageContext pageContext)
		throws Exception {

		return doTag(_PAGE, formName, modelName, false, false, pageContext);
	}

	public static String doTag(
			String page, String formName, String modelName, boolean reverse,
			boolean showAllRoles, PageContext pageContext)
		throws Exception {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		httpServletRequest.setAttribute(
			"liferay-ui:input-permissions:formName", formName);
		httpServletRequest.setAttribute(
			"liferay-ui:input-permissions:groupDefaultActions",
			ResourceActionsUtil.getModelResourceGroupDefaultActions(modelName));
		httpServletRequest.setAttribute(
			"liferay-ui:input-permissions:guestDefaultActions",
			ResourceActionsUtil.getModelResourceGuestDefaultActions(modelName));
		httpServletRequest.setAttribute(
			"liferay-ui:input-permissions:guestUnsupportedActions",
			ResourceActionsUtil.getModelResourceGuestUnsupportedActions(
				modelName));
		httpServletRequest.setAttribute(
			"liferay-ui:input-permissions:modelName", modelName);
		httpServletRequest.setAttribute(
			"liferay-ui:input-permissions:reverse", reverse);
		httpServletRequest.setAttribute(
			"liferay-ui:input-permissions:showAllRoles", showAllRoles);
		httpServletRequest.setAttribute(
			"liferay-ui:input-permissions:supportedActions",
			ResourceActionsUtil.getModelResourceActions(modelName));

		if (showAllRoles) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Map<String, String[]> defaultPermissions =
				PortalDefaultPermissionsConfigurationManagerUtil.
					getDefaultPermissions(
						themeDisplay.getCompanyId(),
						themeDisplay.getSiteGroupId(), modelName);

			if (defaultPermissions == null) {
				defaultPermissions = Collections.emptyMap();
			}

			httpServletRequest.setAttribute(
				"liferay-ui:input-permissions:defaultPermissions",
				defaultPermissions);
			httpServletRequest.setAttribute(
				"liferay-ui:input-permissions:supportedRoles",
				_getSupportedRoles(httpServletRequest, modelName));
		}

		PortalIncludeUtil.include(pageContext, page);

		return StringPool.BLANK;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			doTag(
				getPage(), _formName, _modelName, _reverse, _showAllRoles,
				pageContext);

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
	}

	public String getFormName() {
		return _formName;
	}

	public String getModelName() {
		return _modelName;
	}

	public boolean isReverse() {
		return _reverse;
	}

	public boolean isShowAllRoles() {
		return _showAllRoles;
	}

	public void setFormName(String formName) {
		_formName = formName;
	}

	public void setModelName(String modelName) {
		_modelName = modelName;
	}

	public void setReverse(boolean reverse) {
		_reverse = reverse;
	}

	public void setShowAllRoles(boolean showAllRoles) {
		_showAllRoles = showAllRoles;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	private static int[] _getGroupRoleTypes(
		Group group, int[] defaultRoleTypes) {

		if (group == null) {
			return defaultRoleTypes;
		}
		else if (group.isCompany() || group.isUser() || group.isUserGroup()) {
			return RoleConstants.TYPES_REGULAR;
		}
		else if (group.isOrganization()) {
			return RoleConstants.TYPES_ORGANIZATION_AND_REGULAR_AND_SITE;
		}

		return defaultRoleTypes;
	}

	private static int[] _getRoleTypes(Group group, String modelName) {
		int[] roleTypes = RoleConstants.TYPES_REGULAR_AND_SITE;

		if ((group != null) && group.isDepot()) {
			roleTypes = new int[] {
				RoleConstants.TYPE_DEPOT, RoleConstants.TYPE_REGULAR
			};
		}

		if (ResourceActionsUtil.isPortalModelResource(modelName)) {
			if (Objects.equals(modelName, Organization.class.getName()) ||
				Objects.equals(modelName, User.class.getName())) {

				roleTypes = RoleConstants.TYPES_ORGANIZATION_AND_REGULAR;
			}
			else {
				roleTypes = RoleConstants.TYPES_REGULAR;
			}

			return roleTypes;
		}

		if (group == null) {
			return roleTypes;
		}

		Group parentGroup = null;

		if (group.isLayout()) {
			parentGroup = GroupLocalServiceUtil.fetchGroup(
				group.getParentGroupId());
		}

		if (parentGroup != null) {
			roleTypes = _getGroupRoleTypes(parentGroup, roleTypes);
		}
		else {
			roleTypes = _getGroupRoleTypes(group, roleTypes);
		}

		return roleTypes;
	}

	private static List<Role> _getSupportedRoles(
			HttpServletRequest httpServletRequest, String modelName)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = GroupLocalServiceUtil.getGroup(
			themeDisplay.getScopeGroupId());

		long teamGroupId = group.getGroupId();

		if (group.isLayout()) {
			teamGroupId = group.getParentGroupId();
		}

		return RoleServiceUtil.getGroupRolesAndTeamRoles(
			themeDisplay.getCompanyId(), null,
			ListUtil.fromArray(
				RoleConstants.ADMINISTRATOR, RoleConstants.GUEST,
				RoleConstants.OWNER, RoleConstants.SITE_ADMINISTRATOR,
				RoleConstants.SITE_MEMBER, RoleConstants.SITE_OWNER),
			null, null, _getRoleTypes(group, modelName), 0, teamGroupId,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	private static final String _PAGE =
		"/html/taglib/ui/input_permissions/page.jsp";

	private String _formName = "fm";
	private String _modelName;
	private boolean _reverse;
	private boolean _showAllRoles;

}