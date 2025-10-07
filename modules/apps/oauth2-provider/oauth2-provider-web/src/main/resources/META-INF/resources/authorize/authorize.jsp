<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/authorize/init.jsp" %>

<%
AssignableScopes assignableScopes = oAuth2AuthorizePortletDisplayContext.getAssignableScopes();

OAuth2Application oAuth2Application = oAuth2AuthorizePortletDisplayContext.getOAuth2Application();

Map<String, String> oAuth2Parameters = oAuth2AuthorizePortletDisplayContext.getOAuth2Parameters();

String replyTo = oAuth2Parameters.get("reply_to");

if (Validator.isNotNull(replyTo) && !replyTo.startsWith(PortalUtil.getPortalURL(request))) {
	replyTo = PortalUtil.escapeRedirect(replyTo);
}
%>

<aui:style type="text/css">
	.bg-img-app {
		background-image: url('<%= HtmlUtil.escapeAttribute(oAuth2AuthorizePortletDisplayContext.getThumbnailURL()) %>');
	}
</aui:style>

<clay:container-fluid
	cssClass="closed consent"
>
	<aui:form action="<%= replyTo %>" data-senna-off="true" method="post" name="fm">
		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<div class="panel-body">
					<div class="app-icon aspect-ratio-bg-cover bg-img-app"></div>

					<liferay-user:user-portrait
						user="<%= user %>"
					/>

					<c:choose>
						<c:when test="<%= oAuth2Application == null %>">
							<h1>
								<liferay-ui:message key="authorize-oauth2-application" />
							</h1>
						</c:when>
						<c:when test="<%= !oAuth2AuthorizePortletDisplayContext.hasCreateTokenApplicationPermission(oAuth2Application) %>">
							<h1>
								<liferay-ui:message key="you-do-not-have-permissions-to-authorize-the-application" />
							</h1>
						</c:when>
						<c:otherwise>
							<h1>
								<%= LanguageUtil.format(request, "authorize-x", new String[] {oAuth2Application.getName()}) %>
							</h1>

							<p class="application-wants-permissions text-truncate">
								<liferay-ui:message key="this-application-wants-the-following-permissions" />
							</p>

							<ul class="list-group">

								<%
								for (String applicationName : assignableScopes.getApplicationNames()) {
									String[] messageArguments = new String[2];

									messageArguments[0] = StringPool.BLANK;

									for (String getApplicationScopeDescription : assignableScopes.getApplicationScopeDescription(themeDisplay.getCompanyId(), applicationName)) {
										if (Validator.isBlank(messageArguments[0])) {
											messageArguments[0] = getApplicationScopeDescription;

											continue;
										}

										messageArguments[1] = getApplicationScopeDescription;
										messageArguments[0] = LanguageUtil.format(request, "x-y", messageArguments);
									}

									messageArguments[1] = messageArguments[0];
									messageArguments[0] = HtmlUtil.escape(assignableScopes.getApplicationDescription(applicationName));
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
											<liferay-ui:message arguments="<%= messageArguments %>" key="for-x-y" />
										</clay:content-col>
									</li>

								<%
								}
								%>

							</ul>

							<c:if test="<%= oAuth2Application.isRememberDevice() %>">
								<aui:field-wrapper>
									<aui:input checked="<%= false %>" helpMessage="remember-device-help" id="rememberDevice" label="remember-device" name="rememberDevice" type="checkbox" />
								</aui:field-wrapper>
							</c:if>

							<c:if test="<%= !Validator.isBlank(oAuth2Application.getPrivacyPolicyURL()) %>">
								<p class="privacy-policy text-truncate">
									<liferay-ui:message key="application" />

									<aui:a href="<%= HtmlUtil.escapeJSLink(oAuth2Application.getPrivacyPolicyURL()) %>" target="_blank">
										<liferay-ui:message key="privacy-policy" />
									</aui:a>
								</p>
							</c:if>

							<div class="buttons">

								<%
								for (String paramName : oAuth2Parameters.keySet()) {
									if (paramName.equals("reply_to")) {
										continue;
									}
								%>

									<aui:input name="<%= HtmlUtil.escapeAttribute(paramName) %>" type="hidden" useNamespace="<%= false %>" value="<%= oAuth2Parameters.get(paramName) %>" />

								<%
								}
								%>

								<aui:input name="oauthDecision" type="hidden" useNamespace="<%= false %>" value="deny" />

								<aui:button id="allow" value="authorize" />

								<aui:button id="cancel" type="submit" value="cancel" />

								<aui:script>
									var allowButton = document.getElementById('<portlet:namespace />allow');

									if (allowButton) {
										allowButton.addEventListener('click', () => {
											document.getElementById('oauthDecision').value = 'allow';
											Liferay.Util.postForm(document.<portlet:namespace />fm);
										});
									}

									var cancelButton = document.getElementById('<portlet:namespace />cancel');

									if (cancelButton) {
										cancelButton.addEventListener('click', () => {
											document.getElementById('oauthDecision').value = 'deny';
											Liferay.Util.postForm(document.<portlet:namespace />fm);
										});
									}
								</aui:script>
							</div>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</aui:form>
</clay:container-fluid>