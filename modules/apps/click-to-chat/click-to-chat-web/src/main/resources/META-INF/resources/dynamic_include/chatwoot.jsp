<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script position="inline">
	if (!(window.$chatwoot && window.$chatwoot.hasLoaded)) {
		(function (d, t) {
			var BASE_URL = 'https://app.chatwoot.com';

			var g = d.createElement(t);
			var s = d.getElementsByTagName(t)[0];

			g.src = BASE_URL + '/packs/js/sdk.js';

			s.parentNode.insertBefore(g, s);

			g.onload = function () {
				window.chatwootSDK.run({
					baseUrl: BASE_URL,
					websiteToken:
						'<%= HtmlUtil.escapeJS(clickToChatChatProviderAccountId) %>',
				});
			};
		})(document, 'script');
	}

	<c:if test="<%= themeDisplay.isSignedIn() %>">
		window.onload = function () {
			window.$chatwoot.setUser('<%= user.getUserId() %>', {
				email: '<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>',
				name: '<%= HtmlUtil.escapeJS(user.getScreenName()) %>',
			});
		};
	</c:if>
</aui:script>