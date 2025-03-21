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
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.layout.set.prototype.constants.LayoutSetPrototypePortletKeys" %><%@
page import="com.liferay.layout.set.prototype.web.internal.display.context.LayoutSetPrototypeDisplayContext" %><%@
page import="com.liferay.layout.set.prototype.web.internal.display.context.LayoutSetPrototypeManagementToolbarDisplayContext" %><%@
page import="com.liferay.layout.set.prototype.web.internal.frontend.taglib.clay.servlet.taglib.LayoutSetPrototypeVerticalCard" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchLayoutSetPrototypeException" %><%@
page import="com.liferay.portal.kernel.exception.RequiredLayoutSetPrototypeException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.LayoutSetPrototype" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutSetPrototypeServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.LayoutSetPrototypePermissionUtil" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.kernel.util.CustomJspRegistryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.model.impl.LayoutSetPrototypeImpl" %><%@
page import="com.liferay.portal.util.PropsValues" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.Date" %><%@
page import="java.util.Set" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
LayoutSetPrototypeDisplayContext layoutSetPrototypeDisplayContext = new LayoutSetPrototypeDisplayContext(request, renderRequest, renderResponse);
%>

<%@ include file="/init-ext.jsp" %>