<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
KaleoFormsAdminFieldsDisplayContext kaleoFormsAdminFieldsDisplayContext = new KaleoFormsAdminFieldsDisplayContext(request, kaleoFormsAdminDisplayContext, liferayPortletRequest, liferayPortletResponse, renderRequest);

JSONArray availableDefinitionsJSONArray = JSONFactoryUtil.createJSONArray();
%>

<h3 class="kaleo-process-header"><liferay-ui:message key="fields" /></h3>

<p class="kaleo-process-message"><liferay-ui:message key="please-select-or-create-a-new-field-set-containing-all-the-fields-that-will-be-used-by-your-forms" /></p>

<aui:field-wrapper>
	<liferay-ui:message key="selected-field-set" />:

	<aui:a cssClass="badge badge-info kaleo-process-preview-definition" data-definition-id="<%= kaleoFormsAdminFieldsDisplayContext.getDDMStructureId() %>" href="javascript:void(0);" id="ddmStructureDisplay" label="<%= HtmlUtil.escape(kaleoFormsAdminFieldsDisplayContext.getDDMStructureName()) %>" />

	<aui:input name="ddmStructureId" type="hidden" value="<%= kaleoFormsAdminFieldsDisplayContext.getDDMStructureId() %>" />

	<aui:input name="ddmStructureName" required="<%= true %>" type="hidden" value="<%= kaleoFormsAdminFieldsDisplayContext.getDDMStructureName() %>" />
</aui:field-wrapper>

<liferay-ui:error exception="<%= RecordSetDDMStructureIdException.class %>" message="please-enter-a-valid-definition" />

<liferay-ui:search-container
	searchContainer="<%= kaleoFormsAdminFieldsDisplayContext.getSearchContainer() %>"
>
	<c:if test="<%= DDMStructurePermission.containsAddStructurePermission(permissionChecker, scopeGroupId, scopeClassNameId) %>">
		<liferay-portlet:renderURL portletName="<%= PortletProviderUtil.getPortletId(DDMStructure.class.getName(), PortletProvider.Action.EDIT) %>" var="addURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcPath" value="/edit_structure.jsp" />
			<portlet:param name="navigationStartsOn" value="<%= DDMNavigationHelper.EDIT_STRUCTURE %>" />
			<portlet:param name="closeRedirect" value="<%= kaleoFormsAdminFieldsDisplayContext.getBackURL() %>" />
			<portlet:param name="saveAndContinue" value="<%= Boolean.TRUE.toString() %>" />
			<portlet:param name="showBackURL" value="<%= Boolean.FALSE.toString() %>" />
			<portlet:param name="refererPortletName" value="<%= KaleoFormsPortletKeys.KALEO_FORMS_ADMIN %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>" />
			<portlet:param name="classNameId" value="<%= String.valueOf(PortalUtil.getClassNameId(DDMStructure.class)) %>" />
		</liferay-portlet:renderURL>

		<aui:button-row>
			<aui:button onClick='<%= "javascript:" + liferayPortletResponse.getNamespace() + "editStructure('" + LanguageUtil.format(request, "new-x", LanguageUtil.get(request, "field-set"), false) + "','" + addURL + "');" %>' primary="<%= true %>" value="add-field-set" />
		</aui:button-row>
	</c:if>

	<liferay-ui:search-container-row
		className="com.liferay.dynamic.data.mapping.model.DDMStructure"
		keyProperty="structureId"
		modelVar="structure"
	>
		<liferay-ui:search-container-row-parameter
			name="backURL"
			value="<%= kaleoFormsAdminFieldsDisplayContext.getBackURL() %>"
		/>

		<liferay-util:buffer
			var="structureNameBuffer"
		>

			<%
			JSONArray definitionFieldsJSONArray = DDMUtil.getDDMFormFieldsJSONArray(structure, structure.getDefinition());

			availableDefinitionsJSONArray.put(
				JSONUtil.put(
					"definitionFields", definitionFieldsJSONArray
				).put(
					"definitionId", structure.getStructureId()
				).put(
					"definitionName", HtmlUtil.escapeAttribute(structure.getName(locale))
				));
			%>

			(<aui:a cssClass="kaleo-process-preview-definition" data-definition-id="<%= structure.getStructureId() %>" href="javascript:void(0);" label="view-fields" />)
		</liferay-util:buffer>

		<liferay-ui:search-container-column-text
			name="name"
			value="<%= HtmlUtil.escape(structure.getName(locale)) + StringPool.SPACE + structureNameBuffer %>"
		/>

		<liferay-ui:search-container-column-text
			name="description"
			value="<%= HtmlUtil.escape(structure.getDescription(locale)) %>"
		/>

		<liferay-ui:search-container-column-date
			name="modified-date"
			value="<%= structure.getModifiedDate() %>"
		/>

		<liferay-ui:search-container-column-jsp
			align="right"
			cssClass="entry-action"
			path="/admin/process/structure_action.jsp"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>

<aui:script>
	Liferay.on(
		'<portlet:namespace />chooseDefinition',
		(event) => {
			const ddmStructureId = event.ddmStructureId;
			const ddmStructureName = event.name;

			document.getElementById(
				'<portlet:namespace />ddmStructureDisplay'
			).innerHTML = Liferay.Util.escapeHTML(ddmStructureName);

			document.getElementById('<portlet:namespace />ddmStructureId').value =
				ddmStructureId;
			document.getElementById('<portlet:namespace />ddmStructureName').value =
				ddmStructureName;

			const kaleoFormsAdmin = Liferay.component(
				'<portlet:namespace />KaleoFormsAdmin'
			);

			kaleoFormsAdmin.saveInPortletSession(
				{
					ddmStructureId: ddmStructureId,
					ddmStructureName: ddmStructureName,
				},
				event.dialogId
			);

			kaleoFormsAdmin.updateNavigationControls();
		},
		['aui-base']
	);

	window.<portlet:namespace />editStructure = (title, url) => {
		let closeRedirectURL;
		let redirectOnClose = false;

		Liferay.Util.openModal({
			iframeBodyCssClass: '',
			onClose: () => {
				if (redirectOnClose) {
					Liferay.Util.navigate(closeRedirectURL);
					window.location.reload();
				}
			},
			onOpen: ({iframeWindow}) => {
				const closeRedirectElement = iframeWindow.document.getElementById(
					'_<%= DDMPortletKeys.DYNAMIC_DATA_MAPPING %>_closeRedirect'
				);

				if (closeRedirectElement) {
					closeRedirectURL = closeRedirectElement.value;
				}

				const saveButton =
					iframeWindow.document.querySelector('.btn-primary');

				if (saveButton) {
					const onClick = () => {
						redirectOnClose = true;

						saveButton.removeEventListener('click', onClick);
					};

					saveButton.addEventListener('click', onClick);
				}
			},
			title: title,
			url,
		});
	};
</aui:script>

<aui:script use="liferay-kaleo-forms-components">
	const kaleoDefinitionPreview = new Liferay.KaleoDefinitionPreview({
		availableDefinitions: <%= availableDefinitionsJSONArray.toString() %>,
		height: 600,
		namespace: '<portlet:namespace />',
		on: {
			choose: (event) => {
				Liferay.fire('<portlet:namespace />chooseDefinition', {
					ddmStructureId: event.definitionId,
					name: event.definitionName,
				});
			},
		},
		width: 700,
	});

	const previewButtons = document.querySelectorAll(
		'.kaleo-process-preview-definition'
	);

	previewButtons.forEach((button) => {
		button.addEventListener('click', (event) => {
			kaleoDefinitionPreview.select(event.target.dataset.definitionId);
			kaleoDefinitionPreview.preview();
		});
	});
</aui:script>