<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
FDSSampleDisplayContext fdsSampleDisplayContext = (FDSSampleDisplayContext)request.getAttribute(FDSSampleWebKeys.FDS_SAMPLE_DISPLAY_CONTEXT);
%>

<frontend-data-set:headless-display
	apiURL="<%= fdsSampleDisplayContext.getAPIURL() %>"
	id="<%= FDSSampleFDSNames.HIDDEN_EXCLUDE_TOGGLE %>"
	propsTransformer="{HiddenExcludeTogglePropsTransformer} from frontend-data-set-sample-web"
	style="fluid"
/>