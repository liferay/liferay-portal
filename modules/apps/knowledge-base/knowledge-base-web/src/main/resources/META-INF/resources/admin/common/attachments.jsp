<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/common/init.jsp" %>

<%
KBArticle kbArticle = (KBArticle)request.getAttribute(KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLE);

resourcePrimKey = BeanParamUtil.getLong(kbArticle, request, "resourcePrimKey");

List<FileEntry> attachmentsFileEntries = new ArrayList<FileEntry>();

if (kbArticle != null) {
	attachmentsFileEntries = kbArticle.getAttachmentsFileEntries();
}
%>

<div class="kb-attachments">
	<aui:input name="removeFileEntryIds" type="hidden" />

	<div class="lfr-dynamic-uploader">
		<div class="lfr-upload-container" id="<portlet:namespace />fileUpload"></div>
	</div>

	<span id="<portlet:namespace />selectedFileNameContainer"></span>

	<div class="hide" id="<portlet:namespace />metadataExplanationContainer"></div>

	<div class="hide selected" id="<portlet:namespace />selectedFileNameMetadataContainer"></div>

	<c:if test="<%= !attachmentsFileEntries.isEmpty() %>">
		<div class="h4"><liferay-ui:message key="saved-attachments" /></div>

		<div id="<portlet:namespace />existingAttachmentsContainer">

			<%
			for (FileEntry fileEntry : attachmentsFileEntries) {
			%>

				<div id="<portlet:namespace />fileEntryIdWrapper<%= fileEntry.getFileEntryId() %>">
					<liferay-ui:icon
						icon="paperclip"
						label="<%= true %>"
						markupView="lexicon"
						message='<%= HtmlUtil.escape(fileEntry.getTitle()) + " (" + LanguageUtil.formatStorageSize(fileEntry.getSize(), locale) + ")" %>'
						method="get"
						url='<%= PortletFileRepositoryUtil.getDownloadPortletFileEntryURL(themeDisplay, fileEntry, "status=" + WorkflowConstants.STATUS_APPROVED) %>'
					/>

					<%
					String taglibURL = "javascript:" + liferayPortletResponse.getNamespace() + "deleteFileEntry('" + fileEntry.getFileEntryId() + "');";
					%>

					<liferay-ui:icon-delete
						icon="trash"
						showIcon="<%= true %>"
						url="<%= taglibURL %>"
					/>
				</div>

			<%
			}
			%>

		</div>
	</c:if>
</div>

<aui:script use="liferay-upload">
	new Liferay.Upload({
		'boundingBox': '#<portlet:namespace />fileUpload',
		'deleteFile':
			'<liferay-portlet:actionURL name="/knowledge_base/delete_temp_attachment"><portlet:param name="resourcePrimKey" value="<%= String.valueOf(resourcePrimKey) %>" /></liferay-portlet:actionURL>',

		<%
		DLConfiguration dlConfiguration = ConfigurationProviderUtil.getSystemConfiguration(DLConfiguration.class);
		%>

		'fileDescription':
			'<%= StringUtil.merge(dlConfiguration.fileExtensions()) %>',
		'maxFileSize':
			'<%= DLValidatorUtil.getMaxAllowableSize(themeDisplay.getScopeGroupId(), null) %> B',
		'metadataContainer':
			'#<portlet:namespace />selectedFileNameMetadataContainer',
		'metadataExplanationContainer':
			'#<portlet:namespace />metadataExplanationContainer',
		'namespace': '<portlet:namespace />',
		'tempFileURL': {
			method: Liferay.Service.bind('/kb.kbarticle/get-temp-attachment-names'),
			params: {
				groupId: <%= scopeGroupId %>,
				tempFolderName: '<%= KBWebKeys.TEMP_FOLDER_NAME %>',
			},
		},
		'strings.dropFilesText':
			'<liferay-ui:message key="drag-and-drop-to-upload" />',
		'uploadFile':
			'<liferay-portlet:actionURL name="/knowledge_base/add_temp_attachment"><portlet:param name="resourcePrimKey" value="<%= String.valueOf(resourcePrimKey) %>" /></liferay-portlet:actionURL>',
	});
</aui:script>

<aui:script>
	window['<portlet:namespace />deleteFileEntry'] = function (fileEntryId) {
		var removeFileEntryIdsInput = document.getElementById(
			'<portlet:namespace />removeFileEntryIds'
		);

		var fileEntries = removeFileEntryIdsInput.value;

		if (fileEntries.length) {
			fileEntries += ',';
		}

		fileEntries += fileEntryId;

		removeFileEntryIdsInput.value = fileEntries;

		var fileEntryIdWrapper = document.getElementById(
			'<portlet:namespace />fileEntryIdWrapper' + fileEntryId
		);

		if (fileEntryIdWrapper) {
			fileEntryIdWrapper.style.display = 'none';
		}
	};
</aui:script>