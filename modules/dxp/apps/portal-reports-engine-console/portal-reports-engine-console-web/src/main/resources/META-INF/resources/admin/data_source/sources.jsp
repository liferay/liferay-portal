<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<aui:form
	action='<%=
		PortletURLBuilder.create(
			reportsEngineDisplayContext.getPortletURL()
		).setMVCPath(
			"/admin/view.jsp"
		).buildPortletURL()
	%>'
	method="get"
	name="fm"
>
	<liferay-portlet:renderURLParams varImpl="searchURL" />

	<liferay-ui:search-container
		searchContainer="<%= reportsEngineDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.reports.engine.console.model.Source"
			keyPropertyName="sourceId"
			modelVar="source"
		>
			<liferay-portlet:renderURL varImpl="rowURL">
				<portlet:param name="mvcPath" value="/admin/data_source/edit_data_source.jsp" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="sourceId" value="<%= String.valueOf(source.getSourceId()) %>" />
			</liferay-portlet:renderURL>

			<liferay-ui:search-container-column-text
				href="<%= rowURL %>"
				name="source-name"
				value="<%= HtmlUtil.escape(source.getName(locale)) %>"
			/>

			<liferay-ui:search-container-column-text
				href="<%= rowURL %>"
				name="jdbc-url"
				property="driverUrl"
			/>

			<liferay-ui:search-container-column-text
				href="<%= rowURL %>"
				name="jdbc-user-name"
				property="driverUserName"
			/>

			<liferay-ui:search-container-column-jsp
				align="right"
				path="/admin/data_source/data_source_actions.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= reportsEngineDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<%
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "sources"), currentURL);
%>