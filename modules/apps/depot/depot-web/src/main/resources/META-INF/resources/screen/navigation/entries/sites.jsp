<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DepotAdminSitesDisplayContext depotAdminSitesDisplayContext = new DepotAdminSitesDisplayContext(liferayPortletRequest, liferayPortletResponse);

List<DepotEntryGroupRel> depotEntryGroupRels = depotAdminSitesDisplayContext.getDepotEntryGroupRels();
%>

<clay:sheet-section>
	<clay:content-row
		containerElement="h3"
		cssClass="sheet-subtitle"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<span class="heading-text"><liferay-ui:message key="connected-sites" /></span>
		</clay:content-col>

		<c:if test="<%= !depotAdminSitesDisplayContext.isLiveDepotEntry() %>">
			<clay:content-col>
				<span class="heading-end">

					<%
					PortletURL itemSelectorURL = depotAdminSitesDisplayContext.getItemSelectorURL();

					String itemSelectorURLString = itemSelectorURL.toString();
					%>

					<clay:button
						additionalProps='<%=
							HashMapBuilder.<String, Object>put(
								"currentURL", currentURL
							).put(
								"itemSelectorURL", itemSelectorURLString
							).build()
						%>'
						displayType="secondary"
						id='<%= liferayPortletResponse.getNamespace() + "addConnectedSiteButton" %>'
						label="add"
						propsTransformer="{AddConnectedSitesButtonPropsTransformer} from depot-web"
						small="<%= true %>"
						title="connect-to-a-site"
					/>
				</span>
			</clay:content-col>
		</c:if>
	</clay:content-row>

	<liferay-ui:error exception="<%= DepotEntryGroupRelToGroupException.MustAcceptDepotEntryType.class %>">
		<liferay-ui:message key="this-connection-is-not-allowed" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= DepotEntryGroupRelToGroupException.MustBeLocallyStaged.class %>">
		<liferay-ui:message key="a-locally-staged-asset-library-cannot-be-connected-to-a-remotely-staged-site" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= DepotEntryGroupRelToGroupException.MustBeRemotelyStaged.class %>">
		<liferay-ui:message key="a-remotely-staged-asset-library-cannot-be-connected-to-a-locally-staged-site" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= DepotEntryGroupRelToGroupException.MustBeStaged.class %>">
		<liferay-ui:message key="a-staged-asset-library-cannot-be-connected-to-an-unstaged-site" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= DepotEntryGroupRelToGroupException.MustNotBeStaged.class %>">
		<liferay-ui:message key="an-unstaged-asset-library-cannot-be-connected-to-a-staged-site" />
	</liferay-ui:error>

	<c:if test="<%= depotAdminSitesDisplayContext.isLiveDepotEntry() %>">
		<clay:alert
			displayType="info"
			message="this-is-a-live-asset-library.-site-connections-must-be-managed-from-the-staging-one"
		/>
	</c:if>

	<aui:input name="toGroupId" type="hidden" />

	<liferay-ui:search-container
		compactEmptyResultsMessage="<%= true %>"
		cssClass="lfr-search-container-connected-sites"
		emptyResultsMessage="no-sites-are-connected-yet"
		headerNames="title,null"
		id="connectedSitesSearchContainer"
		iteratorURL="<%= currentURLObj %>"
		total="<%= depotEntryGroupRels.size() %>"
	>
		<liferay-ui:search-container-results
			calculateStartAndEnd="<%= true %>"
			results="<%= depotEntryGroupRels %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.depot.model.DepotEntryGroupRel"
			keyProperty="depotEntryGroupRelId"
			modelVar="depotEntryGroupRel"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
			>
				<%= HtmlUtil.escape(depotAdminSitesDisplayContext.getSiteName(depotEntryGroupRel)) %>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				helpMessage="shows-the-asset-library-content-in-search-results"
				name="searchable-content"
			>
				<liferay-ui:message key='<%= depotEntryGroupRel.isSearchable() ? "yes" : "no" %>' />
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				helpMessage="makes-the-asset-library-web-content-structures-and-document-types-available-in-the-site"
				name="structures-and-types"
			>
				<liferay-ui:message key='<%= depotEntryGroupRel.isDdmStructuresAvailable() ? "yes" : "no" %>' />
			</liferay-ui:search-container-column-text>

			<c:if test="<%= !depotAdminSitesDisplayContext.isLiveDepotEntry() %>">
				<liferay-ui:search-container-column-text>
					<clay:dropdown-menu
						borderless="<%= true %>"
						displayType="secondary"
						dropdownItems="<%= depotAdminSitesDisplayContext.getConnectedSiteDropdownItems(depotEntryGroupRel) %>"
						icon="ellipsis-v"
						monospaced="<%= true %>"
						propsTransformer="{ConnectedSiteDropdownPropsTransformer} from depot-web"
						small="<%= true %>"
					/>
				</liferay-ui:search-container-column-text>
			</c:if>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:sheet-section>