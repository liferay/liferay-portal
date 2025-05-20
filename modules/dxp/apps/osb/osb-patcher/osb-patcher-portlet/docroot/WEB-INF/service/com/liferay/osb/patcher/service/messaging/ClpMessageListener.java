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

package com.liferay.osb.patcher.service.messaging;

import com.liferay.osb.patcher.service.ClpSerializer;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherBuildRelLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixPackLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixRelLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherTicketHintLocalServiceUtil;

import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;

/**
 * @author Calvin Keum
 */
public class ClpMessageListener extends BaseMessageListener {
	public static String getServletContextName() {
		return ClpSerializer.getServletContextName();
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		String command = message.getString("command");
		String servletContextName = message.getString("servletContextName");

		if (command.equals("undeploy") &&
				servletContextName.equals(getServletContextName())) {
			PatcherAccountLocalServiceUtil.clearService();

			PatcherBuildLocalServiceUtil.clearService();

			PatcherBuildRelLocalServiceUtil.clearService();

			PatcherFixLocalServiceUtil.clearService();

			PatcherFixComponentLocalServiceUtil.clearService();

			PatcherFixPackLocalServiceUtil.clearService();

			PatcherFixRelLocalServiceUtil.clearService();

			PatcherProductVersionLocalServiceUtil.clearService();

			PatcherProjectVersionLocalServiceUtil.clearService();

			PatcherTicketHintLocalServiceUtil.clearService();
		}
	}
}