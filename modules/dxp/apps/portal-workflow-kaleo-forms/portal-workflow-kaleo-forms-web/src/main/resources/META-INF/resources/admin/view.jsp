<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
String displayStyle = kaleoFormsAdminDisplayContext.getDisplayStyle();
%>

<liferay-util:include page="/admin/navigation_bar.jsp" servletContext="<%= application %>" />

<liferay-util:include page="/admin/management_bar.jsp" servletContext="<%= application %>" />

<%@ include file="/admin/deprecated_warning.jspf" %>

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "formContainer" %>'
>
	<aui:form action="<%= kaleoFormsAdminDisplayContext.getSearchActionURL() %>" method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= kaleoFormsAdminDisplayContext.getSearchActionURL() %>" />
		<aui:input name="kaleoProcessIds" type="hidden" />

		<liferay-ui:search-container
			id="kaleoProcess"
			rowChecker="<%= new EmptyOnClickRowChecker(renderResponse) %>"
			searchContainer="<%= kaleoFormsAdminDisplayContext.getKaleoProcessSearch() %>"
			total="<%= kaleoFormsAdminDisplayContext.getTotalItems() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess"
				keyPropertyName="kaleoProcessId"
				modelVar="process"
			>
				<portlet:renderURL var="rowURL">
					<portlet:param name="mvcPath" value="/admin/view_kaleo_process.jsp" />
					<portlet:param name="redirect" value="<%= searchContainer.getIteratorURL().toString() %>" />
					<portlet:param name="kaleoProcessId" value="<%= String.valueOf(process.getKaleoProcessId()) %>" />
					<portlet:param name="displayStyle" value="<%= displayStyle %>" />
				</portlet:renderURL>

				<liferay-ui:search-container-column-text
					href="<%= rowURL %>"
					name="id"
					orderable="<%= false %>"
					property="kaleoProcessId"
				/>

				<liferay-ui:search-container-column-text
					href="<%= rowURL %>"
					name="name"
					orderable="<%= false %>"
					truncate="<%= true %>"
					value="<%= HtmlUtil.escape(process.getName(locale)) %>"
				/>

				<liferay-ui:search-container-column-text
					name="description"
					orderable="<%= false %>"
					truncate="<%= true %>"
					value="<%= HtmlUtil.escape(StringUtil.shorten(process.getDescription(locale), 100)) %>"
				/>

				<liferay-ui:search-container-column-date
					name="modified-date"
					orderable="<%= false %>"
					value="<%= process.getModifiedDate() %>"
				/>

				<liferay-ui:search-container-column-jsp
					align="right"
					cssClass="entry-action"
					path="/admin/kaleo_process_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= displayStyle %>"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<%@ include file="/admin/export_kaleo_process.jspf" %>

<%
KaleoFormsUtil.cleanUpPortletSession(portletSession);
%>