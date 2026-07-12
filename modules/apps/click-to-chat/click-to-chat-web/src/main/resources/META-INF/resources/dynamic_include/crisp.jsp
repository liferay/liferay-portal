<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script position="inline" type="text/javascript">
	window.$crisp = [];
	window.CRISP_WEBSITE_ID =
		'<%= HtmlUtil.escapeJS(clickToChatChatProviderAccountId) %>';

	(function () {
		function loadCrispScript() {
			function setCrispUserInfo() {
				if ('<%= themeDisplay.isSignedIn() %>' === 'true') {
					$crisp.push([
						'set',
						'user:email',
						'<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>',
					]);
					$crisp.push([
						'set',
						'user:nickname',
						'<%= HtmlUtil.escapeJS(user.getScreenName()) %>',
					]);
				}
			}

			if (!document.getElementById('crisp-script-chat')) {
				var scriptElement = document.createElement('script');

				scriptElement.setAttribute('async', true);
				scriptElement.setAttribute('id', 'crisp-script-chat');
				scriptElement.setAttribute('src', 'https://client.crisp.chat/l.js');
				scriptElement.setAttribute('type', 'text/javascript');
				scriptElement.onload = function () {
					setCrispUserInfo();
				};

				var bodyElement = document.getElementsByTagName('body').item(0);

				bodyElement.appendChild(scriptElement);
			}
			else {
				setCrispUserInfo();
			}
		}

		window.onload = function () {
			loadCrispScript();
		};

		if (document.readyState === 'complete') {
			loadCrispScript();
		}
	})();
</aui:script>