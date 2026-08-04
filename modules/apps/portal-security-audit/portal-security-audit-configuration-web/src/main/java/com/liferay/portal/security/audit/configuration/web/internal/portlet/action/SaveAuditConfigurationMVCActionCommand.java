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
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;

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

		if (portletId.equals(ConfigurationAdminPortletKeys.SYSTEM_SETTINGS)) {
			if (!permissionChecker.isOmniadmin()) {
				throw new PrincipalException.MustBeOmniadmin(permissionChecker);
			}

			if (!AuditConfigurationOverrideUtil.isOverridden(
					AuditConfiguration.class, "enabled")) {

				AuditConfiguration auditConfiguration =
					_configurationProvider.getSystemConfiguration(
						AuditConfiguration.class);

				Dictionary<String, Object> properties =
					_getAuditConfigurationProperties(
						actionRequest, auditConfiguration);

				properties.put(
					"auditMessageMaxQueueSize",
					ParamUtil.getInteger(
						actionRequest, "auditMessageMaxQueueSize",
						_getAuditMessageMaxQueueSize(auditConfiguration)));

				_configurationProvider.saveSystemConfiguration(
					AuditConfiguration.class, properties);
			}

			Dictionary<String, Object>
				persistentAuditMessageProcessorConfigurationProperties =
					_getPersistentAuditMessageProcessorConfigurationProperties(
						actionRequest,
						_configurationProvider.getSystemConfiguration(
							PersistentAuditMessageProcessorConfiguration.
								class));

			if (!persistentAuditMessageProcessorConfigurationProperties.
					isEmpty()) {

				_configurationProvider.saveSystemConfiguration(
					PersistentAuditMessageProcessorConfiguration.class,
					persistentAuditMessageProcessorConfigurationProperties);
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

			if (!AuditConfigurationOverrideUtil.isOverridden(
					AuditConfiguration.class, "enabled")) {

				_configurationProvider.saveCompanyConfiguration(
					AuditConfiguration.class, companyId,
					_getAuditConfigurationProperties(
						actionRequest,
						_configurationProvider.getCompanyConfiguration(
							AuditConfiguration.class, companyId)));
			}

			Dictionary<String, Object>
				persistentAuditMessageProcessorConfigurationProperties =
					_getPersistentAuditMessageProcessorConfigurationProperties(
						actionRequest,
						_configurationProvider.getCompanyConfiguration(
							PersistentAuditMessageProcessorConfiguration.class,
							companyId));

			if (!persistentAuditMessageProcessorConfigurationProperties.
					isEmpty()) {

				_configurationProvider.saveCompanyConfiguration(
					PersistentAuditMessageProcessorConfiguration.class,
					companyId,
					persistentAuditMessageProcessorConfigurationProperties);
			}
		}

		SessionMessages.add(actionRequest, "requestProcessed");

		sendRedirect(actionRequest, actionResponse);
	}

	private Dictionary<String, Object> _getAuditConfigurationProperties(
		ActionRequest actionRequest, AuditConfiguration auditConfiguration) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"enabled",
			ParamUtil.getBoolean(
				actionRequest, "enabled", auditConfiguration.enabled())
		).build();
	}

	@SuppressWarnings("deprecation")
	private int _getAuditMessageMaxQueueSize(
		AuditConfiguration auditConfiguration) {

		return auditConfiguration.auditMessageMaxQueueSize();
	}

	private Dictionary<String, Object>
		_getPersistentAuditMessageProcessorConfigurationProperties(
			ActionRequest actionRequest,
			PersistentAuditMessageProcessorConfiguration
				persistentAuditMessageProcessorConfiguration) {

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		if (!AuditConfigurationOverrideUtil.isOverridden(
				PersistentAuditMessageProcessorConfiguration.class,
				"bufferSize")) {

			properties.put(
				"bufferSize",
				ParamUtil.getInteger(
					actionRequest, "persistentAuditMessageProcessorBufferSize",
					persistentAuditMessageProcessorConfiguration.bufferSize()));
		}

		if (!AuditConfigurationOverrideUtil.isOverridden(
				PersistentAuditMessageProcessorConfiguration.class,
				"enabled")) {

			properties.put(
				"enabled",
				ParamUtil.getBoolean(
					actionRequest, "persistentAuditMessageProcessorEnabled",
					persistentAuditMessageProcessorConfiguration.enabled()));
		}

		if (!AuditConfigurationOverrideUtil.isOverridden(
				PersistentAuditMessageProcessorConfiguration.class,
				"flushInterval")) {

			properties.put(
				"flushInterval",
				ParamUtil.getLong(
					actionRequest,
					"persistentAuditMessageProcessorFlushInterval",
					persistentAuditMessageProcessorConfiguration.
						flushInterval()));
		}

		return properties;
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}