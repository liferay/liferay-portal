<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String displayStyle = (String)request.getAttribute("edit_role_assignments.jsp-displayStyle");
%>

<liferay-site:membership-policy-error />

<liferay-ui:search-container
	id="assigneesSearch"
	searchContainer='<%= (SearchContainer)request.getAttribute("edit_role_assignments.jsp-searchContainer") %>'
	var="userSearchContainer"
>
	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.User"
		escapedModel="<%= true %>"
		keyPropertyName="userId"
		modelVar="user2"
		rowIdProperty="screenName"
	>
		<%@ include file="/user_columns.jspf" %>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		displayStyle="<%= displayStyle %>"
		markupView="lexicon"
	/>
</liferay-ui:search-container>