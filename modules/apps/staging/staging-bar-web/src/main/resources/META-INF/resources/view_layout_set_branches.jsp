<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
List<LayoutSetBranch> layoutSetBranches = LayoutSetBranchLocalServiceUtil.getLayoutSetBranches(stagingGroup.getGroupId(), privateLayout);

LayoutSetBranch currentLayoutSetBranch = LayoutSetBranchLocalServiceUtil.getUserLayoutSetBranch(themeDisplay.getUserId(), stagingGroup.getGroupId(), privateLayout, 0, 0);

request.setAttribute("view_layout_set_branches.jsp-currentLayoutSetBranchId", String.valueOf(currentLayoutSetBranch.getLayoutSetBranchId()));
%>

<liferay-util:include page="/navigation.jsp" servletContext="<%= application %>">
	<liferay-util:param name="navigationName" value="site-pages-variation" />
</liferay-util:include>

<clay:container-fluid>
	<liferay-ui:success key="sitePageVariationAdded" message="site-page-variation-was-added" />
	<liferay-ui:success key="sitePageVariationDeleted" message="site-page-variation-was-deleted" />
	<liferay-ui:success key="sitePageVariationMerged" message="site-page-variation-was-merged" />
	<liferay-ui:success key="sitePageVariationUpdated" message="site-page-variation-was-updated" />

	<div class="lfr-alert-container"></div>

	<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, stagingGroup, ActionKeys.ADD_LAYOUT_SET_BRANCH) %>">
		<liferay-portlet:renderURL var="addLayoutSetBranchURL">
			<portlet:param name="mvcRenderCommandName" value="/staging_bar/edit_layout_set_branch" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</liferay-portlet:renderURL>

		<aui:button-row>
			<aui:button href="<%= addLayoutSetBranchURL %>" name="addBranchButton" value="add-site-pages-variation" />
		</aui:button-row>
	</c:if>

	<div class="branch-results">
		<liferay-ui:search-container
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
				<liferay-ui:search-container-column-text
					name="name"
				>
					<c:if test="<%= currentLayoutSetBranch.equals(curLayoutSetBranch) %>">
						<strong>
					</c:if>

					<liferay-ui:message key="<%= layoutSetBranchDisplayContext.getLayoutSetBranchDisplayName(curLayoutSetBranch) %>" localizeKey="<%= false %>" />

					<c:if test="<%= curLayoutSetBranch.isMaster() %>">
						<i class="icon-asterisk"></i>
					</c:if>

					<c:if test="<%= currentLayoutSetBranch.equals(curLayoutSetBranch) %>">
						</strong>
					</c:if>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					property="description"
				/>

				<liferay-ui:search-container-column-jsp
					align="right"
					cssClass="entry-action"
					path="/layout_set_branch_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
				paginate="<%= false %>"
				searchContainer="<%= searchContainer %>"
			/>
		</liferay-ui:search-container>
	</div>
</clay:container-fluid>