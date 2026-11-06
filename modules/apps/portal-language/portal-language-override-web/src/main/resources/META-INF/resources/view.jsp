<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewDisplayContext viewDisplayContext = (ViewDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new ViewManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, viewDisplayContext.getSearchContainer(), viewDisplayContext.getDisplayStyle(), viewDisplayContext.isHasManageLanguageOverridesPermission()) %>"
/>

<clay:container-fluid
	cssClass="container-view"
>
	<clay:dropdown-menu
		displayType="secondary"
		dropdownItems="<%= viewDisplayContext.getTranslationLanguageDropdownItems() %>"
		icon="<%= HtmlUtil.escape(StringUtil.toLowerCase(TextFormatter.format(viewDisplayContext.getSelectedLanguageId(), TextFormatter.O))) %>"
		label="<%= HtmlUtil.escape(TextFormatter.format(viewDisplayContext.getSelectedLanguageId(), TextFormatter.O)) %>"
		small="<%= true %>"
	/>

	<liferay-ui:search-container
		orderByCol="key"
		searchContainer="<%= viewDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.language.override.web.internal.display.LanguageItemDisplay"
			keyPropertyName="key"
			modelVar="languageItemDisplay"
		>
			<portlet:renderURL var="editPLOEntryURL">
				<portlet:param name="mvcPath" value="/edit_plo_entry.jsp" />
				<portlet:param name="backURL" value="<%= currentURL %>" />
				<portlet:param name="key" value="<%= languageItemDisplay.getKey() %>" />
				<portlet:param name="selectedLanguageId" value="<%= viewDisplayContext.getSelectedLanguageId() %>" />
			</portlet:renderURL>

			<%
			if (!viewDisplayContext.isHasManageLanguageOverridesPermission()) {
				editPLOEntryURL = null;
			}
			%>

			<c:choose>
				<c:when test='<%= Objects.equals("descriptive", viewDisplayContext.getDisplayStyle()) %>'>
					<liferay-ui:search-container-column-text
						colspan="<%= 3 %>"
						href="<%= editPLOEntryURL %>"
					>
						<div class="h5">
							<strong><%= HtmlUtil.escape(languageItemDisplay.getKey()) %></strong>
						</div>

						<div class="h6 text-default">
							<%= HtmlUtil.escape(languageItemDisplay.getValue()) %>
						</div>

						<c:if test="<%= languageItemDisplay.isOverride() %>">
							<div class="h6">
								<liferay-ui:message key="languages-with-override" />: <%= languageItemDisplay.getOverrideLanguageIdsString() %>
							</div>
						</c:if>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-jsp
						path="/actions.jsp"
					/>
				</c:when>
				<c:when test='<%= Objects.equals("list", viewDisplayContext.getDisplayStyle()) %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-small"
						href="<%= editPLOEntryURL %>"
						name="key"
						value="<%= HtmlUtil.escape(languageItemDisplay.getKey()) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-small"
						href="<%= editPLOEntryURL %>"
						name="current-value"
						value="<%= HtmlUtil.escape(languageItemDisplay.getValue()) %>"
					/>

					<liferay-ui:search-container-column-text
						href="<%= editPLOEntryURL %>"
						name="languages-with-override"
						value="<%= languageItemDisplay.getOverrideLanguageIdsString() %>"
					/>

					<liferay-ui:search-container-column-jsp
						path="/actions.jsp"
					/>
				</c:when>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= viewDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>