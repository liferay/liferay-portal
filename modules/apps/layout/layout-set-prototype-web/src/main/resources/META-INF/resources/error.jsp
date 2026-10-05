<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:error-header />

<liferay-ui:error exception="<%= NoSuchLayoutSetPrototypeException.class %>" message="the-page-template-could-not-be-found" />
<liferay-ui:error exception="<%= UnsupportedOperationException.class %>" message="the-site-template-sync-is-not-available" />
<liferay-ui:error key="publicationsEnabled" message="site-template-sync-cannot-be-run-with-publications-enabled" />

<liferay-ui:error-principal />