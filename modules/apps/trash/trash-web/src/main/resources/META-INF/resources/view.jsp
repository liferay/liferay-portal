<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
TrashManagementToolbarDisplayContext trashManagementToolbarDisplayContext = new TrashManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, trashDisplayContext);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= trashManagementToolbarDisplayContext %>"
	propsTransformer="{TrashManagementToolbarPropsTransformer} from trash-web"
/>

<liferay-util:include page="/restore_path.jsp" servletContext="<%= application %>" />

<div class="closed sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
	<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/trash/info_panel" var="sidebarPanelURL" />

	<liferay-frontend:sidebar-panel
		resourceURL="<%= sidebarPanelURL %>"
		searchContainerId="trash"
	>
		<liferay-util:include page="/info_panel.jsp" servletContext="<%= application %>" />
	</liferay-frontend:sidebar-panel>

	<clay:container-fluid
		cssClass="sidenav-content"
		size="xxxl"
	>
		<c:if test="<%= Validator.isNull(trashDisplayContext.getKeywords()) %>">
			<liferay-site-navigation:breadcrumb
				breadcrumbEntries="<%= trashDisplayContext.getPortletBreadcrumbEntries() %>"
			/>
		</c:if>

		<portlet:actionURL name="deleteEntries" var="deleteTrashEntriesURL">
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</portlet:actionURL>

		<aui:form action="<%= deleteTrashEntriesURL %>" name="fm">
			<liferay-ui:error exception="<%= RestoreEntryException.class %>">

				<%
				RestoreEntryException ree = (RestoreEntryException)errorException;
				%>

				<c:if test="<%= ree.getType() == RestoreEntryException.DUPLICATE %>">
					<liferay-ui:message key="unable-to-move-this-item-to-the-selected-destination" />
				</c:if>

				<c:if test="<%= ree.getType() == RestoreEntryException.INVALID_CONTAINER %>">
					<liferay-ui:message key="the-destination-you-selected-is-an-invalid-container.-please-select-a-different-destination" />
				</c:if>

				<c:if test="<%= ree.getType() == RestoreEntryException.INVALID_STATUS %>">
					<liferay-ui:message key="unable-to-restore-this-item" />
				</c:if>
			</liferay-ui:error>

			<liferay-ui:error exception="<%= TrashEntryException.class %>" message="unable-to-move-this-item-to-the-recycle-bin" />

			<liferay-ui:error exception="<%= TrashPermissionException.class %>">

				<%
				TrashPermissionException tpe = (TrashPermissionException)errorException;
				%>

				<c:if test="<%= tpe.getType() == TrashPermissionException.DELETE %>">
					<liferay-ui:message key="you-do-not-have-permission-to-delete-this-item" />
				</c:if>

				<c:if test="<%= tpe.getType() == TrashPermissionException.EMPTY_TRASH %>">
					<liferay-ui:message key="unable-to-completely-empty-trash-you-do-not-have-permission-to-delete-one-or-more-items" />
				</c:if>

				<c:if test="<%= tpe.getType() == TrashPermissionException.MOVE %>">
					<liferay-ui:message key="you-do-not-have-permission-to-move-this-item-to-the-selected-destination" />
				</c:if>

				<c:if test="<%= tpe.getType() == TrashPermissionException.RESTORE %>">
					<liferay-ui:message key="you-do-not-have-permission-to-restore-this-item" />
				</c:if>

				<c:if test="<%= tpe.getType() == TrashPermissionException.RESTORE_OVERWRITE %>">
					<liferay-ui:message key="you-do-not-have-permission-to-replace-an-existing-item-with-the-selected-one" />
				</c:if>

				<c:if test="<%= tpe.getType() == TrashPermissionException.RESTORE_RENAME %>">
					<liferay-ui:message key="you-do-not-have-permission-to-rename-this-item" />
				</c:if>
			</liferay-ui:error>

			<liferay-ui:search-container
				id="trash"
				searchContainer="<%= trashDisplayContext.getEntrySearch() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.trash.model.TrashEntry"
					keyPropertyName="entryId"
					modelVar="trashEntry"
					rowVar="row"
				>

					<%
					TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(trashEntry.getClassName());

					TrashRenderer trashRenderer = trashHandler.getTrashRenderer(trashEntry.getClassPK());

					String viewContentURLString = null;

					if (trashRenderer != null) {
						PortletURL viewContentURL = PortletURLBuilder.createRenderURL(
							renderResponse
						).setMVCPath(
							"/view_content.jsp"
						).setRedirect(
							currentURL
						).buildPortletURL();

						if (trashEntry.getRootEntry() != null) {
							viewContentURL.setParameter("classNameId", String.valueOf(trashEntry.getClassNameId()));
							viewContentURL.setParameter("classPK", String.valueOf(trashEntry.getClassPK()));
						}
						else {
							viewContentURL.setParameter("trashEntryId", String.valueOf(trashEntry.getEntryId()));
						}

						viewContentURLString = viewContentURL.toString();
					}

					row.setData(
						HashMapBuilder.<String, Object>put(
							"actions", trashManagementToolbarDisplayContext.getAvailableActions(trashEntry)
						).build());
					%>

					<c:choose>
						<c:when test="<%= trashDisplayContext.isDescriptiveView() %>">
							<liferay-ui:search-container-column-icon
								icon="<%= trashRenderer.getIconCssClass() %>"
								toggleRowChecker="<%= true %>"
							/>

							<liferay-ui:search-container-column-text
								colspan="<%= 2 %>"
							>
								<div class="h6 text-default">
									<liferay-ui:message arguments="<%= dateTimeFormat.format(trashEntry.getCreateDate()) %>" key="removed-x" />
								</div>

								<div class="h5">
									<aui:a href="<%= viewContentURLString %>">
										<%= HtmlUtil.escape(trashRenderer.getTitle(locale)) %>
									</aui:a>
								</div>

								<div class="h6 text-default">
									<strong><liferay-ui:message key="type" />:</strong> <%= ResourceActionsUtil.getModelResource(locale, trashEntry.getClassName()) %>
								</div>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text>
								<c:choose>
									<c:when test="<%= trashEntry.getRootEntry() == null %>">
										<clay:dropdown-actions
											aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
											dropdownItems="<%= trashDisplayContext.getTrashEntryActionDropdownItems(trashEntry) %>"
											propsTransformer="{EntriesPropsTransformer} from trash-web"
										/>
									</c:when>
									<c:otherwise>
										<clay:dropdown-actions
											aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
											dropdownItems="<%= trashDisplayContext.getTrashViewContentActionDropdownItems(trashRenderer.getClassName(), trashRenderer.getClassPK()) %>"
											propsTransformer="{EntriesPropsTransformer} from trash-web"
										/>
									</c:otherwise>
								</c:choose>
							</liferay-ui:search-container-column-text>
						</c:when>
						<c:when test="<%= trashDisplayContext.isIconView() %>">
							<liferay-ui:search-container-column-text>
								<clay:vertical-card
									propsTransformer="{EntriesPropsTransformer} from trash-web"
									verticalCard="<%= new TrashEntryVerticalCard(trashEntry, trashRenderer, liferayPortletResponse, renderRequest, searchContainer.getRowChecker(), viewContentURLString) %>"
								/>
							</liferay-ui:search-container-column-text>
						</c:when>
						<c:when test="<%= trashDisplayContext.isListView() %>">
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200 table-title"
								name="name"
							>
								<aui:a href="<%= viewContentURLString %>">
									<%= HtmlUtil.escape(trashRenderer.getTitle(locale)) %>
								</aui:a>

								<c:if test="<%= trashEntry.getRootEntry() != null %>">

									<%
									TrashEntry rootEntry = trashEntry.getRootEntry();

									TrashHandler rootTrashHandler = TrashHandlerRegistryUtil.getTrashHandler(rootEntry.getClassName());

									TrashRenderer rootTrashRenderer = rootTrashHandler.getTrashRenderer(rootEntry.getClassPK());

									String viewRootContentURLString = null;

									if (rootTrashRenderer != null) {
										viewRootContentURLString = PortletURLBuilder.createRenderURL(
											renderResponse
										).setMVCPath(
											"/view_content.jsp"
										).setRedirect(
											currentURL
										).setParameter(
											"trashEntryId", rootEntry.getEntryId()
										).buildString();
									}
									%>

									<liferay-util:buffer
										var="rootEntryIcon"
									>
										<clay:link
											href="<%= viewRootContentURLString %>"
											label="<%= HtmlUtil.escape(rootTrashRenderer.getTitle(locale)) %>"
										/>
									</liferay-util:buffer>

									<span class="trash-root-entry">(<liferay-ui:message arguments="<%= rootEntryIcon %>" key="<%= rootTrashHandler.getDeleteMessage() %>" translateArguments="<%= false %>" />)</span>
								</c:if>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smaller table-cell-minw-150"
								name="type"
								value="<%= ResourceActionsUtil.getModelResource(locale, trashEntry.getClassName()) %>"
							/>

							<liferay-ui:search-container-column-date
								cssClass="table-cell-expand-smaller table-cell-ws-nowrap"
								name="removed-date"
								value="<%= trashEntry.getCreateDate() %>"
							/>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smallest table-cell-minw-150"
								name="removed-by"
								value="<%= HtmlUtil.escape(trashEntry.getUserName()) %>"
							/>

							<liferay-ui:search-container-column-text>
								<c:choose>
									<c:when test="<%= trashEntry.getRootEntry() == null %>">
										<clay:dropdown-actions
											aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
											dropdownItems="<%= trashDisplayContext.getTrashEntryActionDropdownItems(trashEntry) %>"
											propsTransformer="{EntriesPropsTransformer} from trash-web"
										/>
									</c:when>
									<c:otherwise>
										<clay:dropdown-actions
											aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
											dropdownItems="<%= trashDisplayContext.getTrashViewContentActionDropdownItems(trashRenderer.getClassName(), trashRenderer.getClassPK()) %>"
											propsTransformer="{EntriesPropsTransformer} from trash-web"
										/>
									</c:otherwise>
								</c:choose>
							</liferay-ui:search-container-column-text>
						</c:when>
					</c:choose>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="<%= trashDisplayContext.getDisplayStyle() %>"
					markupView="lexicon"
					type='<%= trashDisplayContext.isApproximate() ? "more" : "regular" %>'
				/>
			</liferay-ui:search-container>
		</aui:form>
	</clay:container-fluid>
</div>