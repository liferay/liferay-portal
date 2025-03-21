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
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/item-selector" prefix="liferay-item-selector" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.account.exception.AccountEntryStatusException" %><%@
page import="com.liferay.account.exception.AccountEntryTypeException" %><%@
page import="com.liferay.account.model.AccountEntry" %><%@
page import="com.liferay.commerce.catalog.web.internal.constants.CommerceCatalogFDSNames" %><%@
page import="com.liferay.commerce.catalog.web.internal.constants.CommerceCatalogScreenNavigationConstants" %><%@
page import="com.liferay.commerce.catalog.web.internal.display.context.CommerceCatalogDisplayContext" %><%@
page import="com.liferay.commerce.currency.model.CommerceCurrency" %><%@
page import="com.liferay.commerce.inventory.method.CommerceInventoryMethod" %><%@
page import="com.liferay.commerce.price.list.constants.CommercePriceListConstants" %><%@
page import="com.liferay.commerce.price.list.exception.NoSuchPriceListException" %><%@
page import="com.liferay.commerce.price.list.model.CommercePriceList" %><%@
page import="com.liferay.commerce.product.exception.CommerceCatalogProductsException" %><%@
page import="com.liferay.commerce.product.exception.DuplicateCommerceCatalogExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCatalogException" %><%@
page import="com.liferay.commerce.product.model.CommerceCatalog" %><%@
page import="com.liferay.document.library.kernel.exception.NoSuchFileEntryException" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.petra.string.StringUtil" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.repository.model.FileEntry" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Locale" %><%@
page import="java.util.Set" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />