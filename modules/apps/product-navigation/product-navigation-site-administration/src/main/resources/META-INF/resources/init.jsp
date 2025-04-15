<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/application-list" prefix="liferay-application-list" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.application.list.PanelCategory" %><%@
page import="com.liferay.application.list.constants.ApplicationListWebKeys" %><%@
page import="com.liferay.exportimport.kernel.exception.RemoteExportException" %><%@
page import="com.liferay.item.selector.ItemSelector" %><%@
page import="com.liferay.item.selector.criteria.URLItemSelectorReturnType" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.exception.SystemException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.log.Log" %><%@
page import="com.liferay.portal.kernel.log.LogFactoryUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.product.navigation.site.administration.internal.constants.SiteAdministrationWebKeys" %><%@
page import="com.liferay.product.navigation.site.administration.internal.display.context.SiteAdministrationPanelCategoryDisplayContext" %><%@
page import="com.liferay.site.item.selector.SiteItemSelectorCriterion" %><%@
page import="com.liferay.taglib.aui.AUIUtil" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.Map" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />