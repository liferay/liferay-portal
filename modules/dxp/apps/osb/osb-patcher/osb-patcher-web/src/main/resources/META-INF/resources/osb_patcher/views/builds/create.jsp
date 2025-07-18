<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherCreateBuildsDisplayContext patcherCreateBuildsDisplayContext = new PatcherCreateBuildsDisplayContext(request);

PatcherBuild patcherBuild = patcherCreateBuildsDisplayContext.getPatcherBuild();
%>

<liferay-ui:header
	title="create-build"
/>

<aui:model-context bean="<%= patcherBuild %>" model="<%= PatcherBuild.class %>" />

<portlet:actionURL name="/patcher/add_builds" var="addPatcherBuildURL" />

<liferay-frontend:edit-form
	action="<%= addPatcherBuildURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
	onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "checkForExistingHotfix();" %>'
>
	<liferay-frontend:edit-form-body>
		<clay:row>
			<clay:col>
				<aui:input name="redirect" type="hidden" value="<%= patcherCreateBuildsDisplayContext.getRedirect() %>" />
				<aui:input name="useExistingHotfix" type="hidden" value="<%= false %>" />

				<div class="c-mb-3">
					<p class="c-mb-1 font-weight-semi-bold text-3">
						<liferay-ui:message key="version" />
					</p>

					<p class="text-secondary">
						<%= patcherBuild.getKeyVersion() %>
					</p>
				</div>

				<aui:select label="product-version" name="patcherProductVersionId" onChange='<%= liferayPortletResponse.getNamespace() + "productVersionOnChange(this.value);" %>' required="<%= true %>" showEmptyOption="<%= true %>">

					<%
					for (PatcherProductVersion patcherProductVersion : PatcherProductVersionUtil.getPatcherProductVersions()) {
					%>

						<aui:option label="<%= patcherProductVersion.getName() %>" value="<%= patcherProductVersion.getPatcherProductVersionId() %>" />

					<%
					}
					%>

				</aui:select>

				<aui:select label="project-version" name="patcherProjectVersionId" onChange='<%= liferayPortletResponse.getNamespace() + "projectVersionOnChange(this.value);" %>' required="<%= true %>" />

				<aui:input label="account-code" name="accountEntryCode" required="<%= true %>" type="text" />

				<aui:input helpMessage="the-support-ticket-must-contain-only-the-help-center-ticket-id" name="supportTicket" type="text">
					<aui:validator name="number" />
				</aui:input>

				<aui:select name="type">
					<aui:option label="<%= PatcherBuildConstants.LABEL_OFFICIAL %>" value="<%= PatcherBuildConstants.TYPE_OFFICIAL %>" />
					<aui:option label="<%= PatcherBuildConstants.LABEL_DEBUG %>" value="<%= PatcherBuildConstants.TYPE_DEBUG %>" />
					<aui:option label="<%= PatcherBuildConstants.LABEL_IGNORE %>" value="<%= PatcherBuildConstants.TYPE_IGNORE %>" />
				</aui:select>

				<aui:input name="mergeOnly" type="checkbox" value="<%= patcherCreateBuildsDisplayContext.isMergeOnly() %>" />

				<aui:input name="smokeTestOnly" type="checkbox" wrapperCssClass="d-none" />
			</clay:col>

			<clay:col>
				<aui:input label="tickets-list" name="patcherBuildName" type="textarea" value="<%= patcherBuild.getName() %>" />
			</clay:col>

			<clay:col>
				<aui:input label="troubleshooting-ticket-suggestions" name="troubleshootingTicketList" type="textarea" wrapperCssClass="mb-1" />

				<clay:button
					displayType="secondary"
					icon="plus"
					onClick='<%= liferayPortletResponse.getNamespace() + "troubleshootAddOnClick(this.value);" %>'
					small="<%= true %>"
				/>
			</clay:col>
		</clay:row>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= patcherCreateBuildsDisplayContext.getRedirect() %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script>
	var mergeOnly = document.getElementById('<portlet:namespace />mergeOnly');
	var patcherBuildName = document.getElementById(
		'<portlet:namespace />patcherBuildName'
	);
	var patcherProductVersionId = document.getElementById(
		'<portlet:namespace />patcherProductVersionId'
	);
	var patcherProjectVersionId = document.getElementById(
		'<portlet:namespace />patcherProjectVersionId'
	);
	var select = document.getElementById(
		'<portlet:namespace />patcherProjectVersionId'
	);
	var troubleshootingTextArea = document.getElementById(
		'<portlet:namespace />troubleshootingTicketList'
	);
	var useExistingHotfix = document.getElementById(
		'<portlet:namespace />useExistingHotfix'
	);

	Liferay.provide(
		window,
		'<portlet:namespace />checkForExistingHotfix',
		function () {
			if (
				patcherProductVersionId.value ==
				<%= PatcherProductVersionUtil.getPatcherProductVersionId(PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_QUARTERLY_RELEASES) %>
			) {
				getUseExistingHotfixValue();
			}
			else {
				submitForm(document.<portlet:namespace />fm);
			}
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />productVersionOnChange',
		function (productVersionId) {
			Liferay.Patcher.populateProjectVersionField(
				productVersionId,
				select,
				<%= PatcherProjectVersionUtil.getPatcherProjectVersionsJSONObject() %>
			);

			getTicketSuggestionFields();
		},
		['aui-base', 'liferay-portlet-url']
	);

	function getTicketSuggestionFields() {
		var projectVersionId = patcherProjectVersionId.value
			? patcherProjectVersionId.value
			: 0;

		const data = Liferay.Util.ns('<portlet:namespace />', {
			tickets: patcherBuildName.value,
			productVersionId: patcherProductVersionId.value,
			projectVersionId: projectVersionId,
		});

		Liferay.Util.fetch(
			'<liferay-portlet:resourceURL id="/patcher/get_ticket_suggestion_fields" />',
			{
				body: Liferay.Util.objectToFormData(data),
				method: 'POST',
			}
		)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				troubleshootingTextArea.value = String(
					data.troubleshooting
				).replaceAll(' ', '');
			});
	}

	function getUseExistingHotfixValue() {
		const data = Liferay.Util.ns('<portlet:namespace />', {
			projectVersionId: patcherProjectVersionId.value,
			tickets: patcherBuildName.value,
		});

		Liferay.Util.fetch(
			'<liferay-portlet:resourceURL id="/patcher/exists_hotfix" />',
			{
				body: Liferay.Util.objectToFormData(data),
				method: 'POST',
			}
		)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				if (data.hotfixExists == true) {
					var alertMessage =
						'<liferay-ui:message key="a-hotfix-with-these-parameters-is-already-available-would-you-like-to-use-it-click-ok-to-use-the-existing-hotfix-or-cancel-to-start-the-normal-build-process" />';

					if (confirm(alertMessage)) {
						mergeOnly.value = true;

						useExistingHotfix.value = true;
					}
				}

				submitForm(document.<portlet:namespace />fm);
			});
	}

	Liferay.provide(
		window,
		'<portlet:namespace />projectVersionOnChange',
		function (projectVersionId) {
			getTicketSuggestionFields();
		},
		['aui-base', 'liferay-portlet-url']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />troubleshootAddOnClick',
		function () {
			if (patcherBuildName.value) {
				patcherBuildName.value =
					patcherBuildName.value + ',' + troubleshootingTextArea.value;
			}
			else {
				patcherBuildName.value = troubleshootingTextArea.value;
			}
		},
		['aui-base']
	);

	AUI().ready(function () {
		var A = AUI();

		var productVersionId = A.one(
			'#<portlet:namespace />patcherProductVersionId'
		).val();

		Liferay.Patcher.populateProjectVersionField(
			productVersionId,
			select,
			<%= PatcherProjectVersionUtil.getPatcherProjectVersionsJSONObject() %>
		);

		var projectVersionId =
			<%= patcherCreateBuildsDisplayContext.getPatcherProjectVersionId() %>;

		if (projectVersionId > 0) {
			A.one('#<portlet:namespace />patcherProjectVersionId').val(
				projectVersionId
			);
		}

		YUI().use('event-valuechange', function (Y) {
			Y.one('#<portlet:namespace />patcherBuildName').on(
				'valuechange',
				function (e) {
					if (patcherProductVersionId && patcherProjectVersionId) {
						getTicketSuggestionFields();
					}
				}
			);
		});
	});

	YUI().ready('aui-popover', function (Y) {
		var align_points = [Y.WidgetPositionAlign.BL, Y.WidgetPositionAlign.BR];
		var tickets = document.getElementById(
			'<portlet:namespace />patcherBuildName'
		);
		var trigger = Y.one('#<portlet:namespace />patcherBuildName');

		Liferay.Patcher.getTicketLinksPopover(Y, align_points, tickets, trigger);
	});

	YUI().ready('aui-popover', function (Y) {
		var align_points = [Y.WidgetPositionAlign.LC, Y.WidgetPositionAlign.RC];
		var tickets = document.getElementById(
			'<portlet:namespace />troubleshootingTicketList'
		);
		var trigger = Y.one('#<portlet:namespace />troubleshootingTicketList');

		Liferay.Patcher.getTicketLinksPopover(Y, align_points, tickets, trigger);
	});
</aui:script>