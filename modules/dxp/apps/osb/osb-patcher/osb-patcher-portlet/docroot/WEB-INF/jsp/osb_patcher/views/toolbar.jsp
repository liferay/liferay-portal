<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/WEB-INF/jsp/osb_patcher/views/init.jsp" %>

<portlet:renderURL var="viewPatcherAccountsURL">
	<portlet:param name="controller" value="accounts" />
	<portlet:param name="action" value="index" />

	<c:if test="${not empty patcherProductVersionId}">
		<portlet:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
	</c:if>
</portlet:renderURL>

<portlet:renderURL var="viewPatcherBuildsURL">
	<portlet:param name="controller" value="builds" />
	<portlet:param name="action" value="index" />

	<c:if test="${not empty patcherProductVersionId}">
		<portlet:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
	</c:if>
</portlet:renderURL>

<portlet:renderURL var="viewPatcherFixesURL">
	<portlet:param name="controller" value="fixes" />
	<portlet:param name="action" value="index" />

	<c:if test="${not empty patcherProductVersionId}">
		<portlet:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
	</c:if>
</portlet:renderURL>

<portlet:renderURL var="viewPatcherFixComponentsURL">
	<portlet:param name="controller" value="fix_components" />
	<portlet:param name="action" value="index" />

	<c:if test="${not empty patcherProductVersionId}">
		<portlet:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
	</c:if>
</portlet:renderURL>

<portlet:renderURL var="viewPatcherFixPacksURL">
	<portlet:param name="controller" value="fix_packs" />
	<portlet:param name="action" value="index" />

	<c:if test="${not empty patcherProductVersionId}">
		<portlet:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
	</c:if>
</portlet:renderURL>

<portlet:renderURL var="viewPatcherProductVersionsURL">
	<portlet:param name="controller" value="product_versions" />
	<portlet:param name="action" value="index" />

	<c:if test="${not empty patcherProductVersionId}">
		<portlet:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
	</c:if>
</portlet:renderURL>

<portlet:renderURL var="viewPatcherProjectVersionsURL">
	<portlet:param name="controller" value="project_versions" />
	<portlet:param name="action" value="index" />

	<c:if test="${not empty patcherProductVersionId}">
		<portlet:param name="patcherProductVersionId" value="${patcherProductVersionId}" />
	</c:if>
</portlet:renderURL>

<%
List<String> tabsNames = new ArrayList<String>();
List<String> tabsValuesList = new ArrayList<String>();

tabsNames.add("accounts");

tabsValuesList.add(viewPatcherAccountsURL);

tabsNames.add("fixes");

tabsValuesList.add(viewPatcherFixesURL);

tabsNames.add("qa-builds");

tabsValuesList.add(viewPatcherBuildsURL);

tabsNames.add("fix-components");

tabsValuesList.add(viewPatcherFixComponentsURL);

if (PatcherPermission.contains(themeDisplay, "fix_packs", "index")) {
	tabsNames.add("fix-packs");

	tabsValuesList.add(viewPatcherFixPacksURL);
}

if (PatcherPermission.contains(themeDisplay, "product_versions", "index")) {
	tabsNames.add("product-versions");

	tabsValuesList.add(viewPatcherProductVersionsURL);
}

tabsNames.add("project-versions");

tabsValuesList.add(viewPatcherProjectVersionsURL);

tabsNames.add("jsonws-api");

StringBundler sb = new StringBundler(3);

sb.append(themeDisplay.getCDNBaseURL());
sb.append("/api/jsonws/?contextPath=");
sb.append(PortalUtil.getPathContext(request));

tabsValuesList.add(sb.toString());

String[] tabsValuesArray = new String[8];

tabsValuesList.toArray(tabsValuesArray);
%>

<liferay-ui:tabs names="<%= StringUtil.merge(tabsNames) %>"
	url0="<%= tabsValuesArray[0] %>"
	url1="<%= tabsValuesArray[1] %>"
	url2="<%= tabsValuesArray[2] %>"
	url3="<%= tabsValuesArray[3] %>"
	url4="<%= tabsValuesArray[4] %>"
	url5="<%= tabsValuesArray[5] %>"
	url6="<%= (tabsValuesArray.length > 6) ? tabsValuesArray[6] : StringPool.BLANK %>"
	url7="<%= (tabsValuesArray.length > 7) ? tabsValuesArray[7] : StringPool.BLANK %>"
/>