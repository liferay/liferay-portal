/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.websocket.whiteboard.test.simple.client;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;

import java.io.IOException;

import java.nio.ByteBuffer;

import java.util.concurrent.BlockingQueue;

/**
 * @author Cristina González
 */
@ClientEndpoint
public class BinaryWebSocketClient {

	public BinaryWebSocketClient(BlockingQueue<ByteBuffer> blockingQueue) {
		_blockingQueue = blockingQueue;
	}

	@OnMessage
	public void onMessage(ByteBuffer byteBuffer, Session session)
		throws InterruptedException {

		_blockingQueue.put(byteBuffer);
	}

	@OnOpen
	public void onOpen(Session session) {
		_session = session;
	}

	public void sendMessage(ByteBuffer byteBuffer) throws IOException {
		RemoteEndpoint.Basic basic = _session.getBasicRemote();

		basic.sendBinary(byteBuffer);
	}

	private final BlockingQueue<ByteBuffer> _blockingQueue;
	private Session _session;

}