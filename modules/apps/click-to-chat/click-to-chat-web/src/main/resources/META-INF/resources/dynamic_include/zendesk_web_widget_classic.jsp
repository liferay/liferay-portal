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
					zE('webWidget', 'identify', {
						email: '<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>',
						name: '<%= HtmlUtil.escapeJS(user.getScreenName()) %>',
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
					'https://static.zdassets.com/ekr/snippet.js?key=<%= HtmlUtil.escapeJS(clickToChatChatProviderAccountId) %>'
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