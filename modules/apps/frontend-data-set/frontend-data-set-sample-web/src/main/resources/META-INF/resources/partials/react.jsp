<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ReactFDSDisplayContext reactFDSDisplayContext = new ReactFDSDisplayContext(request);
%>

<div>
	<react:component
		module="{ReactFrontendDataSet} from frontend-data-set-sample-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"apiURL", reactFDSDisplayContext.getAPIURL()
			).put(
				"id", FDSSampleFDSNames.REACT
			).put(
				"style", "fluid"
			).put(
				"views", reactFDSDisplayContext.getViews()
			).build()
		%>'
	/>
</div>