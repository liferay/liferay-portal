<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/connected_applications/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

AssignableScopes assignableScopes = oAuth2ConnectedApplicationsPortletDisplayContext.getAssignableScopes();
OAuth2Authorization oAuth2Authorization = oAuth2ConnectedApplicationsPortletDisplayContext.getOAuth2Authorization();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

OAuth2Application oAuth2Application = oAuth2ConnectedApplicationsPortletDisplayContext.getOAuth2Application();

renderResponse.setTitle(oAuth2Application.getName());
%>

<clay:container-fluid
	cssClass="view-application"
>
	<portlet:actionURL name="/connected_applications/revoke_oauth2_authorizations" var="revokeOAuth2AuthorizationURL" />

	<aui:form action="<%= revokeOAuth2AuthorizationURL %>" method="post" name="fm">
		<aui:input name="backURL" type="hidden" value="<%= redirect %>" />
		<aui:input name="mvcRenderCommandName" type="hidden" value="/oauth2_provider/view_connected_applications" />
		<aui:input name="oAuth2AuthorizationIds" type="hidden" value='<%= ParamUtil.getString(request, "oAuth2AuthorizationId") %>' />

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<div class="panel-body">
					<liferay-ui:icon
						cssClass="app-icon"
						src="<%= oAuth2ConnectedApplicationsPortletDisplayContext.getThumbnailURL() %>"
					/>

					<h1 class="name">
						<%= HtmlUtil.escape(oAuth2Application.getName()) %>
					</h1>

					<p class="description text-truncate">
						<%= HtmlUtil.escape(oAuth2Application.getDescription()) %>
					</p>

					<c:if test="<%= !Validator.isBlank(oAuth2Application.getHomePageURL()) || !Validator.isBlank(oAuth2Application.getPrivacyPolicyURL()) %>">
						<p class="application-information text-truncate">
							<span><liferay-ui:message key="application-information" /></span>:

							<c:if test="<%= !Validator.isBlank(oAuth2Application.getHomePageURL()) %>">
								<aui:a href="<%= HtmlUtil.escapeJSLink(oAuth2Application.getHomePageURL()) %>" label="website" target="_blank" />
							</c:if>

							<c:if test="<%= !Validator.isBlank(oAuth2Application.getPrivacyPolicyURL()) %>">
								<c:if test="<%= !Validator.isBlank(oAuth2Application.getHomePageURL()) %>">
									<%= StringUtil.toLowerCase(LanguageUtil.get(request, "and")) %>
								</c:if>

								<aui:a href="<%= HtmlUtil.escapeJSLink(oAuth2Application.getPrivacyPolicyURL()) %>" label="privacy-policy" target="_blank" />
							</c:if>
						</p>
					</c:if>

					<div class="h4 permissions">
						<liferay-ui:message key="permissions" />
					</div>

					<ul class="list-group">

						<%
						for (String applicationName : assignableScopes.getApplicationNames()) {
						%>

							<li class="list-group-item list-group-item-flex">
								<clay:content-col>
									<clay:icon
										symbol="check"
									/>
								</clay:content-col>

								<clay:content-col
									expand="<%= true %>"
								>
									<div class="list-group-title text-truncate"><%= HtmlUtil.escape(assignableScopes.getApplicationDescription(applicationName)) %></div>

									<p class="list-group-subtitle text-truncate"><%= HtmlUtil.escape(StringUtil.merge(assignableScopes.getApplicationScopeDescription(themeDisplay.getCompanyId(), applicationName), ", ")) %></p>
								</clay:content-col>
							</li>

						<%
						}
						%>

					</ul>

					<div class="activity h4">
						<liferay-ui:message key="activity" />
					</div>

					<p class="last-access text-truncate">
						<span><liferay-ui:message key="last-access" /></span>:
						<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - oAuth2Authorization.getAccessTokenCreateDate().getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
					</p>

					<p class="authorization text-truncate">
						<span><liferay-ui:message key="authorization" /></span>:
						<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - oAuth2Authorization.getCreateDate().getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
					</p>

					<p class="authorization text-truncate">
						<span><liferay-ui:message key="remoteIPInfo" /></span>:
						<%= HtmlUtil.escape(oAuth2Authorization.getRemoteIPInfo()) %>, <%= HtmlUtil.escape(oAuth2Authorization.getRemoteHostInfo()) %>
					</p>

					<%
					Date expirationDate = oAuth2Authorization.getRefreshTokenExpirationDate();

					Date accessTokenExpirationDate = oAuth2Authorization.getAccessTokenExpirationDate();

					if ((expirationDate == null) || expirationDate.before(accessTokenExpirationDate)) {
						expirationDate = accessTokenExpirationDate;
					}
					%>

					<p class="authorization text-truncate">
						<span><liferay-ui:message key="expiration" /></span>:
						<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(locale, Math.abs(System.currentTimeMillis() - expirationDate.getTime()), true) %>" key='<%= expirationDate.before(new Date()) ? "x-ago" : "within-x" %>' translateArguments="<%= false %>" />
					</p>

					<p class="buttons">
						<aui:button cssClass="remove-access" id="removeAccess" value="remove-access" />
						<aui:button href="<%= PortalUtil.escapeRedirect(redirect) %>" value="cancel" />
					</p>
				</div>
			</div>
		</div>
	</aui:form>
</clay:container-fluid>

<aui:script>
	var removeAccessButton = document.getElementById(
		'<portlet:namespace />removeAccess'
	);

	if (removeAccessButton) {
		removeAccessButton.addEventListener('click', () => {
			Liferay.Util.openConfirmModal({
				message:
					'<%= UnicodeLanguageUtil.format(request, "x-will-no-longer-have-access-to-your-account-removed-access-cannot-be-recovered", new String[] {oAuth2Application.getName()}) %>',
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						submitForm(document.<portlet:namespace />fm);
					}
				},
			});
		});
	}
</aui:script>