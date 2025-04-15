/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.display;

import com.liferay.petra.concurrent.DCLSingleton;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author Shuyang Zhou
 */
public abstract class ConfigurationScreenWrapper
	implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		ConfigurationScreen configurationScreen =
			_configurationScreenDCLSingleton.getSingleton(
				this::getConfigurationScreen);

		return configurationScreen.getCategoryKey();
	}

	@Override
	public String getKey() {
		ConfigurationScreen configurationScreen =
			_configurationScreenDCLSingleton.getSingleton(
				this::getConfigurationScreen);

		return configurationScreen.getKey();
	}

	@Override
	public String getName(Locale locale) {
		ConfigurationScreen configurationScreen =
			_configurationScreenDCLSingleton.getSingleton(
				this::getConfigurationScreen);

		return configurationScreen.getName(locale);
	}

	@Override
	public String getScope() {
		ConfigurationScreen configurationScreen =
			_configurationScreenDCLSingleton.getSingleton(
				this::getConfigurationScreen);

		return configurationScreen.getScope();
	}

	@Override
	public boolean isVisible() {
		ConfigurationScreen configurationScreen =
			_configurationScreenDCLSingleton.getSingleton(
				this::getConfigurationScreen);

		return configurationScreen.isVisible();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		ConfigurationScreen configurationScreen =
			_configurationScreenDCLSingleton.getSingleton(
				this::getConfigurationScreen);

		configurationScreen.render(httpServletRequest, httpServletResponse);
	}

	protected abstract ConfigurationScreen getConfigurationScreen();

	private final DCLSingleton<ConfigurationScreen>
		_configurationScreenDCLSingleton = new DCLSingleton<>();

}