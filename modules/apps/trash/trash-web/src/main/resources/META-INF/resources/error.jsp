<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:error-header />

<liferay-ui:error exception="<%= com.liferay.portal.kernel.exception.NoSuchClassNameException.class %>" message="the-entry-could-not-be-found" />
<liferay-ui:error exception="<%= com.liferay.trash.exception.NoSuchEntryException.class %>" message="the-entry-could-not-be-found" />

<liferay-ui:error-principal />