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
taglib uri="http://liferay.com/tld/commerce-ui" prefix="commerce-ui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.commerce.constants.CommercePortletKeys" %><%@
page import="com.liferay.commerce.frontend.helper.HeaderHelperUtil" %><%@
page import="com.liferay.commerce.frontend.model.HeaderActionModel" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.BaseModel" %><%@
page import="com.liferay.portal.kernel.model.GroupedModel" %><%@
page import="com.liferay.portal.kernel.model.WorkflowedModel" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PortletKeys" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowTask" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.List" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
List<HeaderActionModel> actions = (List<HeaderActionModel>)request.getAttribute("liferay-commerce:header:actions");
String additionalStatusLabel = (String)request.getAttribute("liferay-commerce:header:additionalStatusLabel");
String additionalStatusLabelStyle = (String)request.getAttribute("liferay-commerce:header:additionalStatusLabelStyle");
Object bean = request.getAttribute("liferay-commerce:header:bean");
String beanIdLabel = (String)request.getAttribute("liferay-commerce:header:beanIdLabel");
String cssClasses = (String)request.getAttribute("liferay-commerce:header:cssClasses");
long displayBeanId = (long)request.getAttribute("liferay-commerce:header:displayBeanId");
List<DropdownItem> dropdownItems = (List<DropdownItem>)request.getAttribute("liferay-commerce:header:dropdownItems");
String externalReferenceCode = (String)request.getAttribute("liferay-commerce:header:externalReferenceCode");
String externalReferenceCodeEditUrl = (String)request.getAttribute("liferay-commerce:header:externalReferenceCodeEditUrl");
Class<?> model = (Class<?>)request.getAttribute("liferay-commerce:header:model");
String previewUrl = (String)request.getAttribute("liferay-commerce:header:previewUrl");
String thumbnailUrl = (String)request.getAttribute("liferay-commerce:header:thumbnailUrl");
String title = (String)request.getAttribute("liferay-commerce:header:title");
PortletURL transitionPortletURL = (PortletURL)request.getAttribute("liferay-commerce:header:transitionPortletURL");
String wrapperCssClasses = (String)request.getAttribute("liferay-commerce:header:wrapperCssClasses");

long beanId = 0;

BaseModel<?> beanBaseModel = (BaseModel)bean;

if (beanBaseModel != null) {
	beanId = (long)beanBaseModel.getPrimaryKeyObj();
}

if (displayBeanId == 0) {
	displayBeanId = beanId;
}

WorkflowTask reviewWorkflowTask = HeaderHelperUtil.getReviewWorkflowTask(themeDisplay.getCompanyId(), themeDisplay.getUserId(), beanId, model.getName());
%>