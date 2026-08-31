<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPConfigurationListDisplayContext cpConfigurationListDisplayContext = (CPConfigurationListDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPConfigurationList cpConfigurationList = cpConfigurationListDisplayContext.getCPConfigurationList();
%>

<portlet:actionURL name="/cp_configuration_lists/edit_cp_configuration_list_external_reference_code" var="editCPConfigurationListExternalReferenceCodeURL" />

<aui:form action="<%= editCPConfigurationListExternalReferenceCodeURL %>" cssClass="container-fluid container-fluid-max-xl p-4" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="cpConfigurationListId" type="hidden" value="<%= cpConfigurationList.getCPConfigurationListId() %>" />

	<aui:model-context bean="<%= cpConfigurationList %>" model="<%= CPConfigurationList.class %>" />

	<aui:input name="externalReferenceCode" type="text" value="<%= cpConfigurationList.getExternalReferenceCode() %>" wrapperCssClass="form-group-item" />
</aui:form>