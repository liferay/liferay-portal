/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.sitemap.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"mvc.command.name=/site_sitemap/save_group_configuration"
	},
	service = MVCActionCommand.class
)
public class SaveGroupConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isGroupAdmin(themeDisplay.getScopeGroupId())) {
			PrincipalException principalException =
				new PrincipalException.MustBeGroupAdmin(
					permissionChecker.getUserId());

			throw new PortletException(principalException);
		}

		_sitemapConfigurationManager.saveSitemapGroupConfiguration(
			themeDisplay.getScopeGroupId(),
			ParamUtil.getBoolean(actionRequest, "includeCategories"),
			ParamUtil.getBoolean(actionRequest, "includePages"),
			ParamUtil.getBoolean(actionRequest, "includeWebContent"));

		SessionMessages.add(
			actionRequest, "requestProcessed",
			_language.get(
				themeDisplay.getLocale(),
				"your-request-completed-successfully"));

		sendRedirect(actionRequest, actionResponse);
	}

	@Reference
	private Language _language;

	@Reference
	private SitemapConfigurationManager _sitemapConfigurationManager;

}