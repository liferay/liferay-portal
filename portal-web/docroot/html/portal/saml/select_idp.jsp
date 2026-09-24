<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/portal/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String samlIdPRedirectMessage = GetterUtil.getString(request.getAttribute("SAML_IDP_REDIRECT_MESSAGE"));

JSONObject samlSsoLoginContextJSONObject = (JSONObject)request.getAttribute("SAML_SSO_LOGIN_CONTEXT");

JSONArray relevantIdpConnectionsJSONArray = samlSsoLoginContextJSONObject.getJSONArray("relevantIdpConnections");
%>

<aui:form action='<%= PortalUtil.getPortalURL(request) + PortalUtil.getPathMain() + "/portal/login" %>' cssClass="p-3" method="get" name="fm">
	<aui:input name="saveLastPath" type="hidden" value="<%= false %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<c:choose>
		<c:when test="<%= relevantIdpConnectionsJSONArray.length() == 1 %>">
			<p><%= samlIdPRedirectMessage %></p>

			<%
			JSONObject relevantIdpConnectionJSONObject = relevantIdpConnectionsJSONArray.getJSONObject(0);
			%>

			<aui:input name="idpEntityId" type="hidden" value='<%= relevantIdpConnectionJSONObject.getString("entityId") %>' />

			<aui:script sandbox="<%= true %>">
				window.addEventListener("load", (event) => {
					window.fm.submit();
				});
			</aui:script>
		</c:when>
		<c:when test="<%= relevantIdpConnectionsJSONArray.length() > 1 %>">
			<p><liferay-ui:message key="please-select-your-identity-provider" /></p>

			<aui:select label="identity-provider" name="idpEntityId">

				<%
				for (int i = 0; i < relevantIdpConnectionsJSONArray.length(); i++) {
					JSONObject relevantIdpConnectionJSONObject = relevantIdpConnectionsJSONArray.getJSONObject(i);

					String entityId = relevantIdpConnectionJSONObject.getString("entityId");
					String name = relevantIdpConnectionJSONObject.getString("name");
				%>

					<aui:option label="<%= HtmlUtil.escape(name) %>" localizeLabel="<%= false %>" value="<%= entityId %>" />

				<%
				}
				%>

			</aui:select>

			<aui:fieldset>
				<aui:button-row>
					<aui:button type="submit" value="sign-in" />
				</aui:button-row>
			</aui:fieldset>
		</c:when>
		<c:otherwise>
			<liferay-ui:message key="no-identity-provider-is-available-to-sign-you-in" />
		</c:otherwise>
	</c:choose>
</aui:form>

<c:if test="<%= Validator.isNotNull(redirect) %>">
	<aui:script sandbox="<%= true %>">
		var form = document.getElementById('<portlet:namespace />fm');

		var redirect = form.querySelector('#<portlet:namespace />redirect');

		if (redirect) {
			var redirectVal = redirect.getAttribute('value');

			redirect.setAttribute('value', redirectVal + window.location.hash);
		}
	</aui:script>
</c:if>