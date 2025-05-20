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

<liferay-util:include page="/WEB-INF/jsp/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="product-versions" />
</liferay-util:include>

<aui:button-row>
	<portlet:renderURL var="createPatcherProductVersionURL">
		<portlet:param name="controller" value="product_versions" />
		<portlet:param name="action" value="create" />
	</portlet:renderURL>

	<aui:button href="${createPatcherProductVersionURL}" value="create-product-version" />
</aui:button-row>

<portlet:renderURL var="viewPatcherProductVersionsURL">
	<portlet:param name="controller" value="product_versions" />
	<portlet:param name="action" value="index" />
</portlet:renderURL>

<aui:form action="${viewPatcherProductVersionsURL}" method="get" name="fm">
	<aui:fieldset>
		<aui:input inlineField="${true}" label="" name="keywords" size="30" title="search-product-versions" type="text" />

		<aui:button type="submit" value="search" />
	</aui:fieldset>
</aui:form>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-product-versions"
	iteratorURL="${alloySearchResult.portletURL}"
>
	<liferay-ui:search-container-results
		results="${alloySearchResult.baseModels}"
		total="${alloySearchResult.size}"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherProductVersion"
		escapedModel="${true}"
		keyProperty="patcherProductVersionId"
		modelVar="patcherProductVersion"
	>
		<liferay-ui:search-container-column-text
			property="name"
		/>

		<liferay-ui:search-container-column-text
			name="fix-delivery-method"
			value="${PatcherProductVersionConstantsMethods.getTypeLabel(patcherProductVersion.fixDeliveryMethod)}"
		/>

		<liferay-ui:search-container-column-text
			name="module-folder-name"
			property="moduleFolderName"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test='${PatcherPermission.contains(themeDisplay, patcherProductVersion, "edit")}'>
					<portlet:renderURL var="editPatcherProductVersionURL">
						<portlet:param name="controller" value="product_versions" />
						<portlet:param name="action" value="edit" />
						<portlet:param name="id" value="${patcherProductVersion.patcherProductVersionId}" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="${editPatcherProductVersionURL}"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>