<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
int oAuthClientEntriesCount = OAuthClientEntryLocalServiceUtil.getOAuthClientEntriesCount();

OAuthClientEntriesManagementToolbarDisplayContext oAuthClientEntriesManagementToolbarDisplayContext = new OAuthClientEntriesManagementToolbarDisplayContext(currentURLObj, liferayPortletRequest, liferayPortletResponse);
%>

<clay:management-toolbar
	actionDropdownItems="<%= oAuthClientEntriesManagementToolbarDisplayContext.getActionDropdownItems() %>"
	additionalProps="<%= oAuthClientEntriesManagementToolbarDisplayContext.getAdditionalProps() %>"
	creationMenu="<%= oAuthClientEntriesManagementToolbarDisplayContext.getCreationMenu() %>"
	disabled="<%= oAuthClientEntriesCount == 0 %>"
	itemsTotal="<%= oAuthClientEntriesCount %>"
	orderDropdownItems="<%= oAuthClientEntriesManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
	searchContainerId="oAuthClientEntrySearchContainer"
	selectable="<%= true %>"
	showCreationMenu="<%= true %>"
	showSearch="<%= false %>"
	sortingOrder="<%= oAuthClientEntriesManagementToolbarDisplayContext.getOrderByType() %>"
	sortingURL="<%= String.valueOf(oAuthClientEntriesManagementToolbarDisplayContext.getSortingURL()) %>"
	viewTypeItems="<%= oAuthClientEntriesManagementToolbarDisplayContext.getViewTypes() %>"
/>

<clay:container-fluid
	cssClass="closed"
>
	<liferay-ui:search-container
		emptyResultsMessage="no-oauth-clients-were-found"
		id="oAuthClientEntrySearchContainer"
		iteratorURL="<%= currentURLObj %>"
		rowChecker="<%= new EmptyOnClickRowChecker(renderResponse) %>"
		total="<%= oAuthClientEntriesCount %>"
	>
		<liferay-ui:search-container-results
			results="<%= OAuthClientEntryServiceUtil.getCompanyOAuthClientEntries(themeDisplay.getCompanyId()) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.oauth.client.persistence.model.OAuthClientEntry"
			escapedModel="<%= true %>"
			keyPropertyName="clientId"
			modelVar="oAuthClientEntry"
		>
			<portlet:renderURL var="editURL">
				<portlet:param name="mvcRenderCommandName" value="/oauth_client_admin/update_oauth_client_entry" />
				<portlet:param name="authServerWellKnownURI" value="<%= oAuthClientEntry.getAuthServerWellKnownURI() %>" />
				<portlet:param name="clientId" value="<%= oAuthClientEntry.getClientId() %>" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				href="<%= editURL %>"
				name="oauth-client-id"
				property="clientId"
			/>

			<liferay-ui:search-container-column-text
				name="oauth-client-as-well-known-uri"
				property="authServerWellKnownURI"
			/>

			<liferay-ui:search-container-column-jsp
				align="right"
				path="/admin/oauth_client_entry_actions.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>