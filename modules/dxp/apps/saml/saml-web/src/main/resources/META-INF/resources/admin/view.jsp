<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", "general");

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setTabs1(
	tabs1
).buildPortletURL();

String tabs1Names = "general";

if (samlProviderConfigurationHelper.isRoleIb()) {
	tabs1Names += ",identity-provider,service-provider-connections,service-provider,identity-provider-connections";
}
else if (samlProviderConfigurationHelper.isRoleIdp()) {
	tabs1Names += ",identity-provider,service-provider-connections";
}
else if (samlProviderConfigurationHelper.isRoleSp()) {
	tabs1Names += ",service-provider,identity-provider-connections";
}
%>

<div class="container-fluid container-fluid-max-xl sheet">
	<liferay-ui:tabs
		cssClass="subnav-tbar-light"
		names="<%= tabs1Names %>"
		url="<%= portletURL.toString() %>"
	>
		<clay:container-fluid>
			<c:choose>
				<c:when test='<%= tabs1.equals("general") %>'>
					<liferay-util:include page="/admin/general.jsp" servletContext="<%= pageContext.getServletContext() %>" />
				</c:when>
				<c:when test='<%= tabs1.equals("identity-provider") %>'>
					<liferay-util:include page="/admin/identity_provider.jsp" servletContext="<%= pageContext.getServletContext() %>" />
				</c:when>
				<c:when test='<%= tabs1.equals("identity-provider-connections") %>'>
					<liferay-util:include page="/admin/view_identity_provider_connections.jsp" servletContext="<%= pageContext.getServletContext() %>" />
				</c:when>
				<c:when test='<%= tabs1.equals("service-provider") %>'>
					<liferay-util:include page="/admin/service_provider.jsp" servletContext="<%= pageContext.getServletContext() %>" />
				</c:when>
				<c:when test='<%= tabs1.equals("service-provider-connections") %>'>
					<liferay-util:include page="/admin/view_service_provider_connections.jsp" servletContext="<%= pageContext.getServletContext() %>" />
				</c:when>
			</c:choose>
		</clay:container-fluid>
	</liferay-ui:tabs>
</div>