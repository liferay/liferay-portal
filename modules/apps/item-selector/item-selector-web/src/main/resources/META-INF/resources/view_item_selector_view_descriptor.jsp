<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ItemSelectorViewDescriptorRendererDisplayContext itemSelectorViewDescriptorRendererDisplayContext = (ItemSelectorViewDescriptorRendererDisplayContext)request.getAttribute(ItemSelectorViewDescriptorRendererDisplayContext.class.getName());

ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor = itemSelectorViewDescriptorRendererDisplayContext.getItemSelectorViewDescriptor();
boolean listView = Objects.equals(itemSelectorViewDescriptorRendererDisplayContext.getDisplayStyle(), "list");
boolean multipleSelection = itemSelectorViewDescriptorRendererDisplayContext.isMultipleSelection();
SearchContainer<Object> searchContainer = itemSelectorViewDescriptorRendererDisplayContext.getSearchContainer();
%>

<c:if test="<%= itemSelectorViewDescriptor.isShowManagementToolbar() %>">
	<clay:management-toolbar
		managementToolbarDisplayContext="<%= new ItemSelectorViewDescriptorRendererManagementToolbarDisplayContext(itemSelectorViewDescriptorRendererDisplayContext, request, liferayPortletRequest, liferayPortletResponse, searchContainer) %>"
	/>
</c:if>

<clay:container-fluid
	cssClass="item-selector lfr-item-viewer"
	id='<%= liferayPortletResponse.getNamespace() + "entriesContainer" %>'
>
	<c:if test="<%= itemSelectorViewDescriptor.isShowBreadcrumb() %>">
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= itemSelectorViewDescriptorRendererDisplayContext.getBreadcrumbEntries(currentURLObj) %>"
		/>
	</c:if>

	<liferay-ui:search-container
		id="entries"
		searchContainer="<%= searchContainer %>"
		var="entriesSearch"
	>
		<liferay-ui:search-container-row
			ariaLabel='<%= multipleSelection ? StringPool.BLANK : LanguageUtil.get(request, "press-enter-to-select-the-item-and-close-the-modal") %>'
			className="Object"
			cssClass='<%= (listView && !multipleSelection) ? "entry entry-selector" : StringPool.BLANK %>'
			keyPropertyName="<%= itemSelectorViewDescriptor.getKeyProperty() %>"
			modelVar="entry"
			tabIndex='<%= multipleSelection ? StringPool.BLANK : "0" %>'
		>

			<%
			ItemSelectorViewDescriptor.ItemDescriptor itemDescriptor = itemSelectorViewDescriptor.getItemDescriptor(row.getObject());

			row.setData(
				HashMapBuilder.<String, Object>put(
					"value", itemDescriptor.getPayload()
				).build());
			%>

			<c:choose>
				<c:when test="<%= itemSelectorViewDescriptorRendererDisplayContext.isIconDisplayStyle() %>">
					<c:choose>
						<c:when test="<%= itemDescriptor.isCompact() %>">

							<%
							row.setCssClass("card-page-item card-page-item-directory entry " + row.getCssClass());

							HorizontalCard horizontalCard = itemDescriptor.getHorizontalCard(renderRequest, searchContainer.getRowChecker());

							if (horizontalCard == null) {
								BaseModel<?> baseModel = null;

								if (entry instanceof BaseModel) {
									baseModel = (BaseModel<?>)entry;
								}

								horizontalCard = new ItemDescriptorHorizontalCard(baseModel, itemDescriptor, renderRequest, searchContainer.getRowChecker());
							}
							%>

							<liferay-ui:search-container-column-text>
								<clay:horizontal-card
									aria-label='<%= LanguageUtil.format(request, "select-x", horizontalCard.getTitle()) %>'
									horizontalCard="<%= horizontalCard %>"
									role="button"
									tabIndex="0"
								/>
							</liferay-ui:search-container-column-text>
						</c:when>
						<c:otherwise>

							<%
							row.setCssClass("card-page-item card-page-item-asset entry " + row.getCssClass());

							VerticalCard verticalCard = itemDescriptor.getVerticalCard(renderRequest, searchContainer.getRowChecker());

							if (verticalCard == null) {
								BaseModel<?> baseModel = null;

								if (entry instanceof BaseModel) {
									baseModel = (BaseModel<?>)entry;
								}

								verticalCard = new ItemDescriptorVerticalCard(baseModel, itemDescriptor, renderRequest, searchContainer.getRowChecker());
							}
							%>

							<liferay-ui:search-container-column-text>
								<clay:vertical-card
									aria-label='<%= LanguageUtil.format(request, "select-x", verticalCard.getTitle()) %>'
									role="button"
									tabIndex="0"
									verticalCard="<%= verticalCard %>"
								/>
							</liferay-ui:search-container-column-text>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:when test="<%= itemSelectorViewDescriptorRendererDisplayContext.isDescriptiveDisplayStyle() %>">

					<%
					row.setCssClass("item-selector-list-row " + row.getCssClass());
					%>

					<c:if test="<%= itemDescriptor.getUserId() != UserConstants.USER_ID_DEFAULT %>">
						<liferay-ui:search-container-column-user
							showDetails="<%= false %>"
							userId="<%= itemDescriptor.getUserId() %>"
						/>
					</c:if>

					<c:if test="<%= Validator.isNotNull(itemDescriptor.getImageURL()) %>">
						<liferay-ui:search-container-column-image
							src="<%= itemDescriptor.getImageURL() %>"
						/>
					</c:if>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
						cssClass="entry"
					>
						<c:if test="<%= Objects.nonNull(itemDescriptor.getModifiedDate()) %>">

							<%
							Date modifiedDate = itemDescriptor.getModifiedDate();

							String modifiedDateDescription = LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - modifiedDate.getTime(), true);
							%>

							<c:choose>
								<c:when test="<%= Validator.isNotNull(itemDescriptor.getUserName()) %>">
									<span class="text-default">
										<liferay-ui:message arguments="<%= new String[] {itemDescriptor.getUserName(), modifiedDateDescription} %>" key="x-modified-x-ago" />
									</span>
								</c:when>
								<c:otherwise>
									<span class="text-default">
										<liferay-ui:message arguments="<%= modifiedDateDescription %>" key="modified-x-ago" />
									</span>
								</c:otherwise>
							</c:choose>
						</c:if>

						<p class="font-weight-bold h5">
							<%= HtmlUtil.escape(itemDescriptor.getTitle(locale)) %>
						</p>

						<p class="h6 text-default">
							<%= HtmlUtil.escape(itemDescriptor.getSubtitle(locale)) %>
						</p>

						<c:if test="<%= itemDescriptor.getStatus() != null %>">
							<span class="text-default">
								<aui:workflow-status markupView="lexicon" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= itemDescriptor.getStatus() %>" />
							</span>
						</c:if>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:otherwise>

					<%
					TableItemView tableItemView = itemSelectorViewDescriptor.getTableItemView(row.getObject());

					if (tableItemView == null) {
						tableItemView = new DefaultTableItemView(itemDescriptor);
					}

					searchContainer.setHeaderNames(tableItemView.getHeaderNames());

					for (SearchEntry searchEntry : tableItemView.getSearchEntries(locale)) {
						row.addSearchEntry(searchEntry);
					}
					%>

				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= itemSelectorViewDescriptorRendererDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
			resultRowSplitter="<%= itemSelectorViewDescriptor.getResultRowSplitter() %>"
			searchContainer="<%= searchContainer %>"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"itemSelectorReturnType", itemSelectorViewDescriptorRendererDisplayContext.getReturnType()
		).put(
			"itemSelectorSelectedEvent", HtmlUtil.escapeJS(itemSelectorViewDescriptorRendererDisplayContext.getItemSelectedEventName())
		).build()
	%>'
	module='<%= multipleSelection ? "{ViewItemSelectorViewDescriptorMultiple} from item-selector-web" : "{ViewItemSelectorViewDescriptor} from item-selector-web" %>'
/>