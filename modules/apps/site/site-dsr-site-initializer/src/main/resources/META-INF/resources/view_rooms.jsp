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
			apiURL="<%= viewRoomsSectionDisplayContext.getAPIURL() %>"
			creationMenu="<%= viewRoomsSectionDisplayContext.getCreationMenu() %>"
			fdsActionDropdownItems="<%= viewRoomsSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= DSRSiteInitializerFDSNames.DSR_ROOM %>"
			propsTransformer="{RoomsFDSPropsTransformer} from site-dsr-site-initializer"
			selectedItemsKey="embedded.id"
		/>
	</div>
</div>