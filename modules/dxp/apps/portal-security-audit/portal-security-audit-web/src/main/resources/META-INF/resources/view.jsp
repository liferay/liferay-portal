<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-portlet:renderURL varImpl="searchURL" />

<clay:container-fluid
	cssClass="container-view"
>
	<aui:form action="<%= searchURL %>" method="get" name="fm">
		<liferay-portlet:renderURLParams varImpl="searchURL" />

		<liferay-ui:search-container
			searchContainer="<%= auditDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-form
				page="/event_search.jsp"
				servletContext="<%= application %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.portal.security.audit.AuditEvent"
				escapedModel="<%= true %>"
				keyPropertyName="auditEventId"
				modelVar="event"
			>
				<liferay-portlet:renderURL varImpl="rowURL">
					<portlet:param name="mvcPath" value="/view_audit_event.jsp" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="auditEventId" value="<%= String.valueOf(event.getAuditEventId()) %>" />
				</liferay-portlet:renderURL>

				<%@ include file="/search_columns.jspf" %>
			</liferay-ui:search-container-row>

			<hr class="separator" />

			<liferay-ui:search-iterator
				markupView="lexicon"
				searchContainer="<%= auditDisplayContext.getSearchContainer() %>"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>