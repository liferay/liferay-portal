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
taglib uri="http://liferay.com/tld/reading-time" prefix="liferay-reading-time" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.blogs.configuration.BlogsGroupServiceOverriddenConfiguration" %><%@
page import="com.liferay.blogs.constants.BlogsConstants" %><%@
page import="com.liferay.blogs.constants.BlogsPortletKeys" %><%@
page import="com.liferay.blogs.model.BlogsEntry" %><%@
page import="com.liferay.blogs.settings.BlogsGroupServiceSettings" %><%@
page import="com.liferay.blogs.web.internal.configuration.BlogsPortletInstanceConfiguration" %><%@
page import="com.liferay.blogs.web.internal.display.context.BlogsEditEntryDisplayContext" %><%@
page import="com.liferay.blogs.web.internal.security.permission.resource.BlogsPermission" %><%@
page import="com.liferay.blogs.web.internal.util.BlogsPortletInstanceConfigurationUtil" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.permission.GroupPermissionUtil" %><%@
page import="com.liferay.portal.kernel.settings.GroupServiceSettingsLocator" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.rss.util.RSSUtil" %><%@
page import="com.liferay.subscription.service.SubscriptionLocalServiceUtil" %>

<%@ page import="jakarta.portlet.PortletRequest" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />