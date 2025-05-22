<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<aui:model-context bean="${patcherFix}" model="<%= PatcherFix.class %>" />

<portlet:actionURL var="setFixPackFieldsURL">
	<portlet:param name="controller" value="fixes" />
	<portlet:param name="action" value="setFixPackFields" />
</portlet:actionURL>

<aui:form action="${setFixPackFieldsURL}" method="post" name="fm" onSubmit="event.preventDefault(); ${renderResponse.namespace}setFixPackFields();">
	<aui:input name="id" type="hidden" value="${patcherFix.patcherFixId}" />

	<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" label="content" name="patcherFixName" readonly="${true}" type="textarea" value="${patcherFix.name}" />

	<aui:field-wrapper name="fix-pack-schedule" />

	<aui:select disabled="${disabled}" name="fixPackStatus" showEmptyOption="${false}">
		<aui:option label="${WorkflowConstants.LABEL_FIX_FIX_PACK_READY}" value="${WorkflowConstants.STATUS_FIX_FIX_PACK_READY}" />
		<aui:option label="${WorkflowConstants.LABEL_FIX_FIX_PACK_GENERATED}" value="${WorkflowConstants.STATUS_FIX_FIX_PACK_GENERATED}" />
		<aui:option label="${WorkflowConstants.LABEL_FIX_FIX_PACK_MULTI_COMPONENT}" value="${WorkflowConstants.STATUS_FIX_FIX_PACK_MULTI_COMPONENT}" />
		<aui:option label="${WorkflowConstants.LABEL_FIX_FIX_PACK_NOT_COMPATIBLE}" value="${WorkflowConstants.STATUS_FIX_FIX_PACK_NOT_COMPATIBLE}" />
		<aui:option label="${WorkflowConstants.LABEL_FIX_FIX_PACK_RESOLVED_BY_OTHER_FIXES}" value="${WorkflowConstants.STATUS_FIX_FIX_PACK_RESOLVED_BY_OTHER_FIXES}" />
		<aui:option label="${WorkflowConstants.LABEL_FIX_FIX_PACK_SINGLE_COMPONENT}" value="${WorkflowConstants.STATUS_FIX_FIX_PACK_SINGLE_COMPONENT}" />
		<aui:option label="${WorkflowConstants.LABEL_FIX_FIX_PACK_UNKNOWN_COMPONENT}" value="${WorkflowConstants.STATUS_FIX_FIX_PACK_UNKNOWN_COMPONENT}" />
	</aui:select>

	<aui:input disabled="${disabled}" name="dependencies" />

	<c:if test="${!disabled}">
		<aui:button-row>
			<portlet:actionURL var="editPatcherFixFixPackFieldsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="controller" value="fixes" />
				<portlet:param name="action" value="editFixPackFields" />
				<portlet:param name="id" value="${patcherFix.patcherFixId}" />
			</portlet:actionURL>

			<aui:button onClick="form.action = '${editPatcherFixFixPackFieldsURL}';" type="submit" value="reload-available-fix-packs" />
		</aui:button-row>

		<aui:input name="patcherFixPackIds" type="hidden" />

		<liferay-ui:input-move-boxes
			leftBoxName="currentPatcherFixPackFields"
			leftList="${currentPatcherFixPacks}"
			leftReorder="false"
			leftTitle="current-fix-packs"
			rightBoxName="availablePatcherFixPackFields"
			rightList="${availablePatcherFixPacks}"
			rightTitle="available-fix-packs"
		/>
	</c:if>

	<aui:input disabled="${disabled}" name="requirements" />

	<aui:button-row>
		<aui:button disabled="${disabled}" type="submit" value="update" />

		<aui:button onClick="Liferay.Patcher.closeWindow();" value="cancel" />
	</aui:button-row>
</aui:form>

<c:if test="${not empty patcherFixPacks}">
	<aui:field-wrapper name="fix-packs" />

	<liferay-ui:search-container
		total="${fn:length(patcherFixPacks)}"
	>
		<liferay-ui:search-container-results
			results="${patcherFixPacks}"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.osb.patcher.model.PatcherFixPack"
			escapedModel="${true}"
			keyProperty="patcherFixPackId"
			modelVar="patcherFixPack"
		>
			<portlet:renderURL var="viewPatcherFixPackURL">
				<portlet:param name="controller" value="fix_packs" />
				<portlet:param name="action" value="view" />
				<portlet:param name="id" value="${patcherFixPack.patcherFixPackId}" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				href="${viewPatcherFixPackURL}"
				name="name"
				value="${patcherFixPack.name}"
			/>

			<c:set value="${PatcherFixComponentLocalServiceUtil.getPatcherFixComponent(patcherFixPack.getPatcherFixComponentId())}" var="patcherFixComponent" />

			<liferay-ui:search-container-column-text
				name="component"
				value="${patcherFixComponent.name}"
			/>

			<liferay-ui:search-container-column-text
				name="version"
				value="${patcherFixPack.version}"
			/>

			<c:set value="${PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(patcherFixPack.getPatcherProjectVersionId())}" var="patcherProjectVersion" />

			<liferay-ui:search-container-column-text
				name="project-version"
				value="${patcherProjectVersion.name}"
			/>

			<liferay-ui:search-container-column-text
				name="status"
				value="${AlloyLanguageUtil.format(WorkflowConstantsMethods.getStatusLabel(patcherFixPack.getStatus()))}"
			/>

			<liferay-ui:search-container-column-text
				name="released-date"
			>
				<fmt:formatDate
					value="${patcherFixPack.releasedDate}"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			paginate="${false}"
		/>
	</liferay-ui:search-container>
</c:if>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />setFixPackFields',
		function() {
			document.<portlet:namespace />fm.<portlet:namespace />patcherFixPackIds.value = Liferay.Util.listSelect(document.<portlet:namespace />fm.<portlet:namespace />currentPatcherFixPackFields);

			submitForm(document.<portlet:namespace />fm);
		},
		['liferay-util-list-fields']
	);
</aui:script>