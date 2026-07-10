<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
JournalViewMoreMenuItemsDisplayContext journalViewMoreMenuItemsDisplayContext = new JournalViewMoreMenuItemsDisplayContext(renderRequest, renderResponse, journalDisplayContext.getFolderId(), journalDisplayContext.getRestrictionType());
%>

<c:if test="<%= journalDisplayContext.getAddMenuFavItemsLength() == 0 %>">
	<clay:stripe
		message='<%= LanguageUtil.format(resourceBundle, "you-can-add-as-many-as-x-favorites-in-your-quick-menu", journalWebConfiguration.maxAddMenuItems()) %>'
	/>
</c:if>

<liferay-ui:error exception="<%= MaxAddMenuFavItemsException.class %>" message='<%= LanguageUtil.format(resourceBundle, "you-cannot-add-more-than-x-favorites", journalWebConfiguration.maxAddMenuItems()) %>' />

<c:if test="<%= journalDisplayContext.getAddMenuFavItemsLength() >= journalWebConfiguration.maxAddMenuItems() %>">
	<clay:stripe
		displayType="warning"
		message="right-now-your-quick-menu-is-full-of-favorites-if-you-want-to-add-another-one-please-remove-at-least-one-of-them"
	/>
</c:if>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new JournalViewMoreMenuItemsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, journalViewMoreMenuItemsDisplayContext) %>"
/>

<aui:form cssClass="container-fluid container-fluid-max-xl" name="addMenuItemFm">
	<liferay-ui:search-container
		searchContainer="<%= journalViewMoreMenuItemsDisplayContext.getDDMStructuresSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.dynamic.data.mapping.model.DDMStructure"
			escapedModel="<%= true %>"
			modelVar="ddmStructure"
		>
			<liferay-ui:search-container-column-text
				name="menu-item-name"
			>
				<aui:a
					cssClass="selector-button"
					data='<%=
						HashMapBuilder.<String, Object>put(
							"ddmStructureId", ddmStructure.getStructureId()
						).build()
					%>'
					href="javascript:void(0);"
				>
					<%= ddmStructure.getName(locale) %>
				</aui:a>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="scope"
				value="<%= HtmlUtil.escape(journalViewMoreMenuItemsDisplayContext.getDDMStructureScopeName(ddmStructure, locale)) %>"
			/>

			<liferay-ui:search-container-column-text
				name="user"
				property="userName"
			/>

			<liferay-ui:search-container-column-date
				name="modified-date"
				property="modifiedDate"
			/>

			<liferay-ui:search-container-column-jsp
				align="center"
				name='<%= LanguageUtil.format(request, "add-to-favorites-x", String.valueOf(journalDisplayContext.getAddMenuFavItemsLength())) %>'
				path="/view_more_menu_items_actions.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>