/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyController;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;

/**
 * @author Zsolt Balogh
 */
public class PatcherFixSchedulerMessageListener extends BaseMessageListener {

	public static PatcherFixSchedulerMessageListener getInstance(
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
		PatcherUtil.processOSBPatcherStatusFiles(
			_alloyController, PortletPropsValues.OSB_PATCHER_STATUS_FIX_PATH);

		PatcherUtil.notifyUsersInactivePatcherBaseModels(_alloyController);
	}

	private static final PatcherFixSchedulerMessageListener _instance =
		new PatcherFixSchedulerMessageListener();

	private AlloyController _alloyController;

}