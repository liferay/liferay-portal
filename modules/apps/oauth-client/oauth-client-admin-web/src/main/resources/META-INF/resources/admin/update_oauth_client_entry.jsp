<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

OAuthClientEntry oAuthClientEntry = (OAuthClientEntry)request.getAttribute(OAuthClientEntry.class.getName());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle((oAuthClientEntry == null) ? LanguageUtil.get(request, "new-oauth-client") : LanguageUtil.get(request, "edit-oauth-client"));
%>

<portlet:actionURL name="/oauth_client_admin/update_oauth_client_entry" var="updateOAuthClientEntryURL">
	<portlet:param name="mvcRenderCommandName" value="/oauth_client_admin/update_oauth_client_entry" />
</portlet:actionURL>

<aui:form action="<%= updateOAuthClientEntryURL %>" id="oauth-client-entry-fm" method="post" name="oauth-client-entry-fm" onSubmit="event.preventDefault();">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<aui:model-context bean="<%= oAuthClientEntry %>" model="<%= OAuthClientEntry.class %>" />

	<clay:container-fluid
		cssClass="container-view"
	>
		<div class="sheet">
			<aui:fieldset>
				<liferay-ui:error exception="<%= DuplicateOAuthClientEntryException.class %>" message="oauth-client-duplicate-client" />

				<liferay-ui:error exception="<%= OAuthClientEntryAuthRequestParametersJSONException.class %>">
					<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuthClientEntryAuthRequestParametersJSONException)errorException).getMessage()) %>" key="oauth-client-invalid-auth-request-parameters-json-x" />
				</liferay-ui:error>

				<liferay-ui:error exception="<%= OAuthClientEntryAuthServerWellKnownURIException.class %>" message="oauth-client-invalid-auth-server-well-known-uri" />

				<liferay-ui:error exception="<%= OAuthClientEntryInfoJSONException.class %>">
					<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuthClientEntryInfoJSONException)errorException).getMessage()) %>" key="oauth-client-invalid-info-json-x" />
				</liferay-ui:error>

				<liferay-ui:error exception="<%= OAuthClientEntryOIDCUserInfoMapperJSONException.class %>">
					<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuthClientEntryOIDCUserInfoMapperJSONException)errorException).getMessage()) %>" key="oauth-client-invalid-oidc-user-info-mapper-json-x" />
				</liferay-ui:error>

				<liferay-ui:error exception="<%= OAuthClientEntryTokenRequestParametersJSONException.class %>">
					<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuthClientEntryTokenRequestParametersJSONException)errorException).getMessage()) %>" key="oauth-client-invalid-token-request-parameters-json-x" />
				</liferay-ui:error>

				<h3 class="sheet-subtitle"><liferay-ui:message key="oauth-client-configurations" /></h3>

				<aui:input helpMessage="oauth-client-as-well-known-uri-help" label="oauth-client-as-well-known-uri" name="authServerWellKnownURI" type="text" />

				<aui:input helpMessage="metadata-cache-time-help" label="metadata-cache-time" name="metadataCacheTime" type="text" value="<%= (oAuthClientEntry != null) ? oAuthClientEntry.getMetadataCacheTime() : OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT %>" />

				<aui:input helpMessage="token-connection-timeout-help" label="token-connection-timeout" name="tokenConnectionTimeout" type="text" value="<%= (oAuthClientEntry != null) ? oAuthClientEntry.getTokenConnectionTimeout() : OAuthClientEntryConstants.TOKEN_CONNECTION_TIMEOUT_DEFAULT %>" />

				<aui:input
					cssClass="client-info-textarea"
					helpMessage="oauth-client-info-json-help"
					label="oauth-client-info-json"
					name="infoJSON"
					type="textarea"
					value='<%=
						JSONUtil.put(
							"client_id", ""
						).put(
							"client_name", "example_client"
						).put(
							"client_secret", ""
						).put(
							"redirect_uris", JSONUtil.put("")
						).put(
							"scope", "openid email profile"
						).put(
							"subject_type", "public"
						)
					%>'
				/>

				<aui:input name="oAuthClientEntryId" type="hidden" value="<%= (oAuthClientEntry != null) ? oAuthClientEntry.getOAuthClientEntryId() : 0 %>" />

				<aui:input
					cssClass="request-parameters-textarea"
					helpMessage='<%= LanguageUtil.format(request, "oauth-client-default-auth-request-parameters-json-help", "https://www.iana.org/assignments/oauth-parameters", false) %>'
					label="oauth-client-default-auth-request-parameters-json"
					name="authRequestParametersJSON"
					type="textarea"
					value='<%=
						JSONUtil.put(
							"custom_request_parameters", JSONUtil.put("example_key", JSONUtil.put(""))
						).put(
							"resource", JSONUtil.put("")
						).put(
							"response_type", "code"
						).put(
							"scope", "openid email profile"
						)
					%>'
				/>

				<aui:input
					cssClass="request-parameters-textarea"
					helpMessage='<%= LanguageUtil.format(request, "oauth-client-default-token-request-parameters-json-help", "https://www.iana.org/assignments/oauth-parameters", false) %>'
					label="oauth-client-default-token-request-parameters-json"
					name="tokenRequestParametersJSON"
					type="textarea"
					value='<%=
						JSONUtil.put(
							"custom_request_parameters", JSONUtil.put("example_key", JSONUtil.put(""))
						).put(
							"resource", JSONUtil.put("")
						)
					%>'
				/>

				<h3 class="sheet-subtitle"><liferay-ui:message key="oauth-client-oidc-specific-configurations" /></h3>

				<c:if test='<%= FeatureFlagManagerUtil.isEnabled(company.getCompanyId(), "LPD-49855") %>'>

					<%
					String matcherField = (oAuthClientEntry != null) ? oAuthClientEntry.getMatcherField() : "email";
					%>

					<aui:select helpMessage="matcher-field-help" label="matcher-field" name="matcherField" required="<%= true %>" type="text">
						<aui:option label="email" selected='<%= Objects.equals(matcherField, "email") %>' value="email" />
						<aui:option label="screen-name" selected='<%= Objects.equals(matcherField, "screenName") %>' value="screenName" />
					</aui:select>
				</c:if>

				<aui:input cssClass="info-mapper-textarea" helpMessage="oauth-client-oidc-user-info-mapper-json-help" label="oauth-client-oidc-user-info-mapper-json" name="OIDCUserInfoMapperJSON" type="textarea" value="<%= OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON %>" />

				<c:if test='<%= FeatureFlagManagerUtil.isEnabled(company.getCompanyId(), "LPD-49855") %>'>
					<div class="lfr-form-rows" id="<portlet:namespace />customClaimsContentBox">
						<label class="c-mb-1 c-mt-2 font-weight-semi-bold text-4">
							<liferay-ui:message key="custom-claims" />

							<span class="c-ml-1 lfr-portal-tooltip taglib-icon-help" tabindex="0" title="<%= LanguageUtil.get(request, "custom-claims-help") %>">
								<clay:icon
									symbol="question-circle-full"
								/>
							</span>
						</label>

						<%
						JSONObject customClaimsJSONObject = null;

						if (oAuthClientEntry != null) {
							customClaimsJSONObject = JSONFactoryUtil.createJSONObject(oAuthClientEntry.getCustomClaimsJSON());
						}

						if ((customClaimsJSONObject == null) || (customClaimsJSONObject.length() < 1)) {
							customClaimsJSONObject = JSONUtil.put("", "");
						}

						int index = 0;

						for (String key : customClaimsJSONObject.keySet()) {
							String customClaimsKeyId = "customClaimsKey-" + index;
							String customClaimsValueId = "customClaimsValue-" + index;

							index++;
						%>

						<div class="lfr-form-row">
							<div class="form-group">
								<div class="form-group-autofit">
									<div class="form-group-item">
										<aui:select fieldParam="<%= customClaimsKeyId %>" id="<%= customClaimsKeyId %>" inlineField="<%= true %>" label="user-custom-fields" name="<%= customClaimsKeyId %>" showEmptyOption="<%= true %>">

												<%
												for (ExpandoColumn expandoColumn : (List<ExpandoColumn>)request.getAttribute(OAuthClientWebKeys.EXPANDO_COLUMNS)) {
												%>

													<aui:option label="<%= expandoColumn.getName() %>" selected="<%= Objects.equals(expandoColumn.getName(), key) %>" value="<%= expandoColumn.getName() %>"></aui:option>

												<%
												}
												%>

											</aui:select>
										</div>
									<div class="form-group-item">
										<aui:input fieldParam="<%= customClaimsValueId %>" id="<%= customClaimsValueId %>" label="custom-claim" name="<%= customClaimsValueId %>" type="text" value="<%= customClaimsJSONObject.get(key) %>" />
									</div>
								</div>
							</div>

						<%
						}
						%>

						<aui:input name="customClaimsIndexes" type="hidden" />
					</div>

					<aui:script type="module">
						import {autoFields} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-web", "auto_fields") %>';

						autoFields({
							contentBox: '#<portlet:namespace />customClaimsContentBox',
							fieldIndexes: '<portlet:namespace />customClaimsIndexes',
							namespace: '<portlet:namespace />',
						});
					</aui:script>
				</c:if>
			</aui:fieldset>

			<aui:button-row>
				<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "doSubmit();" %>' type="submit" />
				<aui:button href="<%= redirect %>" type="cancel" />
			</aui:button-row>
		</div>
	</clay:container-fluid>
</aui:form>

<aui:script>
	<portlet:namespace />init();

	function <portlet:namespace />doSubmit() {
		var infoJSON = document.getElementById(
			'<portlet:namespace />infoJSON'
		).value;

		try {
			infoJSON = JSON.stringify(JSON.parse(infoJSON), null, 0);
		}
		catch (e) {
			alert('Ill-formatted Info JSON');
			return;
		}

		document.getElementById('<portlet:namespace />infoJSON').value = infoJSON;

		var authRequestParametersJSON = document.getElementById(
			'<portlet:namespace />authRequestParametersJSON'
		).value;

		try {
			authRequestParametersJSON = JSON.stringify(
				JSON.parse(authRequestParametersJSON),
				null,
				0
			);
		}
		catch (e) {
			alert('Ill-formatted Default Authorization Request Parameters JSON');
			return;
		}

		document.getElementById(
			'<portlet:namespace />authRequestParametersJSON'
		).value = authRequestParametersJSON;

		document.getElementById('<portlet:namespace />infoJSON').value = infoJSON;

		var tokenRequestParametersJSON = document.getElementById(
			'<portlet:namespace />tokenRequestParametersJSON'
		).value;

		try {
			tokenRequestParametersJSON = JSON.stringify(
				JSON.parse(tokenRequestParametersJSON),
				null,
				0
			);
		}
		catch (e) {
			alert('Ill-formatted Default Token Request Parameters JSON');
			return;
		}

		document.getElementById(
			'<portlet:namespace />tokenRequestParametersJSON'
		).value = tokenRequestParametersJSON;

		var oidcUserInfoMapperJSON = document.getElementById(
			'<portlet:namespace />OIDCUserInfoMapperJSON'
		).value;

		try {
			oidcUserInfoMapperJSON = JSON.parse(oidcUserInfoMapperJSON);

			var matcherFieldValue = document.getElementById(
				'<portlet:namespace />matcherField'
			)?.value;

			if (
				matcherFieldValue == 'screenName' &&
				!oidcUserInfoMapperJSON.user.screenName
			) {
				alert(
					'Missing screenName value at OpenId Connect User Information Mapper JSON'
				);
				return;
			}

			oidcUserInfoMapperJSON = JSON.stringify(
				oidcUserInfoMapperJSON,
				null,
				0
			);
		}
		catch (e) {
			alert('Ill-formatted OIDC User Info Mapper JSON');
			return;
		}

		document.getElementById(
			'<portlet:namespace />OIDCUserInfoMapperJSON'
		).value = oidcUserInfoMapperJSON;

		submitForm(
			document.getElementById('<portlet:namespace />oauth-client-entry-fm')
		);
	}

	function <portlet:namespace />init() {
		var infoJSON = document.getElementById('<portlet:namespace />infoJSON');

		infoJSON.value = JSON.stringify(JSON.parse(infoJSON.value), null, 4);

		var authRequestParametersJSON = document.getElementById(
			'<portlet:namespace />authRequestParametersJSON'
		);

		authRequestParametersJSON.value = JSON.stringify(
			JSON.parse(authRequestParametersJSON.value),
			null,
			4
		);

		var tokenRequestParametersJSON = document.getElementById(
			'<portlet:namespace />tokenRequestParametersJSON'
		);

		tokenRequestParametersJSON.value = JSON.stringify(
			JSON.parse(tokenRequestParametersJSON.value),
			null,
			4
		);

		var oidcUserInfoMapperJSON = document.getElementById(
			'<portlet:namespace />OIDCUserInfoMapperJSON'
		);

		oidcUserInfoMapperJSON.value = JSON.stringify(
			JSON.parse(oidcUserInfoMapperJSON.value),
			null,
			4
		);
	}
</aui:script>