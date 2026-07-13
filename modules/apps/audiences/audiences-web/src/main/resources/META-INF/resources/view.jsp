<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AudiencesDisplayContext audiencesDisplayContext = new AudiencesDisplayContext(renderRequest, renderResponse);
%>

<frontend-data-set:classic-display
	creationMenu="<%= audiencesDisplayContext.getCreationMenu() %>"
	dataProviderKey="<%= AudiencesFDSNames.AUDIENCES_ENTRIES %>"
	emptyState="<%= audiencesDisplayContext.getEmptyState() %>"
	id="<%= AudiencesFDSNames.AUDIENCES_ENTRIES %>"
	style="fluid"
/>