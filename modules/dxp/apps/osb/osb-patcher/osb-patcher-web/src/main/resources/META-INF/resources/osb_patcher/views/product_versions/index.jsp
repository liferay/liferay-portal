<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
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
	total="${alloySearchResult.size}"
>
	<liferay-ui:search-container-results
		results="${alloySearchResult.baseModels}"
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
				<c:if test="${PatcherPermission.contains(themeDisplay, patcherProductVersion, PatcherActionKeys.EDIT, patcherProductVersion.userId)}">
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