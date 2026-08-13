<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String backURL = PortalUtil.escapeRedirect(ParamUtil.getString(request, "backURL"));

if (backURL == null) {
	backURL = String.valueOf(renderResponse.createRenderURL());
}

BatchPlannerPlanDisplay batchPlannerPlanDisplay = (BatchPlannerPlanDisplay)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<div class="container pt-4">
	<div class="card">
		<div class="card-header h4"><liferay-ui:message key="batch-engine-task-details" /></div>

		<div class="card-body">
			<clay:content-row>
				<clay:content-col
					expand="<%= true %>"
				>
					<clay:row>
						<clay:col
							md="2"
						>
							<liferay-ui:message key="name" />
						</clay:col>

						<clay:col
							md="8"
						>
							<%= HtmlUtil.escape(batchPlannerPlanDisplay.getTitle()) %>
						</clay:col>
					</clay:row>

					<clay:row>
						<clay:col
							md="2"
						>
							<liferay-ui:message key="type" />
						</clay:col>

						<clay:col
							md="8"
						>

							<%
							String exportImportLabel = "import";

							if (batchPlannerPlanDisplay.isExport()) {
								exportImportLabel = "export";
							}
							%>

							<liferay-ui:message key="<%= exportImportLabel %>" />
						</clay:col>
					</clay:row>
				</clay:content-col>

				<clay:content-col
					expand="<%= true %>"
				>
					<clay:row>
						<clay:col
							md="4"
						>
							<liferay-ui:message key="id" />
						</clay:col>

						<clay:col
							md="6"
						>
							<%= String.valueOf(batchPlannerPlanDisplay.getBatchPlannerPlanId()) %>
						</clay:col>
					</clay:row>

					<clay:row>
						<clay:col
							md="4"
						>
							<liferay-ui:message key="create-date" />
						</clay:col>

						<clay:col
							md="6"
						>
							<%= dateTimeFormat.format(batchPlannerPlanDisplay.getCreateDate()) %>
						</clay:col>
					</clay:row>

					<clay:row>
						<clay:col
							md="4"
						>
							<liferay-ui:message key="modified-date" />
						</clay:col>

						<clay:col
							md="6"
						>
							<%= dateTimeFormat.format(batchPlannerPlanDisplay.getModifiedDate()) %>
						</clay:col>
					</clay:row>

					<clay:row>
						<clay:col
							md="4"
						>
							<liferay-ui:message key="count" />
						</clay:col>

						<clay:col
							md="6"
						>
							<%= String.valueOf(batchPlannerPlanDisplay.getTotalItemsCount()) %>
						</clay:col>
					</clay:row>

					<clay:row>
						<clay:col
							md="4"
						>
							<liferay-ui:message key="status" />
						</clay:col>

						<clay:col
							md="6"
						>
							<liferay-ui:message key="<%= BatchPlannerPlanConstants.getStatusLabel(batchPlannerPlanDisplay.getStatus()) %>" />
						</clay:col>
					</clay:row>
				</clay:content-col>
			</clay:content-row>

			<div class="mt-4">
				<clay:link
					displayType="primary"
					href="<%= backURL %>"
					label="back"
					type="button"
				/>
			</div>
		</div>
	</div>
</div>