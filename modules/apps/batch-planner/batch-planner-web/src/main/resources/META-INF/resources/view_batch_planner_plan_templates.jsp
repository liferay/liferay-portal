<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
BatchPlannerPlanTemplateDisplayContext batchPlannerPlanTemplateDisplayContext = (BatchPlannerPlanTemplateDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

SearchContainer<BatchPlannerPlanTemplateDisplay> batchPlannerPlanTemplateDisplaySearchContainer = batchPlannerPlanTemplateDisplayContext.getSearchContainer();

BatchPlannerPlanTemplateManagementToolbarDisplayContext batchPlannerPlanTemplateManagementToolbarDisplayContext = new BatchPlannerPlanTemplateManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, batchPlannerPlanTemplateDisplaySearchContainer);
%>

<clay:navigation-bar
	navigationItems="<%= batchPlannerPlanTemplateDisplayContext.getNavigationItems() %>"
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= batchPlannerPlanTemplateManagementToolbarDisplayContext %>"
	propsTransformer="{BatchPlannerPlanTemplateManagementToolbarPropsTransformer} from batch-planner-web"
/>

<clay:container-fluid>
	<liferay-ui:error exception="<%= BatchPlannerPlanInternalClassNameException.class %>" message="unable-to-perform-the-search-because-the-provided-search-term-is-too-ambiguous" />

	<aui:form method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="batchPlannerPlanIds" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= batchPlannerPlanTemplateDisplaySearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.batch.planner.web.internal.display.BatchPlannerPlanTemplateDisplay"
				keyPropertyName="batchPlannerPlanId"
				modelVar="batchPlannerPlanTemplateDisplay"
			>

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", StringUtil.merge(batchPlannerPlanTemplateManagementToolbarDisplayContext.getAvailableActions(), StringPool.COMMA)
					).build());
				%>

				<liferay-ui:search-container-column-text
					cssClass="code"
					name="id"
					value="<%= String.valueOf(batchPlannerPlanTemplateDisplay.getBatchPlannerPlanId()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="font-weight-bold"
					name="name"
					value="<%= HtmlUtil.escape(batchPlannerPlanTemplateDisplay.getTitle()) %>"
				/>

				<liferay-ui:search-container-column-text
					name="create-date"
					value="<%= dateTimeFormat.format(batchPlannerPlanTemplateDisplay.getCreateDate()) %>"
				/>

				<liferay-ui:search-container-column-text
					name="action"
					value='<%= LanguageUtil.get(request, batchPlannerPlanTemplateDisplay.isExport() ? "export" : "import") %>'
				/>

				<liferay-ui:search-container-column-text
					name="type"
					value="<%= HtmlUtil.escape(batchPlannerPlanTemplateDisplayContext.getSimpleClassName(batchPlannerPlanTemplateDisplay.getInternalClassNameKey())) %>"
				/>

				<liferay-ui:search-container-column-text
					name="format"
					value="<%= HtmlUtil.escape(batchPlannerPlanTemplateDisplay.getExternalType()) %>"
				/>

				<liferay-ui:search-container-column-text
					name="user"
					value="<%= HtmlUtil.escape(batchPlannerPlanTemplateDisplay.getUserName()) %>"
				/>

				<liferay-ui:search-container-column-jsp
					cssClass="entry-action-column"
					path="/batch_planner_plan_template_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>