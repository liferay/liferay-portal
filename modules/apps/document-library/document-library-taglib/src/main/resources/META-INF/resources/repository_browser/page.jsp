<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/repository_browser/init.jsp" %>

<%
RepositoryBrowserTagDisplayContext repositoryBrowserTagDisplayContext = (RepositoryBrowserTagDisplayContext)request.getAttribute(RepositoryBrowserTagDisplayContext.class.getName());
%>

<clay:management-toolbar
	additionalProps="<%= repositoryBrowserTagDisplayContext.getAdditionalProps() %>"
	managementToolbarDisplayContext="<%= repositoryBrowserTagDisplayContext.getManagementToolbarDisplayContext() %>"
	propsTransformer="{RepositoryBrowserManagementToolbarPropsTransformer} from document-library-taglib"
/>

<clay:container-fluid>
	<liferay-ui:error exception="<%= DuplicateFileEntryException.class %>" message="please-enter-a-unique-name" />
	<liferay-ui:error exception="<%= DuplicateFolderNameException.class %>" message="please-enter-a-unique-name" />

	<liferay-site-navigation:breadcrumb
		breadcrumbEntries="<%= repositoryBrowserTagDisplayContext.getBreadcrumbEntries() %>"
		skipEntryContributors="<%= repositoryBrowserTagDisplayContext.isStandaloneBreadcrumb() %>"
	/>

	<input class="hide" id="<portlet:namespace />file" type="file" />

	<liferay-ui:search-container
		id="repositoryEntries"
		searchContainer="<%= repositoryBrowserTagDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.repository.model.RepositoryEntry"
			keyProperty="primaryKey"
			modelVar="repositoryEntry"
		>
			<c:choose>
				<c:when test="<%= repositoryBrowserTagDisplayContext.isDescriptiveDisplayStyle() %>">
					<c:choose>
						<c:when test="<%= !repositoryBrowserTagDisplayContext.isRepositoryEntryThumbnailAvailable(repositoryEntry) %>">
							<liferay-ui:search-container-column-icon
								icon="<%= repositoryBrowserTagDisplayContext.getRepositoryEntryIcon(repositoryEntry) %>"
							/>
						</c:when>
						<c:otherwise>
							<liferay-ui:search-container-column-image
								src="<%= repositoryBrowserTagDisplayContext.getRepositoryEntryThumbnailSrc(repositoryEntry) %>"
							/>
						</c:otherwise>
					</c:choose>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<c:choose>
							<c:when test="<%= repositoryBrowserTagDisplayContext.isRepositoryEntryNavigable(repositoryEntry) %>">
								<p class="font-weight-bold h5">
									<aui:a href="<%= repositoryBrowserTagDisplayContext.getRepositoryEntryURL(repositoryEntry) %>">
										<%= HtmlUtil.escape(repositoryBrowserTagDisplayContext.getRepositoryEntryTitle(repositoryEntry)) %>
									</aui:a>
								</p>
							</c:when>
							<c:otherwise>
								<p class="font-weight-bold h5">
									<%= HtmlUtil.escape(repositoryBrowserTagDisplayContext.getRepositoryEntryTitle(repositoryEntry)) %>
								</p>
							</c:otherwise>
						</c:choose>

						<span class="text-default">
							<liferay-ui:message arguments="<%= repositoryBrowserTagDisplayContext.getRepositoryEntryModifiedDateDescription(repositoryEntry) %>" key="modified-x-ago" />
						</span>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= repositoryBrowserTagDisplayContext.getActionDropdownItems(repositoryEntry) %>"
							propsTransformer="{RepositoryBrowserDropdownPropsTransformer} from document-library-taglib"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test="<%= repositoryBrowserTagDisplayContext.isIconDisplayStyle() %>">
					<liferay-ui:search-container-column-text>
						<c:choose>
							<c:when test="<%= repositoryBrowserTagDisplayContext.isVerticalCard(repositoryEntry) %>">
								<clay:vertical-card
									verticalCard="<%= repositoryBrowserTagDisplayContext.getVerticalCard(repositoryEntry) %>"
								/>
							</c:when>
							<c:otherwise>
								<clay:horizontal-card
									horizontalCard="<%= repositoryBrowserTagDisplayContext.getHorizontalCard(repositoryEntry) %>"
									translated="<%= false %>"
								/>
							</c:otherwise>
						</c:choose>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:otherwise>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200 table-title"
						name="title"
					>
						<%= HtmlUtil.escape(repositoryBrowserTagDisplayContext.getRepositoryEntryTitle(repositoryEntry)) %>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smaller"
						name="size"
						value="<%= repositoryBrowserTagDisplayContext.getRepositoryEntrySizeValue(repositoryEntry) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smaller table-cell-minw-150"
						name="modified-date"
					>
						<liferay-ui:message arguments="<%= repositoryBrowserTagDisplayContext.getRepositoryEntryModifiedDateDescription(repositoryEntry) %>" key="modified-x-ago" />
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= repositoryBrowserTagDisplayContext.getActionDropdownItems(repositoryEntry) %>"
							propsTransformer="{RepositoryBrowserDropdownPropsTransformer} from document-library-taglib"
						/>
					</liferay-ui:search-container-column-text>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= repositoryBrowserTagDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
			resultRowSplitter="<%= repositoryBrowserTagDisplayContext.getResultRowSplitter() %>"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>

<div>
	<liferay-frontend:component
		componentId="repositoryBrowserEventHandler"
		module="{RepositoryBrowserDropdownDefaultEventHandler} from document-library-taglib"
	/>

	<liferay-frontend:component
		context="<%= repositoryBrowserTagDisplayContext.getRepositoryBrowserComponentContext() %>"
		module="{RepositoryBrowserComponent} from document-library-taglib"
	/>
</div>