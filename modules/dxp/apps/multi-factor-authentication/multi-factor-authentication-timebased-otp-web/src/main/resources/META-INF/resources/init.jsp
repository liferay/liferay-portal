<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.multi.factor.authentication.timebased.otp.web.internal.constants.MFATimeBasedOTPWebKeys" %><%@
page import="com.liferay.multi.factor.authentication.timebased.otp.web.internal.display.context.MFATimeBasedOTPCheckerDisplayContext" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="multi-factor-authentication-timebased-otp-web/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>