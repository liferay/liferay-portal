<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
int oAuthClientASLocalMetadatasCount = OAuthClientASLocalMetadataLocalServiceUtil.getOAuthClientASLocalMetadatasCount();

OAuthClientASLocalMetadataManagementToolbarDisplayContext oAuthClientASLocalMetadataManagementToolbarDisplayContext = new OAuthClientASLocalMetadataManagementToolbarDisplayContext(currentURLObj, liferayPortletRequest, liferayPortletResponse);
%>

<clay:management-toolbar
	actionDropdownItems="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getActionDropdownItems() %>"
	additionalProps="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getAdditionalProps() %>"
	creationMenu="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getCreationMenu() %>"
	disabled="<%= oAuthClientASLocalMetadatasCount == 0 %>"
	itemsTotal="<%= oAuthClientASLocalMetadatasCount %>"
	orderDropdownItems="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
	searchContainerId="oAuthClientASLocalMetadataSearchContainer"
	selectable="<%= true %>"
	showCreationMenu="<%= true %>"
	showSearch="<%= false %>"
	sortingOrder="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getOrderByType() %>"
	sortingURL="<%= String.valueOf(oAuthClientASLocalMetadataManagementToolbarDisplayContext.getSortingURL()) %>"
	viewTypeItems="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getViewTypes() %>"
/>

<clay:container-fluid
	cssClass="closed"
>
	<liferay-ui:search-container
		emptyResultsMessage="no-oauth-client-as-local-metadata-were-found"
		id="oAuthClientASLocalMetadataSearchContainer"
		iteratorURL="<%= currentURLObj %>"
		rowChecker="<%= new EmptyOnClickRowChecker(renderResponse) %>"
		total="<%= oAuthClientASLocalMetadatasCount %>"
	>
		<liferay-ui:search-container-results
			results="<%= OAuthClientASLocalMetadataServiceUtil.getCompanyOAuthClientASLocalMetadata(themeDisplay.getCompanyId(), searchContainer.getStart(), searchContainer.getEnd()) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata"
			escapedModel="<%= true %>"
			keyProperty="localWellKnownURIOIC"
			modelVar="oAuthClientASLocalMetadata"
		>
			<portlet:renderURL var="editURL">
				<portlet:param name="mvcRenderCommandName" value="/oauth_client_admin/update_oauth_client_as_local_metadata" />
				<portlet:param name="localWellKnownURIOIC" value="<%= oAuthClientASLocalMetadata.getLocalWellKnownURIOIC() %>" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				href="<%= editURL %>"
				name="oauth-client-as-local-well-known-uri"
				property="localWellKnownURIOIC"
			/>

			<liferay-ui:search-container-column-jsp
				align="right"
				path="/admin/oauth_client_as_local_metadata_actions.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>