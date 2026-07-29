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

if (Validator.isNull(ParamUtil.getString(request, "groupMappingGroupName")) || Validator.isNull(ParamUtil.getString(request, "groupMappingUser"))) {
%>

	<liferay-ui:message key="please-map-each-of-the-group-properties-group-name-and-user-to-an-ldap-attribute" />

<%
	return;
}

LDAPFilterValidator ldapFilterValidator = LDAPFilterValidatorUtil.getLDAPFilterValidator();

String groupFilter = ParamUtil.getString(request, "importGroupSearchFilter");

if (!ldapFilterValidator.isValid(groupFilter)) {
%>

	<liferay-ui:message key="please-enter-a-valid-ldap-search-filter" />

<%
	return;
}

SafeLdapFilter groupSafeLdapFilter = SafeLdapFilterFactory.fromUnsafeFilter(groupFilter, ldapFilterValidator);

Map<String, String> groupMappings = ldapTestDisplayContext.getGroupMappings();

String[] attributeIds = StringUtil.split(StringUtil.merge(groupMappings.values()));

List<SearchResult> searchResults = ldapTestDisplayContext.getGroupSearchResults(attributeIds, safeLdapContext, groupSafeLdapFilter);

if (searchResults == null) {
%>

	<liferay-ui:message key="please-enter-a-valid-ldap-base-dn" />

<%
	return;
}
%>

<liferay-ui:message key="test-ldap-groups" />

<br /><br />

<liferay-ui:message key="a-subset-of-groups-has-been-displayed-for-you-to-review" />

<br /><br />

<table class="lfr-table" width="100%">

	<%
	boolean showMissingAttributeMessage = false;

	int counter = 0;

	for (SearchResult searchResult : searchResults) {
		Attributes attributes = searchResult.getAttributes();

		String name = LDAPUtil.getAttributeString(attributes, groupMappings.get("groupName"));
		String description = LDAPUtil.getAttributeString(attributes, groupMappings.get("description"));
		Attribute attribute = attributes.get(groupMappings.get("user"));

		if (Validator.isNull(name)) {
			showMissingAttributeMessage = true;
		}

		if (attribute != null) {
			SafeLdapFilter safeLdapFilter = groupSafeLdapFilter.and(SafeLdapFilterConstraints.eq(groupMappings.get("groupName"), name));

			attribute = ldapTestDisplayContext.getMultivaluedAttribute(attribute, safeLdapContext, safeLdapFilter);
		}
	%>

		<c:if test="<%= counter == 0 %>">
			<col width="5%" />
			<col width="25%" />
			<col width="60%" />
			<col width="15%" />

			<tr>
				<th>
					#
				</th>
				<th>
					<liferay-ui:message key="name" />
				</th>
				<th>
					<liferay-ui:message key="description" />
				</th>
				<th>
					<liferay-ui:message key="members" />
				</th>
			</tr>
		</c:if>

		<%
		counter++;
		%>

		<tr>
			<td>
				<%= counter %>
			</td>
			<td>
				<%= HtmlUtil.escape(name) %>
			</td>
			<td>
				<%= HtmlUtil.escape(description) %>
			</td>
			<td>
				<%= (attribute == null) ? "0" : String.valueOf(attribute.size()) %>
			</td>
		</tr>

	<%
	}
	%>

	<c:if test="<%= counter == 0 %>">
		<tr>
			<td colspan="4">
				<liferay-ui:message key="no-groups-were-found" />
			</td>
		</tr>
	</c:if>
</table>

<c:if test="<%= showMissingAttributeMessage %>">
	<div class="alert alert-info">
		<liferay-ui:message key="the-above-results-include-groups-which-are-missing-the-required-attributes-(group-name-and-user).-these-groups-will-not-be-imported-until-these-attributes-are-filled-in" />
	</div>
</c:if>

<%
if (safeLdapContext != null) {
	safeLdapContext.close();
}
%>