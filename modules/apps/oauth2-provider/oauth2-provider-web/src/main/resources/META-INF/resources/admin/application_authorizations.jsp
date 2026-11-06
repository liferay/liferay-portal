<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

OAuth2Application oAuth2Application = oAuth2AdminPortletDisplayContext.getOAuth2Application();

long oAuth2ApplicationId = oAuth2Application.getOAuth2ApplicationId();

OAuth2AuthorizationsDisplayContext oAuth2AuthorizationsDisplayContext = new OAuth2AuthorizationsDisplayContext(liferayPortletRequest, liferayPortletResponse, oAuth2ApplicationId);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new OAuth2AuthorizationsManagementToolbarDisplayContext(liferayPortletRequest, liferayPortletResponse, oAuth2ApplicationId, oAuth2AuthorizationsDisplayContext.getSearchContainer()) %>"
	propsTransformer="{OAuth2AuthorizationsManagementToolbarPropsTransformer} from oauth2-provider-web"
/>

<portlet:actionURL name="/admin/revoke_oauth2_authorizations" var="revokeOAuth2AuthorizationsURL">
	<portlet:param name="mvcRenderCommandName" value="/oauth2_provider/view_oauth2_authorizations" />
	<portlet:param name="navigation" value="application_authorizations" />
	<portlet:param name="backURL" value="<%= redirect %>" />
	<portlet:param name="oAuth2ApplicationId" value="<%= String.valueOf(oAuth2ApplicationId) %>" />
</portlet:actionURL>

<clay:container-fluid>
	<aui:form action="<%= revokeOAuth2AuthorizationsURL %>" name="fm">
		<aui:input name="oAuth2ApplicationId" type="hidden" value="<%= oAuth2ApplicationId %>" />
		<aui:input name="oAuth2AuthorizationIds" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= oAuth2AuthorizationsDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.oauth2.provider.model.OAuth2Authorization"
				escapedModel="<%= true %>"
				keyPropertyName="OAuth2AuthorizationId"
				modelVar="oAuth2Authorization"
			>
				<liferay-ui:search-container-column-text
					property="userId"
				/>

				<liferay-ui:search-container-column-text
					property="userName"
				/>

				<liferay-ui:search-container-column-date
					name="authorization"
					property="createDate"
				/>

				<liferay-ui:search-container-column-date
					name="last-access"
					property="accessTokenCreateDate"
				/>

				<%
				Date expirationDate = oAuth2Authorization.getRefreshTokenExpirationDate();

				Date accessTokenExpirationDate = oAuth2Authorization.getAccessTokenExpirationDate();

				if ((expirationDate == null) || expirationDate.before(accessTokenExpirationDate)) {
					expirationDate = accessTokenExpirationDate;
				}
				%>

				<liferay-ui:search-container-column-date
					name="expiration"
					value="<%= expirationDate %>"
				/>

				<liferay-ui:search-container-column-text
					name="remoteIPInfo"
					value='<%= oAuth2Authorization.getRemoteIPInfo() + ", " + oAuth2Authorization.getRemoteHostInfo() %>'
				/>

				<liferay-ui:search-container-column-jsp
					align="right"
					path="/admin/application_authorization_actions.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
				searchContainer="<%= searchContainer %>"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<aui:script>
	function <portlet:namespace />revokeOAuth2Authorization(oAuth2AuthorizationId) {
		Liferay.Util.openConfirmModal({
			message:
				'<%= UnicodeLanguageUtil.get(request, "are-you-sure-you-want-to-revoke-the-authorization") %>',
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					const form = document.<portlet:namespace />fm;

					Liferay.Util.postForm(form, {
						data: {
							oAuth2AuthorizationIds: oAuth2AuthorizationId,
						},
						url: '<%= revokeOAuth2AuthorizationsURL %>',
					});
				}
			},
		});
	}
</aui:script>