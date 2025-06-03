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
public class PatcherFixSchedulerMessageListener extends BaseMessageListener {

	public static PatcherFixSchedulerMessageListener getInstance(
		ThemeDisplay themeDisplay) {

		_patcherFixSchedulerMessageListener.setThemeDisplay(themeDisplay);

		return _patcherFixSchedulerMessageListener;
	}

	public void setThemeDisplay(ThemeDisplay themeDisplay) {
		_themeDisplay = themeDisplay;
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		PatcherUtil.processOSBPatcherStatusFiles(
			PortletPropsValues.OSB_PATCHER_STATUS_FIX_PATH, _themeDisplay);

		PatcherUtil.notifyUsersInactivePatcherBaseModels(_themeDisplay);
	}

	private static final PatcherFixSchedulerMessageListener
		_patcherFixSchedulerMessageListener =
			new PatcherFixSchedulerMessageListener();

	private ThemeDisplay _themeDisplay;

}