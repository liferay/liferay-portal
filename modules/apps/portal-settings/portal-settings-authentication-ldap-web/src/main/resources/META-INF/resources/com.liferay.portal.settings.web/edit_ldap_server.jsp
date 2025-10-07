<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/com.liferay.portal.settings.web/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL", redirect);

long ldapServerId = ParamUtil.getLong(request, "ldapServerId");

LDAPServerConfiguration ldapServerConfiguration = ldapServerConfigurationProvider.getConfiguration(ActionUtil.getCompanyId(request), ldapServerId);

String ldapServerName = ldapServerConfiguration.serverName();
String ldapBaseProviderUrl = ldapServerConfiguration.baseProviderURL();
String ldapBaseDN = ldapServerConfiguration.baseDN();
String ldapSecurityPrincipal = ldapServerConfiguration.securityPrincipal();

String ldapSecurityCredentials = Portal.TEMP_OBFUSCATION_VALUE;

String ldapAuthSearchFilter = ldapServerConfiguration.authSearchFilter();
String ldapUserSearchFilter = ldapServerConfiguration.userSearchFilter();
boolean ignoreUserAuthFilterForAuth = ldapServerConfiguration.ignoreUserSearchFilterForAuth();
String ldapGroupSearchFilter = ldapServerConfiguration.groupSearchFilter();
String ldapUsersDN = ldapServerConfiguration.usersDN();
String[] ldapUserDefaultObjectClasses = ldapServerConfiguration.userDefaultObjectClasses();
String ldapGroupsDN = ldapServerConfiguration.groupsDN();
String[] ldapGroupDefaultObjectClasses = ldapServerConfiguration.groupDefaultObjectClasses();

String[] userMappingArray = ldapServerConfiguration.userMappings();

String userMappingEmailAddress = StringPool.BLANK;
String userMappingFirstName = StringPool.BLANK;
String userMappingFullName = StringPool.BLANK;
String userMappingGroup = StringPool.BLANK;
String userMappingJobTitle = StringPool.BLANK;
String userMappingLastName = StringPool.BLANK;
String userMappingMiddleName = StringPool.BLANK;
String userMappingPassword = StringPool.BLANK;
String userMappingPortrait = StringPool.BLANK;
String userMappingScreenName = StringPool.BLANK;
String userMappingStatus = StringPool.BLANK;
String userMappingUuid = StringPool.BLANK;

for (int i = 0; i < userMappingArray.length; i++) {
	if (!userMappingArray[i].contains("=")) {
		continue;
	}

	String[] mapping = userMappingArray[i].split("=");

	if (mapping.length != 2) {
		continue;
	}

	if (mapping[0].equals("emailAddress")) {
		userMappingEmailAddress = mapping[1];
	}
	else if (mapping[0].equals("firstName")) {
		userMappingFirstName = mapping[1];
	}
	else if (mapping[0].equals("fullName")) {
		userMappingFullName = mapping[1];
	}
	else if (mapping[0].equals("group")) {
		userMappingGroup = mapping[1];
	}
	else if (mapping[0].equals("jobTitle")) {
		userMappingJobTitle = mapping[1];
	}
	else if (mapping[0].equals("lastName")) {
		userMappingLastName = mapping[1];
	}
	else if (mapping[0].equals("middleName")) {
		userMappingMiddleName = mapping[1];
	}
	else if (mapping[0].equals("password")) {
		userMappingPassword = mapping[1];
	}
	else if (mapping[0].equals("portrait")) {
		userMappingPortrait = mapping[1];
	}
	else if (mapping[0].equals("screenName")) {
		userMappingScreenName = mapping[1];
	}
	else if (mapping[0].equals("status")) {
		userMappingStatus = mapping[1];
	}
	else if (mapping[0].equals("uuid")) {
		userMappingUuid = mapping[1];
	}

	mapping[1] = "";
}

String[] groupMappingArray = ldapServerConfiguration.groupMappings();

String groupMappingDescription = StringPool.BLANK;
String groupMappingGroupName = StringPool.BLANK;
String groupMappingUser = StringPool.BLANK;

for (int i = 0; i < groupMappingArray.length; i++) {
	if (!groupMappingArray[i].contains("=")) {
		continue;
	}

	String[] mapping = groupMappingArray[i].split("=");

	if (mapping.length != 2) {
		continue;
	}

	if (mapping[0].equals("description")) {
		groupMappingDescription = mapping[1];
	}
	else if (mapping[0].equals("groupName")) {
		groupMappingGroupName = mapping[1];
	}
	else if (mapping[0].equals("user")) {
		groupMappingUser = mapping[1];
	}
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);

renderResponse.setTitle((ldapServerId == 0) ? LanguageUtil.get(resourceBundle, "add-ldap-server") : LanguageUtil.get(resourceBundle, "edit-ldap-server"));
%>

<portlet:actionURL name="/portal_settings_authentication_ldap/edit_ldap_server" var="editLDAPServerURL" />

<aui:form action="<%= editLDAPServerURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "saveEntry(false);" %>'>
	<liferay-ui:error exception="<%= DuplicateLDAPServerNameException.class %>" message="please-enter-a-unique-ldap-server-name" />
	<liferay-ui:error exception="<%= LDAPFilterException.class %>" message="please-enter-a-valid-ldap-search-filter" />
	<liferay-ui:error exception="<%= LDAPServerNameException.class %>" message="please-enter-a-valid-ldap-server-name" />

	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="ldapServerId" type="hidden" value="<%= ldapServerId %>" />

	<liferay-ui:error key="ldapAuthentication" message="failed-to-bind-to-the-ldap-server-with-given-values" />

	<div class="sheet">
		<div class="panel-group panel-group-flush">
			<aui:fieldset>
				<aui:input cssClass="lfr-input-text-container" label="server-name" name='<%= "ldap--" + LDAPConstants.SERVER_NAME + "--" %>' required="<%= true %>" type="text" value="<%= ldapServerName %>" />

				<aui:input cssClass="lfr-input-text-container" helpMessage="ldap-clock-skew-description" label="clock-skew" name='<%= "ldap--" + LDAPConstants.CLOCK_SKEW + "--" %>' type="text" value="<%= ldapServerConfiguration.clockSkew() %>" />
			</aui:fieldset>

			<aui:fieldset>
				<h3><liferay-ui:message key="default-values" /></h3>

				<aui:field-wrapper label="load-default-server-configuration-for">
					<aui:input label="Apache Directory Server" name="defaultLdap" onClick='<%= liferayPortletResponse.getNamespace() + "updateDefaultLdap('apache');" %>' type="radio" value="apache" />
					<aui:input label="Fedora Directory Server" name="defaultLdap" onClick='<%= liferayPortletResponse.getNamespace() + "updateDefaultLdap('fedora');" %>' type="radio" value="fedora" />
					<aui:input label="Microsoft Active Directory Server" name="defaultLdap" onClick='<%= liferayPortletResponse.getNamespace() + "updateDefaultLdap('microsoft');" %>' type="radio" value="microsoft" />
					<aui:input label="Novell eDirectory" name="defaultLdap" onClick='<%= liferayPortletResponse.getNamespace() + "updateDefaultLdap('novell');" %>' type="radio" value="novell" />
					<aui:input label="OpenLDAP" name="defaultLdap" onClick='<%= liferayPortletResponse.getNamespace() + "updateDefaultLdap('open');" %>' type="radio" value="open" />
					<aui:input label="other-directory-server" name="defaultLdap" onClick='<%= liferayPortletResponse.getNamespace() + "updateDefaultLdap('other');" %>' type="radio" value="other" />
				</aui:field-wrapper>
			</aui:fieldset>

			<aui:fieldset>
				<h3><liferay-ui:message key="connection" /></h3>

				<aui:input cssClass="lfr-input-text-container" helpMessage="the-ldap-url-format-is" label="base-provider-url" name='<%= "ldap--" + LDAPConstants.BASE_PROVIDER_URL + "--" %>' type="text" value="<%= ldapBaseProviderUrl %>" />

				<aui:input cssClass="lfr-input-text-container" helpMessage="the-ldap-url-format-is" label="base-dn" name='<%= "ldap--" + LDAPConstants.BASE_DN + "--" %>' type="text" value="<%= ldapBaseDN %>" />

				<aui:input cssClass="lfr-input-text-container" label="principal" name='<%= "ldap--" + LDAPConstants.SECURITY_PRINCIPAL + "--" %>' type="text" value="<%= ldapSecurityPrincipal %>" />

				<aui:input cssClass="lfr-input-text-container" label="credentials" name='<%= "ldap--" + LDAPConstants.SECURITY_CREDENTIAL + "--" %>' type="password" value="<%= ldapSecurityCredentials %>" />

				<aui:button-row>

					<%
					String taglibOnClick = liferayPortletResponse.getNamespace() + "testSettings('ldapConnection');";
					%>

					<aui:button onClick="<%= taglibOnClick %>" value="test-ldap-connection" />
				</aui:button-row>
			</aui:fieldset>

			<aui:fieldset>
				<h3><liferay-ui:message key="users" /></h3>

				<aui:input cssClass="lfr-input-text-container" helpMessage="authentication-search-filter-help" label="authentication-search-filter" name='<%= "ldap--" + LDAPConstants.AUTH_SEARCH_FILTER + "--" %>' type="text" value="<%= ldapAuthSearchFilter %>" />

				<aui:input cssClass="lfr-input-text-container" label="import-search-filter" name='<%= "ldap--" + LDAPConstants.USER_SEARCH_FILTER + "--" %>' type="text" value="<%= ldapUserSearchFilter %>" />

				<aui:input cssClass="lfr-input-text-container" helpMessage="ignore-user-search-filter-for-auth-help" label="ignore-user-search-filter-for-auth" name='<%= "ldap--" + LDAPConstants.INGORE_USER_SEARCH_FILTER_FOR_AUTH + "--" %>' type="checkbox" value="<%= ignoreUserAuthFilterForAuth %>" />

				<div class="h4"><liferay-ui:message key="user-mapping" /></div>

				<aui:input cssClass="lfr-input-text-container" label="uuid" name="userMappingUuid" type="text" value="<%= userMappingUuid %>" />

				<aui:input cssClass="lfr-input-text-container" label="screen-name" name="userMappingScreenName" type="text" value="<%= userMappingScreenName %>" />

				<aui:input cssClass="lfr-input-text-container" label="email-address" name="userMappingEmailAddress" type="text" value="<%= userMappingEmailAddress %>" />

				<aui:input cssClass="lfr-input-text-container" label="password" name="userMappingPassword" type="text" value="<%= userMappingPassword %>" />

				<%@ include file="/com.liferay.portal.settings.web/edit_ldap_server_user_name.jspf" %>

				<aui:input cssClass="lfr-input-text-container" label="job-title" name="userMappingJobTitle" type="text" value="<%= userMappingJobTitle %>" />

				<aui:input cssClass="lfr-input-text-container" label="status" name="userMappingStatus" type="text" value="<%= userMappingStatus %>" />

				<aui:input cssClass="lfr-input-text-container" label="group" name="userMappingGroup" type="text" value="<%= userMappingGroup %>" />

				<aui:input cssClass="lfr-input-text-container" label="portrait" name="userMappingPortrait" type="text" value="<%= userMappingPortrait %>" />

				<aui:input cssClass="lfr-textarea" helpMessage="enter-properties-file-synxtax-comma-delimiter" label="custom-user-mapping" name='<%= "ldap--" + LDAPConstants.USER_CUSTOM_MAPPINGS + "--" %>' type="textarea" value="<%= StringUtil.merge(ldapServerConfiguration.userCustomMappings(), StringPool.COMMA) %>" />

				<aui:input cssClass="lfr-textarea" helpMessage="enter-properties-file-synxtax-comma-delimiter" label="custom-contact-mapping" name='<%= "ldap--" + LDAPConstants.CONTACT_CUSTOM_MAPPINGS + "--" %>' type="textarea" value="<%= StringUtil.merge(ldapServerConfiguration.contactCustomMappings(), StringPool.COMMA) %>" />

				<aui:input cssClass="lfr-textarea" helpMessage="enter-properties-file-synxtax-comma-delimiter" label="user-ignore-attributes" name='<%= "ldap--" + LDAPConstants.USER_IGNORE_ATTRIBUTES + "--" %>' type="textarea" value="<%= StringUtil.merge(ldapServerConfiguration.userIgnoreAttributes(), StringPool.COMMA) %>" />

				<aui:input name='<%= "ldap--" + LDAPConstants.USER_MAPPINGS + "--" %>' type="hidden" />

				<aui:input name='<%= "ldap--" + LDAPConstants.CONTACT_MAPPINGS + "--" %>' type="hidden" value="<%= StringUtil.merge(ldapServerConfiguration.contactMappings(), StringPool.COMMA) %>" />

				<aui:button-row>

					<%
					String taglibOnClick = liferayPortletResponse.getNamespace() + "testSettings('ldapUsers');";
					%>

					<aui:button onClick="<%= taglibOnClick %>" value="test-ldap-users" />
				</aui:button-row>
			</aui:fieldset>

			<aui:fieldset>
				<h3><liferay-ui:message key="groups" /></h3>

				<aui:input cssClass="lfr-input-text-container" label="import-search-filter" name='<%= "ldap--" + LDAPConstants.GROUP_SEARCH_FILTER + "--" %>' type="text" value="<%= ldapGroupSearchFilter %>" />

				<div class="h4"><liferay-ui:message key="group-mapping" /></div>

				<aui:input cssClass="lfr-input-text-container" label="group-name" name="groupMappingGroupName" type="text" value="<%= groupMappingGroupName %>" />

				<aui:input cssClass="lfr-input-text-container" label="description" name="groupMappingDescription" type="text" value="<%= groupMappingDescription %>" />

				<aui:input cssClass="lfr-input-text-container" label="user" name="groupMappingUser" type="text" value="<%= groupMappingUser %>" />

				<aui:input name='<%= "ldap--" + LDAPConstants.GROUP_MAPPINGS + "--" %>' type="hidden" />

				<aui:button-row>

					<%
					String taglibOnClick = liferayPortletResponse.getNamespace() + "testSettings('ldapGroups');";
					%>

					<aui:button onClick="<%= taglibOnClick %>" value="test-ldap-groups" />
				</aui:button-row>
			</aui:fieldset>

			<aui:fieldset>
				<h3><liferay-ui:message key="export" /></h3>

				<aui:input cssClass="lfr-input-text-container" label="users-dn" name='<%= "ldap--" + LDAPConstants.USERS_DN + "--" %>' type="text" value="<%= ldapUsersDN %>" />

				<aui:input cssClass="lfr-input-text-container" label="user-default-object-classes" name='<%= "ldap--" + LDAPConstants.USER_DEFAULT_OBJECT_CLASSES + "--" %>' type="text" value="<%= StringUtil.merge(ldapUserDefaultObjectClasses, StringPool.COMMA) %>" />

				<aui:input cssClass="lfr-input-text-container" label="groups-dn" name='<%= "ldap--" + LDAPConstants.GROUPS_DN + "--" %>' type="text" value="<%= ldapGroupsDN %>" />

				<aui:input cssClass="lfr-input-text-container" label="group-default-object-classes" name='<%= "ldap--" + LDAPConstants.GROUP_DEFAULT_OBJECT_CLASSES + "--" %>' type="text" value="<%= StringUtil.merge(ldapGroupDefaultObjectClasses, StringPool.COMMA) %>" />
			</aui:fieldset>
		</div>
	</div>

	<aui:button-row>
		<aui:button name="saveButton" onClick='<%= liferayPortletResponse.getNamespace() + "saveLdap();" %>' value="save" />

		<aui:button href="<%= redirect %>" name="cancelButton" type="cancel" />
	</aui:button-row>
</aui:form>

<aui:script>
	function <portlet:namespace />mapValues(fields, fieldValues) {
		var form = document.<portlet:namespace />fm;

		return fields.reduce((prev, item, index) => {
			var mappingElement = Liferay.Util.getFormElement(
				form,
				fieldValues[index]
			);

			if (mappingElement && mappingElement.value) {
				prev += item + '=' + mappingElement.value + ',';
			}

			return prev;
		}, '');
	}

	function <portlet:namespace />saveLdap() {
		var userMappingFields = [
			'emailAddress',
			'firstName',
			'fullName',
			'group',
			'jobTitle',
			'lastName',
			'middleName',
			'password',
			'portrait',
			'screenName',
			'status',
			'uuid',
		];
		var userMappingFieldValues = [
			'userMappingEmailAddress',
			'userMappingFirstName',
			'userMappingFullName',
			'userMappingGroup',
			'userMappingJobTitle',
			'userMappingLastName',
			'userMappingMiddleName',
			'userMappingPassword',
			'userMappingPortrait',
			'userMappingScreenName',
			'userMappingStatus',
			'userMappingUuid',
		];

		var form = document.<portlet:namespace />fm;

		var userMapping = <portlet:namespace />mapValues(
			userMappingFields,
			userMappingFieldValues
		);

		var groupMappingFields = ['description', 'groupName', 'user'];
		var groupMappingFieldValues = [
			'groupMappingDescription',
			'groupMappingGroupName',
			'groupMappingUser',
		];

		var groupMapping = <portlet:namespace />mapValues(
			groupMappingFields,
			groupMappingFieldValues
		);

		Liferay.Util.postForm(form, {
			data: {
				'<%= Constants.CMD %>':
					'<%= (ldapServerId <= 0) ? Constants.ADD : Constants.UPDATE %>',
				'ldap--<%= LDAPConstants.USER_MAPPINGS %>--': userMapping,
				'ldap--<%= LDAPConstants.GROUP_MAPPINGS %>--': groupMapping,
			},
		});
	}

	function <portlet:namespace />updateDefaultLdap(ldapType) {
		var baseDN = '';
		var baseProviderURL = '';
		var credentials = '';
		var exportMappingGroupDefaultObjectClass = '';
		var exportMappingUserDefaultObjectClass = '';
		var groupMappingDescription = '';
		var groupMappingGroupName = '';
		var groupMappingUser = '';
		var importGroupSearchFilter = '';
		var importUserSearchFilter = '';
		var principal = '';
		var searchFilter = '';
		var userMappingEmailAddress = '';
		var userMappingFirstName = '';
		var userMappingFullName = '';
		var userMappingGroup = '';
		var userMappingJobTitle = '';
		var userMappingLastName = '';
		var userMappingMiddleName = '';
		var userMappingPassword = '';
		var userMappingPortrait = '';
		var userMappingScreenName = '';
		var userMappingStatus = '';
		var userMappingUuid = '';

		if (ldapType === 'apache') {
			baseDN = 'dc=example,dc=com';
			baseProviderURL = 'ldap://localhost:10389';
			credentials = 'secret';
			exportMappingGroupDefaultObjectClass = 'top,groupOfUniqueNames';
			exportMappingUserDefaultObjectClass =
				'top,person,inetOrgPerson,organizationalPerson';
			groupMappingDescription = 'description';
			groupMappingGroupName = 'cn';
			groupMappingUser = 'uniqueMember';
			importGroupSearchFilter = '(objectClass=groupOfUniqueNames)';
			importUserSearchFilter = '(objectClass=person)';
			principal = 'uid=admin,ou=system';
			searchFilter = '(mail=@email_address@)';
			userMappingEmailAddress = 'mail';
			userMappingFirstName = 'givenName';
			userMappingJobTitle = 'title';
			userMappingLastName = 'sn';
			userMappingPassword = 'userPassword';
			userMappingScreenName = 'cn';
		}
		else if (ldapType === 'fedora') {
			baseDN = 'dc=localdomain';
			baseProviderURL = 'ldap://localhost:19389';
			importUserSearchFilter = '(objectClass=inetOrgPerson)';
			principal = 'cn=Directory Manager';
			searchFilter = '(mail=@email_address@)';
			userMappingEmailAddress = 'mail';
			userMappingFirstName = 'givenName';
			userMappingFullName = 'cn';
			userMappingJobTitle = 'title';
			userMappingLastName = 'sn';
			userMappingPassword = 'userPassword';
			userMappingScreenName = 'uid';
		}
		else if (ldapType === 'microsoft') {
			baseDN = 'dc=example,dc=com';
			baseProviderURL = 'ldap://localhost:389';
			credentials = 'secret';
			groupMappingDescription = 'sAMAccountName';
			groupMappingGroupName = 'cn';
			groupMappingUser = 'member';
			importGroupSearchFilter = '(objectClass=group)';
			importUserSearchFilter = '(objectClass=person)';
			principal = 'admin';
			searchFilter = '(&(objectCategory=person)(sAMAccountName=@user_id@))';
			userMappingEmailAddress = 'userprincipalname';
			userMappingFirstName = 'givenName';
			userMappingFullName = 'cn';
			userMappingGroup = 'memberOf';
			userMappingLastName = 'sn';
			userMappingMiddleName = 'middleName';
			userMappingPassword = 'unicodePwd';
			userMappingScreenName = 'sAMAccountName';
		}
		else if (ldapType === 'novell') {
			baseProviderURL = 'ldap://localhost:389';
			credentials = 'secret';
			principal = 'cn=admin,ou=test';
			searchFilter = '(mail=@email_address@)';
			userMappingEmailAddress = 'mail';
			userMappingFirstName = 'givenName';
			userMappingJobTitle = 'title';
			userMappingLastName = 'sn';
			userMappingPassword = 'userPassword';
			userMappingScreenName = 'cn';
		}
		else if (ldapType === 'open') {
			baseDN = 'dc=example,dc=com';
			baseProviderURL = 'ldap://localhost:389';
			credentials = 'secret';
			groupMappingDescription = 'description';
			groupMappingGroupName = 'cn';
			groupMappingUser = 'uniqueMember';
			importGroupSearchFilter = '(objectClass=groupOfUniqueNames)';
			importUserSearchFilter = '(objectClass=inetOrgPerson)';
			principal = 'cn=admin,ou=test';
			searchFilter = '(mail=@email_address@)';
			userMappingEmailAddress = 'mail';
			userMappingFirstName = 'givenName';
			userMappingJobTitle = 'title';
			userMappingLastName = 'sn';
			userMappingPassword = 'userPassword';
			userMappingScreenName = 'cn';
		}

		Liferay.Util.setFormValues(document.<portlet:namespace />fm, {
			'ldap--<%= LDAPConstants.BASE_PROVIDER_URL %>--': baseProviderURL,
			'ldap--<%= LDAPConstants.BASE_DN %>--': baseDN,
			'ldap--<%= LDAPConstants.SECURITY_PRINCIPAL %>--': principal,
			'ldap--<%= LDAPConstants.SECURITY_CREDENTIAL %>--': credentials,
			'ldap--<%= LDAPConstants.AUTH_SEARCH_FILTER %>--': searchFilter,
			'ldap--<%= LDAPConstants.USER_SEARCH_FILTER %>--':
				importUserSearchFilter,
			'userMappingEmailAddress': userMappingEmailAddress,
			'userMappingFirstName': userMappingFirstName,
			'userMappingFullName': userMappingFullName,
			'userMappingGroup': userMappingGroup,
			'userMappingJobTitle': userMappingJobTitle,
			'userMappingLastName': userMappingLastName,
			'userMappingMiddleName': userMappingMiddleName,
			'userMappingPassword': userMappingPassword,
			'userMappingPortrait': userMappingPortrait,
			'userMappingScreenName': userMappingScreenName,
			'userMappingStatus': userMappingStatus,
			'userMappingUuid': userMappingUuid,
			'ldap--<%= LDAPConstants.GROUP_SEARCH_FILTER %>--':
				importGroupSearchFilter,
			'groupMappingDescription': groupMappingDescription,
			'groupMappingGroupName': groupMappingGroupName,
			'groupMappingUser': groupMappingUser,
			'ldap--<%= LDAPConstants.USERS_DN %>--': baseDN,
			'ldap--<%= LDAPConstants.USER_DEFAULT_OBJECT_CLASSES %>--':
				exportMappingUserDefaultObjectClass,
			'ldap--<%= LDAPConstants.GROUPS_DN %>--': baseDN,
			'ldap--<%= LDAPConstants.GROUP_DEFAULT_OBJECT_CLASSES %>--':
				exportMappingGroupDefaultObjectClass,
		});
	}

	window['<portlet:namespace />testSettings'] = function (type) {
		var baseUrl;

		var data = {
			p_auth: '<%= AuthTokenUtil.getToken(request) %>',
		};

		if (type === 'ldapConnection') {
			baseUrl =
				'<portlet:renderURL copyCurrentRenderParameters="<%= false %>" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="mvcRenderCommandName" value="/portal_settings_authentication_ldap/test_ldap_connection" /></portlet:renderURL>';
		}
		else if (type === 'ldapGroups') {
			baseUrl =
				'<portlet:renderURL copyCurrentRenderParameters="<%= false %>" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="mvcRenderCommandName" value="/portal_settings_authentication_ldap/test_ldap_groups" /></portlet:renderURL>';

			data.<portlet:namespace />importGroupSearchFilter =
				document.<portlet:namespace />fm[
					'<portlet:namespace />ldap--<%= LDAPConstants.GROUP_SEARCH_FILTER %>--'
				].value;
			data.<portlet:namespace />groupMappingDescription =
				document.<portlet:namespace />fm[
					'<portlet:namespace />groupMappingDescription'
				].value;
			data.<portlet:namespace />groupMappingGroupName =
				document.<portlet:namespace />fm[
					'<portlet:namespace />groupMappingGroupName'
				].value;
			data.<portlet:namespace />groupMappingUser =
				document.<portlet:namespace />fm[
					'<portlet:namespace />groupMappingUser'
				].value;
		}
		else if (type === 'ldapUsers') {
			baseUrl =
				'<portlet:renderURL copyCurrentRenderParameters="<%= false %>" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="mvcRenderCommandName" value="/portal_settings_authentication_ldap/test_ldap_users" /></portlet:renderURL>';

			data.<portlet:namespace />importUserSearchFilter =
				document.<portlet:namespace />fm[
					'<portlet:namespace />ldap--<%= LDAPConstants.USER_SEARCH_FILTER %>--'
				].value;
			data.<portlet:namespace />userMappingEmailAddress =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingEmailAddress'
				].value;
			data.<portlet:namespace />userMappingFirstName =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingFirstName'
				].value;
			data.<portlet:namespace />userMappingFullName =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingFullName'
				].value;
			data.<portlet:namespace />userMappingGroup =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingGroup'
				].value;
			data.<portlet:namespace />userMappingJobTitle =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingJobTitle'
				].value;
			data.<portlet:namespace />userMappingLastName =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingLastName'
				].value;
			data.<portlet:namespace />userMappingMiddleName =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingMiddleName'
				].value;
			data.<portlet:namespace />userMappingPassword =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingPassword'
				].value;
			data.<portlet:namespace />userMappingPortrait =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingPortrait'
				].value;
			data.<portlet:namespace />userMappingScreenName =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingScreenName'
				].value;
			data.<portlet:namespace />userMappingStatus =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingStatus'
				].value;
			data.<portlet:namespace />userMappingUuid =
				document.<portlet:namespace />fm[
					'<portlet:namespace />userMappingUuid'
				].value;
		}

		if (baseUrl != null) {
			data.<portlet:namespace />ldapServerId =
				document.<portlet:namespace />fm[
					'<portlet:namespace />ldapServerId'
				].value;
			data.<portlet:namespace />baseProviderURL =
				document.<portlet:namespace />fm[
					'<portlet:namespace />ldap--<%= LDAPConstants.BASE_PROVIDER_URL %>--'
				].value;
			data.<portlet:namespace />baseDN =
				document.<portlet:namespace />fm[
					'<portlet:namespace />ldap--<%= LDAPConstants.BASE_DN %>--'
				].value;
			data.<portlet:namespace />principal =
				document.<portlet:namespace />fm[
					'<portlet:namespace />ldap--<%= LDAPConstants.SECURITY_PRINCIPAL %>--'
				].value;
			data.<portlet:namespace />credentials =
				document.<portlet:namespace />fm[
					'<portlet:namespace />ldap--<%= LDAPConstants.SECURITY_CREDENTIAL %>--'
				].value;

			Liferay.Util.fetch(new URL(baseUrl), {
				body: Liferay.Util.objectToURLSearchParams(data),
				method: 'POST',
			})
				.then((response) => {
					return response.text();
				})
				.then((text) => {
					Liferay.Util.openModal({
						bodyHTML: text,
						size: 'full-screen',
						title: '<%= UnicodeLanguageUtil.get(request, "ldap") %>',
					});
				})
				.catch((error) => {
					Liferay.Util.openToast({
						message: Liferay.Language.get(
							'an-unexpected-system-error-occurred'
						),
						type: 'danger',
					});
				});
		}
	};
</aui:script>

<%
PortalUtil.addPortletBreadcrumbEntry(request, (ldapServerId == 0) ? LanguageUtil.get(request, "add-ldap-server") : ldapServerName, currentURL);
%>