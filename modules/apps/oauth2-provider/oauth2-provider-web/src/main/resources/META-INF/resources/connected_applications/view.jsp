<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/connected_applications/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL", redirect);

OAuth2ConnectedApplicationsDisplayContext oAuth2ConnectedApplicationsDisplayContext = new OAuth2ConnectedApplicationsDisplayContext(liferayPortletRequest, liferayPortletResponse);

if (Validator.isNotNull(backURL)) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(backURL);
}
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new OAuth2ConnectedApplicationsManagementToolbarDisplayContext(liferayPortletRequest, liferayPortletResponse, oAuth2ConnectedApplicationsDisplayContext.getSearchContainer()) %>"
	propsTransformer="{oAuth2ConnectedApplicationsManagementToolbarPropsTransformer} from oauth2-provider-web"
/>

<clay:container-fluid>
	<aui:form action="<%= currentURLObj %>" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="oAuth2AuthorizationIds" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= oAuth2ConnectedApplicationsDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.oauth2.provider.model.OAuth2Authorization"
				escapedModel="<%= true %>"
				keyPropertyName="OAuth2AuthorizationId"
				modelVar="oAuth2Authorization"
			>

				<%
				OAuth2Application oAuth2Application = OAuth2ApplicationLocalServiceUtil.getOAuth2Application(oAuth2Authorization.getOAuth2ApplicationId());
				%>

				<liferay-ui:search-container-column-image
					src="<%= oAuth2ConnectedApplicationsPortletDisplayContext.getThumbnailURL(oAuth2Application) %>"
					toggleRowChecker="<%= true %>"
				/>

				<portlet:renderURL var="viewURL">
					<portlet:param name="mvcRenderCommandName" value="/oauth2_provider/view_connected_applications" />
					<portlet:param name="oAuth2ApplicationId" value="<%= String.valueOf(oAuth2Authorization.getOAuth2ApplicationId()) %>" />
					<portlet:param name="oAuth2AuthorizationId" value="<%= String.valueOf(oAuth2Authorization.getOAuth2AuthorizationId()) %>" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:renderURL>

				<liferay-ui:search-container-column-text
					colspan="<%= 2 %>"
				>
					<div class="h4">
						<aui:a href="<%= viewURL.toString() %>"><%= HtmlUtil.escape(oAuth2Application.getName()) %></aui:a>
					</div>

					<div class="h5 text-default">
						<span><liferay-ui:message key="authorization" /></span>:
						<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - oAuth2Authorization.getCreateDate().getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
					</div>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-jsp
					align="right"
					path="/connected_applications/authorization_actions.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="descriptive"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<aui:script>
	function <portlet:namespace />removeAccess() {}
</aui:script>