<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.exception.ContactNameException" %><%@
page import="com.liferay.portal.kernel.language.constants.LanguageConstants" %><%@
page import="com.liferay.portal.kernel.security.auth.FullNameDefinition" %><%@
page import="com.liferay.portal.kernel.security.auth.FullNameDefinitionFactory" %><%@
page import="com.liferay.portal.kernel.security.auth.FullNameField" %><%@
page import="com.liferay.portal.kernel.util.CamelCaseUtil" %>

<%@ page import="java.util.function.ToLongFunction" %>

<%
Object bean = request.getAttribute("liferay-ui:user-name-fields:bean");
Contact selContact = (Contact)request.getAttribute("liferay-ui:user-name-fields:contact");
User selUser = (User)request.getAttribute("liferay-ui:user-name-fields:user");

String languageId = request.getParameter("languageId");

if (Validator.isNull(languageId)) {
	if (selUser != null) {
		languageId = selUser.getLanguageId();
	}
	else {
		languageId = themeDisplay.getLanguageId();
	}
}

Locale userLocale = LocaleUtil.fromLanguageId(languageId);
%>