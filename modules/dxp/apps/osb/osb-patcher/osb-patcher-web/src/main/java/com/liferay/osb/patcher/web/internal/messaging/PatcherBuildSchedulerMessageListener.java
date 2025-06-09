/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.messaging;

import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;

/**
 * @author Zsolt Balogh
 */
public class PatcherBuildSchedulerMessageListener extends BaseMessageListener {

	public static PatcherBuildSchedulerMessageListener getInstance(
		ThemeDisplay themeDisplay) {

		_patcherBuildSchedulerMessageListener.setThemeDisplay(themeDisplay);

		return _patcherBuildSchedulerMessageListener;
	}

	public void setThemeDisplay(ThemeDisplay themeDisplay) {
		_themeDisplay = themeDisplay;
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		PatcherUtil.processOSBPatcherMessageQueue(_themeDisplay);

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, CompanyThreadLocal.getCompanyId());

		PatcherUtil.processOSBPatcherStatusFiles(
			patcherConfiguration.patcherStatusBuildJenkinsPath(),
			_themeDisplay);

		PatcherUtil.processOSBPatcherStatusFiles(
			patcherConfiguration.patcherStatusBuildJenkinsTestPath(),
			_themeDisplay);

		PatcherUtil.processOSBPatcherStatusFiles(
			patcherConfiguration.patcherStatusBuildPath(), _themeDisplay);
	}

	private static final PatcherBuildSchedulerMessageListener
		_patcherBuildSchedulerMessageListener =
			new PatcherBuildSchedulerMessageListener();

	private ThemeDisplay _themeDisplay;

}