<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script position="inline" type="text/javascript">
	var _smartsupp = _smartsupp || {};

	_smartsupp.key = '<%= HtmlUtil.escapeJS(clickToChatChatProviderAccountId) %>';

	window.smartsupp ||
		(function (d) {
			var c,
				s,
				o = (smartsupp = function () {
					o._.push(arguments);
				});

			o._ = [];

			c = d.createElement('script');

			c.async = true;
			c.charset = 'utf-8';
			c.src = 'https://www.smartsuppchat.com/loader.js?';
			c.type = 'text/javascript';

			s = d.getElementsByTagName('script')[0];

			s.parentNode.insertBefore(c, s);
		})(document);

	<c:if test="<%= themeDisplay.isSignedIn() %>">
		smartsupp('email', '<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>');
		smartsupp('name', '<%= HtmlUtil.escapeJS(user.getFirstName()) %>');
	</c:if>
</aui:script>