<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String backURL = ParamUtil.getString(request, "backURL");
String dataSetERC = ParamUtil.getString(request, "dataSetERC");
String dataSetLabel = ParamUtil.getString(request, "dataSetLabel");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);

renderResponse.setTitle(dataSetLabel);
%>

<react:component
	module="{DataSet} from frontend-data-set-admin-web"
	props='<%=
		HashMapBuilder.<String, Object>put(
			"backURL", backURL
		).put(
			"cellClientExtensionRenderers", fdsAdminDisplayContext.getCellClientExtensionRenderersJSONArray()
		).put(
			"dataSetERC", dataSetERC
		).put(
			"filterClientExtensionRenderers", fdsAdminDisplayContext.getFDSFilterCETsJSONArray()
		).put(
			"learnResources", LearnMessageUtil.getReactDataJSONObject("frontend-data-set-admin-web")
		).put(
			"manageUserViewsURL", fdsAdminDisplayContext.getManageUserViewsURL()
		).put(
			"namespace", liferayPortletResponse.getNamespace()
		).put(
			"resolvedRESTSchemas", fdsAdminDisplayContext.getRESTApplicationResolvedSchemasJSONArray()
		).put(
			"restApplications", fdsAdminDisplayContext.getRESTApplicationsJSONArray()
		).put(
			"saveDataSetSortURL", fdsAdminDisplayContext.getSaveDataSetSortURL()
		).put(
			"saveDataSetTableSectionsURL", fdsAdminDisplayContext.getSaveDataSetTableSectionsURL()
		).put(
			"spritemap", themeDisplay.getPathThemeSpritemap()
		).put(
			"visualizationModeClientExtensions", fdsAdminDisplayContext.getFDSVisualizationModeCETsJSONArray()
		).build()
	%>'
/>