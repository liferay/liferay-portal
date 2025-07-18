<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherViewFixPacksDisplayContext patcherViewFixPacksDisplayContext = new PatcherViewFixPacksDisplayContext(request, renderRequest, renderResponse);

long patcherFixPackId = ParamUtil.getLong(request, "patcherFixPackId");

PatcherFixPack patcherFixPack = PatcherFixPackLocalServiceUtil.fetchPatcherFixPack(patcherFixPackId);

PatcherFixComponent patcherFixComponent = PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(patcherFixPack.getPatcherFixComponentId());

PatcherFix mainPatcherFix = null;

PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherFixPack.getPatcherBuildId());

if (patcherBuild != null) {
	mainPatcherFix = PatcherFixLocalServiceUtil.getPatcherFix(patcherBuild.getPatcherFixId());
}
%>

<liferay-ui:header
	title="view-fix-pack"
/>

<clay:container-fluid
	cssClass="container-form-lg container-no-gutters"
>
	<clay:sheet
		size="full"
	>
		<h2 class="sheet-title">
			<liferay-ui:message key="view-fix-pack" />
		</h2>

		<c:if test="<%= (mainPatcherFix != null) && Validator.isNotNull(mainPatcherFix.getGitHash()) %>">
			<div class="c-mb-3">
				<p class="c-mb-1 font-weight-semi-bold text-3">
					<liferay-ui:message key="git-hash" />
				</p>

				<p class="text-secondary">
					<a href="<%= PatcherFixUtil.getPatcherFixGitHubURL(mainPatcherFix.getPatcherFixId()) %>" target="_blank"><%= mainPatcherFix.getGitHash() %></a>
				</p>
			</div>
		</c:if>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="name" />
			</p>

			<p class="text-secondary">
				<%= patcherFixPack.getName() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="component" />
			</p>

			<p class="text-secondary">
				<%= patcherFixComponent.getName() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="version" />
			</p>

			<p class="text-secondary">
				<%= patcherFixPack.getVersion() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="status" />
			</p>

			<liferay-portal-workflow:status
				showStatusLabel="<%= false %>"
				status="<%= patcherFixPack.getStatus() %>"
				statusMessage="<%= WorkflowConstants.getStatusLabel(patcherFixPack.getStatus()) %>"
			/>
		</div>

		<c:if test="<%= patcherBuild != null %>">
			<div class="c-mb-3">
				<p class="c-mb-1 font-weight-semi-bold text-3">
					<liferay-ui:message key="build-status" />
				</p>

				<liferay-portal-workflow:status
					showStatusLabel="<%= false %>"
					status="<%= patcherBuild.getStatus() %>"
					statusMessage="<%= WorkflowConstants.getStatusLabel(patcherBuild.getStatus()) %>"
				/>
			</div>
		</c:if>

		<c:if test="<%= (patcherBuild != null) && Validator.isNotNull(patcherBuild.getQaStatus()) %>">
			<div class="c-mb-3">
				<p class="c-mb-1 font-weight-semi-bold text-3">
					<liferay-ui:message key="qa-status" />
				</p>

				<liferay-portal-workflow:status
					showStatusLabel="<%= false %>"
					status="<%= patcherBuild.getQaStatus() %>"
					statusMessage="<%= PatcherBuildUtil.getQAStatusLabel(patcherFixPack.getPatcherBuildId()) %>"
				/>
			</div>
		</c:if>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="released-date" />
			</p>

			<p class="text-secondary">
				<%= dateTimeFormat.format(patcherFixPack.getReleasedDate()) %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="requirements" />
			</p>

			<p class="text-secondary">
				<%= patcherFixPack.getRequirements() %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="new-issues" />
			</p>

			<p class="text-secondary">
				<%= StringUtil.merge(PatcherUtil.getNewTickets(patcherFixPack), StringPool.COMMA_AND_SPACE) %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="overridden-issues" />
			</p>

			<p class="text-secondary">
				<%= StringUtil.merge(PatcherUtil.getOverriddenTickets(patcherFixPack), StringPool.COMMA_AND_SPACE) %>
			</p>
		</div>

		<div class="c-mb-3">
			<p class="c-mb-1 font-weight-semi-bold text-3">
				<liferay-ui:message key="oldest-fix-date" />
			</p>

			<p class="text-secondary">
				<%= dateTimeFormat.format(PatcherFixUtil.getOldestPatcherFixCreateDate(patcherFixPack.getPatcherFixPackId())) %>
			</p>
		</div>

		<aui:button-row>
			<portlet:renderURL var="editPatcherFixPackURL">
				<portlet:param name="mvcRenderCommandName" value="/patcher/edit_fix_packs" />
				<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
			</portlet:renderURL>

			<aui:button href="<%= editPatcherFixPackURL %>" primary="<%= true %>" value="edit" />

			<c:if test="<%= patcherFixPack.getPatcherBuildId() > 0 %>">
				<portlet:renderURL var="viewPatcherBuildURL">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_builds" />
					<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherFixPack.getPatcherBuildId()) %>" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:renderURL>

				<aui:button href="<%= viewPatcherBuildURL %>" value="view-build" />
			</c:if>

			<c:if test="<%= (patcherBuild != null) && (patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_COMPLETE) %>">
				<portlet:actionURL name="/patcher/test_builds" var="testPatcherFixPackURL">
					<portlet:param name="patcherBuildId" value="<%= String.valueOf(patcherFixPack.getPatcherBuildId()) %>" />
					<portlet:param name="status" value="<%= String.valueOf(WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED) %>" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:actionURL>

				<aui:button href="<%= testPatcherFixPackURL %>" value="test" />
			</c:if>
		</aui:button-row>
	</clay:sheet>
</clay:container-fluid>

<clay:container-fluid
	cssClass="container-no-gutters"
>
	<clay:sheet
		size="full"
	>
		<h2 class="sheet-title">
			<liferay-ui:message key="fixes-in-fix-pack" />
		</h2>

		<%
		List<PatcherFix> patcherFixes = PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixes(patcherFixPack.getPatcherFixPackId());
		%>

		<liferay-ui:search-container
			emptyResultsMessage="there-are-no-fixes"
			total="<%= patcherFixes.size() %>"
		>
			<liferay-ui:search-container-results
				results="<%= patcherFixes %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.osb.patcher.model.PatcherFix"
				escapedModel="<%= true %>"
				keyProperty="patcherFixId"
				modelVar="patcherFix"
			>
				<portlet:renderURL var="viewPatcherFixURL">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_fixes" />
					<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFix.getPatcherFixId()) %>" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:renderURL>

				<liferay-ui:search-container-column-text
					href="<%= viewPatcherFixURL %>"
					name="fix-id"
					property="patcherFixId"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-cell-minw-200 table-title"
					name="name"
				>

					<%
					for (String token : StringUtil.split(patcherFix.getName())) {
					%>

						<a class="nobr" href="<%= patcherConfiguration.jiraURL() %>/<%= token %>" target="_blank"><%= token %></a>,

					<%
					}
					%>

				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					name="version"
					property="keyVersion"
				/>

				<liferay-ui:search-container-column-text
					href="<%= PatcherFixUtil.getPatcherFixGitHubURL(patcherFix.getPatcherFixId()) %>"
					name="git-hash"
					target="_blank"
					value="<%= com.liferay.portal.kernel.util.StringUtil.shorten(patcherFix.getGitHash(), 10) %>"
				/>

				<liferay-ui:search-container-column-text
					property="dependencies"
				/>

				<liferay-ui:search-container-column-text
					name="status"
					value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(patcherFix.getStatus())) %>"
				/>

				<liferay-ui:search-container-column-text
					name="fix-pack-status"
				>
					<c:if test="<%= patcherFix.getFixPackStatus() > 0 %>">
						<liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(patcherFix.getFixPackStatus()) %>" />
					</c:if>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					name="type"
					value="<%= LanguageUtil.get(request, PatcherFixConstants.getTypeLabel(patcherFix.getType())) %>"
				/>

				<liferay-ui:search-container-column-text
					align="right"
				>
					<clay:dropdown-actions
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= patcherViewFixPacksDisplayContext.getDropdownItems(patcherFix) %>"
						propsTransformer="{PatcherDropdownDefaultPropsTransformer} from osb-patcher-web"
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
				paginate="<%= false %>"
			/>
		</liferay-ui:search-container>
	</clay:sheet>
</clay:container-fluid>

<%
Set<PatcherFixPack> prerequisitePatcherFixPacks = PatcherFixPackUtil.getPrerequisitePatcherFixPacks(patcherFixPack.getPatcherFixPackId());
%>

<c:if test="<%= !prerequisitePatcherFixPacks.isEmpty() %>">
	<clay:container-fluid
		cssClass="container-no-gutters"
	>
		<clay:sheet
			size="full"
		>
			<h2 class="sheet-title">
				<liferay-ui:message key="dependencies" />
			</h2>

			<liferay-ui:search-container
				total="<%= prerequisitePatcherFixPacks.size() %>"
			>
				<liferay-ui:search-container-results
					results="<%= ListUtil.fromCollection(prerequisitePatcherFixPacks) %>"
				/>

				<liferay-ui:search-container-row
					className="com.liferay.osb.patcher.model.PatcherFixPack"
					escapedModel="<%= true %>"
					keyProperty="patcherFixPackId"
					modelVar="curPatcherFixPack"
				>
					<portlet:renderURL var="viewPatcherFixPackURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/view_fix_packs" />
						<portlet:param name="patcherFixPackId" value="<%= String.valueOf(curPatcherFixPack.getPatcherFixPackId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:renderURL>

					<liferay-ui:search-container-column-text
						href="<%= viewPatcherFixPackURL %>"
						name="name"
						value="<%= curPatcherFixPack.getName() %>"
					/>

					<liferay-ui:search-container-column-text
						name="component"
						value="<%= patcherFixComponent.getName() %>"
					/>

					<liferay-ui:search-container-column-text
						name="version"
						value="<%= String.valueOf(curPatcherFixPack.getVersion()) %>"
					/>

					<%
					PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(curPatcherFixPack.getPatcherProjectVersionId());
					%>

					<liferay-ui:search-container-column-text
						name="project-version"
						value="<%= patcherProjectVersion.getName() %>"
					/>

					<liferay-ui:search-container-column-text
						name="status"
						value="<%= LanguageUtil.get(request, WorkflowConstants.getStatusLabel(curPatcherFixPack.getStatus())) %>"
					/>

					<liferay-ui:search-container-column-date
						name="released-date"
						value="<%= curPatcherFixPack.getReleasedDate() %>"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					paginate="<%= false %>"
				/>
			</liferay-ui:search-container>
		</clay:sheet>
	</clay:container-fluid>
</c:if>