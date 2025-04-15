/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.exception.CTStagingEnabledException;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/update_global_publications_configuration"
	},
	service = MVCActionCommand.class
)
public class UpdateGlobalPublicationsConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletURL redirectURL = PortletURLFactoryUtil.create(
			actionRequest, CTPortletKeys.PUBLICATIONS,
			PortletRequest.RENDER_PHASE);

		boolean enablePublications = ParamUtil.getBoolean(
			actionRequest, "enablePublications");
		boolean enableManageRemotely = ParamUtil.getBoolean(
			actionRequest, "enableManageRemotely");
		boolean enableUnapprovedChanges = ParamUtil.getBoolean(
			actionRequest, "enableUnapprovedChanges");

		try {
			PortletPermissionUtil.check(
				themeDisplay.getPermissionChecker(), CTPortletKeys.PUBLICATIONS,
				ActionKeys.CONFIGURATION);

			_ctSettingsConfigurationHelper.save(
				themeDisplay.getCompanyId(),
				HashMapBuilder.<String, Object>put(
					"enabled", enablePublications
				).put(
					"remoteClientId",
					ParamUtil.getString(actionRequest, "clientId")
				).put(
					"remoteClientSecret",
					ParamUtil.getString(actionRequest, "clientSecret")
				).put(
					"remoteEnabled", enableManageRemotely
				).put(
					"sandboxEnabled",
					ParamUtil.getBoolean(actionRequest, "enableSandboxOnly")
				).put(
					"unapprovedChangesAllowed", enableUnapprovedChanges
				).build());
		}
		catch (ConfigurationException configurationException) {
			Throwable throwable = configurationException.getCause();

			if (throwable.getCause() instanceof CTStagingEnabledException) {
				SessionErrors.add(actionRequest, "stagingEnabled");
			}
			else {
				SessionErrors.add(actionRequest, throwable.getClass());
			}

			redirectURL.setParameter(
				"mvcRenderCommandName", "/change_tracking/view_settings");

			sendRedirect(actionRequest, actionResponse, redirectURL.toString());

			return;
		}

		redirectURL.setParameter(
			"mvcRenderCommandName", "/change_tracking/view_settings");

		hideDefaultSuccessMessage(actionRequest);

		SessionMessages.add(
			actionRequest, "requestProcessed",
			_language.get(
				themeDisplay.getLocale(), "the-configuration-has-been-saved"));

		sendRedirect(actionRequest, actionResponse, redirectURL.toString());
	}

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private Language _language;

}