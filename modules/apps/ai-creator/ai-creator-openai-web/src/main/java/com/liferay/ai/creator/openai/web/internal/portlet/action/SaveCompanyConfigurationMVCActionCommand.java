/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.portlet.action;

import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.PortletException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/instance_settings/save_company_configuration"
	},
	service = MVCActionCommand.class
)
public class SaveCompanyConfigurationMVCActionCommand
	extends BaseSaveConfigurationMVCActionCommand {

	@Override
	protected void checkPermission(ThemeDisplay themeDisplay)
		throws PortletException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin(themeDisplay.getCompanyId())) {
			PrincipalException principalException =
				new PrincipalException.MustBeCompanyAdmin(
					permissionChecker.getUserId());

			throw new PortletException(principalException);
		}
	}

	@Override
	protected void saveAICreatorOpenAIConfiguration(
			String apiKey, boolean enableChatGPT, boolean enableDALLE,
			ThemeDisplay themeDisplay)
		throws ConfigurationException {

		_aiCreatorOpenAIConfigurationManager.
			saveAICreatorOpenAICompanyConfiguration(
				themeDisplay.getCompanyId(), apiKey, enableChatGPT,
				enableDALLE);
	}

	@Reference
	private AICreatorOpenAIConfigurationManager
		_aiCreatorOpenAIConfigurationManager;

}