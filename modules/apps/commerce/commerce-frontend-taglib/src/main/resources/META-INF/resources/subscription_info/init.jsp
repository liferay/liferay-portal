<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<liferay-theme:defineObjects />

<%
String deliveryDurationPeriod = (String)request.getAttribute("liferay-commerce:subscription-info:deliveryDurationPeriod");
String deliverySubscriptionPeriod = (String)request.getAttribute("liferay-commerce:subscription-info:deliverySubscriptionPeriod");
String durationPeriod = (String)request.getAttribute("liferay-commerce:subscription-info:durationPeriod");
String subscriptionPeriod = (String)request.getAttribute("liferay-commerce:subscription-info:subscriptionPeriod");
%>