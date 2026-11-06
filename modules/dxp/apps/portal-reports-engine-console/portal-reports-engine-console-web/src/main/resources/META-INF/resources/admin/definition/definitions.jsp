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
	<liferay-portlet:renderURLParams varImpl="portletURL" />

	<liferay-ui:search-container
		searchContainer="<%= reportsEngineDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.reports.engine.console.model.Definition"
			keyPropertyName="definitionId"
			modelVar="definition"
		>
			<liferay-portlet:renderURL varImpl="rowURL">
				<portlet:param name="mvcPath" value="/admin/definition/edit_definition.jsp" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="definitionId" value="<%= String.valueOf(definition.getDefinitionId()) %>" />
			</liferay-portlet:renderURL>

			<liferay-ui:search-container-column-text
				href="<%= rowURL %>"
				name="definition-name"
				value="<%= HtmlUtil.escape(definition.getName(locale)) %>"
			/>

			<%
			Source source = SourceLocalServiceUtil.fetchSource(definition.getSourceId());
			%>

			<liferay-ui:search-container-column-text
				href="<%= rowURL %>"
				name="source-name"
				value="<%= (source == null) ? ReportDataSourceType.PORTAL.getValue() : HtmlUtil.escape(source.getName(locale)) %>"
			/>

			<liferay-ui:search-container-column-date
				href="<%= rowURL %>"
				name="create-date"
				value="<%= definition.getCreateDate() %>"
			/>

			<liferay-ui:search-container-column-jsp
				align="right"
				path="/admin/definition/definition_actions.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= reportsEngineDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<%
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "definitions"), currentURL);
%>