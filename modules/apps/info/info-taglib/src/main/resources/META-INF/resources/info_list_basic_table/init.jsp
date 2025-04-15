<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.info.item.renderer.InfoItemRenderer" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.taglib.servlet.PipingServletResponseFactory" %>

<%@ page import="java.util.List" %>

<liferay-theme:defineObjects />

<%
InfoItemRenderer<Object> infoItemRenderer = (InfoItemRenderer<Object>)request.getAttribute("liferay-info:info-list-basic-table:infoItemRenderer");
List<String> infoListObjectColumnNames = (List<String>)request.getAttribute("liferay-info:info-list-basic-table:infoListObjectColumnNames");
List<Object> infoListObjects = (List<Object>)request.getAttribute("liferay-info:info-list-basic-table:infoListObjects");
%>