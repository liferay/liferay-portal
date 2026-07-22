<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ScriptManagementConfigurationDisplayContext scriptManagementConfigurationDisplayContext = (ScriptManagementConfigurationDisplayContext)request.getAttribute(ScriptManagementConfigurationDisplayContext.class.getName());
%>

<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" var="baseResourceURL" />

<react:component
	module="{ScriptManagementContainer} from portal-security-script-management-web"
	props='<%=
		HashMapBuilder.<String, Object>put(
			"allowScriptContentToBeExecutedOrIncluded", scriptManagementConfigurationDisplayContext.isAllowScriptContentToBeExecutedOrIncluded()
		).put(
			"baseResourceURL", String.valueOf(baseResourceURL)
		).put(
			"scriptManagementConfigurationDefined", scriptManagementConfigurationDisplayContext.isScriptManagementConfigurationDefined()
		).build()
	%>'
/>