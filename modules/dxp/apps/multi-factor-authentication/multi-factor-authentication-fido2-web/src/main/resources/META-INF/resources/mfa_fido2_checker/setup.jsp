<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
List<MFAFIDO2CredentialEntry> mfaFIDO2CredentialEntries = (List<MFAFIDO2CredentialEntry>)request.getAttribute(MFAFIDO2WebKeys.MFA_FIDO2_CREDENTIAL_ENTRIES);
%>

<div id="<portlet:namespace />messageContainer"></div>

<c:choose>
	<c:when test="<%= mfaFIDO2CredentialEntries.size() < mfaFIDO2Configuration.allowedCredentialsPerUser() %>">
		<aui:button-row>
			<clay:button
				additionalProps='<%=
					HashMapBuilder.put(
						"pkccOptions", request.getAttribute(MFAFIDO2WebKeys.MFA_FIDO2_PKCC_OPTIONS)
					).build()
				%>'
				displayType="primary"
				label="register-a-fido2-authenticator"
				propsTransformer="{RegistrationTransformer} from multi-factor-authentication-fido2-web"
			/>
		</aui:button-row>
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="you-have-registered-the-maximum-number-of-allowed-authenticators" />

		<aui:input name="removeExistingSetup" type="hidden" value="<%= true %>" />

		<aui:button-row>
			<clay:button
				displayType="danger"
				label="remove-all-registered-fido2-authenticators"
				type="submit"
			/>
		</aui:button-row>
	</c:otherwise>
</c:choose>

<aui:input name="responseJSON" showRequiredLabel="yes" type="hidden" />

<liferay-ui:search-container
	iteratorURL="<%= renderResponse.createRenderURL() %>"
	total="<%= mfaFIDO2CredentialEntries.size() %>"
>
	<liferay-ui:search-container-results
		calculateStartAndEnd="<%= true %>"
		results="<%= mfaFIDO2CredentialEntries %>"
	/>

	<liferay-ui:search-container-row
		className="MFAFIDO2CredentialEntry"
		modelVar="mfaFIDO2CredentialEntry"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="authenticator-id"
			value="<%= String.valueOf(mfaFIDO2CredentialEntry.getPrimaryKey()) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="registered-date"
			value="<%= String.valueOf(mfaFIDO2CredentialEntry.getCreateDate()) %>"
		/>

		<liferay-ui:search-container-column-text>
			<portlet:actionURL name="/multi_factor_authentication_fido2/remove_mfa_fido2_credential_entry" var="removeMFAFIDO2CredentialEntryURL">
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="mfaFIDO2CredentialEntryId" value="<%= String.valueOf(mfaFIDO2CredentialEntry.getPrimaryKey()) %>" />
				<portlet:param name="setupMFACheckerServiceId" value="<%= String.valueOf(request.getAttribute(MFAFIDO2WebKeys.SETUP_MFA_CHECKER_SERVICE_ID)) %>" />
			</portlet:actionURL>

			<liferay-ui:icon-delete
				confirmation="are-you-sure-you-want-to-remove-this-authenticator"
				icon="times-circle"
				message="remove"
				showIcon="<%= true %>"
				url="<%= removeMFAFIDO2CredentialEntryURL %>"
			/>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>