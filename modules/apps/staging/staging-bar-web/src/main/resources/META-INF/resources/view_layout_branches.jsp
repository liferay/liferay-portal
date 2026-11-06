<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long layoutSetBranchId = ParamUtil.getLong(request, "layoutSetBranchId");

List<LayoutRevision> layoutRevisions = LayoutRevisionLocalServiceUtil.getChildLayoutRevisions(layoutSetBranchId, LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID, plid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, LayoutRevisionCreateDateComparator.getInstance(true));

long layoutRevisionId = StagingUtil.getRecentLayoutRevisionId(request, layoutSetBranchId, plid);

LayoutRevision currentLayoutRevision = null;

if (layoutRevisionId <= 0) {
	LayoutBranch layoutBranch = LayoutBranchLocalServiceUtil.getMasterLayoutBranch(layoutSetBranchId, plid);

	currentLayoutRevision = LayoutRevisionLocalServiceUtil.getLayoutRevision(layoutSetBranchId, layoutBranch.getLayoutBranchId(), plid);

	layoutRevisionId = currentLayoutRevision.getLayoutRevisionId();
}
else {
	currentLayoutRevision = LayoutRevisionLocalServiceUtil.getLayoutRevision(layoutRevisionId);
}

request.setAttribute("view_layout_branches.jsp-currenttLayoutBranchId", String.valueOf(currentLayoutRevision.getLayoutBranchId()));
%>

<liferay-util:include page="/navigation.jsp" servletContext="<%= application %>">
	<liferay-util:param name="navigationName" value="page-variations" />
</liferay-util:include>

<clay:container-fluid>
	<liferay-ui:success key="pageVariationAdded" message="page-variation-was-added" />
	<liferay-ui:success key="pageVariationDeleted" message="page-variation-was-deleted" />
	<liferay-ui:success key="pageVariationUpdated" message="page-variation-was-updated" />

	<div class="lfr-alert-container"></div>

	<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, stagingGroup, ActionKeys.ADD_LAYOUT_BRANCH) %>">
		<liferay-portlet:renderURL var="addLayoutBranchURL">
			<portlet:param name="mvcRenderCommandName" value="/staging_bar/edit_layout_branch" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="layoutRevisionId" value="<%= String.valueOf(layoutRevisionId) %>" />
		</liferay-portlet:renderURL>

		<aui:button-row>
			<aui:button href="<%= addLayoutBranchURL %>" name="addRootLayoutBranch" value="add-page-variation" />
		</aui:button-row>
	</c:if>

	<div class="branch-results">
		<liferay-ui:search-container
			total="<%= layoutRevisions.size() %>"
		>
			<liferay-ui:search-container-results
				results="<%= layoutRevisions %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.model.LayoutRevision"
				escapedModel="<%= true %>"
				keyPropertyName="layoutRevisionId"
				modelVar="layoutRevision"
			>

				<%
				LayoutBranch layoutBranch = layoutRevision.getLayoutBranch();
				%>

				<liferay-ui:search-container-column-text
					name="name"
				>
					<c:if test="<%= layoutRevision.getLayoutBranchId() == currentLayoutRevision.getLayoutBranchId() %>">
						<strong>
					</c:if>

					<liferay-ui:message key="<%= HtmlUtil.escape(layoutBranchDisplayContext.getLayoutBranchDisplayName(layoutBranch)) %>" localizeKey="<%= false %>" />

					<c:if test="<%= layoutBranch.isMaster() %>">
						<i class="icon-asterisk"></i>
					</c:if>

					<c:if test="<%= layoutRevision.getLayoutBranchId() == currentLayoutRevision.getLayoutBranchId() %>">
						</strong>
					</c:if>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					name="description"
					value="<%= HtmlUtil.escape(layoutBranch.getDescription()) %>"
				/>

				<liferay-ui:search-container-column-jsp
					align="right"
					cssClass="entry-action"
					path="/layout_branch_action.jsp"
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