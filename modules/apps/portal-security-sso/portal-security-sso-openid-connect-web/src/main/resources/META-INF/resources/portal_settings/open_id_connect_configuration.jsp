<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
OpenIdConnectProviderConfigurationDisplayContext openIdConnectProviderConfigurationDisplayContext = (OpenIdConnectProviderConfigurationDisplayContext)request.getAttribute(OpenIdConnectWebKeys.OPEN_ID_CONNECT_PROVIDER_CONFIGURATION_DISPLAY_CONTEXT);
%>

<aui:input helpMessage="provider-name-help" id="providerName" label="provider-name" name="providerName" required="<%= true %>" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getProviderName() %>" />

<aui:input helpMessage="scopes-help" id="scopes" label="scopes" name="scopes" required="<%= true %>" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getScopes() %>" />

<aui:input helpMessage="discovery-endpoint-help" label="discovery-endpoint" name="discoveryEndPoint" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getDiscoveryEndPoint() %>" />

<aui:input helpMessage="discovery-endpoint-cache-help" label="discovery-endpoint-cache-in-millis" name="discoveryEndPointCacheInMillis" type="number" value="<%= openIdConnectProviderConfigurationDisplayContext.getDiscoveryEndPointCacheInMillis() %>" />

<aui:input helpMessage="authorization-endpoint-help" label="authorization-endpoint" name="authorizationEndPoint" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getAuthorizationEndPoint() %>" />

<aui:input helpMessage="issuer-url-help" label="issuer-url" name="issuerURL" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getIssuerURL() %>" />

<aui:input helpMessage="jwks-uri-help" label="jwks-uri" name="jwksURI" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getJwksURI() %>" />

<aui:fieldset helpMessage="id-token-signing-alg-values-help" id='<%= liferayPortletResponse.getNamespace() + "idTokenSigningAlgValuesContentBox" %>' label="id-token-signing-alg-values">

	<%
	int[] idTokenSigningAlgValuesIndexes = openIdConnectProviderConfigurationDisplayContext.getIdTokenSigningAlgValuesIndexes();

	for (int i = 0; i < idTokenSigningAlgValuesIndexes.length; i++) {
		int index = i;

		String fieldId = "idTokenSigningAlgValues-" + index;
	%>

		<div class="form-group-autofit lfr-form-row user-attribute-mapping-row">
			<div class="form-group-item">
				<aui:input fieldParam="<%= fieldId %>" id="<%= fieldId %>" label="id-token-signing-alg-value" name="<%= fieldId %>" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getIdTokenSigningAlgValues()[i] %>" />
			</div>
		</div>

	<%
	}
	%>

	<aui:input name="idTokenSigningAlgValuesIndexes" type="hidden" value="<%= StringUtil.merge(idTokenSigningAlgValuesIndexes) %>" />
</aui:fieldset>

<aui:fieldset helpMessage="subject-types-help" id='<%= liferayPortletResponse.getNamespace() + "subjectTypesContentBox" %>' label="subject-types">

	<%
	int[] subjectTypesIndexes = openIdConnectProviderConfigurationDisplayContext.getSubjectTypesIndexes();

	for (int i = 0; i < subjectTypesIndexes.length; i++) {
		int index = i;

		String fieldId = "subjectTypes-" + index;
	%>

		<div class="form-group-autofit lfr-form-row user-attribute-mapping-row">
			<div class="form-group-item">
				<aui:input fieldParam="<%= fieldId %>" id="<%= fieldId %>" label="subject-type" name="<%= fieldId %>" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getSubjectTypes()[i] %>" />
			</div>
		</div>

	<%
	}
	%>

	<aui:input name="subjectTypesIndexes " type="hidden" value="<%= StringUtil.merge(subjectTypesIndexes) %>" />
</aui:fieldset>

<aui:input helpMessage="token-endpoint-help" label="token-endpoint" name="tokenEndPoint" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getTokenEndPoint() %>" />

<aui:input helpMessage="token-connection-timeout-help" label="token-connection-timeout" name="tokenConnectionTimeout" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getTokenConnectionTimeout() %>" />

<aui:input helpMessage="user-info-endpoint-help" label="user-info-endpoint" name="userInfoEndPoint" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getUserInfoEndPoint() %>" />

<aui:input helpMessage="open-id-connect-client-id-help" label="open-id-connect-client-id" name="openIdConnectClientId" required="<%= true %>" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getOpenIdConnectClientId() %>" />

<aui:input helpMessage="open-id-connect-client-secret-help" label="open-id-connect-client-secret" name="openIdConnectClientSecret" required="<%= true %>" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getOpenIdConnectClientSecret() %>" />

<aui:input helpMessage="registered-id-token-signing-alg-help" label="registered-id-token-signing-alg" name="registeredIdTokenSigningAlg" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getRegisteredIdTokenSigningAlg() %>" />

<aui:fieldset helpMessage="custom-authorization-request-parameters-help" id='<%= liferayPortletResponse.getNamespace() + "customAuthorizationRequestParametersContentBox" %>' label="custom-authorization-request-parameters">

	<%
	int[] customAuthorizationRequestParametersIndexes = openIdConnectProviderConfigurationDisplayContext.getCustomAuthorizationRequestParametersIndexes();

	for (int i = 0; i < customAuthorizationRequestParametersIndexes.length; i++) {
		int index = i;

		String fieldId = "customAuthorizationRequestParameters-" + index;
	%>

		<div class="form-group-autofit lfr-form-row user-attribute-mapping-row">
			<div class="form-group-item">
				<aui:input fieldParam="<%= fieldId %>" id="<%= fieldId %>" label="custom-authorization-request-parameter" name="<%= fieldId %>" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getCustomAuthorizationRequestParameters()[i] %>" />
			</div>
		</div>

	<%
	}
	%>

	<aui:input name="customAuthorizationRequestParametersIndexes" type="hidden" value="<%= StringUtil.merge(customAuthorizationRequestParametersIndexes) %>" />
</aui:fieldset>

<aui:fieldset helpMessage="custom-token-request-parameters-help" id='<%= liferayPortletResponse.getNamespace() + "customTokenRequestParametersContentBox" %>' label="custom-token-request-parameters">

	<%
	int[] customTokenRequestParametersIndexes = openIdConnectProviderConfigurationDisplayContext.getCustomTokenRequestParametersIndexes();

	for (int i = 0; i < customTokenRequestParametersIndexes.length; i++) {
		int index = i;

		String fieldId = "customTokenRequestParameters-" + index;
	%>

		<div class="form-group-autofit lfr-form-row user-attribute-mapping-row">
			<div class="form-group-item">
				<aui:input fieldParam="<%= fieldId %>" id="<%= fieldId %>" label="custom-token-request-parameter" name="<%= fieldId %>" type="text" value="<%= openIdConnectProviderConfigurationDisplayContext.getCustomTokenRequestParameters()[i] %>" />
			</div>
		</div>

	<%
	}
	%>

	<aui:input name="customTokenRequestParametersIndexes" type="hidden" value="<%= StringUtil.merge(customTokenRequestParametersIndexes) %>" />
</aui:fieldset>

<aui:script use="liferay-auto-fields">
	new Liferay.AutoFields({
		contentBox:
			'#<portlet:namespace />customAuthorizationRequestParametersContentBox',
		fieldIndexes:
			'<portlet:namespace />customAuthorizationRequestParametersIndexes',
		namespace: '<portlet:namespace />',
	}).render();

	new Liferay.AutoFields({
		contentBox: '#<portlet:namespace />customTokenRequestParametersContentBox',
		fieldIndexes: '<portlet:namespace />customTokenRequestParametersIndexes',
		namespace: '<portlet:namespace />',
	}).render();

	new Liferay.AutoFields({
		contentBox: '#<portlet:namespace />idTokenSigningAlgValuesContentBox',
		fieldIndexes: '<portlet:namespace />idTokenSigningAlgValuesIndexes',
		namespace: '<portlet:namespace />',
	}).render();

	new Liferay.AutoFields({
		contentBox: '#<portlet:namespace />subjectTypesContentBox',
		fieldIndexes: '<portlet:namespace />subjectTypesIndexes ',
		namespace: '<portlet:namespace />',
	}).render();
</aui:script>