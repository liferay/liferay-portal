<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/designer/init.jsp" %>

<liferay-ui:header
	showBackURL="<%= false %>"
	title="error"
/>

<liferay-ui:error exception="<%= NoSuchDefinitionException.class %>" message="the-workflow-definition-could-not-be-found" />
<liferay-ui:error exception="<%= NoSuchDefinitionVersionException.class %>" message="the-workflow-definition-could-not-be-found" />

<liferay-ui:error-principal />