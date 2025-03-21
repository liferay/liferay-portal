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

<%@ page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration" %><%@
page import="com.liferay.portal.security.ldap.configuration.ConfigurationProvider" %><%@
page import="com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration" %><%@
page import="com.liferay.portal.security.ldap.constants.LDAPConstants" %><%@
page import="com.liferay.portal.security.ldap.exportimport.configuration.LDAPExportConfiguration" %><%@
page import="com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration" %><%@
page import="com.liferay.portal.settings.authentication.ldap.web.internal.portlet.constants.LDAPSettingsConstants" %><%@
page import="com.liferay.portal.settings.authentication.ldap.web.internal.portlet.util.ConfigurationProviderUtil" %>

<%@ page import="java.util.List" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />