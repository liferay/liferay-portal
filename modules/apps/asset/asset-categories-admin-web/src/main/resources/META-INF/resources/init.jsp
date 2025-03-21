<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/learn" prefix="liferay-learn" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.categories.admin.web.internal.constants.AssetCategoriesConstants" %><%@
page import="com.liferay.asset.categories.admin.web.internal.display.context.AssetCategoriesDisplayContext" %><%@
page import="com.liferay.asset.categories.admin.web.internal.display.context.AssetCategoriesManagementToolbarDisplayContext" %><%@
page import="com.liferay.asset.categories.admin.web.internal.display.context.EditVocabularySettingsDisplayContext" %><%@
page import="com.liferay.asset.categories.admin.web.internal.servlet.taglib.util.AssetCategoryActionDropdownItemsProvider" %><%@
page import="com.liferay.asset.categories.admin.web.internal.servlet.taglib.util.AssetVocabularyActionDropdownItemsProvider" %><%@
page import="com.liferay.asset.categories.admin.web.internal.util.AssetCategoryUtil" %><%@
page import="com.liferay.asset.category.property.exception.CategoryPropertyKeyException" %><%@
page import="com.liferay.asset.category.property.exception.CategoryPropertyValueException" %><%@
page import="com.liferay.asset.category.property.exception.DuplicateCategoryPropertyException" %><%@
page import="com.liferay.asset.category.property.model.AssetCategoryProperty" %><%@
page import="com.liferay.asset.category.property.service.AssetCategoryPropertyLocalServiceUtil" %><%@
page import="com.liferay.asset.category.property.service.AssetCategoryPropertyServiceUtil" %><%@
page import="com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalServiceUtil" %><%@
page import="com.liferay.asset.kernel.NoSuchClassTypeException" %><%@
page import="com.liferay.asset.kernel.exception.AssetCategoryLimitException" %><%@
page import="com.liferay.asset.kernel.exception.AssetCategoryNameException" %><%@
page import="com.liferay.asset.kernel.exception.DuplicateCategoryException" %><%@
page import="com.liferay.asset.kernel.exception.DuplicateVocabularyException" %><%@
page import="com.liferay.asset.kernel.exception.NoSuchCategoryException" %><%@
page import="com.liferay.asset.kernel.exception.NoSuchVocabularyException" %><%@
page import="com.liferay.asset.kernel.exception.VocabularyNameException" %><%@
page import="com.liferay.asset.kernel.model.AssetCategory" %><%@
page import="com.liferay.asset.kernel.model.AssetRendererFactory" %><%@
page import="com.liferay.asset.kernel.model.AssetVocabulary" %><%@
page import="com.liferay.asset.kernel.model.AssetVocabularyConstants" %><%@
page import="com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil" %><%@
page import="com.liferay.asset.kernel.service.AssetVocabularyServiceUtil" %><%@
page import="com.liferay.change.tracking.spi.history.util.CTTimelineUtil" %><%@
page import="com.liferay.frontend.taglib.servlet.taglib.util.EmptyResultMessageKeys" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.ModelHintsUtil" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.servlet.MultiSessionMessages" %><%@
page import="com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.MapUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntriesUtil" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Collections" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
AssetCategoriesDisplayContext assetCategoriesDisplayContext = new AssetCategoriesDisplayContext(request, renderRequest, renderResponse);
%>

<%@ include file="/init-ext.jsp" %>