<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.security.fips.configuration.FIPSSessionConfiguration" %><%@
page import="com.liferay.portal.security.fips.constants.FIPSConstants" %><%@
page import="com.liferay.portal.security.fips.util.FIPSUtil" %><%@
page import="com.liferay.portal.security.fips.web.internal.constants.FIPSAdminWebKeys" %><%@
page import="com.liferay.portal.security.fips.web.internal.display.context.FIPSAdminDisplayContext" %>

<%@ page import="java.util.Objects" %>

<liferay-theme:defineObjects />

<%
FIPSAdminDisplayContext fipsAdminDisplayContext = (FIPSAdminDisplayContext)request.getAttribute(FIPSAdminWebKeys.FIPS_ADMIN_DISPLAY_CONTEXT);
%>