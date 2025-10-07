<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
OAuth2ApplicationsDisplayContext oAuth2ApplicationsDisplayContext = new OAuth2ApplicationsDisplayContext(liferayPortletRequest, liferayPortletResponse);

OAuth2ApplicationsManagementToolbarDisplayContext oAuth2ApplicationsManagementToolbarDisplayContext = new OAuth2ApplicationsManagementToolbarDisplayContext(liferayPortletRequest, liferayPortletResponse, oAuth2ApplicationsDisplayContext.getSearchContainer());

String displayStyle = oAuth2ApplicationsManagementToolbarDisplayContext.getDisplayStyle();
%>

<liferay-ui:error embed="<%= false %>" exception="<%= RequiredOAuth2ApplicationException.class %>">

	<%
	RequiredOAuth2ApplicationException requiredOAuth2ApplicationException = (RequiredOAuth2ApplicationException)errorException;
	%>

	<liferay-ui:message key="<%= requiredOAuth2ApplicationException.getMessage() %>" />
</liferay-ui:error>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= oAuth2ApplicationsManagementToolbarDisplayContext %>"
	propsTransformer="{OAuth2ApplicationsManagementToolbarPropsTransformer} from oauth2-provider-web"
/>

<clay:container-fluid
	cssClass="closed"
>
	<aui:form action="<%= currentURLObj %>" method="get" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="oAuth2ApplicationIds" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= oAuth2ApplicationsDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.oauth2.provider.model.OAuth2Application"
				escapedModel="<%= true %>"
				keyProperty="OAuth2ApplicationId"
				modelVar="oAuth2Application"
			>
				<portlet:renderURL var="editURL">
					<portlet:param name="mvcRenderCommandName" value="/oauth2_provider/update_oauth2_application" />
					<portlet:param name="oAuth2ApplicationId" value="<%= String.valueOf(oAuth2Application.getOAuth2ApplicationId()) %>" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:renderURL>

				<c:choose>
					<c:when test='<%= displayStyle.equals("descriptive") %>'>

						<%
						row.setCssClass("autofit-row-center");
						%>

						<liferay-ui:search-container-column-image
							src="<%= oAuth2AdminPortletDisplayContext.getThumbnailURL(oAuth2Application) %>"
							toggleRowChecker="<%= true %>"
						/>

						<c:choose>
							<c:when test="<%= oAuth2AdminPortletDisplayContext.hasUpdatePermission(oAuth2Application) %>">
								<liferay-ui:search-container-column-text
									colspan="<%= 2 %>"
									href="<%= editURL %>"
									property="name"
								/>
							</c:when>
							<c:otherwise>
								<liferay-ui:search-container-column-text
									colspan="<%= 2 %>"
									property="name"
								/>
							</c:otherwise>
						</c:choose>

						<liferay-ui:search-container-column-jsp
							align="right"
							path="/admin/application_actions.jsp"
						/>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="<%= oAuth2AdminPortletDisplayContext.hasUpdatePermission(oAuth2Application) %>">
								<liferay-ui:search-container-column-text
									href="<%= editURL %>"
									property="name"
								/>
							</c:when>
							<c:otherwise>
								<liferay-ui:search-container-column-text
									property="name"
								/>
							</c:otherwise>
						</c:choose>

						<liferay-ui:search-container-column-text
							property="description"
						/>

						<liferay-ui:search-container-column-text
							name="client-id"
							property="clientId"
						/>

						<liferay-ui:search-container-column-text
							name="authorizations"
							value="<%= String.valueOf(oAuth2AdminPortletDisplayContext.getOAuth2AuthorizationsCount(oAuth2Application)) %>"
						/>

						<c:if test="<%= oAuth2AdminPortletDisplayContext.hasAddTrustedApplicationPermission() || oAuth2AdminPortletDisplayContext.hasRememberDevicePermission() %>">
							<liferay-ui:search-container-column-text
								name="extra-properties"
								value="<%= oAuth2AdminPortletDisplayContext.getExtraPropertiesContent(oAuth2Application) %>"
							/>
						</c:if>

						<liferay-ui:search-container-column-jsp
							align="right"
							path="/admin/application_actions.jsp"
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