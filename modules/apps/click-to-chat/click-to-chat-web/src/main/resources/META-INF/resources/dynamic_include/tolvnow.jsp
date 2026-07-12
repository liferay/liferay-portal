<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script position="inline" type="text/javascript">
	var _tn = _tn || [];

	_tn.push([
		'account',
		'<%= HtmlUtil.escapeJS(clickToChatChatProviderAccountId) %>',
	]);
	_tn.push(['action', 'track-view']);

	<c:if test="<%= themeDisplay.isSignedIn() %>">
		_tn.push(['_setEmail', '<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>']);
		_tn.push(['_setName', '<%= HtmlUtil.escapeJS(user.getScreenName()) %>']);
	</c:if>

	(function () {
		function loadTolnowScript() {
			if (!document.getElementById('tolvnow-script-chat')) {
				var spanElement = document.createElement('span');

				spanElement.setAttribute('id', 'tolvnow');

				var scriptElement = document.createElement('script');

				scriptElement.setAttribute('async', true);
				scriptElement.setAttribute('id', 'tolvnow-script-chat');
				scriptElement.setAttribute('src', '//tracker.tolvnow.com/js/tn.js');
				scriptElement.setAttribute('type', 'text/javascript');

				var bodyElement = document.getElementsByTagName('body').item(0);

				bodyElement.appendChild(spanElement);
				bodyElement.appendChild(scriptElement);
			}
		}

		window.onload = function () {
			loadTolnowScript();
		};

		if (document.readyState === 'complete') {
			loadTolnowScript();
		}
	})();
</aui:script>