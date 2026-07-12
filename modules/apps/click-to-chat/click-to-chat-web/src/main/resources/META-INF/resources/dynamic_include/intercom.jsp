<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script position="inline">
	var APP_ID = '<%= HtmlUtil.escapeJS(clickToChatChatProviderAccountId) %>';

	let intercomSettings = {
		app_id: APP_ID,
	};

	<c:if test="<%= themeDisplay.isSignedIn() %>">

		<%
		Date createDate = user.getCreateDate();
		%>

		<c:if test="<%= createDate != null %>">
			intercomSettings.created_at = '<%= createDate.getTime() %>';
		</c:if>

		intercomSettings.email = '<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>';
		intercomSettings.name = '<%= HtmlUtil.escapeJS(user.getFullName()) %>';
		intercomSettings.user_id = '<%= user.getUserId() %>';
	</c:if>

	window.intercomSettings = intercomSettings;

	(function () {
		var w = window;
		var ic = w.Intercom;
		if (typeof ic === 'function') {
			ic('reattach_activator');
			ic('update', w.intercomSettings);
		}
		else {
			var d = document;
			var i = function () {
				i.c(arguments);
			};
			i.q = [];
			i.c = function (args) {
				i.q.push(args);
			};
			w.Intercom = i;
			var l = function () {
				var s = d.createElement('script');
				s.type = 'text/javascript';
				s.async = true;
				s.src = 'https://widget.intercom.io/widget/' + APP_ID;
				var x = d.getElementsByTagName('script')[0];
				x.parentNode.insertBefore(s, x);
			};
			if (document.readyState === 'complete') {
				l();
			}
			else if (w.attachEvent) {
				w.attachEvent('onload', l);
			}
			else {
				w.addEventListener('load', l, false);
			}
		}
	})();
</aui:script>