/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.websocket.whiteboard.test.encode.endpoint;

import com.liferay.websocket.whiteboard.test.encode.data.Example;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;

import java.io.IOException;

/**
 * @author Cristina González
 */
public class EncodeWebSocketEndpoint extends Endpoint {

	@Override
	public void onOpen(final Session session, EndpointConfig endpointConfig) {
		session.addMessageHandler(
			new MessageHandler.Whole<Example>() {

				@Override
				public void onMessage(Example data) {
					try {
						RemoteEndpoint.Basic basic = session.getBasicRemote();

						basic.sendObject(data);
					}
					catch (IOException ioException) {
						throw new RuntimeException(ioException);
					}
					catch (EncodeException encodeException) {
						throw new RuntimeException(encodeException);
					}
				}

			});
	}

}