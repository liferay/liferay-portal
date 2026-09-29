<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewRoomsSectionDisplayContext viewRoomsSectionDisplayContext = (ViewRoomsSectionDisplayContext)request.getAttribute(ViewRoomsSectionDisplayContext.class.getName());
%>

<div>
	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			additionalProps="<%= viewRoomsSectionDisplayContext.getAdditionalProps() %>"
			apiURL="<%= viewRoomsSectionDisplayContext.getAPIURL() %>"
			creationMenu="<%= viewRoomsSectionDisplayContext.getCreationMenu() %>"
			fdsActionDropdownItems="<%= viewRoomsSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= DSRSiteInitializerFDSNames.ROOM %>"
			propsTransformer="{RoomsFDSPropsTransformer} from digital-sales-room-web"
			selectedItemsKey="embedded.id"
			showManagementBar="<%= !viewRoomsSectionDisplayContext.isHomePage() %>"
			showPagination="<%= !viewRoomsSectionDisplayContext.isHomePage() %>"
			showSearch="<%= !viewRoomsSectionDisplayContext.isHomePage() %>"
		/>
	</div>
</div>

<c:if test="<%= SessionErrors.contains(request, PrincipalException.MustHavePermission.class) %>">
	<aui:script>
		Liferay.Util.openToast({
			message:
				'<liferay-ui:message key="you-do-not-have-the-required-permissions" />',
			title: '<liferay-ui:message key="error" />',
			toastProps: {
				autoClose: 5000,
			},
			type: 'danger',
		});
	</aui:script>
</c:if>