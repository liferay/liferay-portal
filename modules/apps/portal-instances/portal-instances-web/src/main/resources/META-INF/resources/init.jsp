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
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.instances.web.internal.constants.PortalInstancesWebKeys" %><%@
page import="com.liferay.portal.instances.web.internal.display.context.PortalInstancesManagementToolbarDisplayContext" %><%@
page import="com.liferay.portal.instances.web.internal.taglib.util.CompanyActionDropdownItems" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.exception.CompanyMxException" %><%@
page import="com.liferay.portal.kernel.exception.CompanyVirtualHostException" %><%@
page import="com.liferay.portal.kernel.exception.CompanyWebIdException" %><%@
page import="com.liferay.portal.kernel.exception.RequiredCompanyException" %><%@
page import="com.liferay.portal.kernel.instance.PortalInstancePool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Company" %><%@
page import="com.liferay.portal.kernel.model.VirtualHost" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.auth.FullNameDefinition" %><%@
page import="com.liferay.portal.kernel.security.auth.FullNameDefinitionFactory" %><%@
page import="com.liferay.portal.kernel.service.CompanyLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PropsKeys" %><%@
page import="com.liferay.portal.kernel.util.PropsUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.site.initializer.SiteInitializer" %><%@
page import="com.liferay.site.initializer.SiteInitializerRegistry" %><%@
page import="com.liferay.taglib.search.ResultRow" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.List" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%@ include file="/init-ext.jsp" %>