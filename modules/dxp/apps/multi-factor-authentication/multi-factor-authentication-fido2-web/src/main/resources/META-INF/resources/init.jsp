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
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.multi.factor.authentication.fido2.credential.model.MFAFIDO2CredentialEntry" %><%@
page import="com.liferay.multi.factor.authentication.fido2.credential.service.MFAFIDO2CredentialEntryLocalServiceUtil" %><%@
page import="com.liferay.multi.factor.authentication.fido2.web.internal.configuration.MFAFIDO2Configuration" %><%@
page import="com.liferay.multi.factor.authentication.fido2.web.internal.constants.MFAFIDO2WebKeys" %><%@
page import="com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %>

<%@ page import="java.util.List" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
MFAFIDO2Configuration mfaFIDO2Configuration = ConfigurationProviderUtil.getCompanyConfiguration(MFAFIDO2Configuration.class, themeDisplay.getCompanyId());
%>