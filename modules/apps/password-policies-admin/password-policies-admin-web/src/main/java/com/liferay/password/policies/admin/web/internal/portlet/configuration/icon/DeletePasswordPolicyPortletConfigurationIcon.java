/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.web.internal.portlet.configuration.icon;

import com.liferay.password.policies.admin.constants.PasswordPoliciesAdminPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.portlet.configuration.icon.BaseJSPPortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.permission.PasswordPolicyPermissionUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PasswordPoliciesAdminPortletKeys.PASSWORD_POLICIES_ADMIN,
		"path=/edit_password_policy.jsp",
		"path=/edit_password_policy_assignments.jsp"
	},
	service = PortletConfigurationIcon.class
)
public class DeletePasswordPolicyPortletConfigurationIcon
	extends BaseJSPPortletConfigurationIcon {

	@Override
	public Map<String, Object> getContext(PortletRequest portletRequest) {
		return HashMapBuilder.<String, Object>put(
			"action", getNamespace(portletRequest) + "deletePasswordPolicy"
		).put(
			"globalAction", true
		).build();
	}

	@Override
	public String getJspPath() {
		return "/configuration/icon/delete_password_policy.jsp";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "delete");
	}

	@Override
	public double getWeight() {
		return 101;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long passwordPolicyId = _getPasswordPolicyId(portletRequest);

		PasswordPolicy passwordPolicy =
			_passwordPolicyLocalService.fetchPasswordPolicy(passwordPolicyId);

		if ((passwordPolicy != null) && !passwordPolicy.isDefaultPolicy() &&
			PasswordPolicyPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), passwordPolicyId,
				ActionKeys.DELETE)) {

			return true;
		}

		return false;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	private long _getPasswordPolicyId(PortletRequest portletRequest) {
		return ParamUtil.getLong(
			_portal.getHttpServletRequest(portletRequest), "passwordPolicyId");
	}

	@Reference
	private Language _language;

	@Reference
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.password.policies.admin.web)"
	)
	private ServletContext _servletContext;

}