<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.layout.admin.web.internal.display.context.LockedLayoutDisplayContext" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
LockedLayoutDisplayContext lockedLayoutDisplayContext = (LockedLayoutDisplayContext)request.getAttribute(LockedLayoutDisplayContext.class.getName());
%>