/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.display.context;

import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class AICreatorOpenAIGroupConfigurationDisplayContext
	extends BaseAICreatorOpenAIConfigurationDisplayContext {

	public AICreatorOpenAIGroupConfigurationDisplayContext(
		AICreatorOpenAIConfigurationManager aiCreatorOpenAIConfigurationManager,
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest);

		_aiCreatorOpenAIConfigurationManager =
			aiCreatorOpenAIConfigurationManager;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public boolean isCompanyChatGPTEnabled() throws ConfigurationException {
		return _aiCreatorOpenAIConfigurationManager.
			isAICreatorChatGPTCompanyEnabled(_themeDisplay.getCompanyId());
	}

	public boolean isCompanyDALLEEnabled() throws ConfigurationException {
		return _aiCreatorOpenAIConfigurationManager.
			isAICreatorDALLECompanyEnabled(_themeDisplay.getCompanyId());
	}

	@Override
	protected String getAICreatorOpenAIAPIKey() throws ConfigurationException {
		return _aiCreatorOpenAIConfigurationManager.
			getAICreatorOpenAIGroupAPIKey(_themeDisplay.getScopeGroupId());
	}

	@Override
	protected boolean isAICreatorChatGPTEnabled()
		throws ConfigurationException {

		return _aiCreatorOpenAIConfigurationManager.
			isAICreatorChatGPTGroupEnabled(
				_themeDisplay.getCompanyId(), _themeDisplay.getScopeGroupId());
	}

	@Override
	protected boolean isAICreatorDALLEEnabled() throws ConfigurationException {
		return _aiCreatorOpenAIConfigurationManager.
			isAICreatorDALLEGroupEnabled(
				_themeDisplay.getCompanyId(), _themeDisplay.getScopeGroupId());
	}

	private final AICreatorOpenAIConfigurationManager
		_aiCreatorOpenAIConfigurationManager;
	private final ThemeDisplay _themeDisplay;

}