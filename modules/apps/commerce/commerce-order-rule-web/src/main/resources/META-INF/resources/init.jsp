<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/commerce-ui" prefix="commerce-ui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.commerce.currency.model.CommerceCurrency" %><%@
page import="com.liferay.commerce.order.rule.constants.COREntryConstants" %><%@
page import="com.liferay.commerce.order.rule.entry.type.COREntryType" %><%@
page import="com.liferay.commerce.order.rule.entry.type.COREntryTypeJSPContributor" %><%@
page import="com.liferay.commerce.order.rule.exception.COREntryExpirationDateException" %><%@
page import="com.liferay.commerce.order.rule.exception.NoSuchCOREntryException" %><%@
page import="com.liferay.commerce.order.rule.model.COREntry" %><%@
page import="com.liferay.commerce.order.rule.web.internal.constants.COREntryFDSNames" %><%@
page import="com.liferay.commerce.order.rule.web.internal.display.context.COREntryDisplayContext" %><%@
page import="com.liferay.commerce.order.rule.web.internal.display.context.COREntryQualifiersDisplayContext" %><%@
page import="com.liferay.commerce.order.rule.web.internal.entry.constants.COREntryScreenNavigationEntryConstants" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.taglib.servlet.PipingServletResponseFactory" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />