<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/import/init.jsp" %>

<%
long groupId = ParamUtil.getLong(request, "groupId");

Group group = null;

if (groupId > 0) {
	group = GroupLocalServiceUtil.getGroup(groupId);
}
else {
	group = (Group)request.getAttribute(WebKeys.GROUP);
}

FileEntry fileEntry = ExportImportHelperUtil.getTempFileEntry(groupId, themeDisplay.getUserId(), ExportImportHelper.TEMP_FOLDER_NAME);

ManifestSummary manifestSummary = ExportImportHelperUtil.getManifestSummary(user.getUserId(), themeDisplay.getSiteGroupId(), new HashMap<String, String[]>(), fileEntry);
%>

<liferay-ui:error exception="<%= LARFileException.class %>" message="please-specify-a-lar-file-to-import" />

<liferay-ui:error exception="<%= LARFileSizeException.class %>">
	<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(UploadServletRequestConfigurationProviderUtil.getMaxSize(), locale) %>" key="please-enter-a-file-with-a-valid-file-size-no-larger-than-x" translateArguments="<%= false %>" />
</liferay-ui:error>

<liferay-ui:error exception="<%= LARTypeException.class %>">

	<%
	LARTypeException lte = (LARTypeException)errorException;
	%>

	<liferay-ui:message arguments="<%= lte.getMessage() %>" key="please-import-a-lar-file-of-the-correct-type-x" />
</liferay-ui:error>

<liferay-ui:error exception="<%= LayoutImportException.class %>" message="an-unexpected-error-occurred-while-importing-your-file" />

<liferay-ui:error exception="<%= LayoutPrototypeException.class %>">

	<%
	LayoutPrototypeException lpe = (LayoutPrototypeException)errorException;
	%>

	<liferay-ui:message key="the-lar-file-could-not-be-imported-because-it-requires-page-templates-or-site-templates-that-could-not-be-found.-please-import-the-following-templates-manually" />

	<ul>

		<%
		for (Tuple missingLayoutPrototype : lpe.getMissingLayoutPrototypes()) {
			String layoutPrototypeClassName = (String)missingLayoutPrototype.getObject(0);
			String layoutPrototypeUuid = (String)missingLayoutPrototype.getObject(1);
			String layoutPrototypeName = (String)missingLayoutPrototype.getObject(2);
		%>

			<li>
				<%= ResourceActionsUtil.getModelResource(locale, layoutPrototypeClassName) %>: <strong><%= HtmlUtil.escape(layoutPrototypeName) %></strong> (<%= HtmlUtil.escape(layoutPrototypeUuid) %>)
			</li>

		<%
		}
		%>

	</ul>
</liferay-ui:error>

<liferay-ui:error exception="<%= LocaleException.class %>">

	<%
	LocaleException le = (LocaleException)errorException;
	%>

	<c:if test="<%= le.getType() == LocaleException.TYPE_EXPORT_IMPORT %>">
		<liferay-ui:message arguments="<%= new String[] {StringUtil.merge(le.getSourceAvailableLanguageIds(), StringPool.COMMA_AND_SPACE), StringUtil.merge(le.getTargetAvailableLanguageIds(), StringPool.COMMA_AND_SPACE)} %>" key="the-available-languages-in-the-lar-file-x-do-not-match-the-site's-available-languages-x" translateArguments="<%= false %>" />
	</c:if>
</liferay-ui:error>

<liferay-ui:error exception="<%= StructureDuplicateStructureKeyException.class %>">

	<%
	StructureDuplicateStructureKeyException sdske = (StructureDuplicateStructureKeyException)errorException;
	%>

	<liferay-ui:message arguments="<%= sdske.getStructureKey() %>" key="dynamic-data-mapping-structure-with-structure-key-x-already-exists" translateArguments="<%= false %>" />
</liferay-ui:error>

<portlet:actionURL name="/export_import/import_layouts" var="importPagesURL">
	<portlet:param name="mvcRenderCommandName" value="viewImport" />
	<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.IMPORT %>" />
	<portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
</portlet:actionURL>

<aui:form action="<%= importPagesURL %>" cssClass="lfr-export-dialog" method="post" name="fm1" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "publishPages();" %>'>
	<portlet:renderURL var="portletURL">
		<portlet:param name="mvcRenderCommandName" value="/export_import/view_import_layouts" />
		<portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />

	<aui:input name="<%= PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_LINK_ENABLED %>" type="hidden" value="<%= true %>" />
	<aui:input name="<%= PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL %>" type="hidden" value="<%= true %>" />
	<aui:input name="<%= PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL %>" type="hidden" value="<%= true %>" />
	<aui:input name="<%= PortletDataHandlerKeys.PORTLET_SETUP_ALL %>" type="hidden" value="<%= true %>" />
	<aui:input name="<%= PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL %>" type="hidden" value="<%= true %>" />

	<div class="export-dialog-tree">
		<div class="alert alert-warning">
			<liferay-ui:message key="import-lar-file-deletion-warning-message" />
		</div>

		<div class="alert alert-warning">
			<liferay-ui:message key="import-process-deletion-warning-message" />
		</div>

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset cssClass="options-group" label="file-summary">
					<dl class="import-file-details options">
						<dt>
							<liferay-ui:message key="name" />
						</dt>
						<dd>
							<%= HtmlUtil.escape(fileEntry.getTitle()) %>
						</dd>
						<dt>
							<liferay-ui:message key="export" />
						</dt>
						<dd>

							<%
							Date exportDate = manifestSummary.getExportDate();
							%>

							<span class="lfr-portal-tooltip" title="<%= HtmlUtil.escape(dateTimeFormat.format(exportDate)) %>">
								<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - exportDate.getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
							</span>
						</dd>
						<dt>
							<liferay-ui:message key="author" />
						</dt>
						<dd>
							<%= HtmlUtil.escape(fileEntry.getUserName()) %>
						</dd>
						<dt>
							<liferay-ui:message key="size" />
						</dt>
						<dd>
							<%= LanguageUtil.formatStorageSize(fileEntry.getSize(), locale) %>
						</dd>
					</dl>
				</aui:fieldset>

				<c:if test="<%= !stagingGroupHelper.isCompanyGroup(group) %>">
					<aui:fieldset collapsible="<%= true %>" cssClass="options-group" label="pages">
						<c:choose>
							<c:when test="<%= !group.isDepot() && !group.isCompany() && !group.isLayoutPrototype() && !group.isLayoutSetPrototype() %>">
								<c:choose>
									<c:when test="<%= group.isPrivateLayoutsEnabled() %>">
										<aui:input id="publicPages" label="public-pages" name="privateLayout" type="radio" value="<%= false %>" />

										<aui:input id="privatePages" label="private-pages" name="privateLayout" type="radio" value="<%= true %>" />
									</c:when>
									<c:otherwise>
										<aui:input name="privateLayout" type="hidden" value="<%= false %>" />
									</c:otherwise>
								</c:choose>

								<aui:input label="logo" name="<%= PortletDataHandlerKeys.LOGO %>" type="checkbox" value="<%= true %>" />

								<aui:input label="site-pages-settings" name="<%= PortletDataHandlerKeys.LAYOUT_SET_SETTINGS %>" type="checkbox" value="<%= true %>" />

								<aui:input label="site-template-settings" name="<%= PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS %>" type="checkbox" value="<%= true %>" />

								<%
								String taglibDeleteMissingLayoutsLabel = "<span style='font-weight: bold;'>" + LanguageUtil.get(request, "delete-missing-layouts") + ":</span> " + LanguageUtil.get(request, "delete-missing-layouts-help");
								%>

								<aui:input label="<%= taglibDeleteMissingLayoutsLabel %>" name="<%= PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS %>" type="checkbox" value="<%= false %>" />
							</c:when>
							<c:otherwise>
								<aui:input name="privateLayout" type="hidden" value="<%= true %>" />
							</c:otherwise>
						</c:choose>

						<%
						String taglibThemeSettingsLabel = "<span style='font-weight: bold;'>" + LanguageUtil.get(request, "theme-settings") + ":</span> " + LanguageUtil.get(request, "export-import-theme-settings-help");
						%>

						<aui:input label="<%= taglibThemeSettingsLabel %>" name="<%= PortletDataHandlerKeys.THEME_REFERENCE %>" type="checkbox" value="<%= true %>" />
					</aui:fieldset>
				</c:if>

				<%
				List<Portlet> dataPortlets = ListUtil.sort(manifestSummary.getDataPortlets(), new PortletTitleComparator(application, locale));
				%>

				<c:if test="<%= !dataPortlets.isEmpty() %>">
					<aui:fieldset collapsible="<%= true %>" cssClass="options-group" id="content">
						<aui:input name="<%= PortletDataHandlerKeys.PORTLET_DATA %>" type="hidden" value="<%= true %>" />
						<aui:input name="<%= PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT %>" type="hidden" value="<%= true %>" />

						<ul class="lfr-tree list-unstyled">
							<li class="p-0 tree-item">
								<ul class="ml-0 p-0 select-options" id="<portlet:namespace />selectContents">
									<li class="options p-0">
										<ul class="portlet-list">

											<%
											Set<String> displayedControls = new HashSet<String>();
											Set<String> portletDataHandlerNames = new HashSet<String>();

											for (Portlet portlet : dataPortlets) {
												PortletDataHandler portletDataHandler = portlet.getPortletDataHandlerInstance();

												if (!portletDataHandler.isEnabled(company.getCompanyId())) {
													continue;
												}

												String portletDataHandlerName = portletDataHandler.getName();

												if (!portletDataHandlerNames.contains(portletDataHandlerName)) {
													portletDataHandlerNames.add(portletDataHandlerName);
												}
												else {
													continue;
												}

												String portletTitle = PortalUtil.getPortletTitle(portlet, application, locale);

												long importModelCount = portletDataHandler.getExportModelCount(manifestSummary);

												long modelDeletionCount = manifestSummary.getModelDeletionCount(portletDataHandler.getDeletionSystemEventStagedModelTypes());
											%>

												<c:if test="<%= (importModelCount != 0) || (modelDeletionCount != 0) %>">
													<li class="tree-item">
														<liferay-util:buffer
															var="badgeHTML"
														>
															<span class="badge badge-info"><%= (importModelCount > 0) ? importModelCount : StringPool.BLANK %></span>
															<span class="badge badge-warning deletions"><%= (modelDeletionCount > 0) ? (modelDeletionCount + StringPool.SPACE + LanguageUtil.get(request, "deletions")) : StringPool.BLANK %></span>
														</liferay-util:buffer>

														<%
														String rootControlId = PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE + portlet.getRootPortletId();
														%>

														<aui:input checked="<%= true %>" label="<%= portletTitle + badgeHTML %>" name="<%= rootControlId %>" type="checkbox" />

														<%
														PortletDataHandlerControl[] importControls = portletDataHandler.getImportControls();
														PortletDataHandlerControl[] importMetadataControls = portletDataHandler.getImportMetadataControls();
														%>

														<ul class="hide" id="<portlet:namespace />showChangeContent_<%= portlet.getRootPortletId() %>">
															<li>
																<span class="selected-labels" id="<portlet:namespace />selectedContent_<%= portlet.getRootPortletId() %>"></span>

																<clay:button
																	cssClass="content-link modify-link pr-1"
																	data-portletid="<%= portlet.getRootPortletId() %>"
																	data-portlettitle="<%= portletTitle %>"
																	displayType="link"
																	id='<%= liferayPortletResponse.getNamespace() + "contentLink_" + portlet.getRootPortletId() %>'
																	label="change"
																/>

																<span id="<portlet:namespace />rightContentArrow_<%= portlet.getRootPortletId() %>">
																	<clay:icon
																		symbol="angle-right-small"
																	/>
																</span>
																<span class="hide" id="<portlet:namespace />downContentArrow_<%= portlet.getRootPortletId() %>">
																	<clay:icon
																		symbol="angle-down-small"
																	/>
																</span>
															</li>
														</ul>

														<c:if test="<%= ArrayUtil.isNotEmpty(importControls) || ArrayUtil.isNotEmpty(importMetadataControls) %>">
															<div class="hide" id="<portlet:namespace />content_<%= portlet.getRootPortletId() %>">
																<ul class="lfr-tree list-unstyled">
																	<li class="tree-item">
																		<aui:fieldset cssClass="portlet-type-data-section" id="<%= portletTitle %>">
																			<c:if test="<%= importControls != null %>">

																				<%
																				request.setAttribute("render_controls.jsp-action", Constants.IMPORT);
																				request.setAttribute("render_controls.jsp-childControl", false);
																				request.setAttribute("render_controls.jsp-controls", importControls);
																				request.setAttribute("render_controls.jsp-manifestSummary", manifestSummary);
																				request.setAttribute("render_controls.jsp-portletDisabled", !portletDataHandler.isPublishToLiveByDefault());
																				request.setAttribute("render_controls.jsp-portletId", portlet.getPortletId());
																				request.setAttribute("render_controls.jsp-rootControlId", rootControlId);
																				%>

																				<aui:field-wrapper label='<%= ArrayUtil.isNotEmpty(importMetadataControls) ? "content" : StringPool.BLANK %>'>
																					<ul class="lfr-tree list-unstyled">
																						<liferay-util:include page="/render_controls.jsp" servletContext="<%= application %>" />
																					</ul>
																				</aui:field-wrapper>
																			</c:if>

																			<c:if test="<%= importMetadataControls != null %>">

																				<%
																				for (PortletDataHandlerControl metadataControl : importMetadataControls) {
																					if (!displayedControls.contains(metadataControl.getControlName())) {
																						displayedControls.add(metadataControl.getControlName());
																					}
																					else {
																						continue;
																					}

																					PortletDataHandlerBoolean control = (PortletDataHandlerBoolean)metadataControl;

																					PortletDataHandlerControl[] childrenControls = control.getChildren();
																				%>

																					<c:if test="<%= ArrayUtil.isNotEmpty(childrenControls) %>">

																						<%
																						request.setAttribute("render_controls.jsp-controls", childrenControls);
																						%>

																						<aui:field-wrapper label="content-metadata">
																							<ul class="lfr-tree list-unstyled">
																								<liferay-util:include page="/render_controls.jsp" servletContext="<%= application %>" />
																							</ul>
																						</aui:field-wrapper>
																					</c:if>

																				<%
																				}
																				%>

																			</c:if>
																		</aui:fieldset>
																	</li>
																</ul>
															</div>

															<aui:script>
																Liferay.Util.toggleBoxes(
																	'<portlet:namespace /><%= PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE + portlet.getRootPortletId() %>',
																	'<portlet:namespace />showChangeContent<%= StringPool.UNDERLINE + portlet.getRootPortletId() %>'
																);
															</aui:script>
														</c:if>
													</li>
												</c:if>

											<%
											}
											%>

										</ul>

										<c:if test="<%= !stagingGroupHelper.isCompanyGroup(group) %>">
											<aui:fieldset cssClass="content-options" label="for-each-of-the-selected-content-types,-import-their">
												<span class="selected-labels" id="<portlet:namespace />selectedContentOptions"></span>

												<clay:button
													cssClass="modify-link options-link pr-1"
													displayType="link"
													id='<%= liferayPortletResponse.getNamespace() + "contentOptionsLink" %>'
													label="change"
												/>

												<span id="<portlet:namespace />rightContentOptionsArrow">
													<clay:icon
														symbol="angle-right-small"
													/>
												</span>
												<span class="hide" id="<portlet:namespace />downContentOptionsArrow">
													<clay:icon
														symbol="angle-down-small"
													/>
												</span>

												<div class="hide" id="<portlet:namespace />contentOptions">
													<ul class="lfr-tree list-unstyled">
														<li class="tree-item">
															<aui:input label="comments" name="<%= PortletDataHandlerKeys.COMMENTS %>" type="checkbox" value="<%= true %>" />

															<aui:input label="ratings" name="<%= PortletDataHandlerKeys.RATINGS %>" type="checkbox" value="<%= true %>" />
														</li>
													</ul>
												</div>
											</aui:fieldset>
										</c:if>
									</li>
								</ul>
							</li>
						</ul>
					</aui:fieldset>
				</c:if>

				<liferay-staging:deletions
					cmd="<%= Constants.IMPORT %>"
				/>

				<liferay-staging:permissions
					action="<%= Constants.IMPORT %>"
					descriptionCSSClass="permissions-description"
					global="<%= group.isCompany() %>"
					labelCSSClass="permissions-label"
				/>

				<c:choose>
					<c:when test="<%= stagingGroupHelper.isCompanyGroup(group) %>">
						<clay:sheet-section>
							<span class="sheet-subtitle" id="<portlet:namespace />updateData">
								<liferay-ui:message key="update-data" />
							</span>
							<span cssClass="mr-1">
								<strong>
									<liferay-ui:message key="mirror" />:
								</strong>
							</span>

							<liferay-ui:message key="import-data-strategy-mirror-help" />
						</clay:sheet-section>
					</c:when>
					<c:otherwise>
						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" cssClass="options-group" label="update-data">
							<c:if test="<%= !stagingGroupHelper.isCompanyGroup(group) %>">
								<clay:alert
									cssClass="hide"
									displayType="warning"
									id='<%= liferayPortletResponse.getNamespace() + "updateDataAlert" %>'
									message="objects-entries-are-always-mirrored-regardless-of-the-selection"
									title="update-data"
								/>
							</c:if>

							<%
							String taglibMirrorLabel = LanguageUtil.get(request, "mirror") + ": <span style='font-weight: normal'>" + LanguageUtil.get(request, "import-data-strategy-mirror-help") + "</span>";
							%>

							<aui:input checked="<%= true %>" id="mirror" label="<%= taglibMirrorLabel %>" name="<%= PortletDataHandlerKeys.DATA_STRATEGY %>" type="radio" value="<%= PortletDataHandlerKeys.DATA_STRATEGY_MIRROR %>" />

							<%
							String taglibMirrorWithOverwritingLabel = LanguageUtil.get(request, "mirror-with-overwriting") + ": <span style='font-weight: normal'>" + LanguageUtil.get(request, "import-data-strategy-mirror-with-overwriting-help") + "</span>";
							%>

							<aui:input id="mirrorWithOverwriting" label="<%= taglibMirrorWithOverwritingLabel %>" name="<%= PortletDataHandlerKeys.DATA_STRATEGY %>" type="radio" value="<%= PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE %>" />

							<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-44307") %>'>

								<%
								String taglibCopyAsNewLabel = LanguageUtil.get(request, "copy-as-new") + ": <span style='font-weight: normal'>" + LanguageUtil.get(request, "import-data-strategy-copy-as-new-help") + "</span>";
								%>

								<aui:input id="copyAsNew" label="<%= taglibCopyAsNewLabel %>" name="<%= PortletDataHandlerKeys.DATA_STRATEGY %>" type="radio" value="<%= PortletDataHandlerKeys.DATA_STRATEGY_COPY_AS_NEW %>" />
							</c:if>
						</aui:fieldset>
					</c:otherwise>
				</c:choose>

				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" cssClass="options-group" label="authorship-of-the-content">

					<%
					String taglibUseTheOriginalAuthorLabel = LanguageUtil.get(request, "use-the-original-author") + ": <span style='font-weight: normal'>" + LanguageUtil.get(request, "use-the-original-author-help") + "</span>";
					%>

					<aui:input checked="<%= true %>" id="currentUserId" label="<%= taglibUseTheOriginalAuthorLabel %>" name="<%= PortletDataHandlerKeys.USER_ID_STRATEGY %>" type="radio" value="<%= UserIdStrategy.CURRENT_USER_ID %>" />

					<%
					String taglibUseTheCurrentUserAsAuthorLabel = LanguageUtil.get(request, "use-the-current-user-as-author") + ": <span style='font-weight: normal'>" + LanguageUtil.get(request, "use-the-current-user-as-author-help") + "</span>";
					%>

					<aui:input id="alwaysCurrentUserId" label="<%= taglibUseTheCurrentUserAsAuthorLabel %>" name="<%= PortletDataHandlerKeys.USER_ID_STRATEGY %>" type="radio" value="<%= UserIdStrategy.ALWAYS_CURRENT_USER_ID %>" />
				</aui:fieldset>
			</div>
		</div>
	</div>

	<aui:button-row>
		<portlet:renderURL var="backURL">
			<portlet:param name="mvcRenderCommandName" value="/export_import/import_layouts" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.VALIDATE %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
		</portlet:renderURL>

		<aui:button href="<%= backURL %>" name="back" value="back" />

		<c:choose>
			<c:when test="<%= stagingGroupHelper.isCompanyGroup(group) %>">
				<aui:button type="submit" value="import" />
			</c:when>
			<c:otherwise>
				<div class="d-inline-block">
					<react:component
						module="{ImportButton} from exportimport-web"
						props='<%=
							HashMapBuilder.<String, Object>put(
								"copyAsNewCheckboxId", liferayPortletResponse.getNamespace() + "copyAsNew"
							).put(
								"deletePortletDataBeforeImportingCheckboxId", liferayPortletResponse.getNamespace() + PortletDataHandlerKeys.DELETE_PORTLET_DATA
							).put(
								"handleSubmitFnName", liferayPortletResponse.getNamespace() + "publishPages"
							).put(
								"isAnyObjectEntrySelectedFnName", liferayPortletResponse.getNamespace() + "isAnyObjectEntrySelected"
							).put(
								"mirrorWithOverwritingCheckboxId", liferayPortletResponse.getNamespace() + "mirrorWithOverwriting"
							).build()
						%>'
					/>
				</div>
			</c:otherwise>
		</c:choose>
	</aui:button-row>
</aui:form>

<aui:script>
	function <portlet:namespace />isAnyObjectEntrySelected() {
		return Array.from(
			document.querySelectorAll(
				'#<portlet:namespace />selectContents input[type="checkbox"][name*="object_definitions"]'
			)
		).some((checkbox) => checkbox.checked);
	}

	function <portlet:namespace />publishPages() {
		var deletePortletDataBeforeImportingCheckbox = document.getElementById(
			'<portlet:namespace /><%= PortletDataHandlerKeys.DELETE_PORTLET_DATA %>'
		);

		var form = document.<portlet:namespace />fm1;

		if (
			deletePortletDataBeforeImportingCheckbox &&
			deletePortletDataBeforeImportingCheckbox.checked
		) {
			Liferay.Util.openConfirmModal({
				message:
					'<%= UnicodeLanguageUtil.get(request, "delete-application-data-before-importing-confirmation") %>',
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						submitForm(form);
					}
				},
			});
		}
		else {
			submitForm(form);
		}
	}

	Liferay.Util.toggleRadio('<portlet:namespace />allApplications', '', [
		'<portlet:namespace />selectApplications',
	]);
	Liferay.Util.toggleRadio(
		'<portlet:namespace />chooseApplications',
		'<portlet:namespace />selectApplications',
		''
	);
</aui:script>

<aui:script sandbox="<%= true %>">
	var showAlertsHandler = Liferay.Util.delegate(
		document.querySelector('#<portlet:namespace />exportImportOptions'),
		'change',
		'input[type="checkbox"][name*="object_definitions"],' +
			'#<portlet:namespace /><%= PortletDataHandlerKeys.DELETE_PORTLET_DATA %>,' +
			'input[name=<portlet:namespace /><%= PortletDataHandlerKeys.DATA_STRATEGY %>]',
		(event) => {
			var deletePortletDataAlert = document.getElementById(
				'<portlet:namespace />deletePortletDataAlert'
			);
			var updateDataAlert = document.getElementById(
				'<portlet:namespace />updateDataAlert'
			);
			var deletePortletDataInput = document.getElementById(
				'<portlet:namespace /><%= PortletDataHandlerKeys.DELETE_PORTLET_DATA %>'
			);
			var updateDataInput = document.querySelector(
				'input[name=<portlet:namespace /><%= PortletDataHandlerKeys.DATA_STRATEGY %>]:checked'
			);
			var isAnyObjectEntrySelected =
				<portlet:namespace />isAnyObjectEntrySelected();

			if (deletePortletDataAlert && deletePortletDataInput) {
				var showDeletePortletDataAlert =
					isAnyObjectEntrySelected && deletePortletDataInput.checked;
				deletePortletDataAlert.classList.toggle(
					'hide',
					!showDeletePortletDataAlert
				);
			}

			if (updateDataAlert && updateDataInput) {
				var showUpdateDataAlert =
					isAnyObjectEntrySelected &&
					(updateDataInput.value ===
						'<%= PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE %>' ||
						updateDataInput.value ===
							'<%= PortletDataHandlerKeys.DATA_STRATEGY_COPY_AS_NEW %>');
				updateDataAlert.classList.toggle('hide', !showUpdateDataAlert);
			}
		}
	);

	Liferay.on('destroyPortlet', function removeListener() {
		showAlertsHandler.dispose();

		Liferay.detach('destroyPortlet', removeListener);
	});
</aui:script>

<aui:script use="liferay-export-import-export-import">
	new Liferay.ExportImport({
		archivedSetupsNode:
			'#<%= PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL %>',
		commentsNode: '#<%= PortletDataHandlerKeys.COMMENTS %>',
		deleteMissingLayoutsNode:
			'#<%= PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS %>',
		deletionsNode: '#<%= PortletDataHandlerKeys.DELETIONS %>',
		form: document.<portlet:namespace />fm1,
		layoutSetSettingsNode: '#<%= PortletDataHandlerKeys.LAYOUT_SET_SETTINGS %>',
		locale: '<%= locale.toLanguageTag() %>',
		logoNode: '#<%= PortletDataHandlerKeys.LOGO %>',
		namespace: '<portlet:namespace />',
		ratingsNode: '#<%= PortletDataHandlerKeys.RATINGS %>',
		setupNode: '#<%= PortletDataHandlerKeys.PORTLET_SETUP_ALL %>',
		themeReferenceNode: '#<%= PortletDataHandlerKeys.THEME_REFERENCE %>',
		timeZoneOffset: <%= timeZoneOffset %>,
		userPreferencesNode:
			'#<%= PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL %>',
	});
</aui:script>