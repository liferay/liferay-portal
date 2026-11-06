<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/message_boards/init.jsp" %>

<%
MBCategory category = (MBCategory)request.getAttribute(WebKeys.MESSAGE_BOARDS_CATEGORY);

long categoryId = MBUtil.getCategoryId(request, category);

long excludedCategoryId = ParamUtil.getLong(request, "excludedMBCategoryId");

MBCategoryDisplay categoryDisplay = new MBCategoryDisplay(scopeGroupId, categoryId);

String categoryExternalReferenceCode = "";
String categoryName = null;

MBBreadcrumbUtil.addPortletBreadcrumbEntries(category, request, renderResponse);

if (category != null) {
	categoryExternalReferenceCode = category.getExternalReferenceCode();
	categoryName = category.getName();
}
else {
	categoryName = LanguageUtil.get(request, "home");
}
%>

<clay:container-fluid>
	<aui:form method="post" name="selectCategoryFm">
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
		/>

		<liferay-ui:search-container
			headerNames="category[message-board],categories,threads,posts,"
			iteratorURL='<%=
				PortletURLBuilder.createRenderURL(
					renderResponse
				).setMVCRenderCommandName(
					"/message_boards/select_category"
				).setParameter(
					"mbCategoryId", categoryId
				).buildPortletURL()
			%>'
			total="<%= MBCategoryServiceUtil.getCategoriesCount(scopeGroupId, excludedCategoryId, categoryId, WorkflowConstants.STATUS_APPROVED) %>"
		>
			<liferay-ui:search-container-results
				results="<%= MBCategoryServiceUtil.getCategories(scopeGroupId, excludedCategoryId, categoryId, WorkflowConstants.STATUS_APPROVED, searchContainer.getStart(), searchContainer.getEnd()) %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.message.boards.model.MBCategory"
				escapedModel="<%= true %>"
				keyPropertyName="categoryId"
				modelVar="curCategory"
			>
				<portlet:renderURL var="rowURL">
					<portlet:param name="mvcRenderCommandName" value="/message_boards/select_category" />
					<portlet:param name="mbCategoryId" value="<%= String.valueOf(curCategory.getCategoryId()) %>" />
				</portlet:renderURL>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-cell-minw-200 table-title"
					href="<%= rowURL %>"
					name="category[message-board]"
				>
					<%= curCategory.getName() %>

					<c:if test="<%= Validator.isNotNull(curCategory.getDescription()) %>">
						<br />

						<%= curCategory.getDescription() %>
					</c:if>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-column-text-end"
					href="<%= rowURL %>"
					name="categories"
					value="<%= String.valueOf(categoryDisplay.getSubcategoriesCount(curCategory)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-column-text-end"
					href="<%= rowURL %>"
					name="threads"
					value="<%= String.valueOf(categoryDisplay.getSubcategoriesThreadsCount(curCategory)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-column-text-end"
					href="<%= rowURL %>"
					name="posts"
					value="<%= String.valueOf(categoryDisplay.getSubcategoriesMessagesCount(curCategory)) %>"
				/>

				<liferay-ui:search-container-column-text>
					<aui:button
						cssClass="selector-button"
						data='<%=
							HashMapBuilder.<String, Object>put(
								"resourceexternalreferencecode", curCategory.getExternalReferenceCode()
							).put(
								"resourceid", curCategory.getCategoryId()
							).put(
								"resourcename", curCategory.getName()
							).build()
						%>'
						value="select"
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<aui:button-row>
				<aui:button
					cssClass="selector-button"
					data='<%=
						HashMapBuilder.<String, Object>put(
							"resourceexternalreferencecode", categoryExternalReferenceCode
						).put(
							"resourceid", categoryId
						).put(
							"resourcename", categoryName
						).build()
					%>'
					value="select-this-category"
				/>
			</aui:button-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>