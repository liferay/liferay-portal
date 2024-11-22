<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<c:if test='<%= SessionErrors.contains(liferayPortletRequest, "invalidThumbnailFormat") %>'>
	<aui:script>
		Liferay.Util.openToast({
			message: '<liferay-ui:message key="file-type-is-invalid" />',
			title: Liferay.Language.get('error'),
			toastProps: {
				autoClose: 5000,
			},
			type: 'danger',
		});
	</aui:script>
</c:if>

<%
StyleBookDisplayContext styleBookDisplayContext = new StyleBookDisplayContext(request, liferayPortletRequest, liferayPortletResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new StyleBookManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, styleBookDisplayContext.getStyleBookEntriesSearchContainer()) %>"
	propsTransformer="{StyleBookManagementToolbarPropsTransformer} from style-book-web"
/>

<portlet:actionURL name="/style_book/delete_style_book_entry" var="deleteStyleBookEntryURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<clay:container-fluid>
	<aui:form action="<%= deleteStyleBookEntryURL %>" name="fm">
		<liferay-ui:search-container
			searchContainer="<%= styleBookDisplayContext.getStyleBookEntriesSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.style.book.model.StyleBookEntry"
				keyProperty="styleBookEntryId"
				modelVar="styleBookEntry"
			>
				<liferay-ui:search-container-column-text>
					<clay:vertical-card
						propsTransformer="{StylebookEntryActionDropdownPropsTransformer} from style-book-web"
						verticalCard="<%= new StyleBookVerticalCard(styleBookEntry, renderRequest, renderResponse, searchContainer.getRowChecker()) %>"
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="icon"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<aui:form name="styleBookEntryFm">
	<aui:input name="styleBookEntryIds" type="hidden" />
</aui:form>

<portlet:actionURL name="/style_book/update_style_book_entry_preview" var="styleBookEntryPreviewURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= styleBookEntryPreviewURL %>" name="styleBookEntryPreviewFm">
	<aui:input name="styleBookEntryId" type="hidden" />
	<aui:input name="fileEntryId" type="hidden" />
</aui:form>