<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%@ page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %>

<%
List<DropdownItem> actionDropdownItems = (List<DropdownItem>)request.getAttribute("liferay-frontend:empty-result-message:actionDropdownItems");
Map<String, Object> additionalProps = (Map<String, Object>)request.getAttribute("liferay-frontend:empty-result-message:additionalProps");
String animationTypeCssClass = GetterUtil.getString((String)request.getAttribute("liferay-frontend:empty-result-message:animationTypeCssClass"));
String animationTypeSymbol = GetterUtil.getString((String)request.getAttribute("liferay-frontend:empty-result-message:animationTypeSymbol"));
String buttonCssClass = GetterUtil.getString((String)request.getAttribute("liferay-frontend:empty-result-message:buttonCssClass"));
String buttonPropsTransformer = GetterUtil.getString((String)request.getAttribute("liferay-frontend:empty-result-message:buttonPropsTransformer"));
String description = (String)request.getAttribute("liferay-frontend:empty-result-message:description");
String elementType = (String)request.getAttribute("liferay-frontend:empty-result-message:elementType");
String propsTransformer = (String)request.getAttribute("liferay-frontend:empty-result-message:propsTransformer");
ServletContext propsTransformerServletContext = (ServletContext)request.getAttribute("liferay-frontend:empty-result-message:propsTransformerServletContext");
String title = (String)request.getAttribute("liferay-frontend:empty-result-message:title");
%>