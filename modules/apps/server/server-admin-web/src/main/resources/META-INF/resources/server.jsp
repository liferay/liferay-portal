<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ServerDisplayContext serverDisplayContext = (ServerDisplayContext)request.getAttribute(ServerAdminWebKeys.SERVER_DISPLAY_CONTEXT);
%>

<clay:navigation-bar
	navigationItems="<%= serverDisplayContext.getServerNavigationItems() %>"
/>

<div class="<%= (tabs1.equals("log-levels") || tabs1.equals("properties")) ? StringPool.BLANK : "container-fluid container-fluid-max-xl mt-3" %>">

	<%
	String key = serverDisplayContext.getSessionMessagesKey();
	%>

	<c:if test="<%= Validator.isNotNull(key) %>">
		<aui:script>
			Liferay.Util.openToast({
				autoClose: 10000,
				message: '<liferay-ui:message key="<%= key %>" />',
				type: 'success',
			});
		</aui:script>
	</c:if>

	<liferay-ui:success key="databaseSchemaExported" message='<%= LanguageUtil.format(request, "the-database-schema-was-exported-successfully-to-x", GetterUtil.getString(SessionMessages.get(renderRequest, "databaseSchemaExported"))) %>' />

	<c:choose>
		<c:when test='<%= tabs1.equals("database-migration") %>'>
			<liferay-util:include page="/database_migration.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= tabs1.equals("document-migration") %>'>
			<liferay-util:include page="/document_migration.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= tabs1.equals("external-services") %>'>
			<liferay-util:include page="/external_services.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= tabs1.equals("friendly-urls") %>'>
			<liferay-util:include page="/friendly_urls.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= tabs1.equals("log-levels") %>'>
			<liferay-util:include page="/log_levels.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= tabs1.equals("mail") %>'>
			<liferay-util:include page="/mail.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= tabs1.equals("production-readiness") %>'>
			<liferay-util:include page="/production_readiness.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= tabs1.equals("properties") %>'>
			<liferay-util:include page="/properties.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= tabs1.equals("script") %>'>
			<liferay-util:include page="/script.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= tabs1.equals("shutdown") %>'>
			<liferay-util:include page="/shutdown.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:otherwise>
			<liferay-util:include page="/resources.jsp" servletContext="<%= application %>" />
		</c:otherwise>
	</c:choose>
</div>