<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<clay:container-fluid
	cssClass="closed"
>
	<liferay-ui:search-container
		emptyResultsMessage="no-oauth-client-as-local-metadata-were-found"
		id="oAuthClientASLocalMetadataSearchContainer"
		iteratorURL="<%= currentURLObj %>"
		rowChecker="<%= new EmptyOnClickRowChecker(renderResponse) %>"
		total="<%= OAuthClientASLocalMetadataLocalServiceUtil.getOAuthClientASLocalMetadatasCount() %>"
	>
		<liferay-ui:search-container-results
			results="<%= OAuthClientASLocalMetadataServiceUtil.getCompanyOAuthClientASLocalMetadata(themeDisplay.getCompanyId(), searchContainer.getStart(), searchContainer.getEnd()) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata"
			escapedModel="<%= true %>"
			keyProperty="localWellKnownURIOAS"
			modelVar="oAuthClientASLocalMetadata"
		>
			<portlet:renderURL var="editURL">
				<portlet:param name="mvcRenderCommandName" value="/oauth_client_admin/update_oauth_client_as_local_metadata" />
				<portlet:param name="issuer" value="<%= oAuthClientASLocalMetadata.getIssuer() %>" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				href="<%= editURL %>"
				name="oauth-client-as-local-well-known-uri"
				property="localWellKnownURIOAS"
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