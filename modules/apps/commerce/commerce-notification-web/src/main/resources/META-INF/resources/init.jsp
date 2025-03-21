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

<%@ page import="com.liferay.commerce.constants.CommerceDefinitionTermConstants" %><%@
page import="com.liferay.commerce.notification.exception.CommerceNotificationTemplateFromException" %><%@
page import="com.liferay.commerce.notification.exception.CommerceNotificationTemplateNameException" %><%@
page import="com.liferay.commerce.notification.exception.CommerceNotificationTemplateTypeException" %><%@
page import="com.liferay.commerce.notification.model.CommerceNotificationTemplate" %><%@
page import="com.liferay.commerce.notification.type.CommerceNotificationType" %><%@
page import="com.liferay.commerce.notification.web.internal.constants.CommerceNotificationFDSNames" %><%@
page import="com.liferay.commerce.notification.web.internal.display.context.CommerceNotificationQueueEntriesDisplayContext" %><%@
page import="com.liferay.commerce.notification.web.internal.display.context.CommerceNotificationTemplatesDisplayContext" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.PropsUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>

<%@ page import="java.util.Map" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />