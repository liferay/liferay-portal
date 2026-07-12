<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script position="inline" type="text/javascript">
	var Tawk_API = Tawk_API || {},
		Tawk_LoadStart = new Date();

	(function () {
		var s1 = document.createElement('script'),
			s0 = document.getElementsByTagName('script')[0];

		s1.async = true;
		s1.charset = 'UTF-8';
		s1.setAttribute('crossorigin', '*');
		s1.src =
			'https://embed.tawk.to/<%= HtmlUtil.escapeJS(clickToChatChatProviderAccountId) %>';

		s0.parentNode.insertBefore(s1, s0);
	})();

	<c:if test="<%= themeDisplay.isSignedIn() %>">
		Tawk_API = Tawk_API || {};

		Tawk_API.visitor = {
			email: '<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>',
			name: '<%= HtmlUtil.escapeJS(user.getScreenName()) %>',
		};
	</c:if>
</aui:script>