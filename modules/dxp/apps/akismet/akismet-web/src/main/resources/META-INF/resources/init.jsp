<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %>

<%@ page import="com.liferay.akismet.web.internal.util.ModerationUtil" %><%@
page import="com.liferay.message.boards.exception.NoSuchMessageException" %><%@
page import="com.liferay.message.boards.exception.RequiredMessageException" %><%@
page import="com.liferay.message.boards.model.MBMessage" %><%@
page import="com.liferay.message.boards.service.MBMessageLocalServiceUtil" %><%@
page import="com.liferay.message.boards.service.MBStatsUserLocalServiceUtil" %><%@
page import="com.liferay.message.boards.util.MBUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.dao.search.RowChecker" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.security.auth.PrincipalException" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>

<%@ page import="jakarta.portlet.PortletURL" %><%@
page import="jakarta.portlet.WindowState" %>

<%@ page import="java.text.DateFormat" %><%@
page import="java.text.Format" %>

<%@ page import="java.util.Date" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
PortletURL portletURL = renderResponse.createRenderURL();

DateFormat longDateFormatDate = DateFormat.getDateInstance(DateFormat.LONG, locale);

longDateFormatDate.setTimeZone(timeZone);

Format dateFormat = FastDateFormatFactoryUtil.getDate(locale, timeZone);
%>