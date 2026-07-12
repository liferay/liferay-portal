<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script position="inline">
	window.__lc = window.__lc || {};
	window.__lc.license =
		'<%= HtmlUtil.escapeJS(clickToChatChatProviderAccountId) %>';

	(function (n, t, c) {
		function i(n) {
			return e._h ? e._h.apply(null, n) : e._q.push(n);
		}

		var e = {
			_h: null,
			_q: [],
			_v: '2.0',
			call: function () {
				i(['call', c.call(arguments)]);
			},
			get: function () {
				if (!e._h)
					throw new Error(
						"[LiveChatWidget] You can't use getters before load."
					);
				return i(['get', c.call(arguments)]);
			},
			init: function () {
				var n = t.createElement('script');
				(n.async = !0),
					(n.type = 'text/javascript'),
					(n.src = 'https://cdn.livechatinc.com/tracking.js'),
					t.head.appendChild(n);
			},
			off: function () {
				i(['off', c.call(arguments)]);
			},
			on: function () {
				i(['on', c.call(arguments)]);
			},
			once: function () {
				i(['once', c.call(arguments)]);
			},
		};

		!n.__lc.asyncInit && e.init(), (n.LiveChatWidget = n.LiveChatWidget || e);
	})(window, document, [].slice);

	<c:if test="<%= themeDisplay.isSignedIn() %>">
		window.onload = function () {
			LiveChatWidget.call(
				'set_customer_email',
				'<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>'
			);
			LiveChatWidget.call(
				'set_customer_name',
				'<%= HtmlUtil.escapeJS(user.getScreenName()) %>'
			);
		};
	</c:if>
</aui:script>