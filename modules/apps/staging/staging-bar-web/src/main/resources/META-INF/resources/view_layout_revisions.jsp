<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long layoutSetBranchId = ParamUtil.getLong(request, "layoutSetBranchId");

LayoutRevision recentLayoutRevision = null;

long currentLayoutRevisionId = StagingUtil.getRecentLayoutRevisionId(request, layoutSetBranchId, plid);

if (currentLayoutRevisionId > 0) {
	recentLayoutRevision = LayoutRevisionLocalServiceUtil.getLayoutRevision(currentLayoutRevisionId);
}
else {
	recentLayoutRevision = LayoutStagingUtil.getLayoutRevision(layout);

	currentLayoutRevisionId = recentLayoutRevision.getLayoutRevisionId();
}

Long liveLayoutRevisionId = null;

List<LayoutRevision> rootLayoutRevisions = LayoutRevisionLocalServiceUtil.getChildLayoutRevisions(layoutSetBranchId, LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID, plid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, LayoutRevisionCreateDateComparator.getInstance(true));
%>

<liferay-util:include page="/navigation.jsp" servletContext="<%= application %>">
	<liferay-util:param name="navigationName" value="history[record]" />
</liferay-util:include>

<clay:container-fluid>
	<c:if test="<%= !rootLayoutRevisions.isEmpty() %>">
		<c:if test="<%= rootLayoutRevisions.size() > 1 %>">
			<aui:select cssClass="variation-selector" inlineLabel="left" label="" name="variationsSelector">

				<%
				for (LayoutRevision rootLayoutRevision : rootLayoutRevisions) {
					LayoutBranch layoutBranch = rootLayoutRevision.getLayoutBranch();
				%>

					<aui:option label="<%= HtmlUtil.escape(layoutBranchDisplayContext.getLayoutBranchDisplayName(layoutBranch)) %>" localizeLabel="<%= false %>" selected="<%= recentLayoutRevision.getLayoutBranchId() == rootLayoutRevision.getLayoutBranchId() %>" value="<%= rootLayoutRevision.getLayoutRevisionId() %>" />

				<%
				}
				%>

				<aui:option label="all-page-variations" value="all" />
			</aui:select>
		</c:if>

		<div class="layout-revision-container" id="<portlet:namespace />layoutRevisionsContainer">

			<%
			for (int i = 0; i < rootLayoutRevisions.size(); i++) {
				LayoutRevision rootLayoutRevision = rootLayoutRevisions.get(i);
			%>

				<div class="layout-variation-container <%= (recentLayoutRevision.getLayoutBranchId() == rootLayoutRevision.getLayoutBranchId()) ? StringPool.BLANK : "hide" %>" id="<portlet:namespace /><%= rootLayoutRevision.getLayoutRevisionId() %>">
					<c:if test="<%= rootLayoutRevisions.size() > 1 %>">

						<%
						LayoutBranch layoutBranch = rootLayoutRevision.getLayoutBranch();
						%>

						<h3 class="layout-variation-name"><liferay-ui:message key="<%= HtmlUtil.escape(layoutBranchDisplayContext.getLayoutBranchDisplayName(layoutBranch)) %>" translateArguments="<%= false %>" /></h3>
					</c:if>

					<liferay-ui:search-container
						id='<%= liferayPortletResponse.getNamespace() + "layoutRevisionsSearchContainer_" + i %>'
						total="<%= LayoutRevisionLocalServiceUtil.getLayoutRevisionsCount(rootLayoutRevision.getLayoutSetBranchId(), rootLayoutRevision.getLayoutBranchId(), rootLayoutRevision.getPlid()) %>"
					>
						<liferay-ui:search-container-results
							results="<%= LayoutRevisionLocalServiceUtil.getLayoutRevisions(rootLayoutRevision.getLayoutSetBranchId(), rootLayoutRevision.getLayoutBranchId(), rootLayoutRevision.getPlid(), QueryUtil.ALL_POS, QueryUtil.ALL_POS, LayoutRevisionCreateDateComparator.getInstance(false)) %>"
						/>

						<liferay-ui:search-container-row
							className="com.liferay.portal.kernel.model.LayoutRevision"
							escapedModel="<%= true %>"
							keyProperty="layoutRevisionId"
							modelVar="curLayoutRevision"
						>
							<liferay-ui:search-container-column-date
								name="date"
								value="<%= curLayoutRevision.getCreateDate() %>"
							/>

							<liferay-ui:search-container-column-text
								name="status"
							>
								<aui:model-context bean="<%= curLayoutRevision %>" model="<%= LayoutRevision.class %>" />

								<%
								if ((liveLayoutRevisionId == null) && curLayoutRevision.isApproved()) {
									liveLayoutRevisionId = _getLastImportLayoutRevisionId(group, layout, themeDisplay.getUser());
								}
								%>

								<aui:workflow-status showIcon="<%= false %>" showLabel="<%= false %>" status="<%= curLayoutRevision.getStatus() %>" statusMessage="<%= _getStatusMessage(curLayoutRevision, GetterUtil.getLong(liveLayoutRevisionId)) %>" />
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								name="version"
							>
								<c:choose>
									<c:when test="<%= curLayoutRevision.getLayoutRevisionId() == currentLayoutRevisionId %>">
										<span class="layout-revision-current"><%= curLayoutRevision.getLayoutRevisionId() %></span>
										<span class="current-version"><liferay-ui:message key="current-version" /></span>
									</c:when>
									<c:otherwise>
										<liferay-ui:csp>
											<a class="layout-revision selection-handle" data-layoutRevisionId="<%= curLayoutRevision.getLayoutRevisionId() %>" data-layoutSetBranchId="<%= curLayoutRevision.getLayoutSetBranchId() %>" href="javascript:void(0);" onclick="<portlet:namespace />selectRevision(<%= curLayoutRevision.getLayoutRevisionId() %>, <%= curLayoutRevision.getLayoutSetBranchId() %>);" title="<liferay-ui:message key="go-to-this-version" />">
												<%= curLayoutRevision.getLayoutRevisionId() %>
											</a>
										</liferay-ui:csp>
									</c:otherwise>
								</c:choose>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								name="user"
							>

								<%
								User curUser = UserLocalServiceUtil.fetchUserById(curLayoutRevision.getUserId());
								%>

								<c:choose>
									<c:when test="<%= (curUser != null) && curUser.isActive() %>">
										<a class="user-handle" href="<%= curUser.getDisplayURL(themeDisplay) %>">
											<%= HtmlUtil.escape(curUser.getFullName()) %>
										</a>
									</c:when>
									<c:otherwise>
										<%= curLayoutRevision.getUserName() %>
									</c:otherwise>
								</c:choose>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-jsp
								align="right"
								cssClass="entry-action"
								path="/layout_revision_action.jsp"
							/>
						</liferay-ui:search-container-row>

						<liferay-ui:search-iterator
							markupView="lexicon"
							paginate="<%= false %>"
							searchContainer="<%= searchContainer %>"
						/>
					</liferay-ui:search-container>
				</div>

			<%
			}
			%>

		</div>
	</c:if>
</clay:container-fluid>

<aui:script>
	function <portlet:namespace />selectRevision(
		layoutRevisionId,
		layoutSetBranchId
	) {
		var updateLayoutData = {
			cmd: 'select_layout_revision',
			doAsUserId: themeDisplay.getDoAsUserIdEncoded(),
			layoutRevisionId: layoutRevisionId,
			layoutSetBranchId: layoutSetBranchId,
			p_auth: Liferay.authToken,
			p_l_id: themeDisplay.getPlid(),
			p_v_l_s_g_id: themeDisplay.getSiteGroupId(),
		};

		Liferay.Util.fetch(themeDisplay.getPathMain() + '/portal/update_layout', {
			body: Liferay.Util.objectToFormData(updateLayoutData),
			method: 'POST',
		}).then(() => {
			var parentWindow = Liferay.Util.getOpener();

			parentWindow.location = parentWindow.location.href.split('?')[0];
		});
	}

	(function () {
		var layoutBranchesContainers = document.querySelectorAll(
			'.layout-variation-container'
		);
		var variationsSelector = document.getElementById(
			'<portlet:namespace />variationsSelector'
		);

		if (layoutBranchesContainers && variationsSelector) {
			variationsSelector.addEventListener('change', () => {
				var variation = variationsSelector.value;

				if (variation === 'all') {
					Array.prototype.forEach.call(
						layoutBranchesContainers,
						(layoutBranchesContainer) => {
							layoutBranchesContainer.classList.remove('hide');
						}
					);
				}
				else {
					Array.prototype.forEach.call(
						layoutBranchesContainers,
						(layoutBranchesContainer) => {
							layoutBranchesContainer.classList.add('hide');
						}
					);

					var variationElement = document.getElementById(
						'<portlet:namespace />' + variation
					);

					if (variationElement) {
						variationElement.classList.remove('hide');
					}
				}
			});
		}
	})();
</aui:script>