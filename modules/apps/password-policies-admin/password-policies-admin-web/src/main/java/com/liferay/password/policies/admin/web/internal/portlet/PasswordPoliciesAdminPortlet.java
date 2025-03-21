/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.web.internal.portlet;

import com.liferay.password.policies.admin.constants.PasswordPoliciesAdminPortletKeys;
import com.liferay.password.policies.admin.web.internal.configuration.PasswordPoliciesConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.DuplicatePasswordPolicyException;
import com.liferay.portal.kernel.exception.NoSuchPasswordPolicyException;
import com.liferay.portal.kernel.exception.PasswordPolicyNameException;
import com.liferay.portal.kernel.exception.RequiredPasswordPolicyException;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.PasswordPolicyService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Scott Lee
 * @author Drew Brokke
 */
@Component(
	configurationPid = "com.liferay.password.policies.admin.web.internal.configuration.PasswordPoliciesConfiguration",
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-users-admin",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.icon=/icons/password_policies_admin.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + PasswordPoliciesAdminPortletKeys.PASSWORD_POLICIES_ADMIN,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class PasswordPoliciesAdminPortlet extends MVCPortlet {

	public void deletePasswordPolicies(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] passwordPolicyIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "passwordPolicyIds"), 0L);

		for (long passwordPolicyId : passwordPolicyIds) {
			_passwordPolicyService.deletePasswordPolicy(passwordPolicyId);
		}
	}

	public void deletePasswordPolicy(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long passwordPolicyId = ParamUtil.getLong(
			actionRequest, "passwordPolicyId");

		_passwordPolicyService.deletePasswordPolicy(passwordPolicyId);
	}

	public void editPasswordPolicy(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long passwordPolicyId = ParamUtil.getLong(
			actionRequest, "passwordPolicyId");

		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");
		boolean changeable = ParamUtil.getBoolean(actionRequest, "changeable");

		boolean changeRequired = false;
		long minAge = 0;

		if (changeable) {
			changeRequired = ParamUtil.getBoolean(
				actionRequest, "changeRequired");
			minAge = ParamUtil.getLong(actionRequest, "minAge");
		}

		boolean checkSyntax = ParamUtil.getBoolean(
			actionRequest, "checkSyntax");
		boolean allowDictionaryWords = ParamUtil.getBoolean(
			actionRequest, "allowDictionaryWords");
		int minAlphanumeric = ParamUtil.getInteger(
			actionRequest, "minAlphanumeric");
		int minLength = ParamUtil.getInteger(actionRequest, "minLength");
		int minLowerCase = ParamUtil.getInteger(actionRequest, "minLowerCase");
		int minNumbers = ParamUtil.getInteger(actionRequest, "minNumbers");
		int minSymbols = ParamUtil.getInteger(actionRequest, "minSymbols");
		int minUpperCase = ParamUtil.getInteger(actionRequest, "minUpperCase");
		String regex = ParamUtil.getString(actionRequest, "regex");
		boolean history = ParamUtil.getBoolean(actionRequest, "history");
		int historyCount = ParamUtil.getInteger(actionRequest, "historyCount");
		boolean expireable = ParamUtil.getBoolean(actionRequest, "expireable");
		long maxAge = ParamUtil.getLong(actionRequest, "maxAge");
		long warningTime = ParamUtil.getLong(actionRequest, "warningTime");
		int graceLimit = ParamUtil.getInteger(actionRequest, "graceLimit");
		boolean lockout = ParamUtil.getBoolean(actionRequest, "lockout");
		int maxFailure = ParamUtil.getInteger(actionRequest, "maxFailure");
		long lockoutDuration = ParamUtil.getLong(
			actionRequest, "lockoutDuration");
		long resetFailureCount = ParamUtil.getLong(
			actionRequest, "resetFailureCount");
		long resetTicketMaxAge = ParamUtil.getLong(
			actionRequest, "resetTicketMaxAge");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			PasswordPolicy.class.getName(), actionRequest);

		if (passwordPolicyId <= 0) {

			// Add password policy

			PasswordPolicy passwordPolicy =
				_passwordPolicyService.addPasswordPolicy(
					name, description, changeable, changeRequired, minAge,
					checkSyntax, allowDictionaryWords, minAlphanumeric,
					minLength, minLowerCase, minNumbers, minSymbols,
					minUpperCase, regex, history, historyCount, expireable,
					maxAge, warningTime, graceLimit, lockout, maxFailure,
					lockoutDuration, resetFailureCount, resetTicketMaxAge,
					serviceContext);

			passwordPolicyId = passwordPolicy.getPasswordPolicyId();
		}
		else {

			// Update password policy

			_passwordPolicyService.updatePasswordPolicy(
				passwordPolicyId, name, description, changeable, changeRequired,
				minAge, checkSyntax, allowDictionaryWords, minAlphanumeric,
				minLength, minLowerCase, minNumbers, minSymbols, minUpperCase,
				regex, history, historyCount, expireable, maxAge, warningTime,
				graceLimit, lockout, maxFailure, lockoutDuration,
				resetFailureCount, resetTicketMaxAge, serviceContext);
		}

		String redirect = _portal.escapeRedirect(
			ParamUtil.getString(actionRequest, "redirect"));

		if (Validator.isNotNull(redirect)) {
			redirect = HttpComponentsUtil.setParameter(
				redirect, actionResponse.getNamespace() + "passwordPolicyId",
				passwordPolicyId);

			actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
		}
	}

	public void editPasswordPolicyAssignments(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long passwordPolicyId = ParamUtil.getLong(
			actionRequest, "passwordPolicyId");

		long[] addUserIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "addUserIds"), 0L);
		long[] removeUserIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "removeUserIds"), 0L);
		long[] addOrganizationIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "addOrganizationIds"), 0L);
		long[] removeOrganizationIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "removeOrganizationIds"), 0L);

		if (ArrayUtil.isNotEmpty(addUserIds)) {
			_userService.addPasswordPolicyUsers(passwordPolicyId, addUserIds);
		}

		if (ArrayUtil.isNotEmpty(removeUserIds)) {
			_userService.unsetPasswordPolicyUsers(
				passwordPolicyId, removeUserIds);
		}

		if (ArrayUtil.isNotEmpty(addOrganizationIds)) {
			_organizationService.addPasswordPolicyOrganizations(
				passwordPolicyId, addOrganizationIds);
		}

		if (ArrayUtil.isNotEmpty(removeOrganizationIds)) {
			_organizationService.unsetPasswordPolicyOrganizations(
				passwordPolicyId, removeOrganizationIds);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_passwordPoliciesConfiguration = ConfigurableUtil.createConfigurable(
			PasswordPoliciesConfiguration.class, properties);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			PasswordPoliciesConfiguration.class.getName(),
			_passwordPoliciesConfiguration);

		if (SessionErrors.contains(
				renderRequest, NoSuchPasswordPolicyException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/error.jsp", renderRequest, renderResponse);
		}
		else if (SessionErrors.contains(
					renderRequest,
					DuplicatePasswordPolicyException.class.getName()) ||
				 SessionErrors.contains(
					 renderRequest,
					 PasswordPolicyNameException.class.getName())) {

			include("/edit_password_policy.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof DuplicatePasswordPolicyException ||
			throwable instanceof NoSuchPasswordPolicyException ||
			throwable instanceof PasswordPolicyNameException ||
			throwable instanceof PrincipalException ||
			throwable instanceof RequiredPasswordPolicyException) {

			return true;
		}

		return false;
	}

	@Reference
	private OrganizationService _organizationService;

	private volatile PasswordPoliciesConfiguration
		_passwordPoliciesConfiguration;

	@Reference
	private PasswordPolicyService _passwordPolicyService;

	@Reference
	private Portal _portal;

	@Reference
	private UserService _userService;

}