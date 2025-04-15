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
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.petra.string.CharPool" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.security.auth.PrincipalException" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.security.service.access.policy.constants.SAPEntryConstants" %><%@
page import="com.liferay.portal.security.service.access.policy.exception.DuplicateSAPEntryNameException" %><%@
page import="com.liferay.portal.security.service.access.policy.exception.SAPEntryNameException" %><%@
page import="com.liferay.portal.security.service.access.policy.exception.SAPEntryTitleException" %><%@
page import="com.liferay.portal.security.service.access.policy.model.SAPEntry" %><%@
page import="com.liferay.portal.security.service.access.policy.service.SAPEntryServiceUtil" %><%@
page import="com.liferay.portal.security.service.access.policy.web.internal.constants.SAPWebKeys" %><%@
page import="com.liferay.portal.security.service.access.policy.web.internal.display.context.SAPEntryDisplayContext" %><%@
page import="com.liferay.portal.security.service.access.policy.web.internal.display.context.SAPEntryManagementToolbarDisplayContext" %><%@
page import="com.liferay.portal.security.service.access.policy.web.internal.security.permission.resource.SAPEntryPermission" %><%@
page import="com.liferay.taglib.search.ResultRow" %>

<%@ page import="jakarta.portlet.ActionRequest" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />