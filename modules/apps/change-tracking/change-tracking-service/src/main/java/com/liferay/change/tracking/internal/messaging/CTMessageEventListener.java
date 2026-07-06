/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.messaging;

import com.liferay.change.tracking.service.CTMessageLocalService;
import com.liferay.change.tracking.spi.listener.CTEventListener;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = CTEventListener.class)
public class CTMessageEventListener implements CTEventListener {

	@Override
	public void onAfterPublish(long ctCollectionId) {
		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				List<Message> messages = _ctMessageLocalService.getMessages(
					ctCollectionId);

				try (SafeCloseable safeCloseable =
						CTCollectionThreadLocal.
							setProductionModeWithSafeCloseable()) {

					for (Message message : messages) {
						_messageBus.sendMessage(
							message.getDestinationName(), message);
					}
				}

				return null;
			});
	}

	@Reference
	private CTMessageLocalService _ctMessageLocalService;

	@Reference
	private MessageBus _messageBus;

}