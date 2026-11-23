/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.liveusers.messaging;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterLink;
import com.liferay.portal.kernel.cluster.Priority;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = "destination.name=" + DestinationNames.LIVE_USERS,
	service = MessageListener.class
)
public class ClusterBridgeMessageListener extends BaseMessageListener {

	@Override
	protected void doReceive(Message message) throws Exception {
		if (!ClusterInvokeThreadLocal.isEnabled()) {
			return;
		}

		Address address = (Address)message.get("CLUSTER_ADDRESS");

		if (address == null) {
			if (_log.isInfoEnabled()) {
				_log.info("Bridging cluster link multicast message " + message);
			}

			_clusterLink.sendMulticastMessage(message, Priority.LEVEL5);
		}
		else {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Bridging cluster link unicast message ", message,
						" to ", address));
			}

			_clusterLink.sendUnicastMessage(address, message, Priority.LEVEL5);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClusterBridgeMessageListener.class);

	@Reference
	private ClusterLink _clusterLink;

}