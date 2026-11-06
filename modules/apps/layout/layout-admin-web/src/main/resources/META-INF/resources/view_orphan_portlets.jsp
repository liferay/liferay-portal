<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
OrphanPortletsDisplayContext orphanPortletsDisplayContext = (OrphanPortletsDisplayContext)request.getAttribute(OrphanPortletsDisplayContext.class.getName());

OrphanPortletsManagementToolbarDisplayContext orphanPortletsManagementToolbarDisplayContext = new OrphanPortletsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, orphanPortletsDisplayContext);

Layout selLayout = orphanPortletsDisplayContext.getSelLayout();

portletDisplay.setDescription(LanguageUtil.get(request, "orphan-widgets-description"));
portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(orphanPortletsDisplayContext.getBackURL());
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(selLayout.getName(locale) + StringPool.SPACE + LanguageUtil.get(request, "orphan-widgets"));
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= orphanPortletsManagementToolbarDisplayContext %>"
	propsTransformer="{OrphanPortletsManagementToolbarPropsTransformer} from layout-admin-web"
/>

<portlet:actionURL name="/layout_admin/delete_orphan_portlets" var="deleteOrphanPortletsURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="backURL" value="<%= orphanPortletsDisplayContext.getBackURL() %>" />
	<portlet:param name="selPlid" value="<%= String.valueOf(orphanPortletsDisplayContext.getSelPlid()) %>" />
</portlet:actionURL>

<aui:form action="<%= deleteOrphanPortletsURL %>" cssClass="container-fluid container-fluid-max-xl" name="fm">
	<clay:alert
		dismissible="<%= true %>"
		displayType="warning"
		message='<%= selLayout.isLayoutPrototypeLinkActive() ? "layout-inherits-from-a-prototype-widgets-cannot-be-manipulated" : "warning-preferences-of-selected-widgets-will-be-reset-or-deleted" %>'
		symbol="warning-full"
	/>

	<liferay-ui:search-container
		searchContainer="<%= orphanPortletsDisplayContext.getOrphanPortletsSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Portlet"
			escapedModel="<%= true %>"
			keyPropertyName="portletId"
			modelVar="portlet"
		>
			<c:choose>
				<c:when test='<%= Objects.equals(orphanPortletsDisplayContext.getDisplayStyle(), "descriptive") %>'>
					<liferay-ui:search-container-column-icon
						icon="archive"
						toggleRowChecker="<%= true %>"
					/>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<div class="h5">
							<%= PortalUtil.getPortletTitle(portlet, application, locale) %>
						</div>

						<div class="h6 text-default">
							<span><%= portlet.getPortletId() %></span>
						</div>

						<div class="h6 text-default">
							<strong><liferay-ui:message key="status" /></strong>: <%= orphanPortletsDisplayContext.getStatus(portlet) %>
						</div>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-jsp
						path="/orphan_portlets_action.jsp"
					/>
				</c:when>
				<c:when test='<%= Objects.equals(orphanPortletsDisplayContext.getDisplayStyle(), "list") %>'>
					<liferay-ui:search-container-column-text
						name="title"
						truncate="<%= true %>"
						value="<%= PortalUtil.getPortletTitle(portlet, application, locale) %>"
					/>

					<liferay-ui:search-container-column-text
						name="portlet-id"
						property="portletId"
						truncate="<%= true %>"
					/>

					<liferay-ui:search-container-column-text
						name="status"
						value="<%= orphanPortletsDisplayContext.getStatus(portlet) %>"
					/>

					<liferay-ui:search-container-column-jsp
						path="/orphan_portlets_action.jsp"
					/>
				</c:when>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= orphanPortletsDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
			type="none"
		/>
	</liferay-ui:search-container>
</aui:form>