<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.security.auth.AuthTokenUtil" %><%@
page import="com.liferay.portal.kernel.security.auth.FullNameDefinition" %><%@
page import="com.liferay.portal.kernel.security.auth.FullNameDefinitionFactory" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.Portal" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PropertiesUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.security.ldap.DuplicateLDAPServerNameException" %><%@
page import="com.liferay.portal.security.ldap.LDAPServerNameException" %><%@
page import="com.liferay.portal.security.ldap.SafeLdapContext" %><%@
page import="com.liferay.portal.security.ldap.SafeLdapFilter" %><%@
page import="com.liferay.portal.security.ldap.SafeLdapFilterConstraints" %><%@
page import="com.liferay.portal.security.ldap.SafeLdapFilterFactory" %><%@
page import="com.liferay.portal.security.ldap.SafeLdapNameFactory" %><%@
page import="com.liferay.portal.security.ldap.SafePortalLDAP" %><%@
page import="com.liferay.portal.security.ldap.configuration.ConfigurationProvider" %><%@
page import="com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration" %><%@
page import="com.liferay.portal.security.ldap.constants.LDAPConstants" %><%@
page import="com.liferay.portal.security.ldap.util.LDAPUtil" %><%@
page import="com.liferay.portal.security.ldap.validator.LDAPFilterException" %><%@
page import="com.liferay.portal.security.ldap.validator.LDAPFilterValidator" %><%@
page import="com.liferay.portal.settings.authentication.ldap.web.internal.portlet.util.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.settings.authentication.ldap.web.internal.util.LDAPFilterValidatorUtil" %><%@
page import="com.liferay.portal.settings.authentication.ldap.web.internal.util.SafePortalLDAPUtil" %><%@
page import="com.liferay.portal.util.PropsValues" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.List" %><%@
page import="java.util.Properties" %>

<%@ page import="javax.naming.InvalidNameException" %><%@
page import="javax.naming.NameNotFoundException" %><%@
page import="javax.naming.directory.Attribute" %><%@
page import="javax.naming.directory.Attributes" %><%@
page import="javax.naming.directory.SearchResult" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
ConfigurationProvider<LDAPServerConfiguration> ldapServerConfigurationProvider = ConfigurationProviderUtil.getLDAPServerConfigurationProvider();
%>