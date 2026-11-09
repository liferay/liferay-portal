<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String uploadProgressId = "dlFileEntryUploadProgress";

FileEntry fileEntry = (FileEntry)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY);

long fileEntryId = BeanParamUtil.getLong(fileEntry, request, "fileEntryId");

long repositoryId = BeanParamUtil.getLong(fileEntry, request, "repositoryId");

if (repositoryId <= 0) {

	// <liferay-ui:asset_add_button /> only passes in groupId

	repositoryId = BeanParamUtil.getLong(fileEntry, request, "groupId");
}

long folderId = BeanParamUtil.getLong(fileEntry, request, "folderId");

Folder folder = null;

if (fileEntry != null) {
	folder = fileEntry.getFolder();
}

if ((folder == null) && (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {
	try {
		folder = DLAppServiceUtil.getFolder(folderId);
	}
	catch (NoSuchFolderException nsfe) {
		folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}
}

FileVersion fileVersion = null;

long fileVersionId = 0;

long fileEntryTypeId = ParamUtil.getLong(request, "fileEntryTypeId", -1);

if (fileEntry != null) {
	fileVersion = fileEntry.getLatestFileVersion();

	fileVersionId = fileVersion.getFileVersionId();

	if ((fileEntryTypeId == -1) && (fileVersion.getModel() instanceof DLFileVersion)) {
		DLFileVersion dlFileVersion = (DLFileVersion)fileVersion.getModel();

		fileEntryTypeId = dlFileVersion.getFileEntryTypeId();
	}
}

DLFileEntryType dlFileEntryType = null;

if (fileEntryTypeId >= 0) {
	dlFileEntryType = DLFileEntryTypeLocalServiceUtil.getFileEntryType(fileEntryTypeId);
}

DLVersionNumberIncrease dlVersionNumberIncrease = DLVersionNumberIncrease.valueOf(request.getParameter("versionIncrease"), DLVersionNumberIncrease.AUTOMATIC);
boolean updateVersionDetails = ParamUtil.getBoolean(request, "updateVersionDetails");

long assetClassPK = DLAssetHelperUtil.getAssetClassPK(fileEntry, fileVersion);

boolean checkedOut = false;
boolean pending = false;

if (fileEntry != null) {
	checkedOut = fileEntry.isCheckedOut();
	pending = fileVersion.isPending();
}

boolean saveAsDraft = false;

DLPortletInstanceSettings dlPortletInstanceSettings = dlRequestHelper.getDLPortletInstanceSettings();

if ((checkedOut || pending) && !dlPortletInstanceSettings.isEnableFileEntryDrafts()) {
	saveAsDraft = true;
}

DLAdminDisplayContext dlAdminDisplayContext = (DLAdminDisplayContext)request.getAttribute(DLAdminDisplayContext.class.getName());

DLEditFileEntryDisplayContext dlEditFileEntryDisplayContext = null;

if (fileEntry == null) {
	dlEditFileEntryDisplayContext = dlDisplayContextProvider.getDLEditFileEntryDisplayContext(request, response, dlFileEntryType);
}
else {
	dlEditFileEntryDisplayContext = dlDisplayContextProvider.getDLEditFileEntryDisplayContext(request, response, fileEntry);
}

String defaultLanguageId = LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault());

String headerTitle = LanguageUtil.get(request, "new-document");

if (fileVersion != null) {
	headerTitle = fileVersion.getTitle();
}
else if ((dlFileEntryType != null) && (fileEntryTypeId != 0)) {
	headerTitle = LanguageUtil.format(request, "new-x", dlFileEntryType.getName(locale), false);
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(headerTitle);
%>

<c:if test="<%= fileVersion != null %>">

	<%
	String version = null;

	if (dlEditFileEntryDisplayContext.isVersionInfoVisible()) {
		version = fileVersion.getVersion();
	}
	%>

	<div class="management-bar management-bar-light navbar navbar-expand-md">
		<clay:container-fluid
			fullWidth="<%= true %>"
		>
			<ul class="m-auto navbar-nav"></ul>

			<ul class="middle navbar-nav">
				<li class="nav-item">
					<aui:workflow-status markupView="lexicon" model="<%= DLFileEntry.class %>" showHelpMessage="<%= false %>" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= fileVersion.getStatus() %>" version="<%= version %>" />
				</li>
			</ul>

			<ul class="end m-auto navbar-nav"></ul>
		</clay:container-fluid>
	</div>
</c:if>

<clay:container-fluid
	cssClass="container-form-lg"
	size="lg"
>
	<c:if test="<%= checkedOut %>">

		<%
		boolean hasLock = fileEntry.hasLock();

		Lock lock = fileEntry.getLock();
		%>

		<c:choose>
			<c:when test="<%= hasLock %>">
				<div class="alert alert-success">
					<c:choose>
						<c:when test="<%= lock.isNeverExpires() %>">
							<liferay-ui:message key="you-now-have-an-indefinite-lock-on-this-document" />
						</c:when>
						<c:otherwise>
							<liferay-ui:message arguments="<%= StringUtil.toLowerCase(LanguageUtil.getTimeDescription(request, DLFileEntryConstants.LOCK_EXPIRATION_TIME)) %>" key="you-now-have-a-lock-on-this-document" translateArguments="<%= false %>" />
						</c:otherwise>
					</c:choose>
				</div>
			</c:when>
			<c:otherwise>
				<div class="alert alert-danger">
					<liferay-ui:message arguments="<%= new Object[] {HtmlUtil.escape(PortalUtil.getUserName(lock.getUserId(), String.valueOf(lock.getUserId()))), dateTimeFormat.format(lock.getCreateDate())} %>" key="you-cannot-modify-this-document-because-it-was-checked-out-by-x-on-x" translateArguments="<%= false %>" />
				</div>
			</c:otherwise>
		</c:choose>
	</c:if>

	<liferay-portlet:actionURL name="/document_library/edit_file_entry" varImpl="editFileEntryURL">
		<liferay-portlet:param name="mvcRenderCommandName" value="/document_library/edit_file_entry" />
	</liferay-portlet:actionURL>

	<aui:form action="<%= editFileEntryURL %>" cssClass="lfr-dynamic-form" enctype="multipart/form-data" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "saveFileEntry(" + saveAsDraft + ");" %>'>
		<aui:input name="<%= Constants.CMD %>" type="hidden" />
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="portletResource" type="hidden" value='<%= ParamUtil.getString(request, "portletResource") %>' />
		<aui:input name="uploadProgressId" type="hidden" value="<%= uploadProgressId %>" />
		<aui:input name="repositoryId" type="hidden" value="<%= repositoryId %>" />
		<aui:input name="folderId" type="hidden" value="<%= folderId %>" />
		<aui:input name="fileEntryId" type="hidden" value="<%= fileEntryId %>" />

		<c:if test="<%= (fileEntry != null) && checkedOut %>">
			<aui:input name="versionIncrease" type="hidden" />
			<aui:input name="changeLog" type="hidden" />
		</c:if>

		<aui:input name="workflowAction" type="hidden" value="<%= String.valueOf(WorkflowConstants.ACTION_PUBLISH) %>" />

		<div class="lfr-form-content">
			<liferay-ui:error exception="<%= AntivirusScannerException.class %>">

				<%
				AntivirusScannerException antivirusScannerException = (AntivirusScannerException)errorException;
				%>

				<liferay-ui:message key="<%= antivirusScannerException.getMessageKey() %>" />
			</liferay-ui:error>

			<liferay-ui:error exception="<%= AntivirusVirusFoundException.class %>">

				<%
				AntivirusVirusFoundException antivirusVirusFoundException = (AntivirusVirusFoundException)errorException;
				%>

				<liferay-ui:message key="<%= antivirusVirusFoundException.getMessageKey() %>" />
			</liferay-ui:error>

			<liferay-ui:error exception="<%= DLStorageQuotaExceededException.class %>">
				<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(PropsValues.DATA_LIMIT_DL_STORAGE_MAX_SIZE, locale) %>" key="you-have-exceeded-the-x-storage-quota-for-this-instance" />
			</liferay-ui:error>

			<liferay-ui:error exception="<%= DuplicateFileEntryException.class %>" message="please-enter-a-unique-document-name" />
			<liferay-ui:error exception="<%= DuplicateFileEntryExternalReferenceCodeException.class %>" message="please-enter-a-unique-external-reference-code" />
			<liferay-ui:error exception="<%= DuplicateFolderNameException.class %>" message="please-enter-a-unique-document-name" />

			<liferay-ui:error exception="<%= LiferayFileItemException.class %>">
				<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(FileItem.THRESHOLD_SIZE, locale) %>" key="please-enter-valid-content-with-valid-content-size-no-larger-than-x" translateArguments="<%= false %>" />
			</liferay-ui:error>

			<liferay-ui:error exception="<%= FileExtensionException.InvalidExtension.class %>">
				<liferay-ui:message arguments="<%= StringUtil.merge(dlConfiguration.fileExtensions(), StringPool.COMMA_AND_SPACE) %>" key="please-enter-a-file-with-a-valid-extension-x" />
			</liferay-ui:error>

			<liferay-ui:error exception="<%= FileExtensionException.MismatchExtension.class %>" message="the-file-extension-cannot-be-different-from-the-file-name-extension" />

			<liferay-ui:error exception="<%= FileMimeTypeException.class %>">
				<liferay-ui:message arguments="<%= dlAdminDisplayContext.getMimeTypes() %>" key="<%= dlAdminDisplayContext.getMimeTypeMessageKey() %>" translateArguments="<%= false %>" />
			</liferay-ui:error>

			<liferay-ui:error exception="<%= FileNameException.class %>" message="please-enter-a-file-with-a-valid-file-name" />
			<liferay-ui:error exception="<%= FileNameExtensionException.class %>" message="the-file-name-cannot-be-empty-or-without-extension" />
			<liferay-ui:error exception="<%= NoSuchFolderException.class %>" message="please-enter-a-valid-folder" />
			<liferay-ui:error exception="<%= RequiredFileException.class %>" message="please-select-the-file-again" />
			<liferay-ui:error exception="<%= ValueDataException.MustInformDefaultLocale.class %>" message="please-enter-a-value-for-the-default-locale-of-custom-attributes" />

			<liferay-ui:error exception="<%= SourceFileNameException.class %>">
				<liferay-ui:message key="the-source-file-does-not-have-the-same-extension-as-the-original-file" />
			</liferay-ui:error>

			<liferay-ui:error exception="<%= FileSizeException.class %>">

				<%
				FileSizeException fileSizeException = (FileSizeException)errorException;

				String maxSize = LanguageUtil.formatStorageSize(fileSizeException.getMaxSize(), locale);
				%>

				<c:choose>
					<c:when test="<%= fileSizeException.getMimeType() == null %>">
						<liferay-ui:message arguments="<%= maxSize %>" key="please-enter-a-file-with-a-valid-file-size-no-larger-than-x" translateArguments="<%= false %>" />
					</c:when>
					<c:otherwise>
						<liferay-ui:message arguments="<%= new Object[] {maxSize, fileSizeException.getMimeType()} %>" key="please-enter-a-file-with-a-valid-file-size-no-larger-than-x-for-type-x" translateArguments="<%= false %>" />
					</c:otherwise>
				</c:choose>
			</liferay-ui:error>

			<liferay-ui:error exception="<%= UploadRequestSizeException.class %>">
				<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(dlEditFileEntryDisplayContext.getMaximumUploadRequestSize(), locale) %>" key="request-is-larger-than-x-and-could-not-be-processed" translateArguments="<%= false %>" />
			</liferay-ui:error>

			<liferay-asset:asset-categories-error />

			<liferay-asset:asset-tags-error />

			<aui:model-context bean="<%= fileVersion %>" model="<%= DLFileVersion.class %>" />

			<div class="sheet">
				<div class="panel-group panel-group-flush">

					<%
					long fileMaxSize = dlEditFileEntryDisplayContext.getMaximumUploadSize();
					%>

					<c:if test="<%= fileMaxSize != 0 %>">
						<div class="alert alert-info">
							<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(fileMaxSize, locale) %>" key="upload-documents-no-larger-than-x" translateArguments="<%= false %>" />
						</div>
					</c:if>

					<%
					String folderName = StringPool.BLANK;

					if (folderId > 0) {
						folder = DLAppLocalServiceUtil.getFolder(folderId);

						folder = folder.toEscapedModel();

						folderId = folder.getFolderId();
						folderName = folder.getName();
					}
					else {
						folderName = LanguageUtil.get(request, "home");
					}
					%>

					<c:if test="<%= dlEditFileEntryDisplayContext.isFolderSelectionVisible() %>">

						<%
						ItemSelector itemSelector = (ItemSelector)request.getAttribute(ItemSelector.class.getName());

						FolderItemSelectorURLProvider folderItemSelectorURLProvider = new FolderItemSelectorURLProvider(request, itemSelector);
						%>

						<liferay-frontend:resource-selector
							inputLabel='<%= LanguageUtil.get(request, "folder") %>'
							inputName="newFolderId"
							modalTitle='<%= LanguageUtil.get(request, "select-folder") %>'
							resourceName="<%= folderName %>"
							resourceValue="<%= String.valueOf(folderId) %>"
							selectEventName="folderSelected"
							selectResourceURL="<%= folderItemSelectorURLProvider.getSelectAddFileEntryFolderURL(folderId) %>"
							showRemoveButton="<%= true %>"
						/>
					</c:if>

					<%@ include file="/document_library/edit_file_entry_picker.jspf" %>

					<aui:input label="title" name="title" />

					<c:if test="<%= dlEditFileEntryDisplayContext.isFileNameVisible() %>">
						<div>
							<aui:input label="file-name" name="fileName" type="text" />

							<c:if test="<%= fileVersion != null %>">
								<react:component
									module="{FileNameInput} from document-library-web"
									props='<%=
										HashMapBuilder.<String, Object>put(
											"initialValue", fileVersion.getFileName()
										).put(
											"required", Validator.isNotNull(fileVersion.getExtension())
										).build()
									%>'
								/>
							</c:if>
						</div>

						<aui:input label="automatically-populate-with-the-uploaded-file-name" name="changeFileName" type="checkbox" value="true" />
					</c:if>

					<c:if test="<%= (folder == null) || folder.isSupportsMetadata() %>">
						<aui:input name="description" />

						<aui:field-wrapper cssClass="form-group" label="external-reference-code" name="externalReferenceCode">
							<div class="small text-secondary"><liferay-ui:message key="unique-key-for-referencing-the-document-definition" /></div>

							<div class="input-group">
								<div class="input-group-item">
									<aui:input disabled="<%= dlEditFileEntryDisplayContext.isERCFieldEnabled() %>" label="" name="externalReferenceCode" type="text" value="<%= dlEditFileEntryDisplayContext.getExternalReferenceCode() %>" />
								</div>
							</div>
						</aui:field-wrapper>

						<c:if test="<%= (folder == null) || (folder.getModel() instanceof DLFolder) %>">

							<%
							boolean inherited = false;

							if (folder != null) {
								DLFolder dlFolder = (DLFolder)folder.getModel();

								if (dlFolder.getRestrictionType() == DLFolderConstants.RESTRICTION_TYPE_INHERIT) {
									inherited = true;
								}
							}

							List<DLFileEntryType> dlFileEntryTypes = DLFileEntryTypeLocalServiceUtil.getFolderFileEntryTypes(PortalUtil.getCurrentAndAncestorSiteGroupIds(scopeGroupId), folderId, inherited);
							%>

							<c:choose>
								<c:when test="<%= dlFileEntryTypes.size() > 1 %>">
									<aui:select changesContext="<%= true %>" label="document-type" name="fileEntryTypeId" onChange='<%= liferayPortletResponse.getNamespace() + "changeFileEntryType();" %>'>

										<%
										for (DLFileEntryType curDLFileEntryType : dlFileEntryTypes) {
										%>

											<c:if test="<%= (curDLFileEntryType.getFileEntryTypeId() == DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) || (fileEntryTypeId == curDLFileEntryType.getFileEntryTypeId()) || DLFileEntryTypePermission.contains(permissionChecker, curDLFileEntryType, ActionKeys.VIEW) %>">
												<aui:option label="<%= HtmlUtil.escape(curDLFileEntryType.getName(locale)) %>" selected="<%= fileEntryTypeId == curDLFileEntryType.getPrimaryKey() %>" value="<%= curDLFileEntryType.getPrimaryKey() %>" />
											</c:if>

										<%
										}
										%>

									</aui:select>
								</c:when>
								<c:otherwise>
									<aui:input name="fileEntryTypeId" type="hidden" value="<%= fileEntryTypeId %>" />
								</c:otherwise>
							</c:choose>

							<aui:input name="defaultLanguageId" type="hidden" value="<%= defaultLanguageId %>" />

							<c:if test="<%= fileEntryTypeId > 0 %>">

								<%
								List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(dlFileEntryType);

								boolean showLanguageSelector = false;

								for (DDMStructure ddmStructure : ddmStructures) {
									if (dlEditFileEntryDisplayContext.isDDMStructureVisible(ddmStructure)) {
										showLanguageSelector = true;

										break;
									}
								}
								%>

								<c:if test="<%= showLanguageSelector %>">
									<div class="mb-3">
										<react:component
											module="{LanguageSelector} from document-library-web"
											props='<%=
												HashMapBuilder.<String, Object>put(
													"ddmStructureIds", DDMStructureUtil.getDDMStructureIds(ddmStructures)
												).put(
													"languageIds", DDMStructureUtil.getAvailableLanguageIds(themeDisplay)
												).put(
													"selectedLanguageId", defaultLanguageId
												).put(
													"translatedLanguageIds", DDMStructureUtil.getTranslatedLanguageIds(ddmStructures, dlEditFileEntryDisplayContext, fileVersionId)
												).build()
											%>'
										/>
									</div>
								</c:if>

								<%
								try {
									boolean localizable = true;

									for (DDMStructure ddmStructure : DLFileEntryTypeUtil.getDDMStructures(dlFileEntryType)) {
										com.liferay.dynamic.data.mapping.storage.DDMFormValues ddmFormValues = dlEditFileEntryDisplayContext.getDDMFormValues(ddmStructure, fileVersionId);
								%>

										<div class="<%= !dlEditFileEntryDisplayContext.isDDMStructureVisible(ddmStructure) ? "hide" : "" %> file-entry-type-fields">

											<%
											DDMFormValuesToMapConverter ddmFormValuesToMapConverter = (DDMFormValuesToMapConverter)request.getAttribute(DDMFormValuesToMapConverter.class.getName());
											%>

											<liferay-data-engine:data-layout-renderer
												containerId='<%= liferayPortletResponse.getNamespace() + "dataEngineLayoutRenderer" + ddmStructure.getStructureId() %>'
												dataDefinitionId="<%= ddmStructure.getStructureId() %>"
												dataRecordValues="<%= ddmFormValuesToMapConverter.convert(ddmFormValues, DDMStructureLocalServiceUtil.getStructure(ddmStructure.getStructureId())) %>"
												defaultLanguageId="<%= defaultLanguageId %>"
												languageId="<%= dlEditFileEntryDisplayContext.getDLFileEntryTypeLanguageId(ddmStructure, PortalUtil.getLocale(request)) %>"
												namespace="<%= liferayPortletResponse.getNamespace() + ddmStructure.getStructureId() + StringPool.UNDERLINE %>"
												persistDefaultValues="<%= true %>"
												persisted="<%= fileEntry != null %>"
											/>
										</div>

								<%
										localizable = false;
									}
								}
								catch (Exception exception) {
									_log.error(exception);
								}
								%>

							</c:if>
						</c:if>
					</c:if>

					<c:choose>
						<c:when test="<%= (fileEntry != null) && !checkedOut && dlAdminDisplayContext.isVersioningStrategyOverridable() %>">
							<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="versioning">
								<aui:input inlineLabel="right" label="customize-the-version-number-increment-and-describe-my-changes" labelCssClass="simple-toggle-switch" name="updateVersionDetails" type="toggle-switch" value="<%= updateVersionDetails %>" />

								<div class="<%= updateVersionDetails ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />versionDetails">
									<aui:input checked="<%= dlVersionNumberIncrease == DLVersionNumberIncrease.MAJOR %>" label="major-version" name="versionIncrease" type="radio" value="<%= DLVersionNumberIncrease.MAJOR %>" />

									<aui:input checked="<%= dlVersionNumberIncrease == DLVersionNumberIncrease.MINOR %>" label="minor-version" name="versionIncrease" type="radio" value="<%= DLVersionNumberIncrease.MINOR %>" />

									<aui:input checked="<%= (dlVersionNumberIncrease == DLVersionNumberIncrease.AUTOMATIC) || (dlVersionNumberIncrease == DLVersionNumberIncrease.NONE) %>" label="keep-current-version-number" name="versionIncrease" type="radio" value="<%= DLVersionNumberIncrease.NONE %>" />

									<aui:model-context />

									<aui:input label="version-notes" maxLength="75" name="changeLog" />

									<aui:model-context bean="<%= fileVersion %>" model="<%= DLFileVersion.class %>" />
								</div>
							</aui:fieldset>
						</c:when>
						<c:otherwise>
							<aui:input name="updateVersionDetails" type="hidden" value="<%= false %>" />
						</c:otherwise>
					</c:choose>

					<%
					Group scopeGroup = themeDisplay.getScopeGroup();
					%>

					<c:if test="<%= !scopeGroup.isCompany() && !scopeGroup.isDepot() %>">
						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="display-page">
							<liferay-asset:select-asset-display-page
								classNameId="<%= PortalUtil.getClassNameId(FileEntry.class) %>"
								classPK="<%= (fileEntry != null) ? fileEntry.getFileEntryId() : 0 %>"
								classTypeId="<%= (fileEntryTypeId < 0) ? DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT : fileEntryTypeId %>"
								groupId="<%= scopeGroupId %>"
								showViewInContextLink="<%= true %>"
							/>
						</aui:fieldset>
					</c:if>

					<c:if test="<%= (folder == null) || folder.isSupportsMetadata() %>">
						<liferay-expando:custom-attributes-available
							className="<%= DLFileEntryConstants.getClassName() %>"
						>
							<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="custom-fields">
								<liferay-expando:custom-attribute-list
									className="<%= DLFileEntryConstants.getClassName() %>"
									classPK="<%= fileVersionId %>"
									editable="<%= true %>"
									label="<%= true %>"
								/>
							</aui:fieldset>
						</liferay-expando:custom-attributes-available>
					</c:if>

					<c:if test="<%= (folder == null) || folder.isSupportsSocial() %>">
						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="categorization">
							<liferay-asset:asset-categories-selector
								className="<%= DLFileEntry.class.getName() %>"
								classPK="<%= assetClassPK %>"
								classTypePK="<%= (fileEntryTypeId < 0) ? DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT : fileEntryTypeId %>"
								groupIds="<%= SiteConnectedGroupGroupProviderUtil.getCurrentAndAncestorSiteAndDepotGroupIds(dlAdminDisplayContext.getRepositoryGroupId(scopeGroupId, repositoryId)) %>"
								visibilityTypes="<%= AssetVocabularyConstants.VISIBILITY_TYPES %>"
							/>

							<liferay-asset:asset-tags-selector
								className="<%= DLFileEntry.class.getName() %>"
								classPK="<%= assetClassPK %>"
								groupIds="<%= SiteConnectedGroupGroupProviderUtil.getCurrentAndAncestorSiteAndDepotGroupIds(dlAdminDisplayContext.getRepositoryGroupId(scopeGroupId, repositoryId)) %>"
							/>

							<c:if test="<%= (fileEntry != null) && dlAdminDisplayContext.isAutoTaggingEnabled() %>">
								<clay:checkbox
									checked="<%= dlAdminDisplayContext.isUpdateAutoTags() %>"
									id='<%= liferayPortletResponse.getNamespace() + "updateAutoTags" %>'
									label='<%= LanguageUtil.get(request, "update-auto-tags") %>'
									name='<%= liferayPortletResponse.getNamespace() + "updateAutoTags" %>'
								/>

								<div class="ml-4">
									<small class="text-secondary">
										<liferay-ui:message key="update-auto-tags-help" />
									</small>
								</div>
							</c:if>
						</aui:fieldset>
					</c:if>

					<c:if test="<%= !RepositoryUtil.isExternalRepository(repositoryId) %>">
						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="schedule[noun]">
							<liferay-ui:error exception="<%= FileEntryDisplayDateException.class %>" message="please-enter-a-valid-publish-date" />
							<liferay-ui:error exception="<%= FileEntryExpirationDateException.class %>" message="please-enter-a-valid-expiration-date" />
							<liferay-ui:error exception="<%= FileEntryReviewDateException.class %>" message="please-enter-a-valid-review-date" />

							<p class="text-secondary">
								<liferay-ui:message key="set-the-publication-date-and-time-for-your-document-to-be-published-automatically" />
							</p>

							<aui:input label="publish-date" name="displayDate" wrapperCssClass="display-date" />

							<p class="text-secondary">
								<liferay-ui:message key="including-an-expiration-date-will-allow-your-documents-or-media-to-expire-automatically-and-become-unpublished" />
							</p>

							<aui:input dateTogglerCheckboxLabel="never-expire" disabled="<%= dlEditFileEntryDisplayContext.isNeverExpire() %>" name="expirationDate" wrapperCssClass="expiration-date" />

							<aui:input dateTogglerCheckboxLabel="never-review" disabled="<%= dlEditFileEntryDisplayContext.isNeverReview() %>" name="reviewDate" wrapperCssClass="review-date" />
						</aui:fieldset>
					</c:if>

					<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="friendly-url">
						<liferay-friendly-url:input
							className="<%= FileEntry.class.getName() %>"
							classPK="<%= fileEntryId %>"
							inputAddon="<%= dlEditFileEntryDisplayContext.getFriendlyURLBase() %>"
							localizable="<%= false %>"
							name="urlTitle"
							showHistory="<%= true %>"
						/>

						<p class="text-secondary"><liferay-ui:message key="the-friendly-url-may-be-modified-to-ensure-uniqueness" /></p>
					</aui:fieldset>

					<c:if test="<%= (folder == null) || folder.isSupportsSocial() %>">
						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="related-assets">
							<liferay-asset:input-asset-links
								className="<%= DLFileEntry.class.getName() %>"
								classPK="<%= assetClassPK %>"
							/>
						</aui:fieldset>
					</c:if>

					<c:if test="<%= (fileEntry == null) && dlEditFileEntryDisplayContext.isPermissionsVisible() %>">
						<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="permissions">
							<liferay-ui:input-permissions
								modelName="<%= DLFileEntryConstants.getClassName() %>"
							/>
						</aui:fieldset>
					</c:if>

					<c:if test="<%= pending %>">
						<div class="alert alert-info">
							<liferay-ui:message key="there-is-a-publication-workflow-in-process" />
						</div>
					</c:if>

					<div class="sheet-footer" data-qa-id="addFileEntryFooter">
						<c:if test="<%= dlEditFileEntryDisplayContext.isSaveButtonVisible() %>">
							<aui:button disabled="<%= dlEditFileEntryDisplayContext.isSaveButtonDisabled() %>" name="saveButton" onClick='<%= liferayPortletResponse.getNamespace() + "saveFileEntry(true);" %>' value="<%= dlEditFileEntryDisplayContext.getSaveButtonLabel() %>" />
						</c:if>

						<c:if test="<%= dlEditFileEntryDisplayContext.isPublishButtonVisible() %>">
							<aui:button disabled="<%= dlEditFileEntryDisplayContext.isPublishButtonDisabled() %>" name="publishButton" type="submit" value="<%= dlEditFileEntryDisplayContext.getPublishButtonLabel() %>" />
						</c:if>

						<c:if test="<%= dlEditFileEntryDisplayContext.isCheckoutDocumentButtonVisible() %>">
							<aui:button disabled="<%= dlEditFileEntryDisplayContext.isCheckoutDocumentButtonDisabled() %>" onClick='<%= liferayPortletResponse.getNamespace() + "checkOut();" %>' primary="<%= false %>" type="submit" value="checkout[document]" />
						</c:if>

						<c:if test="<%= dlEditFileEntryDisplayContext.isCheckinButtonVisible() %>">
							<aui:button disabled="<%= dlEditFileEntryDisplayContext.isCheckinButtonDisabled() %>" onClick='<%= liferayPortletResponse.getNamespace() + "checkIn();" %>' value="save-and-checkin" />
						</c:if>

						<c:if test="<%= dlEditFileEntryDisplayContext.isCancelCheckoutDocumentButtonVisible() %>">
							<aui:button disabled="<%= dlEditFileEntryDisplayContext.isCancelCheckoutDocumentButtonDisabled() %>" onClick='<%= liferayPortletResponse.getNamespace() + "cancelCheckOut();" %>' primary="<%= false %>" type="submit" value="cancel-checkout[document]" />
						</c:if>

						<aui:button href="<%= redirect %>" type="cancel" />
					</div>
				</div>
			</div>
		</div>
	</aui:form>
</clay:container-fluid>

<c:if test="<%= (fileEntry != null) && checkedOut && dlAdminDisplayContext.isVersioningStrategyOverridable() %>">

	<%
	request.setAttribute("edit_file_entry.jsp-checkedOut", checkedOut);
	%>

	<liferay-util:include page="/document_library/version_details.jsp" servletContext="<%= application %>" />
</c:if>

<aui:script>
	var form = document.<portlet:namespace />fm;

	function <portlet:namespace />changeFileEntryType() {
		function updateFileEntryType() {
			Liferay.Util.setFormValues(form, {
				<%= Constants.CMD %>: '<%= Constants.PREVIEW %>',
			});

			form.submit();
		}

		var fileElement = Liferay.Util.getFormElement(form, 'file');

		if (
			(fileElement && fileElement.value) ||
			document.querySelector('.file-entry-type-fields:not(.hide)')
		) {
			Liferay.Util.openConfirmModal({
				message:
					'<liferay-ui:message key="changing-the-document-type-will-clear-the-currently-selected-file-and-metadata" />',
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						updateFileEntryType();
					}
					else {
						Liferay.Util.setFormValues(form, {
							fileEntryTypeId: '<%= fileEntryTypeId %>',
						});
					}
				},
			});
		}
		else {
			updateFileEntryType();
		}
	}

	function <portlet:namespace />cancelCheckOut() {
		Liferay.Util.setFormValues(form, {
			<%= Constants.CMD %>: '<%= Constants.CANCEL_CHECKOUT %>',
		});
	}

	function <portlet:namespace />checkIn() {
		Liferay.Util.setFormValues(form, {
			<%= Constants.CMD %>: '<%= Constants.UPDATE_AND_CHECKIN %>',
		});

		if (<%= dlAdminDisplayContext.isVersioningStrategyOverridable() %>) {
			<portlet:namespace />showVersionDetailsDialog(form);
		}
		else {
			form.submit();
		}
	}

	function <portlet:namespace />checkOut() {
		Liferay.Util.setFormValues(form, {
			<%= Constants.CMD %>: '<%= Constants.CHECKOUT %>',
		});
	}

	function <portlet:namespace />saveFileEntry(draft) {
		var fileElement = Liferay.Util.getFormElement(form, 'file');

		var cmdElement = Liferay.Util.getFormElement(form, 'cmd');

		if (cmdElement && !cmdElement.value) {
			Liferay.Util.setFormValues(form, {
				<%= Constants.CMD %>:
					'<%= (fileEntry == null) ? Constants.ADD : Constants.UPDATE %>',
			});
		}

		if (draft) {
			Liferay.Util.setFormValues(form, {
				workflowAction: '<%= WorkflowConstants.ACTION_SAVE_DRAFT %>',
			});
		}

		submitForm(form);
	}

	function <portlet:namespace />showVersionDetailsDialog() {
		Liferay.componentReady(
			'<portlet:namespace />DocumentLibraryCheckinModal'
		).then((documentLibraryCheckinModal) => {
			documentLibraryCheckinModal.open((versionIncrease, changeLog) => {
				Liferay.Util.setFormValues(form, {
					changeLog: changeLog,
					updateVersionDetails: true,
					versionIncrease: versionIncrease,
				});
				form.submit();
			});
		});
	}

	function <portlet:namespace />updateFileNameAndTitle() {
		var titleElement = document.getElementById('<portlet:namespace />title');

		var urlTitleElement = document.getElementById(
			'<portlet:namespace />urlTitle'
		);

		var fileNameElement = document.getElementById(
			'<portlet:namespace />fileName'
		);
		var fileElement = document.getElementById('<portlet:namespace />file');

		if (fileElement && fileElement.value) {
			var fileFileName = fileElement.value.replace(/^.*[\\\/]/, '');

			if (titleElement && !titleElement.value) {
				titleElement.value = fileFileName.replace(/\.[^.]*$/, '');
			}

			var autoChangeFileName = document.getElementById(
				'<portlet:namespace />changeFileName'
			);

			if (
				fileNameElement &&
				(!fileNameElement.value || autoChangeFileName.checked)
			) {
				fileNameElement.value = fileFileName;
			}

			if (urlTitleElement && !urlTitleElement.value) {
				<c:choose>
					<c:when test="<%= dlEditFileEntryDisplayContext.isFriendlyURLWithExtensionEnabled() %>">
						urlTitleElement.value = fileFileName;
					</c:when>
					<c:otherwise>
						urlTitleElement.value = fileFileName.replace(/\.[^.]*$/, '');
					</c:otherwise>
				</c:choose>
			}
		}

		var formComponent = Liferay.Form.get('<portlet:namespace />fm');

		formComponent.formValidator.validateField('<portlet:namespace />title');
	}
</aui:script>

<c:if test="<%= (fileEntry != null) && !checkedOut && dlAdminDisplayContext.isVersioningStrategyOverridable() %>">
	<aui:script>
		var updateVersionDetailsElement = document.getElementById(
			'<portlet:namespace />updateVersionDetails'
		);
		var versionDetailsElement = document.getElementById(
			'<portlet:namespace />versionDetails'
		);

		if (updateVersionDetailsElement && versionDetailsElement) {
			updateVersionDetailsElement.addEventListener('click', (event) => {
				versionDetailsElement.classList.toggle('hide');
			});
		}
	</aui:script>
</c:if>

<%
if (fileEntry != null) {
	DLBreadcrumbUtil.addPortletBreadcrumbEntries(fileEntry, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "edit"), currentURL);
}
else {
	DLBreadcrumbUtil.addPortletBreadcrumbEntries(folderId, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-file-entry"), currentURL);
}
%>

<%!
private static final Log _log = LogFactoryUtil.getLog("com_liferay_document_library_web.document_library.edit_file_entry_jsp");
%>