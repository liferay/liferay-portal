<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.categories.item.selector.web.internal.constants.AssetCategoryItemSelectorWebKeys" %><%@
page import="com.liferay.asset.categories.item.selector.web.internal.display.context.SelectAssetCategoryInfoItemDisplayContext" %><%@
page import="com.liferay.asset.categories.item.selector.web.internal.display.context.SelectAssetCategoryTreeNodeDisplayContext" %><%@
page import="com.liferay.asset.categories.item.selector.web.internal.display.context.SelectAssetVocabularyDisplayContext" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />