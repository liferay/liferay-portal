/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.messaging;

import com.liferay.alloy.mvc.AlloyController;
import com.liferay.osb.patcher.util.PatcherAlloyControllerImpl;
import com.liferay.osb.patcher.util.PatcherMockAlloyControllerImpl;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.osb.patcher.util.PortletPropsValues;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;

/**
 * @author Zsolt Balogh
 */
public class PatcherBuildSchedulerMessageListener extends BaseMessageListener {

	public static PatcherBuildSchedulerMessageListener getInstance(
		AlloyController alloyController) {

		_instance.setAlloyController(
			new PatcherMockAlloyControllerImpl(
				(PatcherAlloyControllerImpl)alloyController));

		return _instance;
	}

	public void setAlloyController(AlloyController alloyController) {
		_alloyController = alloyController;
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		PatcherUtil.processOSBPatcherMessageQueue(_alloyController);

		PatcherUtil.processOSBPatcherStatusFiles(
			_alloyController,
			PortletPropsValues.OSB_PATCHER_STATUS_BUILD_JENKINS_PATH);

		PatcherUtil.processOSBPatcherStatusFiles(
			_alloyController,
			PortletPropsValues.OSB_PATCHER_STATUS_BUILD_JENKINS_TEST_PATH);

		PatcherUtil.processOSBPatcherStatusFiles(
			_alloyController, PortletPropsValues.OSB_PATCHER_STATUS_BUILD_PATH);
	}

	private static final PatcherBuildSchedulerMessageListener _instance =
		new PatcherBuildSchedulerMessageListener();

	private AlloyController _alloyController;

}