<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditRolesUsersDisplayContext editRolesUsersDisplayContext = new EditRolesUsersDisplayContext(request, renderRequest, renderResponse);
%>

<aui:input name="addUserIds" type="hidden" />
<aui:input name="removeUserIds" type="hidden" />

<liferay-site:membership-policy-error />

<liferay-ui:search-container
	searchContainer="<%= editRolesUsersDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.User"
		escapedModel="<%= true %>"
		keyPropertyName="userId"
		modelVar="user2"
		rowIdProperty="screenName"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand table-cell-minw-200 table-title"
			name="name"
			property="fullName"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand table-cell-minw-200"
			name="screen-name"
			property="screenName"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>