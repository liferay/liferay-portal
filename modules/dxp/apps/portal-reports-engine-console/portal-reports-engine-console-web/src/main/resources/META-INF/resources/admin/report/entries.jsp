<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-portlet:renderURL varImpl="searchURL">
	<portlet:param name="mvcPath" value="/admin/view.jsp" />
</liferay-portlet:renderURL>

<aui:form action="<%= searchURL %>" method="get" name="fm">
	<liferay-portlet:renderURLParams varImpl="searchURL" />

	<liferay-ui:search-container
		searchContainer="<%= reportsEngineDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.reports.engine.console.model.Entry"
			keyPropertyName="entryId"
			modelVar="entry"
		>
			<liferay-portlet:renderURL varImpl="rowURL">
				<portlet:param name="mvcPath" value="/admin/report/requested_report_detail.jsp" />
				<portlet:param name="entryId" value="<%= String.valueOf(entry.getEntryId()) %>" />
			</liferay-portlet:renderURL>

			<%
			Definition definition = DefinitionLocalServiceUtil.fetchDefinition(entry.getDefinitionId());
			%>

			<liferay-ui:search-container-column-text
				href="<%= rowURL %>"
				name="definition-name"
				value="<%= (definition == null) ? StringPool.BLANK : HtmlUtil.escape(definition.getName(locale)) %>"
			/>

			<%
			User user2 = UserLocalServiceUtil.fetchUserById(entry.getUserId());
			%>

			<liferay-ui:search-container-column-text
				href="<%= rowURL %>"
				name="source-name"
				value="<%= (user2 == null) ? StringPool.BLANK : user2.getScreenName() %>"
			/>

			<liferay-ui:search-container-column-date
				href="<%= rowURL %>"
				name="report-generation-date"
				value="<%= entry.getCreateDate() %>"
			/>

			<liferay-ui:search-container-column-jsp
				align="right"
				path="/admin/report/requested_report_actions.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= reportsEngineDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<%
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "reports"), currentURL);
%>