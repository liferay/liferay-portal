<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/repository_entry_browser/init.jsp" %>

<%
String randomNamespace = PortalUtil.generateRandomKey(request, "taglib_ui_repository_entry_browse_page") + StringPool.UNDERLINE;

String displayStyle = GetterUtil.getString(request.getAttribute("liferay-item-selector:repository-entry-browser:displayStyle"));
PortletURL editImageURL = (PortletURL)request.getAttribute("liferay-item-selector:repository-entry-browser:editImageURL");
String emptyResultsMessage = GetterUtil.getString(request.getAttribute("liferay-item-selector:repository-entry-browser:emptyResultsMessage"));
ItemSelectorReturnType existingFileEntryReturnType = (ItemSelectorReturnType)request.getAttribute("liferay-item-selector:repository-entry-browser:existingFileEntryReturnType");
List<String> extensions = (List)request.getAttribute("liferay-item-selector:repository-entry-browser:extensions");
long folderId = ParamUtil.getLong(request, "folderId", GetterUtil.getLong(request.getAttribute("liferay-item-selector:repository-entry-browser:folderId")));
String itemSelectedEventName = GetterUtil.getString(request.getAttribute("liferay-item-selector:repository-entry-browser:itemSelectedEventName"));
ItemSelectorReturnTypeResolver<?, FileEntry> itemSelectorReturnTypeResolver = (ItemSelectorReturnTypeResolver<?, FileEntry>)request.getAttribute("liferay-item-selector:repository-entry-browser:itemSelectorReturnTypeResolver");
long maxFileSize = GetterUtil.getLong(request.getAttribute("liferay-item-selector:repository-entry-browser:maxFileSize"));
String mimeTypeRestriction = GetterUtil.getString(request.getAttribute("liferay-item-selector:repository-entry-browser:mimeTypeRestriction"));
PortletURL portletURL = (PortletURL)request.getAttribute("liferay-item-selector:repository-entry-browser:portletURL");
List<FileEntry> repositoryEntries = (List<FileEntry>)request.getAttribute("liferay-item-selector:repository-entry-browser:repositoryEntries");
int repositoryEntriesCount = GetterUtil.getInteger(request.getAttribute("liferay-item-selector:repository-entry-browser:repositoryEntriesCount"));
boolean showBreadcrumb = GetterUtil.getBoolean(request.getAttribute("liferay-item-selector:repository-entry-browser:showBreadcrumb"));
boolean showDragAndDropZone = GetterUtil.getBoolean(request.getAttribute("liferay-item-selector:repository-entry-browser:showDragAndDropZone"));
boolean showSearch = GetterUtil.getBoolean(request.getAttribute("liferay-item-selector:repository-entry-browser:showSearch"));
String tabName = GetterUtil.getString(request.getAttribute("liferay-item-selector:repository-entry-browser:tabName"));
PortletURL uploadURL = (PortletURL)request.getAttribute("liferay-item-selector:repository-entry-browser:uploadURL");

String keywords = ParamUtil.getString(request, "keywords");

boolean showSearchInfo = false;

if (Validator.isNotNull(keywords)) {
	showSearchInfo = true;
}

String returnType = ItemSelectorRepositoryEntryBrowserUtil.getItemSelectorReturnTypeClassName(itemSelectorReturnTypeResolver, existingFileEntryReturnType);

if (uploadURL != null) {
	uploadURL.setParameter("returnType", returnType);
}
%>

<liferay-util:html-top
	outputKey="com.liferay.item.selector.taglib#/repository_entry_browser/page.jsp"
>
	<aui:link href='<%= PortalUtil.getPathProxy() + ServletContextUtil.getContextPath() + "/repository_entry_browser/css/main.css" %>' rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<%
RepositoryEntryBrowserDisplayContext repositoryEntryBrowserDisplayContext = new RepositoryEntryBrowserDisplayContext(request);

ItemSelectorRepositoryEntryManagementToolbarDisplayContext itemSelectorRepositoryEntryManagementToolbarDisplayContext = new ItemSelectorRepositoryEntryManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, repositoryEntryBrowserDisplayContext);

if (showDragAndDropZone) {
	emptyResultsMessage = null;
}

SearchContainer<?> searchContainer = new SearchContainer(renderRequest, itemSelectorRepositoryEntryManagementToolbarDisplayContext.getCurrentSortingURL(), null, emptyResultsMessage);
%>

<clay:management-toolbar
	clearResultsURL="<%= String.valueOf(itemSelectorRepositoryEntryManagementToolbarDisplayContext.getClearResultsURL()) %>"
	creationMenu="<%= itemSelectorRepositoryEntryManagementToolbarDisplayContext.getCreationMenu() %>"
	disabled="<%= itemSelectorRepositoryEntryManagementToolbarDisplayContext.isDisabled() %>"
	filterDropdownItems="<%= itemSelectorRepositoryEntryManagementToolbarDisplayContext.getFilterDropdownItems() %>"
	filterLabelItems="<%= itemSelectorRepositoryEntryManagementToolbarDisplayContext.getFilterLabelItems() %>"
	itemsTotal="<%= repositoryEntriesCount %>"
	orderDropdownItems="<%= itemSelectorRepositoryEntryManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
	propsTransformer="{ItemSelectorRepositoryEntryBrowserManagementToolbarPropsTransformer} from item-selector-taglib"
	searchActionURL="<%= String.valueOf(itemSelectorRepositoryEntryManagementToolbarDisplayContext.getSearchURL()) %>"
	searchFormMethod="POST"
	searchFormName="searchFm"
	selectable="<%= false %>"
	showCreationMenu="<%= showDragAndDropZone && itemSelectorRepositoryEntryManagementToolbarDisplayContext.isShowCreationMenu() %>"
	showInfoButton="<%= false %>"
	showSearch="<%= showSearch %>"
	sortingOrder="<%= itemSelectorRepositoryEntryManagementToolbarDisplayContext.getOrderByType() %>"
	sortingURL="<%= String.valueOf(itemSelectorRepositoryEntryManagementToolbarDisplayContext.getSortingURL()) %>"
	viewTypeItems="<%= itemSelectorRepositoryEntryManagementToolbarDisplayContext.getViewTypes() %>"
/>

<clay:container-fluid
	cssClass="item-selector lfr-item-viewer"
	id='<%= randomNamespace + "ItemSelectorContainer" %>'
>
	<c:if test="<%= showSearchInfo %>">
		<liferay-util:include page="/repository_entry_browser/search_info.jsp" servletContext="<%= application %>" />
	</c:if>

	<div class="message-container"></div>

	<c:if test="<%= showBreadcrumb && !showSearchInfo %>">

		<%
		ItemSelectorRepositoryEntryBrowserUtil.addPortletBreadcrumbEntries(folderId, displayStyle, request, liferayPortletRequest, liferayPortletResponse, PortletURLUtil.clone(portletURL, liferayPortletResponse));
		%>

		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
		/>
	</c:if>

	<c:choose>
		<c:when test="<%= showDragAndDropZone && !showSearchInfo %>">
			<div class="dropzone-wrapper <%= (repositoryEntriesCount == 0) ? "dropzone-wrapper-search-container-empty" : StringPool.BLANK %>">
	<div class="dropzone dropzone-disabled"><span aria-hidden="true" class="loading-animation loading-animation-sm"></span></div>
		</c:when>
		<c:otherwise>
			<div>
		</c:otherwise>
	</c:choose>
		<react:component
			data='<%=
				HashMapBuilder.<String, Object>put(
					"closeCaption", LanguageUtil.get(request, tabName)
				).put(
					"editImageURL",
					() -> {
						if (editImageURL != null) {
							return editImageURL.toString();
						}

						return null;
					}
				).put(
					"itemSelectedEventName", itemSelectedEventName
				).put(
					"maxFileSize", maxFileSize
				).put(
					"mimeTypeRestriction", mimeTypeRestriction
				).put(
					"rootNode", "#" + randomNamespace + "ItemSelectorContainer"
				).put(
					"uploaderEnabled", showDragAndDropZone && !showSearchInfo
				).put(
					"uploadItemReturnType", HtmlUtil.escapeAttribute(returnType)
				).put(
					"uploadItemURL",
					() -> {
						if (uploadURL != null) {
							return uploadURL.toString();
						}

						return null;
					}
				).put(
					"validExtensions", StringUtil.merge(extensions)
				).build()
			%>'
			module="{ItemSelectorRepositoryEntryBrowser} from item-selector-taglib"
		/>
	</div>

	<div>
		<react:component
			module="{ItemSelectorRepositoryEntryBrowserConfigureAIModal} from item-selector-taglib"
		/>
	</div>

	<c:if test="<%= (existingFileEntryReturnType != null) || (itemSelectorReturnTypeResolver != null) %>">
		<liferay-ui:search-container
			cssClass='<%= displayStyle.equals("list") ? "main-content-body mt-4" : StringPool.BLANK %>'
			searchContainer="<%= searchContainer %>"
			total="<%= repositoryEntriesCount %>"
			var="listSearchContainer"
		>
			<liferay-ui:search-container-results
				results="<%= repositoryEntries %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.repository.model.RepositoryEntry"
				modelVar="repositoryEntry"
			>
				<c:choose>
					<c:when test='<%= displayStyle.equals("list") %>'>

						<%
						FileEntry fileEntry = null;
						FileShortcut fileShortcut = null;
						Folder folder = null;

						if (repositoryEntry instanceof FileEntry) {
							fileEntry = (FileEntry)repositoryEntry;
						}
						else if (repositoryEntry instanceof FileShortcut) {
							fileShortcut = (FileShortcut)repositoryEntry;

							fileEntry = DLAppLocalServiceUtil.getFileEntry(fileShortcut.getToFileEntryId());
						}
						else {
							folder = (Folder)repositoryEntry;
						}
						%>

						<c:if test="<%= fileEntry != null %>">

							<%
							FileVersion latestFileVersion = fileEntry.getLatestFileVersion();

							String title = fileEntry.getTitle();

							JSONObject itemMedatadaJSONObject = ItemSelectorRepositoryEntryBrowserUtil.getItemMetadataJSONObject(fileEntry, locale);

							String thumbnailSrc = DLURLHelperUtil.getThumbnailSrc(fileEntry, themeDisplay);
							%>

							<liferay-ui:search-container-column-text
								name="title"
							>
								<div class="align-items-center d-flex">

									<%
									String iconCssClass = DLUtil.getFileIconCssClass(fileEntry.getExtension());
									%>

									<c:if test="<%= Validator.isNotNull(iconCssClass) %>">
										<liferay-ui:icon
											cssClass="c-mr-2 c-mt-1"
											icon="<%= iconCssClass %>"
											markupView="lexicon"
										/>
									</c:if>

									<a class="<%= repositoryEntryBrowserDisplayContext.isPreviewable(latestFileVersion) ? "item-preview-editable" : StringPool.BLANK %> item-preview" data-metadata="<%= HtmlUtil.escapeAttribute(itemMedatadaJSONObject.toString()) %>" data-mimeType="<%= HtmlUtil.escapeAttribute(fileEntry.getMimeType()) %>" data-returnType="<%= HtmlUtil.escapeAttribute(ItemSelectorRepositoryEntryBrowserUtil.getItemSelectorReturnTypeClassName(itemSelectorReturnTypeResolver, existingFileEntryReturnType)) %>" data-title="<%= HtmlUtil.escapeAttribute(title) %>" data-type="<%= repositoryEntryBrowserDisplayContext.getType(latestFileVersion) %>" data-url="<%= HtmlUtil.escapeAttribute(DLURLHelperUtil.getPreviewURL(fileEntry, latestFileVersion, themeDisplay, StringPool.BLANK)) %>" data-value="<%= HtmlUtil.escapeAttribute(ItemSelectorRepositoryEntryBrowserUtil.getValue(itemSelectorReturnTypeResolver, existingFileEntryReturnType, fileEntry, themeDisplay)) %>" href="<%= Validator.isNotNull(thumbnailSrc) ? HtmlUtil.escapeHREF(DLURLHelperUtil.getImagePreviewURL(fileEntry, themeDisplay)) : themeDisplay.getPathThemeImages() + "/file_system/large/default.png" %>">
										<span class="taglib-text text-truncate">
											<%= HtmlUtil.escape(title) %>
										</span>
									</a>

									<c:if test="<%= !repositoryEntryBrowserDisplayContext.hasGuestViewPermission(fileEntry) %>">
										<clay:icon
											aria-label='<%= LanguageUtil.get(request, "not-visible-to-guest-users") %>'
											cssClass="c-ml-2 c-mt-1 lfr-portal-tooltip text-4 text-secondary"
											data-title='<%= LanguageUtil.get(request, "not-visible-to-guest-users") %>'
											symbol="password-policies"
										/>
									</c:if>
								</div>
							</liferay-ui:search-container-column-text>

							<c:if test="<%= repositoryEntryBrowserDisplayContext.isSearchEverywhere() %>">
								<liferay-ui:search-container-column-text
									name="location"
								>
									<span class="text-secondary">
										<clay:icon
											symbol="<%= repositoryEntryBrowserDisplayContext.getGroupCssIcon(fileEntry.getGroupId()) %>"
										/>

										<small><%= repositoryEntryBrowserDisplayContext.getGroupLabel(fileEntry.getGroupId(), locale) %></small>
									</span>
								</liferay-ui:search-container-column-text>
							</c:if>

							<liferay-ui:search-container-column-text
								name="size"
								value="<%= LanguageUtil.formatStorageSize(fileEntry.getSize(), locale) %>"
							/>

							<liferay-ui:search-container-column-status
								name="status"
								status="<%= latestFileVersion.getStatus() %>"
							/>

							<liferay-ui:search-container-column-text
								name="modified-date"
							>
								<liferay-ui:message arguments="<%= new String[] {LanguageUtil.getTimeDescription(locale, System.currentTimeMillis() - fileEntry.getModifiedDate().getTime(), true), HtmlUtil.escape(fileEntry.getUserName())} %>" key="x-ago-by-x" translateArguments="<%= false %>" />
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text>
								<c:if test="<%= repositoryEntryBrowserDisplayContext.isPreviewable(latestFileVersion) %>">
									<liferay-util:include page="/repository_entry_browser/action_button_preview.jsp" servletContext="<%= application %>" />
								</c:if>
							</liferay-ui:search-container-column-text>
						</c:if>

						<c:if test="<%= folder != null %>">

							<%
							PortletURL viewFolderURL = EntryURLUtil.getFolderPortletURL(folder, liferayPortletRequest, liferayPortletResponse, portletURL);
							%>

							<liferay-ui:search-container-column-text
								name="title"
							>
								<a href="<%= HtmlUtil.escapeHREF(viewFolderURL.toString()) %>" title="<%= HtmlUtil.escapeAttribute(folder.getName()) %>">
									<liferay-ui:icon
										icon="folder"
										markupView="lexicon"
									/>

									<span class="taglib-text">
										<%= HtmlUtil.escape(folder.getName()) %>
									</span>
								</a>
							</liferay-ui:search-container-column-text>

							<c:if test="<%= repositoryEntryBrowserDisplayContext.isSearchEverywhere() %>">
								<liferay-ui:search-container-column-text
									name="location"
								>
									<span class="text-secondary">
										<clay:icon
											symbol="<%= repositoryEntryBrowserDisplayContext.getGroupCssIcon(folder.getGroupId()) %>"
										/>

										<small><%= repositoryEntryBrowserDisplayContext.getGroupLabel(folder.getGroupId(), locale) %></small>
									</span>
								</liferay-ui:search-container-column-text>
							</c:if>

							<liferay-ui:search-container-column-text
								name="size"
								value="--"
							/>

							<liferay-ui:search-container-column-text
								name="status"
								value="--"
							/>

							<liferay-ui:search-container-column-text
								name="modified-date"
							>
								<liferay-ui:message arguments="<%= new String[] {LanguageUtil.getTimeDescription(locale, System.currentTimeMillis() - folder.getModifiedDate().getTime(), true), HtmlUtil.escape(folder.getUserName())} %>" key="x-ago-by-x" translateArguments="<%= false %>" />
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								value="--"
							/>
						</c:if>
					</c:when>
					<c:otherwise>

						<%
						FileEntry fileEntry = null;
						Folder folder = null;

						if (repositoryEntry instanceof FileEntry) {
							fileEntry = (FileEntry)repositoryEntry;
						}
						else if (repositoryEntry instanceof FileShortcut) {
							FileShortcut fileShortcut = (FileShortcut)repositoryEntry;

							fileEntry = DLAppLocalServiceUtil.getFileEntry(fileShortcut.getToFileEntryId());
						}
						else {
							folder = (Folder)repositoryEntry;
						}
						%>

						<c:choose>
							<c:when test='<%= displayStyle.equals("icon") %>'>

								<%
								row.setCssClass("card-page-item card-page-item-directory");
								%>

								<c:if test="<%= folder != null %>">

									<%
									PortletURL viewFolderURL = EntryURLUtil.getFolderPortletURL(folder, liferayPortletRequest, liferayPortletResponse, portletURL);
									%>

									<liferay-ui:search-container-column-text
										colspan="<%= 3 %>"
									>
										<div class="card card-horizontal card-interactive card-interactive-secondary">
											<div class="card-body">
												<div class="card-row">
													<clay:content-col>
														<clay:sticker
															displayType="secondary"
															icon="folder"
															inline="<%= true %>"
														/>
													</clay:content-col>

													<div class="autofit-col autofit-col-expand autofit-col-gutters">
														<p class="card-title text-truncate">
															<aui:a href="<%= viewFolderURL.toString() %>" title="<%= HtmlUtil.escapeAttribute(folder.getName()) %>">
																<%= HtmlUtil.escape(folder.getName()) %>
															</aui:a>
														</p>

														<c:if test="<%= repositoryEntryBrowserDisplayContext.isSearchEverywhere() %>">
															<span class="text-secondary">
																<clay:icon
																	symbol="<%= repositoryEntryBrowserDisplayContext.getGroupCssIcon(folder.getGroupId()) %>"
																/>

																<small><%= repositoryEntryBrowserDisplayContext.getGroupLabel(folder.getGroupId(), locale) %></small>
															</span>
														</c:if>
													</div>
												</div>
											</div>
										</div>
									</liferay-ui:search-container-column-text>
								</c:if>

								<c:if test="<%= fileEntry != null %>">

									<%
									FileVersion latestFileVersion = fileEntry.getLatestFileVersion();

									String title = fileEntry.getTitle();

									JSONObject itemMedatadaJSONObject = ItemSelectorRepositoryEntryBrowserUtil.getItemMetadataJSONObject(fileEntry, locale);

									Map<String, Object> data = HashMapBuilder.<String, Object>put(
										"description", fileEntry.getDescription()
									).put(
										"fileEntryId", fileEntry.getFileEntryId()
									).build();

									String thumbnailSrc = DLURLHelperUtil.getThumbnailSrc(fileEntry, themeDisplay);

									if (Validator.isNotNull(thumbnailSrc)) {
										data.put("href", DLURLHelperUtil.getImagePreviewURL(fileEntry, themeDisplay));
									}
									else {
										data.put("href", themeDisplay.getPathThemeImages() + "/file_system/large/default.png");
									}

									data.put("metadata", itemMedatadaJSONObject.toString());
									data.put("mimeType", fileEntry.getMimeType());
									data.put("returnType", ItemSelectorRepositoryEntryBrowserUtil.getItemSelectorReturnTypeClassName(itemSelectorReturnTypeResolver, existingFileEntryReturnType));
									data.put("title", title);
									data.put("type", repositoryEntryBrowserDisplayContext.getType(latestFileVersion));
									data.put("url", DLURLHelperUtil.getPreviewURL(fileEntry, latestFileVersion, themeDisplay, StringPool.BLANK));
									data.put("value", ItemSelectorRepositoryEntryBrowserUtil.getValue(itemSelectorReturnTypeResolver, existingFileEntryReturnType, fileEntry, themeDisplay));

									String cssClass = "form-check form-check-card item-preview";

									if (repositoryEntryBrowserDisplayContext.isPreviewable(latestFileVersion)) {
										cssClass += " item-preview-editable";
									}

									if (Validator.isNull(thumbnailSrc)) {
										cssClass += " file-card";
									}
									else {
										cssClass += " image-card";
									}
									%>

									<liferay-ui:search-container-column-text>
										<div class="card-type-asset <%= cssClass %>" <%= AUIUtil.buildData(data) %>>
											<div class="card card-interactive">
												<div class="aspect-ratio card-item-first">
													<c:choose>
														<c:when test="<%= Validator.isNull(thumbnailSrc) %>">
															<span class="aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
																<clay:icon
																	symbol="documents-and-media"
																/>
															</span>
														</c:when>
														<c:otherwise>
															<img alt="" class="aspect-ratio-item-center-middle aspect-ratio-item-fluid" src="<%= thumbnailSrc %>" />
														</c:otherwise>
													</c:choose>

													<liferay-document-library:mime-type-sticker
														cssClass="sticker-bottom-left sticker-secondary"
														fileVersion="<%= latestFileVersion %>"
													/>
												</div>

												<div class="card-body">
													<div class="card-row">
														<div class="autofit-col autofit-col-expand">
															<aui:a cssClass="card-title text-truncate" href="" onClick="" title="<%= HtmlUtil.escapeAttribute(title) %>">
																<%= HtmlUtil.escape(title) %>
															</aui:a>

															<c:if test="<%= !repositoryEntryBrowserDisplayContext.hasGuestViewPermission(fileEntry) %>">
																<clay:icon
																	aria-label='<%= LanguageUtil.get(request, "not-visible-to-guest-users") %>'
																	cssClass="c-ml-2 c-mt-1 lfr-portal-tooltip text-4 text-secondary"
																	data-title='<%= LanguageUtil.get(request, "not-visible-to-guest-users") %>'
																	symbol="password-policies"
																/>
															</c:if>

															<div class="card-detail">
																<c:if test="<%= repositoryEntryBrowserDisplayContext.isSearchEverywhere() %>">
																	<span class="text-secondary">
																		<clay:icon
																			symbol="<%= repositoryEntryBrowserDisplayContext.getGroupCssIcon(fileEntry.getGroupId()) %>"
																		/>

																		<small><%= repositoryEntryBrowserDisplayContext.getGroupLabel(fileEntry.getGroupId(), locale) %></small>
																	</span>
																</c:if>
															</div>
														</div>

														<c:if test="<%= repositoryEntryBrowserDisplayContext.isPreviewable(latestFileVersion) %>">
															<div class="autofit-col">
																<liferay-util:include page="/repository_entry_browser/action_button_preview.jsp" servletContext="<%= application %>" />
															</div>
														</c:if>
													</div>
												</div>
											</div>
										</div>
									</liferay-ui:search-container-column-text>
								</c:if>
							</c:when>
							<c:otherwise>
								<c:if test="<%= folder != null %>">

									<%
									PortletURL viewFolderURL = EntryURLUtil.getFolderPortletURL(folder, liferayPortletRequest, liferayPortletResponse, portletURL);
									%>

									<liferay-ui:search-container-column-icon
										icon="folder"
									/>

									<liferay-ui:search-container-column-text
										colspan="<%= 3 %>"
									>
										<div class="h5">
											<a href="<%= HtmlUtil.escapeAttribute(viewFolderURL.toString()) %>" title="<%= folder.getName() %>">
												<strong><%= HtmlUtil.escape(folder.getName()) %></strong>
											</a>
										</div>

										<c:if test="<%= repositoryEntryBrowserDisplayContext.isSearchEverywhere() %>">
											<div class="h6 text-default">
												<liferay-ui:message key="location" />:
												<span class="text-secondary">
													<clay:icon
														symbol="<%= repositoryEntryBrowserDisplayContext.getGroupCssIcon(folder.getGroupId()) %>"
													/>

													<small><%= repositoryEntryBrowserDisplayContext.getGroupLabel(folder.getGroupId(), locale) %></small>
												</span>
											</div>
										</c:if>

										<div class="h6 text-default">
											<liferay-ui:message key="created" />:

											<liferay-ui:message arguments="<%= new String[] {LanguageUtil.getTimeDescription(locale, System.currentTimeMillis() - folder.getCreateDate().getTime(), true), HtmlUtil.escape(folder.getUserName())} %>" key="x-ago-by-x" translateArguments="<%= false %>" />
										</div>
									</liferay-ui:search-container-column-text>
								</c:if>

								<c:if test="<%= fileEntry != null %>">

									<%
									row.setCssClass("item-selector-list-row " + row.getCssClass());

									FileVersion latestFileVersion = fileEntry.getLatestFileVersion();

									String title = fileEntry.getTitle();

									JSONObject itemMedatadaJSONObject = ItemSelectorRepositoryEntryBrowserUtil.getItemMetadataJSONObject(fileEntry, locale);

									String thumbnailSrc = DLURLHelperUtil.getThumbnailSrc(fileEntry, themeDisplay);
									%>

									<c:choose>
										<c:when test="<%= Validator.isNotNull(thumbnailSrc) %>">
											<liferay-ui:search-container-column-image
												src="<%= DLURLHelperUtil.getThumbnailSrc(fileEntry, themeDisplay) %>"
											/>
										</c:when>
										<c:otherwise>
											<liferay-ui:search-container-column-text>
												<liferay-document-library:mime-type-sticker
													fileVersion="<%= latestFileVersion %>"
												/>
											</liferay-ui:search-container-column-text>
										</c:otherwise>
									</c:choose>

									<liferay-ui:search-container-column-text
										colspan="<%= 2 %>"
									>
										<div class="<%= repositoryEntryBrowserDisplayContext.isPreviewable(latestFileVersion) ? "item-preview-editable" : StringPool.BLANK %> item-preview" data-href="<%= Validator.isNotNull(thumbnailSrc) ? HtmlUtil.escapeHREF(DLURLHelperUtil.getImagePreviewURL(fileEntry, themeDisplay)) : themeDisplay.getPathThemeImages() + "/file_system/large/default.png" %>" data-metadata="<%= HtmlUtil.escapeAttribute(itemMedatadaJSONObject.toString()) %>" data-mimeType="<%= HtmlUtil.escapeAttribute(fileEntry.getMimeType()) %>" data-returnType="<%= HtmlUtil.escapeAttribute(ItemSelectorRepositoryEntryBrowserUtil.getItemSelectorReturnTypeClassName(itemSelectorReturnTypeResolver, existingFileEntryReturnType)) %>" data-title="<%= HtmlUtil.escapeAttribute(title) %>" data-type="<%= repositoryEntryBrowserDisplayContext.getType(latestFileVersion) %>" data-url="<%= HtmlUtil.escapeAttribute(DLURLHelperUtil.getPreviewURL(fileEntry, latestFileVersion, themeDisplay, StringPool.BLANK)) %>" data-value="<%= HtmlUtil.escapeAttribute(ItemSelectorRepositoryEntryBrowserUtil.getValue(itemSelectorReturnTypeResolver, existingFileEntryReturnType, fileEntry, themeDisplay)) %>">
											<div class="h5">
												<strong><%= title %></strong>

												<c:if test="<%= !repositoryEntryBrowserDisplayContext.hasGuestViewPermission(fileEntry) %>">
													<clay:icon
														aria-label='<%= LanguageUtil.get(request, "not-visible-to-guest-users") %>'
														cssClass="c-ml-2 c-mt-1 lfr-portal-tooltip text-4 text-secondary"
														data-title='<%= LanguageUtil.get(request, "not-visible-to-guest-users") %>'
														symbol="password-policies"
													/>
												</c:if>
											</div>

											<c:if test="<%= repositoryEntryBrowserDisplayContext.isSearchEverywhere() %>">
												<div class="h6 text-default">
													<liferay-ui:message key="location" />:
													<span class="text-secondary">
														<clay:icon
															symbol="<%= repositoryEntryBrowserDisplayContext.getGroupCssIcon(fileEntry.getGroupId()) %>"
														/>

														<small><%= repositoryEntryBrowserDisplayContext.getGroupLabel(fileEntry.getGroupId(), locale) %></small>
													</span>
												</div>
											</c:if>

											<div class="h6 text-default">
												<liferay-ui:message key="version" />:

												<%= String.valueOf(fileEntry.getVersion()) %>
											</div>

											<div class="h6 text-default">
												<liferay-ui:message key="last-updated" />:

												<liferay-ui:message arguments="<%= new String[] {LanguageUtil.getTimeDescription(locale, System.currentTimeMillis() - fileEntry.getModifiedDate().getTime(), true), HtmlUtil.escape(latestFileVersion.getUserName())} %>" key="x-ago-by-x" translateArguments="<%= false %>" />
											</div>
										</div>
									</liferay-ui:search-container-column-text>

									<liferay-ui:search-container-column-text>
										<c:if test="<%= repositoryEntryBrowserDisplayContext.isPreviewable(latestFileVersion) %>">
											<liferay-util:include page="/repository_entry_browser/action_button_preview.jsp" servletContext="<%= application %>" />
										</c:if>
									</liferay-ui:search-container-column-text>
								</c:if>
							</c:otherwise> </c:choose>
					</c:otherwise>
				</c:choose>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= displayStyle %>"
				markupView="lexicon"
				resultRowSplitter="<%= new RepositoryEntryResultRowSplitter() %>"
				searchContainer="<%= searchContainer %>"
			/>
		</liferay-ui:search-container>

		<c:if test="<%= !showSearchInfo && (uploadURL != null) %>">
			<div class="drop-here-info">
				<div class="drop-here-indicator">
					<div class="drop-icons">
						<clay:icon
							cssClass="drop-icon"
							symbol="picture"
						/>

						<clay:icon
							cssClass="drop-icon"
							symbol="picture"
						/>

						<clay:icon
							cssClass="drop-icon"
							symbol="picture"
						/>
					</div>

					<div class="drop-text">
						<liferay-ui:message key="drop-files-here" />
					</div>
				</div>
			</div>
		</c:if>
	</c:if>

	<div class="item-selector-preview-container"></div>
</clay:container-fluid>