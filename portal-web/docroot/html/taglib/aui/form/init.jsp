<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

--%>

<%@ include file="/html/taglib/taglib-init.jsp" %>

<%
java.lang.String action = GetterUtil.getString((java.lang.String)request.getAttribute("aui:form:action"));
java.lang.String cssClass = GetterUtil.getString((java.lang.String)request.getAttribute("aui:form:cssClass"));
boolean inlineLabels = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:form:inlineLabels")));
java.lang.String method = GetterUtil.getString((java.lang.String)request.getAttribute("aui:form:method"), "post");
java.lang.String name = GetterUtil.getString((java.lang.String)request.getAttribute("aui:form:name"), "fm");
java.lang.String onSubmit = GetterUtil.getString((java.lang.String)request.getAttribute("aui:form:onSubmit"));
java.lang.String portletNamespace = GetterUtil.getString((java.lang.String)request.getAttribute("aui:form:portletNamespace"));
boolean validateOnBlur = GetterUtil.getBoolean(String.valueOf(request.getAttribute("aui:form:validateOnBlur")), true);
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:form:dynamicAttributes");
%>

<%@ include file="/html/taglib/aui/form/init-ext.jspf" %>