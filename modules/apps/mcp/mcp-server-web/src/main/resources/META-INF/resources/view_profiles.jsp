<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-util:include page="/navigation.jsp" servletContext="<%= application %>" />

<div class="p-4 text-secondary">
	<%= LanguageUtil.format(request, "the-x-tab-is-not-implemented-yet", LanguageUtil.get(request, "profiles")) %>
</div>