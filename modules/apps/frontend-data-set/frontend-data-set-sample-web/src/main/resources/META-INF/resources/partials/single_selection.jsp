<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
FDSSampleDisplayContext fdsSampleDisplayContext = (FDSSampleDisplayContext)request.getAttribute(FDSSampleWebKeys.FDS_SAMPLE_DISPLAY_CONTEXT);
%>

<frontend-data-set:headless-display
	apiURL="<%= fdsSampleDisplayContext.getAPIURL() %>"
	id="<%= FDSSampleFDSNames.SINGLE_SELECTION %>"
	propsTransformer="{SingleSelectionPropsTransformer} from frontend-data-set-sample-web"
	selectedItemsKey="id"
	selectionType="single"
	style="fluid"
/>