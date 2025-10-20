<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/data_layout_builder/init.jsp" %>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="dynamic-data-mapping-form-builder/css/main.css" rel="stylesheet" />
	<aui:link hashedFile="<%= true %>" href="data-engine-js-components-web/css/main.css" rel="stylesheet" />
	<aui:link hashedFile="<%= true %>" href="data-engine-taglib/css/main.css" rel="stylesheet" />
</liferay-util:html-top>

<liferay-util:dynamic-include key="com.liferay.data.engine.taglib#/data_layout_builder/start.jsp#pre" />

<liferay-util:dynamic-include key="com.liferay.data.engine.taglib#/data_layout_builder/start.jsp#post" />