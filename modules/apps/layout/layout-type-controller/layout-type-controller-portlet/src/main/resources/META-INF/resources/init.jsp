<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/layout" prefix="liferay-layout" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.layout.type.controller.portlet.internal.constants.PortletLayoutTypeControllerWebKeys" %><%@
page import="com.liferay.layout.type.controller.portlet.internal.display.context.PortletLayoutDisplayContext" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.model.Layout" %><%@
page import="com.liferay.portal.kernel.model.LayoutTemplateConstants" %><%@
page import="com.liferay.portal.kernel.model.LayoutTypePortlet" %><%@
page import="com.liferay.portal.kernel.model.Theme" %><%@
page import="com.liferay.portal.kernel.service.LayoutTemplateLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.layoutconfiguration.util.RuntimePageUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
Layout selLayout = (Layout)request.getAttribute(WebKeys.SEL_LAYOUT);
%>