/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.click.to.chat.web.internal.portlet.action;

import com.liferay.click.to.chat.web.internal.configuration.ClickToChatConfiguration;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author José Abelenda
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"mvc.command.name=/click_to_chat/save_site_configuration"
	},
	service = MVCActionCommand.class
)
public class SaveSiteConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin(themeDisplay.getCompanyId())) {
			SessionErrors.add(actionRequest, PrincipalException.class);

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		_configurationProvider.saveGroupConfiguration(
			ClickToChatConfiguration.class, themeDisplay.getSiteGroupId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"chatProviderAccountId",
				ParamUtil.getString(actionRequest, "chatProviderAccountId")
			).put(
				"chatProviderId",
				ParamUtil.getString(actionRequest, "chatProviderId")
			).put(
				"chatProviderKeyId",
				ParamUtil.getString(actionRequest, "chatProviderKeyId")
			).put(
				"chatProviderSecretKey",
				ParamUtil.getString(actionRequest, "chatProviderSecretKey")
			).put(
				"enabled", ParamUtil.getBoolean(actionRequest, "enabled")
			).put(
				"guestUsersAllowed",
				ParamUtil.getBoolean(actionRequest, "guestUsersAllowed")
			).put(
				"hideInControlPanel",
				ParamUtil.getBoolean(actionRequest, "hideInControlPanel")
			).put(
				"siteSettingsStrategy",
				ParamUtil.getBoolean(actionRequest, "siteSettingsStrategy")
			).build());
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}