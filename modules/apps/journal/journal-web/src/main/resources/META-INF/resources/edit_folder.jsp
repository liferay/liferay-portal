<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

JournalFolder folder = journalDisplayContext.getFolder();

long folderId = BeanParamUtil.getLong(folder, request, "folderId");

long parentFolderId = BeanParamUtil.getLong(folder, request, "parentFolderId", JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

boolean rootFolder = ParamUtil.getBoolean(request, "rootFolder");

Group scopeGroup = themeDisplay.getScopeGroup();

boolean workflowEnabled = (WorkflowHandlerRegistryUtil.getWorkflowHandler(JournalArticle.class.getName()) != null) && !scopeGroup.isLayoutSetPrototype();

List<WorkflowDefinition> workflowDefinitions = null;

if (workflowEnabled) {
	workflowDefinitions = WorkflowDefinitionManagerUtil.liberalGetActiveWorkflowDefinitions(company.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
}

String languageId = LocaleUtil.toLanguageId(locale);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

String title = StringPool.BLANK;

if (rootFolder) {
	title = LanguageUtil.get(request, "home");
}
else {
	if (folder == null) {
		title = LanguageUtil.get(request, "new-folder");
	}
	else {
		title = folder.getName();
	}
}

renderResponse.setTitle(title);
%>

<portlet:actionURL name='<%= rootFolder ? "/journal/update_workflow_definitions" : ((folder == null) ? "/journal/add_folder" : "/journal/update_folder") %>' var="editFolderURL">
	<portlet:param name="mvcPath" value="/edit_folder.jsp" />
</portlet:actionURL>

<liferay-util:buffer
	var="removeButton"
>
	<button aria-label="<%= LanguageUtil.get(request, "remove") %>" class="btn btn-monospaced btn-outline-borderless btn-outline-secondary float-right modify-link" data-rowId="REMOVE_BUTTON_ROW_ID" title="<%= LanguageUtil.get(request, "remove") %>" type="button">
		<clay:icon
			symbol="times-circle"
		/>
	</button>
</liferay-util:buffer>

<liferay-util:buffer
	var="workflowDefinitionsBuffer"
>
	<c:if test="<%= workflowEnabled %>">
		<aui:select label="" name="WORKFLOW_NAME" title="workflow-definition" wrapperCssClass="mb-0">
			<aui:option label="no-workflow" value="" />

			<%
			for (WorkflowDefinition workflowDefinition : workflowDefinitions) {
			%>

				<aui:option label="<%= HtmlUtil.escape(workflowDefinition.getTitle(languageId)) %>" value="<%= HtmlUtil.escapeAttribute(workflowDefinition.getName()) + StringPool.AT + workflowDefinition.getVersion() %>" />

			<%
			}
			%>

		</aui:select>
	</c:if>
</liferay-util:buffer>

<liferay-frontend:edit-form
	action="<%= editFolderURL %>"
	method="post"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="folderId" type="hidden" value="<%= folderId %>" />
	<aui:input name="parentFolderId" type="hidden" value="<%= parentFolderId %>" />

	<liferay-frontend:edit-form-body>
		<liferay-ui:error exception="<%= DuplicateFolderNameException.class %>" message="please-enter-a-unique-folder-name" />

		<liferay-ui:error exception="<%= FolderNameException.class %>">
			<p>
				<liferay-ui:message arguments="<%= JournalFolderConstants.NAME_RESERVED_WORDS %>" key="the-folder-name-cannot-be-blank-or-a-reserved-word-such-as-x" />
			</p>

			<p>
				<liferay-ui:message arguments="<%= JournalFolderConstants.getNameInvalidCharacters(journalDisplayContext.getCharactersBlacklist()) %>" key="the-folder-name-cannot-contain-the-following-invalid-characters-x" />
			</p>
		</liferay-ui:error>

		<liferay-ui:error exception="<%= InvalidDDMStructureException.class %>" message="you-cannot-apply-the-selected-structure-restrictions-for-this-folder.-at-least-one-web-content-references-another-structure" />

		<aui:model-context bean="<%= folder %>" model="<%= JournalFolder.class %>" />

		<c:if test="<%= !rootFolder %>">
			<liferay-frontend:fieldset
				collapsed="<%= false %>"
				collapsible="<%= true %>"
				disabled="<%= !journalDisplayContext.hasUpdateDLFolderPermission() %>"
				label="details"
			>
				<aui:input name="name" />

				<aui:input name="description" />
			</liferay-frontend:fieldset>

			<liferay-expando:custom-attributes-available
				className="<%= JournalFolder.class.getName() %>"
			>
				<liferay-frontend:fieldset
					collapsed="<%= true %>"
					collapsible="<%= true %>"
					disabled="<%= !journalDisplayContext.hasUpdateDLFolderPermission() %>"
					label="custom-fields"
				>
					<liferay-expando:custom-attribute-list
						className="<%= JournalFolder.class.getName() %>"
						classPK="<%= (folder != null) ? folder.getFolderId() : 0 %>"
						editable="<%= true %>"
						label="<%= true %>"
					/>
				</liferay-frontend:fieldset>
			</liferay-expando:custom-attributes-available>
		</c:if>

		<c:if test="<%= !rootFolder && (folder != null) %>">
			<liferay-frontend:fieldset
				collapsed="<%= true %>"
				collapsible="<%= true %>"
				disabled="<%= !journalDisplayContext.hasUpdateDLFolderPermission() %>"
				label="parent-folder"
			>

				<%
				String parentFolderName = LanguageUtil.get(request, "home");

				JournalFolder parentFolder = JournalFolderServiceUtil.fetchFolder(parentFolderId);

				if (parentFolder != null) {
					parentFolderName = parentFolder.getName();
				}
				%>

				<liferay-frontend:resource-selector
					inputLabel='<%= LanguageUtil.get(request, "folder-name") %>'
					inputName="newFolderId"
					modalTitle='<%= LanguageUtil.get(request, "select-folder") %>'
					resourceName="<%= parentFolderName %>"
					resourceValue="<%= String.valueOf(parentFolderId) %>"
					selectEventName="selectFolder"
					selectResourceURL='<%=
						PortletURLBuilder.createRenderURL(
							renderResponse
						).setMVCPath(
							"/select_folder.jsp"
						).setParameter(
							"folderId", folderId
						).setParameter(
							"parentFolderId", parentFolderId
						).setWindowState(
							LiferayWindowState.POP_UP
						).buildString()
					%>'
					showRemoveButton="<%= true %>"
				/>
			</liferay-frontend:fieldset>
		</c:if>

		<c:if test="<%= (rootFolder || (folder != null)) && journalDisplayContext.hasAdvancedUpdateDLFolderPermission() %>">

			<%
			List<DDMStructure> ddmStructures = journalDisplayContext.getDDMStructures(JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW);

			String headerNames = null;

			if (workflowEnabled) {
				headerNames = "name,workflow,null";
			}
			else {
				headerNames = "name,null";
			}
			%>

			<liferay-frontend:fieldset
				collapsed="<%= true %>"
				collapsible="<%= true %>"
				cssClass="structure-restrictions"
				helpMessage='<%= rootFolder ? "" : "structure-restrictions-help" %>'
				label='<%= rootFolder ? "" : (workflowEnabled ? "structure-restrictions-and-workflow" : "structure-restrictions") %>'
			>
				<c:if test="<%= !rootFolder %>">

					<%
					JournalFolder parentFolder = JournalFolderLocalServiceUtil.fetchFolder(folder.getParentFolderId());

					String parentFolderName = LanguageUtil.get(request, "home");

					if (parentFolder != null) {
						parentFolderName = parentFolder.getName();
					}
					%>

					<aui:input checked="<%= folder.getRestrictionType() == JournalFolderConstants.RESTRICTION_TYPE_INHERIT %>" id="restrictionTypeInherit" label='<%= workflowEnabled ? LanguageUtil.format(request, "inherit-allowed-structures-and-workflows-from-the-parent-folder-x", HtmlUtil.escape(parentFolderName)) : LanguageUtil.format(request, "use-structure-restrictions-of-the-parent-folder-x", HtmlUtil.escape(parentFolderName)) %>' name="restrictionType" type="radio" value="<%= JournalFolderConstants.RESTRICTION_TYPE_INHERIT %>" />

					<aui:input checked="<%= folder.getRestrictionType() == JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW %>" id="restrictionTypeDefined" label='<%= workflowEnabled ? LanguageUtil.format(request, "set-the-allowed-structures-and-workflows-for-the-folders-content-x", HtmlUtil.escape(folder.getName())) : LanguageUtil.format(request, "define-specific-structure-restrictions-for-this-folder-x", HtmlUtil.escape(folder.getName())) %>' name="restrictionType" type="radio" value="<%= JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW %>" />

					<div class="<%= (folder.getRestrictionType() == JournalFolderConstants.RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW) ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />restrictionTypeDefinedDiv">
						<liferay-ui:search-container
							headerNames="<%= headerNames %>"
							total="<%= ddmStructures.size() %>"
						>
							<liferay-ui:search-container-results
								results="<%= ddmStructures %>"
							/>

							<liferay-ui:search-container-row
								className="com.liferay.dynamic.data.mapping.model.DDMStructure"
								keyProperty="structureId"
								modelVar="ddmStructure"
							>
								<liferay-ui:search-container-column-text
									cssClass="table-cell-expand table-cell-minw-200 table-title"
									name="name"
									value="<%= HtmlUtil.escape(ddmStructure.getName(locale)) %>"
								/>

								<c:if test="<%= workflowEnabled %>">
									<liferay-ui:search-container-column-text
										cssClass="table-cell-expand table-cell-minw-200"
										name="workflow"
									>
										<aui:select label="" name='<%= "workflowDefinition" + ddmStructure.getStructureId() %>' wrapperCssClass="mb-0">
											<aui:option label="no-workflow" value="" />

											<%
											WorkflowDefinitionLink workflowDefinitionLink = WorkflowDefinitionLinkLocalServiceUtil.fetchWorkflowDefinitionLink(company.getCompanyId(), scopeGroupId, JournalFolder.class.getName(), folderId, ddmStructure.getStructureId(), true);

											for (WorkflowDefinition workflowDefinition : workflowDefinitions) {
												boolean selected = false;

												if ((workflowDefinitionLink != null) && Objects.equals(workflowDefinitionLink.getWorkflowDefinitionName(), workflowDefinition.getName()) && (workflowDefinitionLink.getWorkflowDefinitionVersion() == workflowDefinition.getVersion())) {
													selected = true;
												}
											%>

												<aui:option label="<%= HtmlUtil.escape(workflowDefinition.getTitle(languageId)) %>" selected="<%= selected %>" value="<%= HtmlUtil.escapeAttribute(workflowDefinition.getName()) + StringPool.AT + workflowDefinition.getVersion() %>" />

											<%
											}
											%>

										</aui:select>
									</liferay-ui:search-container-column-text>
								</c:if>

								<liferay-ui:search-container-column-text>
									<clay:button
										aria-label='<%= LanguageUtil.get(request, "remove") %>'
										borderless="<%= true %>"
										cssClass="lfr-portal-tooltip modify-link"
										data-rowId="<%= ddmStructure.getStructureId() %>"
										displayType="secondary"
										icon="times-circle"
										monospaced="<%= true %>"
										title="remove"
									/>
								</liferay-ui:search-container-column-text>
							</liferay-ui:search-container-row>

							<liferay-ui:search-iterator
								markupView="lexicon"
								paginate="<%= false %>"
							/>
						</liferay-ui:search-container>

						<clay:button
							displayType="secondary"
							id='<%= liferayPortletResponse.getNamespace() + "selectDDMStructure" %>'
							label="choose-structure"
						/>

						<liferay-frontend:component
							context='<%=
								HashMapBuilder.<String, Object>put(
									"removeButton", removeButton
								).put(
									"selectDDMStructureURL", journalDisplayContext.getSelectDDMStructureURL()
								).put(
									"workflowDefinitions", workflowDefinitionsBuffer
								).put(
									"workflowEnabled", workflowEnabled
								).build()
							%>'
							module="{SelectDDMStructureButton} from journal-web"
						/>
					</div>
				</c:if>

				<c:if test="<%= workflowEnabled %>">
					<c:choose>
						<c:when test="<%= !rootFolder %>">
							<aui:input checked="<%= folder.getRestrictionType() == JournalFolderConstants.RESTRICTION_TYPE_WORKFLOW %>" id="restrictionTypeWorkflow" label='<%= LanguageUtil.format(request, "set-the-default-workflow-for-the-folders-content-x", HtmlUtil.escape(folder.getName())) %>' name="restrictionType" type="radio" value="<%= JournalFolderConstants.RESTRICTION_TYPE_WORKFLOW %>" />
						</c:when>
						<c:otherwise>
							<aui:input name="restrictionType" type="hidden" value="<%= JournalFolderConstants.RESTRICTION_TYPE_WORKFLOW %>" />
						</c:otherwise>
					</c:choose>

					<div class="<%= (rootFolder || (folder.getRestrictionType() == JournalFolderConstants.RESTRICTION_TYPE_WORKFLOW)) ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />restrictionTypeWorkflowDiv">
						<aui:select label='<%= rootFolder ? "default-workflow-for-all-structures" : StringPool.BLANK %>' name='<%= "workflowDefinition" + JournalArticleConstants.DDM_STRUCTURE_ID_ALL %>'>
							<aui:option label="no-workflow" value="" />

							<%
							WorkflowDefinitionLink workflowDefinitionLink = WorkflowDefinitionLinkLocalServiceUtil.fetchWorkflowDefinitionLink(company.getCompanyId(), scopeGroupId, JournalFolder.class.getName(), folderId, JournalArticleConstants.DDM_STRUCTURE_ID_ALL, true);

							if (workflowDefinitionLink == null) {
								workflowDefinitionLink = WorkflowDefinitionLinkLocalServiceUtil.fetchWorkflowDefinitionLink(company.getCompanyId(), scopeGroupId, JournalArticle.class.getName(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, JournalArticleConstants.DDM_STRUCTURE_ID_ALL, true);
							}

							for (WorkflowDefinition workflowDefinition : workflowDefinitions) {
								boolean selected = false;

								if ((workflowDefinitionLink != null) && Objects.equals(workflowDefinitionLink.getWorkflowDefinitionName(), workflowDefinition.getName()) && (workflowDefinitionLink.getWorkflowDefinitionVersion() == workflowDefinition.getVersion())) {
									selected = true;
								}
							%>

								<aui:option label="<%= HtmlUtil.escape(workflowDefinition.getTitle(languageId)) %>" selected="<%= selected %>" value="<%= HtmlUtil.escapeAttribute(workflowDefinition.getName()) + StringPool.AT + workflowDefinition.getVersion() %>" />

							<%
							}
							%>

						</aui:select>
					</div>
				</c:if>
			</liferay-frontend:fieldset>
		</c:if>

		<c:if test="<%= !rootFolder && (folder == null) %>">
			<liferay-frontend:fieldset
				collapsed="<%= true %>"
				collapsible="<%= true %>"
				label="permissions"
			>
				<liferay-ui:input-permissions
					modelName="<%= JournalFolder.class.getName() %>"
				/>
			</liferay-frontend:fieldset>
		</c:if>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= redirect %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script>
	Liferay.Util.toggleRadio('<portlet:namespace />restrictionTypeInherit', '', [
		'<portlet:namespace />restrictionTypeDefinedDiv',
		'<portlet:namespace />restrictionTypeWorkflowDiv',
	]);
	Liferay.Util.toggleRadio(
		'<portlet:namespace />restrictionTypeDefined',
		'<portlet:namespace />restrictionTypeDefinedDiv',
		'<portlet:namespace />restrictionTypeWorkflowDiv'
	);

	<c:if test="<%= !rootFolder %>">
		Liferay.Util.toggleRadio(
			'<portlet:namespace />restrictionTypeWorkflow',
			'<portlet:namespace />restrictionTypeWorkflowDiv',
			'<portlet:namespace />restrictionTypeDefinedDiv'
		);
	</c:if>
</aui:script>