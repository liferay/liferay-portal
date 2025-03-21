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
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.commerce.constants.CommercePaymentEntryConstants" %><%@
page import="com.liferay.commerce.payment.constants.CommercePaymentScreenNavigationConstants" %><%@
page import="com.liferay.commerce.payment.entry.CommercePaymentEntryRefundType" %><%@
page import="com.liferay.commerce.payment.exception.CommercePaymentEntryAmountException" %><%@
page import="com.liferay.commerce.payment.exception.CommercePaymentEntryPaymentIntegrationTypeException" %><%@
page import="com.liferay.commerce.payment.exception.CommercePaymentEntryPaymentStatusException" %><%@
page import="com.liferay.commerce.payment.exception.CommercePaymentEntryReasonKeyException" %><%@
page import="com.liferay.commerce.payment.exception.CommercePaymentMethodGroupRelNameException" %><%@
page import="com.liferay.commerce.payment.exception.DuplicateCommercePaymentMethodGroupRelQualifierException" %><%@
page import="com.liferay.commerce.payment.exception.NoSuchPaymentEntryException" %><%@
page import="com.liferay.commerce.payment.model.CommercePaymentEntry" %><%@
page import="com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel" %><%@
page import="com.liferay.commerce.payment.web.internal.constants.CommercePaymentMethodGroupRelFDSNames" %><%@
page import="com.liferay.commerce.payment.web.internal.constants.CommercePaymentsFDSNames" %><%@
page import="com.liferay.commerce.payment.web.internal.display.context.CommerceChannelAccountEntryRelDisplayContext" %><%@
page import="com.liferay.commerce.payment.web.internal.display.context.CommercePaymentEntryDisplayContext" %><%@
page import="com.liferay.commerce.payment.web.internal.display.context.CommercePaymentMethodGroupRelQualifiersDisplayContext" %><%@
page import="com.liferay.commerce.payment.web.internal.display.context.CommercePaymentMethodGroupRelsDisplayContext" %><%@
page import="com.liferay.commerce.payment.web.internal.display.context.FunctionCommercePaymentIntegrationConfigurationDisplayContext" %><%@
page import="com.liferay.commerce.term.constants.CommerceTermEntryConstants" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %>

<%@ page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />