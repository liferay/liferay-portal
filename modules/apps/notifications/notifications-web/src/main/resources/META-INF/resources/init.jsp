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
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %>

<%@ page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList" %><%@
page import="com.liferay.notifications.web.internal.constants.NotificationsPortletKeys" %><%@
page import="com.liferay.notifications.web.internal.display.context.NotificationsManagementToolbarDisplayContext" %><%@
page import="com.liferay.notifications.web.internal.search.UserNotificationEventRowChecker" %><%@
page import="com.liferay.notifications.web.internal.servlet.taglib.util.UserNotificationEventActionDropdownItem" %><%@
page import="com.liferay.notifications.web.internal.util.NotificationsUtil" %><%@
page import="com.liferay.notifications.web.internal.util.comparator.PortletIdComparator" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.json.JSONFactoryUtil" %><%@
page import="com.liferay.portal.kernel.json.JSONObject" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Portlet" %><%@
page import="com.liferay.portal.kernel.model.UserNotificationDelivery" %><%@
page import="com.liferay.portal.kernel.model.UserNotificationDeliveryConstants" %><%@
page import="com.liferay.portal.kernel.model.UserNotificationEvent" %><%@
page import="com.liferay.portal.kernel.notifications.UserNotificationDefinition" %><%@
page import="com.liferay.portal.kernel.notifications.UserNotificationDeliveryType" %><%@
page import="com.liferay.portal.kernel.notifications.UserNotificationFeedEntry" %><%@
page import="com.liferay.portal.kernel.notifications.UserNotificationManagerUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.PortletURLFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.service.PortletLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.ServiceContextFactory" %><%@
page import="com.liferay.portal.kernel.service.UserNotificationDeliveryLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserNotificationEventLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Time" %><%@
page import="com.liferay.portal.kernel.util.TreeMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<%@ page import="jakarta.portlet.PortletRequest" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>

<%@ include file="/init-ext.jsp" %>