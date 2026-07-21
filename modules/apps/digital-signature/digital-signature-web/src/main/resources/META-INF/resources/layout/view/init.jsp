<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/layout/init.jsp" %>

<%@ taglib uri="http://liferay.com/tld/layout" prefix="liferay-layout" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.portal.kernel.model.LayoutTemplateConstants" %><%@
page import="com.liferay.portal.kernel.service.LayoutTemplateLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.layoutconfiguration.util.RuntimePageUtil" %>

<liferay-theme:defineObjects />

<liferay-util:html-top>
	<meta content="initial-scale=1.0, width=device-width" name="viewport" />
</liferay-util:html-top>