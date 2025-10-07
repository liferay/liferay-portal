/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.stream.hub.web.socket.config;

import com.liferay.stream.hub.web.socket.SecureHandshakeInterceptor;
import com.liferay.stream.hub.web.socket.handler.SecureTextWebSocketHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author Mahmoud Hussein Tayem
 */
@Configuration
@EnableWebSocket
public class SecureWebSocketConfigurer implements WebSocketConfigurer {

	@Autowired
	public SecureWebSocketConfigurer(
		SecureHandshakeInterceptor secureHandshakeInterceptor,
		SecureTextWebSocketHandler secureTextWebSocketHandler) {

		_secureHandshakeInterceptor = secureHandshakeInterceptor;
		_secureTextWebSocketHandler = secureTextWebSocketHandler;
	}

	@Override
	public void registerWebSocketHandlers(
		WebSocketHandlerRegistry webSocketHandlerRegistry) {

		webSocketHandlerRegistry.addHandler(
			_secureTextWebSocketHandler, "/server"
		).addInterceptors(
			_secureHandshakeInterceptor
		).setAllowedOrigins(
			"*"
		);
	}

	private final SecureHandshakeInterceptor _secureHandshakeInterceptor;
	private final SecureTextWebSocketHandler _secureTextWebSocketHandler;

}