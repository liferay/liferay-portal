/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.stream.hub.web.socket;

import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.resource.v1_0.UserAccountResource;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author Mahmoud Hussein Tayem
 */
@Component
public class WebSocketSessionManager {

	public void addWebSocketSession(
			String subject, WebSocketSession webSocketSession)
		throws Exception {

		if (subject.equals("Guest")) {
			_roleNames.put(subject, List.of("Guest"));
		}
		else {
			List<String> roleNames = _roleNames.get(subject);

			if (roleNames == null) {
				long userId = GetterUtil.getLong(subject);

				_roleNames.put(subject, _getRoleNames(userId));
			}
		}

		List<WebSocketSession> webSocketSessions = getWebSocketSessions(
			subject);

		webSocketSessions.add(webSocketSession);
	}

	public List<String> getSubjects(List<String> roleNames1) {
		List<String> subjects = new ArrayList<>();

		_roleNames.forEach(
			(subject, roleNames2) -> {
				if (roleNames2.stream(
					).anyMatch(
						roleNames1::contains
					)) {

					subjects.add(subject);
				}
			});

		return subjects;
	}

	public List<WebSocketSession> getWebSocketSessions(String subject) {
		List<WebSocketSession> webSocketSessions = _webSocketSessions.get(
			subject);

		if (webSocketSessions == null) {
			webSocketSessions = new ArrayList<>();

			_webSocketSessions.put(subject, webSocketSessions);
		}

		return webSocketSessions;
	}

	public void removeWebSocketSession(WebSocketSession webSocketSession1) {
		_webSocketSessions.entrySet(
		).removeIf(
			entry -> {
				List<WebSocketSession> webSocketSessions = entry.getValue();

				webSocketSessions.removeIf(
					webSocketSession2 -> Objects.equals(
						webSocketSession1.getId(), webSocketSession2.getId()));

				return webSocketSessions.isEmpty();
			}
		);
	}

	private List<String> _getRoleNames(long userId) throws Exception {
		List<String> roleNames = new ArrayList<>();

		UserAccountResource userAccountResource = UserAccountResource.builder(
		).header(
			"Authorization",
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-streamhub-etc-spring-boot-oahs")
		).endpoint(
			_lxcDXPMainDomain, _lxcDXPServerProtocol
		).build();

		UserAccount userAccount = userAccountResource.getUserAccount(userId);

		for (RoleBrief roleBrief : userAccount.getRoleBriefs()) {
			roleNames.add(roleBrief.getName());
		}

		return Collections.unmodifiableList(roleNames);
	}

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Value("${com.liferay.lxc.dxp.mainDomain}")
	private String _lxcDXPMainDomain;

	@Value("${com.liferay.lxc.dxp.server.protocol}")
	private String _lxcDXPServerProtocol;

	private final Map<String, List<String>> _roleNames =
		new ConcurrentHashMap<>();
	private final Map<String, List<WebSocketSession>> _webSocketSessions =
		new ConcurrentHashMap<>();

}