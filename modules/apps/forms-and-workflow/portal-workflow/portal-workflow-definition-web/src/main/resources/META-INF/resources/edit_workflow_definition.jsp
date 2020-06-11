<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

WorkflowDefinition workflowDefinition = (WorkflowDefinition)request.getAttribute(WebKeys.WORKFLOW_DEFINITION);

String name = StringPool.BLANK;
String version = StringPool.BLANK;

if (workflowDefinition != null) {
	name = workflowDefinition.getName();
	version = String.valueOf(workflowDefinition.getVersion());
}

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("mvcPath", "/view.jsp");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle((workflowDefinition == null) ? LanguageUtil.get(request, (workflowDefinition == null) ? "upload-definition" : workflowDefinition.getName()) : workflowDefinition.getName());
%>

<liferay-portlet:actionURL name='<%= (workflowDefinition == null) ? "addWorkflowDefinition" : "updateWorkflowDefinition" %>' var="editWorkflowDefinitionURL">
	<portlet:param name="mvcPath" value="/view.jsp" />
</liferay-portlet:actionURL>

<div class="container-fluid-1280 workflow-definition-container">
	<aui:form action="<%= editWorkflowDefinitionURL %>" enctype="multipart/form-data" method="post">
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="name" type="hidden" value="<%= name %>" />
		<aui:input name="version" type="hidden" value="<%= version %>" />

		<div class="card-horizontal">
			<div class="card-row card-row-padded">
				<liferay-ui:error exception="<%= WorkflowDefinitionFileException.class %>" message="please-enter-a-valid-file" />

				<aui:fieldset>
					<div class="col-xs-6">
						<aui:field-wrapper label="title">
							<liferay-ui:input-localized
								cssClass="form-control"
								name="title"
								xml='<%= BeanPropertiesUtil.getString(workflowDefinition, "title") %>'
							/>
						</aui:field-wrapper>

						<aui:field-wrapper label="file">
							<div class="lfr-dynamic-uploader">
								<div class="lfr-upload-container" id="<portlet:namespace />fileUpload"></div>

								<aui:input name="tempFileName" type="hidden" />
							</div>
						</aui:field-wrapper>
					</div>
				</aui:fieldset>
			</div>
		</div>

		<aui:button-row>
			<c:if test="<%= workflowDefinitionDisplayContext.canPublishWorkflowDefinition() %>">
				<aui:button cssClass="btn-lg" type="submit" />
			</c:if>

			<aui:button cssClass="btn-lg" href="<%= redirect %>" type="cancel" />
		</aui:button-row>
	</aui:form>
</div>

<%
Date expirationDate = new Date(System.currentTimeMillis() + PropsValues.SESSION_TIMEOUT * Time.MINUTE);

Ticket ticket = TicketLocalServiceUtil.addTicket(user.getCompanyId(), User.class.getName(), user.getUserId(), TicketConstants.TYPE_IMPERSONATE, null, expirationDate, new ServiceContext());
%>

<aui:script use="liferay-upload">
	var tempFileNameInput = A.one('#<portlet:namespace />tempFileName');

	new Liferay.Upload(
		{
			after: {
				render: function() {
					var instance = this;

					var boundingBox = this.get('boundingBox');

					boundingBox.one('.select-files-container').placeBefore(boundingBox.one('.upload-list'));
				},
				tempFileRemoved: function() {
					tempFileNameInput.val('');
				},
				uploadComplete: function(file) {
					tempFileNameInput.val(file.name);
				}
			},
			boundingBox: '#<portlet:namespace />fileUpload',

			<%
			DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);
			%>

			decimalSeparator: '<%= decimalFormatSymbols.getDecimalSeparator() %>',
			deleteFile: '<liferay-portlet:actionURL doAsUserId="<%= user.getUserId() %>" name="deleteWorkflowDefinitionFile"><portlet:param name="mvcPath" value="/edit_workflow_definition.jsp" /></liferay-portlet:actionURL>&ticketKey=<%= ticket.getKey() %>',
			fileDescription: '<%= StringUtil.merge(PrefsPropsUtil.getStringArray(PropsKeys.DL_FILE_EXTENSIONS, StringPool.COMMA)) %>',
			maxFileSize: '<%= PrefsPropsUtil.getLong(PropsKeys.DL_FILE_MAX_SIZE) %> B',
			multipleFiles: false,
			namespace: '<portlet:namespace />',
			restoreState: false,
			tempFileURL: {
				method: Liferay.Service.bind('/dlapp/get-temp-file-names'),
				params: {
					folderId: '0',
					folderName: '<%= UploadWorkflowDefinitionFileMVCActionCommand.TEMP_FOLDER_NAME %>',
					groupId: <%= scopeGroupId %>
				}
			},
			tempRandomSuffix: '<%= TempFileEntryUtil.TEMP_RANDOM_SUFFIX %>',
			uploadFile: '<liferay-portlet:actionURL doAsUserId="<%= user.getUserId() %>" name="uploadWorkflowDefinitionFile"><portlet:param name="mvcPath" value="/edit_workflow_definition.jsp" /></liferay-portlet:actionURL>&ticketKey=<%= ticket.getKey() %>'
		}
	);
</aui:script>