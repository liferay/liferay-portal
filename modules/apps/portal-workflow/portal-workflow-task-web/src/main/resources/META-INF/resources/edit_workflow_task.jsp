<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String randomId = StringUtil.randomId();

String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL", redirect);

if (Validator.isNull(backURL)) {
	backURL = request.getHeader(WebKeys.REFERER);
}

String languageId = LanguageUtil.getLanguageId(request);

WorkflowTask workflowTask = workflowTaskDisplayContext.getWorkflowTask();

WorkflowHandler<?> workflowHandler = workflowTaskDisplayContext.getWorkflowHandler(workflowTask);

long classPK = workflowTaskDisplayContext.getWorkflowContextEntryClassPK(workflowHandler, workflowTask);

AssetRenderer<?> assetRenderer = workflowHandler.getAssetRenderer(classPK);

AssetRendererFactory<?> assetRendererFactory = null;

AssetEntry assetEntry = null;

if (assetRenderer != null) {
	assetRendererFactory = assetRenderer.getAssetRendererFactory();

	if (assetRendererFactory != null) {
		assetEntry = assetRendererFactory.getAssetEntry(workflowHandler.getClassName(), assetRenderer.getClassPK());
	}

	String[] availableLanguageIds = assetRenderer.getAvailableLanguageIds();

	if (ArrayUtil.isNotEmpty(availableLanguageIds) && !ArrayUtil.contains(availableLanguageIds, languageId)) {
		languageId = assetRenderer.getDefaultLanguageId();
	}
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);

renderResponse.setTitle(workflowTaskDisplayContext.getHeaderTitle(workflowTask));
%>

<clay:container-fluid>
	<clay:col
		cssClass="lfr-asset-column lfr-asset-column-details"
	>
		<liferay-ui:success key='<%= workflowTaskDisplayContext.getPortletResource() + "requestProcessed" %>' message="your-request-completed-successfully" />

		<liferay-ui:error exception="<%= WorkflowTaskDueDateException.class %>" message="please-enter-a-valid-due-date" />

		<clay:sheet
			size="full"
		>
			<clay:sheet-section>

				<%
				request.removeAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
				%>

				<c:if test="<%= !workflowTaskDisplayContext.isReadOnly() %>">
					<liferay-util:include page="/workflow_task_action.jsp" servletContext="<%= application %>">
						<liferay-util:param name="mvcPath" value="/edit_workflow_task.jsp" />
					</liferay-util:include>
				</c:if>

				<clay:col
					md="6"
				>
					<aui:field-wrapper label="assigned-to">
						<aui:fieldset>
							<div class="align-items-center card-row">
								<c:choose>
									<c:when test="<%= workflowTask.isAssignedToSingleUser() %>">
										<div class="card-col-field mr-2">
											<div class="list-group-card-icon">
												<liferay-user:user-portrait
													userId="<%= workflowTask.getAssigneeUserId() %>"
												/>
											</div>
										</div>

										<div class="card-col-content card-col-gutters">
											<div class="lfr-asset-assigned">
												<%= HtmlUtil.escape(workflowTaskDisplayContext.getWorkflowTaskAssigneeUserName(workflowTask)) %>
											</div>
										</div>
									</c:when>
									<c:otherwise>
										<div class="card-col-content card-col-gutters lfr-asset-assigned">
											<div class="lfr-asset-assigned">
												<%= workflowTaskDisplayContext.getWorkflowTaskUnassignedUserName() %>
											</div>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</aui:fieldset>
					</aui:field-wrapper>

					<aui:field-wrapper label="task-name">
						<aui:fieldset>
							<%= workflowTask.getLabel(workflowTaskDisplayContext.getTaskContentLocale()) %>
						</aui:fieldset>
					</aui:field-wrapper>
				</clay:col>

				<clay:col
					md="6"
				>
					<aui:field-wrapper label="create-date">
						<aui:fieldset>
							<%= workflowTaskDisplayContext.getCreateDateString(workflowTask) %>
						</aui:fieldset>
					</aui:field-wrapper>

					<aui:field-wrapper label="due-date">
						<aui:fieldset>
							<%= workflowTaskDisplayContext.getDueDateString(workflowTask) %>
						</aui:fieldset>
					</aui:field-wrapper>
				</clay:col>

				<c:if test="<%= Validator.isNotNull(workflowTask.getDescription()) %>">
					<clay:col>
						<aui:field-wrapper label="description">
							<aui:fieldset>
								<%= workflowTaskDisplayContext.getDescription(workflowTask) %>
							</aui:fieldset>
						</aui:field-wrapper>
					</clay:col>
				</c:if>
			</clay:sheet-section>

			<clay:panel-group>
				<c:if test="<%= assetRenderer != null %>">
					<clay:panel
						displayTitle="<%= workflowTaskDisplayContext.getPreviewOfTitle(workflowTask) %>"
						expanded="<%= true %>"
					>
						<div class="panel-body">
							<c:if test="<%= assetRenderer.isLocalizable() %>">
								<div class="locale-actions">
									<liferay-site-navigation:language
										formAction="<%= currentURL %>"
										languageId="<%= languageId %>"
										languageIds="<%= assetRenderer.getAvailableLanguageIds() %>"
									/>
								</div>
							</c:if>

							<div class="task-content-actions">
								<c:if test="<%= assetRenderer.hasViewPermission(permissionChecker) %>">
									<portlet:renderURL var="viewFullContentURL">
										<portlet:param name="mvcPath" value="/view_content.jsp" />
										<portlet:param name="redirect" value="<%= currentURL %>" />
										<portlet:param name="languageId" value="<%= languageId %>" />

										<c:if test="<%= assetEntry != null %>">
											<portlet:param name="assetEntryId" value="<%= String.valueOf(assetEntry.getEntryId()) %>" />
											<portlet:param name="assetEntryClassPK" value="<%= String.valueOf(assetEntry.getClassPK()) %>" />
										</c:if>

										<c:if test="<%= assetRendererFactory != null %>">
											<portlet:param name="type" value="<%= assetRendererFactory.getType() %>" />
										</c:if>

										<portlet:param name="showEditURL" value="<%= String.valueOf(workflowTaskDisplayContext.isShowEditURL(workflowTask)) %>" />
										<portlet:param name="workflowTaskId" value="<%= String.valueOf(workflowTask.getWorkflowTaskId()) %>" />
									</portlet:renderURL>

									<c:if test="<%= !workflowTaskDisplayContext.isReadOnly() %>">

										<%
										String viewTaskContentAriaLabel = LanguageUtil.get(request, "view[action]");
										%>

										<span class="lfr-portal-tooltip" title="<%= viewTaskContentAriaLabel %>">
											<clay:link
												aria-label="<%= viewTaskContentAriaLabel %>"
												cssClass="btn btn-monospaced btn-outline-secondary lfr-icon-item taglib-icon"
												href="<%= assetRenderer.isPreviewInContext() ? workflowHandler.getURLViewInContext(assetRenderer.getClassPK(), liferayPortletRequest, liferayPortletResponse, null) : viewFullContentURL.toString() %>"
												icon="view"
												id='<%= liferayPortletResponse.getNamespace() + "view" %>'
												target="_blank"
												title='<%= LanguageUtil.get(request, "view") %>'
											/>
										</span>

										<c:if test="<%= workflowTaskDisplayContext.hasViewDiffsPortletURL(workflowTask) %>">

											<%
											String diffsTaskContentsAriaLabel = LanguageUtil.get(request, "diffs");
											%>

											<span class="lfr-portal-tooltip" title="<%= diffsTaskContentsAriaLabel %>">
												<clay:link
													aria-label="<%= diffsTaskContentsAriaLabel %>"
													cssClass="btn btn-monospaced btn-outline-secondary lfr-icon-item taglib-icon"
													href="<%= workflowTaskDisplayContext.getTaglibViewDiffsURL(workflowTask) %>"
													icon="paste"
													id='<%= liferayPortletResponse.getNamespace() + "diffs" %>'
												/>
											</span>
										</c:if>

										<c:if test="<%= assetEntry != null %>">
											<portlet:renderURL var="viewLayoutClassedModelUsagesURL">
												<portlet:param name="mvcPath" value="/view_layout_classed_model_usages.jsp" />
												<portlet:param name="redirect" value="<%= currentURL %>" />
												<portlet:param name="className" value="<%= assetEntry.getClassName() %>" />
												<portlet:param name="classPK" value="<%= String.valueOf(assetEntry.getClassPK()) %>" />
												<portlet:param name="workflowTaskId" value="<%= String.valueOf(workflowTask.getWorkflowTaskId()) %>" />
											</portlet:renderURL>

											<%
											String viewUsagesTaskContentAriaLabel = LanguageUtil.get(request, "view-usages");
											%>

											<span class="lfr-portal-tooltip" title="<%= viewUsagesTaskContentAriaLabel %>">
												<clay:link
													aria-label="<%= viewUsagesTaskContentAriaLabel %>"
													cssClass="btn btn-monospaced btn-outline-secondary lfr-icon-item taglib-icon"
													href="<%= viewLayoutClassedModelUsagesURL %>"
													icon="list"
													id='<%= liferayPortletResponse.getNamespace() + "viewUsages" %>'
												/>
											</span>
										</c:if>
									</c:if>
								</c:if>

								<c:if test="<%= workflowTaskDisplayContext.hasEditPortletURL(workflowTask) %>">
									<c:choose>
										<c:when test="<%= assetRenderer.hasEditPermission(permissionChecker) && workflowTaskDisplayContext.isShowEditURL(workflowTask) %>">

											<%
											String editTaskContentAriaLabel = LanguageUtil.get(request, "edit");
											%>

											<span class="lfr-portal-tooltip" title="<%= editTaskContentAriaLabel %>">
												<clay:link
													aria-label="<%= editTaskContentAriaLabel %>"
													cssClass="btn btn-monospaced btn-outline-secondary lfr-icon-item taglib-icon"
													href="<%= workflowTaskDisplayContext.getTaglibEditURL(workflowTask) %>"
													icon="pencil"
													id='<%= liferayPortletResponse.getNamespace() + "edit" %>'
												/>
											</span>
										</c:when>
										<c:when test="<%= assetRenderer.hasEditPermission(permissionChecker) && !workflowTaskDisplayContext.isShowEditURL(workflowTask) && !workflowTask.isCompleted() %>">
											<span class="lfr-portal-tooltip" title="<%= LanguageUtil.get(request, "please-assign-the-task-to-yourself-to-be-able-to-edit-the-content") %>">
												<span class="btn btn-monospaced btn-outline-secondary">
													<clay:icon
														symbol="question-circle-full"
													/>
												</span>
											</span>
										</c:when>
									</c:choose>
								</c:if>
							</div>

							<span class="h3 task-content-title">
								<span>
									<clay:icon
										symbol="<%= workflowHandler.getIconCssClass() %>"
									/>

									<span class="taglib-text">
										<%= HtmlUtil.escape(workflowTaskDisplayContext.getAssetTitle(workflowTask)) %>
									</span>
								</span>
							</span>

							<liferay-asset:asset-display
								assetRenderer="<%= assetRenderer %>"
								template="<%= AssetRenderer.TEMPLATE_ABSTRACT %>"
							/>

							<c:if test="<%= assetEntry != null %>">
								<span class="h4 task-content-author">
									<liferay-ui:message key="author" />
								</span>

								<liferay-asset:asset-metadata
									className="<%= assetEntry.getClassName() %>"
									classPK="<%= assetEntry.getClassPK() %>"
									metadataFields='<%= new String[] {"author", "categories", "tags"} %>'
								/>
							</c:if>
						</div>
					</clay:panel>

					<c:if test="<%= (assetEntry != null) && workflowHandler.isCommentable() %>">

						<%
						long discussionClassPK = workflowHandler.getDiscussionClassPK(workflowTask.getOptionalAttributes());
						%>

						<clay:panel
							displayTitle='<%= LanguageUtil.get(request, "comments") %>'
						>
							<div class="panel-body">
								<liferay-comment:discussion
									assetEntryVisible="<%= false %>"
									className="<%= assetRenderer.getClassName() %>"
									classPK="<%= discussionClassPK %>"
									formName='<%= "fm" + discussionClassPK %>'
									ratingsEnabled="<%= false %>"
									redirect="<%= currentURL %>"
									refreshPageOnReply="<%= true %>"
									userId="<%= user.getUserId() %>"
								/>
							</div>
						</clay:panel>
					</c:if>
				</c:if>

				<clay:panel
					displayTitle='<%= LanguageUtil.get(request, "activities") %>'
				>
					<div class="panel-body">
						<%@ include file="/workflow_logs.jspf" %>
					</div>
				</clay:panel>
			</clay:panel-group>
		</clay:sheet>
	</clay:col>
</clay:container-fluid>