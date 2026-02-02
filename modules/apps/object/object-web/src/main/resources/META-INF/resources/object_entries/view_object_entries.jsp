<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>
<%@ page import="com.liferay.bulk.selection.BulkSelectionRunner" %>
<%@ page import="com.liferay.object.web.internal.bulk.selection.BulkSelectionRunnerUtil" %>
<%@ page import="com.liferay.portal.kernel.util.PortalUtil" %>

<%
ViewObjectEntriesDisplayContext viewObjectEntriesDisplayContext = (ViewObjectEntriesDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

ObjectDefinition objectDefinition = viewObjectEntriesDisplayContext.getObjectDefinition();

boolean multiSelectEnabled = FeatureFlagManagerUtil.isEnabled(company.getCompanyId(), "LPD-69713");
%>

<c:choose>
	<c:when test="<%= objectDefinition.isPortlet() || Objects.equals(layout.getType(), LayoutConstants.TYPE_CONTROL_PANEL) %>">
		<%
		BulkSelectionRunner bulkSelectionRunner = BulkSelectionRunnerUtil.getBulkSelectionRunner();
		%>

		<div>
			<react:component
				module="{BulkStatus} from object-web"
				props='<%=
					HashMapBuilder.<String, Object>put(
						"bulkComponentId", liferayPortletResponse.getNamespace() + "BulkStatus"
					).put(
						"bulkInProgress", bulkSelectionRunner.isBusy(user)
					).put(
						"pathModule", PortalUtil.getPathModule()
					).build()
				%>'
			/>
		</div>
		
		<frontend-data-set:headless-display
			apiURL="<%= viewObjectEntriesDisplayContext.getAPIURL() %>"
			bulkActionDropdownItems="<%= viewObjectEntriesDisplayContext.getBulkActionDropdownItems() %>"
			creationMenu="<%= viewObjectEntriesDisplayContext.getCreationMenu() %>"
			fdsActionDropdownItems="<%= viewObjectEntriesDisplayContext.getFDSActionDropdownItems() %>"
			fdsFilters="<%= viewObjectEntriesDisplayContext.getFDSFilters() %>"
			fdsSortItemList="<%= viewObjectEntriesDisplayContext.getFDSSortItemList() %>"
			formName="fm"
			id="<%= viewObjectEntriesDisplayContext.getFDSId() %>"
			itemsPerPage="<%= 20 %>"
			namespace="<%= liferayPortletResponse.getNamespace() %>"
			pageNumber="<%= 1 %>"
			portletURL="<%= liferayPortletResponse.createRenderURL() %>"
			propsTransformer="{ViewObjectEntriesFDSPropsTransformer} from object-web"
			selectionType='<%= multiSelectEnabled ? "multiple" : "" %>'
			showSelectAll="<%= multiSelectEnabled %>"
			style="fluid"
		/>

		<div>
			<react:component
				module="{ModalDeleteObjectEntry} from object-web"
				props='<%=
					HashMapBuilder.<String, Object>put(
						"byExternalReferenceCodePath", viewObjectEntriesDisplayContext.getByExternalReferenceCodePath()
					).build()
				%>'
			/>
		</div>

		<div>
			<react:component
				module="{ModalBulkDeleteObjectEntries} from object-web"
				props='<%=
					HashMapBuilder.<String, Object>put(
						"namespace", liferayPortletResponse.getNamespace()
					).put(
						"objectDefinition", objectDefinition
					).build()
				%>'
			/>
		</div>
		
	</c:when>
	<c:otherwise>
		<clay:alert
			displayType="warning"
			message="this-object-is-not-available"
			title="Warning"
		/>
	</c:otherwise>
</c:choose>