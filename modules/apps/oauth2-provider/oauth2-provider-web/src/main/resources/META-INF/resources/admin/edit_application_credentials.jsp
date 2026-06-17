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

String clientId = (oAuth2Application == null) ? "" : oAuth2Application.getClientId();
String clientSecret = (oAuth2Application == null) ? "" : oAuth2Application.getClientSecret();
String externalReferenceCode = (oAuth2Application == null) ? "" : oAuth2Application.getExternalReferenceCode();
%>

<portlet:actionURL name="/oauth2_provider/update_oauth2_application" var="updateOAuth2ApplicationURL">
	<portlet:param name="mvcRenderCommandName" value="/oauth2_provider/update_oauth2_application" />
	<portlet:param name="oAuth2ApplicationId" value='<%= (oAuth2Application == null) ? "" : String.valueOf(oAuth2Application.getOAuth2ApplicationId()) %>' />
	<portlet:param name="backURL" value="<%= redirect %>" />
</portlet:actionURL>

<aui:form action="<%= updateOAuth2ApplicationURL %>" id="oauth2-application-fm" method="post" name="oauth2-application-fm">
	<clay:container-fluid
		cssClass="container-view"
	>
		<div class="sheet">
			<clay:row>
				<clay:col
					lg="12"
				>
					<liferay-ui:error exception="<%= DuplicateOAuth2ApplicationClientIdException.class %>" focusField="clientId" message="client-id-already-exists" />

					<liferay-ui:error exception="<%= OAuth2ApplicationClientGrantTypeException.class %>">
						<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuth2ApplicationClientGrantTypeException)errorException).getMessage()) %>" key="grant-type-x-is-unsupported-for-this-client-type" />
					</liferay-ui:error>

					<liferay-ui:error exception="<%= OAuth2ApplicationHomePageURLException.class %>" focusField="homePageURL" message="home-page-url-is-invalid" />

					<liferay-ui:error exception="<%= OAuth2ApplicationJWKSException.class %>" focusField="jwks" message="the-json-web-key-set-is-not-fips-compliant" />

					<liferay-ui:error exception="<%= OAuth2ApplicationHomePageURLSchemeException.class %>" focusField="homePageURL" message="home-page-url-scheme-is-invalid" />
					<liferay-ui:error exception="<%= OAuth2ApplicationNameException.class %>" focusField="name" message="missing-application-name" />
					<liferay-ui:error exception="<%= OAuth2ApplicationPrivacyPolicyURLException.class %>" focusField="privacyPolicyURL" message="privacy-policy-url-is-invalid" />

					<liferay-ui:error exception="<%= OAuth2ApplicationPrivacyPolicyURLSchemeException.class %>" focusField="privacyPolicyURL">
						<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuth2ApplicationPrivacyPolicyURLSchemeException)errorException).getMessage()) %>" key="privacy-policy-url-scheme-is-invalid" />
					</liferay-ui:error>

					<liferay-ui:error exception="<%= OAuth2ApplicationRedirectURIException.class %>" focusField="redirectURIs">
						<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuth2ApplicationRedirectURIException)errorException).getMessage()) %>" key="redirect-uri-x-is-invalid" />
					</liferay-ui:error>

					<liferay-ui:error exception="<%= OAuth2ApplicationRedirectURIMissingException.class %>" focusField="redirectURIs">
						<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuth2ApplicationRedirectURIMissingException)errorException).getMessage()) %>" key="redirect-uri-is-missing-for-grant-type-x" />
					</liferay-ui:error>

					<liferay-ui:error exception="<%= OAuth2ApplicationRedirectURIFragmentException.class %>" focusField="redirectURIs">
						<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuth2ApplicationRedirectURIFragmentException)errorException).getMessage()) %>" key="redirect-uri-x-fragment-is-invalid" />
					</liferay-ui:error>

					<liferay-ui:error exception="<%= OAuth2ApplicationRedirectURIPathException.class %>" focusField="redirectURIs">
						<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuth2ApplicationRedirectURIPathException)errorException).getMessage()) %>" key="redirect-uri-x-path-is-invalid" />
					</liferay-ui:error>

					<liferay-ui:error exception="<%= OAuth2ApplicationRedirectURISchemeException.class %>" focusField="redirectURIs">
						<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuth2ApplicationRedirectURISchemeException)errorException).getMessage()) %>" key="redirect-uri-x-scheme-is-invalid" />
					</liferay-ui:error>

					<liferay-ui:error exception="<%= OAuth2ApplicationClientCredentialUserIdException.class %>">

						<%
						OAuth2ApplicationClientCredentialUserIdException oAuth2ApplicationClientCredentialUserIdException = (OAuth2ApplicationClientCredentialUserIdException)errorException;
						%>

						<c:choose>
							<c:when test="<%= Validator.isNotNull(oAuth2ApplicationClientCredentialUserIdException.getClientCredentialUserScreenName()) %>">
								<liferay-ui:message arguments="<%= oAuth2ApplicationClientCredentialUserIdException.getClientCredentialUserScreenName() %>" key="this-operation-cannot-be-performed-because-you-cannot-impersonate-x" />
							</c:when>
							<c:otherwise>
								<liferay-ui:message arguments="<%= oAuth2ApplicationClientCredentialUserIdException.getClientCredentialUserId() %>" key="this-operation-cannot-be-performed-because-you-cannot-impersonate-x" />
							</c:otherwise>
						</c:choose>
					</liferay-ui:error>

					<aui:model-context bean="<%= oAuth2Application %>" model="<%= OAuth2Application.class %>" />

					<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" var="baseResourceURL" />

					<c:if test="<%= oAuth2Application != null %>">
						<aui:fieldset cssClass="mb-3">
							<react:component
								module="{EditClientDetails} from oauth2-provider-web"
								props='<%=
									HashMapBuilder.<String, Object>put(
										"baseResourceURL", String.valueOf(baseResourceURL)
									).put(
										"clientId", clientId
									).put(
										"clientSecret", clientSecret
									).put(
										"externalReferenceCode", externalReferenceCode
									).build()
								%>'
							/>
						</aui:fieldset>
					</c:if>
				</clay:col>
			</clay:row>

			<clay:row>
				<c:choose>
					<c:when test="<%= oAuth2Application != null %>">
						<clay:col
							lg="9"
						>
							<liferay-util:include page="/admin/edit_application_left_column.jsp" servletContext="<%= application %>" />
						</clay:col>

						<clay:col
							cssClass="pt-4"
							lg="3"
						>
							<h3 class="sheet-subtitle"><liferay-ui:message key="icon" /></h3>

							<%
							String thumbnailURL = oAuth2AdminPortletDisplayContext.getThumbnailURL(oAuth2Application);
							%>

							<c:choose>
								<c:when test="<%= oAuth2AdminPortletDisplayContext.hasUpdatePermission(oAuth2Application) %>">
									<liferay-frontend:logo-selector
										currentLogoURL="<%= thumbnailURL %>"
										defaultLogoURL="<%= oAuth2AdminPortletDisplayContext.getDefaultIconURL() %>"
										label='<%= LanguageUtil.get(request, "icon") %>'
									/>
								</c:when>
								<c:otherwise>
									<img alt="<liferay-ui:message escapeAttribute="<%= true %>" key="portrait" />" src="<%= HtmlUtil.escapeAttribute(thumbnailURL) %>" />
								</c:otherwise>
							</c:choose>
						</clay:col>
					</c:when>
					<c:otherwise>
						<clay:col
							lg="12"
						>
							<liferay-util:include page="/admin/edit_application_left_column.jsp" servletContext="<%= application %>" />
						</clay:col>
					</c:otherwise>
				</c:choose>
			</clay:row>

			<clay:row>
				<clay:col
					lg="12"
				>
					<aui:button-row>
						<aui:button type="submit" />

						<aui:button href="<%= portletDisplay.getURLBack() %>" type="cancel" />
					</aui:button-row>
				</clay:col>
			</clay:row>
		</div>
	</clay:container-fluid>
</aui:form>

<aui:script use="aui-modal,liferay-form,node,node-event-simulate">
	window.<portlet:namespace />areAdminApplicationSectionsRequired = function () {
		var selectedClientProfile = <portlet:namespace />getSelectedClientProfile();
		return (
			A.all(
				'#<portlet:namespace />allowedGrantTypes .client-profile-' +
					selectedClientProfile.val() +
					' input:checked[name=<%= liferayPortletResponse.getNamespace() %>grant-<%= GrantType.AUTHORIZATION_CODE.name() %>]'
			).size() > 0 ||
			A.all(
				'#<portlet:namespace />allowedGrantTypes .client-profile-' +
					selectedClientProfile.val() +
					' input:checked[name=<%= liferayPortletResponse.getNamespace() %>grant-<%= GrantType.AUTHORIZATION_CODE_PKCE.name() %>]'
			).size() > 0
		);
	};

	window.<portlet:namespace />getSelectedClientProfile = function () {
		return A.one('#<portlet:namespace />clientProfile').get('selectedOptions');
	};

	window.<portlet:namespace />isClientCredentialsSectionRequired = function () {
		var selectedClientProfile = <portlet:namespace />getSelectedClientProfile();

		return (
			A.all(
				'#<portlet:namespace />allowedGrantTypes .client-profile-' +
					selectedClientProfile.val() +
					' input:checked[name=<%= liferayPortletResponse.getNamespace() %>grant-<%= GrantType.CLIENT_CREDENTIALS.name() %>]'
			).size() > 0
		);
	};

	window.<portlet:namespace />isConfidentialClientRequired = function () {
		var selectedClientProfile = <portlet:namespace />getSelectedClientProfile();

		return (
			A.all(
				'#<portlet:namespace />allowedGrantTypes .client-profile-' +
					selectedClientProfile.val() +
					' input:checked[data-issupportsconfidentialclients="true"][data-issupportspublicclients="false"]'
			).size() > 0
		);
	};

	window.<portlet:namespace />isRedirectURIRequired = function () {
		var selectedClientProfile = <portlet:namespace />getSelectedClientProfile();

		return (
			A.all(
				'#<portlet:namespace />allowedGrantTypes .client-profile-' +
					selectedClientProfile.val() +
					' input:checked[data-isredirect="true"]'
			).size() > 0
		);
	};

	window.<portlet:namespace />requiredRedirectURIs = function () {
		var grantTypesNodeList = A.all(
			'#<portlet:namespace />allowedGrantTypes .allowedGrantType'
		)._nodes;

		var grantTypeNode = null;
		var grantTypeToggleElement = null;

		var redirectURIs = false;

		for (var i = 0; i < grantTypesNodeList.length; i++) {
			grantTypeNode = grantTypesNodeList[i];

			if (grantTypeNode.hasAttribute('hidden')) {
				continue;
			}
			else {
				grantTypeToggleElement = grantTypeNode.children[0].children[0];

				if (
					grantTypeToggleElement.getAttribute('data-isredirect') ===
						'true' &&
					grantTypeToggleElement.checked
				) {
					redirectURIs = true;

					break;
				}
			}
		}

		<portlet:namespace />updateRedirectURIs(redirectURIs);
	};

	window.<portlet:namespace />setControlEqualTo = function (
		targetControlId,
		srcControlId
	) {
		var targetControl = A.one('#<portlet:namespace />' + targetControlId);
		var srcControl = A.one('#<portlet:namespace />' + srcControlId);

		<portlet:namespace />updateComponent(targetControl, srcControl.val());
	};

	window.<portlet:namespace />updateAdminOptionsApplicationSection = function () {
		var rememberApplicationSection = A.one(
			'#<portlet:namespace />rememberDeviceSection'
		);

		var trustedApplicationSection = A.one(
			'#<portlet:namespace />trustedApplicationSection'
		);

		var trustedApplicationCheckbox = document.querySelector(
			'input[name^="<portlet:namespace />trustedApplication"]'
		);

		if (<portlet:namespace />areAdminApplicationSectionsRequired()) {
			trustedApplicationSection.show();

			if (trustedApplicationCheckbox.checked) {
				rememberApplicationSection.hide();
			}
			else {
				rememberApplicationSection.show();
			}
		}
		else {
			rememberApplicationSection.hide();
			trustedApplicationSection.hide();
		}
	};

	window.<portlet:namespace />updateAllowedGrantTypes = function (clientProfile) {
		A.all('#<portlet:namespace />allowedGrantTypes .allowedGrantType').hide();
		A.all(
			'#<portlet:namespace />allowedGrantTypes .allowedGrantType.client-profile-' +
				clientProfile
		).show();

		<portlet:namespace />requiredRedirectURIs();
		<portlet:namespace />updateAdminOptionsApplicationSection();
		<portlet:namespace />updateClientCredentialsSection();
	};

	window.<portlet:namespace />updateClientCredentialsSection = function () {
		var clientCredentialsSection = A.one(
			'#<portlet:namespace />clientCredentialsSection'
		);
		var allowedGrantTypesSection = A.one(
			'#<portlet:namespace />allowedGrantTypesSection'
		);

		if (<portlet:namespace />isClientCredentialsSectionRequired()) {
			clientCredentialsSection.show();
			allowedGrantTypesSection.addClass('col-lg-7');
			allowedGrantTypesSection.removeClass('col-lg-12');
		}
		else {
			clientCredentialsSection.hide();
			allowedGrantTypesSection.addClass('col-lg-12');
			allowedGrantTypesSection.removeClass('col-lg-7');
		}
	};

	window.<portlet:namespace />updateComponent = function (component, newValue) {
		component.val(newValue);
		component.simulate('keyup');
		component.simulate('change');
	};

	window.<portlet:namespace />updateRedirectURIs = function (required) {
		var redirectURIsNode = document.getElementById(
			'<portlet:namespace />redirectURIs'
		);

		if (redirectURIsNode) {
			var lexiconIconParent =
				redirectURIsNode.parentNode.firstElementChild.firstElementChild;

			if (lexiconIconParent) {
				if (required) {
					lexiconIconParent.style = 'visibility:visible;';
				}
				else {
					lexiconIconParent.style = 'visibility:hidden;';
				}
			}
		}
	};

	var clientProfile = A.one('#<portlet:namespace />clientProfile');

	clientProfile.delegate(
		'change',
		(event) => {
			var newClientProfileValue = event.currentTarget.val();
			<portlet:namespace />updateAllowedGrantTypes(newClientProfileValue);
		},
		'#<portlet:namespace />clientProfile'
	);

	<portlet:namespace />updateAllowedGrantTypes(clientProfile.val());

	var form = Liferay.Form.get('<portlet:namespace />oauth2-application-fm');

	var oldFieldRules = form.get('fieldRules');
	var newFieldRules = [
		{
			body: function (val, fieldNode, ruleValue) {
				return <portlet:namespace />isConfidentialClientRequired();
			},
			custom: false,
			fieldName: '<portlet:namespace />clientSecret',
			validatorName: 'required',
		},
		{
			body: function (val, fieldNode, ruleValue) {
				return <portlet:namespace />isRedirectURIRequired();
			},
			custom: false,
			fieldName: '<portlet:namespace />redirectURIs',
			validatorName: 'required',
		},
	];

	var fieldRules = oldFieldRules.concat(newFieldRules);

	form.set('fieldRules', fieldRules);

	<portlet:namespace />updateAdminOptionsApplicationSection();
</aui:script>