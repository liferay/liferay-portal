<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String closeRedirect = ParamUtil.getString(request, "closeRedirect");

String portletResourceNamespace = ParamUtil.getString(request, "portletResourceNamespace", liferayPortletResponse.getNamespace());

DDMStructure structure = (DDMStructure)request.getAttribute(DDMWebKeys.DYNAMIC_DATA_MAPPING_STRUCTURE);

DDMStructureVersion structureVersion = null;

if (structure != null) {
	structureVersion = structure.getLatestStructureVersion();
}

long groupId = BeanParamUtil.getLong(structure, request, "groupId", scopeGroupId);

long parentStructureId = BeanParamUtil.getLong(structure, request, "parentStructureId", DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID);

String parentStructureName = StringPool.BLANK;

try {
	DDMStructure parentStructure = DDMStructureServiceUtil.getStructure(parentStructureId);

	parentStructureName = parentStructure.getName(locale);
}
catch (NoSuchStructureException nsee) {
}

long classNameId = PortalUtil.getClassNameId(DDMStructure.class);
long classPK = BeanParamUtil.getLong(structure, request, "structureId");

String script = null;

if (structure != null) {
	script = BeanParamUtil.getString(structureVersion, request, "definition");
}
else {
	script = BeanParamUtil.getString(structure, request, "definition");
}

JSONArray fieldsJSONArray = null;

if (structure != null) {
	fieldsJSONArray = DDMUtil.getDDMFormFieldsJSONArray(structureVersion, script);
}
else {
	fieldsJSONArray = DDMUtil.getDDMFormFieldsJSONArray(structure, script);
}

String fieldsJSONArrayString = StringPool.BLANK;

if (fieldsJSONArray != null) {
	fieldsJSONArrayString = fieldsJSONArray.toString();
}

boolean showBackURL = ParamUtil.getBoolean(request, "showBackURL", true);
%>

<portlet:actionURL name="/dynamic_data_mapping/add_structure" var="addStructureURL">
	<portlet:param name="mvcPath" value="/edit_structure.jsp" />
</portlet:actionURL>

<portlet:actionURL name="/dynamic_data_mapping/update_structure" var="updateStructureURL">
	<portlet:param name="mvcPath" value="/edit_structure.jsp" />
</portlet:actionURL>

<%
String requestUpdateStructureURL = ParamUtil.getString(request, "updateStructureURL");

if (Validator.isNotNull(requestUpdateStructureURL)) {
	updateStructureURL = requestUpdateStructureURL;
}
%>

<clay:container-fluid>
	<aui:form action="<%= (structure == null) ? addStructureURL : updateStructureURL %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "saveStructure();" %>'>
		<aui:input name="redirect" type="hidden" value="<%= ddmDisplay.getViewTemplatesBackURL(liferayPortletRequest, liferayPortletResponse, classPK) %>" />
		<aui:input name="closeRedirect" type="hidden" value="<%= closeRedirect %>" />
		<aui:input name="groupId" type="hidden" value="<%= groupId %>" />
		<aui:input name="classNameId" type="hidden" value="<%= String.valueOf(classNameId) %>" />
		<aui:input name="classPK" type="hidden" value="<%= String.valueOf(classPK) %>" />
		<aui:input name="scopeClassNameId" type="hidden" value="<%= scopeClassNameId %>" />
		<aui:input name="definition" type="hidden" />
		<aui:input name="status" type="hidden" />
		<aui:input name="saveAndContinue" type="hidden" value='<%= ParamUtil.getBoolean(request, "saveAndContinue") %>' />

		<liferay-ui:error exception="<%= DDMFormLayoutValidationException.class %>" message="please-enter-a-valid-form-layout" />

		<liferay-ui:error exception="<%= DDMFormLayoutValidationException.MustNotDuplicateFieldName.class %>">

			<%
			DDMFormLayoutValidationException.MustNotDuplicateFieldName mndfn = (DDMFormLayoutValidationException.MustNotDuplicateFieldName)errorException;
			%>

			<liferay-ui:message arguments="<%= HtmlUtil.escape(StringUtil.merge(mndfn.getDuplicatedFieldNames(), StringPool.COMMA_AND_SPACE)) %>" key="the-definition-field-name-x-was-defined-more-than-once" translateArguments="<%= false %>" />
		</liferay-ui:error>

		<liferay-ui:error exception="<%= DDMFormValidationException.class %>" message="please-enter-a-valid-form-definition" />

		<liferay-ui:error exception="<%= DDMFormValidationException.MustNotDuplicateFieldName.class %>">

			<%
			DDMFormValidationException.MustNotDuplicateFieldName mndfn = (DDMFormValidationException.MustNotDuplicateFieldName)errorException;
			%>

			<liferay-ui:message arguments="<%= HtmlUtil.escape(mndfn.getFieldName()) %>" key="the-definition-field-name-x-was-defined-more-than-once" translateArguments="<%= false %>" />
		</liferay-ui:error>

		<liferay-ui:error exception="<%= DDMFormValidationException.MustSetFieldsForForm.class %>" message="please-add-at-least-one-field" />

		<liferay-ui:error exception="<%= DDMFormValidationException.MustSetOptionsForField.class %>">

			<%
			DDMFormValidationException.MustSetOptionsForField msoff = (DDMFormValidationException.MustSetOptionsForField)errorException;
			%>

			<liferay-ui:message arguments="<%= HtmlUtil.escape(msoff.getFieldName()) %>" key="at-least-one-option-should-be-set-for-field-x" translateArguments="<%= false %>" />
		</liferay-ui:error>

		<liferay-ui:error exception="<%= DDMFormValidationException.MustSetValidCharactersForFieldName.class %>">

			<%
			DDMFormValidationException.MustSetValidCharactersForFieldName msvcffn = (DDMFormValidationException.MustSetValidCharactersForFieldName)errorException;
			%>

			<liferay-ui:message arguments="<%= HtmlUtil.escape(msvcffn.getFieldName()) %>" key="invalid-characters-were-defined-for-field-name-x" translateArguments="<%= false %>" />
		</liferay-ui:error>

		<liferay-ui:error exception="<%= LocaleException.class %>">

			<%
			LocaleException le = (LocaleException)errorException;
			%>

			<c:if test="<%= le.getType() == LocaleException.TYPE_CONTENT %>">
				<liferay-ui:message arguments="<%= new String[] {StringUtil.merge(le.getSourceAvailableLanguageIds(), StringPool.COMMA_AND_SPACE), StringUtil.merge(le.getTargetAvailableLanguageIds(), StringPool.COMMA_AND_SPACE)} %>" key="the-default-language-x-does-not-match-the-portal's-available-languages-x" />
			</c:if>
		</liferay-ui:error>

		<liferay-ui:error exception="<%= StructureDefinitionException.class %>" message="please-enter-a-valid-definition" />
		<liferay-ui:error exception="<%= StructureDuplicateElementException.class %>" message="please-enter-unique-structure-field-names-(including-field-names-inherited-from-the-parent-structure)" />
		<liferay-ui:error exception="<%= StructureNameException.class %>" message="please-enter-a-valid-name" />

		<c:if test="<%= showBackURL %>">

			<%
			boolean localizeTitle = true;
			String title = "new-structure";

			if (structure != null) {
				localizeTitle = false;
				title = LanguageUtil.format(request, "edit-x", structure.getName(locale), false);
			}
			else {
				title = LanguageUtil.format(request, "new-x", ddmDisplay.getStructureName(locale), false);
			}
			%>

			<c:choose>
				<c:when test="<%= ddmDisplay.isShowBackURLInTitleBar() %>">

					<%
					portletDisplay.setShowBackIcon(true);
					portletDisplay.setURLBack(PortalUtil.escapeRedirect(ddmDisplay.getViewTemplatesBackURL(liferayPortletRequest, liferayPortletResponse, classPK)));

					renderResponse.setTitle(title);
					%>

				</c:when>
				<c:otherwise>
					<liferay-ui:header
						backURL="<%= ddmDisplay.getViewTemplatesBackURL(liferayPortletRequest, liferayPortletResponse, classPK) %>"
						localizeTitle="<%= localizeTitle %>"
						showBackURL="<%= showBackURL %>"
						title="<%= title %>"
					/>
				</c:otherwise>
			</c:choose>
		</c:if>

		<aui:model-context bean="<%= structure %>" model="<%= DDMStructure.class %>" />

		<c:if test="<%= (structureVersion != null) && ddmDisplay.isVersioningEnabled() %>">
			<aui:workflow-status model="<%= DDMStructure.class %>" status="<%= structureVersion.getStatus() %>" version="<%= structureVersion.getVersion() %>" />

			<div class="structure-history-toolbar" id="<portlet:namespace />structureHistoryToolbar"></div>

			<aui:script use="aui-dialog-iframe-deprecated,aui-toolbar,liferay-util-window">
				var toolbarChildren = [
					<portlet:renderURL var="viewHistoryURL">
						<portlet:param name="mvcPath" value="/view_structure_history.jsp" />
						<portlet:param name="redirect" value="<%= ddmDisplay.getViewTemplatesBackURL(liferayPortletRequest, liferayPortletResponse, classPK) %>" />
						<portlet:param name="structureId" value="<%= String.valueOf(structure.getStructureId()) %>" />
					</portlet:renderURL>

					{
						label: '<%= UnicodeLanguageUtil.get(request, "view-history") %>',
						on: {
							click: function (event) {
								event.domEvent.preventDefault();

								window.location.href = '<%= viewHistoryURL %>';
							},
						},
					},
				];

				new A.Toolbar({
					boundingBox: '#<portlet:namespace />structureHistoryToolbar',
					children: toolbarChildren,
				}).render();
			</aui:script>
		</c:if>

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset>
					<aui:field-wrapper>
						<c:if test="<%= (structure != null) && (DDMStorageLinkLocalServiceUtil.getStructureStorageLinksCount(classPK) > 0) %>">
							<div class="alert alert-warning">
								<liferay-ui:message key="there-are-content-references-to-this-structure.-you-may-lose-data-if-a-field-name-is-renamed-or-removed" />
							</div>
						</c:if>

						<c:if test="<%= (classPK > 0) && (DDMTemplateLocalServiceUtil.getTemplatesCount(null, classNameId, classPK) > 0) %>">
							<div class="alert alert-info">
								<liferay-ui:message key="there-are-template-references-to-this-structure.-please-update-them-if-a-field-name-is-renamed-or-removed" />
							</div>
						</c:if>

						<c:if test="<%= (structure != null) && (groupId != scopeGroupId) %>">
							<div class="alert alert-warning">
								<liferay-ui:message key="this-structure-does-not-belong-to-this-site.-you-may-affect-other-sites-if-you-edit-this-structure" />
							</div>
						</c:if>
					</aui:field-wrapper>

					<aui:input name="name" />

					<liferay-ui:panel-container
						cssClass="lfr-structure-entry-details-container"
						extended="<%= false %>"
						id="structureDetailsPanelContainer"
						persistState="<%= true %>"
					>
						<liferay-ui:panel
							collapsible="<%= true %>"
							defaultState="closed"
							extended="<%= false %>"
							id="structureDetailsSectionPanel"
							markupView="lexicon"
							persistState="<%= true %>"
							title='<%= LanguageUtil.get(request, "details") %>'
						>
							<clay:row
								cssClass="lfr-ddm-types-form-column"
							>
								<aui:input name="storageType" type="hidden" value="<%= storageTypeValue %>" />
							</clay:row>

							<c:if test="<%= !ddmDisplayContext.autogenerateStructureKey() %>">
								<aui:input disabled="<%= (structure != null) ? true : false %>" label='<%= LanguageUtil.format(request, "x-key", HtmlUtil.escape(ddmDisplay.getStructureName(locale)), false) %>' name="structureKey" />
							</c:if>

							<aui:input name="description" />

							<aui:field-wrapper label='<%= LanguageUtil.format(request, "parent-x", HtmlUtil.escape(ddmDisplay.getStructureName(locale)), false) %>'>
								<aui:input name="parentStructureId" type="hidden" value="<%= parentStructureId %>" />

								<aui:input cssClass="lfr-input-text" disabled="<%= true %>" label="" name="parentStructureName" type="text" value="<%= parentStructureName %>" />

								<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "openParentStructureSelector();" %>' value="select" />

								<aui:button disabled="<%= Validator.isNull(parentStructureName) %>" name="removeParentStructureButton" onClick='<%= liferayPortletResponse.getNamespace() + "removeParentStructure();" %>' value="remove" />
							</aui:field-wrapper>

							<c:if test="<%= structure != null %>">
								<portlet:resourceURL id="/dynamic_data_mapping/get_structure" var="getStructureURL">
									<portlet:param name="structureId" value="<%= String.valueOf(classPK) %>" />
								</portlet:resourceURL>

								<aui:input name="url" type="resource" value="<%= getStructureURL.toString() %>" />

								<c:if test="<%= Validator.isNotNull(refererWebDAVToken) %>">
									<aui:input name="webDavURL" type="resource" value="<%= structure.getWebDavURL(themeDisplay, refererWebDAVToken) %>" />
								</c:if>
							</c:if>
						</liferay-ui:panel>
					</liferay-ui:panel-container>

					<%@ include file="/form_builder.jspf" %>
				</aui:fieldset>
			</div>
		</div>
	</aui:form>

	<aui:button-row>
		<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "saveStructure(false);" %>' primary="<%= true %>" value='<%= LanguageUtil.get(request, "save") %>' />

		<c:if test="<%= ddmDisplay.isVersioningEnabled() %>">
			<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "saveStructure(true);" %>' value='<%= LanguageUtil.get(request, "save-draft") %>' />
		</c:if>

		<aui:button href="<%= PortalUtil.escapeRedirect(ddmDisplay.getViewTemplatesBackURL(liferayPortletRequest, liferayPortletResponse, classPK)) %>" type="cancel" />
	</aui:button-row>
</clay:container-fluid>

<aui:script>
	function <portlet:namespace />openParentStructureSelector() {
		const opener = Liferay.Util.getOpener();

		opener.Liferay.Util.openSelectionModal({
			onSelect: function (selectedItem) {
				const form = document.<portlet:namespace />fm;

				if (!form) {
					return;
				}

				Liferay.Util.setFormValues(form, {
					parentStructureId: selectedItem.ddmstructureid,
					parentStructureName: Liferay.Util.unescape(selectedItem.name),
				});

				const removeParentStructureButton = Liferay.Util.getFormElement(
					form,
					'removeParentStructureButton'
				);

				if (removeParentStructureButton) {
					Liferay.Util.toggleDisabled(removeParentStructureButton, false);
				}
			},
			selectEventName: '<portlet:namespace />selectParentStructure',
			title: '<%= HtmlUtil.escapeJS(scopeTitle) %>',

			<%
			Portlet portlet = PortletLocalServiceUtil.getPortletById(portletDisplay.getId());
			%>

			url: '<%=
				PortletURLBuilder.create(
					PortletURLFactoryUtil.create(request, DDMPortletKeys.DYNAMIC_DATA_MAPPING, PortletRequest.RENDER_PHASE)
				).setMVCPath(
					"/select_structure.jsp"
				).setParameter(
					"classNameId", PortalUtil.getClassNameId(DDMStructure.class)
				).setParameter(
					"classPK", (structure != null) ? structure.getPrimaryKey() : 0
				).setParameter(
					"groupId", groupId
				).setParameter(
					"navigationStartsOn", DDMNavigationHelper.EDIT_STRUCTURE
				).setParameter(
					"portletResourceNamespace", liferayPortletResponse.getNamespace()
				).setParameter(
					"refererPortletName", refererPortletName
				).setParameter(
					"showAncestorScopes", true
				).setParameter(
					"showBackURL", false
				).setParameter(
					"showHeader", false
				).setParameter(
					"showManageTemplates", false
				).setParameter(
					"structureAvailableFields", liferayPortletResponse.getNamespace() + "getAvailableFields"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString()
				%>',
		});
	}

	function <portlet:namespace />removeParentStructure() {
		const form = document.<portlet:namespace />fm;

		if (!form) {
			return;
		}

		Liferay.Util.setFormValues(form, {
			parentStructureId: '',
			parentStructureName: '',
		});

		const removeParentStructureButton = Liferay.Util.getFormElement(
			form,
			'removeParentStructureButton'
		);

		if (removeParentStructureButton) {
			Liferay.Util.toggleDisabled(removeParentStructureButton, true);
		}
	}

	function <portlet:namespace />saveStructure(draft) {
		Liferay.Util.postForm(document.<portlet:namespace />fm, {
			data: {
				definition: <portlet:namespace />formBuilder.getContentValue(),
				status: draft
					? <%= String.valueOf(WorkflowConstants.STATUS_DRAFT) %>
					: <%= String.valueOf(WorkflowConstants.STATUS_APPROVED) %>,
			},
		});
	}
</aui:script>