<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/com.liferay.portal.settings.web/init.jsp" %>

<%
long ldapServerId = ParamUtil.getLong(request, "ldapServerId");

String baseProviderURL = ParamUtil.getString(request, "baseProviderURL");
String baseDN = ParamUtil.getString(request, "baseDN");
String principal = ParamUtil.getString(request, "principal");

String credentials = request.getParameter("credentials");

long companyId = ActionUtil.getCompanyId(request);

if (credentials.equals(Portal.TEMP_OBFUSCATION_VALUE)) {
	LDAPServerConfiguration ldapServerConfiguration = ldapServerConfigurationProvider.getConfiguration(companyId, ldapServerId);

	credentials = ldapServerConfiguration.securityCredential();
}

SafePortalLDAP safePortalLDAP = SafePortalLDAPUtil.getSafePortalLDAP();

SafeLdapContext safeLdapContext = safePortalLDAP.getSafeLdapContext(companyId, baseProviderURL, principal, credentials);

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

StringBundler sb = new StringBundler(23);

sb.append("screenName=");
sb.append(ParamUtil.getString(request, "userMappingScreenName"));
sb.append("\n");
sb.append("password=");
sb.append(ParamUtil.getString(request, "userMappingPassword"));
sb.append("\n");
sb.append("emailAddress=");
sb.append(ParamUtil.getString(request, "userMappingEmailAddress"));
sb.append("\n");
sb.append("fullName=");
sb.append(ParamUtil.getString(request, "userMappingFullName"));
sb.append("\n");
sb.append("firstName=");
sb.append(ParamUtil.getString(request, "userMappingFirstName"));
sb.append("\n");
sb.append("lastName=");
sb.append(ParamUtil.getString(request, "userMappingLastName"));
sb.append("\n");
sb.append("jobTitle=");
sb.append(ParamUtil.getString(request, "userMappingJobTitle"));
sb.append("\n");
sb.append("group=");
sb.append(ParamUtil.getString(request, "userMappingGroup"));

String userMappingsParams = sb.toString();

Properties userMappings = PropertiesUtil.load(userMappingsParams);

String[] attributeIds = StringUtil.split(StringUtil.merge(userMappings.values()));

List<SearchResult> searchResults = new ArrayList<SearchResult>();

if (Validator.isNotNull(userFilter) && !userFilter.equals(StringPool.STAR)) {
	try {
		safePortalLDAP.getUsers(themeDisplay.getCompanyId(), safeLdapContext, new byte[0], 20, SafeLdapNameFactory.fromUnsafe(baseDN), SafeLdapFilterFactory.fromUnsafeFilter(userFilter, ldapFilterValidator), attributeIds, searchResults);
	}
	catch (InvalidNameException | NameNotFoundException exception) {
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

		String emailAddress = LDAPUtil.getAttributeString(attributes, userMappings.getProperty("emailAddress"));
		String firstName = LDAPUtil.getAttributeString(attributes, userMappings.getProperty("firstName"));
		String lastName = LDAPUtil.getAttributeString(attributes, userMappings.getProperty("lastName"));
		String jobTitle = LDAPUtil.getAttributeString(attributes, userMappings.getProperty("jobTitle"));

		String password = StringUtil.toLowerCase(LDAPUtil.getAttributeString(attributes, userMappings.getProperty("password")));
		String screenName = StringUtil.toLowerCase(LDAPUtil.getAttributeString(attributes, userMappings.getProperty("screenName")));

		Attribute attribute = attributes.get(userMappings.getProperty("group"));

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