<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.frontend.esm.FrontendESMUtil" %><%@
page import="com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator" %><%@
page import="com.liferay.portal.kernel.settings.ParameterMapSettingsLocator" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.security.sso.token.configuration.TokenConfiguration" %><%@
page import="com.liferay.portal.security.sso.token.constants.TokenConfigurationKeys" %><%@
page import="com.liferay.portal.security.sso.token.constants.TokenConstants" %><%@
page import="com.liferay.portal.security.sso.token.security.auth.TokenLocation" %><%@
page import="com.liferay.portal.settings.authentication.token.web.internal.constants.PortalSettingsTokenConstants" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />