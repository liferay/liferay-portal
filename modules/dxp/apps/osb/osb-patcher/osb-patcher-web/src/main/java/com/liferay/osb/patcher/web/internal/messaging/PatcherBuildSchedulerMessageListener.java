/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.messaging;

import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.osb.patcher.util.PortletPropsValues;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
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

		PatcherUtil.processOSBPatcherStatusFiles(
			PortletPropsValues.OSB_PATCHER_STATUS_BUILD_JENKINS_PATH,
			_themeDisplay);

		PatcherUtil.processOSBPatcherStatusFiles(
			PortletPropsValues.OSB_PATCHER_STATUS_BUILD_JENKINS_TEST_PATH,
			_themeDisplay);

		PatcherUtil.processOSBPatcherStatusFiles(
			PortletPropsValues.OSB_PATCHER_STATUS_BUILD_PATH, _themeDisplay);
	}

	private static final PatcherBuildSchedulerMessageListener
		_patcherBuildSchedulerMessageListener =
			new PatcherBuildSchedulerMessageListener();

	private ThemeDisplay _themeDisplay;

}