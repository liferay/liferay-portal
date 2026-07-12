<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<aui:script async="<%= true %>" src='<%= "//code.jivosite.com/widget/" + clickToChatChatProviderAccountId %>'></aui:script>

<c:if test="<%= themeDisplay.isSignedIn() %>">
	<aui:script position="inline">
		function jivo_onOpen() {
			jivo_api.setContactInfo({
				email: '<%= HtmlUtil.escapeJS(user.getEmailAddress()) %>',
				name: '<%= user.getScreenName() %>',
			});
		}
	</aui:script>
</c:if>