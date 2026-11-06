<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

List<LayoutSetBranch> layoutSetBranches = LayoutSetBranchLocalServiceUtil.getLayoutSetBranches(groupId, privateLayout);

long layoutSetBranchId = ParamUtil.getLong(request, "layoutSetBranchId");

LayoutSetBranch layoutSetBranch = LayoutSetBranchLocalServiceUtil.getLayoutSetBranch(layoutSetBranchId);

if (layoutSetBranches.contains(layoutSetBranch)) {
	layoutSetBranches = ListUtil.copy(layoutSetBranches);

	layoutSetBranches.remove(layoutSetBranch);
}
%>

<clay:container-fluid>
	<div class="site-pages-variation taglib-header">
		<a class="icon-monospaced list-unstyled portlet-icon-back text-default" href="<%= HtmlUtil.escapeAttribute(redirect) %>" title="<%= HtmlUtil.escapeAttribute(LanguageUtil.get(resourceBundle, "back")) %>">
			<liferay-ui:icon
				icon="angle-left"
				markupView="lexicon"
			/>
		</a>

		<h3 class="header-title header-title-with-overflow">
			<span>
				<%= HtmlUtil.escape(LanguageUtil.get(resourceBundle, "merge-site-pages-variation")) %>
			</span>
		</h3>
	</div>

	<div id="<portlet:namespace />mergeLayoutSetBranch">
		<portlet:actionURL name="/staging_bar/merge_layout_set_branch" var="mergeLayoutSetBranchURL">
			<portlet:param name="mvcRenderCommandName" value="/staging_bar/view_layout_set_branches" />
		</portlet:actionURL>

		<aui:form action="<%= mergeLayoutSetBranchURL %>" enctype="multipart/form-data" method="post" name="fm4">
			<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
			<aui:input name="groupId" type="hidden" value="<%= groupId %>" />
			<aui:input name="layoutSetBranchId" type="hidden" value="<%= layoutSetBranchId %>" />
			<aui:input name="mergeLayoutSetBranchId" type="hidden" />

			<liferay-ui:search-container
				id="layoutSetBranchesSearchContainer"
				total="<%= layoutSetBranches.size() %>"
			>
				<liferay-ui:search-container-results
					results="<%= layoutSetBranches %>"
				/>

				<liferay-ui:search-container-row
					className="com.liferay.portal.kernel.model.LayoutSetBranch"
					escapedModel="<%= true %>"
					keyPropertyName="layoutSetBranchId"
					modelVar="curLayoutSetBranch"
				>

					<%
					long curLayoutSetBranchId = curLayoutSetBranch.getLayoutSetBranchId();

					String layoutSetBranchDisplayName = layoutSetBranchDisplayContext.getLayoutSetBranchDisplayName(curLayoutSetBranch);
					%>

					<liferay-ui:search-container-column-text
						name="branch"
						value="<%= layoutSetBranchDisplayName %>"
					/>

					<liferay-ui:search-container-column-text>
						<liferay-ui:csp>
							<a class="layout-set-branch" data-layoutSetBranchId="<%= curLayoutSetBranchId %>" data-layoutSetBranchMessage="<%= LanguageUtil.format(request, "are-you-sure-you-want-to-merge-changes-from-x", layoutSetBranchDisplayName, false) %>" data-layoutSetBranchName="<%= HtmlUtil.escapeAttribute(curLayoutSetBranch.getName()) %>" href="#" id="<portlet:namespace /><%= curLayoutSetBranchId %>" onClick="<portlet:namespace />selectLayoutSetBranch('<%= curLayoutSetBranchId %>');">
								<liferay-ui:message key="select" />
							</a>
						</liferay-ui:csp>
					</liferay-ui:search-container-column-text>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					paginate="<%= false %>"
					searchContainer="<%= searchContainer %>"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</div>
</clay:container-fluid>

<aui:script>
	function <portlet:namespace />selectLayoutSetBranch(layoutSetBranchId) {
		var layoutSetBranch = document.getElementById(
			'<portlet:namespace />' + layoutSetBranchId
		);

		if (layoutSetBranch) {
			Liferay.Util.openConfirmModal({
				message: layoutSetBranch.getAttribute(
					'data-layoutSetBranchMessage'
				),
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						Liferay.Util.postForm(document.<portlet:namespace />fm4, {
							data: {
								mergeLayoutSetBranchId:
									layoutSetBranch.getAttribute(
										'data-layoutSetBranchId'
									),
							},
						});
					}
				},
			});
		}
	}
</aui:script>