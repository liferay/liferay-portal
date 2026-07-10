<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
DLSelectRestrictedFileEntryTypesDisplayContext selectRestrictedFileEntryTypesDisplayContext = new DLSelectRestrictedFileEntryTypesDisplayContext(request, renderRequest, renderResponse);
%>

<clay:navigation-bar
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.setActive(true);
						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "document-types"));
					});
			}
		}
	%>'
/>

<aui:form action="<%= selectRestrictedFileEntryTypesDisplayContext.getFormActionURL() %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="selectFileEntryTypeFm">
	<liferay-ui:search-container
		searchContainer="<%= selectRestrictedFileEntryTypesDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.document.library.kernel.model.DLFileEntryType"
			keyProperty="fileEntryTypeId"
			modelVar="fileEntryType"
		>
			<liferay-ui:search-container-column-icon
				icon="edit-layout"
			/>

			<liferay-ui:search-container-column-text
				colspan="<%= 2 %>"
			>
				<div class="h5"><%= HtmlUtil.escape(fileEntryType.getName(locale)) %></div>

				<div class="h6 text-default">
					<span><%= HtmlUtil.escape(fileEntryType.getDescription(locale)) %></span>
				</div>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text>
				<aui:button
					cssClass="selector-button"
					data='<%=
						HashMapBuilder.<String, Object>put(
							"entityid", fileEntryType.getFileEntryTypeId()
						).put(
							"entityname", fileEntryType.getName(locale)
						).build()
					%>'
					value="choose"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="descriptive"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>