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
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.frontend.taglib.servlet.taglib.util.EmptyResultMessageKeys" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.exception.LayoutFriendlyURLException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.servlet.SessionErrors" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.DateUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.URLCodec" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.redirect.exception.CircularRedirectEntryException" %><%@
page import="com.liferay.redirect.exception.DuplicateRedirectEntrySourceURLException" %><%@
page import="com.liferay.redirect.exception.RequiredRedirectEntryDestinationURLException" %><%@
page import="com.liferay.redirect.exception.RequiredRedirectEntrySourceURLException" %><%@
page import="com.liferay.redirect.web.internal.display.context.EditRedirectEntryDisplayContext" %><%@
page import="com.liferay.redirect.web.internal.display.context.RedirectDisplayContext" %><%@
page import="com.liferay.redirect.web.internal.display.context.RedirectEntriesDisplayContext" %><%@
page import="com.liferay.redirect.web.internal.display.context.RedirectEntryInfoPanelDisplayContext" %><%@
page import="com.liferay.redirect.web.internal.display.context.RedirectNotFoundEntriesDisplayContext" %><%@
page import="com.liferay.redirect.web.internal.display.context.RedirectPatternConfigurationDisplayContext" %><%@
page import="com.liferay.redirect.web.internal.util.RedirectUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />