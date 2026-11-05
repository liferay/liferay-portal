<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
int cur = ParamUtil.getInteger(request, SearchContainer.DEFAULT_CUR_PARAM);

PortletURL portletURL = renderResponse.createRenderURL();
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new PortalInstancesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse) %>"
	propsTransformer="{PortalInstancesManagementToolbarPropsTransformer} from portal-instances-web"
	propsTransformerServletContext="<%= application %>"
/>

<portlet:renderURL var="redirectURL">
	<portlet:param name="cur" value="<%= String.valueOf(cur) %>" />
</portlet:renderURL>

<aui:form action="<%= portletURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= redirectURL %>" />
	<aui:input name="className" type="hidden" />

	<%@ include file="/instances.jspf" %>
</aui:form>