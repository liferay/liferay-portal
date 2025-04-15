<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.asset.kernel.exception.AssetTagException" %><%@
page import="com.liferay.asset.kernel.exception.AssetTagNameException" %><%@
page import="com.liferay.asset.kernel.exception.DuplicateTagException" %><%@
page import="com.liferay.asset.kernel.exception.NoSuchTagException" %><%@
page import="com.liferay.asset.kernel.model.AssetTag" %><%@
page import="com.liferay.asset.tags.admin.web.internal.display.context.AssetTagsDisplayContext" %><%@
page import="com.liferay.asset.tags.admin.web.internal.display.context.AssetTagsManagementToolbarDisplayContext" %><%@
page import="com.liferay.asset.util.AssetHelper" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.ModelHintsUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntriesUtil" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
AssetTagsDisplayContext assetTagsDisplayContext = new AssetTagsDisplayContext(request, renderRequest, renderResponse);
%>

<%@ include file="/init-ext.jsp" %>