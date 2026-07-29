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
%>

<c:choose>
	<c:when test="<%= safeLdapContext != null %>">
		<liferay-ui:message key="liferay-has-successfully-connected-to-the-ldap-server" />
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="liferay-has-failed-to-connect-to-the-ldap-server" />
	</c:otherwise>
</c:choose>

<%
if (safeLdapContext != null) {
	safeLdapContext.close();
}
%>