<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/designer/init.jsp" %>

<%
String successMessageKey = KaleoDesignerPortletKeys.KALEO_DESIGNER + "requestProcessed";
%>

<c:if test="<%= MultiSessionMessages.contains(renderRequest, successMessageKey) %>">
	<liferay-ui:success key="<%= successMessageKey %>" message="<%= (String)MultiSessionMessages.get(renderRequest, successMessageKey) %>" translateMessage="<%= false %>" />
</c:if>

<%
String mvcPath = ParamUtil.getString(request, "mvcPath", "/designer/edit_workflow_definition.jsp");

String redirect = ParamUtil.getString(request, "redirect");
String closeRedirect = ParamUtil.getString(request, "closeRedirect");

KaleoDefinitionVersion kaleoDefinitionVersion = (KaleoDefinitionVersion)request.getAttribute(KaleoDesignerWebKeys.KALEO_DRAFT_DEFINITION);

KaleoDefinition kaleoDefinition = kaleoDesignerDisplayContext.getKaleoDefinition(kaleoDefinitionVersion);

String name = BeanParamUtil.getString(kaleoDefinitionVersion, request, "name");
String draftVersion = BeanParamUtil.getString(kaleoDefinitionVersion, request, "version");
String content = BeanParamUtil.getString(kaleoDefinitionVersion, request, "contentAsXML");

String latestDraftVersion = StringPool.BLANK;
int version = 0;

KaleoDefinitionVersion latestKaleoDefinitionVersion = null;

if (kaleoDefinitionVersion != null) {
	if (Validator.isNull(name)) {
		name = kaleoDefinitionVersion.getName();
	}

	draftVersion = kaleoDefinitionVersion.getVersion();

	latestKaleoDefinitionVersion = KaleoDefinitionVersionLocalServiceUtil.getLatestKaleoDefinitionVersion(themeDisplay.getCompanyId(), name);

	latestDraftVersion = latestKaleoDefinitionVersion.getVersion();

	if (kaleoDefinition != null) {
		version = kaleoDefinition.getVersion();
	}
}

portletDisplay.setShowBackIcon(true);

portletDisplay.setURLBack(
	PortletURLBuilder.create(
		PortalUtil.getControlPanelPortletURL(renderRequest, KaleoDesignerPortletKeys.CONTROL_PANEL_WORKFLOW, PortletRequest.RENDER_PHASE)
	).setMVCPath(
		"/view.jsp"
	).buildString());

renderResponse.setTitle((kaleoDefinitionVersion == null) ? LanguageUtil.get(request, "new-workflow") : kaleoDefinitionVersion.getTitle(locale));

String state = (String)request.getParameter(WorkflowWebKeys.WORKFLOW_JSP_STATE);

String duplicateTitle = kaleoDesignerDisplayContext.getDuplicateTitle(kaleoDefinition);

String randomNamespace = StringUtil.randomId() + StringPool.UNDERLINE;
%>

<div class="container-fluid container-fluid-max-xl lfr-alert-container"></div>

<c:if test="<%= kaleoDefinitionVersion != null %>">
	<aui:model-context bean="<%= kaleoDefinitionVersion %>" model="<%= KaleoDefinitionVersion.class %>" />

	<div class="management-bar management-bar-light navbar navbar-expand-md">
		<clay:container-fluid>
			<ul class="m-auto navbar-nav"></ul>

			<ul class="middle navbar-nav">
				<li class="nav-item">
					<c:if test="<%= !Objects.equals(state, WorkflowWebKeys.WORKFLOW_PREVIEW_BEFORE_RESTORE_STATE) %>">
						<c:choose>
							<c:when test="<%= (kaleoDefinition != null) && kaleoDefinition.isActive() %>">
								<clay:label
									displayType="info"
									label="published"
									large="<%= true %>"
								/>
							</c:when>
							<c:otherwise>
								<clay:label
									label="not-published"
									large="<%= true %>"
								/>
							</c:otherwise>
						</c:choose>
					</c:if>

					<%
					String userName = kaleoDesignerDisplayContext.getUserName(kaleoDefinitionVersion);
					%>

					<span>
						<c:choose>
							<c:when test="<%= userName == null %>">
								<%= displayDateFormat.format(kaleoDefinitionVersion.getModifiedDate()) %>
							</c:when>
							<c:when test="<%= Objects.equals(state, WorkflowWebKeys.WORKFLOW_PREVIEW_BEFORE_RESTORE_STATE) %>">
								<liferay-ui:message arguments="<%= new String[] {displayDateFormat.format(kaleoDefinitionVersion.getModifiedDate()), HtmlUtil.escape(userName)} %>" key="revision-from-x-by-x" translateArguments="<%= false %>" />
							</c:when>
							<c:otherwise>
								<liferay-ui:message arguments="<%= new String[] {displayDateFormat.format(kaleoDefinitionVersion.getModifiedDate()), HtmlUtil.escape(userName)} %>" key="x,-by-x" translateArguments="<%= false %>" />
							</c:otherwise>
						</c:choose>
					</span>
				</li>
			</ul>

			<ul class="end navbar-nav">
				<li class="nav-item">
					<c:if test='<%= !Objects.equals(state, WorkflowWebKeys.WORKFLOW_PREVIEW_BEFORE_RESTORE_STATE) && !Objects.equals(state, "view") %>'>
						<liferay-frontend:sidebar-toggler-button
							cssClass="btn-secondary"
							icon="info-circle"
							label="info"
							typeMobile="relative"
						/>
					</c:if>
				</li>
			</ul>
		</clay:container-fluid>
	</div>
</c:if>

<div class="closed sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
	<c:if test='<%= (kaleoDefinitionVersion != null) && !Objects.equals(state, WorkflowWebKeys.WORKFLOW_PREVIEW_BEFORE_RESTORE_STATE) && !Objects.equals(state, "view") %>'>
		<div class="sidenav-menu-slider sidenav-visible-xs">
			<div class="sidebar sidebar-light">
				<div class="tbar-visible-xs">
					<nav class="component-tbar tbar">
						<clay:container-fluid>
							<ul class="tbar-nav">
								<li class="tbar-item">
									<clay:icon
										cssClass="component-action sidenav-close"
										symbol="times"
									/>
								</li>
							</ul>
						</clay:container-fluid>
					</nav>
				</div>

				<div class="sidebar-header">
					<div class="sidebar-section">
						<div class="component-title">
							<span class="text-truncate-inline">
								<span class="text-truncate"><%= HtmlUtil.escape(kaleoDefinitionVersion.getTitle(locale)) %></span>
							</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</c:if>

	<div class="sidenav-content">
		<clay:container-fluid
			size='<%= (Objects.equals(renderRequest.getWindowState(), LiferayWindowState.POP_UP) || Objects.equals(renderRequest.getWindowState(), WindowState.MAXIMIZED)) ? "xl" : "lg" %>'
		>
			<aui:form cssClass="full-width-content" method="post" name="fm" onSubmit="event.preventDefault();">
				<aui:model-context bean="<%= kaleoDefinitionVersion %>" model="<%= KaleoDefinitionVersion.class %>" />
				<aui:input name="mvcPath" type="hidden" value="<%= mvcPath %>" />
				<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
				<aui:input name="closeRedirect" type="hidden" value="<%= closeRedirect %>" />
				<aui:input name="name" type="hidden" value="<%= name %>" />
				<aui:input name="content" type="hidden" value="<%= content %>" />
				<aui:input name="version" type="hidden" value="<%= String.valueOf(version) %>" />
				<aui:input name="draftVersion" type="hidden" value="<%= draftVersion %>" />
				<aui:input name="latestDraftVersion" type="hidden" value="<%= latestDraftVersion %>" />

				<%@ include file="/designer/edit_kaleo_definition_version_exceptions.jspf" %>

				<div class="sheet">
					<div class="panel-group panel-group-flush">
						<aui:fieldset>
							<liferay-ui:input-localized
								disabled='<%= kaleoDesignerDisplayContext.isDefinitionInputDisabled((Objects.equals(state, WorkflowWebKeys.WORKFLOW_PREVIEW_BEFORE_RESTORE_STATE) || Objects.equals(state, "view")), kaleoDefinitionVersion, permissionChecker) %>'
								name="title"
								placeholder="untitled-workflow"
								xml='<%= BeanPropertiesUtil.getString(kaleoDefinitionVersion, "title") %>'
							/>
						</aui:fieldset>

						<aui:fieldset>
							<div class="diagram-builder property-builder" id="<portlet:namespace />propertyBuilder">
								<div class="property-builder-content" id="<portlet:namespace />propertyBuilderContent">
									<div class="tabbable">
										<div class="tabbable-content">
											<ul class="nav nav-tabs">
												<li class="active nav-item">
													<a class="active nav-link" href="javascript:void(0);">
														<liferay-ui:message key="nodes" />
													</a>
												</li>
												<li class="nav-item">
													<a class="nav-link" href="javascript:void(0);">
														<liferay-ui:message key="properties" />
													</a>
												</li>
											</ul>

											<div class="tab-content">
												<div class="tab-pane"></div>

												<div class="tab-pane"></div>
											</div>
										</div>
									</div>

									<div class="property-builder-content-container">
										<div class="tabbable">
											<div class="main-tab tabbable-content">
												<ul class="nav nav-tabs">
													<li class="active nav-item">
														<a class="active nav-link" href="javascript:void(0);">
															<liferay-ui:message key="diagram" />
														</a>
													</li>
													<li class="nav-item">
														<a class="nav-link" href="javascript:void(0);">
															<liferay-ui:message key="source" />
														</a>
													</li>
												</ul>

												<div class="tab-content">
													<div class="tab-pane">
														<div class="property-builder-canvas">
															<div class="property-builder-drop-container"></div>
														</div>
													</div>

													<div class="tab-pane">
														<liferay-util:buffer
															var="importFileMark"
														>
															<label class="btn btn-link p-0" for="<portlet:namespace />upload">
																<%= StringUtil.toLowerCase(LanguageUtil.get(request, "import-a-file")) %>
															</label>
														</liferay-util:buffer>

														<c:if test='<%= !kaleoDesignerDisplayContext.isDefinitionInputDisabled(Objects.equals(state, WorkflowWebKeys.WORKFLOW_PREVIEW_BEFORE_RESTORE_STATE) || Objects.equals(state, "view"), kaleoDefinitionVersion, permissionChecker) %>'>
															<liferay-ui:message arguments="<%= importFileMark %>" key="write-your-definition-or-x" translateArguments="<%= false %>" />
														</c:if>

														<input class="d-none" id="<portlet:namespace />upload" type="file" />

														<div class="lfr-template-editor-wrapper" id="<portlet:namespace />editorWrapper"></div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>

							<aui:script>
								window['<portlet:namespace />afterTabViewChange'] = function (event) {
									var tabContentNode = event.newVal.get('boundingBox');

									var kaleoDesigner = <portlet:namespace />kaleoDesigner;

									if (tabContentNode === kaleoDesigner.viewNode && kaleoDesigner.editor) {
										setTimeout(() => {
											kaleoDesigner.set('definition', kaleoDesigner.editor.get('value'));
										}, 0);
									}
								};

								window['<portlet:namespace />publishKaleoDefinitionVersion'] = function () {
									<portlet:namespace />updateContent();

									<portlet:namespace />updateAction(
										'<portlet:actionURL name="/kaleo_designer/publish_kaleo_definition_version" />'
									);

									submitForm(document.<portlet:namespace />fm);
								};

								window['<portlet:namespace />saveKaleoDefinitionVersion'] = function () {
									<portlet:namespace />updateContent();

									<portlet:namespace />updateAction(
										'<portlet:actionURL name="/kaleo_designer/save_kaleo_definition_version" />'
									);

									submitForm(document.<portlet:namespace />fm);
								};

								window['<portlet:namespace />updateAction'] = function (action) {
									var form = document.<portlet:namespace />fm;

									form.setAttribute('action', action);
								};

								window['<portlet:namespace />updateContent'] = function () {
									var content = document.getElementById('<portlet:namespace />content');

									var activeTab =
										<portlet:namespace />kaleoDesigner.contentTabView.getActiveTab();

									if (activeTab === <portlet:namespace />kaleoDesigner.sourceNode) {
										content.value = <portlet:namespace />kaleoDesigner.editor.get('value');
									}
									else {
										content.value = <portlet:namespace />kaleoDesigner.getContent();
									}
								};

								window['<portlet:namespace />closeKaleoDialog'] = function () {
									var dialog = Liferay.Util.getWindow();

									if (dialog) {
										dialog.destroy();
									}
								};

								<%
								String saveCallback = ParamUtil.getString(request, "saveCallback");
								%>

								<c:if test="<%= (kaleoDefinitionVersion != null) && Validator.isNotNull(saveCallback) %>">
									Liferay.Util.getOpener()['<%= HtmlUtil.escapeJS(saveCallback) %>'](
										'<%= HtmlUtil.escapeJS(name) %>',
										<%= version %>,
										<%= draftVersion %>
									);
								</c:if>
							</aui:script>

							<aui:script use="liferay-kaleo-designer-dialogs,liferay-kaleo-designer-utils,liferay-portlet-kaleo-designer">
								var MAP_ROLE_TYPES = {
									account: 6,
									depot: 5,
									organization: 3,
									regular: 1,
									site: 2,
								};

								var initializeKaleoDesigner = function () {
									window.<portlet:namespace />kaleoDesigner = new Liferay.KaleoDesigner({

										<%
										String availableFields = ParamUtil.getString(request, "availableFields");
										%>

										<c:if test="<%= Validator.isNotNull(availableFields) %>">
											availableFields: A.Object.getValue(
												window,
												'<%= HtmlUtil.escapeJS(availableFields) %>'.split('.')
											),
										</c:if>

										<%
										String availablePropertyModels = ParamUtil.getString(request, "availablePropertyModels", "Liferay.KaleoDesigner.AVAILABLE_PROPERTY_MODELS.KALEO_FORMS_EDIT");
										%>

										<c:if test="<%= Validator.isNotNull(availablePropertyModels) %>">
											availablePropertyModels: A.Object.getValue(
												window,
												'<%= HtmlUtil.escapeJS(availablePropertyModels) %>'.split('.')
											),
										</c:if>

										boundingBox: '#<portlet:namespace />propertyBuilder',
										data: {
											kaleoProcessId:
												'<%= ParamUtil.getLong(request, "kaleoProcessId") %>',
										},

										<c:if test="<%= Validator.isNotNull(content) %>">
											definition: '<%= HtmlUtil.escapeJS(content) %>',
										</c:if>

										<%
										String propertiesSaveCallback = ParamUtil.getString(request, "propertiesSaveCallback");
										%>

										<c:if test="<%= Validator.isNotNull(propertiesSaveCallback) %>">
											on: {
												save: Liferay.Util.getOpener()[
													'<%= HtmlUtil.escapeJS(propertiesSaveCallback) %>'
												],
											},
										</c:if>

										portletNamespace: '<portlet:namespace />',

										<%
										String portletResourceNamespace = ParamUtil.getString(request, "portletResourceNamespace");
										%>

										portletResourceNamespace:
											'<%= HtmlUtil.escapeJS(portletResourceNamespace) %>',
										srcNode: '#<portlet:namespace />propertyBuilderContent',
									}).render();

									<c:if test='<%= kaleoDesignerDisplayContext.isDefinitionInputDisabled(Objects.equals(state, WorkflowWebKeys.WORKFLOW_PREVIEW_BEFORE_RESTORE_STATE) || Objects.equals(state, "view"), kaleoDefinitionVersion, permissionChecker) %>'>
										<portlet:namespace />kaleoDesigner.after('render', () => {
											var diagramBuilderControlElements = document.querySelectorAll(
												'#<portlet:namespace />propertyBuilder .diagram-builder-controls'
											);

											diagramBuilderControlElements.forEach((element) => {
												element.parentElement.removeChild(element);
											});

											<portlet:namespace />kaleoDesigner.detachAll();

											<portlet:namespace />kaleoDesigner.set('readOnly', true);
										});
									</c:if>

									var uploadFile = document.querySelector('#<portlet:namespace />upload');

									var previousContent = '';

									uploadFile.addEventListener('change', (evt) => {
										var files = evt.target.files;

										if (files) {
											var reader = new FileReader();

											reader.onloadend = function (evt) {
												if (evt.target.readyState == FileReader.DONE) {
													previousContent =
														<portlet:namespace />kaleoDesigner.getEditorContent();

													<portlet:namespace />kaleoDesigner.setEditorContent(
														evt.target.result
													);

													uploadFile.value = '';

													Liferay.KaleoDesignerDialogs.showDefinitionImportSuccessMessage(
														'<portlet:namespace />'
													);
												}
											};

											reader.readAsText(files[0]);
										}
									});

									Liferay.on('<portlet:namespace />undoDefinition', (event) => {
										<portlet:namespace />kaleoDesigner.setEditorContent(previousContent);

										Liferay.KaleoDesignerDialogs.showActionUndoneSuccessMessage();
									});

									<portlet:namespace />kaleoDesigner.contentTabView.after({
										selectionChange: <portlet:namespace />afterTabViewChange,
									});

									var fields = <portlet:namespace />kaleoDesigner.get('fields');

									if (fields.size() == 0) {
										<portlet:namespace />kaleoDesigner.set('fields', [
											{
												name: 'StartNode',
												type: 'start',
												xy: [100, 40],
											},

											{
												actions: {
													description: [Liferay.KaleoDesignerStrings.approve],
													executionType: ['onEntry'],
													name: [Liferay.KaleoDesignerStrings.approve],
													script: [
														'com.liferay.portal.kernel.workflow.WorkflowStatusManagerUtil.updateStatus(com.liferay.portal.kernel.workflow.WorkflowConstants.getLabelStatus("approved"), workflowContext);',
													],
													scriptLanguage: ['groovy'],
												},
												name: 'EndNode',
												type: 'end',
												xy: [100, 500],
											},
										]);

										<portlet:namespace />kaleoDesigner.connect('StartNode', 'EndNode');
									}

									var createRoleAutocomplete = function (
										inputNode,
										resultTextLocator,
										selectFn
									) {
										var instance = this;

										var roleType = 0;
										var roleTypeNode = inputNode.previous('[name=roleType]');

										if (roleTypeNode) {
											roleType = roleTypeNode.val();
										}

										var type = MAP_ROLE_TYPES[roleType] || 0;

										var autocomplete = Liferay.KaleoDesignerAutoCompleteUtil.create(
											'<portlet:namespace />',
											inputNode,
											'<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="roles" />',
											null,
											resultTextLocator,
											selectFn
										);

										autocomplete.set(
											'requestTemplate',
											'&<portlet:namespace />type=' +
												type +
												'&<portlet:namespace />keywords={query}'
										);

										autocomplete.sendRequest('');
									};

									var createUserAutocomplete = function (
										inputNode,
										resultTextLocator,
										selectFn
									) {
										var autocomplete = Liferay.KaleoDesignerAutoCompleteUtil.create(
											'<portlet:namespace />',
											inputNode,
											'<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="users" />',
											null,
											resultTextLocator,
											selectFn
										);

										autocomplete.sendRequest('');
									};

									A.getDoc().delegate(
										'focus',
										(event) => {
											var inputNode = event.currentTarget;

											var inputName = inputNode.attr('name');

											if (inputName == 'roleName' || inputName == 'roleNameAC') {
												createRoleAutocomplete(inputNode, null, (event) => {
													var data = event.result.raw;
													var roleId = inputNode.next('[name=roleId]');

													if (roleId) {
														roleId.val(data.roleId);
													}
												});
											}
											else if (inputName == 'fullName') {
												createUserAutocomplete(inputNode, inputName, (event) => {
													var data = event.result.raw;

													A.each(data, (item, index, collection) => {
														var input = inputNode
															.siblings('[name=' + index + ']')
															.first();

														if (input) {
															input.val(data[index]);
														}
													});
												});
											}
										},
										'.assignments-cell-editor-input'
									);
								};

								var opener = Liferay.Util.getOpener();

								var inModal = window !== opener;

								if (inModal && opener.document.querySelector('.loading-animation')) {
									opener.Liferay.on('modalIframeLoaded', () => {
										initializeKaleoDesigner();
									});
								}
								else {
									initializeKaleoDesigner();
								}

								<c:choose>
									<c:when test="<%= kaleoDefinitionVersion == null %>">
										Liferay.componentReady('<portlet:namespace />title').then((titleComponent) => {
											var titlePlaceholderInput = titleComponent.get('inputPlaceholder');

											if (titlePlaceholderInput) {
												titlePlaceholderInput.after('change', (event) => {
													<portlet:namespace />kaleoDesigner.set(
														'definitionName',
														titleComponent.getValue()
													);
												});
											}
										});
									</c:when>
								</c:choose>

								var dialog = Liferay.Util.getWindow();

								if (dialog && !dialog._dialogAction) {
									dialog._dialogAction = function (event) {
										if (!event.newVal) {

											<%
											boolean refreshOpenerOnClose = ParamUtil.getBoolean(request, "refreshOpenerOnClose");
											%>

											<c:if test="<%= Validator.isNotNull(portletResourceNamespace) && refreshOpenerOnClose %>">

												<%
												String openerWindowName = ParamUtil.getString(request, "openerWindowName");
												%>

												var openerWindow = Liferay.Util.getTop();

												<c:if test="<%= Validator.isNotNull(openerWindowName) %>">
													var openerDialog = Liferay.Util.getWindow(
														'<%= HtmlUtil.escapeJS(openerWindowName) %>'
													);

													openerWindow = openerDialog.iframe.node
														.get('contentWindow')
														.getDOM();
												</c:if>

												openerWindow.Liferay.Portlet.refresh(
													'#p_p_id<%= HtmlUtil.escapeJS(portletResourceNamespace) %>'
												);
											</c:if>
										}
									};

									dialog.on('visibleChange', dialog._dialogAction);
								}
							</aui:script>
						</aui:fieldset>
					</div>
				</div>

				<c:choose>
					<c:when test='<%= !Objects.equals(state, WorkflowWebKeys.WORKFLOW_PREVIEW_BEFORE_RESTORE_STATE) && !Objects.equals(state, "view") %>'>
						<aui:button-row>
							<c:if test="<%= kaleoDesignerDisplayContext.isPublishKaleoDefinitionVersionButtonVisible(permissionChecker, kaleoDefinitionVersion) %>">
								<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "publishKaleoDefinitionVersion();" %>' primary="<%= true %>" value="<%= kaleoDesignerDisplayContext.getPublishKaleoDefinitionVersionButtonLabel(kaleoDefinitionVersion) %>" />
							</c:if>

							<c:if test="<%= kaleoDesignerDisplayContext.isSaveKaleoDefinitionVersionButtonVisible(kaleoDefinitionVersion) %>">
								<aui:button onClick='<%= liferayPortletResponse.getNamespace() + "saveKaleoDefinitionVersion();" %>' value="save" />
							</c:if>

							<c:if test="<%= Validator.isNotNull(closeRedirect) %>">
								<aui:button type="cancel" />
							</c:if>

							<span class="lfr-portlet-workflowdesigner-message" id="<portlet:namespace />toolbarMessage"></span>
						</aui:button-row>
					</c:when>
					<c:when test='<%= Objects.equals(state, "view") && KaleoDefinitionVersionPermission.contains(permissionChecker, kaleoDefinitionVersion, ActionKeys.UPDATE) %>'>
						<portlet:renderURL var="editURL">
							<portlet:param name="mvcPath" value="/designer/edit_workflow_definition.jsp" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="name" value="<%= kaleoDefinitionVersion.getName() %>" />
							<portlet:param name="draftVersion" value="<%= kaleoDefinitionVersion.getVersion() %>" />
						</portlet:renderURL>

						<aui:button-row>
							<aui:button href="<%= editURL %>" primary="<%= true %>" value="edit" />
						</aui:button-row>
					</c:when>
				</c:choose>
			</aui:form>
		</clay:container-fluid>
	</div>
</div>

<c:if test="<%= kaleoDefinition != null %>">
	<liferay-portlet:actionURL name="/kaleo_designer/duplicate_workflow_definition" portletName="<%= KaleoDesignerPortletKeys.KALEO_DESIGNER %>" var="duplicateWorkflowDefinition">
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</liferay-portlet:actionURL>

	<div class="hide" id="<%= randomNamespace %>titleInputLocalized">
		<aui:form name='<%= randomNamespace + "form" %>'>
			<aui:input name="randomNamespace" type="hidden" value="<%= randomNamespace %>" />
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
			<aui:input name="name" type="hidden" value="<%= PortalUUIDUtil.generate() %>" />
			<aui:input name="content" type="hidden" value="<%= kaleoDefinition.getContentAsXML() %>" />
			<aui:input name="defaultDuplicationTitle" type="hidden" value="<%= duplicateTitle %>" />
			<aui:input name="duplicatedDefinitionName" type="hidden" value="<%= kaleoDefinition.getName() %>" />
			<aui:input name="duplicatedDefinitionTitle" type="hidden" value="<%= HtmlUtil.escape(kaleoDefinition.getTitle(LanguageUtil.getLanguageId(request))) %>" />

			<aui:fieldset>
				<clay:col
					size="12"
				>
					<aui:field-wrapper label="title">
						<liferay-ui:input-localized
							name='<%= randomNamespace + "title" %>'
							xml="<%= duplicateTitle %>"
						/>
					</aui:field-wrapper>
				</clay:col>

				<clay:col
					size="12"
				>
					<liferay-ui:message key="copy-does-not-include-revisions" />
				</clay:col>
			</aui:fieldset>
		</aui:form>
	</div>

	<aui:script use="liferay-kaleo-designer-dialogs">
		var duplicateWorkflowTitle = '<liferay-ui:message key="duplicate-workflow" />';

		Liferay.on('<portlet:namespace />duplicateDefinition', function (event) {
			Liferay.KaleoDesignerDialogs.confirmBeforeDuplicateDialog(
				this,
				'<%= duplicateWorkflowDefinition %>',
				duplicateWorkflowTitle,
				'<%= randomNamespace %>',
				'<portlet:namespace />'
			);
		});
	</aui:script>
</c:if>