<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.asset.util.AssetHelper" %><%@
page import="com.liferay.petra.reflect.ReflectionUtil" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.portal.kernel.portlet.PortletURLFactoryUtil" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.repository.model.FileEntry" %><%@
page import="com.liferay.portal.kernel.service.ServiceContextFactory" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.URLCodec" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.wiki.constants.WikiPortletKeys" %><%@
page import="com.liferay.wiki.constants.WikiWebKeys" %><%@
page import="com.liferay.wiki.model.WikiPage" %><%@
page import="com.liferay.wiki.model.WikiPageDisplay" %><%@
page import="com.liferay.wiki.service.WikiPageLocalServiceUtil" %><%@
page import="com.liferay.wiki.web.internal.frontend.taglib.clay.servlet.taglib.WikiPageAttachmentHorizontalCard" %>

<%@ page import="jakarta.portlet.PortletMode" %><%@
page import="jakarta.portlet.PortletRequest" %><%@
page import="jakarta.portlet.PortletURL" %><%@
page import="jakarta.portlet.WindowState" %>

<%@ page import="java.util.function.Supplier" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />