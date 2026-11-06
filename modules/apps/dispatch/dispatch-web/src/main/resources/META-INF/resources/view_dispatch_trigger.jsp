<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DispatchTriggerDisplayContext dispatchTriggerDisplayContext = (DispatchTriggerDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

PortletURL portletURL = PortletURLBuilder.create(
	dispatchTriggerDisplayContext.getPortletURL()
).setTabs1(
	"dispatch-trigger"
).setParameter(
	"searchContainerId", "dispatchTriggers"
).buildPortletURL();
%>

<clay:navigation-bar
	navigationItems="<%= dispatchTriggerDisplayContext.getNavigationItems() %>"
/>

<liferay-util:include page="/dispatch_trigger_toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="searchContainerId" value="dispatchTriggers" />
</liferay-util:include>

<div id="<portlet:namespace />dispatchTriggerContainer">
	<div class="closed container" id="<portlet:namespace />infoPanelId">
		<aui:form action="<%= portletURL %>" method="post" name="fm">
			<aui:input name="<%= Constants.CMD %>" type="hidden" />
			<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />

			<liferay-ui:search-container
				id="dispatchTriggers"
				searchContainer="<%= dispatchTriggerDisplayContext.getSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.dispatch.model.DispatchTrigger"
					keyPropertyName="dispatchTriggerId"
					modelVar="dispatchTrigger"
				>
					<liferay-ui:search-container-column-text
						cssClass="font-weight-bold important table-cell-expand"
						href='<%=
							PortletURLBuilder.createRenderURL(
								renderResponse
							).setMVCRenderCommandName(
								"/dispatch/edit_dispatch_trigger"
							).setRedirect(
								currentURL
							).setParameter(
								"dispatchTriggerId", dispatchTrigger.getDispatchTriggerId()
							).buildPortletURL()
						%>'
						name="name"
						value="<%= HtmlUtil.escape(dispatchTrigger.getName()) %>"
					/>

					<liferay-ui:search-container-column-text
						name="task-executor-type"
						property="dispatchTaskExecutorType"
						translate="<%= true %>"
					/>

					<liferay-ui:search-container-column-text
						name="system"
						value='<%= dispatchTrigger.isSystem() ? LanguageUtil.get(request, "yes") : LanguageUtil.get(request, "no") %>'
					/>

					<liferay-ui:search-container-column-date
						name="create-date"
						property="createDate"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-ws-nowrap"
						name="next-fire-date"
						value="<%= dispatchTriggerDisplayContext.getNextFireDateString(dispatchTrigger) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="font-weight-bold important table-cell-ws-nowrap"
						name="status"
					>

						<%
						DispatchTaskStatus dispatchTaskStatus = dispatchTrigger.getDispatchTaskStatus();
						%>

						<div class="background-task-status-row background-task-status-<%= dispatchTaskStatus.getLabel() %> h6 <%= dispatchTaskStatus.getCssClass() %>">
							<liferay-ui:message key="<%= dispatchTaskStatus.getLabel() %>" />
						</div>
					</liferay-ui:search-container-column-text>

					<%
					DispatchTriggerMetadata dispatchTriggerMetadata = dispatchTriggerDisplayContext.getDispatchTriggerMetadata(dispatchTrigger.getDispatchTriggerId());
					%>

					<c:choose>
						<c:when test="<%= dispatchTriggerMetadata.isDispatchTaskExecutorReady() %>">
							<liferay-ui:search-container-column-jsp
								cssClass="table-cell-ws-nowrap"
								path="/trigger/buttons.jsp"
							/>
						</c:when>
						<c:otherwise>
							<liferay-ui:search-container-column-text
								cssClass="font-weight-bold important table-cell-ws-nowrap"
							>
								<div class="background-task-status-row h6 text-warning">
									<liferay-ui:message key="incomplete" />
								</div>
							</liferay-ui:search-container-column-text>
						</c:otherwise>
					</c:choose>

					<liferay-ui:search-container-column-jsp
						cssClass="entry-action-column"
						path="/dispatch_trigger_action.jsp"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="list"
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</div>
</div>