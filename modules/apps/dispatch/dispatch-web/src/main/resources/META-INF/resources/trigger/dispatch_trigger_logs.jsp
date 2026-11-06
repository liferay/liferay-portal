<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DispatchLogDisplayContext dispatchLogDisplayContext = (DispatchLogDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

DispatchTrigger dispatchTrigger = dispatchLogDisplayContext.getDispatchTrigger();

PortletURL portletURL = PortletURLBuilder.create(
	dispatchLogDisplayContext.getPortletURL()
).setParameter(
	"searchContainerId", "dispatchLogs"
).buildPortletURL();

request.setAttribute("view.jsp-portletURL", portletURL);

SearchContainer<DispatchLog> dispatchLogSearchContainer = DispatchLogSearchContainerFactory.create(liferayPortletRequest, liferayPortletResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new ViewDispatchLogManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, dispatchLogSearchContainer) %>"
	propsTransformer="{DispatchLogManagementToolbarPropsTransformer} from dispatch-web"
/>

<div id="<portlet:namespace />triggerLogsContainer">
	<div class="closed container-fluid container-fluid-max-xl" id="<portlet:namespace />infoPanelId">
		<aui:form action="<%= portletURL %>" method="post" name="fm">
			<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.DELETE %>" />
			<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
			<aui:input name="deleteDispatchLogIds" type="hidden" />

			<div class="trigger-lists-container" id="<portlet:namespace />entriesContainer">
				<liferay-ui:search-container
					id="dispatchLogs"
					searchContainer="<%= dispatchLogSearchContainer %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.dispatch.model.DispatchLog"
						keyPropertyName="dispatchLogId"
						modelVar="dispatchLog"
					>
						<liferay-ui:search-container-column-text
							cssClass="font-weight-bold important table-cell-expand"
							href='<%=
								PortletURLBuilder.createRenderURL(
									renderResponse
								).setMVCRenderCommandName(
									"/dispatch/view_dispatch_log"
								).setRedirect(
									currentURL
								).setParameter(
									"dispatchLogId", dispatchLog.getDispatchLogId()
								).setParameter(
									"dispatchTriggerId", dispatchTrigger.getDispatchTriggerId()
								).buildPortletURL()
							%>'
							name="start-date"
						>
							<%= fastDateTimeFormat.format(dispatchLog.getStartDate()) %>
						</liferay-ui:search-container-column-text>

						<%
						Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(FastDateFormatConstants.SHORT, FastDateFormatConstants.LONG, locale, TimeZone.getTimeZone(dispatchTrigger.getTimeZoneId()));
						%>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="scheduled-start-date"
						>
							<%= dateTimeFormat.format(dispatchLog.getStartDate()) %>
						</liferay-ui:search-container-column-text>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="runtime"
						>
							<%= (dispatchLog.getEndDate() == null) ? StringPool.DASH : String.valueOf(dispatchLog.getEndDate().getTime() - dispatchLog.getStartDate().getTime()) + " ms" %>
						</liferay-ui:search-container-column-text>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="trigger"
							value="<%= HtmlUtil.escape(dispatchTrigger.getName()) %>"
						/>

						<liferay-ui:search-container-column-text
							name="status"
						>

							<%
							DispatchTaskStatus dispatchTaskStatus = DispatchTaskStatus.valueOf(dispatchLog.getStatus());
							%>

							<div class="background-task-status-row background-task-status-<%= dispatchTaskStatus.getLabel() %> h6 <%= dispatchTaskStatus.getCssClass() %>">
								<liferay-ui:message key="<%= dispatchTaskStatus.getLabel() %>" />
							</div>
						</liferay-ui:search-container-column-text>

						<liferay-ui:search-container-column-jsp
							cssClass="entry-action-column"
							path="/dispatch_log_action.jsp"
						/>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						displayStyle="list"
						markupView="lexicon"
					/>
				</liferay-ui:search-container>
			</div>
		</aui:form>
	</div>
</div>