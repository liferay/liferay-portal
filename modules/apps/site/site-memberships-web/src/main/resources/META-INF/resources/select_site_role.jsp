<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SelectRolesDisplayContext selectRolesDisplayContext = new SelectRolesDisplayContext(request, renderRequest, renderResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new SelectRolesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, selectRolesDisplayContext) %>"
/>

<aui:form cssClass="container-fluid portlet-site-memberships-assign-roles" name="fm">
	<liferay-ui:search-container
		id="roles"
		searchContainer="<%= selectRolesDisplayContext.getRoleSearchSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Role"
			escapedModel="<%= true %>"
			keyPropertyName="roleId"
			modelVar="role"
		>

			<%
			Map<String, Object> data = HashMapBuilder.<String, Object>put(
				"id", role.getRoleId()
			).build();
			%>

			<c:choose>
				<c:when test='<%= Objects.equals(selectRolesDisplayContext.getDisplayStyle(), "icon") %>'>
					<liferay-ui:search-container-column-text>
						<clay:vertical-card
							verticalCard="<%= new SelectRoleVerticalCard(role, renderRequest) %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= Objects.equals(selectRolesDisplayContext.getDisplayStyle(), "descriptive") %>'>
					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<div class="h5">
							<aui:a cssClass="selector-button" data="<%= data %>" href="javascript:void(0);">
								<%= HtmlUtil.escape(role.getTitle(locale)) %>
							</aui:a>
						</div>

						<div class="h6 text-default">
							<span><%= HtmlUtil.escape(role.getDescription(locale)) %></span>
						</div>

						<div class="h6 text-default">
							<liferay-ui:message key="<%= role.getTypeLabel() %>" />
						</div>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= Objects.equals(selectRolesDisplayContext.getDisplayStyle(), "list") %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="title"
						truncate="<%= true %>"
					>
						<aui:a cssClass="selector-button" data="<%= data %>" href="javascript:void(0);">
							<%= HtmlUtil.escape(role.getTitle(locale)) %>
						</aui:a>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="description"
						value="<%= HtmlUtil.escape(role.getDescription(locale)) %>"
					/>

					<liferay-ui:search-container-column-text
						name="type"
						value="<%= LanguageUtil.get(request, role.getTypeLabel()) %>"
					/>
				</c:when>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= selectRolesDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>