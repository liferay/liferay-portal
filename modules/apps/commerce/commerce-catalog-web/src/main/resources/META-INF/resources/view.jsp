<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceCatalogDisplayContext commerceCatalogDisplayContext = (CommerceCatalogDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<liferay-ui:error embed="<%= false %>" exception="<%= CommerceCatalogProductsException.class %>" message="you-cannot-delete-catalogs-that-have-products" />
<liferay-ui:error embed="<%= false %>" exception="<%= CommerceCatalogSystemException.class %>" message="you-cannot-delete-master-catalog" />

<frontend-data-set:classic-display
	creationMenu="<%= commerceCatalogDisplayContext.getCreationMenu() %>"
	dataProviderKey="<%= CommerceCatalogFDSNames.CATALOGS %>"
	id="<%= CommerceCatalogFDSNames.CATALOGS %>"
	style="fluid"
/>