<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/message_boards/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

MBCategory category = (MBCategory)request.getAttribute(WebKeys.MESSAGE_BOARDS_CATEGORY);

long categoryId = MBUtil.getCategoryId(request, category);

long parentCategoryId = BeanParamUtil.getLong(category, request, "parentCategoryId", MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));

String headerTitle = LanguageUtil.format(request, "move-x", category.getName(), false);

if (portletTitleBasedNavigation) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);

	renderResponse.setTitle(headerTitle);
}
%>

<div <%= portletTitleBasedNavigation ? "class=\"container-fluid container-fluid-max-xl container-form-lg\"" : StringPool.BLANK %>>
	<c:if test="<%= !portletTitleBasedNavigation %>">
		<h3><%= LanguageUtil.format(request, "move-x", HtmlUtil.escape(category.getName()), false) %></h3>
	</c:if>

	<portlet:actionURL name="/message_boards/move_category" var="moveCategoryURL" />

	<aui:form action="<%= moveCategoryURL %>" method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="mbCategoryId" type="hidden" value="<%= categoryId %>" />

		<aui:model-context bean="<%= category %>" model="<%= MBCategory.class %>" />

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset>

					<%
					String parentCategoryName = StringPool.BLANK;

					try {
						MBCategory parentCategory = MBCategoryLocalServiceUtil.getCategory(parentCategoryId);

						parentCategoryName = parentCategory.getName();
					}
					catch (NoSuchCategoryException nsce) {
					}
					%>

					<liferay-frontend:resource-selector
						inputLabel='<%= LanguageUtil.get(request, "parent-category") %>'
						inputName="parentCategoryId"
						modalTitle='<%= LanguageUtil.format(request, "select-x", "category[message-board]") %>'
						resourceName="<%= parentCategoryName %>"
						resourceValue="<%= String.valueOf(parentCategoryId) %>"
						selectEventName="selectCategory"
						selectResourceURL='<%=
							PortletURLBuilder.createRenderURL(
								renderResponse
							).setMVCRenderCommandName(
								"/message_boards/select_category"
							).setParameter(
								"excludedMBCategoryId", categoryId
							).setParameter(
								"mbCategoryId", (category == null) ? MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID : category.getParentCategoryId()
							).setWindowState(
								LiferayWindowState.POP_UP
							).buildString()
						%>'
						showRemoveButton="<%= true %>"
					/>

					<aui:input label="merge-with-parent-category" name="mergeWithParentCategory" type="checkbox" />
				</aui:fieldset>

				<div class="sheet-footer">
					<aui:button type="submit" value="move" />

					<aui:button href="<%= redirect %>" type="cancel" />
				</div>
			</div>
		</div>
	</aui:form>
</div>

<%
MBBreadcrumbUtil.addPortletBreadcrumbEntries(category, request, renderResponse);

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "move"), currentURL);
%>