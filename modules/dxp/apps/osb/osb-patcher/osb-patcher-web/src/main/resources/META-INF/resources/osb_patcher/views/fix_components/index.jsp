<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="fix-components" />
</liferay-util:include>

<aui:button-row>
	<portlet:renderURL var="createPatcherFixComponentURL">
		<portlet:param name="controller" value="fix_components" />
		<portlet:param name="action" value="create" />
	</portlet:renderURL>

	<aui:button href="${createPatcherFixComponentURL}" value="create-fix-component" />
</aui:button-row>

<portlet:renderURL var="viewPatcherFixComponentsURL">
	<portlet:param name="controller" value="fix_components" />
	<portlet:param name="action" value="index" />
</portlet:renderURL>

<aui:form action="${viewPatcherFixComponentsURL}" method="get" name="fm">
	<aui:fieldset>
		<aui:input inlineField="${true}" label="" name="keywords" size="30" title="search-fix-components" type="text" />

		<aui:button type="submit" value="search" />
	</aui:fieldset>
</aui:form>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-fix-components"
	iteratorURL="${alloySearchResult.portletURL}"
>
	<liferay-ui:search-container-results
		results="${alloySearchResult.baseModels}"
		total="${alloySearchResult.size}"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherFixComponent"
		escapedModel="${true}"
		keyProperty="patcherFixComponentId"
		modelVar="patcherFixComponent"
	>
		<liferay-ui:search-container-column-text
			property="name"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test="${PatcherPermission.contains(themeDisplay, patcherFixComponent, PatcherActionKeys.EDIT)}">
					<portlet:renderURL var="editPatcherFixComponentURL">
						<portlet:param name="controller" value="fix_components" />
						<portlet:param name="action" value="edit" />
						<portlet:param name="id" value="${patcherFixComponent.patcherFixComponentId}" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="${editPatcherFixComponentURL}"
					/>
				</c:if>

				<c:if test="${PatcherPermission.contains(themeDisplay, patcherFixComponent, ActionKeys.DELETE)}">
					<portlet:actionURL var="deletePatcherFixComponentURL">
						<portlet:param name="controller" value="fix_components" />
						<portlet:param name="action" value="delete" />
						<portlet:param name="id" value="${patcherFixComponent.patcherFixComponentId}" />
						<portlet:param name="redirect" value="${alloySearchResult.portletURL}" />
					</portlet:actionURL>

					<liferay-ui:icon-delete
						url="${deletePatcherFixComponentURL}"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>