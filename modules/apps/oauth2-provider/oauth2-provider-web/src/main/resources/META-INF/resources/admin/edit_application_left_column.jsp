<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
OAuth2Application oAuth2Application = oAuth2AdminPortletDisplayContext.getOAuth2Application();
%>

<aui:model-context bean="<%= oAuth2Application %>" model="<%= OAuth2Application.class %>" />

<aui:fieldset>
	<aui:input helpMessage="application-name-help" label="name" name="name" required="<%= true %>" />

	<aui:input helpMessage="home-page-url-help" name="homePageURL" />

	<c:if test="<%= oAuth2Application != null %>">
		<aui:input helpMessage="application-description-help" label="description" name="description" />
	</c:if>

	<aui:input helpMessage="redirect-uris-help" label="redirect-uris" name="redirectURIs" required="<%= true %>" />

	<c:if test="<%= oAuth2Application != null %>">
		<aui:input helpMessage="privacy-policy-url-help" name="privacyPolicyURL" />
	</c:if>

	<aui:select helpMessage="client-authentication-method-help" label="client-authentication-method" name="clientAuthenticationMethod" required="<%= true %>">
		<aui:option label="client-secret-basic-or-post" value="client_secret_post" />
		<aui:option label="none" value="none" />
		<aui:option label="client-secret-jwt" value="client_secret_jwt" />
		<aui:option label="private-key-jwt" value="private_key_jwt" />
	</aui:select>

	<aui:input cssClass="jwks-textarea" helpMessage="json-web-key-set-help" label="JSON Web Key Set" name="jwks" type="textarea" />

	<aui:select helpMessage="client-profile-help" name="clientProfile">

		<%
		ClientProfile[] clientProfiles = oAuth2AdminPortletDisplayContext.getSortedClientProfiles();

		for (ClientProfile clientProfile : clientProfiles) {
		%>

			<aui:option label="<%= clientProfile.name() %>" value="<%= clientProfile.id() %>" />

		<%
		}
		%>

	</aui:select>

	<clay:row>

		<%
		String clientCredentialsCheckboxName = null;
		%>

		<clay:col
			id='<%= liferayPortletResponse.getNamespace() + "allowedGrantTypesSection" %>'
			lg="7"
		>
			<h3 class="sheet-subtitle"><liferay-ui:message key="allowed-grant-types" /></h3>

			<aui:field-wrapper>
				<div id="<portlet:namespace />allowedGrantTypes">

					<%
					List<GrantType> allowedGrantTypesList = new ArrayList<>();

					if (oAuth2Application != null) {
						allowedGrantTypesList = oAuth2Application.getAllowedGrantTypesList();
					}

					List<GrantType> oAuth2Grants = oAuth2AdminPortletDisplayContext.getGrantTypes(portletPreferences);

					for (GrantType grantType : oAuth2Grants) {
						Set<String> cssClasses = new HashSet<>();

						for (ClientProfile clientProfile : ClientProfile.values()) {
							Set<GrantType> grantTypes = clientProfile.grantTypes();

							if (grantTypes.contains(grantType)) {
								cssClasses.add("client-profile-" + clientProfile.id());
							}
						}

						String cssClassesStr = StringUtil.merge(cssClasses, StringPool.SPACE);

						boolean checked = false;

						if ((oAuth2Application == null) || allowedGrantTypesList.contains(grantType)) {
							checked = true;
						}

						String name = "grant-" + grantType.name();

						if (grantType.equals(GrantType.CLIENT_CREDENTIALS)) {
							clientCredentialsCheckboxName = name;
						}

						checked = ParamUtil.getBoolean(request, name, checked);

						Map<String, Object> data = HashMapBuilder.<String, Object>put(
							"isredirect", grantType.isRequiresRedirectURI()
						).put(
							"issupportsconfidentialclients", grantType.isSupportsConfidentialClients()
						).put(
							"issupportspublicclients", grantType.isSupportsPublicClients()
						).build();
					%>

						<div class="allowedGrantType <%= cssClassesStr %>">
							<c:choose>
								<c:when test="<%= grantType.equals(GrantType.AUTHORIZATION_CODE) || grantType.equals(GrantType.AUTHORIZATION_CODE_PKCE) %>">
									<aui:input checked="<%= checked %>" data="<%= data %>" label="<%= grantType.name() %>" name="<%= name %>" onchange='<%= liferayPortletResponse.getNamespace() + "updateAdminOptionsApplicationSection();" %>' type="checkbox" />
								</c:when>
								<c:when test="<%= grantType.equals(GrantType.CLIENT_CREDENTIALS) %>">
									<aui:input checked="<%= checked %>" data="<%= data %>" helpMessage="the-client-will-impersonate-the-selected-client-credential-user-but-will-be-restricted-to-the-selected-scopes" label="<%= grantType.name() %>" name="<%= clientCredentialsCheckboxName %>" onchange='<%= liferayPortletResponse.getNamespace() + "updateClientCredentialsSection();" %>' type="checkbox" />
								</c:when>
								<c:otherwise>
									<aui:input checked="<%= checked %>" data="<%= data %>" label="<%= grantType.name() %>" name="<%= name %>" type="checkbox" />
								</c:otherwise>
							</c:choose>
						</div>

						<c:if test="<%= grantType.isRequiresRedirectURI() %>">
							<aui:script>
								var allowedAuthorizationTypeCheckbox = document.getElementById(
									'<portlet:namespace /><%= name %>'
								);

								if (allowedAuthorizationTypeCheckbox) {
									allowedAuthorizationTypeCheckbox.addEventListener('click', (event) => {
										<portlet:namespace />requiredRedirectURIs();
									});
								}
							</aui:script>
						</c:if>

					<%
					}
					%>

				</div>
			</aui:field-wrapper>
		</clay:col>

		<c:if test="<%= clientCredentialsCheckboxName != null %>">
			<clay:col
				id='<%= liferayPortletResponse.getNamespace() + "clientCredentialsSection" %>'
				lg="5"
			>
				<h3 class="sheet-subtitle"><liferay-ui:message key="client-credentials-user" /></h3>

				<aui:field-wrapper>
					<c:choose>
						<c:when test="<%= oAuth2Application != null %>">
							<aui:input name="clientCredentialUserId" type="hidden" value="<%= oAuth2Application.getClientCredentialUserId() %>" />

							<aui:input disabled="<%= true %>" label="" name="clientCredentialUserName" type="text" value="<%= HtmlUtil.escapeAttribute(oAuth2Application.getClientCredentialUserName()) %>" />
						</c:when>
						<c:otherwise>
							<aui:input name="clientCredentialUserId" type="hidden" value="<%= user.getUserId() %>" />

							<aui:input disabled="<%= true %>" label="" name="clientCredentialUserName" type="text" value="<%= HtmlUtil.escapeAttribute(user.getScreenName()) %>" />
						</c:otherwise>
					</c:choose>

					<div class="btn-group button-holder">
						<aui:button data-qa-id="selectUserButton" id="selectUserButton" value="select" />

						<aui:button data-qa-id="useSignedInUserButton" id="useSignedInUserButton" value="use-signed-in-user" />
					</div>
				</aui:field-wrapper>

				<aui:script use="aui-base,aui-io">
					var useSignedInUserButton = document.getElementById(
						'<portlet:namespace />useSignedInUserButton'
					);

					if (useSignedInUserButton) {
						useSignedInUserButton.addEventListener('click', (event) => {
							A.one('#<portlet:namespace />clientCredentialUserId').val(
								'<%= user.getUserId() %>'
							);
							A.one('#<portlet:namespace />clientCredentialUserName').val(
								'<%= HtmlUtil.escapeJS(user.getScreenName()) %>'
							);
						});
					}

					var selectUserButton = document.getElementById(
						'<portlet:namespace />selectUserButton'
					);

					if (selectUserButton) {
						selectUserButton.addEventListener('click', (event) => {
							Liferay.Util.openSelectionModal({
								onSelect: function (event) {
									const item = JSON.parse(event.value);

									A.one('#<portlet:namespace />clientCredentialUserId').val(
										item.id
									);
									A.one('#<portlet:namespace />clientCredentialUserName').val(
										item.name
									);
								},
								selectEventName: '<portlet:namespace />selectUsers',

								<%
								ItemSelector itemSelector = (ItemSelector)request.getAttribute(ItemSelector.class.getName());

								UserOAuth2ItemSelectorCriterion userOAuth2ItemSelectorCriterion = new UserOAuth2ItemSelectorCriterion();

								userOAuth2ItemSelectorCriterion.setDesiredItemSelectorReturnTypes(new UUIDItemSelectorReturnType());
								%>

								title: '<liferay-ui:message key="users" />',
								url: '<%= HtmlUtil.escapeJS(String.valueOf(itemSelector.getItemSelectorURL(RequestBackedPortletURLFactoryUtil.create(request), liferayPortletResponse.getNamespace() + "selectUsers", userOAuth2ItemSelectorCriterion))) %>',
							});
						});
					}
				</aui:script>
			</clay:col>
		</c:if>

		<c:if test="<%= oAuth2AdminPortletDisplayContext.hasAddTrustedApplicationPermission() %>">
			<clay:col
				id='<%= liferayPortletResponse.getNamespace() + "trustedApplicationSection" %>'
				lg="6"
			>
				<h3 class="sheet-subtitle"><liferay-ui:message key="trusted-application" /></h3>

				<aui:field-wrapper>
					<aui:input checked="<%= (oAuth2Application == null) ? false : oAuth2Application.isTrustedApplication() %>" helpMessage="trusted-application-help" id="trustedApplication" label="trusted-application" name="trustedApplication" onchange='<%= liferayPortletResponse.getNamespace() + "updateAdminOptionsApplicationSection();" %>' type="checkbox" />
				</aui:field-wrapper>
			</clay:col>
		</c:if>

		<c:if test="<%= oAuth2AdminPortletDisplayContext.hasRememberDevicePermission() %>">
			<clay:col
				id='<%= liferayPortletResponse.getNamespace() + "rememberDeviceSection" %>'
				lg="6"
			>
				<h3 class="sheet-subtitle"><liferay-ui:message key="remember-device" /></h3>

				<aui:field-wrapper>
					<aui:input checked="<%= (oAuth2Application == null) ? false : oAuth2Application.isRememberDevice() %>" helpMessage="remember-device-admin-help" id="rememberDevice" label="remember-device" name="rememberDevice" type="checkbox" />
				</aui:field-wrapper>
			</clay:col>
		</c:if>
	</clay:row>

	<c:if test="<%= oAuth2Application != null %>">
		<h3 class="sheet-subtitle"><liferay-ui:message key="supported-features" /></h3>

		<aui:field-wrapper>

			<%
			List<String> oAuth2ApplicationFeaturesList = new ArrayList<>();

			if (oAuth2Application != null) {
				oAuth2ApplicationFeaturesList = oAuth2Application.getFeaturesList();
			}

			for (String oAuth2Feature : oAuth2AdminPortletDisplayContext.getOAuth2Features(portletPreferences)) {
				boolean checked = false;

				if ((oAuth2Application != null) && oAuth2ApplicationFeaturesList.contains(oAuth2Feature)) {
					checked = true;
				}

				String name = "feature-" + oAuth2Feature;

				checked = ParamUtil.getBoolean(request, name, checked);
			%>

				<div class="supportedFeature">
					<aui:input checked="<%= checked %>" label='<%= oAuth2Feature.equals("token.introspection") ? "token-introspection" : HtmlUtil.escape(oAuth2Feature) %>' name="<%= name %>" type="checkbox" />
				</div>

			<%
			}
			%>

		</aui:field-wrapper>
	</c:if>
</aui:fieldset>