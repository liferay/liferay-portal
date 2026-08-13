<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
portletDisplay.setBeta(true);

BatchPlannerPlanDisplayContext batchPlannerPlanDisplayContext = (BatchPlannerPlanDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

SearchContainer<BatchPlannerPlanDisplay> batchPlannerPlanDisplaySearchContainer = batchPlannerPlanDisplayContext.getSearchContainer();
%>

<clay:navigation-bar
	navigationItems="<%= batchPlannerPlanDisplayContext.getNavigationItems() %>"
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new BatchPlannerPlanManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, batchPlannerPlanDisplaySearchContainer) %>"
/>

<clay:container-fluid>
	<liferay-ui:error exception="<%= BatchPlannerPlanInternalClassNameException.class %>" message="unable-to-perform-the-search-because-the-provided-search-term-is-too-ambiguous" />

	<liferay-ui:search-container
		cssClass="mt-3"
		searchContainer="<%= batchPlannerPlanDisplaySearchContainer %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.batch.planner.web.internal.display.BatchPlannerPlanDisplay"
			keyProperty="batchPlannerPlanId"
			modelVar="batchPlannerPlanDisplay"
		>
			<liferay-ui:search-container-column-text
				cssClass="font-weight-bold important table-cell-expand"
				href='<%=
					PortletURLBuilder.createRenderURL(
						renderResponse
					).setMVCRenderCommandName(
						"/batch_planner/view_batch_planner_plan"
					).setRedirect(
						currentURL
					).setParameter(
						"batchPlannerPlanId", batchPlannerPlanDisplay.getBatchPlannerPlanId()
					).buildPortletURL()
				%>'
				name="name"
				value="<%= HtmlUtil.escape(batchPlannerPlanDisplay.getTitle()) %>"
			/>

			<liferay-ui:search-container-column-text
				name="action"
				value="<%= LanguageUtil.get(request, batchPlannerPlanDisplay.getAction()) %>"
			/>

			<liferay-ui:search-container-column-text
				name="type"
				value="<%= batchPlannerPlanDisplayContext.getSimpleClassName(batchPlannerPlanDisplay.getInternalClassNameKey()) %>"
			/>

			<liferay-ui:search-container-column-text
				name="creation-date"
				value="<%= dateTimeFormat.format(batchPlannerPlanDisplay.getCreateDate()) %>"
			/>

			<liferay-ui:search-container-column-text
				name="author"
				value="<%= PortalUtil.getUserEmailAddress(batchPlannerPlanDisplay.getUserId()) %>"
			/>

			<liferay-ui:search-container-column-text
				name="status"
			>
				<div class="h6 text-uppercase <%= BatchPlannerPlanConstants.getStatusCssClass(batchPlannerPlanDisplay.getStatus()) %>">
					<liferay-ui:message key="<%= BatchPlannerPlanConstants.getStatusLabel(batchPlannerPlanDisplay.getStatus()) %>" />
				</div>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="successful-rows"
				value="<%= String.valueOf(batchPlannerPlanDisplay.getProcessedItemsCount()) %>"
			/>

			<liferay-ui:search-container-column-text
				name="failed-rows"
				value="<%= String.valueOf(batchPlannerPlanDisplay.getFailedItemsCount()) %>"
			/>

			<liferay-ui:search-container-column-text
				name="total"
				value="<%= String.valueOf(batchPlannerPlanDisplay.getTotalItemsCount()) %>"
			/>

			<liferay-ui:search-container-column-jsp
				cssClass="entry-action-column"
				path="/batch_planner_plan_action.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>