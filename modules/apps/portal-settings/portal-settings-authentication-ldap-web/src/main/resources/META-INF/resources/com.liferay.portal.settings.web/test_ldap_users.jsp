<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/com.liferay.portal.settings.web/init.jsp" %>

<%@ include file="/com.liferay.portal.settings.web/test_ldap_init.jspf" %>

<%
SafeLdapContext safeLdapContext = ldapTestDisplayContext.getSafeLdapContext();

if (safeLdapContext == null) {
%>

	<liferay-ui:message key="liferay-has-failed-to-connect-to-the-ldap-server" />

<%
	return;
}

FullNameDefinition fullNameDefinition = FullNameDefinitionFactory.getInstance(locale);

if (Validator.isNull(ParamUtil.getString(request, "userMappingScreenName")) || Validator.isNull(ParamUtil.getString(request, "userMappingPassword")) || (Validator.isNull(ParamUtil.getString(request, "userMappingEmailAddress")) && PropsValues.USERS_EMAIL_ADDRESS_REQUIRED) || Validator.isNull(ParamUtil.getString(request, "userMappingFirstName")) || (Validator.isNull(ParamUtil.getString(request, "userMappingLastName")) && fullNameDefinition.isFieldRequired("last-name"))) {
%>

	<liferay-ui:message key="please-map-each-of-the-user-properties-screen-name,-password,-email-address,-first-name,-and-last-name-to-an-ldap-attribute" />

<%
	return;
}

LDAPFilterValidator ldapFilterValidator = LDAPFilterValidatorUtil.getLDAPFilterValidator();

String userFilter = ParamUtil.getString(request, "importUserSearchFilter");

if (!ldapFilterValidator.isValid(userFilter)) {
%>

	<liferay-ui:message key="please-enter-a-valid-ldap-search-filter" />

<%
	return;
}

Map<String, String> userMappings = ldapTestDisplayContext.getUserMappings();

String[] attributeIds = StringUtil.split(StringUtil.merge(userMappings.values()));

List<SearchResult> searchResults = Collections.emptyList();

if (Validator.isNotNull(userFilter) && !userFilter.equals(StringPool.STAR)) {
	searchResults = ldapTestDisplayContext.getUserSearchResults(attributeIds, safeLdapContext, SafeLdapFilterFactory.fromUnsafeFilter(userFilter, ldapFilterValidator));

	if (searchResults == null) {
%>

		<liferay-ui:message key="please-enter-a-valid-ldap-base-dn" />

<%
		return;
	}
}
%>

<liferay-ui:message key="test-ldap-users" />

<br /><br />

<liferay-ui:message key="a-subset-of-users-has-been-displayed-for-you-to-review" />

<%
boolean showMissingAttributeMessage = false;
%>

<liferay-ui:search-container
	emptyResultsMessage="no-users-were-found"
	total="<%= searchResults.size() %>"
>
	<liferay-ui:search-container-results
		calculateStartAndEnd="<%= true %>"
		results="<%= searchResults %>"
	/>

	<liferay-ui:search-container-row
		className="javax.naming.directory.SearchResult"
		modelVar="searchResult"
	>

		<%
		Attributes attributes = searchResult.getAttributes();

		String emailAddress = LDAPUtil.getAttributeString(attributes, userMappings.get("emailAddress"));
		String firstName = LDAPUtil.getAttributeString(attributes, userMappings.get("firstName"));
		String lastName = LDAPUtil.getAttributeString(attributes, userMappings.get("lastName"));
		String jobTitle = LDAPUtil.getAttributeString(attributes, userMappings.get("jobTitle"));
		String password = StringUtil.toLowerCase(LDAPUtil.getAttributeString(attributes, userMappings.get("password")));
		String screenName = StringUtil.toLowerCase(LDAPUtil.getAttributeString(attributes, userMappings.get("screenName")));

		Attribute attribute = attributes.get(userMappings.get("group"));

		if ((PropsValues.USERS_EMAIL_ADDRESS_REQUIRED && Validator.isNull(emailAddress)) || Validator.isNull(firstName) || (fullNameDefinition.isFieldRequired("last-name") && Validator.isNull(lastName)) || Validator.isNull(password) || Validator.isNull(screenName)) {
			showMissingAttributeMessage = true;
		}
		%>

		<liferay-ui:search-container-column-text
			name="screenName"
			value="<%= HtmlUtil.escape(screenName) %>"
		/>

		<liferay-ui:search-container-column-text
			name="emailAddress"
			value="<%= HtmlUtil.escape(emailAddress) %>"
		/>

		<%@ include file="/com.liferay.portal.settings.web/test_ldap_users_user_name.jspf" %>

		<liferay-ui:search-container-column-text
			name="password"
			value="<%= Validator.isNotNull(password) ? StringPool.EIGHT_STARS : StringPool.BLANK %>"
		/>

		<liferay-ui:search-container-column-text
			name="job-title"
			value="<%= HtmlUtil.escape(jobTitle) %>"
		/>

		<liferay-ui:search-container-column-text
			name="group"
			value='<%= (attribute == null) ? "0" : String.valueOf(attribute.size()) %>'
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		paginate="<%= false %>"
	/>
</liferay-ui:search-container>

<c:if test="<%= showMissingAttributeMessage %>">
	<div class="alert alert-info">
		<liferay-ui:message key="the-above-results-include-users-which-are-missing-the-required-attributes-(screen-name,-password,-email-address,-first-name,-and-last-name).-these-users-will-not-be-imported-until-these-attributes-are-filled-in" />
	</div>
</c:if>

<%
if (safeLdapContext != null) {
	safeLdapContext.close();
}
%>