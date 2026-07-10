<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script position="inline">
	(function () {
		function loadTidioScript() {
			function setTidioUserInfo() {
				if ('<%= themeDisplay.isSignedIn() %>' === 'true') {
					document.tidioIdentify = {
						distinct_id: '<%= user.getUserId() %>',
						email: '<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>',
						name: '<%= HtmlUtil.escapeJS(user.getFirstName()) %>',
					};
				}
			}

			if (!document.getElementById('tidio-script-chat')) {
				var scriptElement = document.createElement('script');

				scriptElement.setAttribute('id', 'tidio-script-chat');
				scriptElement.setAttribute(
					'src',
					'//code.tidio.co/<%= HtmlUtil.escapeJS(clickToChatChatProviderAccountId) %>.js'
				);
				scriptElement.setAttribute('type', 'text/javascript');
				scriptElement.onload = function () {
					setTidioUserInfo();
				};

				var bodyElement = document.getElementsByTagName('body').item(0);

				bodyElement.appendChild(scriptElement);
			}
			else {
				setTidioUserInfo();
			}
		}

		window.onload = function () {
			loadTidioScript();
		};

		if (document.readyState === 'complete') {
			loadTidioScript();
		}
	})();
</aui:script>