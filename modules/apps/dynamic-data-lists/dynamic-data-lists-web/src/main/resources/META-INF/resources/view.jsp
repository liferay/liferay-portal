<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PortletURL portletURL = ddlDisplayContext.getPortletURL();

String displayStyle = ddlDisplayContext.getDisplayStyle();
%>

<liferay-util:include page="/navigation_bar.jsp" servletContext="<%= application %>" />

<liferay-util:include page="/management_bar.jsp" servletContext="<%= application %>" />

<%@ include file="/deprecated_warning.jspf" %>

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "formContainer" %>'
>
	<aui:form action="<%= portletURL %>" method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
		<aui:input name="recordSetIds" type="hidden" />

		<liferay-ui:search-container
			id="ddlRecordSet"
			rowChecker="<%= new EmptyOnClickRowChecker(renderResponse) %>"
			searchContainer="<%= ddlDisplayContext.getSearchContainer() %>"
		>

			<%
			request.setAttribute(WebKeys.SEARCH_CONTAINER, searchContainer);
			%>

			<liferay-ui:search-container-row
				className="com.liferay.dynamic.data.lists.model.DDLRecordSet"
				keyPropertyName="recordSetId"
				modelVar="recordSet"
			>
				<liferay-portlet:renderURL varImpl="rowURL">
					<portlet:param name="mvcPath" value="/view_record_set.jsp" />
					<portlet:param name="redirect" value="<%= searchContainer.getIteratorURL().toString() %>" />
					<portlet:param name="recordSetId" value="<%= String.valueOf(recordSet.getRecordSetId()) %>" />
					<portlet:param name="displayStyle" value="<%= displayStyle %>" />
				</liferay-portlet:renderURL>

				<%
				if (!DDLRecordSetPermission.contains(permissionChecker, recordSet, ActionKeys.VIEW)) {
					rowURL = null;
				}
				%>

				<c:choose>
					<c:when test='<%= displayStyle.equals("descriptive") %>'>
						<liferay-ui:search-container-column-text>
							<liferay-user:user-portrait
								userId="<%= recordSet.getUserId() %>"
							/>
						</liferay-ui:search-container-column-text>

						<liferay-ui:search-container-column-jsp
							colspan="<%= 2 %>"
							href="<%= rowURL %>"
							path="/view_record_set_descriptive.jsp"
						/>

						<liferay-ui:search-container-column-jsp
							path="/record_set_action.jsp"
						/>
					</c:when>
					<c:otherwise>
						<%@ include file="/search_columns.jspf" %>

						<liferay-ui:search-container-column-jsp
							path="/record_set_action.jsp"
						/>
					</c:otherwise>
				</c:choose>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= displayStyle %>"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<%@ include file="/export_record_set.jspf" %>