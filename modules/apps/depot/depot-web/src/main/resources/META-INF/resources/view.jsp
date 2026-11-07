<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DepotAdminDisplayContext depotAdminDisplayContext = (DepotAdminDisplayContext)request.getAttribute(DepotAdminDisplayContext.class.getName());

DepotAdminManagementToolbarDisplayContext depotAdminManagementToolbarDisplayContext = new DepotAdminManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, depotAdminDisplayContext);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= depotAdminManagementToolbarDisplayContext %>"
	propsTransformer="{DepotAdminManagementToolbarPropsTransformer} from depot-web"
/>

<div class="closed sidenav-container sidenav-right">
	<clay:container-fluid
		cssClass="sidenav-content"
	>
		<liferay-ui:error exception="<%= DepotEntryStagedException.class %>" message="cannot-delete-a-staged-asset-library.-unstage-the-asset-library-and-try-again" />
		<liferay-ui:error exception="<%= RequiredFileEntryTypeException.class %>" message="cannot-delete-a-document-type-that-is-presently-used-by-one-or-more-documents-in-a-connected-site" />

		<portlet:actionURL name="deleteGroups" var="deleteGroupsURL" />

		<aui:form action="<%= depotAdminDisplayContext.getIteratorURL() %>" name="fm">
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

			<liferay-ui:search-container
				id="<%= depotAdminDisplayContext.getSearchContainerId() %>"
				searchContainer="<%= depotAdminDisplayContext.searchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.depot.model.DepotEntry"
					escapedModel="<%= true %>"
					keyProperty="depotEntryId"
					rowIdProperty="depotEntryId"
				>

					<%
					DepotEntry depotEntry = depotAdminDisplayContext.getDepotEntry((DepotEntry)row.getObject());

					Group depotEntryGroup = depotEntry.getGroup();

					row.setData(depotAdminManagementToolbarDisplayContext.getRowData(depotEntry));
					%>

					<c:choose>
						<c:when test="<%= depotAdminDisplayContext.isDisplayStyleDescriptive() %>">
							<liferay-ui:search-container-column-text>
								<liferay-ui:search-container-column-icon
									icon="books"
									toggleRowChecker="<%= true %>"
								/>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								colspan="<%= 2 %>"
							>
								<div class="h5">
									<aui:a cssClass="selector-button" href="<%= depotAdminDisplayContext.getViewDepotURL(depotEntry) %>">
										<%= HtmlUtil.escape(depotEntryGroup.getDescriptiveName(locale)) %>
									</aui:a>
								</div>

								<div class="h6">

									<%
									int depotEntryConnectedGroupsCount = depotAdminDisplayContext.getDepotEntryConnectedGroupsCount(depotEntry);
									%>

									<liferay-ui:message arguments="<%= depotEntryConnectedGroupsCount %>" key='<%= (depotEntryConnectedGroupsCount != 1) ? "x-connected-sites" : "x-connected-site" %>' />
								</div>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text>
								<clay:dropdown-actions
									aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
									dropdownItems="<%= depotAdminDisplayContext.getActionDropdownItems(depotEntry) %>"
									propsTransformer="{DepotEntryDropdownPropsTransformer} from depot-web"
								/>
							</liferay-ui:search-container-column-text>
						</c:when>
						<c:when test="<%= depotAdminDisplayContext.isDisplayStyleIcon() %>">
							<liferay-ui:search-container-column-text>
								<clay:vertical-card
									verticalCard="<%= depotAdminDisplayContext.getDepotEntryVerticalCard(depotEntry) %>"
								/>
							</liferay-ui:search-container-column-text>
						</c:when>
						<c:otherwise>
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200 table-title"
								name="name"
								orderable="<%= true %>"
							>
								<aui:a href="<%= depotAdminDisplayContext.getViewDepotURL(depotEntry) %>" label="<%= HtmlUtil.escape(depotEntryGroup.getDescriptiveName(locale)) %>" localizeLabel="<%= false %>" />
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200"
								name="num-of-connections"
								value="<%= String.valueOf(depotAdminDisplayContext.getDepotEntryConnectedGroupsCount(depotEntry)) %>"
							/>

							<liferay-ui:search-container-column-text>
								<clay:dropdown-actions
									aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
									dropdownItems="<%= depotAdminDisplayContext.getActionDropdownItems(depotEntry) %>"
									propsTransformer="{DepotEntryDropdownPropsTransformer} from depot-web"
								/>
							</liferay-ui:search-container-column-text>
						</c:otherwise>
					</c:choose>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="<%= depotAdminDisplayContext.getDisplayStyle() %>"
					markupView="lexicon"
					searchContainer="<%= searchContainer %>"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</clay:container-fluid>
</div>

<liferay-frontend:component
	componentId="<%= DepotAdminWebKeys.DEPOT_ENTRY_DROPDOWN_DEFAULT_EVENT_HANDLER %>"
	module="{DepotEntryDropdownDefaultEventHandler} from depot-web"
/>