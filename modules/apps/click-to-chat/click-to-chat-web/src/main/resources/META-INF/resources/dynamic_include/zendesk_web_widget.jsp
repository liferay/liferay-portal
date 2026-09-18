<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script position="inline">
	(function () {
		function loadZendeskScript() {
			function setZendeskUserInfo() {
				if ('<%= themeDisplay.isSignedIn() %>' === 'true') {

					<%
					ClickToChatConfiguration clickToChatConfiguration = ClickToChatConfigurationUtil.getClickToChatConfiguration(themeDisplay.getCompanyId(), themeDisplay.getSiteGroupId());

					String chatProviderSecretKey = GetterUtil.getString(request.getAttribute(ClickToChatWebKeys.CLICK_TO_CHAT_CHAT_PROVIDER_SECRET_KEY));

					String jwtToken = Jwts.builder(
					).setHeaderParam(
						"alg", SignatureAlgorithm.HS256.getValue()
					).setHeaderParam(
						"kid", clickToChatConfiguration.chatProviderKeyId()
					).setHeaderParam(
						"typ", "JWT"
					).claim(
						"email", user.getEmailAddress()
					).claim(
						"external_id", String.valueOf(user.getUserId())
					).claim(
						"name", user.getFullName()
					).claim(
						"scope", "user"
					).signWith(
						Keys.hmacShaKeyFor(chatProviderSecretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256
					).compact();
					%>

					zE('messenger', 'loginUser', (callback) => {
						callback('<%= jwtToken %>');
					});
				}
			}

			if (!document.getElementById('ze-snippet')) {
				var scriptElement = document.createElement('script');

				scriptElement.onload = function () {
					setZendeskUserInfo();
				};

				scriptElement.setAttribute('id', 'ze-snippet');
				scriptElement.setAttribute(
					'src',
					'https://static.zdassets.com/ekr/snippet.js?key=<%= clickToChatChatProviderAccountId %>'
				);
				scriptElement.setAttribute('type', 'text/javascript');

				var bodyElement = document.getElementsByTagName('body').item(0);

				bodyElement.appendChild(scriptElement);
			}
			else {
				setZendeskUserInfo();
			}
		}

		window.onload = function () {
			loadZendeskScript();
		};

		if (document.readyState === 'complete') {
			loadZendeskScript();
		}
	})();
</aui:script>