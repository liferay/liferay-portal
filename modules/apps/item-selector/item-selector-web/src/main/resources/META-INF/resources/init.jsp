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
taglib uri="http://liferay.com/tld/item-selector" prefix="liferay-item-selector" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.frontend.taglib.clay.servlet.taglib.HorizontalCard" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem" %><%@
page import="com.liferay.item.selector.ItemSelectorViewDescriptor" %><%@
page import="com.liferay.item.selector.ItemSelectorViewRenderer" %><%@
page import="com.liferay.item.selector.TableItemView" %><%@
page import="com.liferay.item.selector.web.internal.DefaultTableItemView" %><%@
page import="com.liferay.item.selector.web.internal.display.context.ItemSelectorViewDescriptorRendererDisplayContext" %><%@
page import="com.liferay.item.selector.web.internal.display.context.ItemSelectorViewDescriptorRendererManagementToolbarDisplayContext" %><%@
page import="com.liferay.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.ItemDescriptorHorizontalCard" %><%@
page import="com.liferay.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.ItemDescriptorVerticalCard" %><%@
page import="com.liferay.item.selector.web.internal.portlet.LocalizedItemSelectorRendering" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchEntry" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.log.Log" %><%@
page import="com.liferay.portal.kernel.log.LogFactoryUtil" %><%@
page import="com.liferay.portal.kernel.model.BaseModel" %><%@
page import="com.liferay.portal.kernel.model.UserConstants" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<%@ page import="java.util.Date" %><%@
page import="java.util.List" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />