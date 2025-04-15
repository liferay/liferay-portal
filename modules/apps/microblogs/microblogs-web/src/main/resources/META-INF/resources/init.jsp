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
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.microblogs.constants.MicroblogsEntryConstants" %><%@
page import="com.liferay.microblogs.exception.NoSuchEntryException" %><%@
page import="com.liferay.microblogs.model.MicroblogsEntry" %><%@
page import="com.liferay.microblogs.service.MicroblogsEntryLocalServiceUtil" %><%@
page import="com.liferay.microblogs.service.MicroblogsEntryServiceUtil" %><%@
page import="com.liferay.microblogs.util.comparator.EntryCreateDateComparator" %><%@
page import="com.liferay.microblogs.web.internal.display.context.MicroblogsDisplayContext" %><%@
page import="com.liferay.microblogs.web.internal.security.permission.resource.MicroblogsEntryPermission" %><%@
page import="com.liferay.microblogs.web.internal.security.permission.resource.MicroblogsPermission" %><%@
page import="com.liferay.microblogs.web.internal.util.MicroblogsWebUtil" %><%@
page import="com.liferay.microblogs.web.internal.util.WebKeys" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.PortletURLFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.ServiceContextFactory" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PrefsParamUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.social.kernel.model.SocialRelationConstants" %>

<%@ page import="jakarta.portlet.PortletRequest" %><%@
page import="jakarta.portlet.PortletURL" %><%@
page import="jakarta.portlet.WindowState" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.List" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
boolean showStatus = PrefsParamUtil.getBoolean(portletPreferences, request, "showStatus", true);

Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>