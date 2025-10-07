<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
FileEntry fileEntry = (FileEntry)request.getAttribute("info_panel.jsp-fileEntry");
FileVersion fileVersion = (FileVersion)request.getAttribute("info_panel.jsp-fileVersion");
boolean hideActions = GetterUtil.getBoolean(request.getAttribute("info_panel_file_entry.jsp-hideActions"));

DLPortletInstanceSettings dlPortletInstanceSettings = dlRequestHelper.getDLPortletInstanceSettings();
DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext = dlDisplayContextProvider.getDLViewFileVersionDisplayContext(request, response, fileVersion);

long assetClassPK = DLAssetHelperUtil.getAssetClassPK(fileEntry, fileVersion);
%>

<div class="sidebar-header">
	<clay:content-row
		cssClass="sidebar-section"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<clay:content-section>
				<h1 class="component-title">
					<%= HtmlUtil.escape(fileVersion.getTitle()) %>
				</h1>

				<c:if test="<%= !Objects.equals(fileVersion.getTitle(), fileVersion.getFileName()) %>">
					<p class="component-subtitle">
						<%= HtmlUtil.escape(fileVersion.getFileName()) %>
					</p>
				</c:if>

				<c:if test="<%= dlViewFileVersionDisplayContext.isVersionInfoVisible() %>">
					<clay:label
						displayType="info"
						label='<%= LanguageUtil.get(request, "version") + StringPool.SPACE + fileVersion.getVersion() %>'
					/>
				</c:if>

				<aui:model-context bean="<%= fileVersion %>" model="<%= DLFileVersion.class %>" />

				<aui:workflow-status model="<%= DLFileEntry.class %>" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= fileVersion.getStatus() %>" />

				<c:if test="<%= !fileVersion.isApproved() && dlViewFileVersionDisplayContext.hasApprovedVersion() %>">
					<clay:label
						displayType="success"
						label="approved"
					/>
				</c:if>
			</clay:content-section>
		</clay:content-col>

		<clay:content-col>
			<ul class="autofit-padded-no-gutters autofit-row">
				<li class="autofit-col">
					<liferay-util:include page="/document_library/subscribe_file_entry.jsp" servletContext="<%= application %>" />
				</li>

				<%
				DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper = new DLPortletInstanceSettingsHelper(dlRequestHelper);
				%>

				<c:if test="<%= !hideActions && dlPortletInstanceSettingsHelper.isShowActions() %>">
					<li class="autofit-col">
						<liferay-util:include page="/document_library/file_entry_action.jsp" servletContext="<%= application %>" />
					</li>
				</c:if>
			</ul>
		</clay:content-col>
	</clay:content-row>
</div>

<div class="sidebar-body">

	<%
	String tabsNames = "details";

	if (dlViewFileVersionDisplayContext.isVersionInfoVisible()) {
		tabsNames += ",versions";
	}
	%>

	<liferay-ui:tabs
		cssClass="navbar-no-collapse"
		names="<%= tabsNames %>"
		param='<%= "tabs_" + fileEntry.getFileEntryId() %>'
		refresh="<%= false %>"
	>
		<liferay-ui:section>

			<%
			String thumbnailSrc = DLURLHelperUtil.getThumbnailSrc(fileEntry, fileVersion, themeDisplay);
			%>

			<c:if test="<%= Validator.isNotNull(thumbnailSrc) %>">
				<div class="aspect-ratio aspect-ratio-16-to-9 sidebar-panel thumbnail">
					<img alt="<liferay-ui:message escapeAttribute="<%= true %>" key="thumbnail" />" class="aspect-ratio-item-center-middle aspect-ratio-item-fluid" src="<%= DLURLHelperUtil.getThumbnailSrc(fileEntry, fileVersion, themeDisplay) %>" />
				</div>
			</c:if>

			<liferay-dynamic-section:dynamic-section
				name="com.liferay.document.library.web#/document_library/info_panel_file_entry.jsp#fileEntryOwner"
			>
				<clay:content-row
					cssClass="sidebar-section widget-metadata"
					noGutters="true"
				>

					<%
					User owner = UserLocalServiceUtil.fetchUser(fileEntry.getUserId());
					%>

					<clay:content-col>
						<liferay-user:user-portrait
							user="<%= owner %>"
						/>
					</clay:content-col>

					<clay:content-col
						expand="<%= true %>"
					>
						<div class="component-title username">
							<c:if test="<%= owner != null %>">
								<a href="<%= owner.isGuestUser() ? StringPool.BLANK : owner.getDisplayURL(themeDisplay) %>"><%= HtmlUtil.escape(owner.getFullName()) %></a>
							</c:if>
						</div>

						<div class="component-subtitle">
							<liferay-ui:message key="owner" />
						</div>
					</clay:content-col>
				</clay:content-row>
			</liferay-dynamic-section:dynamic-section>

			<c:if test="<%= dlViewFileVersionDisplayContext.isDownloadLinkVisible() || dlViewFileVersionDisplayContext.isSharingLinkVisible() %>">
				<div class="sidebar-section">
					<div class="btn-group">
						<c:if test="<%= dlViewFileVersionDisplayContext.isDownloadLinkVisible() %>">
							<c:choose>
								<c:when test="<%= PropsValues.DL_FILE_ENTRY_CONVERSIONS_ENABLED && DocumentConversionUtil.isEnabled() %>">

									<%
									String[] conversions = DocumentConversionUtil.getConversions(fileVersion.getExtension());
									%>

									<c:choose>
										<c:when test="<%= conversions.length > 0 %>">
											<div class="btn-group-item" data-analytics-external-reference-code="<%= fileEntry.getExternalReferenceCode() %>" data-analytics-file-entry-id="<%= String.valueOf(fileEntry.getFileEntryId()) %>" data-analytics-file-entry-title="<%= HtmlUtil.escapeAttribute(String.valueOf(fileEntry.getTitle())) %>" data-analytics-file-entry-version="<%= String.valueOf(fileEntry.getVersion()) %>">
												<clay:dropdown-menu
													dropdownItems='<%=
														new JSPDropdownItemList(pageContext) {
															{
																ThemeDisplay themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY);

																Map<String, Object> data = HashMapBuilder.<String, Object>put(
																	"analytics-external-reference-code", fileEntry.getExternalReferenceCode()
																).put(
																	"analytics-file-entry-id", String.valueOf(fileEntry.getFileEntryId())
																).put(
																	"analytics-file-entry-title", String.valueOf(fileEntry.getTitle())
																).put(
																	"analytics-file-entry-version", String.valueOf(fileEntry.getVersion())
																).build();

																add(
																	dropdownItem -> {
																		dropdownItem.setData(data);
																		dropdownItem.setHref(DLURLHelperUtil.getDownloadURL(fileEntry, fileVersion, themeDisplay, StringPool.BLANK, false, true));
																		dropdownItem.setLabel(LanguageUtil.get(httpServletRequest, "this-version"));
																		dropdownItem.setSeparator(true);
																	});

																addGroup(
																	dropdownGroupItem -> {
																		dropdownGroupItem.setDropdownItems(
																			new DropdownItemList() {
																				{
																					for (String conversion : conversions) {
																						add(
																							dropdownItem -> {
																								dropdownItem.setData(data);
																								dropdownItem.setHref(DLURLHelperUtil.getDownloadURL(fileEntry, fileVersion, themeDisplay, "&targetExtension=" + conversion));
																								dropdownItem.setLabel(StringUtil.toUpperCase(conversion));
																							});
																					}
																				}
																			});
																		dropdownGroupItem.setLabel(LanguageUtil.get(httpServletRequest, "convert-to"));
																	});
															}
														}
													%>'
													label="download"
													small="<%= true %>"
												/>
											</div>
										</c:when>
										<c:otherwise>
											<div class="btn-group-item">
												<clay:link
													data-analytics-external-reference-code="<%= fileEntry.getExternalReferenceCode() %>"
													data-analytics-file-entry-id="<%= fileEntry.getFileEntryId() %>"
													data-analytics-file-entry-title="<%= fileEntry.getTitle() %>"
													data-analytics-file-entry-version="<%= fileEntry.getVersion() %>"
													displayType="primary"
													href="<%= DLURLHelperUtil.getDownloadURL(fileEntry, fileVersion, themeDisplay, StringPool.BLANK, false, true) %>"
													label="download"
													small="<%= true %>"
													title='<%= LanguageUtil.format(resourceBundle, "file-size-x", LanguageUtil.formatStorageSize(fileVersion.getSize(), locale), false) %>'
													type="button"
												/>
											</div>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<div class="btn-group-item" data-analytics-external-reference-code="<%= fileEntry.getExternalReferenceCode() %>" data-analytics-file-entry-id="<%= String.valueOf(fileEntry.getFileEntryId()) %>" data-analytics-file-entry-title="<%= HtmlUtil.escapeAttribute(String.valueOf(fileEntry.getTitle())) %>" data-analytics-file-entry-version="<%= String.valueOf(fileEntry.getVersion()) %>">
										<clay:link
											displayType="primary"
											href="<%= DLURLHelperUtil.getDownloadURL(fileEntry, fileVersion, themeDisplay, StringPool.BLANK, false, true) %>"
											label="download"
											small="<%= true %>"
											title='<%= LanguageUtil.format(resourceBundle, "file-size-x", LanguageUtil.formatStorageSize(fileVersion.getSize(), locale), false) %>'
											type="button"
										/>
									</div>
								</c:otherwise>
							</c:choose>
						</c:if>

						<c:if test="<%= dlViewFileVersionDisplayContext.isSharingLinkVisible() %>">
							<div class="btn-group-item">
								<liferay-sharing:button
									className="<%= DLFileEntryConstants.getClassName() %>"
									classPK="<%= fileEntry.getFileEntryId() %>"
								/>
							</div>
						</c:if>
					</div>
				</div>

				<div class="sidebar-section">

					<%
					DLViewFileEntryDisplayContext dlViewFileEntryDisplayContext = (DLViewFileEntryDisplayContext)request.getAttribute(DLViewFileEntryDisplayContext.class.getName());

					boolean latestVersion = false;

					if (dlViewFileEntryDisplayContext != null) {
						latestVersion = fileVersion.equals(dlViewFileEntryDisplayContext.getFileVersion());
					}
					else {
						latestVersion = fileVersion.equals(fileEntry.getLatestFileVersion());
					}

					String urlLabel = null;

					if (latestVersion) {
						urlLabel = LanguageUtil.get(resourceBundle, "latest-version-url");
					}
					else {
						urlLabel = LanguageUtil.format(request, "version-x-url", fileVersion.getVersion());
					}

					String urlInputId = liferayPortletResponse.getNamespace() + "urlInput";
					%>

					<div class="form-group">
						<label for="<%= urlInputId %>"><%= urlLabel %></label>

						<div class="input-group input-group-sm">
							<div class="input-group-item input-group-prepend">
								<input class="form-control" id="<%= urlInputId %>" readonly value="<%= DLURLHelperUtil.getPreviewURL(fileEntry, fileVersion, themeDisplay, StringPool.BLANK, !latestVersion, true) %>" />
							</div>

							<span class="input-group-append input-group-item input-group-item-shrink">
								<clay:button
									cssClass="dm-infopanel-copy-clipboard lfr-portal-tooltip"
									data-clipboard-target='<%= "#" + urlInputId %>'
									displayType="secondary"
									icon="paste"
									title="copy-link"
								/>
							</span>
						</div>
					</div>

					<c:if test="<%= portletDisplay.isWebDAVEnabled() && fileEntry.isSupportsSocial() && latestVersion %>">

						<%
						String webDavURLInputId = liferayPortletResponse.getNamespace() + "webDavURLInput";
						%>

						<div class="form-group">
							<label for="<%= webDavURLInputId %>">
								<liferay-ui:message key='<%= TextFormatter.format("webDavURL", TextFormatter.K) %>' />

								<liferay-ui:icon-help message='<%= LanguageUtil.get(request, "webdav-help") %>' />
							</label>

							<div class="input-group input-group-sm">
								<div class="input-group-item input-group-prepend">
									<input class="form-control" id="<%= webDavURLInputId %>" readonly value="<%= DLURLHelperUtil.getWebDavURL(themeDisplay, fileEntry.getFolder(), fileEntry) %>" />
								</div>

								<span class="input-group-append input-group-item input-group-item-shrink">
									<clay:button
										cssClass="dm-infopanel-copy-clipboard lfr-portal-tooltip"
										data-clipboard-target='<%= "#" + webDavURLInputId %>'
										displayType="secondary"
										icon="paste"
										title="copy-link"
									/>
								</span>
							</div>
						</div>
					</c:if>
				</div>
			</c:if>

			<dl class="sidebar-dl sidebar-section">
				<c:if test="<%= fileVersion.getModel() instanceof DLFileVersion %>">

					<%
					DLFileVersion dlFileVersion = (DLFileVersion)fileVersion.getModel();

					DLFileEntryType dlFileEntryType = dlFileVersion.getDLFileEntryType();
					%>

					<dt class="sidebar-dt">
						<liferay-ui:message key="document-type" />
					</dt>
					<dd class="sidebar-dd">
						<%= HtmlUtil.escape(dlFileEntryType.getName(locale)) %>
					</dd>
				</c:if>

				<c:if test="<%= Validator.isNotNull(fileVersion.getExtension()) %>">
					<dt class="sidebar-dt">
						<liferay-ui:message key="extension[file]" />
					</dt>
					<dd class="sidebar-dd">
						<%= HtmlUtil.escape(fileVersion.getExtension()) %>
					</dd>
				</c:if>

				<dt class="sidebar-dt">
					<liferay-ui:message key="size" />
				</dt>
				<dd class="sidebar-dd">
					<%= HtmlUtil.escape(LanguageUtil.formatStorageSize(fileVersion.getSize(), locale)) %>
				</dd>
				<dt class="sidebar-dt">
					<liferay-ui:message key="modified" />
				</dt>
				<dd class="sidebar-dd">
					<liferay-ui:message arguments="<%= new Object[] {dateTimeFormat.format(fileVersion.getModifiedDate()), HtmlUtil.escape(fileVersion.getStatusByUserName())} %>" key="x-by-x" translateArguments="<%= false %>" />
				</dd>
				<dt class="sidebar-dt">
					<liferay-ui:message key="created" />
				</dt>
				<dd class="sidebar-dd">
					<liferay-ui:message arguments="<%= new Object[] {dateTimeFormat.format(fileEntry.getCreateDate()), HtmlUtil.escape(fileEntry.getUserName())} %>" key="x-by-x" translateArguments="<%= false %>" />
				</dd>
				<dt class="sidebar-dt">
					<liferay-ui:message key="expiration-date" />
				</dt>
				<dd class="sidebar-dd">
					<c:choose>
						<c:when test="<%= fileVersion.getExpirationDate() != null %>">
							<liferay-ui:message arguments="<%= new Object[] {dateTimeFormat.format(fileVersion.getExpirationDate()), HtmlUtil.escape(fileVersion.getUserName())} %>" key="x-by-x" translateArguments="<%= false %>" />
						</c:when>
						<c:otherwise>
							<liferay-ui:message key="never-expire" />
						</c:otherwise>
					</c:choose>
				</dd>
				<dt class="sidebar-dt">
					<liferay-ui:message key="review-date" />
				</dt>
				<dd class="sidebar-dd">
					<c:choose>
						<c:when test="<%= fileVersion.getReviewDate() != null %>">
							<liferay-ui:message arguments="<%= new Object[] {dateTimeFormat.format(fileVersion.getReviewDate()), HtmlUtil.escape(fileEntry.getUserName())} %>" key="x-by-x" translateArguments="<%= false %>" />
						</c:when>
						<c:otherwise>
							<liferay-ui:message key="never-review" />
						</c:otherwise>
					</c:choose>
				</dd>

				<%
				request.setAttribute("info_panel_location.jsp-parentFolder", fileEntry.getFolder());
				%>

				<liferay-util:include page="/document_library/info_panel_location.jsp" servletContext="<%= application %>" />

				<liferay-asset:asset-tags-available
					className="<%= DLFileEntryConstants.getClassName() %>"
					classPK="<%= assetClassPK %>"
				>
					<dt class="sidebar-dt">
						<liferay-ui:message key="tags" />
					</dt>
					<dd class="sidebar-dd">
						<liferay-asset:asset-tags-summary
							className="<%= DLFileEntryConstants.getClassName() %>"
							classPK="<%= assetClassPK %>"
						/>
					</dd>
				</liferay-asset:asset-tags-available>

				<c:if test="<%= dlPortletInstanceSettings.isEnableRatings() && fileEntry.isSupportsSocial() %>">
					<dt class="sidebar-dt">
						<liferay-ui:message key="ratings" />
					</dt>
					<dd class="sidebar-dd">
						<liferay-ratings:ratings
							className="<%= DLFileEntryConstants.getClassName() %>"
							classPK="<%= fileEntry.getFileEntryId() %>"
							inTrash="<%= fileEntry.isInTrash() %>"
						/>
					</dd>
				</c:if>

				<c:if test="<%= Validator.isNotNull(fileEntry.getDescription()) %>">
					<dt class="sidebar-dt">
						<liferay-ui:message key="description" />
					</dt>
					<dd class="sidebar-dd">
						<%= HtmlUtil.replaceNewLine(HtmlUtil.escape(fileVersion.getDescription())) %>
					</dd>
				</c:if>

				<liferay-asset:asset-categories-available
					className="<%= DLFileEntryConstants.getClassName() %>"
					classPK="<%= assetClassPK %>"
				>
					<dt class="sidebar-dt">
						<liferay-ui:message key="categories" />
					</dt>
					<dd class="sidebar-dd">
						<liferay-asset:asset-categories-summary
							className="<%= DLFileEntryConstants.getClassName() %>"
							classPK="<%= assetClassPK %>"
							displayStyle="simple-category"
						/>
					</dd>
				</liferay-asset:asset-categories-available>
			</dl>

			<%
			AssetEntry layoutAssetEntry = AssetEntryLocalServiceUtil.fetchEntry(DLFileEntryConstants.getClassName(), assetClassPK);
			%>

			<c:if test="<%= (layoutAssetEntry != null) && dlPortletInstanceSettings.isEnableRelatedAssets() && fileEntry.isSupportsSocial() %>">
				<liferay-asset:asset-links
					assetEntryId="<%= layoutAssetEntry.getEntryId() %>"
				/>
			</c:if>

			<clay:panel-group>
				<c:if test="<%= dlViewFileVersionDisplayContext.getDDMStructuresCount() > 0 %>">

					<%
					try {
						for (DDMStructure ddmStructure : dlViewFileVersionDisplayContext.getDDMStructures()) {
							DDMFormValues ddmFormValues = null;

							try {
								ddmFormValues = dlViewFileVersionDisplayContext.getDDMFormValues(ddmStructure);
							}
							catch (Exception e) {
							}
					%>

							<c:if test="<%= ddmFormValues != null %>">
								<clay:panel
									displayTitle="<%= HtmlUtil.escape(ddmStructure.getName(locale)) %>"
								>
									<div class="panel-body">
										<liferay-data-engine:data-layout-renderer
											containerId='<%= liferayPortletResponse.getNamespace() + "dataEngineLayoutRenderer" + ddmStructure.getStructureId() %>'
											dataDefinitionId="<%= ddmStructure.getStructureId() %>"
											dataRecordValues="<%= DataRecordValuesUtil.getDataRecordValues(ddmFormValues, ddmStructure) %>"
											namespace="<%= liferayPortletResponse.getNamespace() + ddmStructure.getStructureId() + StringPool.UNDERLINE %>"
											readOnly="<%= true %>"
										/>
									</div>
								</clay:panel>
							</c:if>

					<%
						}
					}
					catch (Exception e) {
					}
					%>

				</c:if>

				<liferay-expando:custom-attributes-available
					className="<%= DLFileEntryConstants.getClassName() %>"
					classPK="<%= fileVersion.getFileVersionId() %>"
					editable="<%= false %>"
				>
					<clay:panel
						displayTitle='<%= LanguageUtil.get(request, "custom-fields") %>'
					>
						<div class="panel-body">
							<liferay-expando:custom-attribute-list
								className="<%= DLFileEntryConstants.getClassName() %>"
								classPK="<%= fileVersion.getFileVersionId() %>"
								editable="<%= false %>"
								label="<%= true %>"
							/>
						</div>
					</clay:panel>
				</liferay-expando:custom-attributes-available>

				<%
				try {
					List<DDMStructure> ddmStructures = DDMStructureLocalServiceUtil.getClassStructures(company.getCompanyId(), PortalUtil.getClassNameId(RawMetadataProcessor.class), StructureStructureKeyComparator.getInstance(false));

					for (DDMStructure ddmStructure : ddmStructures) {
						DDMFormValues ddmFormValues = null;

						DLFileEntryMetadata fileEntryMetadata = DLFileEntryMetadataLocalServiceUtil.fetchFileEntryMetadata(ddmStructure.getStructureId(), fileVersion.getFileVersionId());

						if (fileEntryMetadata != null) {
							ddmFormValues = dlViewFileVersionDisplayContext.getDDMFormValues(fileEntryMetadata.getDDMStorageId());
						}
				%>

						<c:if test="<%= ddmFormValues != null %>">
							<clay:panel
								displayTitle='<%= LanguageUtil.get(request, "metadata." + ddmStructure.getStructureKey()) %>'
							>
								<div class="panel-body">
									<liferay-ddm:html
										classNameId="<%= PortalUtil.getClassNameId(com.liferay.dynamic.data.mapping.model.DDMStructure.class) %>"
										classPK="<%= ddmStructure.getPrimaryKey() %>"
										ddmFormValues="<%= ddmFormValues %>"
										fieldsNamespace="<%= String.valueOf(ddmStructure.getPrimaryKey()) %>"
										groupId="<%= fileVersion.getGroupId() %>"
										readOnly="<%= true %>"
										requestedLocale="<%= ddmFormValues.getDefaultLocale() %>"
										showEmptyFieldLabel="<%= false %>"
									/>
								</div>
							</clay:panel>
						</c:if>

				<%
					}
				}
				catch (Exception e) {
				}
				%>

			</clay:panel-group>
		</liferay-ui:section>

		<c:if test="<%= dlViewFileVersionDisplayContext.isVersionInfoVisible() %>">
			<liferay-ui:section>

				<%
				request.setAttribute("info_panel.jsp-fileEntry", fileEntry);
				%>

				<liferay-util:include page="/document_library/file_entry_history.jsp" servletContext="<%= application %>" />
			</liferay-ui:section>
		</c:if>
	</liferay-ui:tabs>
</div>

<liferay-frontend:component
	module="{InfoPanel} from document-library-web"
/>