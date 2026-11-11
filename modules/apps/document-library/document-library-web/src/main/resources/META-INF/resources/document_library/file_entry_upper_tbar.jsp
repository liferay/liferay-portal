<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
DLViewFileEntryDisplayContext dlViewFileEntryDisplayContext = (DLViewFileEntryDisplayContext)request.getAttribute(DLViewFileEntryDisplayContext.class.getName());

FileEntry fileEntry = dlViewFileEntryDisplayContext.getFileEntry();
FileVersion fileVersion = dlViewFileEntryDisplayContext.getFileVersion();
%>

<nav class="component-tbar subnav-tbar-light tbar upper-tbar" role="navigation">
	<clay:container-fluid
		fullWidth="<%= true %>"
	>
		<ul class="tbar-nav">
			<li class="tbar-item tbar-item-expand">
				<div class="tbar-section text-left">
					<h2 class="text-truncate-inline upper-tbar-title" title="<%= HtmlUtil.escapeAttribute(dlViewFileEntryDisplayContext.getDocumentTitle()) %>">
						<span class="text-truncate"><%= HtmlUtil.escape(dlViewFileEntryDisplayContext.getDocumentTitle()) %></span>
					</h2>

					<c:if test="<%= fileEntry.hasLock() || fileEntry.isCheckedOut() %>">
						<span class="lfr-portal-tooltip" title="<%= LanguageUtil.get(request, "locked-document") %>">
							<clay:icon
								cssClass="inline-item inline-item-after state-icon"
								symbol="lock"
							/>
						</span>
					</c:if>

					<c:if test="<%= dlViewFileEntryDisplayContext.isShared() %>">
						<clay:icon
							cssClass="inline-item inline-item-after lfr-portal-tooltip state-icon"
							symbol="users"
						/>
					</c:if>
				</div>
			</li>
			<li class="tbar-item">
				<clay:button
					borderless="<%= true %>"
					data-qa-id="infoButton"
					displayType="secondary"
					icon="info-circle-open"
					id='<%= liferayPortletResponse.getNamespace() + "OpenContextualSidebar" %>'
					small="<%= true %>"
					title='<%= LanguageUtil.get(resourceBundle, "info") %>'
				/>
			</li>

			<c:if test="<%= dlViewFileEntryDisplayContext.isSharingLinkVisible() %>">
				<li class="d-none d-sm-flex tbar-item">
					<liferay-sharing:button
						className="<%= DLFileEntryConstants.getClassName() %>"
						classPK="<%= fileEntry.getFileEntryId() %>"
					/>
				</li>
			</c:if>

			<c:if test="<%= dlViewFileEntryDisplayContext.isDownloadLinkVisible() %>">
				<li class="d-none d-sm-flex tbar-item">
					<clay:link
						data-analytics-external-reference-code="<%= fileEntry.getExternalReferenceCode() %>"
						data-analytics-file-entry-id="<%= fileEntry.getFileEntryId() %>"
						data-analytics-file-entry-title="<%= fileEntry.getTitle() %>"
						data-analytics-file-entry-version="<%= fileEntry.getVersion() %>"
						displayType="primary"
						href="<%= DLURLHelperUtil.getDownloadURL(fileEntry, fileVersion, themeDisplay, StringPool.BLANK, true, true, false) %>"
						icon="download"
						label="download"
						small="<%= true %>"
						title='<%= LanguageUtil.format(resourceBundle, "file-size-x", LanguageUtil.formatStorageSize(fileVersion.getSize(), locale), false) %>'
						type="button"
					/>
				</li>
			</c:if>

			<li class="tbar-item">
				<clay:dropdown-actions
					aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
					dropdownItems="<%= dlViewFileEntryDisplayContext.getActionDropdownItems() %>"
					propsTransformer="{DLFileEntryDropdownPropsTransformer} from document-library-web"
				/>
			</li>
		</ul>
	</clay:container-fluid>
</nav>