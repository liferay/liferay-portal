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
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.asset.display.page.item.selector.web.internal.dao.search.LayoutPageTemplateResultRowSplitter" %><%@
page import="com.liferay.asset.display.page.item.selector.web.internal.display.context.AssetDisplayPagesItemSelectorCustomViewDisplayContext" %><%@
page import="com.liferay.asset.display.page.item.selector.web.internal.display.context.AssetDisplayPagesItemSelectorCustomViewManagementToolbarDisplayContext" %><%@
page import="com.liferay.asset.display.page.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.LayoutPageTemplateCollectionHorizontalCard" %><%@
page import="com.liferay.asset.display.page.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.LayoutPageTemplateEntryVerticalCard" %><%@
page import="com.liferay.layout.page.template.model.LayoutPageTemplateCollection" %><%@
page import="com.liferay.layout.page.template.model.LayoutPageTemplateEntry" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />