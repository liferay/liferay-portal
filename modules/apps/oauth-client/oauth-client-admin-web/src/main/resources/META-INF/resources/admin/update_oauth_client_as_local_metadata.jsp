<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

OAuthClientASLocalMetadata oAuthClientASLocalMetadata = (OAuthClientASLocalMetadata)request.getAttribute(OAuthClientASLocalMetadata.class.getName());

String authorizationEndpoint = (String)request.getAttribute("authorization_endpoint");
String jwksUri = (String)request.getAttribute("jwks_uri");
String supportedGrantTypes = (String)request.getAttribute("supported-grant-types");
String supportedScopes = (String)request.getAttribute("supported-scopes");
String supportedSubjectTypes = (String)request.getAttribute("supported_subject_types");
String tokenEndpoint = (String)request.getAttribute("token_endpoint");
String userinfoEndpoint = (String)request.getAttribute("userinfo_endpoint");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle((oAuthClientASLocalMetadata == null) ? LanguageUtil.get(request, "new-oauth-client-as-local-metadata") : LanguageUtil.get(request, "edit-oauth-client-as-local-metadata"));
%>

<portlet:actionURL name="/oauth_client_admin/update_oauth_client_as_local_metadata" var="updateOAuthClientASLocalMetadataURL">
	<portlet:param name="mvcRenderCommandName" value="/oauth_client_admin/update_oauth_client_as_local_metadata" />
</portlet:actionURL>

<aui:form action="<%= updateOAuthClientASLocalMetadataURL %>" id="oauth-client-as-fm" method="post" name="oauth-client-as-fm" onSubmit="event.preventDefault();">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<aui:model-context bean="<%= oAuthClientASLocalMetadata %>" model="<%= OAuthClientASLocalMetadata.class %>" />

	<clay:container-fluid
		cssClass="container-view"
	>
		<div class="sheet">
			<aui:fieldset>
				<liferay-ui:error exception="<%= DuplicateOAuthClientASLocalMetadataException.class %>" message="oauth-client-as-local-metadata-duplicate-as-local-metadata" />

				<liferay-ui:error exception="<%= DuplicateOAuthClientASIssuerException.class %>" message="oauth-client-as-local-metadata-invalid-local-well-known-uri" />

				<liferay-ui:error exception="<%= OAuthClientASLocalMetadataLocalWellKnownURIException.class %>" message="oauth-client-as-local-metadata-invalid-local-well-known-uri" />

				<liferay-ui:error exception="<%= OAuthClientASLocalMetadataJSONException.class %>">
					<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuthClientASLocalMetadataJSONException)errorException).getMessage()) %>" key="oauth-client-as-local-metadata-invalid-metadata-json-x" />
				</liferay-ui:error>

				<aui:fieldset label="general">
					<aui:input helpMessage="oauth-client-as-local-metadata-issuer-help" label="oauth-client-as-local-metadata-issuer" name="issuer" required="<%= true %>" type="text" />
					<aui:input helpMessage="oauth-client-as-local-metadata-allowed-scopes-help" label="oauth-client-as-local-metadata-allowed-scopes" name="supported-scopes" type="text" value="<%= supportedScopes %>" />

					<aui:input helpMessage="oauth-client-as-local-metadata-allowed-grant-types-help" label="oauth-client-as-local-metadata-allowed-grant-types" name="supported-grant-types" type="text" value="<%= supportedGrantTypes %>" />

					<aui:input label="oauth-client-as-local-metadata-authorization_endpoint" name="authorization_endpoint" type="text" value="<%= authorizationEndpoint %>" />

					<aui:input label="oauth-client-as-local-metadata-jwks_uri" name="jwks_uri" type="text" value="<%= jwksUri %>" />

					<aui:input label="oauth-client-as-local-metadata-token_endpoint" name="token_endpoint" type="text" value="<%= tokenEndpoint %>" />
				</aui:fieldset>

				<aui:fieldset label="oauth-client-as-local-oauth-authorization-server">
					<aui:input checked="<%= (oAuthClientASLocalMetadata != null) ? oAuthClientASLocalMetadata.getLocalWellKnownEnabled() : false %>" label="enable" name="enabled" type="checkbox" />

					<aui:input helpMessage="oauth-client-as-local-well-known-uri-oauth-authorization-server-help" label="oauth-client-as-local-well-known-uri-oauth-authorization-server" name="localWellKnownURIOAS" readonly="true" type="text" />

					<aui:input
						helpMessage="oauth-client-as-local-metadata-json-oauth-authorization-server-help"
						label="oauth-client-as-local-metadata-json-oauth-authorization-server"
						name="metadataJSONOAS"
						readonly="true"
						style="min-height: 600px;"
						type="textarea"
						value='<%=
							JSONUtil.put(
								"authorization_endpoint", ""
							).put(
								"issuer", ""
							).put(
								"jwks_uri", ""
							).put(
								"token_endpoint", ""
							)
						%>'
					/>
				</aui:fieldset>

				<aui:fieldset label="oauth-client-as-local--openid-configuration">
					<aui:input label="oauth-client-as-local-metadata-subject_types_supported" name="supported_subject_types" type="text" value="<%= supportedSubjectTypes %>" />

					<aui:input label="oauth-client-as-local-metadata-userinfo_endpoint" name="userinfo_endpoint" type="text" value="<%= userinfoEndpoint %>" />

					<aui:input helpMessage="oauth-client-as-local-well-known-uri-openid-configuration-help" label="oauth-client-as-local-well-known-uri-openid-configuration" name="localWellKnownURIOIC" readonly="true" type="text" />

					<aui:input
						helpMessage="oauth-client-as-local-metadata-json-openid-configuration-help"
						label="oauth-client-as-local-metadata-json-openid-configuration"
						name="metadataJSONOIC"
						readonly="true"
						style="min-height: 600px;"
						type="textarea"
						value='<%=
							JSONUtil.put(
								"authorization_endpoint", ""
							).put(
								"issuer", ""
							).put(
								"jwks_uri", ""
							).put(
								"subject_types_supported", JSONUtil.put("public")
							).put(
								"token_endpoint", ""
							).put(
								"userinfo_endpoint", ""
							)
						%>'
					/>
				</aui:fieldset>

				<aui:button-row>
					<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "doSubmit();" %>' type="submit" />
					<aui:button href="<%= redirect %>" type="cancel" />
				</aui:button-row>
			</aui:fieldset>
		</div>
	</clay:container-fluid>
</aui:form>

<aui:script>
	<portlet:namespace />init();

	function <portlet:namespace />doSubmit() {
		var form = document.getElementById(
			'<portlet:namespace />oauth-client-as-fm'
		);

		var metadataJSONOAS = document.getElementById(
			'<portlet:namespace />metadataJSONOAS'
		).value;

		try {
			metadataJSONOAS = JSON.stringify(JSON.parse(metadataJSONOAS), null, 0);
		}
		catch (e) {
			alert('Ill-formatted Metadata JSON');
			return;
		}

		document.getElementById('<portlet:namespace />metadataJSONOAS').value =
			metadataJSONOAS;

		var metadataJSONOIC = document.getElementById(
			'<portlet:namespace />metadataJSONOIC'
		).value;

		try {
			metadataJSONOIC = JSON.stringify(JSON.parse(metadataJSONOIC), null, 0);
		}
		catch (e) {
			alert('Ill-formatted Metadata JSON');
			return;
		}

		document.getElementById('<portlet:namespace />metadataJSONOIC').value =
			metadataJSONOIC;

		submitForm(form);
	}

	function <portlet:namespace />init() {
		var metadataJSONOAS = document.getElementById(
			'<portlet:namespace />metadataJSONOAS'
		);

		metadataJSONOAS.value = JSON.stringify(
			JSON.parse(metadataJSONOAS.value),
			null,
			4
		);

		var metadataJSONOIC = document.getElementById(
			'<portlet:namespace />metadataJSONOIC'
		);

		metadataJSONOIC.value = JSON.stringify(
			JSON.parse(metadataJSONOIC.value),
			null,
			4
		);
	}
</aui:script>