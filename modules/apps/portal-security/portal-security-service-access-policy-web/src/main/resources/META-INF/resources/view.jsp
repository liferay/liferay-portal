<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SAPEntryDisplayContext sapEntryDisplayContext = new SAPEntryDisplayContext(liferayPortletRequest, liferayPortletResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new SAPEntryManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, sapEntryDisplayContext.getSearchContainer()) %>"
/>

<clay:container-fluid>
	<liferay-ui:search-container
		searchContainer="<%= sapEntryDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.security.service.access.policy.model.SAPEntry"
			escapedModel="<%= true %>"
			keyPropertyName="sapEntryId"
			modelVar="sapEntry"
		>
			<portlet:renderURL var="rowURL">
				<portlet:param name="mvcPath" value="/edit_entry.jsp" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="sapEntryId" value="<%= String.valueOf(sapEntry.getSapEntryId()) %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				href="<%= rowURL %>"
				name="name"
			>
				<strong><%= sapEntry.getName() %></strong>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="description"
				value="<%= sapEntry.getTitle(locale) %>"
			/>

			<liferay-ui:search-container-column-text
				name="enabled"
				value='<%= LanguageUtil.get(request, sapEntry.isEnabled() ? "yes" : "no") %>'
			/>

			<liferay-ui:search-container-column-text
				name="default"
				value='<%= LanguageUtil.get(request, sapEntry.isDefaultSAPEntry() ? "yes" : "no") %>'
			/>

			<liferay-ui:search-container-column-jsp
				path="/entry_action.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>