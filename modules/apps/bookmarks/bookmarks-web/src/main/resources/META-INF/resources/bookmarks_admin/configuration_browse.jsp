<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/bookmarks/init.jsp" %>

<%
BookmarksConfigurationDisplayContext bookmarksConfigurationDisplayContext = new BookmarksConfigurationDisplayContext(request, renderRequest, renderResponse);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(bookmarksConfigurationDisplayContext.getBackURL());
portletDisplay.setURLBackTitle("bookmarks");
%>

<clay:container-fluid
	cssClass="container-form-lg"
>
	<clay:row>
		<clay:col
			lg="3"
		>
			<p class="c-mb-1 sheet-tertiary-title text-2 text-secondary">
				<liferay-ui:message key="settings" />
			</p>

			<clay:vertical-nav
				verticalNavItems="<%= bookmarksConfigurationDisplayContext.getSettingsVerticalNavItemList() %>"
			/>

			<p class="c-mb-1 sheet-tertiary-title text-2 text-secondary">
				<liferay-ui:message key="notifications" />
			</p>

			<clay:vertical-nav
				verticalNavItems="<%= bookmarksConfigurationDisplayContext.getNotificationsVerticalNavItemList() %>"
			/>
		</clay:col>

		<clay:col
			lg="9"
		>
			<h2>
				<%= bookmarksConfigurationDisplayContext.getTitle() %>
			</h2>

			<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL">
				<portlet:param name="navigation" value="<%= bookmarksConfigurationDisplayContext.getNavigation() %>" />
				<portlet:param name="serviceName" value="<%= BookmarksConstants.SERVICE_NAME %>" />
				<portlet:param name="settingsScope" value="group" />
			</liferay-portlet:actionURL>

			<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
				<clay:sheet
					cssClass="c-mb-4 c-mt-4 c-p-0"
					size="full"
				>
					<h3 class="c-pl-4 c-pr-4 c-pt-4 sheet-title">
						<clay:content-row
							verticalAlign="center"
						>
							<clay:content-col>
								<%= bookmarksConfigurationDisplayContext.getSubtitle() %>
							</clay:content-col>
						</clay:content-row>
					</h3>

					<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
					<aui:input name="redirect" type="hidden" value="<%= bookmarksConfigurationDisplayContext.getRedirect() %>" />

					<liferay-ui:error key="emailEntryAddedBody" message="please-enter-a-valid-body" />
					<liferay-ui:error key="emailEntryAddedSubject" message="please-enter-a-valid-subject" />
					<liferay-ui:error key="emailEntryUpdatedBody" message="please-enter-a-valid-body" />
					<liferay-ui:error key="emailEntryUpdatedSubject" message="please-enter-a-valid-subject" />

					<%
					Map<String, String> emailDefinitionTerms = BookmarksUtil.getEmailDefinitionTerms(renderRequest, bookmarksGroupServiceConfiguration.emailFromAddress(), bookmarksGroupServiceConfiguration.emailFromName());
					%>

					<c:choose>
						<c:when test='<%= Objects.equals(bookmarksConfigurationDisplayContext.getNavigation(), "display-settings") %>'>
							<div class="c-px-4">
								<aui:input name="preferences--folderColumns--" type="hidden" />
								<aui:input name="preferences--entryColumns--" type="hidden" />

								<liferay-frontend:resource-selector
									inputLabel='<%= LanguageUtil.get(request, "root-folder") %>'
									inputName="preferences--rootFolderId--"
									modalTitle='<%= LanguageUtil.get(request, "select-folder") %>'
									resourceName="<%= rootFolderName %>"
									resourceValue="<%= String.valueOf(rootFolderId) %>"
									selectEventName="selectFolder"
									selectResourceURL='<%=
										PortletURLBuilder.create(
											PortalUtil.getControlPanelPortletURL(request, themeDisplay.getScopeGroup(), BookmarksPortletKeys.BOOKMARKS_ADMIN, 0, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE)
										).setMVCRenderCommandName(
											"/bookmarks/select_folder"
										).setPortletResource(
											portletResource
										).setWindowState(
											LiferayWindowState.POP_UP
										).buildString()
									%>'
									showRemoveButton="<%= true %>"
									warningMessage='<%= rootFolderInTrash ? LanguageUtil.get(request, "the-selected-root-folder-is-in-the-recycle-bin-please-remove-it-or-select-another-one") : null %>'
								/>

								<aui:input label="show-search" name="preferences--showFoldersSearch--" type="checkbox" value="<%= bookmarksGroupServiceConfiguration.showFoldersSearch() %>" />

								<aui:input name="preferences--showSubfolders--" type="checkbox" value="<%= bookmarksGroupServiceConfiguration.showSubfolders() %>" />

								<aui:field-wrapper label="show-columns">

									<%

									// Left list

									List<KeyValuePair> leftList = new ArrayList<>();

									Set<String> availableFolderColumns = SetUtil.fromArray(StringUtil.split(allFolderColumns));

									String[] sortedFolderColumns = ArrayUtil.clone(folderColumns);

									Arrays.sort(sortedFolderColumns);

									for (String folderColumn : availableFolderColumns) {
										if (Arrays.binarySearch(sortedFolderColumns, folderColumn) < 0) {
											leftList.add(new KeyValuePair(folderColumn, LanguageUtil.get(request, folderColumn)));
										}
									}

									leftList = ListUtil.sort(leftList, new KeyValuePairComparator(false, true));

									// Right list

									List<KeyValuePair> rightList = new ArrayList<>();

									for (String folderColumn : folderColumns) {
										rightList.add(new KeyValuePair(folderColumn, LanguageUtil.get(request, folderColumn)));
									}
									%>

									<liferay-ui:input-move-boxes
										leftBoxName="availableFolderColumns"
										leftList="<%= leftList %>"
										leftTitle="available"
										rightBoxName="currentFolderColumns"
										rightList="<%= rightList %>"
										rightReorder="<%= Boolean.TRUE.toString() %>"
										rightTitle="in-use"
									/>
								</aui:field-wrapper>
							</div>
						</c:when>
						<c:when test='<%= Objects.equals(bookmarksConfigurationDisplayContext.getNavigation(), "entry-added-email") %>'>
							<div class="c-px-4">
								<liferay-frontend:email-notification-settings
									emailBodyLocalizedValuesMap="<%= bookmarksGroupServiceConfiguration.emailEntryAddedBody() %>"
									emailDefinitionTerms="<%= emailDefinitionTerms %>"
									emailEnabled="<%= bookmarksGroupServiceConfiguration.emailEntryAddedEnabled() %>"
									emailParam="emailEntryAdded"
									emailSubjectLocalizedValuesMap="<%= bookmarksGroupServiceConfiguration.emailEntryAddedSubject() %>"
								/>
							</div>
						</c:when>
						<c:when test='<%= Objects.equals(bookmarksConfigurationDisplayContext.getNavigation(), "entry-updated-email") %>'>
							<div class="c-px-4">
								<liferay-frontend:email-notification-settings
									emailBodyLocalizedValuesMap="<%= bookmarksGroupServiceConfiguration.emailEntryUpdatedBody() %>"
									emailDefinitionTerms="<%= emailDefinitionTerms %>"
									emailEnabled="<%= bookmarksGroupServiceConfiguration.emailEntryUpdatedEnabled() %>"
									emailParam="emailEntryUpdated"
									emailSubjectLocalizedValuesMap="<%= bookmarksGroupServiceConfiguration.emailEntryUpdatedSubject() %>"
								/>
							</div>
						</c:when>
						<c:otherwise>
							<div class="c-px-4">
								<liferay-frontend:fieldset>
									<aui:input cssClass="lfr-input-text-container" label="name" name="preferences--emailFromName--" value="<%= bookmarksGroupServiceConfiguration.emailFromName() %>">
										<aui:validator errorMessage="please-enter-a-valid-name" name="required" />
									</aui:input>

									<aui:input cssClass="lfr-input-text-container" label="address" name="preferences--emailFromAddress--" value="<%= bookmarksGroupServiceConfiguration.emailFromAddress() %>">
										<aui:validator errorMessage="please-enter-a-valid-email-address" name="required" />
										<aui:validator name="email" />
									</aui:input>
								</liferay-frontend:fieldset>
							</div>
						</c:otherwise>
					</c:choose>
				</clay:sheet>

				<c:if test='<%= Objects.equals(bookmarksConfigurationDisplayContext.getNavigation(), "display-settings") %>'>
					<clay:sheet
						cssClass="c-mb-4 c-p-0 mt-4"
						size="full"
					>
						<h3 class="c-pl-4 c-pr-4 c-pt-4 sheet-title">
							<clay:content-row
								verticalAlign="center"
							>
								<clay:content-col>
									<liferay-ui:message key="bookmarks-listing" />
								</clay:content-col>
							</clay:content-row>
						</h3>

						<div class="c-px-4">
							<aui:input label="show-related-assets" name="preferences--enableRelatedAssets--" type="checkbox" value="<%= bookmarksGroupServiceConfiguration.enableRelatedAssets() %>" />

							<aui:field-wrapper label="show-columns">

								<%

								// Left list

								List<KeyValuePair> leftList = new ArrayList<>();

								Set<String> availableEntryColumns = SetUtil.fromArray(StringUtil.split(allEntryColumns));

								String[] sortedEntryColumns = ArrayUtil.clone(entryColumns);

								Arrays.sort(sortedEntryColumns);

								for (String entryColumn : availableEntryColumns) {
									if (Arrays.binarySearch(sortedEntryColumns, entryColumn) < 0) {
										leftList.add(new KeyValuePair(entryColumn, LanguageUtil.get(request, entryColumn)));
									}
								}

								leftList = ListUtil.sort(leftList, new KeyValuePairComparator(false, true));

								// Right list

								List<KeyValuePair> rightList = new ArrayList<>();

								for (String entryColumn : entryColumns) {
									rightList.add(new KeyValuePair(entryColumn, LanguageUtil.get(request, entryColumn)));
								}
								%>

								<liferay-ui:input-move-boxes
									leftBoxName="availableEntryColumns"
									leftList="<%= leftList %>"
									leftTitle="available"
									rightBoxName="currentEntryColumns"
									rightList="<%= rightList %>"
									rightReorder="<%= Boolean.TRUE.toString() %>"
									rightTitle="in-use"
								/>
							</aui:field-wrapper>
						</div>
					</clay:sheet>
				</c:if>

				<aui:button-row>
					<div class="c-gap-1 d-flex">
						<clay:button
							cssClass="c-mr-2"
							label="save"
							propsTransformer="{SaveConfigurationButtonPropsTransformer} from bookmarks-web"
						/>

						<clay:link
							displayType="secondary"
							href="<%= bookmarksConfigurationDisplayContext.getBackURL() %>"
							label="cancel"
							type="button"
						/>
					</div>
				</aui:button-row>
			</aui:form>
		</clay:col>
	</clay:row>
</clay:container-fluid>