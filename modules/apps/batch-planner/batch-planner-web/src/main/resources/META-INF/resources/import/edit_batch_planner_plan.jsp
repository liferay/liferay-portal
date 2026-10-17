<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long batchPlannerPlanId = ParamUtil.getLong(renderRequest, "batchPlannerPlanId");

boolean editable = ParamUtil.getBoolean(renderRequest, "editable");

EditBatchPlannerPlanDisplayContext editBatchPlannerPlanDisplayContext = (EditBatchPlannerPlanDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", String.valueOf(renderResponse.createRenderURL())));

renderResponse.setTitle(editable ? LanguageUtil.get(request, "edit-template") : LanguageUtil.get(request, "import"));
%>

<clay:container
	cssClass="container pt-4"
>
	<form id="<portlet:namespace />fm" name="<portlet:namespace />fm">
		<input id="<portlet:namespace />batchPlannerPlanId" name="<portlet:namespace />batchPlannerPlanId" type="hidden" value="<%= batchPlannerPlanId %>" />
		<input id="<portlet:namespace />externalType" name="<portlet:namespace />externalType" type="hidden" value="" />

		<div class="row">
			<div class="col-lg-6 d-flex flex-column">
				<div class="card flex-fill">
					<div class="card-header h4"><liferay-ui:message key="import-settings" /></div>

					<div class="card-body">
						<liferay-frontend:edit-form-body>
							<aui:input name="name" />

							<clay:row>
								<clay:col
									md="6"
								>
									<div id="<portlet:namespace />templateSelect"></div>
								</clay:col>

								<clay:col
									md="6"
								>
									<react:component
										module="{ImportEntityType} from batch-planner-web"
										props='<%=
											HashMapBuilder.<String, Object>put(
												"__reactDOMFlushSync", true
											).put(
												"internalClassNameKeyId", liferayPortletResponse.getNamespace() + "internalClassNameKey"
											).put(
												"internalClassNameKeyInitialOptions", editBatchPlannerPlanDisplayContext.getInternalClassNameKeySelectOptions()
											).put(
												"internalClassNameKeyLabel", LanguageUtil.get(request, "entity-type")
											).put(
												"internalClassNameKeyName", liferayPortletResponse.getNamespace() + "internalClassNameKey"
											).build()
										%>'
									/>
								</clay:col>
							</clay:row>

							<clay:row>
								<clay:col>
									<react:component
										module="{Scope} from batch-planner-web"
										props='<%=
											HashMapBuilder.<String, Object>put(
												"__reactDOMFlushSync", true
											).build()
										%>'
									/>
								</clay:col>
							</clay:row>

							<clay:alert
								cssClass="hide"
								displayType="info"
								id='<%= liferayPortletResponse.getNamespace() + "downloadTemplateAlert" %>'
								title="download-a-sample-file-for-this-entity"
								__reactDOMFlushSync="<%= true %>"
							>
								<clay:link
									cssClass="link-primary single-link"
									href="#"
									id='<%= liferayPortletResponse.getNamespace() + "downloadBatchPlannerPlanTemplate" %>'
									label="download"
									__reactDOMFlushSync="<%= true %>"
								/>

								<liferay-frontend:component
									context='<%=
										HashMapBuilder.<String, Object>put(
											"HTMLElementId", liferayPortletResponse.getNamespace() + "downloadBatchPlannerPlanTemplate"
										).put(
											"type", "batchPlannerTemplate"
										).build()
									%>'
									module="{DownloadHelper} from batch-planner-web"
								/>
							</clay:alert>

							<div class="d-none mt-2">
								<clay:checkbox
									checked="<%= false %>"
									disabled="<%= true %>"
									id='<%= liferayPortletResponse.getNamespace() + "detectCategoryNames" %>'
									label='<%= LanguageUtil.get(request, "detect-category-names-from-CSV-file") %>'
									name='<%= liferayPortletResponse.getNamespace() + "detectCategoryNames" %>'
									__reactDOMFlushSync="<%= true %>"
								/>
							</div>

							<div class="mt-2">
								<clay:checkbox
									checked="<%= true %>"
									id='<%= liferayPortletResponse.getNamespace() + "onErrorFail" %>'
									label='<%= LanguageUtil.get(request, "stop-the-import-on-error") %>'
									name='<%= liferayPortletResponse.getNamespace() + "onErrorFail" %>'
									__reactDOMFlushSync="<%= true %>"
								/>
							</div>

							<clay:row>
								<clay:col>
									<react:component
										module="{Strategies} from batch-planner-web"
										props='<%=
											HashMapBuilder.<String, Object>put(
												"__reactDOMFlushSync", true
											).build()
										%>'
									/>
								</clay:col>
							</clay:row>
						</liferay-frontend:edit-form-body>
					</div>
				</div>
			</div>

			<div class="col-lg-6 d-flex flex-column">
				<div class="card flex-fill">
					<div class="card-header h4"><liferay-ui:message key="file-settings" /></div>

					<div class="card-body">
						<liferay-frontend:edit-form-body>
							<div id="<portlet:namespace />fileSettings"></div>

							<div class="form-group">
								<clay:radio
									checked="<%= true %>"
									label='<%= LanguageUtil.get(request, "upload-a-file-from-my-computer") %>'
									name="selectFile"
									value="computer"
								/>

								<clay:radio
									disabled="<%= true %>"
									label='<%= LanguageUtil.get(request, "use-a-file-already-on-the-server") %>'
									name="selectFile"
									value="server"
								/>
							</div>

							<div id="<portlet:namespace />fileUpload">
								<react:component
									module="{FileUpload} from batch-planner-web"
									props='<%=
										HashMapBuilder.<String, Object>put(
											"__reactDOMFlushSync", true
										).build()
									%>'
								/>
							</div>
						</liferay-frontend:edit-form-body>
					</div>
				</div>
			</div>
		</div>

		<span>
			<react:component
				module="{ImportForm} from batch-planner-web"
				props='<%=
					HashMapBuilder.<String, Object>put(
						"__reactDOMFlushSync", true
					).put(
						"formDataQuerySelector", "#" + liferayPortletResponse.getNamespace() + "fm"
					).put(
						"formImportURL",
						ResourceURLBuilder.createResourceURL(
							renderResponse
						).setCMD(
							Constants.IMPORT
						).setResourceID(
							"/batch_planner/submit_batch_planner_plan"
						).buildString()
					).put(
						"formSaveAsTemplateURL",
						ActionURLBuilder.createActionURL(
							renderResponse
						).setActionName(
							"/batch_planner/edit_import_batch_planner_plan_template"
						).setCMD(
							Constants.ADD
						).setParameter(
							"template", true
						).buildString()
					).put(
						"mappedFields", editBatchPlannerPlanDisplayContext.getSelectedBatchPlannerPlanMappings()
					).build()
				%>'
			/>
		</span>
	</form>
</clay:container>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"initialTemplateClassName", editBatchPlannerPlanDisplayContext.getSelectedInternalClassNameKey()
		).put(
			"initialTemplateMapping", editBatchPlannerPlanDisplayContext.getSelectedBatchPlannerPlanMappings()
		).put(
			"isExport", false
		).put(
			"templatesOptions", editBatchPlannerPlanDisplayContext.getTemplateSelectOptions()
		).build()
	%>'
	module="{editBatchPlannerPlan} from batch-planner-web"
/>

<liferay-frontend:component
	module="{showUploadInput} from batch-planner-web"
/>