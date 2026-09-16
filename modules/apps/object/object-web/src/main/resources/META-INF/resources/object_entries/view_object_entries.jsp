<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewObjectEntriesDisplayContext viewObjectEntriesDisplayContext = (ViewObjectEntriesDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

ObjectDefinition objectDefinition = viewObjectEntriesDisplayContext.getObjectDefinition();

String portletNamespace = liferayPortletResponse.getNamespace();
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
						"bulkComponentId", portletNamespace + "BulkStatus"
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
			namespace="<%= portletNamespace %>"
			pageNumber="<%= 1 %>"
			portletURL="<%= liferayPortletResponse.createRenderURL() %>"
			propsTransformer="{ViewObjectEntriesFDSPropsTransformer} from object-web"
			selectionType="multiple"
			showSelectAll="<%= true %>"
			style="fluid"
		/>

		<div>
			<react:component
				module="{ModalDeleteObjectEntry} from object-web"
				props='<%=
					HashMapBuilder.<String, Object>put(
						"byExternalReferenceCodePath", viewObjectEntriesDisplayContext.getByExternalReferenceCodePath()
					).put(
						"portletNamespace", portletNamespace
					).build()
				%>'
			/>
		</div>

		<div>
			<react:component
				module="{ModalBulkDeleteObjectEntries} from object-web"
				props='<%=
					HashMapBuilder.<String, Object>put(
						"objectDefinition", objectDefinition
					).put(
						"portletNamespace", portletNamespace
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