/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christian Moura
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/portal_security_audit_configuration/save_audit_configuration"
	},
	service = MVCActionCommand.class
)
public class SaveAuditConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		String portletId = PortalUtil.getPortletId(actionRequest);

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", ParamUtil.getBoolean(actionRequest, "enabled")
			).build();

		if (portletId.equals(ConfigurationAdminPortletKeys.SYSTEM_SETTINGS)) {
			if (!permissionChecker.isOmniadmin()) {
				throw new PrincipalException.MustBeOmniadmin(permissionChecker);
			}

			properties.put(
				"auditMessageMaxQueueSize",
				ParamUtil.getInteger(
					actionRequest, "auditMessageMaxQueueSize",
					_getAuditMessageMaxQueueSize()));

			if (!AuditConfigurationOverrideUtil.isOverridden("enabled")) {
				_configurationProvider.saveSystemConfiguration(
					AuditConfiguration.class, properties);
			}
		}
		else {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			long companyId = themeDisplay.getCompanyId();

			FeatureFlagManagerUtil.checkEnabled(companyId, "LPD-6417");

			if (!permissionChecker.isCompanyAdmin(companyId)) {
				throw new PrincipalException.MustBeCompanyAdmin(
					permissionChecker);
			}

			if (!AuditConfigurationOverrideUtil.isOverridden("enabled")) {
				_configurationProvider.saveCompanyConfiguration(
					AuditConfiguration.class, companyId, properties);
			}
		}

		SessionMessages.add(actionRequest, "requestProcessed");

		sendRedirect(actionRequest, actionResponse);
	}

	@SuppressWarnings("deprecation")
	private int _getAuditMessageMaxQueueSize() throws Exception {
		AuditConfiguration auditConfiguration =
			_configurationProvider.getSystemConfiguration(
				AuditConfiguration.class);

		return auditConfiguration.auditMessageMaxQueueSize();
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}