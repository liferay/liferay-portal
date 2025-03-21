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
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.commerce.inventory.model.CommerceInventoryWarehouse" %><%@
page import="com.liferay.commerce.product.constants.CPMeasurementUnitConstants" %><%@
page import="com.liferay.commerce.shipping.engine.fixed.exception.CommerceShippingFixedOptionAmountException" %><%@
page import="com.liferay.commerce.shipping.engine.fixed.exception.CommerceShippingFixedOptionKeyException" %><%@
page import="com.liferay.commerce.shipping.engine.fixed.exception.CommerceShippingFixedOptionRelPriceException" %><%@
page import="com.liferay.commerce.shipping.engine.fixed.exception.DuplicateCommerceShippingFixedOptionQualifierException" %><%@
page import="com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption" %><%@
page import="com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionRel" %><%@
page import="com.liferay.commerce.shipping.engine.fixed.web.internal.constants.CommerceShippingFixedOptionFDSNames" %><%@
page import="com.liferay.commerce.shipping.engine.fixed.web.internal.constants.CommerceShippingFixedOptionScreenNavigationConstants" %><%@
page import="com.liferay.commerce.shipping.engine.fixed.web.internal.display.context.CommerceShippingFixedOptionQualifiersDisplayContext" %><%@
page import="com.liferay.commerce.shipping.engine.fixed.web.internal.display.context.CommerceShippingFixedOptionRelsDisplayContext" %><%@
page import="com.liferay.commerce.shipping.engine.fixed.web.internal.display.context.CommerceShippingFixedOptionsDisplayContext" %><%@
page import="com.liferay.commerce.term.constants.CommerceTermEntryConstants" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Country" %><%@
page import="com.liferay.portal.kernel.model.Region" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>

<%@ page import="java.math.BigDecimal" %>

<%@ page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
String languageId = LanguageUtil.getLanguageId(locale);
%>