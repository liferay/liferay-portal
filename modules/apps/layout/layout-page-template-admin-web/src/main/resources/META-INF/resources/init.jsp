<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portal-workflow" prefix="liferay-portal-workflow" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.frontend.taglib.servlet.taglib.util.EmptyResultMessageKeys" %><%@
page import="com.liferay.info.item.InfoItemServiceRegistry" %><%@
page import="com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.dao.search.LayoutPageTemplateResultRowSplitter" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.AssetDisplayPageUsagesDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.AssetDisplayPageUsagesManagementToolbarDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.DisplayPageDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.DisplayPageManagementToolbarDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.DisplayPageTemplateInfoPanelDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.LayoutPageTemplateCollectionsDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.LayoutPageTemplateCollectionsManagementToolbarDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.LayoutPageTemplateDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.LayoutPageTemplateManagementToolbarDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.LayoutPageTemplatesAdminDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.LayoutPrototypeDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.LayoutPrototypeManagementToolbarDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.MasterLayoutDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.MasterLayoutManagementToolbarDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.display.context.SelectDisplayPageMasterLayoutDisplayContext" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.frontend.taglib.clay.servlet.taglib.DisplayPageTemplateCollectionHorizontalCard" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.frontend.taglib.clay.servlet.taglib.DisplayPageVerticalCard" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.frontend.taglib.clay.servlet.taglib.LayoutPageTemplateEntryVerticalCard" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.frontend.taglib.clay.servlet.taglib.LayoutPrototypeVerticalCard" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.frontend.taglib.clay.servlet.taglib.MasterLayoutVerticalCard" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.frontend.taglib.clay.servlet.taglib.SelectDisplayPageMasterLayoutVerticalCard" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.frontend.taglib.clay.servlet.taglib.SelectLayoutPageTemplateEntryMasterLayoutVerticalCard" %><%@
page import="com.liferay.layout.page.template.admin.web.internal.servlet.taglib.util.LayoutPageTemplateCollectionActionDropdownItem" %><%@
page import="com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys" %><%@
page import="com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants" %><%@
page import="com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateCollectionException" %><%@
page import="com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionNameException" %><%@
page import="com.liferay.layout.page.template.exception.LayoutPageTemplateEntryNameException" %><%@
page import="com.liferay.layout.page.template.exception.RequiredLayoutPageTemplateEntryException" %><%@
page import="com.liferay.layout.page.template.model.LayoutPageTemplateCollection" %><%@
page import="com.liferay.layout.page.template.model.LayoutPageTemplateEntry" %><%@
page import="com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalServiceUtil" %><%@
page import="com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil" %><%@
page import="com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil" %><%@
page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %><%@
page import="com.liferay.portal.kernel.exception.PortalException" %><%@
page import="com.liferay.portal.kernel.exception.RequiredLayoutPrototypeException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.LayoutPrototype" %><%@
page import="com.liferay.portal.kernel.model.ModelHintsUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder" %><%@
page import="com.liferay.portal.kernel.service.LayoutPrototypeServiceUtil" %><%@
page import="com.liferay.portal.kernel.servlet.MultiSessionMessages" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.model.impl.LayoutPrototypeImpl" %><%@
page import="com.liferay.portal.util.PropsValues" %>

<%@ page import="jakarta.portlet.PortletRequest" %><%@
page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.text.DateFormat" %><%@
page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.List" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
LayoutPageTemplatesAdminDisplayContext layoutPageTemplatesAdminDisplayContext = new LayoutPageTemplatesAdminDisplayContext(liferayPortletRequest, liferayPortletResponse);
%>

<%@ include file="/init-ext.jsp" %>