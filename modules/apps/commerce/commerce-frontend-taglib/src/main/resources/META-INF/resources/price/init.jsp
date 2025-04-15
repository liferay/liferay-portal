<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.commerce.constants.CommercePriceConstants" %><%@
page import="com.liferay.commerce.frontend.model.PriceModel" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<%@ page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
boolean compact = (boolean)request.getAttribute("commerce-ui:price:compact");
boolean displayDiscountLevels = (boolean)request.getAttribute("commerce-ui:price:displayDiscountLevels");
String namespace = (String)request.getAttribute("commerce-ui:price:namespace");
boolean netPrice = (boolean)request.getAttribute("commerce-ui:price:netPrice");
PriceModel priceModel = (PriceModel)request.getAttribute("commerce-ui:price:priceModel");
%>