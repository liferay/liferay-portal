<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
DLEditFolderDisplayContext dlEditFolderDisplayContext = new DLEditFolderDisplayContext(request);

Folder folder = dlEditFolderDisplayContext.getFolder();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(dlEditFolderDisplayContext.getRedirect());

renderResponse.setTitle(dlEditFolderDisplayContext.getHeaderTitle());
%>

<liferay-util:buffer
	var="removeFileEntryTypeIcon"
>
	<liferay-ui:icon
		icon="times"
		label="<%= true %>"
		markupView="lexicon"
		message="remove"
	/>
</liferay-util:buffer>

<clay:container-fluid
	cssClass="container-form-lg"
	size="lg"
>
	<portlet:actionURL name="/document_library/edit_folder" var="editFolderURL">
		<portlet:param name="mvcRenderCommandName" value="/document_library/edit_folder" />
	</portlet:actionURL>

	<aui:form action="<%= editFolderURL %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "savePage();" %>'>
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= dlEditFolderDisplayContext.getCmd() %>" />
		<aui:input name="redirect" type="hidden" value="<%= dlEditFolderDisplayContext.getRedirect() %>" />
		<aui:input name="portletResource" type="hidden" value='<%= ParamUtil.getString(request, "portletResource") %>' />
		<aui:input name="folderId" type="hidden" value="<%= dlEditFolderDisplayContext.getFolderId() %>" />
		<aui:input name="repositoryId" type="hidden" value="<%= dlEditFolderDisplayContext.getRepositoryId() %>" />
		<aui:input name="parentFolderId" type="hidden" value="<%= dlEditFolderDisplayContext.getParentFolderId() %>" />

		<liferay-ui:error exception="<%= DuplicateDLFolderExternalReferenceCodeException.class %>" message="please-enter-a-unique-external-reference-code" />
		<liferay-ui:error exception="<%= DuplicateFileEntryException.class %>" message="please-enter-a-unique-folder-name" />
		<liferay-ui:error exception="<%= DuplicateFolderNameException.class %>" message="please-enter-a-unique-folder-name" />

		<liferay-ui:error exception="<%= FolderNameException.class %>">
			<p>
				<liferay-ui:message arguments="<%= DLFolderConstants.getNameReservedWords(PropsValues.DL_NAME_BLACKLIST) %>" key="the-folder-name-cannot-be-blank-or-a-reserved-word-such-as-x" />
			</p>

			<p>
				<liferay-ui:message arguments="<%= DLFolderConstants.getNameInvalidEndCharacters(PropsValues.DL_CHAR_LAST_BLACKLIST) %>" key="the-folder-name-cannot-end-with-the-following-characters-x" />
			</p>

			<p>
				<liferay-ui:message arguments="<%= DLFolderConstants.getNameInvalidCharacters(PropsValues.DL_CHAR_BLACKLIST) %>" key="the-folder-name-cannot-contain-the-following-invalid-characters-x" />
			</p>
		</liferay-ui:error>

		<liferay-ui:error exception="<%= RequiredFileEntryTypeException.class %>" message="please-select-a-document-type" />

		<aui:model-context bean="<%= folder %>" model="<%= DLFolder.class %>" />

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset>
					<c:if test="<%= !dlEditFolderDisplayContext.isRootFolder() %>">
						<c:if test="<%= folder != null %>">
							<aui:input disabled="<%= !dlEditFolderDisplayContext.hasUpdateDLFolderPermission() %>" name="parentFolder" type="resource" value="<%= dlEditFolderDisplayContext.getParentFolderName() %>" />
						</c:if>

						<aui:input disabled="<%= !dlEditFolderDisplayContext.hasUpdateDLFolderPermission() %>" name="name" />

						<c:if test="<%= dlEditFolderDisplayContext.isShowDescription() %>">
							<aui:input disabled="<%= !dlEditFolderDisplayContext.hasUpdateDLFolderPermission() %>" name="description" />
						</c:if>

						<aui:field-wrapper cssClass="form-group" label="external-reference-code" name="externalReferenceCode">
							<div class="small text-secondary"><liferay-ui:message key="unique-key-for-referencing-the-folder-definition" /></div>

							<div class="input-group">
								<div class="input-group-item">
									<aui:input disabled="<%= dlEditFolderDisplayContext.isERCFieldEnabled() %>" label="" name="externalReferenceCode" type="text" />
								</div>
							</div>
						</aui:field-wrapper>
					</c:if>
				</aui:fieldset>

				<c:if test="<%= dlEditFolderDisplayContext.hasAdvancedUpdateDLFolderPermission() && dlEditFolderDisplayContext.isFileEntryTypeSupported() %>">
					<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" helpMessage="<%= dlEditFolderDisplayContext.getFileEntryTypeRestrictionsHelpMessage() %>" label="<%= dlEditFolderDisplayContext.getFileEntryTypeRestrictionsLabel() %>">
						<c:if test="<%= !dlEditFolderDisplayContext.isRootFolder() %>">
							<aui:input checked="<%= dlEditFolderDisplayContext.isRestrictionTypeInherit() %>" id="restrictionTypeInherit" label='<%= LanguageUtil.format(request, dlEditFolderDisplayContext.isWorkflowEnabled() ? "use-document-type-restrictions-and-workflow-of-the-parent-folder-x" : "use-document-type-restrictions-of-the-parent-folder-x", dlEditFolderDisplayContext.getParentFolderName(), false) %>' name="restrictionType" type="radio" value="<%= DLFolderConstants.RESTRICTION_TYPE_INHERIT %>" />

							<aui:input checked="<%= dlEditFolderDisplayContext.isRestrictionTypeFileEntryTypesAndWorkflow() %>" id="restrictionTypeDefined" label='<%= LanguageUtil.format(request, dlEditFolderDisplayContext.isWorkflowEnabled() ? "define-specific-document-type-restrictions-and-workflow-for-this-folder-x" : "define-specific-document-type-restrictions-for-this-folder-x", folder.getName(), false) %>' name="restrictionType" type="radio" value="<%= DLFolderConstants.RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW %>" />

							<div class="<%= dlEditFolderDisplayContext.isRestrictionTypeFileEntryTypesAndWorkflow() ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />restrictionTypeDefinedDiv">
								<liferay-ui:search-container
									headerNames="<%= dlEditFolderDisplayContext.getHeaderNames() %>"
									total="<%= dlEditFolderDisplayContext.getDLFileEntryTypesCount() %>"
								>
									<liferay-ui:search-container-results
										results="<%= dlEditFolderDisplayContext.getDLFileEntryTypes() %>"
									/>

									<liferay-ui:search-container-row
										className="com.liferay.document.library.kernel.model.DLFileEntryType"
										escapedModel="<%= true %>"
										keyPropertyName="fileEntryTypeId"
										modelVar="dlFileEntryType"
									>
										<liferay-ui:search-container-column-text
											name="name"
											value="<%= dlFileEntryType.getName(locale) %>"
										/>

										<c:if test="<%= dlEditFolderDisplayContext.isWorkflowEnabled() %>">
											<liferay-ui:search-container-column-text
												name="workflow"
											>
												<aui:select label="" name='<%= "workflowDefinition" + dlFileEntryType.getFileEntryTypeId() %>' title="workflow-definition">
													<aui:option label="no-workflow" value="" />

													<%
													for (WorkflowDefinition workflowDefinition : dlEditFolderDisplayContext.getWorkflowDefinitions()) {
													%>

														<aui:option label="<%= HtmlUtil.escapeAttribute(workflowDefinition.getTitle(dlEditFolderDisplayContext.getLanguageId())) %>" selected="<%= dlEditFolderDisplayContext.isWorkflowDefinitionSelected(workflowDefinition, dlFileEntryType.getFileEntryTypeId()) %>" value="<%= HtmlUtil.escapeAttribute(workflowDefinition.getName()) + StringPool.AT + workflowDefinition.getVersion() %>" />

													<%
													}
													%>

												</aui:select>
											</liferay-ui:search-container-column-text>
										</c:if>

										<liferay-ui:search-container-column-text>
											<a class="modify-link" data-rowId="<%= dlFileEntryType.getFileEntryTypeId() %>" href="javascript:void(0);"><%= removeFileEntryTypeIcon %></a>
										</liferay-ui:search-container-column-text>
									</liferay-ui:search-container-row>

									<liferay-ui:search-iterator
										markupView="lexicon"
										paginate="<%= false %>"
									/>
								</liferay-ui:search-container>

								<liferay-ui:icon
									cssClass="modify-link select-file-entry-type"
									icon="search"
									id="selectDocumentTypeButton"
									label="<%= true %>"
									linkCssClass="btn btn-secondary"
									markupView="lexicon"
									message="select-document-type"
									url="javascript:void(0);"
								/>

								<aui:select cssClass='<%= ListUtil.isNotEmpty(dlEditFolderDisplayContext.getDLFileEntryTypes()) ? "default-document-type" : "default-document-type hide" %>' helpMessage="default-document-type-help" label="default-document-type" name="defaultFileEntryTypeId">

									<%
									for (DLFileEntryType fileEntryType : dlEditFolderDisplayContext.getDLFileEntryTypes()) {
									%>

										<aui:option id='<%= liferayPortletResponse.getNamespace() + "defaultFileEntryTypeId-" + fileEntryType.getFileEntryTypeId() %>' label="<%= HtmlUtil.escape(fileEntryType.getName(locale)) %>" selected="<%= dlEditFolderDisplayContext.isFileEntryTypeSelected(fileEntryType) %>" value="<%= fileEntryType.getFileEntryTypeId() %>" />

									<%
									}
									%>

								</aui:select>
							</div>
						</c:if>

						<c:if test="<%= dlEditFolderDisplayContext.isWorkflowEnabled() %>">
							<c:choose>
								<c:when test="<%= !dlEditFolderDisplayContext.isRootFolder() %>">
									<aui:input checked="<%= dlEditFolderDisplayContext.isRestrictionTypeWorkflow() %>" id="restrictionTypeWorkflow" label='<%= LanguageUtil.format(locale, "set-the-default-workflow-for-the-folders-content-x", folder.getName(), false) %>' name="restrictionType" type="radio" value="<%= DLFolderConstants.RESTRICTION_TYPE_WORKFLOW %>" />
								</c:when>
								<c:otherwise>
									<aui:input name="restrictionType" type="hidden" value="<%= DLFolderConstants.RESTRICTION_TYPE_WORKFLOW %>" />
								</c:otherwise>
							</c:choose>

							<div class="<%= (dlEditFolderDisplayContext.isRootFolder() || dlEditFolderDisplayContext.isRestrictionTypeWorkflow()) ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />restrictionTypeWorkflowDiv">
								<aui:select label="default-workflow-for-all-document-types" name='<%= "workflowDefinition" + DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL %>'>
									<aui:option label="no-workflow" value="" />

									<%
									for (WorkflowDefinition workflowDefinition : dlEditFolderDisplayContext.getWorkflowDefinitions()) {
									%>

										<aui:option label="<%= HtmlUtil.escapeAttribute(workflowDefinition.getTitle(dlEditFolderDisplayContext.getLanguageId())) %>" selected="<%= dlEditFolderDisplayContext.isWorkflowDefinitionSelected(workflowDefinition, DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL) %>" value="<%= HtmlUtil.escapeAttribute(workflowDefinition.getName()) + StringPool.AT + workflowDefinition.getVersion() %>" />

									<%
									}
									%>

								</aui:select>
							</div>
						</c:if>
					</aui:fieldset>
				</c:if>

				<c:if test="<%= !dlEditFolderDisplayContext.isRootFolder() %>">
					<c:if test="<%= dlEditFolderDisplayContext.isSupportsMetadata() %>">
						<liferay-expando:custom-attributes-available
							className="<%= DLFolderConstants.getClassName() %>"
						>
							<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="custom-fields">
								<liferay-expando:custom-attribute-list
									className="<%= DLFolderConstants.getClassName() %>"
									classPK="<%= (folder != null) ? folder.getFolderId() : 0 %>"
									editable="<%= true %>"
									label="<%= true %>"
								/>
							</aui:fieldset>
						</liferay-expando:custom-attributes-available>
					</c:if>

					<c:if test="<%= dlEditFolderDisplayContext.isSupportsPermissions() %>">
						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="permissions">
							<liferay-ui:input-permissions
								modelName="<%= DLFolderConstants.getClassName() %>"
							/>
						</aui:fieldset>
					</c:if>
				</c:if>

				<div class="sheet-footer">
					<aui:button type="submit" />

					<aui:button href="<%= dlEditFolderDisplayContext.getRedirect() %>" type="cancel" />
				</div>
			</div>
		</div>
	</aui:form>
</clay:container-fluid>

<liferay-util:buffer
	var="workflowDefinitionsBuffer"
>
	<c:if test="<%= dlEditFolderDisplayContext.isWorkflowEnabled() %>">
		<aui:select label="" name="LIFERAY_WORKFLOW_DEFINITION_FILE_ENTRY_TYPE" title="workflow-definition">
			<aui:option label="no-workflow" value="" />

			<%
			for (WorkflowDefinition workflowDefinition : dlEditFolderDisplayContext.getWorkflowDefinitions()) {
			%>

				<aui:option label="<%= HtmlUtil.escapeAttribute(workflowDefinition.getTitle(dlEditFolderDisplayContext.getLanguageId())) %>" value="<%= HtmlUtil.escapeAttribute(workflowDefinition.getName()) + StringPool.AT + workflowDefinition.getVersion() %>" />

			<%
			}
			%>

		</aui:select>
	</c:if>
</liferay-util:buffer>

<aui:script sandbox="<%= true %>">
	window['<portlet:namespace />documentTypesChanged'] = false;

	window['<portlet:namespace />savePage'] = function () {
		var message =
			'<%= UnicodeLanguageUtil.get(request, dlEditFolderDisplayContext.isWorkflowEnabled() ? "change-document-types-and-workflow-message" : "change-document-types-message") %>';

		if (<portlet:namespace />documentTypesChanged) {
			Liferay.Util.openConfirmModal({
				message: message,
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						submitForm(document.<portlet:namespace />fm);
					}
				},
			});
		}
		else {
			submitForm(document.<portlet:namespace />fm);
		}
	};

	Liferay.Util.toggleRadio('<portlet:namespace />restrictionTypeInherit', '', [
		'<portlet:namespace />restrictionTypeDefinedDiv',
		'<portlet:namespace />restrictionTypeWorkflowDiv',
	]);

	Liferay.Util.toggleRadio(
		'<portlet:namespace />restrictionTypeDefined',
		'<portlet:namespace />restrictionTypeDefinedDiv',
		'<portlet:namespace />restrictionTypeWorkflowDiv'
	);

	<c:if test="<%= !dlEditFolderDisplayContext.isRootFolder() %>">
		Liferay.Util.toggleRadio(
			'<portlet:namespace />restrictionTypeWorkflow',
			'<portlet:namespace />restrictionTypeWorkflowDiv',
			'<portlet:namespace />restrictionTypeDefinedDiv'
		);
	</c:if>
</aui:script>

<aui:script use="liferay-search-container">
	var selectFileEntryType = function (fileEntryTypeId, fileEntryTypeName) {
		var searchContainer = Liferay.SearchContainer.get(
			'<portlet:namespace />dlFileEntryTypesSearchContainer'
		);

		var fileEntryTypeLink =
			'<a class="modify-link" data-rowId="' +
			fileEntryTypeId +
			'" href="javascript:void(0);"><%= UnicodeFormatter.toString(removeFileEntryTypeIcon) %></a>';

		<c:choose>
			<c:when test="<%= dlEditFolderDisplayContext.isWorkflowEnabled() %>">
				var workflowDefinitions =
					'<%= UnicodeFormatter.toString(workflowDefinitionsBuffer) %>';

				workflowDefinitions = workflowDefinitions.replace(
					/LIFERAY_WORKFLOW_DEFINITION_FILE_ENTRY_TYPE/g,
					'workflowDefinition' + fileEntryTypeId
				);

				window.<portlet:namespace />documentTypesChanged = true;

				searchContainer.addRow(
					[fileEntryTypeName, workflowDefinitions, fileEntryTypeLink],
					fileEntryTypeId
				);
			</c:when>
			<c:otherwise>
				searchContainer.addRow(
					[fileEntryTypeName, fileEntryTypeLink],
					fileEntryTypeId
				);
			</c:otherwise>
		</c:choose>

		searchContainer.updateDataStore();

		var select = document.getElementById(
			'<portlet:namespace />defaultFileEntryTypeId'
		);

		var selectContainer = document.querySelector(
			'#<portlet:namespace />restrictionTypeDefinedDiv .default-document-type'
		);

		selectContainer.classList.remove('hide');
		selectContainer.removeAttribute('hidden');
		selectContainer.style.display = '';

		var option = document.createElement('option');
		option.setAttribute(
			'id',
			'<portlet:namespace />defaultFileEntryTypeId-' + fileEntryTypeId
		);
		option.setAttribute('value', fileEntryTypeId);
		option.text = fileEntryTypeName;

		select.classList.remove('hide');
		select.removeAttribute('hidden');
		select.style.display = '';

		select.appendChild(option);
	};

	var selectDocumentTypeButton = document.getElementById(
		'<portlet:namespace />selectDocumentTypeButton'
	);

	if (selectDocumentTypeButton) {
		selectDocumentTypeButton.addEventListener('click', () => {
			var searchContainer = Liferay.SearchContainer.get(
				'<portlet:namespace />dlFileEntryTypesSearchContainer'
			);

			var searchContainerData = searchContainer.getData();

			if (!searchContainerData.length) {
				searchContainerData = [];
			}
			else {
				searchContainerData = searchContainerData.split(',');
			}

			Liferay.Util.openSelectionModal({
				id: '<portlet:namespace />fileEntryTypeSelector',
				onSelect: function (selectedItem) {
					selectFileEntryType(
						selectedItem.entityid,
						selectedItem.entityname
					);
				},
				selectEventName: '<portlet:namespace />selectFileEntryType',
				selectedData: searchContainerData,
				title: '<%= UnicodeLanguageUtil.get(request, "document-types") %>',
				url: '<portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString() %>"><portlet:param name="mvcPath" value="/document_library/select_restricted_file_entry_type.jsp" /><portlet:param name="includeBasicFileEntryType" value="<%= Boolean.TRUE.toString() %>" /></portlet:renderURL>',
			});
		});
	}

	var searchContainer = Liferay.SearchContainer.get(
		'<portlet:namespace />dlFileEntryTypesSearchContainer'
	);

	searchContainer.get('contentBox').delegate(
		'click',
		(event) => {
			var link = event.currentTarget;

			var tr = link.ancestor('tr');

			searchContainer.deleteRow(tr, link.getAttribute('data-rowId'));

			var option = document.getElementById(
				'<portlet:namespace />defaultFileEntryTypeId-' +
					link.getAttribute('data-rowId')
			);

			option.parentElement.removeChild(option);

			window.<portlet:namespace />documentTypesChanged = true;
		},
		'.modify-link'
	);
</aui:script>

<%
if (!dlEditFolderDisplayContext.isRootFolder() && (folder == null)) {
	DLBreadcrumbUtil.addPortletBreadcrumbEntries(dlEditFolderDisplayContext.getParentFolderId(), request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-folder"), currentURL);
}
else {
	DLBreadcrumbUtil.addPortletBreadcrumbEntries(dlEditFolderDisplayContext.getFolderId(), request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "edit"), currentURL);
}
%>